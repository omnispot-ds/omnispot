/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Mar 17, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.constenum;

/**
 * The policy to apply when copying deployment media files.
 * 
 * @author gerogias
 */
public interface IMediaCopyPolicyEnum {

	/**
	 * Keep existing files.
	 */
	int KEEP_EXISTING = 0;

	/**
	 * Always overwrite existing files.
	 */
	int OVERWRITE_ALWAYS = 1;

	/**
	 * If the name is the same and CRC differs, then create a new file with a
	 * unique name.
	 */
	int CHECK_CRC = 2;
}
