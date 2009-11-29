/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 09 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util.propertyeditor;

import java.awt.Color;

import com.kesdip.common.test.BaseTest;

/**
 * Test case for {@link ColorEditor}.
 * 
 * @author gerogias
 */
public class ColorEditorTest extends BaseTest {

	/**
	 * The instance to test.
	 */
	private ColorEditor editor = null;
	
	/**
	 * @see com.kesdip.common.test.BaseTest#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		this.editor = new ColorEditor();
	}

	/**
	 * @see com.kesdip.common.test.BaseTest#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		this.editor = null;
	}

	/**
	 * Test method for
	 * {@link com.kesdip.common.util.propertyeditor.ColorEditor#getAsText()}.
	 */
	public final void testGetAsText() {
		// null value
		editor.setValue(null);
		assertEquals("Null value", "", editor.getAsText());
		// normal value
		editor.setValue(new Color(128, 128, 128));
		assertEquals("Normal value", "(128,128,128)", editor.getAsText());
	}

	/**
	 * Test method for
	 * {@link com.kesdip.common.util.propertyeditor.ColorEditor#setAsText(java.lang.String)}
	 * .
	 */
	public final void testSetAsTextString() {
		// null value
		editor.setAsText(null);
		assertNull("Empty value", editor.getValue());
		editor.setAsText("");
		assertNull("Empty value", editor.getValue());
		
		// malformed
		try {
			editor.setAsText("test");
			fail("Malformed value");
		} catch (IllegalArgumentException iae) {
			// success
		}
		try {
			editor.setAsText("127,12,34)");
			fail("Malformed value");
		} catch (IllegalArgumentException iae) {
			// success
		}
		
		// missing value
		try {
			editor.setAsText("(12,34)");
			fail("Malformed value");
		} catch (IllegalArgumentException iae) {
			// success
		}
		
		// out of range
		try {
			editor.setAsText("(-127,12,34)");
			fail("Out of range value");
		} catch (IllegalArgumentException iae) {
			// success
		}
		try {
			editor.setAsText("(127,324,34)");
			fail("Out of range value");
		} catch (IllegalArgumentException iae) {
			// success
		}
		try {
			editor.setAsText("(127,12,-34)");
			fail("Out of range value");
		} catch (IllegalArgumentException iae) {
			// success
		}
		
		// normal
		editor.setAsText("(12,34,25)");
		assertEquals("Normal value", new Color(12, 34, 25), editor.getValue());
		
		editor.setAsText("(12,134,25)");
		assertEquals("Normal value", new Color(12, 134, 25), editor.getValue());
	}

}
