package com.kesdip.common.util;

import java.io.File;

import com.kesdip.common.test.BaseTest;

/**
 * Test case for FileUtils class.
 * 
 * @author gerogias
 */
public class FileUtilsTest extends BaseTest {

	private File testFile1 = null;

	private File testFile2 = null;

	protected void setUp() throws Exception {
		super.setUp();
		testFile1 = new File(getClass().getClassLoader().getResource(
				"com/kesdip/common/util/resources/test1.txt").getFile());
		testFile2 = new File(getClass().getClassLoader().getResource(
				"com/kesdip/common/util/resources/test2.txt").getFile());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		testFile1 = null;
		testFile2 = null;
	}

	public final void testTail() throws Exception {
		assertEquals("2 lines expected", "Line 4\r\nLine 5", FileUtils.tail(
				testFile1, 2));
		assertEquals("2 lines expected", "Γραμμή 4\r\nΓραμμή 5", FileUtils
				.tail(testFile2, 2));

		assertEquals("5 lines expected",
				"Line 1\r\nLine 2\r\nLine 3\r\nLine 4\r\nLine 5", FileUtils
						.tail(testFile1, 6));
		assertEquals("5 lines expected",
				"Γραμμή 1\r\nΓραμμή 2\r\nΓραμμή 3\r\nΓραμμή 4\r\nΓραμμή 5",
				FileUtils.tail(testFile2, 6));
	}

}
