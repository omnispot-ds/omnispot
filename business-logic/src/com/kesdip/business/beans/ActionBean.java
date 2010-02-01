/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 18, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.beans;

import com.kesdip.business.domain.admin.generated.Action;

/**
 * DTO encapsulating an action towards a player (start, stop, reboot, fetch
 * logs, reconfigure).
 * 
 * @author gerogias
 */
public class ActionBean extends BaseMultitargetBean {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The wrapped action.
	 */
	private Action action = null;

	/**
	 * @return the action
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(Action action) {
		this.action = action;
	}
}
