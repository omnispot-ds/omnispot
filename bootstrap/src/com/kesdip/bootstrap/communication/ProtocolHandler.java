package com.kesdip.bootstrap.communication;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

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
import com.kesdip.bootstrap.message.DeployMessage;
import com.kesdip.bootstrap.message.RebootPlayerMessage;
import com.kesdip.bootstrap.message.RestartPlayerMessage;
import com.kesdip.business.communication.ActionSerializationHandler;
import com.kesdip.business.constenum.IActionParamsEnum;
import com.kesdip.business.constenum.IActionStatusEnum;
import com.kesdip.business.constenum.IActionTypesEnum;
import com.kesdip.business.domain.generated.Action;
import com.kesdip.business.domain.generated.Parameter;


public class ProtocolHandler {

	private static final Logger logger =
		Logger.getLogger(ProtocolHandler.class);
	private Manager manager;
	private ActionSerializationHandler actionHandler = new ActionSerializationHandler();

	@Transactional(isolation = Isolation.READ_COMMITTED)
	@SuppressWarnings("unchecked")
	public void performRequest() throws Exception {

		logger.info("Performing request...");
		HttpClient http = new HttpClient();
		PostMethod post = new PostMethod(serverURL);

		boolean playerAlive = true;

		List<Action> actions = getHibernateTemplate().loadAll(Action.class);
		String serializedActions = "NO_ACTIONS";
		
		if (actions.size() > 0) {
			serializedActions = actionHandler.serialize(actions.toArray(new Action[0]));
			logger.info("Actions found and will be sent to server: " + serializedActions);
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

		//delete all actions with status done...
		for (Action action:actions) {
			if (action.getStatus() == IActionStatusEnum.OK);
			getHibernateTemplate().delete(action);
			logger.info("deleted action with status OK: "+action.toString());
		}
		
		handleResponse(post.getResponseBodyAsString());

	}

	public void handleResponse(String serializedActions) throws Exception {

		logger.info("Response received from server...");
		if (serializedActions.length() == 0)
			return;

		if (!serializedActions.equals("NO_ACTIONS")){
			logger.info("actions received from server: " + serializedActions);			
			Action[] actions = actionHandler.deserialize(serializedActions);
			//must store them and add the necessary messages if required
			for (Action action:actions) {
				if (action.getType() == IActionTypesEnum.DEPLOY) {
					Set<Parameter> params = action.getParameters();
					String descriptorUrl = "";
					String crc = "";
					for (Parameter p : params) {
						if (p.getName().equals(IActionParamsEnum.DEPLOYMENT_URL)) {
							descriptorUrl = p.getValue();
						}
						if (p.getName().equals(IActionParamsEnum.DEPLOYMENT_CRC)) {
							crc = p.getValue();
						}
						p.setId((Long)getHibernateTemplate().save(p));
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
				action.setInstallation(null);
				action.setStatus(IActionStatusEnum.SENT);
				getHibernateTemplate().save(action);
			}
		}
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
