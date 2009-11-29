/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.beans;

import java.io.Serializable;

/**
 * Bean holding list item id/label pairs.
 * 
 * @author gerogias
 */
public class ListItemBean implements Serializable {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public ListItemBean() {
		// do nothing
	}

	/**
	 * Constructor.
	 * 
	 * @param id the id
	 * @param label the label
	 */
	public ListItemBean(Long id, String label) {
		this.id = id;
		this.label = label;
	}

	/**
	 * The id.
	 */
	private Long id = null;

	/**
	 * The label.
	 */
	private String label = null;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}
}
