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
import java.text.SimpleDateFormat;
import java.util.Date;

import com.kesdip.business.domain.admin.generated.Installation;
import com.kesdip.common.util.DateUtils;

/**
 * Represents a printscreen from an installation.
 * 
 * @author gerogias
 */
public class PrintScreen implements Serializable {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The installation where the print-screen originates from.
	 */
	private Installation installation = null;

	/**
	 * When the printscreen file was created.
	 */
	private Date fileDate = null;

	/**
	 * The absolute URL for the image.
	 */
	private String fileUrl = null;

	/**
	 * Default constructor.
	 */
	public PrintScreen() {
		// do nothing
	}

	/**
	 * @return the fileDate
	 */
	public Date getFileDate() {
		return fileDate;
	}

	/**
	 * @param fileDate
	 *            the fileDate to set
	 */
	public void setFileDate(Date fileDate) {
		this.fileDate = fileDate;
	}

	/**
	 * @return the fileUrl
	 */
	public String getFileUrl() {
		return fileUrl;
	}

	/**
	 * @param fileUrl
	 *            the fileUrl to set
	 */
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	/**
	 * @return String the fileDate as a formatted string or <code>null</code>
	 */
	public String getFileDateString() {
		if (fileDate == null) {
			return null;
		}
		return new SimpleDateFormat(DateUtils.DATE_TIME_FORMAT).format(fileDate);
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
