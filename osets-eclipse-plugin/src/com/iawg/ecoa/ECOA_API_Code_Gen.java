/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.apigen.APIGenerator;
import com.iawg.ecoa.buildgen.BuildGeneratorAPI;

/**
 * This is the main entry point for the ECOA API Code Generator
 * 
 * @author Daniel.Clarke
 *
 */
public class ECOA_API_Code_Gen {
	private static final Logger LOGGER = LogManager.getLogger(ECOA_API_Code_Gen.class);

	/**
	 * This is the main function of the application.
	 * 
	 * @param args
	 *            project file (XML), protection domain name for which source
	 *            code is to be generated.
	 */
	public static void main(String[] args) {
		// The argument should be the root Steps/ directory
		if (args.length == 1) {
			boolean apiOnly = true;
			Path projectLocation = Paths.get(args[0]);

			ECOA_API_Code_Gen ecoaAPICodeGen = new ECOA_API_Code_Gen(projectLocation, apiOnly);
			ecoaAPICodeGen.generateAPIs();
		} else {
			LOGGER.info("ERROR - call should be \"ECOA_API_Code_Gen_Plugin <project root directory>\"");
			
		}

	}

	private ECOA_System_Model systemModel;

	private Path containerOutputDir;
	private Path stepsDir;

	/**
	 * This constructor can be used to create a system model
	 * 
	 * @param stepsDir
	 * @param apiOnly
	 */
	public ECOA_API_Code_Gen(Path stepsDir, boolean apiOnly) {
		// Create an object to manage processing of the XML
		this.systemModel = new ECOA_System_Model(stepsDir, apiOnly);
		this.containerOutputDir = systemModel.getSystemModel().getOutputDir();
		this.stepsDir = stepsDir;
	}

	/**
	 * This constructor can be used to pass in a system model object.
	 * 
	 * @param stepsDir
	 * @param containerOutputDir
	 * @param systemModel
	 */
	public ECOA_API_Code_Gen(Path stepsDir, Path containerOutputDir, ECOA_System_Model systemModel) {
		this.systemModel = systemModel;
		this.containerOutputDir = systemModel.getSystemModel().getOutputDir();
		this.stepsDir = stepsDir;
	}

	public void generateAPIs() {
		// Start code generation of the application developer files
		APIGenerator apiGen = new APIGenerator(systemModel.getSystemModel(), systemModel.getToolConfig(), containerOutputDir);
		apiGen.generate();

		// Generate build files
		BuildGeneratorAPI buildGen = new BuildGeneratorAPI(systemModel.getSystemModel(), stepsDir, containerOutputDir);
		buildGen.generateBuildFiles();

		// End of code generator
		LOGGER.info("ECOA API Code Generator Completed!");

	}

}
