/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel;

import java.nio.file.Path;

import com.iawg.ecoa.systemmodel.assembly.SM_AssemblySchema;
import com.iawg.ecoa.systemmodel.componentdefinition.SM_ComponentTypes;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementations;
import com.iawg.ecoa.systemmodel.deployment.SM_Deployment;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalSystem;
import com.iawg.ecoa.systemmodel.servicedefinition.SM_ServiceDefinitions;
import com.iawg.ecoa.systemmodel.types.SM_Types;

/*
 * The top-level System Model class containing all other objects.
 */
public class SystemModel {

	private SM_Types types = new SM_Types();
	private SM_ServiceDefinitions serviceDefinitions = new SM_ServiceDefinitions();
	private SM_ComponentTypes componentDefinitions = new SM_ComponentTypes();
	private SM_ComponentImplementations componentImplementations = new SM_ComponentImplementations();

	private SM_Deployment deployment = new SM_Deployment();
	private SM_AssemblySchema initialAssembly = null;
	private SM_AssemblySchema finalAssembly = null;
	private SM_LogicalSystem logicalSystem = null;

	private Path outputDir;

	public SM_ComponentTypes getComponentDefinitions() {
		return componentDefinitions;
	}

	public SM_ComponentImplementations getComponentImplementations() {
		return componentImplementations;
	}

	public SM_Deployment getDeployment() {
		return deployment;
	}

	public SM_AssemblySchema getFinalAssembly() {
		return finalAssembly;
	}

	public SM_AssemblySchema getInitialAssembly() {
		return initialAssembly;
	}

	public SM_LogicalSystem getLogicalSystem() {
		return logicalSystem;
	}

	public SM_ServiceDefinitions getServiceDefinitions() {
		return serviceDefinitions;
	}

	// Getter functions
	public SM_Types getTypes() {
		return types;
	}

	public void setFinalAssembly(SM_AssemblySchema assembly) {
		this.finalAssembly = assembly;
	}

	// Setter functions
	public void setInitialAssembly(SM_AssemblySchema assembly) {
		this.initialAssembly = assembly;
	}

	public void setLogicalSystem(SM_LogicalSystem logicalSystem) {
		this.logicalSystem = logicalSystem;
	}

	public void setOutputDir(Path outputDir) {
		this.outputDir = outputDir;
	}

	public Path getOutputDir() {
		return outputDir;
	}
}
