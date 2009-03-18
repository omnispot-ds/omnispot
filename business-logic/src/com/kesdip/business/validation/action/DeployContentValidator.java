/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 18, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.validation.action;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.kesdip.business.beans.ContentDeploymentBean;
import com.kesdip.business.constenum.IFileNamesEnum;
import com.kesdip.business.util.Errors;
import com.kesdip.business.validation.BaseValidator;
import com.kesdip.common.exception.GenericSystemException;
import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.StreamUtils;
import com.kesdip.common.util.StringUtils;

/**
 * Validation for the content deployment action.
 * 
 * @author gerogias
 */
public class DeployContentValidator extends BaseValidator {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(DeployContentValidator.class);

	/**
	 * Performs validation.
	 * 
	 * @see gr.panouepe.monitor.common.util.Validator#validate(java.lang.Object,
	 *      gr.panouepe.monitor.common.util.Errors)
	 */
	public void validate(Object obj, Errors errors) {
		ContentDeploymentBean bean = (ContentDeploymentBean) obj;

		// check null parent objects
		if (bean.getCustomer() == null && bean.getSite() == null
				&& bean.getInstallationGroup() == null
				&& bean.getInstallation() == null) {
			errors.addError("error.invalid.parent");
		}
		checkNullOrEmpty(bean.getName(), "name", errors);
		checkLengthNotGreaterThan(bean.getName(), "name", 50, errors);
		logger.debug("Checking file");
		// check file extension and size
		if (bean.getContentFile() == null) {
			logger.debug("XML file is null");
			errors.addError("contentFile", "error.file.null");
		} else {
			String suffix = FileUtils.getSuffix(bean.getContentFile()
					.getOriginalFilename());
			if (!"xml".equalsIgnoreCase(suffix)
					&& !"zip".equalsIgnoreCase(suffix)) {
				if (logger.isDebugEnabled()) {
					logger.debug("File '"
							+ bean.getContentFile().getOriginalFilename()
							+ "' does not have the xml or zip suffix");
				}
				errors.addError("contentFile", "error.file.suffix");
			}
			checkFileSize(bean, errors);
			if ("xml".equalsIgnoreCase(suffix)) {
				checkXml(bean, errors);
			} else {
				checkZip(bean, errors);
			}
		}
	}

	/**
	 * Check the file size to not be 0. If it is, an error message is added to
	 * the Errors object.
	 * 
	 * @param bean
	 *            the file-containing bean
	 * @param errors
	 *            the object to hold errors
	 */
	private final void checkFileSize(ContentDeploymentBean bean, Errors errors) {
		InputStream input = null;
		try {
			input = bean.getContentFile().getInputStream();
			if (logger.isDebugEnabled()) {
				logger.debug("Available bytes: " + input.available());
			}
			if (input.available() == 0) {
				errors.addError("contentFile", "error.file.size");
				// do not proceed to parsing
				return;
			}
		} catch (Exception e) {
			logger.error("Error opening file stream", e);
			throw new GenericSystemException(e);
		} finally {
			StreamUtils.close(input);
		}
	}

