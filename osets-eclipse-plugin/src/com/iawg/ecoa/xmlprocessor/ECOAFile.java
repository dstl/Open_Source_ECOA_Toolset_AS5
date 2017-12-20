/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.xmlprocessor;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class represents a single XML file.
 * 
 */
public class ECOAFile {
	private static final Logger LOGGER = LogManager.getLogger(ECOAFile.class);
	private Path name;
	private String namespaceName;
	private Object object;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            the name of the XML file
	 * @param Object
	 *            the object that is the root of the object tree that results
	 *            from the unmarshalling of the XML file
	 */
	public ECOAFile(Path file, Object newObject) {
		this.name = file;
		object = newObject;

		// Isolate the namespace name from the rest of the filename
		if (file.getFileName().toString().endsWith(".types.xml")) {
			// The delimiter will depend upon the operating system in use
			String delimiter = "/";
			String temp[];
			temp = file.getFileName().toString().split(delimiter);
			// Name[(temp.length) - 1] contains the namespace name terminated
			// with ".types.xml"
			namespaceName = temp[(temp.length) - 1];
			namespaceName = namespaceName.substring(0, (namespaceName.length() - 10));

			if (namespaceName.contains(".")) {
				LOGGER.info("WARNING: filename contains '\\.' character - this is not allowed");
				
			}

			namespaceName = namespaceName.replaceAll("__", ".");
			LOGGER.info("Namespace for this types file is \"" + namespaceName + "\"...");
		}

	}

	public Path getName() {
		return name;
	}

	public String getNamespaceName() {
		return namespaceName;
	}

	public Object getObject() {
		return object;
	}
}
