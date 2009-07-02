/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components.ticker;

import org.springframework.beans.factory.InitializingBean;

/**
 * A simple ticker source implementation that takes the contents of the ticker
 * from a simple string. The string is wrapped around when finished.
 * 
 * @author Pafsanias Ftakas
 */
public class StringTickerSource implements TickerSource, InitializingBean {
	private String src;
	
	public void setSrc(String src) {
		this.src = src;
	}
	
	private StringBuilder sb;
	
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

	@Override
	public void afterPropertiesSet() throws Exception {
		if (src == null)
			throw new Exception("Property 'src' must be set.");
		reset();
	}
}
