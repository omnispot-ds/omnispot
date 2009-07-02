package com.kesdip.player.components.weather;

import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RssWeatherSource extends WeatherDataSource {
	
	private static final Logger logger = Logger.getLogger(RssWeatherSource.class);
	
	private String rssUrl;

	private StringBuilder sb;
	private SyndFeed feed;

	@Override
	public Object getWeatherData() {
		update();
		return sb.toString();
	}
	
	public void update() {
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
	
	private void readFeed() {
		if (feed == null){
			createFeed();
		}
		if (feed == null)
			return;
		List<?> entries = feed.getEntries();
		for (Iterator<?> iterator = entries.iterator(); iterator.hasNext();) {
			SyndEntry syndEntry = (SyndEntry) iterator.next();

			sb.append(syndEntry.toString());
			//sb.append("   ");
		}
	}
	
	public String getRssUrl() {
		return rssUrl;
	}
	
	public void setRssUrl(String rssUrl) {
		this.rssUrl = rssUrl;
	}

}
