/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components.ticker;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RssTickerSource implements TickerSource {
	private static final Logger logger = Logger.getLogger(RssTickerSource.class);
	
	/**
	 * a default string appearing between title and description
	 */
	private static final String DEFAULT_AFTER_TITLE = ": ";
	
	/**
	 * a default string appearing between items
	 */
	private static final String DEFAULT_ITEM_SEPARATOR = " - ";
	
	private String rssUrl;

	private StringBuilder sb;
	
	private SyndFeed feed;
	
	private boolean showOnlyTitles;
	
	private String afterTitle = DEFAULT_AFTER_TITLE;
	
	private String itemSeparator = DEFAULT_ITEM_SEPARATOR;
	
	public RssTickerSource() {
		setRssUrl(null);
	}

	public RssTickerSource(String rssUrl) {
		setRssUrl(rssUrl);
	}
	
	public void setRssUrl(String rssUrl) {
		this.rssUrl = rssUrl;
	}

	@Override
	public void addTrailingChar() {
		readFeed();
	}

	@Override
	public void dropLeadingChar() {
		sb.deleteCharAt(0);
	}

	@Override
	public String getCurrentContent() {
		if (sb == null)
			reset();
		
		return sb.toString();
	}

	@Override
	public void reset() {
		sb = new StringBuilder();
		
		createFeed();

		readFeed();
	}

	private void createFeed() {
		SyndFeedInput input = new SyndFeedInput();
		try {
			feed = input.build(new XmlReader(new URL(rssUrl)));
		} catch (Exception e) {
			logger.error("Unable to create feed.", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void readFeed() {
		if (logger.isTraceEnabled()) {
			logger.trace("Reading RSS source from: " + rssUrl +
					" (" + (showOnlyTitles ? "true" : "false") + ")");
		}
		
		if (feed == null){
			createFeed();
		}
		if (feed == null)
			return;
		List entries = feed.getEntries();
		for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
			SyndEntry syndEntry = (SyndEntry) iterator.next();

			sb.append(syndEntry.getTitle());
			if(!showOnlyTitles){
				sb.append(afterTitle != null ? afterTitle : " ");
				sb.append(syndEntry.getDescription().getValue());
			}
			sb.append(itemSeparator != null ? itemSeparator : " ");
		}
	}

	public void setShowOnlyTitles(boolean showOnlyTitles) {
		this.showOnlyTitles = showOnlyTitles;
	}

	public void setAfterTitle(String afterTitle) {
		this.afterTitle = afterTitle;
	}

	public void setItemSeparator(String itemSeparator) {
		this.itemSeparator = itemSeparator;
	}
	

}
