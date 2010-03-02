/*
 * Disclaimer:
 * Copyright 2008-2010 - Omni-Spot E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 02 Μαρ 2010
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.util.deployment;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.kesdip.business.exception.XmlParsingException;

/**
 * A SAX handler to gather all resources inside a deployment XML file.
 * 
 * @author gerogias
 */
public class DeploymentResourceGatherer extends DefaultHandler {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(DeploymentResourceGatherer.class);

	/**
	 * The XML stream.
	 */
	private InputStream inputStream = null;

	/**
	 * The read resources.
	 */
	private Set<String> resources = null;

	/**
	 * Flag to keep track if we are in the context of a "resource" element.
	 */
	private boolean insideResource = false;

	/**
	 * Constructor.
	 * 
	 * @param xmlStream
	 *            the stream to read from. It is not closed or reset after
	 *            processing
	 */
	public DeploymentResourceGatherer(InputStream xmlStream) {
		this.inputStream = xmlStream;
	}

	/**
	 * Return all resources in the XML.
	 * 
	 * @return Set the resources of the XML
	 * @throws XmlParsingException
	 *             on parsing error
	 */
	public Set<String> getResources() throws XmlParsingException {
		if (resources == null) {
			try {
				resources = new HashSet<String>();
				SAXParser parser = SAXParserFactory.newInstance()
						.newSAXParser();
				parser.parse(inputStream, this);
			} catch (Exception e) {
				logger.error("Error parsing deployment XML", e);
				throw new XmlParsingException(e);
			}
		}
		return resources;
	}

	/**
	 * Drop the "insideResource" flag, if necessary.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		if ("bean".equals(localName) && insideResource == true) {
			insideResource = false;
		}
	}

	/**
	 * Raise the "insideElement" flag or add a resource to the collection.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		if ("bean".equals(qName)
				&& "com.kesdip.player.components.Resource".equals(attributes
						.getValue("class"))) {
			// <bean class="com.kesdip.player.components.Resource">
			insideResource = true;
		} else if (insideResource && "property".equals(qName)
				&& "identifier".equals(attributes.getValue("name"))) {
			// <property name="identifier">
			resources.add(attributes.getValue("value"));
		}
	}
}
