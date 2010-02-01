/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 22, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.validation.installation;

import org.apache.log4j.Logger;

import com.kesdip.business.domain.admin.generated.Installation;
import com.kesdip.business.util.Errors;
import com.kesdip.business.validation.BaseValidator;

/**
 * Validation for the Installation creation action.
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
		Installation installation = (Installation) obj;

		if (installation.getSite() == null) {
			errors.addError("error.invalid.parent");
			return;
		}
		
		checkNullOrEmpty(installation.getName(), "name", errors);
		checkLengthNotGreaterThan(installation.getName(), "name", 50, errors);
		checkLengthNotGreaterThan(installation.getScreenType(), "screenType",
				50, errors);
		checkLengthNotGreaterThan(installation.getComments(), "comments", 512,
				errors);
		if (fieldExistsCaseIgnore("INSTALLATION", "NAME", installation
				.getName(), "SITE_ID", installation.getSite().getId())) {
			if (logger.isDebugEnabled()) {
				logger.debug("Name " + installation.getName() + " exists");
			}
			errors.addError("name", "error.name.exists");
		}
	}
}
