/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components.ticker;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

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
	
	private int refreshInterval;
	
	private String lastContent;
	
	//optimization logic
	
	int charsToDisplay = 100;
	
	TickerContentHandler contentHandler;
	
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
		if (logger.isTraceEnabled())
			logger.trace("addTrailingChar called");
		sb.append(contentHandler.nextChunck());
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
		loadContent();
		try {
			Scheduler sched = new StdSchedulerFactory().getScheduler();

			sched.start();
			
			JobDetail jobDetail = new JobDetail("refreshJob",
                    null,
                    RefreshJob.class);
			
			jobDetail.getJobDataMap().put("source", this);
			
			Trigger trigger = TriggerUtils.makeMinutelyTrigger(refreshInterval);
			Calendar start = Calendar.getInstance();
			start.add(Calendar.MINUTE, refreshInterval);
			trigger.setStartTime(start.getTime());
			trigger.setName("aTrigger");

			sched.scheduleJob(jobDetail, trigger);
			
		} catch (SchedulerException e) {
			logger.error("Unable to schedule refresh job", e);
		}
	}
	
	/**
	 * Loads content from the specified rss source
	 */
	void loadContent(){		
		createFeed();
		readFeed();
	}

	private void createFeed() {
		logger.info("createFeed() called");
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
		logger.info("readFeed() called");
		if (feed == null){
			createFeed();
		}
		if (feed == null) {
			
			return;
		}
		List entries = feed.getEntries();
		StringBuilder builder = new StringBuilder();
		for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
			SyndEntry syndEntry = (SyndEntry) iterator.next();

			builder.append(syndEntry.getTitle());
			if(!showOnlyTitles){
				builder.append(afterTitle != null ? afterTitle : " ");
				builder.append(syndEntry.getDescription().getValue());
			}
			builder.append(itemSeparator != null ? itemSeparator : " ");
		}
		lastContent = builder.toString();
		contentHandler = new TickerContentHandler();
		
		if(sb == null){
			sb = new StringBuilder();
			sb.append(contentHandler.nextChunck());
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

	public int getRefreshInterval() {
		return refreshInterval;
	}

	public void setRefreshInterval(int refreshInterval) {
		this.refreshInterval = refreshInterval;
	}
	
	private class TickerContentHandler {
		
		List<String> chuncksList;
		int chuncksIndex = 0;
		int numberOfChuncks = 0;
		
		public TickerContentHandler() {
			sliceContent();
		}
		
		private void sliceContent() {
			System.out.println("-----------------------");
			numberOfChuncks = lastContent.length()/charsToDisplay;
			chuncksList = new ArrayList<String>();
			for (int i = 1;i <= numberOfChuncks;i++) {
				chuncksList.add(lastContent.substring((i-1)*charsToDisplay,i*charsToDisplay));
			}
			chuncksList.add(lastContent.substring(numberOfChuncks*charsToDisplay));
			
			if (logger.isDebugEnabled()) {
				logger.debug("content length:" + lastContent.length());
				logger.debug("chars to display: " + charsToDisplay);
				logger.debug("number of chuncks: " + numberOfChuncks);
				logger.debug("chuncks list contents : ");
				int i = 0;
				for (String chunck:chuncksList) {
					logger.debug(i+": "+chunck);
					i++;
				}
			}
		}
		
		public String nextChunck() {
			String retVal = chuncksList.get(chuncksIndex);
			
			if (chuncksIndex == chuncksList.size()) 
				chuncksIndex = 0;
			
			chuncksIndex++;
			
			return retVal;
		}
	}
}
