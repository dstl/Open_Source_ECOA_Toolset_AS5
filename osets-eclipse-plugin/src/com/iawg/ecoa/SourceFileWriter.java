/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This is an abstract class that is the base class for all extending classes
 * that write to files.
 * 
 * @author Shaun Cullimore
 */
public abstract class SourceFileWriter {
	private static final Logger LOGGER = LogManager.getLogger(SourceFileWriter.class);
	protected String LF = System.lineSeparator();
	protected StringBuilder codeStringBuilder = new StringBuilder();
	protected BufferedWriter bufferedWriter;
	protected Path file;
	protected Path outputDir;

	public SourceFileWriter(Path outputDir) {
		this.outputDir = outputDir;
	}

	public void close() {
		try {
			bufferedWriter.write(codeStringBuilder.toString());
			bufferedWriter.close();
		} catch (IOException e) {
			LOGGER.info("ERROR - file \"" + file + "\" could not be written");
			
		}
	}

	public abstract void open();

	protected void openFile(Path file) {
		try {
			// Create the directory if required
			if (!Files.exists(file.getParent())) {
				Files.createDirectories(file.getParent());
			}

			bufferedWriter = Files.newBufferedWriter(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			LOGGER.info("ERROR - file \"" + file + "\" could not be opened for writing");
			
		}
	}

	protected void createDirectory(Path directory) {
		try {
			if (!Files.exists(directory)) {
				Files.createDirectories(directory);
			}
		} catch (IOException e) {
			LOGGER.info("ERROR - unable to create directory " + directory);
			
		}
	}

	protected abstract void setFileStructure();

}
