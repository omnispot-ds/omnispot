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
public class DeployMessage implements Message {
	private static final Logger logger = Logger.getLogger(DeployMessage.class);
	
	private String descriptorUrl;
	
	public DeployMessage(String descriptorUrl) {
		this.descriptorUrl = descriptorUrl;
	}
	
	public String getDescriptorUrl() {
		return descriptorUrl;
	}

	@Override
	public void process() throws Exception {
		logger.info("Starting processing of deployment message for: " +
				descriptorUrl);
		
		Connection c = null;
		try {
			c = DBUtils.getConnection();
			
			long id = 1;
			boolean update = false;
			
			PreparedStatement ps = c.prepareStatement(
					"SELECT ID FROM DEPLOYMENT WHERE URL=?");
			ps.setString(1, descriptorUrl);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				// A row already exists with the descriptor URL. We should update.
				update = true;
				id = rs.getLong(1);
			}
			
			rs.close();
			ps.close();
			
			if (!update) {
				ps = c.prepareStatement("INSERT INTO DEPLOYMENT " +
						"(URL, FILENAME, DEPLOY_DATE, FAILED_RESOURCE, RETRIES) " +
						"VALUES (?,?,?,?,?)");
				ps.setString(1, descriptorUrl);
				ps.setString(2, "");
				ps.setTimestamp(3, new Timestamp(new Date().getTime()));
				ps.setString(4, "N");
				ps.setInt(5, 0);
			} else {
				ps = c.prepareStatement("UPDATE DEPLOYMENT " +
						"SET FILENAME=?, DEPLOY_DATE=?, FAILED_RESOURCE=?, " +
						"RETRIES=?  WHERE ID=?");
				ps.setString(1, "");
				ps.setTimestamp(2, new Timestamp(new Date().getTime()));
				ps.setString(3, "N");
				ps.setLong(4, id);
				ps.setInt(5, 0);
			}
			
			ps.executeUpdate();
			ps.close();
			
			ContentRetriever.getSingleton().addTask(
					new DescriptorHandler(descriptorUrl));
			
			c.commit();
		} catch (Exception e) {
			if (c != null) try { c.rollback(); } catch (SQLException sqle) { }
			throw e;
		} finally {
			if (c != null) try { c.close(); } catch (SQLException e) { }
		}
	}

	@Override
	public String toMessageString() {
		return "[Deploy:" + descriptorUrl + "]";
	}
}
