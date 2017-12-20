/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.jaxbclasses.projectConfig.BinaryModuleImpl;
import com.iawg.ecoa.jaxbclasses.projectConfig.ComponentImpl;
import com.iawg.ecoa.jaxbclasses.projectConfig.ConfigData;
import com.iawg.ecoa.jaxbclasses.projectConfig.PrecompiledComponents;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.xmlprocessor.XMLProc0Types;
import com.iawg.ecoa.xmlprocessor.XMLProc1ServiceDefs;
import com.iawg.ecoa.xmlprocessor.XMLProc2aCompDefs;
import com.iawg.ecoa.xmlprocessor.XMLProc3InitAssembly;
import com.iawg.ecoa.xmlprocessor.XMLProc4aCompImpl;
import com.iawg.ecoa.xmlprocessor.XMLProc4bBinDesc;
import com.iawg.ecoa.xmlprocessor.XMLProc5FUDPBinding;
import com.iawg.ecoa.xmlprocessor.XMLProc5aLogicalSys;
import com.iawg.ecoa.xmlprocessor.XMLProc5bFinalAssembly;
import com.iawg.ecoa.xmlprocessor.XMLProc5cDeployment;
import com.iawg.ecoa.xmlprocessor.XMLProc5eUID;
import com.iawg.ecoa.xmlprocessor.XMLProcProject;

public class SystemModelManager {
	private static final Logger LOGGER = LogManager.getLogger(SystemModelManager.class);
	private static final String SEP_PATTERN_01 = "5-Integration";
	private static final String INTDIR = SEP_PATTERN_01;

	private Path rootDir;
	private SystemModel systemModel = new SystemModel();
	private ToolConfig toolConfig = new ToolConfig();
	private XMLProcProject pxfp = null;
	private boolean apiOnly;

	private XMLProc0Types typesProc = new XMLProc0Types();
	private XMLProc1ServiceDefs serviceDefsProc = new XMLProc1ServiceDefs();
	private XMLProc2aCompDefs compDefsProc = new XMLProc2aCompDefs();
	private XMLProc3InitAssembly initAssemblyProc = new XMLProc3InitAssembly();
	private XMLProc4aCompImpl compImplProc = new XMLProc4aCompImpl(systemModel);
	private XMLProc5aLogicalSys logicalSysProc = new XMLProc5aLogicalSys();
	private XMLProc5bFinalAssembly finalAssemblyProc = new XMLProc5bFinalAssembly();
	private XMLProc5cDeployment deploymentProc = new XMLProc5cDeployment();
	private XMLProc5eUID uidProcessor = new XMLProc5eUID();
	private XMLProc5FUDPBinding udpBindingProcessor = new XMLProc5FUDPBinding();
	private XMLProc4bBinDesc binDescProc = new XMLProc4bBinDesc();

	public SystemModelManager(Path rootDir, boolean apiOnly) {
		this.rootDir = rootDir;
		this.apiOnly = apiOnly;

		if (Files.notExists(rootDir)) {
			LOGGER.info("ERROR - project root directory \"" + rootDir + "\" not found");
			
		}

		Path projectFile = null;

		// Use a "glob" expression to return a list of files which in end in
		// .xml
		try (DirectoryStream<Path> projectFiles = Files.newDirectoryStream(rootDir, "*.xml")) {
			List<Path> projectFileList = new ArrayList<Path>();
			for (Path file : projectFiles) {
				projectFileList.add(file);
			}

			if (projectFileList.size() > 1) {
				LOGGER.info("ERROR - more than one xml file in directory");
				
			} else if (projectFileList.size() == 1) {
				projectFile = projectFileList.get(0);
			}
		} catch (IOException e) {
			LOGGER.info("ERROR - could not list contents of project root directory");
			
		}

		pxfp = new XMLProcProject(rootDir);

		if (projectFile == null) {
			LOGGER.info("WARNING - config file not found using default tool config and searching directories...");
			systemModel.setOutputDir(rootDir.resolve("output"));
			LOGGER.info("INFO - using default output directory of " + systemModel.getOutputDir());
		} else {
			pxfp.parseFile(projectFile);
			LOGGER.info("Config file \"" + projectFile + "\" opened OK...");

			ConfigData tConfig = pxfp.getToolConfig();

			toolConfig.setGenerateBodies(tConfig.getAPIConfig().isGenerateBodies());
			toolConfig.setGenerateTemplateCModules(tConfig.getAPIConfig().isGenerateTemplateCModules());
			toolConfig.setOverwriteFiles(tConfig.getAPIConfig().isOverwrite());
			toolConfig.setInstrumentAllModules(tConfig.getPlatformConfig().isInstrumentAllModules());

			systemModel.setOutputDir(pxfp.getOutputDirectory());
			LOGGER.info("INFO - using output directory of " + systemModel.getOutputDir());

			if (!pxfp.hasProjectConfig()) {
				LOGGER.info("WARNING - project config not found searching directories...");
				systemModel.setOutputDir(rootDir.resolve("output"));
				LOGGER.info("INFO - using default output directory of " + systemModel.getOutputDir());
			}
		}
	}

