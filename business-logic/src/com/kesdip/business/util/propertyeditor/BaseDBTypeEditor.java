/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.util.propertyeditor;

import org.springframework.orm.hibernate3.HibernateTemplate;

import com.kesdip.common.util.propertyeditor.BaseTypeEditor;

/**
 * Base class for all custom type resolvers needing DB access.
 * 
 * @author gerogias
 */
public class BaseDBTypeEditor extends BaseTypeEditor {

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