	/**
	 * Parse the uploaded XML for grammatical correctness.
	 * 
	 * @param bean
	 *            the file-containing bean
	 * @param errors
	 *            the object to hold errors
	 */
	private final void checkXml(ContentDeploymentBean bean, Errors errors) {
		// XML parsing
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(false);
			SAXParser parser = factory.newSAXParser();
			parser.parse(bean.getContentFile().getInputStream(),
					new DefaultHandler());
		} catch (SAXException se) {
			if (logger.isDebugEnabled()) {
				logger.debug("Invalid XML: " + se.getMessage());
			}
			errors.addError("contentFile", se.getMessage());
		} catch (Exception e) {
			logger.error("Error parsing file", e);
			throw new GenericSystemException(e);
		}
	}

	/**
	 * Check the uploaded ZIP for inconsistencies. Things which are checked:
	 * <ul>
	 * <li>ZIP contains a root <code>deployment.xml</code> file</li>
	 * <li>the XML file is grammatically correct</li>
	 * <li>all static media referenced by the XML file are located in the ZIP</li>
	 * <li>all files have a size &gt; zero</li>
	 * </ul>
	 * 
	 * @param bean
	 *            the file upload bean
	 * @param errors
	 *            object to hold errors
	 */
	final void checkZip(ContentDeploymentBean bean, Errors errors) {
		try {
			Map<String, ZipEntry> entryMap = getZipEntries(bean
					.getContentFile());
			// check file existence (deployment XML, "media" folder, files under
			// "media")
			checkFiles(entryMap, errors);
			// parse deployment XML
			parseDeployment(bean.getContentFile(), entryMap, errors);
		} catch (Exception e) {
			logger.error("Error opening file stream", e);
			errors.addError("contentFile", e.getMessage());
		}
	}

	/**
	 * Iterates all entries in the stream and adds them to the map using their
	 * name as key.
	 * 
	 * @param file
	 *            the ZIP
	 * @return Map the entry map
	 * @throws IOException
	 *             on error
	 */
	private final Map<String, ZipEntry> getZipEntries(MultipartFile file)
			throws IOException {
		Map<String, ZipEntry> entryMap = null;
		InputStream input = file.getInputStream();
		try {
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
					input));
			entryMap = new HashMap<String, ZipEntry>();
			ZipEntry zipEntry = null;
			while ((zipEntry = zis.getNextEntry()) != null) {
				entryMap.put(zipEntry.getName(), zipEntry);
				zis.closeEntry();
			}
		} finally {
			StreamUtils.close(input);
		}
		return entryMap;
	}

	/**
	 * Check the entries in the map.
	 * 
	 * @param entries
	 *            the map
	 * @param errors
	 *            the errors object
	 */
	private final void checkFiles(Map<String, ZipEntry> entries, Errors errors) {
		boolean foundDeployment = false, foundZeroSize = false;
		ZipEntry zipEntry = null;
		for (String name : entries.keySet()) {
			if (IFileNamesEnum.DEPLOYMENT_XML.equals(name)) {
				foundDeployment = true;
			}
			zipEntry = entries.get(name);
			if (zipEntry.getSize() == 0 && !foundZeroSize) {
				foundZeroSize = true;
				errors.addError("contentFile", "error.media.zero.size");
			}
		}
		if (!foundDeployment) {
			errors.addError("contentFile", "error.no.deployment.xml");
		}
	}

	/**
	 * Parse the deployment and look for the entries in the zip. If at least one
	 * is not found, then an error is added to the error object holder. If
	 * {@link #DEPLOYMENT_FILE} does not exist, nothing is done.
	 * 
	 * @param file
	 *            the file
	 * @param entries
	 *            the ZIP entries
	 * @param errors
	 *            the error holder
	 */
	final void parseDeployment(MultipartFile file,
			Map<String, ZipEntry> entries, Errors errors) throws IOException {
		InputStream input = null;
		try {
			input = file.getInputStream();
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
					input));
			ZipEntry deployment = entries.get(IFileNamesEnum.DEPLOYMENT_XML);
			if (deployment == null) {
				return;
			}
			ZipEntry tempEntry = null;
			String tempName = null;
			do {
				tempEntry = zis.getNextEntry();
				tempName = tempEntry != null ? tempEntry.getName() : "";
				// do nothing, just locate the right entry
			} while (!deployment.getName().equals(tempName)
					&& tempEntry != null);

			SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setValidating(false);
			try {
				SAXParser parser = parserFactory.newSAXParser();
				parser.parse(zis, new LocalResourceHandler(entries, errors));
			} catch (SAXException spe) {
				errors
						.addError("contentFile",
								"error.malformed.deployment.xml");
			} catch (ParserConfigurationException pce) {
				throw new IOException(pce);
			}
		} finally {
			StreamUtils.close(input);
		}
	}

	/**
	 * Utility class which waits for <code>property</code> tags and checks their
	 * <code>name</code> and <code>value</code> attributes. If the "name" is
	 * <em>identifier</em>, then the "value" is used to lookup up into the map
	 * of entries. The only exception is when the "value" is considered to
	 * represent a remote resource.
	 * <p>
	 * The handler only throws one error and ignores any consequent errors.
	 * </p>
	 * 
	 * @see StringUtils#isURL(String)
	 * @author gerogias
	 */
	static class LocalResourceHandler extends DefaultHandler {

		/**
		 * If an error has been reported.
		 */
		private boolean errorReported = false;

		/**
		 * The entries.
		 */
		private Map<String, ZipEntry> entries = null;

		/**
		 * The errors.
		 */
		private Errors errors = null;

		/**
		 * Constructor.
		 * 
		 * @param entries
		 *            the ZIP entries
		 * @param
		 */
		public LocalResourceHandler(Map<String, ZipEntry> entries, Errors errors) {
			this.entries = entries;
			this.errors = errors;
		}

		/**
		 * If the element is the right one and both attributes exist, process.
		 * 
		 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
		 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
		 */
		@Override
		public void startElement(String uri, String localName, String name,
				Attributes attributes) throws SAXException {

			// in this case, do nothing
			if (!"property".equalsIgnoreCase(name) || errorReported) {
				return;
			}
			// no "name", or name != identifier
			if (!"identifier".equalsIgnoreCase(attributes.getValue("name"))) {
				return;
			}
			String path = attributes.getValue("value");
			if (path == null) {
				errors.addError("contentFile", "error.no.resource.path");
				errorReported = true;
				return;
			}
			if (!StringUtils.isURL(path) && !entries.containsKey(path)) {
				errors.addError("contentFile", "error.resource.not.present");
				errorReported = true;
				return;
			}
		}
	}
}
