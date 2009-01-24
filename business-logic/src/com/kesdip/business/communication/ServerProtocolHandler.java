package com.kesdip.business.communication;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;
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

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp)
			throws Exception {

		Map<String, String[]> parameters = isMultipart(req) ? parseMultipart(req)
				: req.getParameterMap();

		installationId = getParameter("installationId", parameters);
		String serializedActions = getParameter("serializedActions", parameters);
		String playerProcAlive = getParameter("playerProcAlive", parameters);
		logger.info("Received: InstallationId: " + installationId
				+ " playerProcAlive: " + playerProcAlive
				+ " serializedActions: " + serializedActions);
		if (req.getAttribute("screenshot") != null) {
			FileStorageSettings settings = ApplicationSettings.getInstance()
					.getFileStorageSettings();
			FileItem fileitem = (FileItem) req.getAttribute("screenshot");
			File destFile = new File(settings.getPrintScreenFolder()
					+ File.separator + installationId, settings
					.getPrintScreenName());
			destFile.mkdirs();
			fileitem.write(destFile);
		}
		byte[] bytes = Base64.decodeBase64(serializedActions.getBytes());
		serializedActions = new String(bytes);
		if (!serializedActions.equals("NO_ACTIONS")) {
			ObjectInputStream instream = new ObjectInputStream(
					new ByteArrayInputStream(serializedActions.getBytes()));
			Action[] actions = (Action[]) instream.readObject();

			// now update the admin-console db
			for (Action action : actions) {
				List<Action> l = getHibernateTemplate().find(
						"from " + Action.class.getName()
								+ " a where a.actionId = ? ",
						new Object[] { action.getActionId() });
				if (l.size() > 1)
					throw new AssertionError("Duplicate actionIds found!?!?");
				action.setId(l.get(0).getId());
				// TODO Maybe delete actions with status OK??
				getHibernateTemplate().update(action);
			}
			
			List<Installation> installations=  getHibernateTemplate().find(
					"from " +Installation.class.getName()+" i where i.uuid = ?",
					new Object[] {installationId});
			if (installations.size() != 0) {
				Installation installation = installations.get(0);
				installation
				.setCurrentStatus(playerProcAlive.equals("TRUE") ? IInstallationStatus.OK
						: IInstallationStatus.PLAYER_DOWN);
				getHibernateTemplate().update(installation);
			}
		}
		sendResponse(resp);

	}

	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	private void sendResponse(HttpServletResponse resp) throws Exception {

		// send any pending actions
				
		List<Action> actions = getHibernateTemplate().find(
				"select a from " + Action.class.getName()+" a "
						+ "inner join a.installation i where a.status= ? and i.uuid = ?",
				new Object[] { IActionStatusEnum.SCHEDULED, installationId });

		String serializedActions = "NO_ACTIONS";
		if (actions.size() > 0) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream outStream = new ObjectOutputStream(out);
			outStream.writeObject(actions);
			byte[] bytes = Base64.encodeBase64(out.toByteArray());
			serializedActions = new String(bytes);
		}
		resp.getOutputStream().print(serializedActions);
		resp.getOutputStream().close();
		
		for (Action action:actions) {
			action.setStatus(IActionStatusEnum.SEND);
			getHibernateTemplate().update(action);
		}

	}

	@SuppressWarnings("unchecked")
	private Map<String, List<String>> parseMultipart(HttpServletRequest req)
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
		return formParameters;
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
