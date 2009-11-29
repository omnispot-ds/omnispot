/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 01 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.configure;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import com.kesdip.common.exception.FieldSetException;
import com.kesdip.common.util.StringUtils;

/**
 * A utility class to wrap the modification of beans contained in a Spring
 * <code>ApplicationContext</code>.
 * <p>
 * The class wraps an ApplicationContext and accepts bean property modification
 * requests using an expression and a value. The value can be in the target
 * property's type but it is usually a string. The class takes care of type
 * conversions internally.
 * </p>
 * 
 * @author gerogias
 */
public class ApplicationContextBeanSetter {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ApplicationContextBeanSetter.class);

	/**
	 * The wrapped context.
	 */
	private ApplicationContext context = null;

	/**
	 * Constructor, initializes the wrapped context.
	 * 
	 * @param theCtx
	 *            the context to wrap
	 */
	public ApplicationContextBeanSetter(ApplicationContext theCtx) {
		this.context = theCtx;
	}

	/**
	 * @return ApplicationContext the context
	 */
	public ApplicationContext getContext() {
		return context;
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
	 * @return boolean <code>true</code> if setting the field completed ok,
	 *         <code>false</code> if the bean could not be found in the context
	 * @throws FieldSetException
	 *             on error setting the field, usually caused by an incorrect
	 *             string representation or a <code>null</code> value
	 * @throws IllegalArgumentException
	 *             if the expression is <code>null</code>/empty or if the
	 *             expression does not adhere to the above contract
	 */
	public boolean setValue(String expression, Object value)
			throws FieldSetException, IllegalArgumentException {

		// invalid expression
		if (!isExpressionCorrect(expression)) {
			logger.warn("Expression is incorrect: " + expression);
			throw new IllegalArgumentException("Expression is incorrect: "
					+ expression);
		}
		String[] parts = expression.split("\\.");
		Object bean = null;
		try {
			bean = context.getBean(parts[0]);
		} catch (NoSuchBeanDefinitionException nsbde) {
			// bean not found
			if (logger.isInfoEnabled()) {
				logger.info("Bean not found: " + parts[0]);
			}
			return false;
		}
		BeanSetter beanSetter = new BeanSetter(bean);
		String fieldExpression = expression
				.substring(expression.indexOf('.') + 1);
		return beanSetter.setValue(fieldExpression, value, parts[0]);
	}

	/**
	 * Utility method to check an expression if it has the expected syntax for
	 * {@link #setValue(String, Object)}. It does not check if the expression
	 * actually corresponds to any bean.
	 * 
	 * @param expression
	 *            the expression
	 * @return boolean <code>true</code> if it is correct
	 */
	public static boolean isExpressionCorrect(String expression) {
		if (StringUtils.isEmpty(expression)) {
			return false;
		}
		int lastDot = expression.lastIndexOf('.');
		if (lastDot == -1 || lastDot == 0 || lastDot == expression.length() - 1) {
			return false;
		}
		return true;
	}
}
