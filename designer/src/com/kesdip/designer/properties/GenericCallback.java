package com.kesdip.designer.properties;

/**
 * 
 * Generic callback object to give our cell editors in order
 * for them to notify us when a change to a cell has been made.
 * 
 * @author n.giamouris
 *
 */
public abstract class GenericCallback {

	public abstract Object doCallback(Object ...args);
	
}
