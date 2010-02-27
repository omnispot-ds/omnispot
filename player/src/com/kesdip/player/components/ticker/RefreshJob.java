package com.kesdip.player.components.ticker;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class RefreshJob implements Job {
	private static final Logger logger = Logger.getLogger(RefreshJob.class);

	public RefreshJob() {

	}

	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		logger.info("Executing...");
		JobDataMap dataMap = ctx.getJobDetail().getJobDataMap();

		RssTickerSource source = (RssTickerSource) dataMap.get("source");

		source.updateContent();

	}

}
