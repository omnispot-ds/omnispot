/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jun 23, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.designer.constenum;

import com.kesdip.designer.properties.ResourceListDialog;

/**
 * Enumeration of the different column names in the {@link ResourceListDialog}
 * and its descendants.
 * 
 * @author gerogias
 */
public interface ResourceListColumnNames {

	/**
	 * Resource identifier/path column.
	 */
	String RESOURCE = "resource";

	/**
	 * Full-screen column.
	 */
	String FULL_SCREEN = "fullscreen";

	/**
	 * CRON expression column.
	 */
	String CRON_EXPRESSION = "cronExpression";
}