	private void processPreBuiltComponents(PrecompiledComponents precompiledComponents) {
		if (precompiledComponents != null) {
			for (ComponentImpl ciConfig : precompiledComponents.getComponentImpls()) {
				SM_ComponentImplementation compImpl = systemModel.getComponentImplementations().getImplementationByName(ciConfig.getName());
				if (compImpl != null) {
					// Set the flag in the system model that this component
					// implementation is prebuilt.
					compImpl.setPrebuilt(true);
					for (BinaryModuleImpl binaryModImpl : ciConfig.getBinaryModuleImpls()) {
						SM_ModuleImpl modImpl = compImpl.getModuleImplementationByName(binaryModImpl.getName());
						if (modImpl != null) {
							// Set the flag in the system model that this
							// component implementation is prebuilt and store
							// the location of the library/object.
							modImpl.setPrebuilt(true);
							modImpl.setPrebuiltObjLocation(binaryModImpl.getBinaryObjectLocation());
						} else {
							LOGGER.info("WARNING - unable to find prebuilt module implementation for prebuilt component " + ciConfig.getName());
						}
					}
				} else {
					LOGGER.info("WARNING - unable to find prebuilt component implementation for prebuilt component " + ciConfig.getName());
				}
			}
		}
	}

	private List<Path> getMultipleFileMatchingGlob(Path directory, String globPattern) {
		List<Path> filesToReturn = new ArrayList<Path>();

		try (DirectoryStream<Path> filesMatchingGlob = Files.newDirectoryStream(directory, globPattern)) {
			for (Path matchedFile : filesMatchingGlob) {
				filesToReturn.add(matchedFile);
			}
		} catch (IOException e) {
			LOGGER.info("WARNING - could not list contents of " + directory.getFileName());
		}
		return filesToReturn;
	}

	private List<Path> getMultipleFileMatchingGlobSubDirs(Path directory, String globPattern) {
		List<Path> filesToReturn = new ArrayList<Path>();

		try (DirectoryStream<Path> allFiles = Files.newDirectoryStream(directory)) {
			for (Path matchedFile : allFiles) {
				if (Files.isDirectory(matchedFile)) {
					filesToReturn.addAll(getMultipleFileMatchingGlobSubDirs(matchedFile, globPattern));
				} else if (matchedFile.toString().endsWith(globPattern)) {
					filesToReturn.add(matchedFile);
				}
			}
		} catch (IOException e) {
			LOGGER.info("WARNING - could not list contents of " + directory.getFileName());
		}
		return filesToReturn;
	}

	public Path getRootDir() {
		return rootDir;
	}

	private Path getSingleFileMatchingGlob(Path directory, String globPattern) {
		Path fileToReturn = null;

		try (DirectoryStream<Path> filesMatchingGlob = Files.newDirectoryStream(directory, globPattern)) {
			List<Path> matchedFileList = new ArrayList<Path>();
			for (Path matchedFile : filesMatchingGlob) {
				matchedFileList.add(matchedFile);
			}

			if (matchedFileList.size() > 1) {
				LOGGER.info("ERROR - more than one " + globPattern + " file");
				
			} else if (matchedFileList.size() == 1) {
				fileToReturn = matchedFileList.get(0);
			}
		} catch (IOException e) {
			LOGGER.info("ERROR - could not list contents of " + directory.getFileName());
			
		}
		return fileToReturn;
	}

