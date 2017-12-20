/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.xmlprocessor;

import java.nio.file.Path;

import com.iawg.ecoa.jaxbclasses.vcbinding.DestinationPlatformType;
import com.iawg.ecoa.jaxbclasses.vcbinding.LogicalComputingPlatformType;
import com.iawg.ecoa.jaxbclasses.vcbinding.VCBinding;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class XMLProcVCBinding {
	private VCBinding vcBinding;

	public XMLProcVCBinding() {
		vcBinding = null;
	}

	public VCBinding getVCMapping() {
		return vcBinding;
	}

	public void parseFile(Path vcFile) {
		XMLFileProcessor pxfp = new XMLFileProcessor("ecoa-vcbinding-1.0.xsd", "com.iawg.ecoa.jaxbclasses.vcbinding");

		vcBinding = (VCBinding) pxfp.parseFile(vcFile);
	}

	public void updateSystemModel(SystemModel systemModel) {

		if (vcBinding != null && systemModel.getLogicalSystem() != null) {
			for (LogicalComputingPlatformType vclcp : vcBinding.getLogicalComputingPlatforms()) {
				for (SM_LogicalComputingPlatform lcp : systemModel.getLogicalSystem().getLogicalcomputingPlatforms()) {
					if (vclcp.getPlatformName().equals(lcp.getName())) {
						lcp.setReceivingAddressInfo(vclcp.getReceivingVCID().toString());

						for (DestinationPlatformType dest : vclcp.getDestinationPlatforms()) {
							lcp.addDestinationAddressInfo(dest.getPlatformID(), dest.getPlatformName(), dest.getVCID());
						}
					}
				}
			}
		}
	}
}
