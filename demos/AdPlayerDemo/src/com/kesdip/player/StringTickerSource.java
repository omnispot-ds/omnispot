package com.kesdip.player;

/**
 * A simple implementation of the TickerSource interface. It takes a string as
 * its original source, and constructs a string buffer that contains as many
 * copies of the string as is necessary to fill the ticker.
 * 
 * @author Pafsanias Ftakas
 */
public class StringTickerSource implements TickerSource {
	private String src;
	
	private StringBuilder sb;
	
	public StringTickerSource(String src) {
		this.src = src;
		reset();
	}

	@Override
	public void addTrailingChar() {
		sb.append(src);
	}

	@Override
	public void dropLeadingChar() {
		sb.deleteCharAt(0);
	}

	@Override
	public String getCurrentContent() {
		return sb.toString();
	}

	@Override
	public void reset() {
		sb = new StringBuilder(src);
	}
}
