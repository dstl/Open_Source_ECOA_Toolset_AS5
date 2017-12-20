/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.deployment.logicalsystem;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.systemmodel.SM_Object;

public class SM_LogicalSystem extends SM_Object {
	private static final Logger LOGGER = LogManager.getLogger(SM_LogicalSystem.class);

	private List<SM_LogicalComputingPlatform> logicalComputingPlatforms = new ArrayList<SM_LogicalComputingPlatform>();

	public SM_LogicalSystem(String name) {
		super(name);
	}

	public void addLogicalComputingPlatform(SM_LogicalComputingPlatform logicalComputingPlatform) {
		logicalComputingPlatforms.add(logicalComputingPlatform);
	}

	public List<SM_LogicalComputingPlatform> getLogicalcomputingPlatforms() {
		return logicalComputingPlatforms;
	}

	public boolean exists(String logicalSystemName) {
		if (name != null) {
			return name.equals(logicalSystemName);
		} else {
			return false;
		}
	}

	public SM_LogicalComputingNode findLogicalComputingNode(String nodeName) {
		SM_LogicalComputingNode logicalComputingNode = null;

		for (SM_LogicalComputingPlatform lcp : logicalComputingPlatforms) {

			logicalComputingNode = lcp.findLogicalComputingNode(nodeName);

			if (logicalComputingNode != null)
				break;
		}
		return logicalComputingNode;
	}

	public SM_LogicalComputingPlatform getLogicalcomputingPlatformByName(String lcpName) {
		for (SM_LogicalComputingPlatform lcp : logicalComputingPlatforms) {
			if (lcp.getName().equals(lcpName)) {
				return lcp;
			}
		}

		LOGGER.info("Error - failed to find logical computing platform " + lcpName + " in deployment.");
		
		return null;
	}

}
