/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 13, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.beans;

import org.springframework.web.multipart.MultipartFile;

import com.kesdip.business.constenum.IMediaCopyPolicyEnum;

/**
 * Utility bean to assist in content deployment.
 * 
 * @author gerogias
 */
public class ContentDeploymentBean extends BaseMultitargetBean {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The uploaded content file.
	 */
	private MultipartFile contentFile = null;
	
	/**
	 * Name for the deployment.
	 */
	private String name = null;
	
	/**
	 * The policy to apply when copying media files.
	 * @see IMediaCopyPolicyEnum
	 */
	private int policy = IMediaCopyPolicyEnum.KEEP_EXISTING;
	
	/**
	 * @return the contentFile
	 */
	public MultipartFile getContentFile() {
		return contentFile;
	}

	/**
	 * @return the policy
	 */
	public int getPolicy() {
		return policy;
	}

	/**
	 * @param policy the overwrite policy to set
	 */
	public void setPolicy(int policy) {
		this.policy = policy;
	}

	/**
	 * @param contentFile the contentFile to set
	 */
	public void setContentFile(MultipartFile contentFile) {
		this.contentFile = contentFile;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
