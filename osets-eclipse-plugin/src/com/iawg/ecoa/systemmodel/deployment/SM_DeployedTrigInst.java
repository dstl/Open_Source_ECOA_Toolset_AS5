/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.deployment;

import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;

public class SM_DeployedTrigInst {
	private SM_ProtectionDomain protectionDomain;
	private SM_ComponentInstance compInstance;
	private SM_TriggerInstance trigInstance;
	private SM_DynamicTriggerInstance dynTrigInstance;
	private int priority;

	public SM_DeployedTrigInst(SM_ComponentInstance compInst, SM_TriggerInstance trigInst, SM_ProtectionDomain pd, int triggerPriority) {
		this.compInstance = compInst;
		this.trigInstance = trigInst;
		this.protectionDomain = pd;
		this.priority = triggerPriority;
	}

	public SM_DeployedTrigInst(SM_ComponentInstance compInst, SM_DynamicTriggerInstance trigInst, SM_ProtectionDomain pd, int triggerPriority) {
		this.compInstance = compInst;
		this.dynTrigInstance = trigInst;
		this.protectionDomain = pd;
		this.priority = triggerPriority;
	}

	public SM_TriggerInstance getTrigInstance() {
		return trigInstance;
	}

	public SM_DynamicTriggerInstance getDynTrigInstance() {
		return dynTrigInstance;
	}

	public boolean isDynamicTriggerInstance() {
		return (trigInstance == null) && (dynTrigInstance != null);
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
