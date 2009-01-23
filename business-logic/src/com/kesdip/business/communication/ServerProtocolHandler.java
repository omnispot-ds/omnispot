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

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.config.ApplicationSettings;
import com.kesdip.business.constenum.IActionStatusEnum;
import com.kesdip.business.constenum.IInstallationStatus;
import com.kesdip.business.domain.generated.Action;
import com.kesdip.business.domain.generated.Installation;

public class ServerProtocolHandler {
	
	private final static Logger logger = Logger.getLogger(ServerProtocolHandler.class);
	
	String installationId;
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public void handleRequest(HttpServletRequest req, HttpServletResponse resp) 
	throws Exception {

		if (isMultipart(req))
			parseMultipart(req);

		installationId = req.getParameter("installationId" );
		String serializedActions = req.getParameter("serializedActions");
		String playerProcAlive = req.getParameter("playerProcAlive");
		logger.info("Received: InstallationId: "+installationId+" playerProcAlive: " +playerProcAlive + " serializedActions: "+serializedActions);
		if (req.getAttribute("screenshot") != null) {
			FileItem fileitem = (FileItem)req.getAttribute("screenshot");
			fileitem.write(new File(ApplicationSettings
					.getInstance().getFileStorageSettings().getPrintScreenFolder()+File.pathSeparator+installationId,"screenshot.jpg"));
		}
		if (!serializedActions.equals("NO_ACTIONS")) {
		ObjectInputStream instream = new ObjectInputStream(new ByteArrayInputStream(serializedActions.getBytes()));
		Action[] actions = (Action[])instream.readObject();
		
		//now update the admin-console db
		for (Action action:actions) {
			List<Action> l = getHibernateTemplate().find(
					"from " + Action.class.getName() + " a where a.actionId = ? ",
					new Object[] { action.getActionId()});
			if (l.size() > 1)
				throw new AssertionError("Duplicate actionIds found!?!?");
			action.setId(l.get(0).getId());
			//TODO Maybe delete actions with status OK??
			getHibernateTemplate().update(action);
		}
		Installation installation = new Installation();
		installation.setUuid(installationId);
		installation = (Installation)getHibernateTemplate().load(Installation.class, installation);
		installation.setCurrentStatus(playerProcAlive.equals("TRUE")?IInstallationStatus.OK:IInstallationStatus.PLAYER_DOWN);
		getHibernateTemplate().update(installation);
		}
		sendResponse(resp);
	
	}
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	private void sendResponse(HttpServletResponse resp) throws Exception {

		//send any pending actions
		Installation installation = new Installation();
		installation.setId(Long.valueOf(installationId));
		List<Action> actions = getHibernateTemplate().find(
				"from " + Action.class.getName() + " a where a.status= ? and a.installation = ?",
				new Object[] { IActionStatusEnum.SCHEDULED , installation } );


		String serializedActions = "NO_ACTIONS";
		if (actions.size() > 0) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream outStream = new ObjectOutputStream(out);
			outStream.writeObject(actions);
			serializedActions = out.toString();
		}
		resp.getOutputStream().print(serializedActions);
		resp.getOutputStream().close();
	
	}
	
	@SuppressWarnings("unchecked")
	private void parseMultipart(HttpServletRequest req) throws FileUploadException, UnsupportedEncodingException{
		ServletFileUpload upload = new ServletFileUpload();
		DiskFileItemFactory factory = new DiskFileItemFactory(Integer.MAX_VALUE , new File(ApplicationSettings
				.getInstance().getFileStorageSettings().getPrintScreenFolder()));
		upload.setFileItemFactory(factory);
		upload.setHeaderEncoding("UTF-8");

		logger.debug("Parsing request with Commons FileUpload.");
		List fileItems = upload.parseRequest(req);
		Map<String, ArrayList<String>> formParameters = new HashMap<String, ArrayList<String>>();
		Map<String, FileItem> fileParameters = new HashMap<String, FileItem>();	

		logger.debug("Populating Maps.");
		for (int i = 0; i < fileItems.size(); i++) {
			FileItem item = (FileItem)fileItems.get(i);				

			if (item.isFormField() == true) {																		
				ArrayList<String> values = formParameters.get(item.getFieldName());
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
		req.getParameterMap().putAll(formParameters);
	}

	private boolean isMultipart(HttpServletRequest req) {
		return (req.getHeader("content-type") != null && 
				req.getHeader("content-type").indexOf("multipart/form-data") != -1);
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
