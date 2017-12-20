/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.xmlprocessor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBElement;

import com.iawg.ecoa.jaxbclasses.projectConfig.ConfigData;
import com.iawg.ecoa.jaxbclasses.projectConfig.ECOAProject;
import com.iawg.ecoa.jaxbclasses.projectConfig.EcoaProjectConfig;
import com.iawg.ecoa.jaxbclasses.projectConfig.Files;

/**
 * This class processes an XML file that references the XML files used to
 * describe an ECOA project.
 * 
 * @author Shaun Cullimore
 */
public class XMLProcProject {
	private EcoaProjectConfig ecoaproject;
	private List<Path> typeFiles = new ArrayList<Path>();
	private List<Path> serviceDefinitions = new ArrayList<Path>();
	private List<Path> componentDefinitions = new ArrayList<Path>();
	private Path initialAssembly = null;
	private List<Path> componentImplementations = new ArrayList<Path>();
	private Path logicalSystem = null;
	private Path finalAssembly = null;
	private Path deployment = null;
	private Path outputDirectory = null;
	private Path rootDir;

	private ConfigData tConfig;
	private boolean hasProjectConfig = false;

	public XMLProcProject(Path rootDir) {
		this.rootDir = rootDir;
	}

	public List<Path> getComponentDefinitions() {
		return componentDefinitions;
	}

	public List<Path> getComponentImplementations() {
		return componentImplementations;
	}

	public Path getDeployment() {
		return deployment;
	}

	public Path getFinalAssembly() {
		return finalAssembly;
	}

	public Path getInitialAssembly() {
		return initialAssembly;
	}

	public Path getLogicalSystem() {
		return logicalSystem;
	}

	public Path getOutputDirectory() {
		return outputDirectory;
	}

	public List<Path> getServiceDefinitions() {
		return serviceDefinitions;
	}

	public ConfigData getToolConfig() {
		return tConfig;
	}

	public List<Path> getTypes() {
		return typeFiles;
	}

	public boolean hasProjectConfig() {
		return hasProjectConfig;
	}

	public void parseFile(Path project) {
		XMLFileProcessor pxfp = new XMLFileProcessor("ecoa-project-config-1.0.xsd", "com.iawg.ecoa.jaxbclasses.projectConfig");

		ecoaproject = (EcoaProjectConfig) pxfp.parseFile(project);

		tConfig = ecoaproject.getToolConfig();

		ECOAProject projectConfig = ecoaproject.getECOAProject();

		if (projectConfig != null) {
			hasProjectConfig = true;

			List<JAXBElement<?>> config = projectConfig.getServiceDefinitionsAndComponentDefinitionsAndTypes();

			Iterator<JAXBElement<?>> projectFileLists = config.iterator();

			while (projectFileLists.hasNext()) {
				JAXBElement<?> projectFile = projectFileLists.next();

				String elementName = projectFile.getName().getLocalPart();

				if (elementName.equals("types")) {
					Files flist = (Files) projectFile.getValue();

					List<String> fileList = flist.getFiles();

					for (String file : fileList) {
						typeFiles.add(rootDir.resolve(file));
					}
				} else if (elementName.equals("serviceDefinitions")) {
					Files flist = (Files) projectFile.getValue();

					List<String> fileList = flist.getFiles();

					for (String file : fileList) {
						serviceDefinitions.add(rootDir.resolve(file));
					}
				} else if (elementName.equals("componentDefinitions")) {
					Files flist = (Files) projectFile.getValue();

					List<String> fileList = flist.getFiles();

					for (String file : fileList) {
						componentDefinitions.add(rootDir.resolve(file));
					}
				} else if (elementName.equals("initialAssembly")) {
					initialAssembly = rootDir.resolve((String) projectFile.getValue());
				} else if (elementName.equals("componentImplementations")) {
					Files flist = (Files) projectFile.getValue();

					List<String> fileList = flist.getFiles();

					for (String file : fileList) {
						componentImplementations.add(rootDir.resolve(file));
					}
				} else if (elementName.equals("logicalSystem")) {
					logicalSystem = rootDir.resolve((String) projectFile.getValue());
				} else if (elementName.equals("implementationAssembly")) {
					finalAssembly = rootDir.resolve((String) projectFile.getValue());
				} else if (elementName.equals("deploymentSchema")) {
					deployment = rootDir.resolve((String) projectFile.getValue());
				} else if (elementName.equals("outputDirectory")) {
					outputDirectory = rootDir.resolve((String) projectFile.getValue()).normalize();
				}
			}
		}
	}

}
