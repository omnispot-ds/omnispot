/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.helpers;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.kesdip.player.components.Component;
import com.kesdip.player.components.Resource;

/**
 * Helper class. Implementation of the quartz Job interface to represent
 * jobs that will schedule a particular resource inside a component.
 * 
 * @author Pafsanias Ftakas
 */
public class ComponentJob implements Job {
	@Override
	public void execute(JobExecutionContext ctx) throws JobExecutionException {
		Component component = (Component)
			ctx.getJobDetail().getJobDataMap().get("component");
		Resource resource = (Resource)
			ctx.getJobDetail().getJobDataMap().get("resource");
		component.runResource(resource);
	}

}
