/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Mar 17, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.designer.constenum;

/**
 * An enumeration of various file names.
 * 
 * @author gerogias
 */
public interface IFileNames {

	/**
	 * The deployment file name, when serialized as part of a publish action.
	 */
	String DEPLOYMENT_XML = "deployment.xml";

	/**
	 * The file containing installed plugin versions in the current directory.
	 */
	String VERSIONS_TXT = "versions.txt";
	
	/**
	 * Location of the admin console WAR in the classpath.
	 */
	String ADMIN_CONSOLE_WAR = "/lib/admin-console.war";
	
	/**
	 * Manager info file.
	 */
	String MANAGER_INFO = "manager.info";
}
