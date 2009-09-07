/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 02 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.test;

import java.io.File;
import java.net.URL;
import java.util.Random;

import junit.framework.TestCase;

import com.kesdip.common.util.StringUtils;

/**
 * Base class for all tests.
 * 
 * @author gerogias
 */
public class BaseTest extends TestCase {

	/**
	 * A random number generator.
	 */
	private Random randomGen = new Random(System.currentTimeMillis());

	/**
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/**
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Returns the given resource as a file.
	 * 
	 * @param path
	 *            the resource path
	 * @return File the file representing the resource or <code>null</code> if
	 *         it does not exist or it does not identify a file.
	 */
	protected File getResource(String path) {

		URL resource = Thread.currentThread().getContextClassLoader()
				.getResource(path);
		if (resource == null) {
			return null;
		}
		File file = new File(resource.getFile());
		if (!file.isFile()) {
			return null;
		}
		return file;
	}

	/**
	 * Creates a unique, temporary folder, based on the given identifier.
	 * 
	 * @param identifier
	 *            the identifier
	 * @return File the created folder
	 * @throws IllegalStateException
	 *             if the folder could not be created
	 * @throws IllegalArgumentException
	 *             if the identifier is <code>null</code>/empty
	 */
	protected File createTempFolder(String identifier)
			throws IllegalStateException, IllegalArgumentException {

		if (StringUtils.isEmpty(identifier)) {
			throw new IllegalArgumentException("Identifier cannot be empty");
		}
		String uniqueName = identifier + generateUniqueSuffix();
		File uniqueFolder = new File(System.getProperty("java.io.tmpdir"),
				uniqueName);
		if (!uniqueFolder.mkdirs()) {
			throw new IllegalStateException("Folder '"
					+ uniqueFolder.getAbsolutePath() + "' could not be created");
		}
		return uniqueFolder;
	}

	/**
	 * Generates a unique filename suffix.
	 * 
	 * @return String the suffix
	 */
	protected String generateUniqueSuffix() {

		return "_" + System.currentTimeMillis() + "_" + randomGen.nextLong();
	}
	
}
