/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 10 Δεκ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.bootstrap.message;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.kesdip.common.util.DBUtils;
import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.StringUtils;

/**
 * Reads the last 100 lines of <code>bootstrap.log</code> and sets them as the
 * message of the action in the DB.
 * 
 * @author gerogias
 */
public class FetchLogsMessage extends Message {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger
			.getLogger(FetchLogsMessage.class);

	/**
	 * Relative path to bootstrap.log file.
	 */
	private static final String BOOTSTRAP_LOG = "../logs/bootstrap.log";

	/**
	 * The number of lines to read from the file. Care should be given to the
	 * size of this number as it is directly affected by the limits of the
	 * ACTION.MESSAGE column.
	 * 
	 * @see http://markmail.org/message/6uigrniht6swawoi
	 */
	private static final int LINES = 100;

	private String actionId = null;

	/**
	 * Constructor.
	 * @param actionId
	 */
	public FetchLogsMessage(String actionId) {
		this.actionId = actionId;
	}
	
	/**
	 * @see com.kesdip.bootstrap.message.Message#getActionId()
	 */
	@Override
	public String getActionId() {
		return actionId;
	}

	/**
	 * @see com.kesdip.bootstrap.message.Message#process()
	 */
	@Override
	public void process() throws Exception {
		if (logger.isInfoEnabled()) {
			logger.info("Retrieving last " + LINES + " lines of bootstrap.log");
		}
		File log = new File(BOOTSTRAP_LOG);
		String lines = FileUtils.tail(log, LINES);
		lines = StringUtils.convertToActionMessage(lines);

		// update the action's message
		Connection c = null;
		try {
			c = DBUtils.getConnection();

			PreparedStatement ps = c
					.prepareStatement("UPDATE ACTION SET MESSAGE=? WHERE ACTION_ID=?");
			ps.setString(1, lines);
			ps.setString(2, getActionId());
			ps.executeUpdate();

			ps.close();

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

	/**
	 * @see com.kesdip.bootstrap.message.IMessage#toMessageString()
	 */
	@Override
	public String toMessageString() {
		return "[FetchLogs]";
	}

}
