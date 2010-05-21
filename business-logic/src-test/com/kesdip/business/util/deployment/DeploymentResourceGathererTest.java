/*
 * Disclaimer:
 * Copyright 2008-2010 - Omni-Spot E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 02 Μαρ 2010
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.util.deployment;

import java.io.InputStream;
import java.util.Set;

import com.kesdip.common.test.BaseTest;
import com.kesdip.common.util.StreamUtils;

/**
 * Test case for {@link DeploymentResourceGatherer}.
 * 
 * @author gerogias
 */
public class DeploymentResourceGathererTest extends BaseTest {

	private InputStream des1 = null;
	private InputStream des2 = null;

	private DeploymentResourceGatherer gatherer = null;

	/**
	 * 3 resources, in 2 beans, duplicate ignored.
	 */
	public final void testGetResources1() throws Exception {
		gatherer = new DeploymentResourceGatherer(des1);
		Set<String> res = gatherer.getResources();
		assertEquals("Incorrect resources", 3, res.size());
		assertTrue("Resource not found", res.contains("image.jpg"));
		assertTrue("Resource not found", res.contains("2.wmv"));
		assertTrue("Resource not found", res.contains("3.wmv"));
	}

	/**
	 * 3 resources in 1 bean.
	 */
	public final void testGetResources2() throws Exception {
		gatherer = new DeploymentResourceGatherer(des2);
		Set<String> res = gatherer.getResources();
		assertEquals("Incorrect resources", 3, res.size());
		assertTrue("Resource not found", res.contains("Piano.wmv"));
		assertTrue("Resource not found", res.contains("turtuolis.wmv"));
		assertTrue("Resource not found", res.contains("test.wmv"));
	}

	/**
	 * @see com.kesdip.common.test.BaseTest#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		des1 = getClass().getClassLoader().getResourceAsStream(
				getClass().getPackage().getName().replace('.', '/')
						+ "/test1.des.xml");
		des2 = getClass().getClassLoader().getResourceAsStream(
				getClass().getPackage().getName().replace('.', '/')
						+ "/test2.des.xml");
	}

	/**
	 * @see com.kesdip.common.test.BaseTest#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		StreamUtils.close(des1);
		StreamUtils.close(des2);
		gatherer = null;
	}
}
