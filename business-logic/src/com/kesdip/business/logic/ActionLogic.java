/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 18, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.logic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.beans.ActionBean;
import com.kesdip.business.beans.ActionQueryBean;
import com.kesdip.business.beans.BaseMultitargetBean;
import com.kesdip.business.beans.ContentDeploymentBean;
import com.kesdip.business.config.ApplicationSettings;
import com.kesdip.business.constenum.IActionParamsEnum;
import com.kesdip.business.constenum.IActionStatusEnum;
import com.kesdip.business.constenum.IActionTypesEnum;
import com.kesdip.business.constenum.IFileNamesEnum;
import com.kesdip.business.constenum.IMediaCopyPolicyEnum;
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
import com.kesdip.common.util.StringUtils;

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
	 * deploys to as many {@link Installation}s as needed. It copies the file to
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
		String suffix = FileUtils.getSuffix(object.getContentFile()
				.getOriginalFilename());
		ProcessedXmlFile processedFile = null;
		try {
			// process the file
			if ("xml".equalsIgnoreCase(suffix)) {
				processedFile = handleXml(object.getContentFile()
						.getInputStream(), object.getContentFile()
						.getOriginalFilename());
			} else {
				processedFile = handleZip(object);
			}
		} catch (IOException e) {
			logger.error("Error opening XML file", e);
			throw new GenericSystemException("Error opening XML file", e);
		}
		// add the deployments in the DB
		Deployment deployment = null;
		try {
			logger.debug("Creating deployment in the DB");
			Set<Installation> installations = getInstallations(object);
			String contentBase = ApplicationSettings.getInstance()
					.getServerSettings().getContentBaseUrl();
			deployment = new Deployment();
			deployment.setName(object.getName());
			deployment
					.setCrc(String.valueOf(processedFile.getCrc().getValue()));
			deployment.setLocalFile(processedFile.getXmlFile()
					.getAbsolutePath());
			deployment.setUrl(contentBase + processedFile.getUniqueName());
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
				parameter.setValue(String.valueOf(processedFile.getCrc()
						.getValue()));
				parameter.setId((Long) getHibernateTemplate().save(parameter));
				action.getParameters().add(parameter);
				// URL
				parameter = new Parameter();
				parameter.setName(IActionParamsEnum.DEPLOYMENT_URL);
				parameter.setValue(contentBase + processedFile.getUniqueName());
				parameter.setId((Long) getHibernateTemplate().save(parameter));
				action.getParameters().add(parameter);
				// store action
				action.setActionId(getActionId());
				action.setDateAdded(new Date());
				action.setInstallation(installation);
				action.setType(IActionTypesEnum.DEPLOY);
				action.setStatus(IActionStatusEnum.SCHEDULED);
				action.setId((Long) getHibernateTemplate().save(action));
			}
		} catch (RuntimeException re) {
			// delete file on error
			processedFile.getXmlFile().delete();
			// TODO: Delete media files as well
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
					parameter.setId((Long) getHibernateTemplate().save(
							parameter));
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
	 * Returns all actions for the given target object, organized per action
	 * type, action status and installation.
	 * 
	 * @param bean
	 *            the query bean
	 * @return ActionQueryBean the populated bean
	 * @throws ValidationException
	 *             on validation error
	 */
	@Transactional(readOnly = true)
	public ActionQueryBean getActionSet(ActionQueryBean bean)
			throws ValidationException {
		validate(bean, "actionSet");
		bean.setEntityName(getEntityName(bean));
		// get installations to iterate
		Set<Installation> installations = getInstallations(bean);
		for (Installation installation : installations) {
			// add to maps as appropriate
			bean.addInstallation(installation);
		}
		return bean;
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

	/**
	 * Process an uploaded XML. Serializes it to the appropriate location, with
	 * a unique filename.
	 * 
	 * @param xmlStream
	 *            the XML input stream; it is closed after processing
	 * @param originalFileName
	 *            the original file name
	 * @return ProcessedXmlFile the results of processing the XML
	 */
	private final ProcessedXmlFile handleXml(InputStream xmlStream,
			String originalFileName) {
		File contentFolder = new File(ApplicationSettings.getInstance()
				.getFileStorageSettings().getContentFolder());
		String uniqueName = FileUtils.getUniqueFileName(originalFileName);
		File destContent = new File(contentFolder, uniqueName);
		if (logger.isDebugEnabled()) {
			logger.debug("Copying to file " + destContent.getAbsolutePath());
		}
		CRC32 crc = new CRC32();
		try {
			StreamUtils.copyToFile(xmlStream, destContent, crc);
		} finally {
			StreamUtils.close(xmlStream);
		}
		return new ProcessedXmlFile(destContent, crc, uniqueName);
	}

	/**
	 * Process an uploaded ZIP file. Serializes the deployment XML and any media
	 * files to the appropriate locations. Before serialization, it processes
	 * the resource references in the XML to point back to the default media
	 * folder.
	 * 
	 * @param object
	 *            the file-wrapping object
	 * @return ProcessedXmlFile the results of processing the XML
	 * @throws IOException
	 *             on error
	 */
	private final ProcessedXmlFile handleZip(ContentDeploymentBean object)
			throws IOException {

		ProcessedXmlFile deploymentXml = null;
		ZipInputStream input = null;
		try {
			input = new ZipInputStream(object.getContentFile().getInputStream());
			ZipEntry zipEntry = null;
			while ((zipEntry = input.getNextEntry()) != null) {
				if (IFileNamesEnum.DEPLOYMENT_XML.equalsIgnoreCase(zipEntry
						.getName())) {
					ByteArrayInputStream bais = new ByteArrayInputStream(
							processDeploymentXml(input));
					deploymentXml = handleXml(bais,
							IFileNamesEnum.DEPLOYMENT_XML);
				} else {
					handleMedia(input, zipEntry, object.getPolicy());
				}
				input.closeEntry();
			}
		} finally {
			StreamUtils.close(input);
		}
		return deploymentXml;
	}

	/**
	 * Serialize a media file to the content folder, depending on the selected
	 * policy. If the file name contains path info, it is flattened.
	 * 
	 * @param input
	 *            the media file stream
	 * @param zipEntry
	 *            the ZIP entry, may contain path info
	 * @param policy
	 *            the overwrite policy
	 * @see IMediaCopyPolicyEnum
	 */
	private final void handleMedia(InputStream input, ZipEntry zipEntry,
			int policy) {

		File contentFolder = new File(ApplicationSettings.getInstance()
				.getFileStorageSettings().getContentFolder());
		String filename = FileUtils.getName(zipEntry.getName());
		File destFile = new File(contentFolder, filename);

		if (!destFile.exists()
				|| IMediaCopyPolicyEnum.OVERWRITE_ALWAYS == policy) {
			// does not exist or overwrite
			StreamUtils.copyToFile(input, destFile);
		} else if (IMediaCopyPolicyEnum.KEEP_EXISTING == policy) {
			// do nothing
		} else {
			// check CRC
			CRC32 crc = StreamUtils.getCrc(destFile);
			if (crc.getValue() != zipEntry.getCrc()) {
				StreamUtils.copyToFile(input, destFile);
			}
		}
	}

	/**
	 * Replace all content filenames with a URL pointing to the configured
	 * content folder. If a file name begins with a protocol identifier
	 * (http://, ftp://), it is left intact.
	 * 
	 * 
	 * @param inputStream
	 *            the stream holding the XML
	 * @return byte[] the copy of the XML data
	 * @throws IOException
	 *             on error
	 */
	final byte[] processDeploymentXml(InputStream inputStream)
			throws IOException {

		String replacement = "<property name=\"identifier\" value=\"{0}\"/>";
		Pattern pattern = Pattern.compile(
				"<property\\s+?name=\"identifier\"\\s+?value=\"(.+?)\"\\/>",
				Pattern.CASE_INSENSITIVE);

		String contentBase = ApplicationSettings.getInstance()
				.getServerSettings().getContentBaseUrl();
		String xml = StreamUtils.readString(inputStream, "UTF-8", false);
		StringBuffer newXml = new StringBuffer(xml.length());
		Matcher matcher = pattern.matcher(xml);
		String fileName = null, url = null;
		while (matcher.find()) {
			fileName = matcher.group(1);
			if (!StringUtils.isURL(fileName)) {
				fileName = FileUtils.getName(matcher.group(1));
				url = contentBase + fileName;
			} else {
				url = fileName;
			}
			matcher.appendReplacement(newXml, MessageFormat.format(replacement,
					url));
		}
		matcher.appendTail(newXml);
		return newXml.toString().getBytes("UTF-8");
	}

	/**
	 * An internal utility class to return a processed XML file and its CRC.
	 * 
	 * @author gerogias
	 * 
	 */
	private static class ProcessedXmlFile {

		/**
		 * The wrapped XML file.
		 */
		private File xmlFile = null;

		/**
		 * The calculated CRC.
		 */
		private CRC32 crc = null;

		/**
		 * The unique file name.
		 */
		private String uniqueName = null;

		/**
		 * @return File the XML file
		 */
		public File getXmlFile() {
			return xmlFile;
		}

		/**
		 * @return CRC32 the calculated CRC
		 */
		public CRC32 getCrc() {
			return crc;
		}

		/**
		 * @return the uniqueName
		 */
		public String getUniqueName() {
			return uniqueName;
		}

		/**
		 * Constructor.
		 * 
		 * @param file
		 *            the file
		 * @param crc
		 *            the crc
		 * @param uniqueName
		 *            the calculated unique file name
		 */
		private ProcessedXmlFile(File file, CRC32 crc, String uniqueName) {
			this.xmlFile = file;
			this.crc = crc;
			this.uniqueName = uniqueName;
		}
	}
}
