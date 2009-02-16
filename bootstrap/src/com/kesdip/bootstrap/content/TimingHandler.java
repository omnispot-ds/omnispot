/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.content;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.kesdip.bootstrap.Config;
import com.kesdip.business.constenum.IActionStatusEnum;
import com.kesdip.business.constenum.IActionTypesEnum;
import com.kesdip.common.util.DBUtils;

/**
 * Just in case something goes wrong while trying to download resources from
 * the server, we need a means of re-trying after a certain amount of time.
 * This handler allows us to do just that. Its run method sleeps for an amount
 * of time and then wakes up, looks at the PENDING table, and inserts a resource
 * handler for each pending download. Resource download can gracefully handle
 * the case where some other thread has already downloaded the same resource.
 * Finally this handler adds another instance of itself, so that the whole
 * process can repeat itself after the sleep period has expired.
 * 
 * @author Pafsanias Ftakas
 */
public class TimingHandler implements ContentHandler {
	private static final Logger logger =
		Logger.getLogger(TimingHandler.class);

	public String toMessageString() {
		return "[TimingHandler]";
	}

	public void run() {
		try {
			try {
				String sleepPeriod =
					Config.getSingleton().getTimingHandlerSleepPeriod();
				long sleepMillis = Long.parseLong(sleepPeriod);
				Thread.sleep(sleepMillis);
			} catch (InterruptedException ie) {
				// Intentionally empty
			}
			
			logger.info("Starting timing handler");
			
			Connection c = null;
			try {
				c = DBUtils.getConnection();
				
				actionsUpdate(c);
				
				Map<ResourceHandler, Integer> pendingMap =
					new HashMap<ResourceHandler, Integer>();
				
				PreparedStatement ps = c.prepareStatement(
						"SELECT ID, URL, CRC, RETRIES FROM DEPLOYMENT " +
						"WHERE FILENAME='' AND FAILED_RESOURCE='N'");
				ResultSet rs = ps.executeQuery();
				
				Set<Long> failedDeployments = new HashSet<Long>();
				Map<Long, String> retryDeployments = new HashMap<Long, String>();
				Map<Long, String> retryCRC = new HashMap<Long, String>();
				Map<Long, Integer> retryCounts = new HashMap<Long, Integer>();
				
				while (rs.next()) {
					long id = rs.getLong(1);
					String url = rs.getString(2);
					String crc = rs.getString(3);
					int retries = rs.getInt(4);
					
					if (retries >= Config.getSingleton().getResourceRetryLimit()) {
						failedDeployments.add(id);
					} else {
						retryDeployments.put(id, url);
						retryCRC.put(id, crc);
						retryCounts.put(id, retries);
					}
				}
				
				rs.close();
				ps.close();
				
				for (Long id : failedDeployments) {
					logger.error("Retry limit has been reached for " +
							"deployment with ID: " + id +
							". Giving up.");
					
					ps = c.prepareStatement("UPDATE DEPLOYMENT " +
						"SET FAILED_RESOURCE='Y' WHERE ID=?");
					ps.setLong(1, id);
					ps.executeUpdate();
					ps.close();
				}
				
				for (Long id : retryDeployments.keySet()) {
					String url = retryDeployments.get(id);
					String crc = retryCRC.get(id);
					int retries = retryCounts.get(id);
					
					ps = c.prepareStatement("UPDATE DEPLOYMENT " +
							"SET RETRIES=? WHERE ID=?");
					ps.setInt(1, retries + 1);
					ps.setLong(2, id);
					int modifiedRows = ps.executeUpdate();
					if (modifiedRows != 1)
						throw new Exception("Trying to update the number " +
								"of retries in the deployment table touched " +
								modifiedRows + " rows.");
					ps.close();
					
					logger.info("Retry #" + (retries + 1) +
							" for deployment with ID: " + id + ".");
					
					ContentRetriever.getSingleton().addTask(
							new DescriptorHandler(url, Long.parseLong(crc)));
				}
				
				ps = c.prepareStatement(
						"SELECT RESOURCE.URL, RESOURCE.CRC, RESOURCE.RETRIES, " +
						"PENDING.DEPLOYMENT_ID, PENDING.RESOURCE_ID " +
						"FROM RESOURCE, PENDING " +
						"WHERE RESOURCE.ID=PENDING.RESOURCE_ID");
				rs = ps.executeQuery();
				
				while (rs.next()) {
					String url = rs.getString(1);
					String crc = rs.getString(2);
					int retries = rs.getInt(3);
					long id = rs.getLong(4);
					long resourceId = rs.getLong(5);
					pendingMap.put(new ResourceHandler(url, crc, id, resourceId),
							retries);
				}
				
				rs.close();
				ps.close();
				
				for (ResourceHandler handler : pendingMap.keySet()) {
					int retries = pendingMap.get(handler);
					
					if (retries >= Config.getSingleton().getResourceRetryLimit()) {
						logger.info("Retry limit has been reached for resource " +
								"with ID: " + handler.getResourceId() +
								". Giving up.");
						
						ps = c.prepareStatement("DELETE FROM PENDING " +
								"WHERE DEPLOYMENT_ID=? AND RESOURCE_ID=?");
						ps.setLong(1, handler.getDeploymentId());
						ps.setLong(2, handler.getResourceId());
						ps.executeUpdate();
						ps.close();
						
						ps = c.prepareStatement("UPDATE DEPLOYMENT " +
								"SET FAILED_RESOURCE='Y' WHERE ID=?");
						ps.setLong(1, handler.getDeploymentId());
						ps.executeUpdate();
						ps.close();
					} else {
						ps = c.prepareStatement("UPDATE RESOURCE " +
								"SET RETRIES=? WHERE ID=?");
						ps.setInt(1, retries + 1);
						ps.setLong(2, handler.getResourceId());
						int modifiedRows = ps.executeUpdate();
						if (modifiedRows != 1)
							throw new Exception("Trying to update the number " +
									"of retries in the resource table touched " +
									modifiedRows + " rows.");
						ps.close();
						
						logger.info("Retry #" + (retries + 1) +
								" for resource with ID: " +
								handler.getResourceId() + ".");
						
						ContentRetriever.getSingleton().addTask(handler);
					}					
				}
				
				c.commit();
			} catch (Exception e) {
				if (c != null) try { c.rollback(); } catch (SQLException sqle) { }
				throw e;
			} finally {
				if (c != null) try { c.close(); } catch (SQLException e) { }
			}
		} catch (Throwable t) {
			logger.error("Throwable while running timing handler", t);
		} finally {
			ContentRetriever.getSingleton().addTask(new TimingHandler());
		}
		
		logger.info("Completed task for: " + toMessageString());
	}

