/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 1, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */
package com.kesdip.admin.web.constenum;

/**
 * Enumeration of the different object keys.
 * 
 * @author gerogias
 */
public interface IObjectKeysEnum {

	/**
	 * The request key to prevent multiple resource inclusion.
	 */
	String REQUEST_TAG_KEY = "treeViewTag";

	/**
	 * Spring context key for the tree view bean.
	 */
	String TREE_VIEW_GEN_KEY = "treeViewGenerator";

	/**
	 * Spring context key for the option list bean.
	 */
	String OPTION_LIST_GEN_KEY = "optionListGenerator";

	/**
	 * The current object in the request.
	 */
	String CURRENT_REQUEST_OBJECT = "currentObject";
	
	/**
	 * The status bean.
	 */
	String STATUS_BEAN_KEY = "statusBean";
}
