package com.kesdip.bootstrap;

import com.kesdip.bootstrap.message.RebootPlayerMessage;

import junit.framework.TestCase;

/**
 * Just call the process message of the RebootPlayerMessage class to test the
 * reboot functionality works. Of course, running this test could be somewhat
 * detrimental to your running programs.
 * 
 * @author Pafsanias Ftakas
 */
public class RebootMessageTest extends TestCase {
	public void testReboot() throws Exception {
		RebootPlayerMessage msg = new RebootPlayerMessage("test");
		msg.process();
	}
}
