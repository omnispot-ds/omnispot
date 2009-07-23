/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 15 Δεκ 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.constenum;

/**
 * Holds all possible values for the status of an {@link Installation}.
 * 
 * @author gerogias
 */
public interface IInstallationStatus {

	/**
	 * The installation is OK.
	 */
	short OK = 0;

	/**
	 * The player has a problem but the machine is reachable.
	 */
	short PLAYER_DOWN = -1;

	/**
	 * The machine is unavailable.
	 */
	short MACHINE_DOWN = -2;
}
