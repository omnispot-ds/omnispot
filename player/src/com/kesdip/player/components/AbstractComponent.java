/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components;

import java.awt.Color;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;
import com.kesdip.player.DeploymentLayout.CompletionStatus;
import com.kesdip.player.helpers.ComponentJob;

/**
 * Abstract class that encapsulates the common functionality and state that
 * all components should have. In particular, it contains the location and size
 * of the component and its background color.
 * 
 * @author Pafsanias Ftakas
 */
public abstract class AbstractComponent implements Component {
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected Color backgroundColor;
	protected Player player;
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void setLocked(boolean locked) {
		// Only used in the designer.
	}

	@Override
	public CompletionStatus isComplete() {
		return CompletionStatus.DONT_CARE;
	}

	@Override
	public void releaseResources() {
		// Do nothing. Subclasses should override if they need to do something
		// here. Check the documentation of the Component interface.
	}

	@Override
	public Set<Resource> gatherResources() {
		return new HashSet<Resource>();
	}
	
	/**
	 * Helper method to schedule all its resources that have cron expressions with
	 * the timing monitor.
	 * @param timingMonitor The timing monitor to schedule jobs with.
	 * @param resources The resources that potentially need to be scheduled.
	 * @throws ParseException 
	 * @throws SchedulerException 
	 */
	protected void scheduleResources(TimingMonitor timingMonitor,
			List<Resource> resources) throws ParseException, SchedulerException {
		for (Resource resource : resources) {
			if (resource.getCronExpression() == null)
				continue;
			
			Trigger trigger = new CronTrigger(resource.getIdentifier() + "_trigger",
					"component", resource.getCronExpression());
			JobDetail jobDetail = new JobDetail(resource.getIdentifier() + "_job",
					"component", ComponentJob.class);
			jobDetail.getJobDataMap().put("component", this);
			jobDetail.getJobDataMap().put("resource", resource);
			timingMonitor.scheduleJob(jobDetail, trigger);
		}
	}

	@Override
	public synchronized void runResource(Resource resource) {
		// Do nothing. Subclasses should override if they need to do something
		// here. Check the documentation of the Component interface.
	}
	
}
