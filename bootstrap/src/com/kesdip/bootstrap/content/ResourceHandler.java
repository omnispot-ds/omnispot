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
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.zip.CRC32;

import org.apache.log4j.Logger;

import com.kesdip.bootstrap.Config;
import com.kesdip.common.util.DBUtils;
import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.StreamCopyListener;
import com.kesdip.common.util.StreamUtils;
import com.kesdip.common.util.StringUtils;

/**
 * A handler for retrieving resources from the server.
 * 
 * @author Pafsanias Ftakas
 */
public class ResourceHandler implements ContentHandler, StreamCopyListener {
	private static final Logger logger = Logger
			.getLogger(ResourceHandler.class);

	private String resourceUrl;
	private String crc;
	private long deployment_id;
	private long resource_id;
	private long startByteIndex;

	public ResourceHandler(String resourceUrl, String crc, long deployment_id,
			long resource_id, long startByteIndex) {
		this.resourceUrl = resourceUrl;
		this.crc = crc;
		this.deployment_id = deployment_id;
		this.resource_id = resource_id;
		this.startByteIndex = startByteIndex;
	}

	public long getDeploymentId() {
		return deployment_id;
	}

	public long getResourceId() {
		return resource_id;
	}

	public String toMessageString() {
		return "[ResourceHandler:" + resourceUrl + "," + crc + ","
				+ deployment_id + "," + resource_id + "]";
	}

	public void run() {
		InputStream is = null;
		FileOutputStream os = null;
		try {
			logger.info("Starting download of resource: " + resourceUrl);

			// Download the resource.
			URL resource = new URL(StringUtils.encodeFileName(resourceUrl));
			File resourceDir = new File(Config.getSingleton().getResourcePath());
			if (!resourceDir.isDirectory()) {
				resourceDir.mkdirs();
			}
			int counter = 0;
			File newResource;
			UUID newResourceUUID = UUID.randomUUID();
			do {
				newResource = new File(resourceDir, "resource_"
						+ newResourceUUID + "_" + counter);
				counter++;
			} while (!newResource.createNewFile());
			os = new FileOutputStream(newResource);
			URLConnection connection = resource.openConnection();
			if (startByteIndex > 0) {
				connection.setRequestProperty("Range", "byte=" + startByteIndex
						+ "-");
				FileChannel fileChannel = os.getChannel();
				fileChannel.position(startByteIndex);
			}
			is = connection.getInputStream();

			StreamUtils.copyStream(is, os, this);

			// Check the CRC
			// compute it a-posteriori as this may be a resumed download
			CRC32 resourceCRC = FileUtils.getCrc(newResource);
			long v = Long.parseLong(crc);
			if (v != resourceCRC.getValue())
				throw new Exception("Downloaded resource CRC ("
						+ resourceCRC.getValue() + ") does not match resource "
						+ "specified CRC (" + v + ").");

			Connection c = null;
			try {
				c = DBUtils.getConnection();

				PreparedStatement ps = c
						.prepareStatement("SELECT * FROM PENDING WHERE DEPLOYMENT_ID=? AND RESOURCE_ID=?");
				ps.setLong(1, deployment_id);
				ps.setLong(2, resource_id);
				ResultSet rs = ps.executeQuery();

				boolean updateResource = true;

				if (!rs.next()) {
					logger.info("Some other thread downloaded the resource "
							+ "before us. No need to do anything else.");
					updateResource = false;
				}

				rs.close();
				ps.close();

				if (updateResource) {
					ps = c.prepareStatement("UPDATE RESOURCE "
							+ "SET FILENAME=? WHERE ID=?");
					ps.setString(1, newResource.getPath());
					ps.setLong(2, resource_id);
					int modifiedRows = ps.executeUpdate();
					if (modifiedRows != 1) {
						throw new Exception("Updating the resource with the "
								+ "filename, touched " + modifiedRows
								+ " rows.");
					}
					ps.close();

					ps = c.prepareStatement("DELETE FROM PENDING "
							+ "WHERE DEPLOYMENT_ID=? AND RESOURCE_ID=?");
					ps.setLong(1, deployment_id);
					ps.setLong(2, resource_id);
					ps.executeUpdate();
					ps.close();
				}

				c.commit();
			} catch (Exception e) {
				try {
					c.rollback();
				} catch (Exception e1) {
					// do nothing
				}
				throw e;
			} finally {
				try {
					c.close();
				} catch (Exception e) {
					// do nothing
				}
				try {
					is.close();
				} catch (Exception e) {
					// do nothing
				}
				try {
					os.close();
				} catch (Exception e) {
					// do nothing
				}
			}
		} catch (Throwable t) {
			logger.error("Throwable while retrieving resource: " + resourceUrl,
					t);
		}

		logger.info("Completed task for: " + toMessageString());
	}

	/**
	 * Update the RESOURCE table with the current downloaded byte count and the
	 * current timestamp.
	 * 
	 * @see com.kesdip.common.util.StreamCopyListener#bufferCopied(int,
	 *      java.util.zip.CRC32)
	 */
	@Override
	public void bufferCopied(int numOfBytes, CRC32 currentCrc) {
		Connection c = null;
		try {
			c = DBUtils.getConnection();
			PreparedStatement ps = c
					.prepareStatement("UPDATE RESOURCE "
							+ "SET RESOURCE.DOWNLOADED_BYTES=?, RESOURCE.LAST_UPDATE=? "
							+ "WHERE RESOURCE.ID=? AND RESOURCE.ID IN ( "
							+ "SELECT PENDING.RESOURCE_ID FROM PENDING "
							+ "WHERE PENDING.DEPLOYMENT_ID=? "
							+ ")");
			ps.setLong(1, numOfBytes);
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setLong(3, resource_id);
			ps.setLong(4, deployment_id);
			ps.executeUpdate();

			ps.close();
			c.commit();
		} catch (Exception e) {
			logger.error("Error updating bytes read", e);
		} finally {
			try {
				c.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	/**
	 * @see com.kesdip.common.util.StreamCopyListener#copyCompleted()
	 */
	@Override
	public void copyCompleted() {
		// do nothing for now
	}

	/**
	 * @see com.kesdip.common.util.StreamCopyListener#copyFailed()
	 */
	@Override
	public void copyFailed() {
		// do nothing for now
	}

	/**
	 * @return int always 50
	 * @see com.kesdip.common.util.StreamCopyListener#getByteBufferCount()
	 */
	@Override
	public int getByteBufferCount() {
		return 50;
	}
}
