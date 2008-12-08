/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap;

/**
 * A dummy class for testing purposes, that has a never ending main method.
 * This is used to start a process that is not the real player process in the
 * bootstrap application.
 * 
 * @author Pafsanias Ftakas
 */
public class DummyPlayer {

	public static void main(String[] args) {
		try {
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
