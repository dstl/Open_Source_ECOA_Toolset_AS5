/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.deployment.logicalsystem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.udp.SM_UDPBinding;
import com.iawg.ecoa.systemmodel.deployment.udp.SM_destAddressInfo;

public class SM_LogicalComputingPlatform extends SM_Object {
	private static final Logger LOGGER = LogManager.getLogger(SM_LogicalComputingPlatform.class);

	private List<SM_LogicalComputingNode> logicalComputingNodes = new ArrayList<SM_LogicalComputingNode>();
	private String receivingAddressInfo;
	private List<SM_destAddressInfo> destPlatformAddressInfo;
	private SM_UDPBinding relatedUDPBinding;
	private Map<SM_LogicalComputingPlatform, ArrayList<SM_Wire>> mapOfProvidedServicesToPlatform = new HashMap<SM_LogicalComputingPlatform, ArrayList<SM_Wire>>();
	// Use 8 as the default (not sure this is correct assumption!)
	private int notificationMaxNumber = 8;

	public SM_LogicalComputingPlatform(String name) {
		super(name);
	}

	public void addLogicalComputingNode(SM_LogicalComputingNode logicalComputingNode) {
		logicalComputingNodes.add(logicalComputingNode);
	}

	public List<SM_LogicalComputingNode> getLogicalcomputingNodes() {
		return logicalComputingNodes;
	}

	public SM_LogicalComputingNode findLogicalComputingNode(String computingNodeName) {
		SM_LogicalComputingNode logicalComputingNode = null;

		for (SM_LogicalComputingNode lcn : logicalComputingNodes) {
			if (lcn.getName().equals(computingNodeName)) {
				logicalComputingNode = lcn;
				break;
			}
		}
		return logicalComputingNode;
	}

	public void setReceivingAddressInfo(String string) {
		this.receivingAddressInfo = string;
	}

	public String getReceivingAddressInfo() {
		return receivingAddressInfo;
	}

	public void addDestinationAddressInfo(String platformID, String platformName, String vcid) {
		// TODO Auto-generated method stub

	}

	public List<SM_destAddressInfo> getDestPlatformAddressInfo() {
		return destPlatformAddressInfo;
	}

	public void addDestPlatformAddressInfo(String platformName, String platformID, String addressInfo) {
		SM_destAddressInfo newInfo = new SM_destAddressInfo(platformName, platformID, addressInfo);

		destPlatformAddressInfo.add(newInfo);
	}

	public void setRelatedUDPBinding(SM_UDPBinding udpBinding) {
		this.relatedUDPBinding = udpBinding;
	}

	public SM_UDPBinding getRelatedUDPBinding() {
		if (this.relatedUDPBinding == null) {
			LOGGER.info("No UDP binding for Logical Computing PLatform - " + name);
			
		}

		return this.relatedUDPBinding;
	}

	public Map<SM_LogicalComputingPlatform, ArrayList<SM_Wire>> getMapOfProvidedServicesToPlatform() {
		return mapOfProvidedServicesToPlatform;
	}

	public int getNotificationMaxNumber() {
		return notificationMaxNumber;
	}

	public void setNotificationMaxNumber(BigDecimal notificationMaxNumber) {
		this.notificationMaxNumber = notificationMaxNumber.intValue();
	}

	public List<SM_ProtectionDomain> getAllProtectionDomains() {
		List<SM_ProtectionDomain> pdList = new ArrayList<SM_ProtectionDomain>();

		for (SM_LogicalComputingNode node : logicalComputingNodes) {
			for (SM_ProtectionDomain pd : node.getProtectionDomains()) {
				pdList.add(pd);
			}
		}

		return pdList;
	}

	public SM_LogicalComputingNode getLogicalComputingNodeByName(String lcnName) {
		for (SM_LogicalComputingNode lcn : logicalComputingNodes) {
			if (lcn.getName().equals(lcnName)) {
				return lcn;
			}
		}

		LOGGER.info("Error - failed to find logical computing node " + lcnName + " in deployment.");
		
		return null;
	}

}
