/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Represents a resource that can be specified in the spring configuration as
 * part of a particular components description. E.g. an image component might
 * specify a list of images to be displayed. Each image would be represented by
 * an instance of this class.
 * </p>
 * <p>
 * Each resource has an associated optional cron expression which defines (if
 * present) the triggering times of the resource in the component in which it is
 * contained. Components must honor this.
 * </p>
 * <p>
 * Finally, a resource can have any number of component-specific attributes as
 * name-value pairs.
 * </p>
 * <p>
 * A resource is uniquely identified in collections (i.e. when using
 * {@link #equals(Object)}, {@link #hashCode()}) by its <code>identifier</code>
 * field (see Bug#148).
 * </p>
 * 
 * @author Pafsanias Ftakas
 */
public class Resource {
	private String identifier;
	private String checksum;
	private String cronExpression;
	/**
	 * Attributes as name-value pairs.
	 */
	private Map<String, String> attributes = null;

	/**
	 * Default constructor.
	 */
	public Resource() {
		attributes = new HashMap<String, String>();
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{'").append(identifier).append("',").append(checksum)
				.append(",").append(cronExpression);
		return builder.toString();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg) {
		if (arg == null) {
			return false;
		}
		if (!(arg instanceof Resource)) {
			return false;
		}
		Resource res = (Resource) arg;
		if (this.identifier == null && res.identifier == null) {
			// both identifiers null
			return true;
		}
		if (this.identifier != null) {
			// this identifier is not null
			return this.identifier.equals(res.identifier);
		} else {
			// this identifier null and the other not null
			return false;
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return identifier != null ? identifier.hashCode() : -1;
	}

}
