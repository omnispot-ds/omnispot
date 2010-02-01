/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 11, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.util.propertyeditor;

import org.apache.log4j.Logger;

import com.kesdip.business.domain.admin.generated.Customer;

/**
 * Resolves a string id to a {@link Customer}.
 * 
 * @author gerogias
 */
public class CustomerEditor extends BaseDBTypeEditor {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(CustomerEditor.class);

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
			throw new IllegalArgumentException("Customer key is not a number");
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Searching for id " + id);
		}
		Customer customer = (Customer) getHibernateTemplate().get(Customer.class,
				id);
		if (customer == null) {
			logger.error("Id " + key + " does not correspond to an object");
			throw new IllegalArgumentException("Id " + key
					+ " does not correspond to an object");
		}
		setValue(customer);
	}
}
