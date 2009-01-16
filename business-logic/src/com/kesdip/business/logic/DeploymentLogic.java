/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */
package com.kesdip.business.logic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.CRC32;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.beans.ContentDeploymentBean;
import com.kesdip.business.config.ApplicationSettings;
import com.kesdip.business.domain.generated.Customer;
import com.kesdip.business.domain.generated.Deployment;
import com.kesdip.business.domain.generated.Installation;
import com.kesdip.business.domain.generated.InstallationGroup;
import com.kesdip.business.domain.generated.Site;
import com.kesdip.business.exception.ValidationException;
import com.kesdip.common.exception.GenericSystemException;
import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.StreamUtils;

/**
 * Deployment-related logic.
 * 
 * @author sgerogia
 */
public class DeploymentLogic extends BaseLogicAction {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(DeploymentLogic.class);

	/**
	 * Deploys a content file.
	 * <p>
	 * Depending on the selected object (customer, site, group, installation),
	 * deploys to as many {@link Installations} as needed. It copies the file to
	 * the proper folder, ensuring it has a unique file name to avoid clashes.
	 * </p>
	 * 
	 * @param object
	 *            the DTO
	 * @return the created object
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public Deployment deployContent(ContentDeploymentBean object)
			throws ValidationException {

		validate(object, "deployContent");
		File contentFolder = new File(ApplicationSettings.getInstance()
				.getFileStorageSettings().getContentFolder());
		String uniqueName = FileUtils.getUniqueFileName(object.getContentFile()
				.getOriginalFilename());
		File destContent = new File(contentFolder, uniqueName);
		if (logger.isDebugEnabled()) {
			logger.debug("Copying to file " + destContent.getAbsolutePath());
		}
		CRC32 crc = new CRC32();
		InputStream input = null;
		try {
			input = object.getContentFile().getInputStream();
			StreamUtils.copyToFile(input, destContent, crc);
		} catch (IOException e) {
			logger.error("Error getting InputStream", e);
			throw new GenericSystemException("Error getting InputStream", e);
		} finally {
			try {
				input.close();
			} catch (Exception e) {
				// do nothing
			}
		}
		Deployment deployment = null;
		try {
			logger.debug("Creating deployment in the DB");
			String contentBase = ApplicationSettings.getInstance()
					.getServerSettings().getContentBase();
			deployment = new Deployment();
			deployment.setCrc(String.valueOf(crc.getValue()));
			deployment.setLocalFile(destContent.getAbsolutePath());
			deployment.setUrl(contentBase + uniqueName);
			deployment.setInstallations(getInstallations(object));
			deployment.setId((Long) getHibernateTemplate().save(deployment));
		} catch (RuntimeException re) {
			// delete file on error
			destContent.delete();
			throw re;
		}
		return deployment;
	}

	/**
	 * Return all installations for the given object.
	 * 
	 * @param bean
	 *            the bean
	 * @return Set a set of Installations
	 */
	private final Set<Installation> getInstallations(ContentDeploymentBean bean) {

		Set<Installation> installations = new HashSet<Installation>();
		if (bean.getInstallation() != null) {
			installations.add((Installation) getHibernateTemplate().get(
					Installation.class, bean.getInstallation().getId()));
		} else if (bean.getSite() != null) {
			Site site = (Site) getHibernateTemplate().get(Site.class,
					bean.getSite().getId());
			installations.addAll(site.getInstallations());
		} else if (bean.getInstallationGroup() != null) {
			InstallationGroup group = (InstallationGroup) getHibernateTemplate()
					.get(InstallationGroup.class,
							bean.getInstallationGroup().getId());
			installations.addAll(group.getInstallations());
		} else {
			Customer customer = (Customer) getHibernateTemplate().get(
					Customer.class, bean.getCustomer().getId());
			for (Site site : customer.getSites()) {
				installations.addAll(site.getInstallations());
			}
		}
		return installations;
	}
}
