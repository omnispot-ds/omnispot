package com.kesdip.player;

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
