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
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.UUID;
import java.util.zip.CRC32;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.kesdip.bootstrap.Config;
import com.kesdip.common.util.DBUtils;
import com.kesdip.common.util.StreamUtils;
import com.kesdip.common.util.StringUtils;
import com.kesdip.player.DeploymentContents;
import com.kesdip.player.DeploymentLayout;
import com.kesdip.player.DeploymentSettings;
import com.kesdip.player.components.Resource;
import com.kesdip.player.components.RootContainer;

/**
 * A handler for retrieving descriptors from the server.
 * 
 * @author Pafsanias Ftakas
 */
public class DescriptorHandler extends ContentHandler {
	private static final Logger logger = Logger
			.getLogger(DescriptorHandler.class);

	private String descriptorUrl;
	private long crc;

	public DescriptorHandler(String descriptorUrl, long crc) {
		super(descriptorUrl);
		this.descriptorUrl = descriptorUrl;
		this.crc = crc;
	}

	public String toMessageString() {
		return "[DescriptorHandler:" + descriptorUrl + ", " + crc + "]";
	}

	protected void contentHandlingLogic() {
		
		FileOutputStream os = null;
		InputStream is = null;
		try {
			if (logger.isInfoEnabled()) {
				logger.info("Starting download of deployment descriptor: "
						+ descriptorUrl);
			}

			// Download the deployment descriptor.
			URL descriptor = new URL(StringUtils.encodeFileName(descriptorUrl));
			File deploymentDir = new File(Config.getSingleton()
					.getDeploymentPath());
			if (!deploymentDir.isDirectory()) {
				deploymentDir.mkdirs();
			}
			int counter = 0;
			File newDeployment;
			UUID newDeploymentUUID = UUID.randomUUID();
			do {
				newDeployment = new File(deploymentDir, "appContext_"
						+ newDeploymentUUID + "_" + counter + ".xml");
				counter++;
			} while (!newDeployment.createNewFile());
			os = new FileOutputStream(newDeployment);
			is = descriptor.openStream();
			CRC32 resourceCRC = new CRC32();
			StreamUtils.copyStream(is, os, resourceCRC);

			// Check the CRC
			if (crc != resourceCRC.getValue()) {
				throw new Exception("Downloaded resource CRC ("
						+ resourceCRC.getValue() + ") does not match resource "
						+ "specified CRC (" + crc + ").");
			}
			// Load the application context to gather all the resources.
			ApplicationContext ctx = new FileSystemXmlApplicationContext(
					newDeployment.getPath());
			DeploymentSettings settings = (DeploymentSettings) ctx
					.getBean("deploymentSettings");
			DeploymentContents contents = (DeploymentContents) ctx
					.getBean("deploymentContents");
			HashSet<Resource> resourceSet = new HashSet<Resource>();
			for (DeploymentLayout layout : contents.getLayouts()) {
				for (RootContainer root : layout.getContentRoots()) {
					resourceSet.addAll(root.gatherResources());

					for (Resource resource : root.gatherResources()) {
						if (logger.isInfoEnabled()) {
							logger.info("Found resource: "
									+ resource.getIdentifier());
						}
					}
				}
			}

			Connection c = null;
			try {
				c = DBUtils.getConnection();

				PreparedStatement ps = c
						.prepareStatement("SELECT ID, FILENAME FROM DEPLOYMENT WHERE URL=?");
				ps.setString(1, descriptorUrl);
				ResultSet rs = ps.executeQuery();

				if (!rs.next()) {
					throw new Exception("Unable to find row for descriptor: "
							+ descriptorUrl);
				}
				long id = rs.getLong(1);
				boolean descriptorDownloaded = rs.getString(2).length() != 0;

				rs.close();
				ps.close();

				if (!descriptorDownloaded) {
					ps = c
							.prepareStatement("UPDATE DEPLOYMENT SET FILENAME=?, DEPLOY_DATE=? WHERE ID=?");
					ps.setString(1, newDeployment.getPath());
					ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
					ps.setLong(3, id);

					ps.executeUpdate();
					ps.close();

					for (Resource resource : resourceSet) {
						ps = c
								.prepareStatement("SELECT ID FROM RESOURCE "
										+ "WHERE URL=? AND CRC=? AND SIZE=DOWNLOADED_BYTES");
						ps.setString(1, resource.getIdentifier());
						// Bug-128: Use the proper part of the composite checksum 
						ps.setString(2, StringUtils.extractCrc(resource
								.getChecksum()));
						rs = ps.executeQuery();

						boolean resourceExists = false;
						long resourceId = 1;

						if (rs.next()) {
							resourceExists = true;
							resourceId = rs.getLong(1);
						}

						rs.close();
						ps.close();

						if (resourceExists) {
							if (logger.isInfoEnabled()) {
								logger.info("Resource "
										+ resource.getIdentifier()
										+ " with CRC " + resource.getChecksum()
										+ " already exists.");
							}
						} else {
							String checksum = StringUtils.extractCrc(resource
									.getChecksum());
							String size = StringUtils.extractSize(resource
									.getChecksum());

							ps = c
									.prepareStatement(
											"INSERT INTO RESOURCE "
													+ "(URL, CRC, FILENAME, RETRIES, DOWNLOADED_BYTES, LAST_UPDATE, SIZE) "
													+ "VALUES (?,?,?,?,?,?,?)",
											Statement.RETURN_GENERATED_KEYS);
							ps.setString(1, resource.getIdentifier());
							ps.setString(2, checksum);
							ps.setString(3, "");
							ps.setInt(4, 0);
							ps.setLong(5, 0);
							ps.setTimestamp(6, new Timestamp(0));
							ps
									.setLong(7, size != null ? Long
											.valueOf(size) : 0);
							ps.executeUpdate();
							rs = ps.getGeneratedKeys();
							if (!rs.next()) {
								throw new Exception(
										"Row insertion did not generate any keys");
							}
							resourceId = rs.getLong(1);
							rs.close();
							ps.close();

							ps = c
									.prepareStatement("INSERT INTO PENDING VALUES (?,?)");
							ps.setLong(1, id);
							ps.setLong(2, resourceId);
							ps.executeUpdate();
							ps.close();
							ContentRetriever.getSingleton().addTask(
									new ResourceHandler(resource
											.getIdentifier(), checksum, id,
											resourceId, 0));
						}
					}
				}

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
				StreamUtils.close(is);
				is = null;
				StreamUtils.close(os);
				os = null;
			}
		} catch (Throwable t) {
			logger.error("Throwable while retrieving descriptor: "
					+ descriptorUrl, t);
		}
		if (logger.isInfoEnabled()) {
			logger.info("Completed task for: " + toMessageString());
		}
	}

}
