/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.xmlprocessor;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.jaxbclasses.step5fUDP.Platform;
import com.iawg.ecoa.jaxbclasses.step5fUDP.UDPBinding;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;
import com.iawg.ecoa.systemmodel.deployment.udp.SM_UDPBinding;

public class XMLProc5FUDPBinding {
	private static final Logger LOGGER = LogManager.getLogger(XMLProc5FUDPBinding.class);
	private UDPBinding udpBinding;

	public void parseFile(Path udpFile) {
		XMLFileProcessor pxfp = new XMLFileProcessor("ecoa-udpbinding-1.0.xsd", "com.iawg.ecoa.jaxbclasses.step5fUDP");

		udpBinding = (UDPBinding) pxfp.parseFile(udpFile);
	}

	public void updateSystemModel(SystemModel systemModel) {
		if (udpBinding != null && systemModel.getLogicalSystem() != null) {
			for (Platform platform : udpBinding.getPlatforms()) {
				// Create a new UDP binding object.
				SM_UDPBinding udpBinding = new SM_UDPBinding(platform.getName(), platform.getPlatformId(), platform.getReceivingMulticastAddress(), platform.getReceivingPort());

				// Relate the UDP binding object to the logical computing
				// platform.
				for (SM_LogicalComputingPlatform lcp : systemModel.getLogicalSystem().getLogicalcomputingPlatforms()) {
					if (lcp.getName().equals(platform.getName())) {
						udpBinding.setRelatedLCP(lcp);
					}
				}

				if (udpBinding.getRelatedLCP() == null) {
					LOGGER.info("Processing UDPBinding - failed to find Logical Computing Platform - " + platform.getName());
					
				}
			}
		}
	}
}
