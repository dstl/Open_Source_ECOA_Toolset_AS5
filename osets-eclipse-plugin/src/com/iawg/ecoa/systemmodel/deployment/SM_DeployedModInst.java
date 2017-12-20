/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.deployment;

import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;

public class SM_DeployedModInst {

	private SM_ComponentInstance compInstance;
	private SM_ModuleInstance modInstance;
	private SM_ProtectionDomain protectionDomain;
	private int priority;

	public SM_DeployedModInst(SM_ComponentInstance compInst, SM_ModuleInstance modInst, SM_ProtectionDomain pd, int modulePriority) {
		compInstance = compInst;
		modInstance = modInst;
		protectionDomain = pd;
		this.priority = modulePriority;
	}

	public SM_ModuleInstance getModInstance() {
		return modInstance;
	}

	public SM_ProtectionDomain getProtectionDomain() {
		return protectionDomain;
	}

	public SM_ComponentInstance getCompInstance() {
		return compInstance;
	}

	public int getPriority() {
		return priority;
	}

}
