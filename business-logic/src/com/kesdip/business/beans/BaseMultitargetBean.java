/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 18, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.beans;

import java.io.Serializable;

import com.kesdip.business.domain.admin.generated.Customer;
import com.kesdip.business.domain.admin.generated.Installation;
import com.kesdip.business.domain.admin.generated.InstallationGroup;
import com.kesdip.business.domain.admin.generated.Site;

/**
 * Base class for all beans who wish to be able to refer to more than one
 * "parent" (site, customer, group, installation).
 * 
 * @author gerogias
 */
public abstract class BaseMultitargetBean implements Serializable {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The parent customer.
	 */
	private Customer customer = null;
	
	/**
	 * The parent group.
	 */
	private InstallationGroup installationGroup = null;
	
	/**
	 * The parent site.
	 */
	private Site site = null;
	
	/**
	 * The parent installation.
	 */
	private Installation installation = null;

	/**
	 * The name of the parent entity.
	 */
	private String entityName = null; 
	
	/**
	 * How many installations will be affected.
	 */
	private int installationCount = 0;
	
	/**
	 * @return the installationCount
	 */
	public int getInstallationCount() {
		return installationCount;
	}

	/**
	 * @param installationCount the installationCount to set
	 */
	public void setInstallationCount(int installationCount) {
		this.installationCount = installationCount;
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
	 * @return the name of the parent entity
	 */
	public String getEntityName() {
		if (entityName == null) {
			if (installation != null) {
				entityName = installation.getName();
			} else if (installationGroup != null) {
				entityName = installationGroup.getName();
			} else if (site != null) {
				entityName = site.getName();
			} else if (customer != null) {
				entityName = customer.getName();
			} 
		}
		return entityName;
	}

	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

}
