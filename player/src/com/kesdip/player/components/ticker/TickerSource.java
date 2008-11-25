package com.kesdip.player.components.ticker;

/**
 * The interface that a ticker source should implement.
 * 
 * @author Pafsanias Ftakas
 */
public interface TickerSource {
	void dropLeadingChar();
	void addTrailingChar();
	String getCurrentContent();
	void reset();
}
