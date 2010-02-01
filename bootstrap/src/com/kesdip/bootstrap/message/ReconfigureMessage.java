/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 01 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.bootstrap.message;

import java.io.PrintStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.kesdip.bootstrap.Manager;
import com.kesdip.business.constenum.IClientReconfigureComponentsEnum;
import com.kesdip.business.domain.admin.generated.Action;
import com.kesdip.business.domain.admin.generated.Parameter;
import com.kesdip.common.configure.ApplicationContextBeanSetter;
import com.kesdip.common.configure.BeanSetter;
import com.kesdip.common.exception.FieldSetException;
import com.kesdip.player.Player;
import com.kesdip.player.configure.DeploymentConfigurer;

/**
 * Reconfigures some aspect of the bootstrap and/or player.
 * <p>
 * The class retrieves the parameter name-value pairs from the {@link Action}
 * stored in the DB and determines if they refer to bootstrap or player. Each
 * field name must be of the form
 * <code>{manager|player}.[beanId.]field1[.nestedField2...]</code>. The first
 * part determines if the rest of the expression refers to a bean in the
 * bootstrap (manager) or the player. If this does not happen, the error is
 * logged and the expression is ignored. Else, the 1st part is stripped and the
 * rest is either resolved against the {@link Manager} instance and the
 * contained Spring context or is passed to the {@link Player} process via
 * command-line.
 * </p>
 * 
 * @author gerogias
 * @see IClientReconfigureComponentsEnum
 * @see DeploymentConfigurer
 */
public class ReconfigureMessage extends Message {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ReconfigureMessage.class);

	/**
	 * The action id.
	 */
	private String actionId = null;

	/**
	 * The bootstrap manager instance.
	 */
	private Manager manager = null;

	/**
	 * The player process.
	 */
	private Process playerProcess = null;

	/**
	 * The player's input
	 */
	private PrintStream playerInput = null;

	/**
	 * Constructor.
	 * 
	 * @param actionId
	 *            the action's id
	 * @param manager
	 *            the bootstrap manager class
	 * @param playerProcess
	 *            the player's process instance
	 */
	public ReconfigureMessage(String actionId, Manager manager,
			Process playerProcess) {
		this.actionId = actionId;
		this.manager = manager;
		this.playerProcess = playerProcess;
		if (playerProcess != null) {
			this.playerInput = new PrintStream(playerProcess.getOutputStream());
		}
	}

	/**
	 * @see com.kesdip.bootstrap.message.Message#getActionId()
	 */
	@Override
	public String getActionId() {
		return actionId;
	}

	/**
	 * Retrieve the message from the DB and process its parameters passing the
	 * name-value pairs to the manager or player. Invalid expressions are logged
	 * and ignored.
	 * 
	 * @see com.kesdip.bootstrap.message.Message#process()
	 */
	@Override
	public void process() throws Exception {

		Action action = getActionFromDb();
		if (action != null) {
			for (Parameter parameter : action.getParameters()) {
				processParameter(parameter);
			}
			try {
				playerInput.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	/**
	 * @see com.kesdip.bootstrap.message.IMessage#toMessageString()
	 */
	@Override
	public String toMessageString() {
		return "[Reconfigure]";
	}

	/**
	 * @return Action the action from the DB based on the actionId
	 */
	@SuppressWarnings("unchecked")
	private final Action getActionFromDb() {

		HibernateTemplate hibernateTemplate = getHibernateTemplate();
		List<Action> actions = hibernateTemplate.find("select a from "
				+ Action.class.getName() + " a where a.actionId = '"
				+ getActionId() + "'");
		return !actions.isEmpty() ? actions.iterator().next() : null;
	}

	/**
	 * Process the expression of the parameter and delegate to the right
	 * 
	 * @param parameter
	 *            the parameter to process
	 */
	private final void processParameter(Parameter parameter) {
		String expression = parameter.getName();
		// sanity checks
		if (!ApplicationContextBeanSetter.isExpressionCorrect(expression)) {
			logger.warn("Invalid expression: " + expression);
			return;
		}

		String[] parts = expression.split("\\.");
		String beanExpression = expression
				.substring(expression.indexOf('.') + 1);
		if (IClientReconfigureComponentsEnum.MANAGER.equals(parts[0])) {
			try {
				processManagerExpression(beanExpression, parameter.getValue());
			} catch (FieldSetException fse) {
				logger.error("Error setting value for " + expression, fse);
			}
		} else if (IClientReconfigureComponentsEnum.PLAYER.equals(parts[0])) {
			// send to process input
			playerInput.println(beanExpression + '=' + parameter.getValue());
		} else {
			logger.warn("Invalid module name in expression: " + expression);
		}

	}

	/**
	 * Updates the {@link Manager} instance using the given expression/value
	 * pair. If the expression identifies a field, the value is updated against
	 * the {@link Manager} instance itself. Else, if it defines a bean, it is
	 * evaluated against the nested Spring application context.
	 * 
	 * @param expression
	 *            the expression
	 * @param value
	 *            the value
	 * @throws FieldSetException
	 *             if the field fails to update
	 */
	private final void processManagerExpression(String expression, Object value)
			throws FieldSetException {
		boolean isNestedBean = expression.indexOf('.') != -1;
		if (!isNestedBean) {
			// a Manager field
			BeanSetter beanSetter = new BeanSetter(manager);
			if(!beanSetter.setValue(expression, value, "manager")) {
				logger.warn("Value not set for " + expression);
			}
		} else {
			// an application context bean field
			ApplicationContextBeanSetter setter = new ApplicationContextBeanSetter(
					manager.getApplicationContext());
			if(!setter.setValue(expression, value)) {
				logger.warn("Value not set for " + expression);
			}
		}
	}
}
