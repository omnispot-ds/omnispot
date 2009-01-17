/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 9, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.validation.deploy;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.kesdip.business.beans.ContentDeploymentBean;
import com.kesdip.business.util.Errors;
import com.kesdip.business.validation.BaseValidator;
import com.kesdip.common.exception.GenericSystemException;
import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.StreamUtils;

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
		logger.debug("Checking file");
		// check file extension and size
		if (bean.getContentFile() == null) {
			logger.debug("XML file is null");
			errors.addError("contentFile", "error.xml.null");
		} else {
			if (!"xml".equalsIgnoreCase(FileUtils.getSuffix(bean
					.getContentFile().getOriginalFilename()))) {
				if (logger.isDebugEnabled()) {
					logger.debug("File '"
							+ bean.getContentFile().getOriginalFilename()
							+ "' does not have the xml suffix");
				}
				errors.addError("contentFile", "error.xml.suffix");
			}
			InputStream input = null;
			try {
				input = bean.getContentFile().getInputStream();
				if (logger.isDebugEnabled()) {
					logger.debug("Available bytes: " + input.available());
				}
				if (input.available() == 0) {
					errors.addError("contentFile", "error.xml.fileSize");
					// do not proceed to parsing
					return;
				}
			} catch (Exception e) {
				logger.error("Error opening file stream", e);
				throw new GenericSystemException(e);
			} finally {
				StreamUtils.close(input);
			}
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
	}
}
