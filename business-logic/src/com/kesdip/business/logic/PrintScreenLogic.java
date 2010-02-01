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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.beans.PrintScreen;
import com.kesdip.business.beans.ViewPrintScreenBean;
import com.kesdip.business.config.ApplicationSettings;
import com.kesdip.business.config.FileStorageSettings;
import com.kesdip.business.domain.admin.generated.Installation;

/**
 * Printscreen-related logic.
 * 
 * @author sgerogia
 */
public class PrintScreenLogic extends BaseLogic {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(PrintScreenLogic.class);

	/**
	 * Return the prinstscreens for this bean.
	 * <p>
	 * Checks to see if the request refers to a customer/group/site. Returns the
	 * original bean populated. If a printscreen cannot be found for an
	 * installation, a default image is returned.
	 * </p>
	 * 
	 * @return ViewPrintScreensBean the populated object
	 * @throws IllegalArgumentException
	 *             if the argument is <code>null</code> or w/o a contained
	 *             object (e.g. customer).
	 */
	@Transactional(readOnly = true)
	@SuppressWarnings("unchecked")
	public ViewPrintScreenBean getPrintScreens(ViewPrintScreenBean bean)
			throws IllegalArgumentException {
		if (bean == null) {
			logger.error("ViewPrintScreenBean is null");
			throw new IllegalArgumentException("ViewPrintScreenBean is null");
		}
		// check for parent object
		if (bean.getCustomer() == null && bean.getInstallationGroup() == null
				&& bean.getSite() == null && bean.getInstallation() == null) {
			logger.error("No parent object defined");
			throw new IllegalArgumentException("No parent object defined");
		}

		String psServerBase = ApplicationSettings.getInstance()
				.getServerSettings().getPrintScreenBaseUrl();
		FileStorageSettings fileStorageSettings = ApplicationSettings
				.getInstance().getFileStorageSettings();

		// iterate installations
		Set<Installation> installations = getInstallations(bean);
		bean.setEntityName(getEntityName(bean));
		PrintScreen printScreen = null;
		File psFile = null;
		for (Installation installation : installations) {
			psFile = getPrintScreenFile(installation, fileStorageSettings);
			printScreen = new PrintScreen();
			printScreen.setInstallation(installation);
			printScreen.setFileUrl(psServerBase + installation.getUuid());
			printScreen.setFileDate(new Date(psFile.lastModified()));
			bean.addPrintscreen(printScreen);
		}
		return bean;
	}

	/**
	 * @param bean
	 *            the bean to check
	 * @return Set of Installations for the parent object
	 */
	@SuppressWarnings("serial")
	private final Set<Installation> getInstallations(
			final ViewPrintScreenBean bean) {
		if (bean.getSite() != null) {
			SiteLogic logic = getLogicFactory().getSiteLogic();
			return logic.getInstance(bean.getSite()).getInstallations();
		} else if (bean.getInstallationGroup() != null) {
			GroupLogic logic = getLogicFactory().getGroupLogic();
			return logic.getInstance(bean.getInstallationGroup())
					.getInstallations();
		} else if (bean.getCustomer() != null) {
			InstallationLogic logic = getLogicFactory().getInstallationLogic();
			return new HashSet<Installation>(logic.getInstallations(bean
					.getCustomer()));
		} else {
			return new HashSet<Installation>() {
				{
					add(bean.getInstallation());
				}
			};
		}
	}

	/**
	 * Return the printscreen file for this installation. The method checks for
	 * file <em>folderBase/installation.uuid/printScreenName</em> and returns
	 * the file. If it does not exist, it returns the
	 * <em>defaultPrintScreen</em>.
	 * 
	 * @param installation
	 *            the current installation
	 * @param storageSettings
	 *            the file storage settings
	 * @return File the file
	 */
	private final File getPrintScreenFile(Installation installation,
			FileStorageSettings storageSettings) {
		String fileName = storageSettings.getPrintScreenFolder()
				+ installation.getUuid() + File.separatorChar
				+ storageSettings.getPrintScreenName();
		File psFile = new File(fileName);
		return psFile.isFile() ? psFile : new File("foo");
	}
}
