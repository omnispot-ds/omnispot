/*
 * Disclaimer:
 * Copyright 2008 - Panou E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 04 Ιαν 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.util.propertyeditor;

import org.apache.log4j.Logger;

import com.kesdip.business.domain.admin.generated.Installation;

/**
 * @author pavlos
 */
public class InstallationEditor extends BaseDBTypeEditor {
	
	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(InstallationEditor.class);
	
	/**
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String key) throws IllegalArgumentException {
		Long id = null;
		try {
			id = Long.valueOf(key);
		} catch (NumberFormatException nfe) {
			logger.error(nfe);
			throw new IllegalArgumentException("Installation key is not a number");
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Searching for id " + id);
		}
		Installation installation = (Installation) getHibernateTemplate().get(Installation.class,
				id);
		if (installation == null) {
			logger.error("Id " + key + " does not correspond to an object");
			throw new IllegalArgumentException("Id " + key
					+ " does not correspond to an object");
		}
		setValue(installation);
	}

}
