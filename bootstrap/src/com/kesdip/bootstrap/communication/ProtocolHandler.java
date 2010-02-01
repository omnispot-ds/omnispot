package com.kesdip.bootstrap.communication;

import java.io.File;
import java.util.List;
import java.util.Set;

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
import com.kesdip.bootstrap.Screen;
import com.kesdip.bootstrap.message.DeployMessage;
import com.kesdip.bootstrap.message.FetchLogsMessage;
import com.kesdip.bootstrap.message.Message;
import com.kesdip.bootstrap.message.RebootPlayerMessage;
import com.kesdip.bootstrap.message.ReconfigureMessage;
import com.kesdip.bootstrap.message.RestartPlayerMessage;
import com.kesdip.business.communication.ActionSerializationHandler;
import com.kesdip.business.constenum.IActionParamsEnum;
import com.kesdip.business.constenum.IActionStatusEnum;
import com.kesdip.business.constenum.IActionTypesEnum;
import com.kesdip.business.constenum.IMessageParamsEnum;
import com.kesdip.business.domain.admin.generated.Action;
import com.kesdip.business.domain.admin.generated.Parameter;

/**
 * Handles client-to-server communication.
 * <p>
 * Performs a request to the server ({@link #performRequest()}), including the
 * screen-dump, if necessary, and any pending outgoing messages. Retrieves and
 * parses the server's response, scheduling any incoming messages.
 * </p>
 * 
 * @author gerogias
 */
public class ProtocolHandler {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ProtocolHandler.class);

	/**
	 * The parent bootstrap manager.
	 */
	private Manager manager;

	/**
	 * The protocol serializer class.
	 */
	private ActionSerializationHandler actionHandler = null;

	/**
	 * The server's URL to perform requests to.
	 */
	private String serverURL = null;

	/**
	 * Utility Hibernate template.
	 */
	private HibernateTemplate hibernateTemplate = null;

	/**
	 * Default constructor.
	 */
	public ProtocolHandler() {
		actionHandler = new ActionSerializationHandler();
		serverURL = Config.getSingleton().getServerURL();
	}

	/**
	 * Perform request to server.
	 * 
	 * @throws Exception
	 *             on error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	@SuppressWarnings("unchecked")
	public void performRequest() throws Exception {

		logger.info("Performing request...");
		HttpClient http = new HttpClient();
		PostMethod post = new PostMethod(serverURL);

		boolean playerAlive = RestartPlayerMessage.isPlayerProcessAlive();

		List<Action> actions = getHibernateTemplate().loadAll(Action.class);
		String serializedActions = IActionParamsEnum.NO_ACTIONS;

		if (actions.size() > 0) {
			serializedActions = actionHandler.serialize(actions
					.toArray(new Action[0]));
			if (logger.isDebugEnabled()) {
				logger.debug(actions.size()
						+ " actions found and will be sent to server: "
						+ serializedActions);
			}
		}

		String installationId = Config.getSingleton().getinstallationId();
		if (logger.isDebugEnabled()) {
			logger.debug("installationId: " + installationId);
		}
		if (manager.includeScreendump()) {
			logger.debug("Including Screendump");
			Screen.grabAndSaveToFile();
			Part[] parts = {
					new StringPart(IMessageParamsEnum.INSTALLATION_ID,
							installationId),
					new StringPart(IMessageParamsEnum.PLAYER_PROC_ALIVE,
							Boolean.toString(playerAlive)),
					new StringPart(IMessageParamsEnum.SERIALIZED_ACTIONS,
							serializedActions),
					new FilePart(IMessageParamsEnum.SCREENSHOT, new File(Config
							.getSingleton().getScreenShotStorageLocation(),
							"screenShot.jpg")) };
			post.setRequestEntity(new MultipartRequestEntity(parts, post
					.getParams()));
		} else {
			NameValuePair[] data = {
					new NameValuePair(IMessageParamsEnum.INSTALLATION_ID,
							installationId),
					new NameValuePair(IMessageParamsEnum.PLAYER_PROC_ALIVE,
							Boolean.toString(playerAlive)),
					new NameValuePair(IMessageParamsEnum.SERIALIZED_ACTIONS,
							serializedActions) };
			post.setRequestBody(data);
		}

		http.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
		http.executeMethod(post);

		// delete all actions with status done...
		for (Action action : actions) {
			if (action.getStatus() == IActionStatusEnum.OK) {
				for (Parameter param : action.getParameters()) {
					getHibernateTemplate().delete(param);
				}
				getHibernateTemplate().delete(action);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Deleted action with status OK: "
						+ action.toString());
			}
		}
		handleResponse(post.getResponseBodyAsString());
	}

	/**
	 * Parse response from server.
	 * 
	 * @param serializedActions
	 *            the server's serialized action string
	 * @throws Exception
	 *             on error
	 */
	public void handleResponse(String serializedActions) throws Exception {

		logger.info("Response received from server...");
		if (serializedActions.length() == 0) {
			return;
		}

		if (!serializedActions.equals(IActionParamsEnum.NO_ACTIONS)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Actions received from server: " + serializedActions);
			}
			Action[] actions = actionHandler.deserialize(serializedActions);
			// must store them and add the necessary messages if required
			for (Action action : actions) {
				// first save the actions
				action.setInstallation(null);
				action.setStatus(IActionStatusEnum.SCHEDULED);
				for (Parameter param : action.getParameters()) {
					param.setId((Long) getHibernateTemplate().save(param));
				}
				getHibernateTemplate().save(action);
				getHibernateTemplate().flush();

				Message message = null;
				if (action.getType() == IActionTypesEnum.DEPLOY) {
					Set<Parameter> params = action.getParameters();
					String descriptorUrl = "";
					String crc = "";
					for (Parameter p : params) {
						if (p.getName()
								.equals(IActionParamsEnum.DEPLOYMENT_URL)) {
							descriptorUrl = p.getValue();
						}
						if (p.getName()
								.equals(IActionParamsEnum.DEPLOYMENT_CRC)) {
							crc = p.getValue();
						}
					}
					logger.info("Adding new deploy message");
					message = new DeployMessage(descriptorUrl, Long
							.parseLong(crc), action.getActionId());
				} else if (action.getType() == IActionTypesEnum.RESTART) {
					logger.info("Adding new restartPlayer message");
					message = new RestartPlayerMessage(action.getActionId());
				} else if (action.getType() == IActionTypesEnum.REBOOT) {
					logger.info("Adding new rebootPlayer message");
					message = new RebootPlayerMessage(action.getActionId());
				} else if (action.getType() == IActionTypesEnum.RECONFIGURE) {
					logger.info("Adding new reconfigure message");
					message = new ReconfigureMessage(action.getActionId(),
							manager, RestartPlayerMessage.getPlayerProcess());
				} else if (action.getType() == IActionTypesEnum.FETCH_LOGS) {
					logger.info("Adding new fetch logs message");
					message = new FetchLogsMessage(action.getActionId());
				}
				// add to the pump
				if (message != null) {
					message.setHibernateTemplate(hibernateTemplate);
					manager.getPump().addMessage(message);
				}
			}
		}
	}

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
