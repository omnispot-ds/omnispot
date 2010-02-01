/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 9, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.validation.user;

import org.apache.log4j.Logger;

import com.kesdip.business.domain.admin.generated.User;
import com.kesdip.business.util.Errors;

/**
 * Validation for the User creation action.
 * 
 * @author gerogias
 */
public class CreateValidator extends BaseUserValidator {

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
		User user = (User) obj;

		checkNullOrEmpty(user.getUsername(), "username", errors);
		checkNullOrEmpty(user.getPassword(), "password", errors);
		checkNullOrEmpty(user.getFirstName(), "firstName", errors);
		checkNullOrEmpty(user.getLastName(), "lastName", errors);
		checkLengthNotGreaterThan(user.getUsername(), "username", 50, errors);
		checkLengthNotGreaterThan(user.getPassword(), "password", 50, errors);
		checkLengthNotGreaterThan(user.getFirstName(), "firstName", 50, errors);
		checkLengthNotGreaterThan(user.getLastName(), "lastName", 50, errors);
		if (getHibernateTemplate().get(User.class, user.getUsername()) != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Username " + user.getUsername() + " exists");
			}
			errors.addError("username", "error.username.exists");
		}
		if (user.getRights() == null || user.getRights().isEmpty()) {
			logger.debug("No roles selected");
			errors.addError("rights", "error.rights.not.selected");
		}
	}
}
