/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap;

import org.apache.log4j.Logger;

import com.kesdip.bootstrap.message.DeployMessage;

/**
 * A dummy class for testing purposes, that has a never ending main method.
 * This is used to start a process that is not the real player process in the
 * bootstrap application.
 * 
 * @author Pafsanias Ftakas
 */
public class DummyPlayer {
	private static final Logger logger = Logger.getLogger(DummyPlayer.class);

	public static void main(String[] args) {
		logger.info("Starting dummy player");
		try {
			MessagePump pump = new MessagePump();
			pump.start();
			pump.addMessage(new DeployMessage("file:test/sample.xml", 3215048850L, null));
			
			while (true) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// Intentionally left empty.
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

}
