/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.exception;

import java.util.ArrayList;
import java.util.Collection;

import com.kesdip.business.util.Error;
import com.kesdip.business.util.Errors;
import com.kesdip.common.exception.BaseUncheckedException;

/**
 * An exception to wrap validation errors.
 * 
 * @author gerogias
 */
public class ValidationException extends BaseUncheckedException implements
		Errors {

	/**
	 * Serialization version.
	 */
	static final long serialVersionUID = 1L;

	/**
	 * Errors referring at field level.
	 */
	private Collection<Error> fieldErrors = null;

	/**
	 * Errors referring at object level.
	 */
	private Collection<Error> objectErrors = null;

	/**
	 * Default constructor.
	 */
	public ValidationException() {
		this.fieldErrors = new ArrayList<Error>();
		this.objectErrors = new ArrayList<Error>();
	}

	/*
	 * (non-Javadoc)
	 */
	public void addError(Error error) {
		if (error.getField() == null) {
			objectErrors.add(error);
		} else {
			fieldErrors.add(error);
		}
	}

	/*
	 * (non-Javadoc)
	 */
	public void addError(String field, String messageKey) {
		Error error = new Error(field, messageKey);
		addError(error);
	}

	/*
	 * (non-Javadoc)
	 */
	public void addError(String messageKey) {
		addError(null, messageKey);
	}

	/*
	 * (non-Javadoc)
	 */
	public Collection<Error> getAllErrors() {

		Collection<Error> errors = new ArrayList<Error>(fieldErrors.size()
				+ objectErrors.size());
		errors.addAll(fieldErrors);
		errors.addAll(objectErrors);
		return errors;
	}

	/*
	 * (non-Javadoc)
	 */
	public Collection<Error> getFieldErrors() {
		return fieldErrors;
	}

	/*
	 * (non-Javadoc)
	 */
	public Collection<Error> getObjectErrors() {
		return objectErrors;
	}

	/*
	 * (non-Javadoc)
	 */
	public boolean hasErrors() {
		return !fieldErrors.isEmpty() || !objectErrors.isEmpty();
	}

}
