/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 18, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.logic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.zip.CRC32;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.beans.ActionBean;
import com.kesdip.business.beans.BaseMultitargetBean;
import com.kesdip.business.beans.ContentDeploymentBean;
import com.kesdip.business.config.ApplicationSettings;
import com.kesdip.business.constenum.IActionParamsEnum;
import com.kesdip.business.constenum.IActionStatusEnum;
import com.kesdip.business.constenum.IActionTypesEnum;
import com.kesdip.business.domain.generated.Action;
import com.kesdip.business.domain.generated.Customer;
import com.kesdip.business.domain.generated.Deployment;
import com.kesdip.business.domain.generated.Installation;
import com.kesdip.business.domain.generated.InstallationGroup;
import com.kesdip.business.domain.generated.Parameter;
import com.kesdip.business.domain.generated.Site;
import com.kesdip.business.exception.ValidationException;
import com.kesdip.common.exception.GenericSystemException;
import com.kesdip.common.util.DateUtils;
import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.StreamUtils;

/**
 * Action-related logic.
 * 
 * @author gerogias
 */
public class ActionLogic extends BaseLogic {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(ActionLogic.class);

	/**
	 * Deploys a content file.
	 * <p>
	 * Depending on the selected object (customer, site, group, installation),
	 * deploys to as many {@link Installation}s as needed. It copies the file
	 * to the proper folder, ensuring it has a unique file name to avoid
	 * clashes.
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
			StreamUtils.close(input);
		}
		Deployment deployment = null;
		try {
			logger.debug("Creating deployment in the DB");
			Set<Installation> installations = getInstallations(object); 
			String contentBase = ApplicationSettings.getInstance()
					.getServerSettings().getContentBase();
			deployment = new Deployment();
			deployment.setName(object.getName());
			deployment.setCrc(String.valueOf(crc.getValue()));
			deployment.setLocalFile(destContent.getAbsolutePath());
			deployment.setUrl(contentBase + uniqueName);
			deployment.setInstallations(installations);
			deployment.setId((Long) getHibernateTemplate().save(deployment));
			// creating actions for all installations
			Action action = null;
			Parameter parameter = null;
			for (Installation installation : installations) {
				action = new Action();
				// CRC
				parameter = new Parameter();
				parameter.setName(IActionParamsEnum.DEPLOYMENT_CRC);
				parameter.setValue(String.valueOf(crc.getValue()));
				parameter.setId((Long)getHibernateTemplate().save(parameter));
				action.getParameters().add(parameter);
				// URL
				parameter = new Parameter();
				parameter.setName(IActionParamsEnum.DEPLOYMENT_URL);
				parameter.setValue(contentBase + uniqueName);
				parameter.setId((Long)getHibernateTemplate().save(parameter));
				action.getParameters().add(parameter);
				// store action
				action.setActionId(getActionId());
				action.setDateAdded(new Date());
				action.setInstallation(installation);
				action.setType(IActionTypesEnum.DEPLOY);
				action.setStatus(IActionStatusEnum.SCHEDULED);
				action.setId((Long)getHibernateTemplate().save(action));
			}
		} catch (RuntimeException re) {
			// delete file on error
			destContent.delete();
			throw re;
		}
		return deployment;
	}

	/**
	 * Schedules an action for a player or a set of players.
	 * <p>
	 * Depending on the selected object (customer, site, group, installation),
	 * creates an {@link Action} for as many {@link Installation}s as needed.
	 * </p>
	 * 
	 * @param object
	 *            the DTO
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public void scheduleAction(ActionBean object) throws ValidationException {

		validate(object, "scheduleAction");
		Set<Installation> installations = getInstallations(object);
		Action action = null;
		Date currentDate = new Date();
		for (Installation installation : installations) {
			action = new Action();
			// the parameters first (for CONFIGURE action only)
			if (object.getAction().getType() == IActionTypesEnum.RECONFIGURE) {
				for (Parameter parameter : object.getAction().getParameters()) {
					parameter.setId(null);
					parameter.setId((Long)getHibernateTemplate().save(parameter));
					action.getParameters().add(parameter);
				}
			}
			action.setDateAdded(currentDate);
			action.setInstallation(installation);
			action.setStatus(IActionStatusEnum.SCHEDULED);
			action.setType(object.getAction().getType());
			action.setActionId(getActionId());
			action.setId((Long) getHibernateTemplate().save(action));
			
		}
	}

	/**
	 * Return all installations for the given object.
	 * 
	 * @param bean
	 *            the bean
	 * @return Set a set of Installations
	 */
	private final Set<Installation> getInstallations(BaseMultitargetBean bean) {

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

	/**
	 * @return String the actionId (date_UUID)
	 */
	private final String getActionId() {
		return new SimpleDateFormat(DateUtils.DATE_FORMAT).format(new Date())
				+ '_' + UUID.randomUUID().toString();
	}
}
