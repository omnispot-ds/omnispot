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
public class DeleteValidator extends BaseUserValidator {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(DeleteValidator.class);

	/**
	 * Performs validation.
	 * 
	 * @see gr.panouepe.monitor.common.util.Validator#validate(java.lang.Object,
	 *      gr.panouepe.monitor.common.util.Errors)
	 */
	@SuppressWarnings("unchecked")
	public void validate(Object obj, Errors errors) {
		User dto = (User) obj;
		User dbUser = (User) getHibernateTemplate().get(User.class,
				dto.getUsername());
		// do not delete if last admin
		if ((hasRole(dbUser, IRolesEnum.ADMINISTRATOR) && getUsersWithAccessRight(
				IRolesEnum.ADMINISTRATOR).size() == 1)) {
			if (logger.isDebugEnabled()) {
				logger.debug("User " + dbUser.getUsername()
						+ " is the last admin");
			}
			errors.addError("error.last.admin.delete");
		}
	}
}
