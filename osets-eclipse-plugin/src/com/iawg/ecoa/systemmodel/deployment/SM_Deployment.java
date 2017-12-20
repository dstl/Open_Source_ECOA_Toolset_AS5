/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.deployment;

import java.util.ArrayList;
import java.util.List;

import com.iawg.ecoa.systemmodel.assembly.SM_AssemblySchema;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalSystem;

public class SM_Deployment {
	SM_AssemblySchema assembly;
	SM_LogicalSystem logicalSystem;
	List<SM_ProtectionDomain> protectionDomains = new ArrayList<SM_ProtectionDomain>();

	public void setAssembly(SM_AssemblySchema finalAssembly) {
		this.assembly = finalAssembly;
	}

	public SM_AssemblySchema getAssembly() {
		return this.assembly;
	}

	public void setLogicalSystem(SM_LogicalSystem logicalSystem) {
		this.logicalSystem = logicalSystem;
	}

	public void addPD(SM_ProtectionDomain pd) {
		protectionDomains.add(pd);
	}

	public List<SM_ProtectionDomain> getProtectionDomains() {
		return protectionDomains;
	}
}
