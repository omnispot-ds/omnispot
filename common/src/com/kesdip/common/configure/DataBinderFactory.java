/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 02 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.configure;

import java.awt.Color;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.validation.DataBinder;
import org.springframework.validation.DefaultBindingErrorProcessor;

import com.kesdip.common.util.StringUtils;
import com.kesdip.common.util.propertyeditor.BaseTypeEditor;
import com.kesdip.common.util.propertyeditor.ColorEditor;
import com.kesdip.common.util.propertyeditor.DateTimeEditor;
import com.kesdip.common.util.propertyeditor.FileArrayEditor;

/**
 * Factory class for {@link DataBinder} instances. The main duty is to
 * pre-populate the instances with custom editors. These instances are meant to
 * be used outside of the context of Spring's execution. Inside Spring (e.g.
 * while processing a web request) this should be left to the framework.
 * 
 * @author gerogias
 */
public class DataBinderFactory {

	/**
	 * The custom editors.
	 */
	private static Map<Class<?>, BaseTypeEditor> customEditors = null;

	/**
	 * Private, default constructor.
	 */
	private DataBinderFactory() {
		// do nothing
	}

	/**
	 * Static initializer for custom editors. Add new instances here.
	 */
	static {
		customEditors = new HashMap<Class<?>, BaseTypeEditor>();
		customEditors.put(Date.class, new DateTimeEditor());
		customEditors.put(File[].class, new FileArrayEditor());
		customEditors.put(Color.class, new ColorEditor());
	}

	/**
	 * Creates a DataBinder instance, registering all required custom editors.
	 * 
	 * @param bean
	 *            the bean to wrap
	 * @param name
	 *            the name of the bean
	 * @return DataBinder the created instance
	 * @throws IllegalArgumentException
	 *             if the arguments are <code>null</code>/empty
	 */
	public static DataBinder createDataBinder(Object bean, String name)
			throws IllegalArgumentException {

		if (bean == null) {
			throw new IllegalArgumentException("Bean cannot be null");
		}
		if (StringUtils.isEmpty(name)) {
			throw new IllegalArgumentException("Bean name cannot be null/empty");
		}

		DataBinder binder = new DataBinder(bean, name);
		binder.setBindingErrorProcessor(new DefaultBindingErrorProcessor());
		binder.setIgnoreInvalidFields(false);
		binder.setIgnoreUnknownFields(false);
		for (Class<?> clazz : customEditors.keySet()) {
			binder.registerCustomEditor(clazz, customEditors.get(clazz));
		}
		return binder;
	}

	/**
	 * Adds an editor for a specific type in the global map. This method may
	 * also be used to "override" an already registered editor with a new one.
	 * 
	 * @param supportedType
	 *            the class the editor is for
	 * @param editor
	 *            the editor instance
	 * @throws IllegalArgumentException
	 *             if an argument is <code>null</code>
	 */
	public static void addCustomTypeEditor(Class<?> supportedType,
			BaseTypeEditor editor) throws IllegalArgumentException {

		if (supportedType == null || editor == null) {
			throw new IllegalArgumentException("Arguments cannot be null");
		}
		customEditors.put(supportedType, editor);
	}
}
