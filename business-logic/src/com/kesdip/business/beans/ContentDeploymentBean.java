/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 13, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.beans;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import com.kesdip.business.domain.generated.Customer;
import com.kesdip.business.domain.generated.Installation;
import com.kesdip.business.domain.generated.InstallationGroup;
import com.kesdip.business.domain.generated.Site;

/**
 * Utility bean to assist in content deployment.
 * 
 * @author gerogias
 */
public class ContentDeploymentBean implements Serializable {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The target installation.
	 */
	private Installation installation = null;

	/**
	 * The target group.
	 */
	private InstallationGroup installationGroup = null;

	/**
	 * The target site.
	 */
	private Site site = null;
	
	/**
	 * The uploaded content file.
	 */
	private MultipartFile contentFile = null;
	
	/**
	 * The target customer.
	 */
	private Customer customer = null;

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
	 * @return the installation
	 */
	public Installation getInstallation() {
		return installation;
	}

	/**
	 * @return the installationGroup
	 */
	public InstallationGroup getInstallationGroup() {
		return installationGroup;
	}

	/**
	 * @return the site
	 */
	public Site getSite() {
		return site;
	}

	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	/**
	 * @param installation the installation to set
	 */
	public void setInstallation(Installation installation) {
		this.installation = installation;
	}

	/**
	 * @param installationGroup the installationGroup to set
	 */
	public void setInstallationGroup(InstallationGroup installationGroup) {
		this.installationGroup = installationGroup;
	}

	/**
	 * @param site the site to set
	 */
	public void setSite(Site site) {
		this.site = site;
	}

	/**
	 * @return the contentFile
	 */
	public MultipartFile getContentFile() {
		return contentFile;
	}

	/**
	 * @param contentFile the contentFile to set
	 */
	public void setContentFile(MultipartFile contentFile) {
		this.contentFile = contentFile;
	}
}
