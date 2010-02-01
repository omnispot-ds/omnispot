/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 9, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.validation.site;

import org.apache.log4j.Logger;

import com.kesdip.business.domain.admin.generated.Site;
import com.kesdip.business.util.Errors;
import com.kesdip.business.validation.BaseValidator;

/**
 * Validator for the user deletion.
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
	public void validate(Object obj, Errors errors) {
		Site site = (Site) obj;

		if (site.getCustomer() == null) {
			errors.addError("error.invalid.parent");
			return;
		}
		checkNullOrEmpty(site.getName(), "name", errors);
		checkLengthNotGreaterThan(site.getName(), "name", 50, errors);
		checkLengthNotGreaterThan(site.getComments(), "comments", 512,
				errors);
		if (fieldExistsCaseIgnore("SITE", "NAME", site.getName(), site.getId(), "CUSTOMER_ID")) {
			if (logger.isDebugEnabled()) {
				logger.debug("Name " + site.getName() + " exists");
			}
			errors.addError("name", "error.name.exists");
		}
	}
}
