/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class WriterSupport {
	private static final Logger LOGGER = LogManager.getLogger(WriterSupport.class);

	public static void replaceText(StringBuilder sb, String replaceWhat, String replaceWith) {
		int startIndex = sb.indexOf(replaceWhat);
		int endIndex = startIndex + replaceWhat.length();

		if (startIndex >= 0 && endIndex >= 1) {
			sb.replace(startIndex, endIndex, replaceWith);
		}
	}

	public static void copyResource(Path outputDir, String resourceName) {
		try {
			// Create the directory if required
			if (!Files.exists(outputDir.getParent())) {
				Files.createDirectories(outputDir.getParent());
			}

			InputStream is = WriterSupport.class.getClassLoader().getResourceAsStream(resourceName);
			Files.copy(is, outputDir, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LOGGER.info("Failed to copy resource " + resourceName);

		}
	}

	// Suppress default constructor for noninstantiability
	private WriterSupport() {
		throw new AssertionError();
	}

}
