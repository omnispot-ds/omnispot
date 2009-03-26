package com.kesdip.business.communication;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.config.ApplicationSettings;
import com.kesdip.business.config.FileStorageSettings;
import com.kesdip.business.constenum.IActionStatusEnum;
import com.kesdip.business.constenum.IInstallationStatus;
import com.kesdip.business.domain.generated.Action;
import com.kesdip.business.domain.generated.Installation;

public class ServerProtocolHandler {

	private final static Logger logger = Logger
			.getLogger(ServerProtocolHandler.class);

	String installationId;
	
	private ActionSerializationHandler actionHandler = new ActionSerializationHandler();

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@SuppressWarnings("unchecked")
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {


		Map<String, String[]> parameters = isMultipart(req) ? parseMultipart(req)
				: req.getParameterMap();

		installationId = getParameter("installationId", parameters);
		String serializedActions = getParameter("serializedActions", parameters);
		String playerProcAlive = getParameter("playerProcAlive", parameters);
		
		//update installation status
		List<Installation> installations = getHibernateTemplate().find(
				"from " + Installation.class.getName()
						+ " i where i.uuid = ?",
				new Object[] { installationId });
		if (installations.size() != 0) {
			Installation installation = installations.get(0);
			installation
					.setCurrentStatus(playerProcAlive.equalsIgnoreCase("TRUE") ? IInstallationStatus.OK
							: IInstallationStatus.PLAYER_DOWN);
			getHibernateTemplate().update(installation);
		} else {
			logger.error("Player with installation id: "+installationId+" doesnot exist!!??");
			return;
		}
		
		if (req.getAttribute("screenshot") != null) {
			FileStorageSettings settings = ApplicationSettings.getInstance()
					.getFileStorageSettings();
			FileItem fileitem = (FileItem) req.getAttribute("screenshot");
			File destFile = new File(settings.getPrintScreenFolder()
					+ File.separator + installationId, settings
					.getPrintScreenName());
			destFile.getParentFile().mkdirs();
			fileitem.write(destFile);
			// Bug#36: Explicitly set lastModified date
			destFile.setLastModified(System.currentTimeMillis());
		}
		logger.debug("Received: InstallationId: " + installationId
				+ " playerProcAlive: " + playerProcAlive
				+ " serializedActions: " + serializedActions);
		if (!serializedActions.equals("NO_ACTIONS")) {
			Action[] actions = actionHandler.deserialize(serializedActions);
			// now update the admin-console db
			for (Action action : actions) {
				List<Action> l = getHibernateTemplate().find(
						"from " + Action.class.getName()
								+ " a where a.actionId = ? ",
						new Object[] { action.getActionId() });
				if (l.size() > 1)
					throw new AssertionError("Duplicate actionIds found!?!?");
				Action dbAction = l.get(0);
				dbAction.setStatus(action.getStatus());
				getHibernateTemplate().update(dbAction);
			}

			logger.debug("Received actions updated! ");
		}
		sendResponse(resp);

	}

	@SuppressWarnings("unchecked")
	private void sendResponse(HttpServletResponse resp) throws Exception {

		// send any pending actions

		List<Action> actions = getHibernateTemplate()
				.find(
						"select distinct a from "
								+ Action.class.getName()
								+ " a left join fetch a.parameters p "
								+ "inner join a.installation i where a.status= ? and i.uuid = ?",
						new Object[] { IActionStatusEnum.SCHEDULED,
								installationId });

		String serializedActions = "NO_ACTIONS";
		if (actions.size() > 0) {
			serializedActions = actionHandler.serialize(actions.toArray(new Action[0]));
			logger.info("Actions found and will be sent to installation with id "
					+actions.get(0).getInstallation().getName()+". Actions: "+serializedActions);
		}
		resp.getOutputStream().print(serializedActions);
		resp.getOutputStream().close();

		for (Action action : actions) {
			logger.info("Actions sent and updated in server db");
			action.setStatus(IActionStatusEnum.SENT);
			getHibernateTemplate().update(action);
		}

	}

	@SuppressWarnings("unchecked")
	private Map<String, String[]> parseMultipart(HttpServletRequest req)
			throws FileUploadException, UnsupportedEncodingException {
		ServletFileUpload upload = new ServletFileUpload();
		// TODO Decide on maximum file size
		DiskFileItemFactory factory = new DiskFileItemFactory(
				Integer.MAX_VALUE, new File(ApplicationSettings.getInstance()
						.getFileStorageSettings().getTempFolder()));
		upload.setFileItemFactory(factory);
		upload.setHeaderEncoding("UTF-8");

		logger.debug("Parsing request with Commons FileUpload.");
		List fileItems = upload.parseRequest(req);
		Map<String, List<String>> formParameters = new HashMap<String, List<String>>();
		Map<String, FileItem> fileParameters = new HashMap<String, FileItem>();

		logger.debug("Populating Maps.");
		for (int i = 0; i < fileItems.size(); i++) {
			FileItem item = (FileItem) fileItems.get(i);

			if (item.isFormField() == true) {
				List<String> values = formParameters.get(item.getFieldName());
				if (values != null) {
					values.add(item.getString("UTF-8"));
				} else {
					values = new ArrayList<String>();
					values.add(item.getString("UTF-8"));
					formParameters.put(item.getFieldName(), values);
				}
			} else {
				fileParameters.put(item.getFieldName(), item);
				req.setAttribute(item.getFieldName(), item);
			}
		}
		// fix List<String> to String[]
		Map<String, String[]> fixedFormParameters = new HashMap<String, String[]>();
		for (String key : formParameters.keySet()) {
			fixedFormParameters.put(key, formParameters.get(key).toArray(
					new String[] {}));
		}
		return fixedFormParameters;
	}

	/**
	 * @param req
	 *            the request
	 * @return boolean <code>true</code> if the request is a multipart
	 */
	private boolean isMultipart(HttpServletRequest req) {
		return (req.getHeader("content-type") != null && req.getHeader(
				"content-type").indexOf("multipart/form-data") != -1);
	}

	/**
	 * @param parameterName
	 *            the param name
	 * @param parameters
	 *            the parameter map
	 * @return String the value of the first parameter in the list or
	 *         <code>null</code>
	 */
	private final String getParameter(String parameterName,
			Map<String, String[]> parameters) {
		return parameters.containsKey(parameterName) ? parameters
				.get(parameterName)[0] : null;
	}

	/**
	 * Utility Hibernate template.
	 */
	private HibernateTemplate hibernateTemplate = null;

	/**
	 * @return the hibernateTemplate
	 */
	protected HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	/**
	 * @param hibernateTemplate
	 *            the hibernateTemplate to set
	 */
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
}
