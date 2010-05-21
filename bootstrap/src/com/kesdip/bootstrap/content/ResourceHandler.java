/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.content;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.net.URLConnection;
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
public class ResourceHandler extends ContentHandler implements StreamCopyListener {
	/**
	 * The logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ResourceHandler.class);

	private String resourceUrl;
	private String crc;
	private long deployment_id;
	private long resource_id;
	private long startByteIndex;

	public ResourceHandler(String resourceUrl, String crc, long deployment_id,
			long resource_id, long startByteIndex) {
		super(resourceUrl);
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

	protected void contentHandlingLogic() {
		InputStream is = null;
		RandomAccessFile os = null;
		try {
			logger.info("Starting download of resource: " + resourceUrl);

			// Download the resource.
			URL resource = new URL(StringUtils.encodeFileName(resourceUrl));
			File resourceDir = new File(Config.getSingleton().getResourcePath());
			if (!resourceDir.isDirectory()) {
				resourceDir.mkdirs();
			}
			File newResource = getResourceFileName(resourceDir);
			os = new RandomAccessFile(newResource, "rw");
			URLConnection connection = resource.openConnection();
			if (startByteIndex > 0) {
				connection.setRequestProperty("Range", "byte=" + startByteIndex
						+ "-");
				os.seek(startByteIndex);
			}
			is = connection.getInputStream();

			StreamUtils.copyStream(is, os, this);

			// Check the CRC
			// compute it a-posteriori as this may be a resumed download
			CRC32 resourceCRC = FileUtils.getCrc(newResource);
			long v = Long.parseLong(crc);
			if (v != resourceCRC.getValue()) {
				throw new Exception("Resource '"
						+ newResource.getAbsolutePath() + "'. Expected CRC ("
						+ resourceCRC.getValue() + "), actual CRC (" + v + ").");
			}

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
				// TODO Now that filename is saved immediately, should we remove
				// this?
				if (updateResource) {
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
				StreamUtils.close(is);
				is = null;
				StreamUtils.close(os);
				os = null;
			}
		} catch (Throwable t) {
			logger.error("Throwable while retrieving resource: " + resourceUrl,
					t);
		}

		logger.info("Completed task for: " + toMessageString());
	}

	/**
	 * Returns the name of the file for the current resource. The file is either
	 * new (a new download) or an existing one (a resumed download).
	 * 
	 * @param resourceDir
	 *            the parent resource directory
	 * @return File the filename for the resource
	 * @throws IOException
	 *             on error creating the new file
	 */
	private final File getResourceFileName(File resourceDir) throws IOException {
		File resource = null;
		// first try in the DB to see if it is a resumed download
		Connection c = null;
		try {
			c = DBUtils.getConnection();
			PreparedStatement ps = c
					.prepareStatement("SELECT RESOURCE.FILENAME "
							+ "FROM RESOURCE, PENDING "
							+ "WHERE RESOURCE.ID=PENDING.RESOURCE_ID "
							+ "AND RESOURCE.ID=? AND PENDING.DEPLOYMENT_ID=?");
			ps.setLong(1, resource_id);
			ps.setLong(2, deployment_id);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String filename = rs.getString(1);
				if (!StringUtils.isEmpty(filename)) {
					resource = new File(filename);
				}
			}
			rs.close();
			ps.close();
		} catch (Exception e) {
			logger.error("Error querying resource file name from the DB", e);
		} finally {
			try {
				c.close();
			} catch (Exception e) {
				// only logging
				logger.error("Error closing connection", e);
			}
		}
		if (resource != null) {
			return resource;
		}
		// not in the DB, so create a new
		UUID newResourceUUID = UUID.randomUUID();
		int counter = 0;
		do {
			resource = new File(resourceDir, "resource_" + newResourceUUID
					+ "_" + counter);
			counter++;
		} while (!resource.createNewFile());
		// save filename in DB
		try {
			c = DBUtils.getConnection();
			PreparedStatement ps = c.prepareStatement("UPDATE RESOURCE "
					+ "SET FILENAME=? WHERE ID=?");
			ps.setString(1, resource.getPath());
			ps.setLong(2, resource_id);
			int modifiedRows = ps.executeUpdate();
			if (modifiedRows != 1) {
				throw new Exception("Updating resource " + resource_id
						+ " with the " + "filename, touched " + modifiedRows
						+ " rows.");
			}
			ps.close();
			c.commit();
		} catch (Exception e) {
			logger.error("Error updating new resource file name in the DB", e);
		} finally {
			try {
				c.close();
			} catch (Exception e) {
				// only logging
				logger.error("Error closing connection", e);
			}
		}
		return resource;
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
							+ "WHERE PENDING.DEPLOYMENT_ID=? " + ")");
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
	 * Updates the downloaded bytes column with the value of the size.
	 * <p>
	 * The method updates the entry only if size &gt; 0.
	 * </p>
	 * 
	 * @see com.kesdip.common.util.StreamCopyListener#copyCompleted()
	 */
	@Override
	public void copyCompleted() {
		Connection c = null;
		try {
			c = DBUtils.getConnection();
			PreparedStatement ps = c.prepareStatement("UPDATE RESOURCE "
					+ "SET DOWNLOADED_BYTES=SIZE, RESOURCE.LAST_UPDATE=? "
					+ "WHERE RESOURCE.ID=? AND SIZE>0");
			ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			ps.setLong(2, resource_id);
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
	 * @see com.kesdip.common.util.StreamCopyListener#copyFailed()
	 */
	@Override
	public void copyFailed() {
		// do nothing for now
	}

	/**
	 * @return int always 30
	 * @see com.kesdip.common.util.StreamCopyListener#getByteBufferCount()
	 */
	@Override
	public int getByteBufferCount() {
		return 30;
	}

	/**
	 * @return the resourceUrl
	 */
	public String getResourceUrl() {
		return resourceUrl;
	}
}
