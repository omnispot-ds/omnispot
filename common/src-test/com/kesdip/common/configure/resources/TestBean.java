/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 02 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.configure.resources;

import java.io.File;
import java.util.Date;

/**
 * A class used in unit testing.
 * 
 * @author gerogias
 */
public class TestBean {

	private int intField = 0;

	private float floatField = 0;

	private String stringField = "";

	private Date dateField = null;

	private File[] fileArrayField = null;

	private TestNestedBean nestedBean = null;
	
	/**
	 * Default constructor.
	 */
	public TestBean() {
		nestedBean = new TestNestedBean();
	}
	
	/**
	 * @return the intField
	 */
	public int getIntField() {
		return intField;
	}

	/**
	 * @param intField the intField to set
	 */
	public void setIntField(int intField) {
		this.intField = intField;
	}

	/**
	 * @return the floatField
	 */
	public float getFloatField() {
		return floatField;
	}

	/**
	 * @param floatField the floatField to set
	 */
	public void setFloatField(float floatField) {
		this.floatField = floatField;
	}

	/**
	 * @return the stringField
	 */
	public String getStringField() {
		return stringField;
	}

	/**
	 * @param stringField the stringField to set
	 */
	public void setStringField(String stringField) {
		this.stringField = stringField;
	}

	/**
	 * @return the dateField
	 */
	public Date getDateField() {
		return dateField;
	}

	/**
	 * @param dateField the dateField to set
	 */
	public void setDateField(Date dateField) {
		this.dateField = dateField;
	}

	/**
	 * @return the fileArrayField
	 */
	public File[] getFileArrayField() {
		return fileArrayField;
	}

	/**
	 * @param fileArrayField the fileArrayField to set
	 */
	public void setFileArrayField(File[] fileArrayField) {
		this.fileArrayField = fileArrayField;
	}
	
	/**
	 * A nested bean class.
	 * 
	 * @author gerogias
	 */
	public static class TestNestedBean {
		
		private int intField = 0;

		/**
		 * @return the intField
		 */
		public int getIntField() {
			return intField;
		}

		/**
		 * @param intField the intField to set
		 */
		public void setIntField(int intField) {
			this.intField = intField;
		}
	}

	/**
	 * @return the nestedBean
	 */
	public TestNestedBean getNestedBean() {
		return nestedBean;
	}

	/**
	 * @param nestedBean the nestedBean to set
	 */
	public void setNestedBean(TestNestedBean nestedBean) {
		this.nestedBean = nestedBean;
	}
}
