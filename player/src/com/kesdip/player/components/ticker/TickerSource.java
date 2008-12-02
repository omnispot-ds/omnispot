/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components.ticker;

/**
 * The interface that a ticker source should implement.
 * 
 * @author Pafsanias Ftakas
 */
public interface TickerSource {
	/**
	 * Implementors should drop the leading character from the ticker.
	 */
	void dropLeadingChar();
	
	/**
	 * Implementors should add a trailing character (potentially more) to the
	 * ticker.
	 */
	void addTrailingChar();
	
	/**
	 * Implementors will return the current content of the ticker as a string.
	 * @return The current content of the ticker as a string.
	 */
	String getCurrentContent();
	
	/**
	 * Resets the ticker contents.
	 */
	void reset();
}
