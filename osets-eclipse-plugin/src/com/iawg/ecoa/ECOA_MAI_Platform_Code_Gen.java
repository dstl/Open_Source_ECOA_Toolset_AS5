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

import com.iawg.ecoa.buildgen.BuildGeneratorPlatform;
import com.iawg.ecoa.platformgen.PlatformGenerator;

/**
 * This is the main entry point for the ECOA MAI Platform Code Generator
 * 
 * @author Daniel.Clarke
 *
 */
public class ECOA_MAI_Platform_Code_Gen {
	private static final Logger LOGGER = LogManager.getLogger(ECOA_MAI_Platform_Code_Gen.class);

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
			boolean apiOnly = false;
			Path stepsDir = Paths.get(args[0]);

			ECOA_MAI_Platform_Code_Gen ecoaMAIPlatformCodeGen = new ECOA_MAI_Platform_Code_Gen(stepsDir, apiOnly);
			ecoaMAIPlatformCodeGen.generateCode();
		} else {
			LOGGER.info("ERROR - call should be \"ECOA_MAI_Platform_Code_Gen_Plugin <project root directory>\"");
			
		}

	}

	private ECOA_System_Model systemModel;
	private Path stepsDir;
	private Path containerOutputDir;

	public ECOA_MAI_Platform_Code_Gen(Path stepsDir, boolean apiOnly) {
		// Create an object to manage processing of the XML
		systemModel = new ECOA_System_Model(stepsDir, apiOnly);
		this.stepsDir = stepsDir;
		containerOutputDir = systemModel.getSystemModel().getOutputDir();
	}

	public void generateCode() {
		// Generate the ECOA standard APIs and types.
		ECOA_API_Code_Gen ecoaAPICodeGen = new ECOA_API_Code_Gen(stepsDir, containerOutputDir, systemModel);
		ecoaAPICodeGen.generateAPIs();

		if (systemModel.getSystemModel().getFinalAssembly() != null && systemModel.getSystemModel().getDeployment() != null && systemModel.getSystemModel().getLogicalSystem() != null) {
			PlatformGenerator platGen = new PlatformGenerator(systemModel.getSystemModel(), containerOutputDir, stepsDir);
			// Generate platform code
			platGen.generatePlatformCode();

			// Generate build files
			BuildGeneratorPlatform buildGen = new BuildGeneratorPlatform(systemModel.getSystemModel(), stepsDir, containerOutputDir);
			buildGen.generateBuildFiles();

			// End of code generator
			LOGGER.info("ECOA MAI Platform Code Generator Completed!");
		} else {
			LOGGER.info("Required deployment files not found - not generating platform!");
		}
	}

}
