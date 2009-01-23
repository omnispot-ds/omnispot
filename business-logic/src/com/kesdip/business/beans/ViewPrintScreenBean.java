/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 16, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.kesdip.business.domain.generated.Customer;
import com.kesdip.business.domain.generated.Installation;
import com.kesdip.business.domain.generated.InstallationGroup;
import com.kesdip.business.domain.generated.Site;

/**
 * Bean to assist in installation printscreen viewing.
 * 
 * @author gerogias
 */
public class ViewPrintScreenBean implements Serializable {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The name of the parent entity;
	 */
	private String entityName = null;
	
	/**
	 * The nested printscreens.
	 */
	private Set<PrintScreen> printScreens = null;

	/**
	 * The customer to examine.
	 */
	private Customer customer = null;
	
	/**
	 * The InstallationGroup to examine.
	 */
	private InstallationGroup installationGroup = null;
	
	/**
	 * The Site to examine.
	 */
	private Site site = null;
	
	/**
	 * The Installation to examine.
	 */
	private Installation installation = null;
	
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

	/**
	 * @return the customer
	 */
	public Customer getCustomer() {
		return customer;
	}

	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * @return the installationGroup
	 */
	public InstallationGroup getInstallationGroup() {
		return installationGroup;
	}

	/**
	 * @param installationGroup the installationGroup to set
	 */
	public void setInstallationGroup(InstallationGroup installationGroup) {
		this.installationGroup = installationGroup;
	}

	/**
	 * @return the site
	 */
	public Site getSite() {
		return site;
	}

	/**
	 * @param site the site to set
	 */
	public void setSite(Site site) {
		this.site = site;
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}

	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	/**
	 * @return the installation
	 */
	public Installation getInstallation() {
		return installation;
	}

	/**
	 * @param installation the installation to set
	 */
	public void setInstallation(Installation installation) {
		this.installation = installation;
	}
}
