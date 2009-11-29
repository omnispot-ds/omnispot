/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jun 23, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.designer.constenum;

import com.kesdip.designer.properties.ResourceListCellEditor;
import com.kesdip.designer.properties.ResourceListPropertyDescriptor;
import com.kesdip.designer.properties.VideoResourceListCellEditor;

/**
 * Enumeration of the different {@link ResourceListCellEditor} instances the
 * {@link ResourceListPropertyDescriptor} can create.
 * 
 * @author gerogias
 */
public interface ResourceListCellEditorTypes {

	/**
	 * Represents a {@link ResourceListCellEditor}.
	 */
	int GENERIC_RESOURCE_LIST_EDITOR = 1;

	/**
	 * Represents a {@link VideoResourceListCellEditor}.
	 */
	int VIDEO_RESOURCE_LIST_EDITOR = 2;
}
