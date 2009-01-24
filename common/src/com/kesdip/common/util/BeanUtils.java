/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 1, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */
package com.kesdip.common.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;

import com.kesdip.common.exception.FieldCopyException;
import com.kesdip.common.exception.GenericSystemException;

/**
 * Common bean manipulation methods.
 * 
 * @author gerogias
 */
public class BeanUtils extends org.apache.commons.beanutils.BeanUtils {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(BeanUtils.class);

	/**
	 * Copies all public getters from source to target setters. Ignores the
	 * collections.
	 * 
	 * @param source
	 * @param target
	 * @throws IllegalArgumentException
	 *             if either object is <code>null</code> or source not a
	 *             superclass of target
	 * @throws FieldCopyException
	 *             if copy fails
	 */
	public static void copy(Object source, Object target)
			throws IllegalArgumentException, FieldCopyException {

		if (source == null || target == null) {
			logger.error("Objects cannot be null");
			throw new IllegalArgumentException("Objects cannot be null");
		}

		if (!source.getClass().isAssignableFrom(target.getClass())) {
			logger.error("Objects must be of the same type");
			throw new IllegalArgumentException(
					"Objects must be of the same type");
		}

		Method[] targetMethods = target.getClass().getDeclaredMethods();
		Method sourceMethod = null;
		String methodName = null;
		for (int i = 0; i < targetMethods.length; i++) {
			if (!targetMethods[i].getName().startsWith("set")) {
				continue;
			}
			if (targetMethods[i].getParameterTypes().length != 0) {
				continue;
			}
			if (Collection.class.isAssignableFrom(targetMethods[i]
					.getReturnType())) {
				continue;
			}
			if (Map.class.isAssignableFrom(targetMethods[i].getReturnType())) {
				continue;
			}
			if (!Modifier.isPublic(targetMethods[i].getModifiers())) {
				continue;
			}
			try {
				methodName = "get" + targetMethods[i].getName().substring(3);
				sourceMethod = target.getClass().getDeclaredMethod(methodName,
						new Class[] { targetMethods[i].getReturnType() });
				if (!Modifier.isPublic(sourceMethod.getModifiers())) {
					continue;
				}
				if (logger.isTraceEnabled()) {
					logger.trace("Copying "
							+ targetMethods[i].getName().substring(3));
				}
				sourceMethod.invoke(target, new Object[] { targetMethods[i]
						.invoke(source) });
			} catch (Exception e) {
				logger.error("Field '"
						+ targetMethods[i].getName().substring(3)
						+ "'. Message: " + e.getMessage());
				throw new FieldCopyException("Field '"
						+ targetMethods[i].getName().substring(3)
						+ "'. Message: " + e.getMessage());
			}
		}
	}

	/**
	 * Invokes the JavaBean getter for the given field.
	 * 
	 * @param obj
	 *            the object to invoke
	 * @param fieldName
	 *            the name of the field
	 * @return Object the result
	 * @throws IllegalArgumentException
	 *             if the arguments are <code>null</code>/empty
	 * @throws GenericSystemException
	 */
	public static Object invokeGetter(Object obj, String fieldName)
			throws GenericSystemException {

		if (obj == null || StringUtils.isEmpty(fieldName)) {
			logger.error("Arguments cannot be null/empty");
			throw new IllegalArgumentException("Arguments cannot be null/empty");
		}

		String getterMethodName = "get"
				+ Character.toUpperCase(fieldName.charAt(0))
				+ fieldName.substring(1);
		try {
			Method method = obj.getClass().getMethod(getterMethodName,
					(Class[]) null);
			if (logger.isTraceEnabled()) {
				logger.trace("Calling getter " + getterMethodName);
			}
			return method.invoke(obj, (Object[]) null);
		} catch (Exception e) {
			throw new GenericSystemException("Error invoking "
					+ getterMethodName, e);
		}
	}

	/**
	 * Chops off the final part from a qualified class name.
	 * 
	 * @param className
	 *            the name to process
	 * @return String the class name
	 * @throws IllegalArgumentException
	 *             if the argument is <code>null</code>
	 */
	public static final String getClassName(Class clazz)
			throws IllegalArgumentException {
		if (clazz == null) {
			logger.error("Class cannot be null");
			throw new IllegalArgumentException("Class cannot be null");
		}
		String className = clazz.getName();
		String[] parts = className.split("\\.");
		if (logger.isTraceEnabled()) {
			logger.trace("Class name " + parts[parts.length - 1]);
		}
		return parts[parts.length - 1];
	}

	/**
	 * Similar to {@link #getClassName(Class)}, with the additional processing
	 * of removing any byte manipulation suffixes (like CGLib adds).
	 * 
	 * @param clazz
	 *            the class
	 * @return String the clean class name
	 * @throws IllegalArgumentException
	 *             if the argument is <code>null</code>
	 */
	public static final String getCleanClassName(Class clazz)
			throws IllegalArgumentException {
		String name = getClassName(clazz);
		int dollarIndex = name.indexOf('$');
		String value = dollarIndex != -1 ? name.substring(0, dollarIndex)
				: name;
		if (logger.isTraceEnabled()) {
			logger.trace("Clean class name " + value);
		}
		return value;
	}
}
