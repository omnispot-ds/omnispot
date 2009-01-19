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