	public SystemModel getSystemModel() {
		return systemModel;
	}

	public ToolConfig getToolConfig() {
		return toolConfig;
	}

	private void processCompImpls() {
		LOGGER.info("");

		List<Path> compImplFiles;
		if (!pxfp.hasProjectConfig()) {
			compImplFiles = getMultipleFileMatchingGlobSubDirs(rootDir.resolve("4-ComponentImplementations"), ".impl.xml");
		} else {
			compImplFiles = pxfp.getComponentImplementations();
		}

		if (compImplFiles != null) {
			for (Path compImpFile : compImplFiles) {
				LOGGER.info("Starting parse of component implementation file " + compImpFile.getFileName() + "...");
				compImplProc.parseFile(compImpFile);

				if (!apiOnly) {
					// Process the binary description file (located in the same
					// directory if it is present.
					Path binDescFile = getSingleFileMatchingGlob(compImpFile.getParent(), "bin-desc.xml");
					if (binDescFile != null) {
						LOGGER.info("Starting parse of binary description file " + binDescFile.getFileName() + "...");
						binDescProc.parseFile(binDescFile);
					}
				}
			}

			compImplProc.updateSystemModel(toolConfig.isInstrumentAllModules());
			if (!apiOnly) {
				binDescProc.updateSystemModel(systemModel);
			}
		}
	}

	private void processComponentDefs() {
		LOGGER.info("");

		List<Path> componentDefFiles;

		if (!pxfp.hasProjectConfig()) {
			componentDefFiles = getMultipleFileMatchingGlobSubDirs(rootDir.resolve("2-ComponentDefinitions"), ".componentType");
		} else {
			componentDefFiles = pxfp.getComponentDefinitions();
		}

		if (componentDefFiles != null) {
			for (Path componentDefFile : componentDefFiles) {
				LOGGER.info("Starting parse of component definition file " + componentDefFile.getFileName() + "...");
				compDefsProc.parseFile(componentDefFile);
			}

			compDefsProc.updateSystemModel(systemModel);
		}
	}

	private void processDeployment() {
		LOGGER.info("");

		Path deploymentFile = null;

		if (!pxfp.hasProjectConfig()) {
			deploymentFile = getSingleFileMatchingGlob(rootDir.resolve(INTDIR), "*deployment.xml");
		} else {
			deploymentFile = pxfp.getDeployment();
		}

		if (deploymentFile != null) {
			LOGGER.info("Starting parse of deployment file " + deploymentFile.getFileName() + "...");
			deploymentProc.parseFile(deploymentFile);

			deploymentProc.updateSystemModel(systemModel);
		} else {
			LOGGER.info("There is no deployment file to parse...");
		}
	}

	public void processFiles() {
		processTypes();
		processServiceDefs();
		processComponentDefs();

		if (!apiOnly) {
			processInitAssembly();
		}

		processCompImpls();

		if (!apiOnly) {
			processLogicalSystem();
			processFinalAssembly();
			processDeployment();
			processUDPBinding();
			processUIDMapping();

			// Determine which components (if any) are pre-built.
			if (pxfp != null && pxfp.getToolConfig() != null) {
				processPreBuiltComponents(pxfp.getToolConfig().getPrecompiledComponents());
			}
		}

	}

	private void processFinalAssembly() {
		LOGGER.info("");

		Path assySchemaFile = null;

		if (!pxfp.hasProjectConfig()) {
			assySchemaFile = getSingleFileMatchingGlob(rootDir.resolve(INTDIR), "*impl.composite");
		} else {
			assySchemaFile = pxfp.getFinalAssembly();
		}

		if (assySchemaFile != null) {
			LOGGER.info("Starting parse of final assembly file " + assySchemaFile.getFileName() + "...");
			finalAssemblyProc.parseFile(assySchemaFile);
		} else {
			LOGGER.info("There is no final assembly file to parse...");
		}

		finalAssemblyProc.updateSystemModel(systemModel);
	}

