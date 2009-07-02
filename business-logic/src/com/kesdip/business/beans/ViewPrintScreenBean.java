/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 16, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.beans;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Bean to assist in installation printscreen viewing.
 * 
 * @author gerogias
 */
public class ViewPrintScreenBean extends BaseMultitargetBean {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The nested printscreens.
	 */
	private Set<PrintScreen> printScreens = null;

	/**
	 * Default constructor.
	 */
	public ViewPrintScreenBean() {
		printScreens = new HashSet<PrintScreen>();
	}

	/**
	 * @param printScreen
	 *            the printscreen
	 * @return boolean <code>true</code> if the object was not added
	 */
	public boolean addPrintscreen(PrintScreen printScreen) {
		return printScreens.add(printScreen);
	}

	/**
	 * @param printScreen
	 *            the object to remove
	 * @return boolean <code>true</code> if the object existed and was removed
	 */
	public boolean removePrintscreen(PrintScreen printScreen) {
		return printScreens.remove(printScreen);
	}

	/**
	 * @return Set a read-only Set of printscreens
	 */
	public Set<PrintScreen> getPrintScreens() {
		return Collections.unmodifiableSet(printScreens);
	}
}
