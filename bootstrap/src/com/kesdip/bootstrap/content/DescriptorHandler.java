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
import java.sql.Timestamp;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.kesdip.bootstrap.Config;
import com.kesdip.common.util.StreamUtils;
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
public class DescriptorHandler implements ContentHandler {
	private static final Logger logger =
		Logger.getLogger(DescriptorHandler.class);
	
	private String descriptorUrl;
	
	public DescriptorHandler(String descriptorUrl) {
		this.descriptorUrl = descriptorUrl;
	}

	@Override
	public String toMessageString() {
		return "[DescriptorHandler:" + descriptorUrl + "]";
	}

	@Override
	public void run() {
		try {
			logger.info("Starting download of deployment descriptor: " + descriptorUrl);
			
			URL descriptor = new URL(descriptorUrl);
			File deploymentDir = new File(Config.getSingleton().getDeploymentPath());
			int counter = 0;
			File newDeployment = new File(deploymentDir, "appContext" + counter + ".xml");
			while (!newDeployment.createNewFile()) {
				counter++;
				newDeployment = new File(deploymentDir, "appContext" + counter + ".xml");
			}
			FileOutputStream os = new FileOutputStream(newDeployment);
			InputStream is = descriptor.openStream();
			StreamUtils.copyStream(is, os);
			os.close();
			is.close();
			
			ApplicationContext ctx = new FileSystemXmlApplicationContext(
					newDeployment.getPath());
			DeploymentSettings settings =
				(DeploymentSettings) ctx.getBean("deploymentSettings");
			DeploymentContents contents =
				(DeploymentContents) ctx.getBean("deploymentContents");
			HashSet<Resource> resourceSet = new HashSet<Resource>();
			for (DeploymentLayout layout : contents.getLayouts()) {
				for (RootContainer root : layout.getContentRoots()) {
					resourceSet.addAll(root.gatherResources());
					
					for (Resource resource : root.gatherResources()) {
						logger.info("Found resource: " + resource.getIdentifier());
					}
				}
			}
			
			Connection c = null;
			try {
				c = Config.getSingleton().getConnection();
				
				PreparedStatement ps = c.prepareStatement(
						"SELECT ID FROM DEPLOYMENT WHERE URL=?");
				ps.setString(1, descriptorUrl);
				ResultSet rs = ps.executeQuery();
				
				if (!rs.next()) {
					throw new Exception("Unable to find row for descriptor: " +
							descriptorUrl);
				}
				long id = rs.getLong(1);
				
				rs.close();
				ps.close();
				
				ps = c.prepareStatement(
						"UPDATE DEPLOYMENT SET FILENAME=?, DEPLOY_DATE=? WHERE ID=?");
				ps.setString(1, newDeployment.getPath());
				ps.setTimestamp(2, new Timestamp(settings.getStartTime().getTime()));
				ps.setLong(3, id);
				
				ps.executeUpdate();
				ps.close();
				
				for (Resource resource : resourceSet) {
					// TODO: Handle resource addition to the tables and starting
					// a resource handler to download it from the server.
					ContentRetriever.getSingleton().addTask(
							new ResourceHandler(resource.getIdentifier()));
				}
				
				c.commit();
			} catch (Exception e) {
				if (c != null) try { c.rollback(); } catch (SQLException sqle) { }
				throw e;
			} finally {
				if (c != null) try { c.close(); } catch (SQLException e) { }
			}
		} catch (Throwable t) {
			logger.error("Throwable while retrieving descriptor: " + descriptorUrl, t);
		}
	}

}
