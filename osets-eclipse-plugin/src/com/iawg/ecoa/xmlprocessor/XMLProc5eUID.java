/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.xmlprocessor;

import java.nio.file.Path;

import com.iawg.ecoa.jaxbclasses.step5eUID.ID;
import com.iawg.ecoa.jaxbclasses.step5eUID.IDMap;
import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;

public class XMLProc5eUID {
	private IDMap uidMap;

	public void parseFile(Path uidFile) {
		XMLFileProcessor pxfp = new XMLFileProcessor("ecoa-uid-1.0.xsd", "com.iawg.ecoa.jaxbclasses.step5eUID");

		uidMap = (IDMap) pxfp.parseFile(uidFile);
	}

	public void updateSystemModel(SystemModel systemModel) {
		if (uidMap != null && systemModel.getFinalAssembly() != null) {
			// Update the system model.
			for (ID uid : uidMap.getIDS()) {
				if (uid.getKey().contains(":")) {
					// This key is for a service UID

					// Get the 5 parts of the string
					String sourceCompInstName = uid.getKey().substring(0, uid.getKey().indexOf("/"));
					String sourceServInstName = uid.getKey().substring(uid.getKey().indexOf("/") + 1, uid.getKey().indexOf(":"));

					String destCompInstName = uid.getKey().substring(uid.getKey().indexOf(":") + 1, uid.getKey().lastIndexOf("/"));
					String destServInstName = uid.getKey().substring(uid.getKey().lastIndexOf("/") + 1, uid.getKey().lastIndexOf(":"));

					String opName = uid.getKey().substring(uid.getKey().lastIndexOf(":") + 1, uid.getKey().length());

					// Add the service operation UID to the wire.
					for (SM_Wire wire : systemModel.getFinalAssembly().getWires()) {
						if (wire.getSource().getName().equals(sourceCompInstName)) {
							if (wire.getSourceOp().getName().equals(sourceServInstName)) {
								if (wire.getTarget().getName().equals(destCompInstName)) {
									if (wire.getTargetOp().getName().equals(destServInstName)) {
										for (SM_Object operation : wire.getSourceOp().getServiceInterface().getOps()) {
											if (operation.getName().equals(opName)) {
												wire.addUID(uid.getValue(), operation, uid.getKey());
											}
										}
									}
								}
							}
						}
					}
				} else if (uid.getKey().contains("/")) {
					// This key is for a service instance (used for service
					// availability)

					// Get the 2 parts of the string
					String compInstName = uid.getKey().substring(0, uid.getKey().indexOf("/"));
					String providedServInstName = uid.getKey().substring(uid.getKey().indexOf("/") + 1, uid.getKey().length());

					// Add the service instance UID to the component instance.
					for (SM_ComponentInstance compInst : systemModel.getFinalAssembly().getComponentInstances()) {
						if (compInst.getName().equals(compInstName)) {
							for (SM_ServiceInstance servInst : compInst.getCompType().getServiceInstancesList()) {
								if (servInst.getName().equals(providedServInstName)) {
									compInst.addServiceInstanceUID(uid.getValue(), servInst);
								}
							}
						}
					}
				} else {
					// This key is for a composite.

					// Add the composite UID to the correct composite.
					// TODO - at the moment the system model can only hold a
					// single composite (this may be incorrect)
					if (systemModel.getFinalAssembly().getName().equals(uid.getKey())) {
						systemModel.getFinalAssembly().setUID(uid.getValue());
					}
				}
			}
		}
	}
}
