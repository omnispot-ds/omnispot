/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 07 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.configure;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.PropertyValues;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import com.kesdip.common.exception.FieldSetException;
import com.kesdip.common.util.StringUtils;

/**
 * A utility class to set the value of JavaBeans.
 * <p>
 * The class wraps a bean and accepts property modification requests using an
 * expression and a value. The value can be in the target property's type but it
 * is usually a string. The class takes care of type conversions internally.
 * </p>
 * 
 * @author gerogias
 */
public class BeanSetter {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(BeanSetter.class);

	/**
	 * The wrapped bean.
	 */
	private Object bean = null;

	/**
	 * Constructor.
	 * 
	 * @param bean
	 *            the bean to wrap
	 */
	public BeanSetter(Object bean) {
		this.bean = bean;
	}

	/**
	 * @return the bean
	 */
	public Object getBean() {
		return bean;
	}

	/**
	 * Sets the value of the bean and field identified by the expression.
	 * <p>
	 * The expression is of the form <code>beanId.field1Id[.field2Id...]</code>
	 * and can identify a field of a nested child. The first part of the
	 * expression (beanId) is used to locate the bean in the context. If it is
	 * not found, <code>false</code> is returned. If it is found, the rest of
	 * the expression and value are used to set the field.
	 * </p>
	 * 
	 * @param expression
	 *            the expression identifying the field to set
	 * @param value
	 *            the value to set; either in the expected type or (usually) in
	 *            a proper string representation.
	 * @param beanName
	 *            the name to identify the bean. Optional, set to "bean" by
	 *            default
	 * @return boolean <code>true</code> if setting the field completed ok,
	 *         <code>false</code> if the bean could not be found in the context
	 * @throws FieldSetException
	 *             on error setting the field, usually caused by an incorrect
	 *             string representation or a <code>null</code> value
	 * @throws IllegalArgumentException
	 *             if the expression is <code>null</code>/empty or if the
	 *             expression does not adhere to the above contract
	 */
	public boolean setValue(String expression, Object value, String beanName)
			throws FieldSetException, IllegalArgumentException {

		// invalid expression
		if (StringUtils.isEmpty(expression)) {
			logger.warn("Expression is incorrect: " + expression);
			throw new IllegalArgumentException("Expression is incorrect: "
					+ expression);
		}
		String name = StringUtils.isEmpty(beanName) ? "bean" : beanName;
		DataBinder binder = DataBinderFactory.createDataBinder(bean, name);
		try {
			binder.bind(createPropertyValues(expression, value));
		} catch (NotWritablePropertyException nwpe) {
			logger.error("Error setting field: " + nwpe.getMessage());
			throw new FieldSetException(nwpe);
		}
		BindingResult result = binder.getBindingResult();
		if (!result.hasErrors()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Value updated for " + expression);
			}
			return true;
		} else {
			logger.error("Error setting field: " + result.toString());
			throw new FieldSetException(result.toString());
		}
	}

	/**
	 * Generate a {@link PropertyValues} instance from a name/value pair.
	 * 
	 * @param expression
	 *            the expression
	 * @param value
	 *            the value
	 * @return PropertyValues the generated instance
	 */
	public static final PropertyValues createPropertyValues(
			final String expression, final Object value) {

		MutablePropertyValues values = new MutablePropertyValues();
		Map<String, Object> valueMap = new HashMap<String, Object>();
		valueMap.put(expression, value);
		values.addPropertyValues(valueMap);
		return values;
	}

}
