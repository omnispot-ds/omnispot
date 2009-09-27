/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.kesdip.common.util.DBUtils;

/**
 * Helper thread that monitors deployments. Whenever the timing is such that a
 * new deployment has to be deployed, this thread, will signal this to the
 * player thread in order for the transition to take place.
 * 
 * @author Pafsanias Ftakas
 */
public class TimingMonitor implements Runnable {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(TimingMonitor.class);

	/**
	 * Helper class. Implementation of the quartz Job interface to represent
	 * jobs that will schedule a particular deployment layout.
	 * 
	 * @author Pafsanias Ftakas
	 */
	private class LayoutJob implements Job {
		@Override
		public void execute(JobExecutionContext ctx)
				throws JobExecutionException {
			DeploymentLayout layout = (DeploymentLayout) ctx.getJobDetail()
					.getJobDataMap().get("layout");
			logger.info("Scheduler starting layout: " + layout.getName());
			startLayout(layout);
		}

	}

	private Player player;
	private Scheduler scheduler;
	private long lastDeploymentID;
	/**
	 * Flag to notify the class it is in preview mode. 
	 */
	private boolean previewMode = false;

	/**
	 * Initializing constructor.
	 * 
	 * @param player
	 *            The player associated with this timing monitor.
	 * @param previewMode
	 *            <code>true</code> if the player is in preview mode (i.e. no
	 *            DB)
	 * @throws SchedulerException
	 *             Iff something goes wrong starting the scheduler.
	 */
	public TimingMonitor(Player player, boolean previewMode)
			throws SchedulerException {
		this.player = player;
		this.scheduler = StdSchedulerFactory.getDefaultScheduler();
		this.scheduler.start();
		this.lastDeploymentID = -1;
		this.previewMode = previewMode;
	}
	
	/**
	 * Initializing constructor.
	 * Preview mode defaults to <code>false</code>.
	 * 
	 * @param player
	 *            The player associated with this timing monitor.
	 * @throws SchedulerException
	 *             Iff something goes wrong starting the scheduler.
	 */
	public TimingMonitor(Player player)
			throws SchedulerException {
		this.player = player;
		this.scheduler = StdSchedulerFactory.getDefaultScheduler();
		this.scheduler.start();
		this.lastDeploymentID = -1;
	}

	/* TRANSIENT STATE */
	@SuppressWarnings("unused")
	private Date deploymentStart;
	private Date layoutStart;
	private DeploymentLayout currentLayout;

	/**
	 * Callback. Called by the player when a layout is starting to keep the
	 * transient state of the timing monitor up to date.
	 * 
	 * @param layout
	 *            the layout that just started playing
	 */
	public synchronized void startingLayout(DeploymentLayout layout) {
		this.currentLayout = layout;
		this.layoutStart = new Date();
	}

	/**
	 * Accessor for the currently playing layout.
	 * 
	 * @return The current deployment layout.
	 */
	private synchronized DeploymentLayout getCurrentLayout() {
		return currentLayout;
	}

	/**
	 * Schedule a job with the timing monitor scheduler. If a job with the same
	 * name already exists, it does nothing.
	 * 
	 * @param jobDetail
	 *            The job details.
	 * @param trigger
	 *            The trigger details.
	 * @throws SchedulerException
	 */
	public synchronized void scheduleJob(JobDetail jobDetail, Trigger trigger)
			throws SchedulerException {
		JobDetail existingJobDetail = scheduler.getJobDetail(jobDetail
				.getName(), jobDetail.getGroup());
		if (existingJobDetail == null) {
			scheduler.scheduleJob(jobDetail, trigger);
		}
	}

	/**
	 * Helper method to remove all scheduled jobs from the quartz scheduler.
	 * 
	 * @throws SchedulerException
	 *             iff something goes wrong. private void removeAllJobs() throws
	 *             SchedulerException { String[] groupNames =
	 *             scheduler.getJobGroupNames(); for (String groupName :
	 *             groupNames) { String[] jobNames =
	 *             scheduler.getJobNames(groupName); for (String jobName :
	 *             jobNames) { scheduler.deleteJob(jobName, groupName); } } }
	 */
	/**
	 * Helper method to remove all scheduled component jobs from the quartz
	 * scheduler. Used when a layout changes, so as to remove scheduled jobs for
	 * components, but leave any scheduled jobs for sibling layouts.
	 * 
	 * @throws SchedulerException
	 *             iff something goes wrong.
	 */
	private void removeAllComponentJobs() throws SchedulerException {
		String[] jobNames = scheduler.getJobNames("component");
		for (String jobName : jobNames) {
			scheduler.deleteJob(jobName, "component");
		}
	}

