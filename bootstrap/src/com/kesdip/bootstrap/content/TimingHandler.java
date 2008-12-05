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

import org.apache.log4j.Logger;

import com.kesdip.bootstrap.Config;
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

	@Override
	public String toMessageString() {
		return "[TimingHandler]";
	}

	@Override
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
				
				PreparedStatement ps = c.prepareStatement(
						"SELECT RESOURCE.URL, RESOURCE.CRC, " +
						"PENDING.DEPLOYMENT_ID, PENDING.RESOURCE_ID " +
						"FROM RESOURCE, PENDING " +
						"WHERE RESOURCE.ID=PENDING.RESOURCE_ID");
				ResultSet rs = ps.executeQuery();
				
				while (rs.next()) {
					String url = rs.getString(1);
					String crc = rs.getString(2);
					long id = rs.getLong(3);
					long resourceId = rs.getLong(4);
					ContentRetriever.getSingleton().addTask(
							new ResourceHandler(url, crc, id, resourceId));
				}
				
				rs.close();
				ps.close();
				
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

}
