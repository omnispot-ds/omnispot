/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap;

import com.kesdip.bootstrap.message.DeployMessage;
import com.kesdip.common.test.BaseTest;

/**
 * Test case for {@link MessagePump}.
 * 
 * @author gerogias
 */
public class MessagePumpTest extends BaseTest {
	private MessagePump pump;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		pump = new MessagePump();
		pump.start();
	}

	public void testDeploy() throws Exception {
		pump.addMessage(new DeployMessage("file:test/sample.xml", 3965232118L,
				null));

		// sleep for a while to let the message get processed.
		Thread.sleep(10L * 60 * 1000);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		pump.stopRunning();
	}

}
