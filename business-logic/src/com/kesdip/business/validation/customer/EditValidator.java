/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 9, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.validation.customer;

import org.apache.log4j.Logger;

import com.kesdip.business.domain.admin.generated.Customer;
import com.kesdip.business.util.Errors;
import com.kesdip.business.validation.BaseValidator;

/**
 * Validator for the customer deletion.
 * 
 * @author gerogias
 */
public class EditValidator extends BaseValidator {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(EditValidator.class);

	/**
	 * Performs validation.
	 * 
	 * @see gr.panouepe.monitor.common.util.Validator#validate(java.lang.Object,
	 *      gr.panouepe.monitor.common.util.Errors)
	 */
	@SuppressWarnings("unchecked")
	public void validate(Object obj, Errors errors) {
		Customer customer = (Customer) obj;

		checkNullOrEmpty(customer.getName(), "name", errors);
		checkLengthNotGreaterThan(customer.getName(), "name", 50, errors);
		checkLengthNotGreaterThan(customer.getComments(), "comments", 512,
				errors);
		if (fieldExistsCaseIgnore("CUSTOMER", "NAME", customer.getName(), customer.getId())) {
			if (logger.isDebugEnabled()) {
				logger.debug("Name " + customer.getName() + " exists");
			}
			errors.addError("name", "error.name.exists");
		}
	}
}
