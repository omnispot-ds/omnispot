package com.kesdip.player;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class RssTickerSource implements TickerSource {
	
	private String rssUrl;

	private StringBuilder sb;
	
	private SyndFeed feed;

	public RssTickerSource(String rssUrl) {
		this.rssUrl = rssUrl;
		reset();
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
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void readFeed() {
		if(feed == null){
			createFeed();
		}
		List entries = feed.getEntries();
		for (Iterator iterator = entries.iterator(); iterator.hasNext();) {
			SyndEntry syndEntry = (SyndEntry) iterator.next();

			sb.append(syndEntry.getTitle());
			sb.append("   ");
		}
	}
	

}
