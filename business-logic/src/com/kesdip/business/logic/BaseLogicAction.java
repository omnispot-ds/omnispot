/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.logic;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.kesdip.business.exception.InvalidEntityIdentifierException;
import com.kesdip.business.exception.ValidationException;
import com.kesdip.business.validation.ValidationUtils;
import com.kesdip.business.validation.Validator;

/**
 * Base class for all actions implementations.
 * 
 * @author gerogias
 */
public abstract class BaseLogicAction {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(BaseLogicAction.class);

	/**
	 * Utility Hibernate template.
	 */
	private HibernateTemplate hibernateTemplate = null;

	/**
	 * The validators for this action, organized per method name.
	 */
	private Map<String, Collection<Validator>> validators = null;

	/**
	 * The logic factory.
	 */
	private LogicFactory logicFactory = null;

	/**
	 * Default constructor.
	 */
	public BaseLogicAction() {
		validators = new HashMap<String, Collection<Validator>>();
	}

	/**
	 * Validates the object using the given set of validators.
	 * <p>
	 * If there is no set associated with this key, nothing happens.
	 * </p>
	 * 
	 * @param data
	 *            the object to validate
	 * @param methodName
	 *            the method name
	 * @throws ValidationException
	 *             thrown if there were validation errors
	 */
	protected void validate(Object data, String methodName)
			throws ValidationException {
		if (!validators.containsKey(methodName)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Validator key " + methodName + " does not exist");
			}
			return;
		}
		ValidationException exc = new ValidationException();
		ValidationUtils.validate(data, validators.get(methodName), exc);
		if (exc.hasErrors()) {
			throw exc;
		}
	}

	/**
	 * Check if the identifier is valid.
	 * 
	 * @param clazz
	 * @param id
	 * @return Object the located object
	 * @throws InvalidEntityIdentifierException
	 *             if the identifier does not exist
	 */
	protected final Object entityExists(Class clazz, Serializable id)
			throws InvalidEntityIdentifierException {
		Object obj = getHibernateTemplate().get(clazz, id);
		if (obj == null) {
			logger.error("Class: '" + clazz.getName() + "' with id: '" + id
					+ "' does not exist");
			throw new InvalidEntityIdentifierException("Class: '"
					+ clazz.getName() + "' with id: '" + id
					+ "' does not exist");
		}
		return obj;
	}

	public Map getValidators() {
		return validators;
	}

	public void setValidators(Map<String, Collection<Validator>> validators) {
		this.validators = validators;
	}

	/**
	 * @return the hibernateTemplate
	 */
	protected HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	/**
	 * @param hibernateTemplate
	 *            the hibernateTemplate to set
	 */
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	/**
	 * @return the logicFactory
	 */
	public LogicFactory getLogicFactory() {
		return logicFactory;
	}

	/**
	 * @param logicFactory
	 *            the logicFactory to set
	 */
	public void setLogicFactory(LogicFactory logicFactory) {
		this.logicFactory = logicFactory;
	}

}
