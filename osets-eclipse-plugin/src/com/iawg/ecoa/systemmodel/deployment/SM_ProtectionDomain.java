/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.deployment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingNode;

public class SM_ProtectionDomain extends SM_Object {
	private List<SM_DeployedModInst> deployedModInsts = new ArrayList<SM_DeployedModInst>();
	private List<SM_DeployedTrigInst> deployedTrigInsts = new ArrayList<SM_DeployedTrigInst>();
	private List<SM_DeployedTrigInst> deployedDynTrigInsts = new ArrayList<SM_DeployedTrigInst>();
	private SM_LogicalComputingNode logicalComputingNode = null;
	private Map<SM_ProtectionDomain, ArrayList<SM_Wire>> mapOfProvidedServicesToPD = new HashMap<SM_ProtectionDomain, ArrayList<SM_Wire>>();
	private List<SM_ProtectionDomain> listOfPDsCommunicateWith = new ArrayList<SM_ProtectionDomain>();

	public SM_ProtectionDomain(String name) {
		super(name);
	}

	public void addDeployedModuleInstance(SM_DeployedModInst deployedModInst) {
		deployedModInsts.add(deployedModInst);
	}

	public SM_LogicalComputingNode getLogicalComputingNode() {
		return logicalComputingNode;
	}

	public void addDeployedTriggerInstance(SM_DeployedTrigInst deployedTrigInst) {
		deployedTrigInsts.add(deployedTrigInst);
	}

	public void addDeployedDynamicTriggerInstance(SM_DeployedTrigInst deployedTrigInst) {
		deployedDynTrigInsts.add(deployedTrigInst);
	}

	public List<SM_DeployedModInst> getDeployedModInsts() {
		return deployedModInsts;
	}

	public List<SM_DeployedTrigInst> getDeployedTrigInsts() {
		return deployedTrigInsts;
	}

	public List<SM_DeployedTrigInst> getDeployedDynTrigInsts() {
		return deployedDynTrigInsts;
	}

	public void setComputingNode(SM_LogicalComputingNode logicalComputingNode) {
		this.logicalComputingNode = logicalComputingNode;
	}

	public List<SM_ComponentInstance> getComponentInstances() {
		List<SM_ComponentInstance> compInstList = new ArrayList<SM_ComponentInstance>();

		for (SM_DeployedModInst depModInst : deployedModInsts) {
			SM_ComponentInstance compInst = depModInst.getCompInstance();

			if (!compInstList.contains(compInst)) {
				compInstList.add(compInst);
			}
		}

		return compInstList;
	}

	public List<SM_ComponentImplementation> getComponentImplementations() {
		List<SM_ComponentImplementation> compImplList = new ArrayList<SM_ComponentImplementation>();

		for (SM_ComponentInstance compInst : this.getComponentInstances()) {
			if (!compImplList.contains(compInst.getImplementation())) {
				compImplList.add(compInst.getImplementation());
			}
		}

		return compImplList;
	}

	public Map<SM_ProtectionDomain, ArrayList<SM_Wire>> getMapOfProvidedServicesToPD() {
		return mapOfProvidedServicesToPD;
	}

	public List<SM_ProtectionDomain> getListOfPDsCommunicateWith() {
		return listOfPDsCommunicateWith;
	}

	public void setListOfPDsCommunicateWith(List<SM_ProtectionDomain> listOfPDsCommunicateWith) {
		this.listOfPDsCommunicateWith = listOfPDsCommunicateWith;
	}

	public void addPDsCommunicateWith(SM_ProtectionDomain pd) {
		if (!listOfPDsCommunicateWith.contains(pd)) {
			listOfPDsCommunicateWith.add(pd);
		}
	}

}
