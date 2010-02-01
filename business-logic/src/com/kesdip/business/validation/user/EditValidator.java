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

import com.kesdip.business.constenum.IRolesEnum;
import com.kesdip.business.domain.admin.generated.User;
import com.kesdip.business.util.Errors;

/**
 * Validator for the user deletion.
 * 
 * @author gerogias
 */
public class EditValidator extends BaseUserValidator {

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
		User user = (User) obj;

		checkNullOrEmpty(user.getPassword(), "password", errors);
		checkNullOrEmpty(user.getFirstName(), "firstName", errors);
		checkNullOrEmpty(user.getLastName(), "lastName", errors);
		checkLengthNotGreaterThan(user.getPassword(), "password", 50, errors);
		checkLengthNotGreaterThan(user.getFirstName(), "firstName", 50, errors);
		checkLengthNotGreaterThan(user.getLastName(), "lastName", 50, errors);
		if (user.getRights() == null || user.getRights().isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Roles are null/empty");
			}
			errors.addError("rights", "error.rights.not.selected");
		}
		// do not remove last admin right
		User dbUser = (User) getHibernateTemplate().get(User.class,
				user.getUsername());
		if ((hasRole(dbUser, IRolesEnum.ADMINISTRATOR)
				&& !hasRole(user, IRolesEnum.ADMINISTRATOR) && getUsersWithAccessRight(
				IRolesEnum.ADMINISTRATOR).size() == 1)) {
			if (logger.isDebugEnabled()) {
				logger.debug("User " + dbUser.getUsername()
						+ " is the last admin");
			}
			errors.addError("rights", "error.last.admin.delete");
		}
	}
}