	private void actionsUpdate(Connection c) throws SQLException {
		//look for any pending updates in the action table...
		PreparedStatement ps = c.prepareStatement(
				"SELECT PARAMETER.PARAM_VALUE FROM ACTION,PARAMETER " +
				"WHERE PARAMETER.ACTION_ID = ACTION.ID AND " +
				"PARAMETER.NAME=? AND ACTION.TYPE=? AND ACTION.STATUS=?");
		ps.setString(1, "crc");
		ps.setShort(2, IActionTypesEnum.DEPLOY);
		ps.setShort(3, IActionStatusEnum.IN_PROGRESS);
		
		ResultSet rs = ps.executeQuery();
		List<String> crcs = new ArrayList<String>();
		
		while (rs.next()) {
			crcs.add(rs.getString(1));
		}
		rs.close();
		ps.close();
		
		if (crcs.size() == 0)
			return;
		
		ps = c.prepareStatement(
				"SELECT CRC,FAILED_RESOURCE FROM DEPLOYMENT " +
				"WHERE FILENAME != '' " +
		"AND DEPLOY_DATE <= ? AND CRC=? ORDER BY DEPLOY_DATE DESC");
		PreparedStatement ps2 = c.prepareStatement(
				"update ACTION SET ACTION.STATUS=? " +
				"where action.id in " + 
				"(select parameter.ACTION_ID from PARAMETER where "+ 
						"PARAMETER.NAME=? and PARAMETER.PARAM_VALUE=?" );

		for (String crc:crcs) {
			
			ps.setTimestamp(1, new Timestamp(new Date().getTime()));
			ps.setString(2, crc);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				String crcKey = rs.getString(1);
				String failed = rs.getString(2);
				if (failed.equals("Y"))
					ps2.setShort(1, IActionStatusEnum.FAILED);
				else
					ps2.setShort(1, IActionStatusEnum.OK);
				ps2.setString(2, "crc");
				ps2.setString(3, crcKey);
				ps2.executeUpdate();
			}
		}
		ps2.close();
		ps.close();
		rs.close();
	}
}
