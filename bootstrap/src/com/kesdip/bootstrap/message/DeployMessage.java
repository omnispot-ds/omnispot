/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.message;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

import com.kesdip.bootstrap.content.ContentRetriever;
import com.kesdip.bootstrap.content.DescriptorHandler;
import com.kesdip.common.util.DBUtils;

/**
 * Encapsulates the handling of a deployment message from the server.
 * 
 * @author Pafsanias Ftakas
 */
public class DeployMessage extends Message {
	private static final Logger logger = Logger.getLogger(DeployMessage.class);

	private String descriptorUrl;
	private long crc;
	private String actionId = null;

	public DeployMessage(String descriptorUrl, long crc, String actionId) {
		this.descriptorUrl = descriptorUrl;
		this.crc = crc;
		this.actionId = actionId;
	}

	public String getDescriptorUrl() {
		return descriptorUrl;
	}

	public long getCRC() {
		return crc;
	}

	public void process() throws Exception {
		logger.info("Starting processing of deployment message for: "
				+ descriptorUrl);

		Connection c = null;
		try {
			c = DBUtils.getConnection();

			long id = 1;
			boolean update = false;

			PreparedStatement ps = c
					.prepareStatement("SELECT ID FROM DEPLOYMENT WHERE URL=?");
			ps.setString(1, descriptorUrl);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				// A row already exists with the descriptor URL. We should
				// update.
				update = true;
				id = rs.getLong(1);
			}

			rs.close();
			ps.close();

			if (!update) {
				ps = c
						.prepareStatement("INSERT INTO DEPLOYMENT "
								+ "(URL, FILENAME, CRC, DEPLOY_DATE, FAILED_RESOURCE, RETRIES) "
								+ "VALUES (?,?,?,?,?,?)");
				ps.setString(1, descriptorUrl);
				ps.setString(2, "");
				ps.setString(3, Long.toString(crc));
				ps.setTimestamp(4, new Timestamp(new Date().getTime()));
				ps.setString(5, "N");
				ps.setInt(6, 0);
			} else {
				ps = c
						.prepareStatement("UPDATE DEPLOYMENT "
								+ "SET FILENAME=?, CRC=?, DEPLOY_DATE=?, FAILED_RESOURCE=?, "
								+ "RETRIES=?  WHERE ID=?");
				ps.setString(1, "");
				ps.setString(2, Long.toString(crc));
				ps.setTimestamp(3, new Timestamp(new Date().getTime()));
				ps.setString(4, "N");
				ps.setLong(5, id);
				ps.setInt(6, 0);
			}

			ps.executeUpdate();
			ps.close();

			ContentRetriever.getSingleton().addTask(
					new DescriptorHandler(descriptorUrl, crc));

			c.commit();
		} catch (Exception e) {
			if (c != null) {
				try {
					c.rollback();
				} catch (SQLException sqle) {
					// do nothing
				}
			}
			throw e;
		} finally {
			if (c != null) {
				try {
					c.close();
				} catch (SQLException e) {
					// do nothing
				}
			}
		}
	}

	public String toMessageString() {
		return "[Deploy:" + descriptorUrl + "]";
	}

	@Override
	public String getActionId() {
		return actionId;
	}

	/**
	 * Signals that this type of message is not handled in the message pump
	 * thread.
	 * 
	 * @return always <code>false</code>
	 * @see com.kesdip.bootstrap.message.Message#isOKHandledInPumpThread()
	 */
	@Override
	protected boolean isOKHandledInPumpThread() {
		return false;
	}

}
