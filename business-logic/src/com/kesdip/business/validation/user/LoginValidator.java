/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 9, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.validation.user;

import com.kesdip.business.domain.admin.generated.User;
import com.kesdip.business.util.Errors;

/**
 * Validation for the login DTO.
 * 
 * @author gerogias
 */
public class LoginValidator extends BaseUserValidator {

	/**
	 * Checks that the username/password are not empty.
	 */
	public void validate(Object obj, Errors errors) {
		User user = (User)obj;
		checkNullOrEmpty(user.getUsername(), "username", errors);
		checkNullOrEmpty(user.getPassword(), "password", errors);
	}
}
