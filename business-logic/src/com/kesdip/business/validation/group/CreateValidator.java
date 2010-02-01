/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 9, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.validation.group;

import org.apache.log4j.Logger;

import com.kesdip.business.domain.admin.generated.InstallationGroup;
import com.kesdip.business.util.Errors;
import com.kesdip.business.validation.BaseValidator;

/**
 * Validation for the Group creation action.
 * 
 * @author gerogias
 */
public class CreateValidator extends BaseValidator {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(CreateValidator.class);

	/**
	 * Performs validation.
	 * 
	 * @see gr.panouepe.monitor.common.util.Validator#validate(java.lang.Object,
	 *      gr.panouepe.monitor.common.util.Errors)
	 */
	public void validate(Object obj, Errors errors) {
		InstallationGroup group = (InstallationGroup) obj;

		if (group.getCustomer() == null) {
			errors.addError("error.invalid.parent");
			return;
		}
		checkNullOrEmpty(group.getName(), "name", errors);
		checkLengthNotGreaterThan(group.getName(), "name", 50, errors);
		checkLengthNotGreaterThan(group.getComments(), "comments", 512,
				errors);
		if (fieldExistsCaseIgnore("INST_GROUP", "NAME", group.getName(), "CUSTOMER_ID", group.getCustomer().getId())) {
			if (logger.isDebugEnabled()) {
				logger.debug("Name " + group.getName() + " exists");
			}
			errors.addError("name", "error.name.exists");
		}
	}
}
