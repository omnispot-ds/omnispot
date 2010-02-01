/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Feb 3, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kesdip.business.constenum.IActionStatusEnum;
import com.kesdip.business.constenum.IActionTypesEnum;
import com.kesdip.business.domain.admin.generated.Action;
import com.kesdip.business.domain.admin.generated.Installation;

/**
 * Used to query a set of actions for a particular target object.
 * <p>
 * Contains the {@link Action}s for the given target object organized per action
 * type, status and {@link Installation}.
 * </p>
 * <p>
 * The bean's structure allows the following enumerations in a UI
 * <ol>
 * <li><code>perTypeAndInstallation:<br/>
 * for each action type<br/>
 * for each installation<br/>
 * type all Actions  
 * </code></li>
 * <li><code>perTypeAndStatus:<br/>
 * for each action type<br/>
 * for each Action.status && Action.dateAdded<br/>
 * type all Installations  
 * </code></li>
 * </ol>
 * </p>
 * <p>
 * It also allows the display of the latest "Fetch Log" action item with a
 * non-null message.
 * </p>
 * 
 * @see IActionTypesEnum
 * @see IActionStatusEnum
 * @author gerogias
 */
public class ActionQueryBean extends BaseMultitargetBean {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Actions per type and status.
	 */
	private Map<Short, List<ActionInstallationListBean>> perTypeAndStatus = null;

	/**
	 * Actions per type and installation.
	 */
	private Map<Short, List<InstallationActionListBean>> perTypeAndInstallation = null;

	/**
	 * The latest log action.
	 */
	private Action logAction = null;
	
	/**
	 * Default constructor.
	 */
	public ActionQueryBean() {
		perTypeAndInstallation = new HashMap<Short, List<InstallationActionListBean>>();
		perTypeAndStatus = new HashMap<Short, List<ActionInstallationListBean>>();
	}

	/**
	 * Process the installation and its child Actions and add to internal maps.
	 * 
	 * @param installation
	 *            the installation
	 */
	public void addInstallation(Installation installation) {

		ActionInstallationListBean perAction = null;
		InstallationActionListBean perInstallation = null;
		List<ActionInstallationListBean> perActionList = null;
		List<InstallationActionListBean> perInstallationList = null;
		int index = -1;
		for (Action action : installation.getPendingActions()) {
			// per installation processing
			perInstallationList = perTypeAndInstallation.get(action.getType());
			if (perInstallationList == null) {
				perInstallationList = new ArrayList<InstallationActionListBean>();
				perTypeAndInstallation.put(action.getType(),
						perInstallationList);
			}
			perInstallation = new InstallationActionListBean();
			perInstallation.setInstallation(installation);
			if ((index = perInstallationList.indexOf(perInstallation)) != -1) {
				perInstallation = perInstallationList.get(index);
			} else {
				perInstallationList.add(perInstallation);
			}
			perInstallation.getActions().add(action);
			// per action processing
			perActionList = perTypeAndStatus.get(action.getType());
			if (perActionList == null) {
				perActionList = new ArrayList<ActionInstallationListBean>();
				perTypeAndStatus.put(action.getType(), perActionList);
			}
			perAction = new ActionInstallationListBean();
			perAction.setAction(action);
			if ((index = perActionList.indexOf(perAction)) != -1) {
				perAction = perActionList.get(index);
			} else {
				perActionList.add(perAction);
			}
			perAction.getInstallations().add(installation);
		}
	}

	/**
	 * Utility class which holds an Installation and an {@link Action} list. Its
	 * {@link #hashCode()} and {@link #equals(Object)} methods are based on the
	 * Action's type and dateAdded.
	 */
	public static class ActionInstallationListBean implements Serializable {

		/**
		 * Serialization version.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The action.
		 */
		private Action action = null;

		/**
		 * The installations.
		 */
		private List<Installation> installations = null;

		/**
		 * Default constructor.
		 */
		public ActionInstallationListBean() {
			installations = new ArrayList<Installation>();
		}

		/**
		 * @return the action
		 */
		public Action getAction() {
			return action;
		}

		/**
		 * @param action
		 *            the action to set
		 */
		public void setAction(Action action) {
			this.action = action;
		}

		/**
		 * @return the installations
		 */
		public List<Installation> getInstallations() {
			return installations;
		}

		/**
		 * @param installations
		 *            the installations to set
		 */
		public void setInstallations(List<Installation> installations) {
			this.installations = installations;
		}

		/**
		 * Based on the wrapped Action's status and dateAdded.
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {

			if (!(obj instanceof ActionInstallationListBean)) {
				return false;
			}
			ActionInstallationListBean bean = (ActionInstallationListBean) obj;
			if (this.action == null && bean.action == null) {
				return true;
			}
			if (this.action.getStatus() == bean.action.getStatus()
					&& this.action.getDateAdded() == null
					&& bean.action.getDateAdded() == null) {
				return true;
			}
			return this.action.getStatus() == bean.action.getStatus()
					&& this.action.getDateAdded().equals(
							bean.action.getDateAdded());
		}

		/**
		 * Based on the wrapped Action's status and dateAdded.
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			if (this.action == null) {
				return -1;
			}
			return action.getStatus()
					+ ((action.getDateAdded() != null) ? action.getDateAdded()
							.hashCode() : 7);
		}

	}

	/**
	 * Utility class which holds an Action and an{@link Installation} list. Its
	 * {@link #hashCode()} and {@link #equals(Object)} methods are based on
	 * Installation.
	 */
	public static class InstallationActionListBean implements Serializable {

		/**
		 * Serialization version.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * The installation.
		 */
		private Installation installation = null;

		/**
		 * The actions.
		 */
		private List<Action> actions = null;

		/**
		 * Default constructor.
		 */
		public InstallationActionListBean() {
			actions = new ArrayList<Action>();
		}

		/**
		 * @return the actions
		 */
		public List<Action> getActions() {
			return actions;
		}

		/**
		 * @param actions
		 *            the actions to set
		 */
		public void setActions(List<Action> actions) {
			this.actions = actions;
		}

		/**
		 * @return the installation
		 */
		public Installation getInstallation() {
			return installation;
		}

		/**
		 * @param installation
		 *            the installation to set
		 */
		public void setInstallation(Installation installation) {
			this.installation = installation;
		}

		/**
		 * Based on the wrapped Installation.
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {

			if (!(obj instanceof InstallationActionListBean)) {
				return false;
			}
			InstallationActionListBean bean = (InstallationActionListBean) obj;
			if (this.installation == null && bean.installation == null) {
				return true;
			}
			return this.installation.equals(bean.installation);
		}

		/**
		 * Based on the wrapped Installation.
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			if (this.installation == null) {
				return -1;
			}
			return installation.hashCode();
		}

	}

	/**
	 * @return the perTypeAndInstallation
	 */
	public Map<Short, List<InstallationActionListBean>> getPerTypeAndInstallation() {
		return perTypeAndInstallation;
	}

	/**
	 * @return the perTypeAndStatus
	 */
	public Map<Short, List<ActionInstallationListBean>> getPerTypeAndStatus() {
		return perTypeAndStatus;
	}

	/**
	 * @return the logAction
	 */
	public Action getLogAction() {
		return logAction;
	}

	/**
	 * @param logAction the logAction to set
	 */
	public void setLogAction(Action logAction) {
		this.logAction = logAction;
	}

}
