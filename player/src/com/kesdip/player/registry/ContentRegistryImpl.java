/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.registry;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.kesdip.common.util.DBUtils;
import com.kesdip.player.components.Resource;

/**
 * This is the content registry implementation that talks with the Derby DB
 * to get the latest information about resources downloaded from the server.
 * 
 * @author Pafsanias Ftakas
 */
public class ContentRegistryImpl extends ContentRegistry {
	private static final Logger logger =
		Logger.getLogger(ContentRegistryImpl.class);
	
	@Override
	public InputStream getResourceAsStream(Resource resource) {
		try {
			return new FileInputStream(getResourcePath(resource));
		} catch (FileNotFoundException e) {
			logger.error("Unable to create stream resource around " + resource, e);
		}
		
		return null;
	}

	@Override
	public String getResourcePath(Resource resource) {
		Connection c = null;
		try {
			c = DBUtils.getConnection();
			
			String retVal = null;
			
			PreparedStatement ps = c.prepareStatement("SELECT FILENAME FROM RESOURCE " +
					"WHERE URL=? AND CRC=? AND FILENAME != ''");
			ps.setString(1, resource.getIdentifier());
			ps.setString(2, resource.getChecksum());
			ResultSet rs = ps.executeQuery();
			
			if (rs.next())
				retVal = rs.getString(1);
			
			rs.close();
			ps.close();
			
			c.commit();
			
			return retVal;
		} catch (Exception e) {
			logger.warn("Unable to query resource path: " + e.getMessage());
			if (c != null) try { c.rollback(); } catch (SQLException sqle) { }
		} finally {
			if (c != null) try { c.close(); } catch (SQLException e) { }
		}
		
		return null;
	}

	@Override
	public boolean hasResource(Resource resource) {
		return getResourcePath(resource) != null;
	}

}
