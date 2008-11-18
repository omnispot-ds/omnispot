package com.kesdip.player;

public interface TickerSource {
	void dropLeadingChar();
	void addTrailingChar();
	String getCurrentContent();
	void reset();
}
