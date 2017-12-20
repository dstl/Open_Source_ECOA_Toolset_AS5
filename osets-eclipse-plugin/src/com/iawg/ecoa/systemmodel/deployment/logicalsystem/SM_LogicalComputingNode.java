/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.deployment.logicalsystem;

import java.util.ArrayList;
import java.util.List;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class SM_LogicalComputingNode extends SM_Object {

	private SM_LogicalComputingPlatform logicalComputingPlatform = null;
	private List<SM_ProtectionDomain> protectionDomains = new ArrayList<SM_ProtectionDomain>();
	private boolean isLittleEndian = false;
	private String os;

	public SM_LogicalComputingNode(String name, boolean isLittleEndian, String os) {
		super(name);
		this.isLittleEndian = isLittleEndian;
		this.os = os;
	}

	public void setComputingPlatform(SM_LogicalComputingPlatform logicalComputingPlatform) {
		this.logicalComputingPlatform = logicalComputingPlatform;
	}

	public SM_LogicalComputingPlatform getLogicalComputingPlatform() {
		return logicalComputingPlatform;
	}

	public List<SM_ProtectionDomain> getProtectionDomains() {
		return protectionDomains;
	}

	public void addProtectionDomain(SM_ProtectionDomain pd) {
		this.protectionDomains.add(pd);
	}

	public boolean isLittleEndian() {
		return isLittleEndian;
	}

	public void setLittleEndian(boolean isLittleEndian) {
		this.isLittleEndian = isLittleEndian;
	}

	public String getOs() {
		return os;
	}
}
