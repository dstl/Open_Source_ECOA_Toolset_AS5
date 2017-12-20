/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.xmlprocessor;

import java.nio.file.Path;

import com.iawg.ecoa.jaxbclasses.step5aLogicalSys.LogicalSystem;
import com.iawg.ecoa.jaxbclasses.step5aLogicalSys.LogicalSystem.LogicalComputingPlatform;
import com.iawg.ecoa.jaxbclasses.step5aLogicalSys.LogicalSystem.LogicalComputingPlatform.LogicalComputingNode;
import com.iawg.ecoa.jaxbclasses.step5aLogicalSys.LogicalSystem.LogicalComputingPlatform.LogicalComputingNode.Endianess;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingNode;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalSystem;

public class XMLProc5aLogicalSys {
	private LogicalSystem logicalSystem;

	public void parseFile(Path logicSysFile) {
		XMLFileProcessor pxfp = new XMLFileProcessor("ecoa-logicalsystem-1.0.xsd", "com.iawg.ecoa.jaxbclasses.step5aLogicalSys");

		logicalSystem = (LogicalSystem) pxfp.parseFile(logicSysFile);
	}

	public void updateSystemModel(SystemModel systemModel) {
		if (logicalSystem != null) {
			systemModel.setLogicalSystem(new SM_LogicalSystem(logicalSystem.getId()));

			for (LogicalComputingPlatform lcp : logicalSystem.getLogicalComputingPlatforms()) {
				SM_LogicalComputingPlatform logicalComputingPlatform = new SM_LogicalComputingPlatform(lcp.getId());

				for (LogicalComputingNode lcn : lcp.getLogicalComputingNodes()) {
					Endianess endian = lcn.getEndianess();
					boolean isLittleEndian;

					if (endian.getType().equalsIgnoreCase("BIG")) {
						isLittleEndian = false;
					} else {
						isLittleEndian = true;
					}

					SM_LogicalComputingNode logicalComputingNode = new SM_LogicalComputingNode(lcn.getId(), isLittleEndian, lcn.getOs().getName());

					logicalComputingNode.setComputingPlatform(logicalComputingPlatform);

					logicalComputingPlatform.addLogicalComputingNode(logicalComputingNode);
				}

				systemModel.getLogicalSystem().addLogicalComputingPlatform(logicalComputingPlatform);
			}
		}
	}
}
