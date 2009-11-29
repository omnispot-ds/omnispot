/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 02 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.configure;

import java.util.GregorianCalendar;

import com.kesdip.common.configure.resources.TestBean;
import com.kesdip.common.exception.FieldSetException;
import com.kesdip.common.test.BaseSpringTest;

/**
 * Test case for {@link ApplicationContextBeanSetter}.
 * 
 * @author gerogias
 */
public class ApplicationContextBeanSetterTest extends BaseSpringTest {

	/**
	 * Instance to test.
	 */
	private ApplicationContextBeanSetter instance = null;

	/**
	 * @see com.kesdip.common.test.BaseSpringTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		instance = new ApplicationContextBeanSetter(context);
	}

	/**
	 * Test method for
	 * {@link com.kesdip.common.configure.ApplicationContextBeanSetter#setValue(java.lang.String, java.lang.Object)}
	 * .
	 */
	public final void testSetValue() throws Exception {
		// null expression
		try {
			instance.setValue(null, "test");
			fail("Null expression");
		} catch (IllegalArgumentException iae) {
			// success
		}
		// empty expression
		try {
			instance.setValue("", "test");
			fail("Empty expression");
		} catch (IllegalArgumentException iae) {
			// success
		}
		// non-existent bean
		assertFalse("Unknown bean", instance.setValue("foo.intField", 3));
		// non-existent field
		try {
			instance.setValue("testBean.steliosField", 4);
			fail("Unknown field");
		} catch (FieldSetException fse) {
			// success
		}
		// incorrect type
		try {
			instance.setValue("testBean.intField", "test");
			fail("Incorrect type");
		} catch (FieldSetException fse) {
			// success
		}
		
		TestBean testBean = (TestBean)context.getBean("testBean");
		// correct examples
		assertTrue("Correct field", instance.setValue("testBean.intField", 10));
		assertEquals("Correct field", 10, testBean.getIntField());
		assertTrue("Correct field", instance.setValue("testBean.floatField", 7.45));
		assertEquals("Correct field", 7.45F, testBean.getFloatField());
		assertTrue("Correct field", instance.setValue("testBean.stringField", "A test value"));
		assertEquals("Correct field", "A test value", testBean.getStringField());
		assertTrue("Correct field", instance.setValue("testBean.dateField", "01/09/2009 10:30:30"));
		assertEquals("Correct field", new GregorianCalendar(2009, 8, 1, 10, 30, 30).getTime(), testBean.getDateField());
		assertTrue("Correct field", instance.setValue("testBean.fileArrayField", "c:/tmp/test.txt,c:/foo/bar.txt"));
		assertEquals("Correct field", 2, testBean.getFileArrayField().length);
		assertTrue("Correct field", instance.setValue("testBean.nestedBean.intField", "20"));
		assertEquals("Correct field", 20, testBean.getNestedBean().getIntField());
	}

	/**
	 * Test method for
	 * {@link com.kesdip.common.configure.ApplicationContextBeanSetter#isExpressionCorrect(java.lang.String)}
	 * .
	 */
	public final void testIsExpressionCorrect() {
		assertFalse("Null expression", ApplicationContextBeanSetter
				.isExpressionCorrect(null));
		assertFalse("Empty expression", ApplicationContextBeanSetter
				.isExpressionCorrect(""));
		assertFalse("Invalid expression", ApplicationContextBeanSetter
				.isExpressionCorrect("bean"));
		assertFalse("Invalid expression", ApplicationContextBeanSetter
				.isExpressionCorrect("bean."));
		assertFalse("Invalid expression", ApplicationContextBeanSetter
				.isExpressionCorrect(".field"));
		assertTrue("Correct expression", ApplicationContextBeanSetter
				.isExpressionCorrect("bean.field"));
		assertTrue("Correct expression", ApplicationContextBeanSetter
				.isExpressionCorrect("bean.field.field"));
	}

	/**
	 * @see com.kesdip.common.test.BaseSpringTest#getAppContextPath()
	 */
	@Override
	protected String getAppContextPath() {
		return "/com/kesdip/common/configure/resources/applicationContextBeanSetter.xml";
	}

}
