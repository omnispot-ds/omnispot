/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.util;

import java.util.Collection;

/**
 * Methods implemented by a collection of errors. 
 * @author gerogias
 */
public interface Errors {
	
	/**
	 * Add an error for a field.
	 * @param field
	 * @param messageKey
	 */
	public void addError(String field, String messageKey);
	
	/**
	 * Add an error for the object.
	 * @param messageKey
	 */
	public void addError(String messageKey);
	
	/**
	 * Add an error. 
	 * Depending on if the field is <code>null</code> the error 
	 * is considered a field or object-level error. 
	 * @param error
	 */
	public void addError(Error error);
	
	/**
	 * @return Collection of all errors, never <code>null</code>
	 */
	public Collection<Error> getAllErrors();
	
	/**
	 * @return Collection of object-level errors, never <code>null</code>
	 */
	public Collection<Error> getObjectErrors();
	
	/**
	 * @return Collection of field errors, never <code>null</code>
	 */
	public Collection<Error> getFieldErrors();
	
	/**
	 * @return boolean <code>true</code> if there are any errors
	 */
	public boolean hasErrors();
}
