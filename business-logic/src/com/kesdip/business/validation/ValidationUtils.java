/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.validation;

import java.util.Collection;

import com.kesdip.business.util.Errors;

/**
 * Utility methods used during validation.
 * @author gerogias
 */
public class ValidationUtils {

	/**
	 * Performs validation of the object, against the validator, adding the 
	 * errors in the collection.
	 * @param obj
	 * @param validator
	 * @param errors
	 */
	public static void validate(Object obj, Validator validator, Errors errors) {
		if (obj == null) {
			return;
		}
		validator.validate(obj, errors);
	}
	
	/**
	 * Performs validation of the object, against a collection of 
	 * validators, adding the errors in the collection.
	 * @param obj
	 * @param validators
	 * @param errors
	 */
	public static void validate(Object obj, Collection<Validator> validators, Errors errors) {
		if (obj == null) {
			return;
		}
		for (Validator validator : validators) {
			validate(obj, validator, errors);
		}
	}
}
