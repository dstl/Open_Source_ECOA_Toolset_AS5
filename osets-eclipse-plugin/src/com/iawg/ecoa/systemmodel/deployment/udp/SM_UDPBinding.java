/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.deployment.udp;

import java.math.BigInteger;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class SM_UDPBinding extends SM_Object {
	private long platformID;
	private String receivingMulticastAddr;
	private BigInteger receivingPort;
	private SM_LogicalComputingPlatform relatedLCP;

	public SM_UDPBinding(String name, long platformID, String receivingMulticastAddr, BigInteger receivingPort) {
		super(name);
		this.platformID = platformID;
		this.receivingMulticastAddr = receivingMulticastAddr;
		this.receivingPort = receivingPort;
	}

	public long getPlatformID() {
		return platformID;
	}

	public String getReceivingMulticastAddr() {
		return receivingMulticastAddr;
	}

	public BigInteger getReceivingPort() {
		return receivingPort;
	}

	public void setRelatedLCP(SM_LogicalComputingPlatform lcp) {
		this.relatedLCP = lcp;
		this.relatedLCP.setRelatedUDPBinding(this);
	}

	public SM_LogicalComputingPlatform getRelatedLCP() {
		return relatedLCP;
	}

}
