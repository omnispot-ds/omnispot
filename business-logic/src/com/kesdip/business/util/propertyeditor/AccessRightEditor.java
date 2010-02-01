/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.util.propertyeditor;

import javax.management.relation.Role;

import org.apache.log4j.Logger;

import com.kesdip.business.domain.admin.generated.AccessRight;

/**
 * Resolves a name to a {@link Role}.
 * 
 * @author gerogias
 */
public class AccessRightEditor extends BaseDBTypeEditor {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(AccessRightEditor.class);

	/**
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String key) throws IllegalArgumentException {
		if (logger.isTraceEnabled()) {
			logger.trace("Searching for name " + key);
		}
		AccessRight role = (AccessRight) getHibernateTemplate().get(AccessRight.class, key);
		if (role == null) {
			logger.error("Name " + key + " does not correspond to an object");
			throw new IllegalArgumentException("Name " + key
					+ " does not correspond to an object");
		}
		setValue(role);
	}
}
