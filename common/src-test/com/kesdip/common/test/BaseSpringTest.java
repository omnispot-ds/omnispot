/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 02 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Base class for all Spring-related test cases. Loads the XML application
 * context defined by {@link #getAppContextPath()} during setup.
 * 
 * @author gerogias
 */
public abstract class BaseSpringTest extends BaseTest {

	/**
	 * The Spring context instance.
	 */
	protected ApplicationContext context = null;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		context = new ClassPathXmlApplicationContext(getAppContextPath());
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		context = null;
	}

	/**
	 * Template method for descendants.
	 * 
	 * @return String the resource classpath of the XML context
	 */
	protected abstract String getAppContextPath();
}
