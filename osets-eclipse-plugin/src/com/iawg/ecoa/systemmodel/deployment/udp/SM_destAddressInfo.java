/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.deployment.udp;

public class SM_destAddressInfo {

	private String platformID;
	private String platformName;
	private String addressInfo;

	public SM_destAddressInfo(String platformID, String platformName, String addressInfo) {
		this.addressInfo = addressInfo;
		this.platformID = platformID;
		this.platformName = platformName;
	}

	String getAddressInfo() {
		return addressInfo;
	}

	String getPlatformName() {
		return platformName;
	}

	String getPlatformID() {
		return platformID;
	}
}
