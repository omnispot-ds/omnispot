/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.validation;

import com.kesdip.business.util.Errors;

/**
 * Performs validation on an object.
 * 
 * @author gerogias
 */
public interface Validator {

	/**
	 * Performs validation.
	 * 
	 * @param obj
	 * @param errors
	 *            where to add errors
	 */
	public void validate(Object obj, Errors errors);
}
