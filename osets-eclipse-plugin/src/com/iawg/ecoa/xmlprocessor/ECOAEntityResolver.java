/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.xmlprocessor;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

public class ECOAEntityResolver implements LSResourceResolver {
	private static final Logger LOGGER = LogManager.getLogger(ECOAEntityResolver.class);
	private static final String SEP_PATTERN_11 = "ERROR \"resolveEntity\" unable to find schema \"";

	@Override
	public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {

		Pattern p = Pattern.compile("/?([^/]+\\.xsd)");

		Matcher m = p.matcher(systemId);

		if (m.find()) {
			// LOGGER.info("Resolving \"" + m.group(1) + "\"...");

			switch (m.group(1)) {
			case "ecoa-project-1.0.xsd": {
				InputStream is = this.getClass().getClassLoader().getResourceAsStream("ecoa-project-1.0.xsd");

				if (is != null) {
					return new ECOALSInput(is);
				} else {
					LOGGER.info(SEP_PATTERN_11 + m.group(1) + "\"...");
					
				}
			}
			case "ecoa-common-1.0.xsd": {
				InputStream is = this.getClass().getClassLoader().getResourceAsStream("ecoa-common-1.0.xsd");

				if (is != null) {
					return new ECOALSInput(is);
				} else {
					LOGGER.info(SEP_PATTERN_11 + m.group(1) + "\"...");
					
				}
			}
			case "xml.xsd": {
				InputStream is = this.getClass().getClassLoader().getResourceAsStream("xml/xml.xsd");

				if (is != null) {
					return new ECOALSInput(is);
				} else {
					LOGGER.info(SEP_PATTERN_11 + m.group(1) + "\"...");
					
				}
			}
			case "ecoa-sca-1.0.xsd": {
				InputStream is = this.getClass().getClassLoader().getResourceAsStream("sca/ecoa-sca-1.0.xsd");

				if (is != null) {
					return new ECOALSInput(is);
				} else {
					LOGGER.info(SEP_PATTERN_11 + m.group(1) + "\"...");
					
				}
			}
			case "ecoa-sca-instance-1.0.xsd": {
				InputStream is = this.getClass().getClassLoader().getResourceAsStream("sca/extensions/ecoa-sca-instance-1.0.xsd");

				if (is != null) {
					return new ECOALSInput(is);
				} else {
					LOGGER.info(SEP_PATTERN_11 + m.group(1) + "\"...");
					
				}
			}
			case "ecoa-sca-interface-1.0.xsd": {
				InputStream is = this.getClass().getClassLoader().getResourceAsStream("sca/extensions/ecoa-sca-interface-1.0.xsd");

				if (is != null) {
					return new ECOALSInput(is);
				} else {
					LOGGER.info(SEP_PATTERN_11 + m.group(1) + "\"...");
					
				}
			}
			case "ecoa-sca-attributes-1.0.xsd": {
				InputStream is = this.getClass().getClassLoader().getResourceAsStream("sca/ecoa-sca-attributes-1.0.xsd");

				if (is != null) {
					return new ECOALSInput(is);
				} else {
					LOGGER.info(SEP_PATTERN_11 + m.group(1) + "\"...");
					
				}
			}
			case "sca-core-1.1-cd06-subset.xsd": {
				InputStream is = this.getClass().getClassLoader().getResourceAsStream("sca/sca-core-1.1-cd06-subset.xsd");

				if (is != null) {
					return new ECOALSInput(is);
				} else {
					LOGGER.info(SEP_PATTERN_11 + m.group(1) + "\"...");
					
				}
			}
			default:
				LOGGER.info("ERROR unable to \"resolveEntity\" - " + m.group(1));
				
			}
		}
		return null;
	}
}
