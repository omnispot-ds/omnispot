package com.kesdip.bootstrap.communication;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.bootstrap.Config;
import com.kesdip.bootstrap.Manager;
import com.kesdip.bootstrap.domain.generated.Action;
import com.kesdip.bootstrap.domain.generated.Parameter;
import com.kesdip.bootstrap.message.DeployMessage;
import com.kesdip.bootstrap.message.RebootPlayerMessage;
import com.kesdip.bootstrap.message.RestartPlayerMessage;
import com.kesdip.business.constenum.IActionParamsEnum;
import com.kesdip.business.constenum.IActionStatusEnum;
import com.kesdip.business.constenum.IActionTypesEnum;


public class ProtocolHandler {

	private static final Logger logger =
		Logger.getLogger(ProtocolHandler.class);
	private Manager manager;

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@SuppressWarnings("unchecked")
	public void performRequest() throws Exception {

		logger.info("Performing request...");
		HttpClient http = new HttpClient();
		PostMethod post = new PostMethod(serverURL);

		boolean playerAlive = RestartPlayerMessage.isPlayerProcessAlive();

		List<Action> actions = getHibernateTemplate().loadAll(Action.class);
		String serializedActions = new String(Base64.encodeBase64("NO_ACTIONS".getBytes()));
		if (actions.size() > 0) {
			List<com.kesdip.business.domain.generated.Action> serverActions = 
				new ArrayList<com.kesdip.business.domain.generated.Action>();
			for (Action action:actions) {
				serverActions.add(populateFrom(action));
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream outStream = new ObjectOutputStream(out);
			outStream.writeObject(serverActions);
			byte[] bytes = Base64.encodeBase64(out.toByteArray());
			serializedActions = new String(bytes);
			logger.info("Actions found");
		}

		String installationId = Config.getSingleton().getinstallationId();
		logger.info("installationId: "+installationId);
		if (manager.includeScreendump()) {
			//get screenshot
			logger.info("Including Screendump");
			Robot robot = new Robot();
			BufferedImage screenShot = 
				robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			ImageIO.write(screenShot, "JPG", new File(Config.getSingleton().getScreenShotStorageLocation() , "screenShot.jpg"));
			Part[] parts = {
					new StringPart("installationId", installationId),
					new StringPart("playerProcAlive", Boolean.toString(playerAlive)),
					new StringPart("serializedActions" , serializedActions),
					new FilePart("screenshot",
							new File(Config.getSingleton().getScreenShotStorageLocation() ,
							"screenShot.jpg"))
			};
			post.setRequestEntity(new MultipartRequestEntity(parts ,post.getParams()));
		} else {
			NameValuePair[] data = {
					new NameValuePair("installationId", installationId),
					new NameValuePair("playerProcAlive", Boolean.toString(playerAlive)),
					new NameValuePair("serializedActions" , serializedActions)
			};
			post.setRequestBody(data);
		}


		http.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		http.executeMethod(post);

//		delete all actions with status done...
		for (Action action:actions) {
			if (action.getStatus() == IActionStatusEnum.OK);
			getHibernateTemplate().delete(action);
			logger.info("deleted action "+action.toString());
		}
		
		handleResponse(post.getResponseBodyAsString());
		


	}

	@SuppressWarnings("unchecked")
	public void handleResponse(String serializedActions) throws Exception {

		logger.info("Response received from server...");
		if (serializedActions.length() == 0)
			return;

		byte[] bytes = Base64.decodeBase64(serializedActions.getBytes()); 
		String response = new String(bytes);
		if (!response.equals("NO_ACTIONS")){
			logger.info("actions received");
			ObjectInputStream instream = new ObjectInputStream(new ByteArrayInputStream(bytes));
			List<com.kesdip.business.domain.generated.Action> actions =
				(List<com.kesdip.business.domain.generated.Action>)instream.readObject();
			//must store them and add the necessary messages if required
			for (com.kesdip.business.domain.generated.Action action:actions) {
				Action bootstrapAction = populateFrom(action);
				if (action.getType() == IActionTypesEnum.DEPLOY) {
					Set<Parameter> params = bootstrapAction.getParameters();
					String descriptorUrl = "";
					String crc = "";
					for (Iterator<Parameter> i = params.iterator();i.hasNext();) {
						Parameter p = i.next();
						if (p.getName().equals(IActionParamsEnum.DEPLOYMENT_URL)) {
							descriptorUrl = p.getValue();
						}
						if (p.getName().equals(IActionParamsEnum.DEPLOYMENT_CRC)) {
							crc = p.getValue();
						}
					}
					logger.info("Adding new deploy message");
					manager.getPump().addMessage(new DeployMessage(descriptorUrl,Long.parseLong(crc), action.getActionId()));
				} else if (action.getType() == IActionTypesEnum.RESTART) {
					logger.info("Adding new restartplayer message");
					manager.getPump().addMessage(new RestartPlayerMessage(action.getActionId()));
				} else if (action.getType() == IActionTypesEnum.REBOOT) {
					logger.info("Adding new rebootplayer message");
					manager.getPump().addMessage(new RebootPlayerMessage(action.getActionId()));
				}

				getHibernateTemplate().save(bootstrapAction);
			}
		}
	}

	private Action populateFrom(com.kesdip.business.domain.generated.Action action) {
		Action bootstrapAction = new Action();
		logger.info("action:"+action.toString());
		bootstrapAction.setActionId(action.getActionId());
		bootstrapAction.setDateAdded(action.getDateAdded());
		bootstrapAction.setMessage(action.getMessage());
		bootstrapAction.setStatus(action.getStatus());
		bootstrapAction.setType(action.getType());
		logger.info("bootstrapaction:"+bootstrapAction.toString());
		for (com.kesdip.business.domain.generated.Parameter parameter:action.getParameters()) {
			Parameter bootstrapParameter = new Parameter();
			bootstrapParameter.setName(parameter.getName());
			bootstrapParameter.setValue(parameter.getValue());
			bootstrapAction.getParameters().add(bootstrapParameter);
		}
		return bootstrapAction;
	}
	
	private com.kesdip.business.domain.generated.Action populateFrom(Action action) {
	com.kesdip.business.domain.generated.Action serverAction = new com.kesdip.business.domain.generated.Action();
	logger.info("action:"+action.toString());
	serverAction.setActionId(action.getActionId());
	serverAction.setDateAdded(action.getDateAdded());
	serverAction.setMessage(action.getMessage());
	serverAction.setStatus(action.getStatus());
	serverAction.setType(action.getType());
	logger.info("serveraction:"+serverAction.toString());
	for (Parameter parameter:action.getParameters()) {
		com.kesdip.business.domain.generated.Parameter serverParameter = 
			new com.kesdip.business.domain.generated.Parameter();
		serverParameter.setName(parameter.getName());
		serverParameter.setValue(parameter.getValue());
		serverAction.getParameters().add(serverParameter);
	}
	return serverAction;
}

	public String serverURL = Config.getSingleton().getServerURL();
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

	public Manager getManager() {
		return manager;
	}

	public void setManager(Manager manager) {
		this.manager = manager;
	}

}