	/**
	 * Helper method to start "playing" a deployment.
	 * 
	 * @param contextPath
	 *            The deployment descriptor path.
	 * @throws BeanInitializationException
	 *             if the context file does nto define the expected beans
	 * @throws IOException
	 *             if the context file is not found
	 * @throws ParseException
	 *             if CRON expressions are invalid
	 * @throws SchedulerException
	 *             if Quartz jobs fail to schedule
	 */
	private void startDeployment(long id, String contextPath)
			throws BeanInitializationException, IOException, ParseException,
			SchedulerException {
		ApplicationContext ctx = new FileSystemXmlApplicationContext(
				contextPath);
		DeploymentSettings settings = (DeploymentSettings) ctx
				.getBean("deploymentSettings");
		DeploymentContents contents = (DeploymentContents) ctx
				.getBean("deploymentContents");
		if (settings == null) {
			throw new BeanInitializationException(
					"The application context "
							+ "factory should contain a bean with ID 'deploymentSettings'.");
		}
		if (contents == null) {
			throw new BeanInitializationException(
					"The application context "
							+ "factory should contain a bean with ID 'deploymentContents'.");
		}
		removeAllComponentJobs();
		for (DeploymentLayout layout : contents.getLayouts()) {
			if (layout.getCronExpression() == null) {
				continue;
			}

			Trigger trigger = new CronTrigger(layout.getName() + "_trigger",
					"layout", layout.getCronExpression());
			JobDetail jobDetail = new JobDetail(layout.getName() + "_job",
					"layout", LayoutJob.class);
			jobDetail.getJobDataMap().put("layout", layout);
			scheduler.scheduleJob(jobDetail, trigger);
		}

		player.startDeployment(ctx, settings, contents);
		deploymentStart = new Date();
		lastDeploymentID = id;
	}

	/**
	 * Helper method to instruct the player to start a particular layout.
	 * 
	 * @param layout
	 *            The deployment layout to start "playing".
	 */
	private void startLayout(DeploymentLayout layout) {
		player.startLayout(layout);
	}

	/**
	 * Helper method to check if another deployment is available, when to
	 * schedule it, and when appropriate instructs the player to switch to the
	 * new deployment.
	 */
	private void monitorDeployments() {
		Connection c = null;
		try {
			c = DBUtils.getConnection();

			// Query to find the latest valid complete deployment. By valid,
			// we mean that the deployment date is not set in the future.
			PreparedStatement ps = c
					.prepareStatement("SELECT ID, FILENAME FROM DEPLOYMENT "
							+ "WHERE FILENAME != '' AND FAILED_RESOURCE = 'N' "
							+ "AND DEPLOY_DATE <= ? ORDER BY DEPLOY_DATE DESC");
			ps.setTimestamp(1, new Timestamp(new Date().getTime()));
			ResultSet rs = ps.executeQuery();

			long potentialDeploymentId = -1;
			String potentialDeploymentPath = "";
			if (rs.next()) {
				// The first row is the latest deployment (potentially)
				potentialDeploymentId = rs.getLong(1);
				potentialDeploymentPath = rs.getString(2);
			}

			rs.close();
			ps.close();

			if (potentialDeploymentId != -1
					&& lastDeploymentID != potentialDeploymentId) {
				ps = c.prepareStatement("SELECT COUNT(*) FROM PENDING "
						+ "WHERE PENDING.DEPLOYMENT_ID=?");
				ps.setLong(1, potentialDeploymentId);
				rs = ps.executeQuery();

				if (!rs.next()) {
					throw new Exception(
							"Count query returned empty result set.");
				}

				if (rs.getInt(1) == 0) {
					// Deployment is complete. No pending resources exist for
					// it.
					if (logger.isInfoEnabled()) {
						logger.info("Startind deployment with ID: "
							+ potentialDeploymentId + ", from path: "
							+ potentialDeploymentPath);
					}
					try {
						startDeployment(potentialDeploymentId,
								potentialDeploymentPath);
					} catch (Exception e) {
						logger.error("Failed to start deployment: "
								+ potentialDeploymentPath, e);
					}
				}

				rs.close();
				ps.close();
			}

			c.commit();
		} catch (Exception e) {
			logger.error("Unable to monitor deployments", e);
			if (c != null)
				try {
					c.rollback();
				} catch (SQLException sqle) {
				}
		} finally {
			if (c != null)
				try {
					c.close();
				} catch (SQLException e) {
					// do nothing
				}
		}
	}

	/**
	 * Helper method to check if a particular layout has expired. This method is
	 * only interested in deployment layouts that have a duration specified. If
	 * the current layout has a duration specified and has been "playing" for
	 * more than the duration time, then this method instruct the player thread
	 * to stop the current layout.
	 */
	private void checkLayoutExpiry() {
		DeploymentLayout currentLayout = getCurrentLayout();
		if (currentLayout == null) {
			return;
		}

		// The layout did not specify a duration. Skip test.
		if (currentLayout.getDuration() == 0) {
			return;
		}

		Date now = new Date();
		Date expiry = new Date(layoutStart.getTime()
				+ currentLayout.getDuration() * 1000L);
		if (now.after(expiry)) {
			// The layout duration has expired.
			player.completeLayout();
		}
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (!previewMode) {
					monitorDeployments();
				}

				checkLayoutExpiry();

				try {
					Thread.sleep(1000);
				} catch (InterruptedException ie) {
				}
			}
		} catch (Throwable t) {
			logger.error("Error during the monitor thread main loop", t);
		}
	}

}
