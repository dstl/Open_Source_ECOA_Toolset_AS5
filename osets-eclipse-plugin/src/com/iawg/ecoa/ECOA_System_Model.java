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

import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerType;

@SuppressWarnings("unused")
public class ECOA_System_Model {
	private static final Logger LOGGER = LogManager.getLogger(ECOA_System_Model.class);

	public enum ImplLanguage {
		C, CPP, ADA
	}

	public enum UnderlyingPlatformEnum {
		POSIX, CONVERGED_APOS, MAI_APOS
	}

	/**
	 * This is the main function of the application.
	 * 
	 * <p>
	 * Used for testing only as normally will be invoked from a project
	 * depending on this library.
	 * </p>
	 * 
	 * @param args
	 *            Steps directory of ECOA XML.
	 */
	public static void main(String[] args) {
		// The argument should be the root Steps/ directory
		if (args.length == 1) {
			Path projectLocation = Paths.get(args[0]);
			boolean apiOnly = false;

			ECOA_System_Model ecoaSystemModel = new ECOA_System_Model(projectLocation, apiOnly);
		} else {
			LOGGER.info("ERROR - call should be \"ECOA_System_Model <project root directory>\"");
			
		}
	}

	private SystemModelManager systemModelManager;

	public ECOA_System_Model(Path projectLocation, boolean apiOnly) {
		// Create an object to manage processing of the XML
		systemModelManager = new SystemModelManager(projectLocation, apiOnly);
		systemModelManager.processFiles();
	};

	public SystemModel getSystemModel() {
		return systemModelManager.getSystemModel();
	}

	public ToolConfig getToolConfig() {
		return systemModelManager.getToolConfig();
	}
}
