/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components.ticker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
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

import com.kesdip.player.helpers.SingleCharacterReader;
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

	File feedBackup;

	private String rssUrl;

	private StringBuilder sb;

	private SyndFeed feed;

	private boolean showOnlyTitles;

	private String afterTitle = DEFAULT_AFTER_TITLE;

	private String itemSeparator = DEFAULT_ITEM_SEPARATOR;

	private int refreshInterval;

	private String content = "";

	SingleCharacterReader charStream;

	@Override
	public void addTrailingChar() {
		sb.append(charStream.nextChar());
	}

	@Override
	public void dropLeadingChar() {
		sb.deleteCharAt(0);
	}

	@Override
	public String getCurrentContent() {
		if (sb == null) {
			reset();
		}

		return sb.toString();
	}

	@Override
	public void reset() {
		if (feedBackup == null)
			feedBackup = new File(
					System.getProperty("user.home") +
					File.separator +
					rssUrl.replaceAll("/", "").replaceAll(":", "") +
					".rss"
			);
		loadContent();
		try {
			Scheduler sched = new StdSchedulerFactory().getScheduler();

			sched.start();

			JobDetail jobDetail = new JobDetail("refreshJob"+this.toString(),
					null,
					RefreshJob.class);

			jobDetail.getJobDataMap().put("source", this);
			Trigger trigger = TriggerUtils.makeMinutelyTrigger(refreshInterval);
			Calendar start = Calendar.getInstance();
			start.add(Calendar.MINUTE, refreshInterval);
			trigger.setStartTime(start.getTime());
			trigger.setName("aTrigger"+this.toString());

			sched.scheduleJob(jobDetail, trigger);
			if (logger.isDebugEnabled())
				logger.debug("Refresh job scheduled!");

		} catch (SchedulerException e) {
			logger.error("Unable to schedule refresh job", e);
		}
	}

	boolean contentUpdate = true;
	public void updateContent() {
		contentUpdate = true;
		loadContent();
		contentUpdate=false;
	}
	
	/**
	 * Loads content from the specified rss source
	 */
	void loadContent(){	
		readFeed();

		if (charStream == null) {
			//first time called
			charStream = new SingleCharacterReader(content);
		} else {
			//called by refreshJob
			charStream.updateContent(content);
		}
		if(sb == null){
			sb = new StringBuilder();
			sb.append(charStream.nextChar());
		}	
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
		if (contentUpdate){
			createFeed();
		}
		StringBuilder builder = new StringBuilder();
		if (feed != null) {
			List entries = feed.getEntries();
			for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
				SyndEntry syndEntry = (SyndEntry) iterator.next();
				builder.append(syndEntry.getTitle());
				if(!showOnlyTitles){
					builder.append(afterTitle != null ? afterTitle : " ");
					builder.append(syndEntry.getDescription().getValue());
				}
				builder.append(itemSeparator != null ? itemSeparator : " ");
			}
			content = builder.toString();
		}

		if (content.length() == 0) {
			readFromBackUp(feedBackup);
		} else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					backUp();
				}
			})
			.start();
		}
	}

	private void backUp() {
		boolean fileCreated = false;
		try {
			if (!feedBackup.exists()) {
				fileCreated = feedBackup.createNewFile();
				if (!fileCreated) {
					logger.error("Cannot create rss file");
					return;
				}
			} 
			if (System.currentTimeMillis() - feedBackup.lastModified() < 1000l && 
					!fileCreated){
				logger.error("rss file currently modified?!another rssticker with the same url?" +
						"backing off..");
				return;//back off
			}
			if (!feedBackup.canWrite()) {
				logger.error("cannot write to rss file!");
				return;//back off
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(feedBackup));
			out.write(content.toString());
			out.close();

		} catch (IOException e) {
			logger.error("Cannot write to rss file",e);
		}
	}

	private void readFromBackUp(File file) {
		logger.warn("Feed not available..");
		try {
			StringBuffer fileData = new StringBuffer(1000);
			BufferedReader reader = new BufferedReader(
					new FileReader(file));
			char[] buf = new char[1024];
			int numRead=0;
			while((numRead=reader.read(buf)) != -1){
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[1024];
			}
			reader.close();
			content =  fileData.toString();
		} catch (IOException e){
			logger.error("Cannot read from rss file",e);
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

	public void setRssUrl(String rssUrl) {
		this.rssUrl = rssUrl;
	}
}