	private void processInitAssembly() {
		LOGGER.info("");

		Path assySchemaFile = null;

		if (!pxfp.hasProjectConfig()) {
			assySchemaFile = getSingleFileMatchingGlob(rootDir.resolve("3-InitialAssembly"), "*.composite");
		} else {
			assySchemaFile = pxfp.getInitialAssembly();
		}

		if (assySchemaFile != null) {
			LOGGER.info("Starting parse of initial assembly file " + assySchemaFile.getFileName() + "...");
			initAssemblyProc.parseFile(assySchemaFile);
		} else {
			LOGGER.info("There is no initial assembly file to parse...");
		}

		initAssemblyProc.updateSystemModel(systemModel);
	}

	private void processLogicalSystem() {
		LOGGER.info("");

		Path logicSysFile = null;

		if (!pxfp.hasProjectConfig()) {
			logicSysFile = getSingleFileMatchingGlob(rootDir.resolve(INTDIR), "*logical_system.xml");
		} else {
			logicSysFile = pxfp.getLogicalSystem();
		}

		if (logicSysFile != null) {
			LOGGER.info("Starting parse of logical system file " + logicSysFile.getFileName() + "...");
			logicalSysProc.parseFile(logicSysFile);
		} else {
			LOGGER.info("There is no logical system file to parse...");
		}

		logicalSysProc.updateSystemModel(systemModel);
	}

	private void processServiceDefs() {
		LOGGER.info("");

		List<Path> serviceDefFiles;

		if (!pxfp.hasProjectConfig()) {
			serviceDefFiles = getMultipleFileMatchingGlob(rootDir.resolve("1-Services"), "*.interface.xml");
		} else {
			serviceDefFiles = pxfp.getServiceDefinitions();
		}

		if (serviceDefFiles != null) {
			for (Path serviceDefFile : serviceDefFiles) {
				LOGGER.info("Starting parse of service definition file " + serviceDefFile.getFileName() + "...");
				serviceDefsProc.parseFile(serviceDefFile);
			}

			serviceDefsProc.updateSystemModel(systemModel);
		}
	}

	private void processTypes() {
		LOGGER.info("");

		List<Path> typeFiles;
		if (!pxfp.hasProjectConfig()) {
			typeFiles = getMultipleFileMatchingGlob(rootDir.resolve("0-Types"), "*.types.xml");
		} else {
			typeFiles = pxfp.getTypes();
		}

		if (typeFiles != null) {
			for (Path typeFile : typeFiles) {
				LOGGER.info("Starting parse of types file " + typeFile.getFileName() + "...");
				typesProc.parseFile(typeFile);
			}

			typesProc.updateSystemModel(systemModel);
		}
	}

	private void processUDPBinding() {
		LOGGER.info("");

		Path udpFile = null;

		if (!pxfp.hasProjectConfig()) {
			udpFile = getSingleFileMatchingGlob(rootDir.resolve(INTDIR), "*udpbinding.xml");
		} else {
			LOGGER.info("Project file does not currently define a UDP binding - searching directories...");
			udpFile = getSingleFileMatchingGlob(rootDir.resolve(INTDIR), "*udpbinding.xml");
		}

		if (udpFile != null) {
			LOGGER.info("Starting parse of udpbinding file " + udpFile.getFileName() + "...");
			udpBindingProcessor.parseFile(udpFile);

		} else {
			LOGGER.info("There is no udp binding file to parse...");
		}

		udpBindingProcessor.updateSystemModel(systemModel);
	}

	private void processUIDMapping() {
		LOGGER.info("");

		Path uidFile = null;

		// Project file does not currently define a UID file.

		// if (!pxfp.hasProjectConfig())
		// {
		uidFile = getSingleFileMatchingGlob(rootDir.resolve(INTDIR), "*IDs.xml");
		// }
		// else
		// {
		// LOGGER.info("Project file does not currently define a UID
		// mapping file");
		// }

		if (uidFile != null) {
			LOGGER.info("Starting parse of uid mapping file " + uidFile.getFileName() + "...");
			uidProcessor.parseFile(uidFile);
		} else {
			LOGGER.info("There is no uid mapping file to parse...");
		}

		uidProcessor.updateSystemModel(systemModel);
	}

}