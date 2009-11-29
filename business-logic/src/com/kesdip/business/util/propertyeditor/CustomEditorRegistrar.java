/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.util.propertyeditor;

import java.util.Map;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;

import com.kesdip.common.util.propertyeditor.BaseTypeEditor;

/**
 * Common registrar for all custom editors.
 * 
 * @author gerogias
 */
public class CustomEditorRegistrar implements PropertyEditorRegistrar {

	/**
	 * The collection of editors to use.
	 */
	private Map<Class, BaseTypeEditor> editors = null;
	
	/**
	 * Registers all custom editors.
	 * 
	 * @see org.springframework.beans.PropertyEditorRegistrar#registerCustomEditors(org.springframework.beans.PropertyEditorRegistry)
	 */
	public void registerCustomEditors(PropertyEditorRegistry registry) {
		for (Class clazz : editors.keySet()) {
			registry.registerCustomEditor(clazz, editors.get(clazz));
		}
	}

	/**
	 * @return the editors
	 */
	public Map<Class, BaseTypeEditor> getEditors() {
		return editors;
	}

	/**
	 * @param editors the editors to set
	 */
	public void setEditors(Map<Class, BaseTypeEditor> editors) {
		this.editors = editors;
	}

}
