/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.content;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.zip.CRC32;

import org.apache.log4j.Logger;

import com.kesdip.bootstrap.Config;
import com.kesdip.common.util.DBUtils;
import com.kesdip.common.util.StreamUtils;

/**
 * A handler for retrieving resources from the server.
 * 
 * @author Pafsanias Ftakas
 */
public class ResourceHandler implements ContentHandler {
	private static final Logger logger =
		Logger.getLogger(ResourceHandler.class);
	
	private String resourceUrl;
	private String crc;
	private long deployment_id;
	private long resource_id;
	
	public ResourceHandler(String resourceUrl, String crc, long deployment_id,
			long resource_id) {
		this.resourceUrl = resourceUrl;
		this.crc = crc;
		this.deployment_id = deployment_id;
		this.resource_id = resource_id;
	}
	
	public long getDeploymentId() {
		return deployment_id;
	}
	
	public long getResourceId() {
		return resource_id;
	}

	public String toMessageString() {
		return "[ResourceHandler:" + resourceUrl + "," + crc + "," +
			deployment_id + "," + resource_id + "]";
	}

	public void run() {
		try {
			logger.info("Starting download of resource: " + resourceUrl);
			
			// Download the resource.
			URL resource = new URL(resourceUrl);
			File resourceDir = new File(Config.getSingleton().getResourcePath());
			int counter = 0;
			File newResource;
			UUID newResourceUUID = UUID.randomUUID();
			do {
				newResource = new File(resourceDir, "resource_" +
						newResourceUUID + "_" + counter);
				counter++;
			} while (!newResource.createNewFile());
			FileOutputStream os = new FileOutputStream(newResource);
			InputStream is = resource.openStream();
			CRC32 resourceCRC = new CRC32();
			StreamUtils.copyStream(is, os, resourceCRC);
			os.close();
			is.close();
			
			// Check the CRC
			long v = Long.parseLong(crc);
			if (v != resourceCRC.getValue())
				throw new Exception("Downloaded resource CRC (" +
						resourceCRC.getValue() + ") does not match resource " +
								"specified CRC (" + v + ").");

			Connection c = null;
			try {
				c = DBUtils.getConnection();
				
				PreparedStatement ps = c.prepareStatement(
						"SELECT * FROM PENDING WHERE DEPLOYMENT_ID=? AND RESOURCE_ID=?");
				ps.setLong(1, deployment_id);
				ps.setLong(2, resource_id);
				ResultSet rs = ps.executeQuery();
				
				boolean updateResource = true;
				
				if (!rs.next()) {
					logger.info("Some other thread downloaded the resource " +
							"before us. No need to do anything else.");
					updateResource = false;
				}
				
				rs.close();
				ps.close();
				
				if (updateResource) {
					ps = c.prepareStatement("UPDATE RESOURCE " +
							"SET FILENAME=? WHERE ID=?");
					ps.setString(1, newResource.getPath());
					ps.setLong(2, resource_id);
					int modifiedRows = ps.executeUpdate();
					if (modifiedRows != 1)
						throw new Exception("Updating the resource with the " +
								"filename, touched " + modifiedRows + " rows.");
					ps.close();

					ps = c.prepareStatement("DELETE FROM PENDING " +
							"WHERE DEPLOYMENT_ID=? AND RESOURCE_ID=?");
					ps.setLong(1, deployment_id);
					ps.setLong(2, resource_id);
					ps.executeUpdate();
					ps.close();
				}
				
				c.commit();
			} catch (Exception e) {
				if (c != null) try { c.rollback(); } catch (SQLException sqle) { }
				throw e;
			} finally {
				if (c != null) try { c.close(); } catch (SQLException e) { }
			}
		} catch (Throwable t) {
			logger.error("Throwable while retrieving resource: " + resourceUrl, t);
		}
		
		logger.info("Completed task for: " + toMessageString());
	}

}
