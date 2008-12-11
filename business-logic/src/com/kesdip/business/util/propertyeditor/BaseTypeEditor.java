/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.util.propertyeditor;

import java.beans.PropertyEditorSupport;

import org.springframework.orm.hibernate3.HibernateTemplate;

/**
 * Base class for all custom type resolvers.
 * 
 * @author gerogias
 */
public class BaseTypeEditor extends PropertyEditorSupport {

	private HibernateTemplate hibernateTemplate = null;

	/**
	 * @return the hibernateTemplate
	 */
	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	/**
	 * @param hibernateTemplate the hibernateTemplate to set
	 */
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
}
