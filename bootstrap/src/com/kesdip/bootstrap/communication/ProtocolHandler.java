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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.bootstrap.Config;
import com.kesdip.bootstrap.Manager;
import com.kesdip.bootstrap.domain.generated.Action;
import com.kesdip.bootstrap.domain.generated.Parameter;
import com.kesdip.bootstrap.message.DeployMessage;
import com.kesdip.bootstrap.message.RestartPlayerMessage;
import com.kesdip.business.constenum.IActionStatusEnum;
import com.kesdip.business.constenum.IActionTypesEnum;


public class ProtocolHandler {

	private static final Logger logger =
		Logger.getLogger(ProtocolHandler.class);
	private Manager manager;
	
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public void performRequest() throws Exception {

		logger.info("Performing request...");
		HttpClient http = new HttpClient();
		PostMethod post = new PostMethod(serverURL);

		boolean playerAlive = RestartPlayerMessage.isPlayerProcessAlive();

		List<Action> actions = getHibernateTemplate().loadAll(Action.class);
		String serializedActions = "NO_ACTIONS";
		if (actions.size() > 0) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ObjectOutputStream outStream = new ObjectOutputStream(out);
			outStream.writeObject(actions);
			byte[] bytes = Base64.encodeBase64(out.toByteArray());
			serializedActions = new String(bytes);
		}
		logger.info("Actions found: "+serializedActions);
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

		handleResponse(post.getResponseBodyAsString());
		//delete all actions with status done...
		for (Action action:actions) {
			if (action.getStatus() == IActionStatusEnum.OK);
			getHibernateTemplate().delete(action);
		}
		

	}

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void handleResponse(String serializedActions) throws Exception {
    	
    	logger.info("Response received from server...actions received: "+serializedActions);
    	if (serializedActions.length() == 0)
    		return;
		ObjectInputStream instream = new ObjectInputStream(new ByteArrayInputStream(serializedActions.getBytes()));
		Action[] actions = (Action[])instream.readObject();
		//must store them and add the necessary messages if required
		for (Action action:actions) {
			if (action.getType() == IActionTypesEnum.DEPLOY) {
				Set<Parameter> params = action.getParameters();
				String descriptorUrl = "";
				String crc = "";
				for (Iterator<Parameter> i = params.iterator();i.hasNext();) {
					Parameter p = i.next();
					if (p.getName().equals("descriptorUrl")) {
						descriptorUrl = p.getValue();
					}
					if (p.getName().equals("crc")) {
						crc = p.getValue();
					}
				}
				logger.info("Adding new deploy message");
				manager.getPump().addMessage(new DeployMessage(descriptorUrl,Long.parseLong(crc), action.getActionId()));
			} else if (action.getType() == IActionTypesEnum.REBOOT) {
				logger.info("Adding new restartplayer message");
				manager.getPump().addMessage(new RestartPlayerMessage(action.getActionId()));
			}

			getHibernateTemplate().save(action);
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
