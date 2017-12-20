/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.xmlprocessor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.jaxbclasses.step5cDeployment.Deployment;
import com.iawg.ecoa.jaxbclasses.step5cDeployment.PlatformConfiguration;
import com.iawg.ecoa.jaxbclasses.step5cDeployment.ProtectionDomain;
import com.iawg.ecoa.jaxbclasses.step5cDeployment.ProtectionDomain.DeployedModuleInstance;
import com.iawg.ecoa.jaxbclasses.step5cDeployment.ProtectionDomain.DeployedTriggerInstance;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedModInst;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedTrigInst;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingNode;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

/**
 * This class processes a step 5c XML file that defines an ECOA system
 * deployment.
 * 
 * @author Shaun Cullimore
 */
public class XMLProc5cDeployment {
	private static final Logger LOGGER = LogManager.getLogger(XMLProc5cDeployment.class);
	private static final String SEP_PATTERN_01 = " does not exist";
	public static List<Deployment> deployments = new ArrayList<Deployment>();

	public void parseFile(Path deploymentFile) {
		XMLFileProcessor pxfp = new XMLFileProcessor("ecoa-deployment-1.0.xsd", "com.iawg.ecoa.jaxbclasses.step5cDeployment");

		Deployment deployment = (Deployment) pxfp.parseFile(deploymentFile);
		deployments.clear();
		deployments.add(deployment);
	}

	private void processDeployedModInsts(SystemModel systemModel, SM_ProtectionDomain protectionDomain, DeployedModuleInstance dmi) {
		if (systemModel.getFinalAssembly().getInstanceByName(dmi.getComponentName()) != null) {
			SM_ComponentInstance compInst = systemModel.getFinalAssembly().getInstanceByName(dmi.getComponentName());

			if (compInst.getImplementation().getModuleInstance(dmi.getModuleInstanceName()) != null) {
				SM_ModuleInstance modInst = compInst.getImplementation().getModuleInstance(dmi.getModuleInstanceName());
				SM_DeployedModInst depModInst = new SM_DeployedModInst(compInst, modInst, protectionDomain, dmi.getModulePriority().intValue());
				protectionDomain.addDeployedModuleInstance(depModInst);
				compInst.addDeployedModInst(depModInst);
				compInst.setProtectionDomain(protectionDomain);
			} else {
				LOGGER.info("Error processing deployment. Module Instance " + dmi.getModuleInstanceName() + SEP_PATTERN_01);

			}
		} else {
			LOGGER.info("Error processing deployment. Component " + dmi.getComponentName() + SEP_PATTERN_01);

		}
	}

	private void processDeployedTrigInsts(SystemModel systemModel, SM_ProtectionDomain protectionDomain, DeployedTriggerInstance dti) {
		if (systemModel.getFinalAssembly().getInstanceByName(dti.getComponentName()) != null) {
			SM_ComponentInstance compInst = systemModel.getFinalAssembly().getInstanceByName(dti.getComponentName());

			if (compInst.getImplementation().getTriggerInstanceByName(dti.getTriggerInstanceName()) != null) {
				SM_TriggerInstance trigInst = compInst.getImplementation().getTriggerInstanceByName(dti.getTriggerInstanceName());
				SM_DeployedTrigInst depTrigInst = new SM_DeployedTrigInst(compInst, trigInst, protectionDomain, dti.getTriggerPriority().intValue());
				protectionDomain.addDeployedTriggerInstance(depTrigInst);
				compInst.addDeployedTrigInst(depTrigInst);
				compInst.setProtectionDomain(protectionDomain);
			} else if (compInst.getImplementation().getDynamicTriggerInstanceByName(dti.getTriggerInstanceName()) != null) {
				SM_DynamicTriggerInstance dynTrigInst = compInst.getImplementation().getDynamicTriggerInstanceByName(dti.getTriggerInstanceName());
				SM_DeployedTrigInst depTrigInst = new SM_DeployedTrigInst(compInst, dynTrigInst, protectionDomain, dti.getTriggerPriority().intValue());
				protectionDomain.addDeployedDynamicTriggerInstance(depTrigInst);
				compInst.addDeployedTrigInst(depTrigInst);
				compInst.setProtectionDomain(protectionDomain);
			} else {
				LOGGER.info("Error processing deployment. Trigger Instance " + dti.getTriggerInstanceName() + SEP_PATTERN_01);

			}
		} else {
			LOGGER.info("Error processing deployment. Component " + dti.getComponentName() + SEP_PATTERN_01);

		}
	}

	public void updateSystemModel(SystemModel systemModel) {
		if (deployments != null) {
			// There should only be one deployment?!
			// May need to change this if it's not the case...
			if (deployments.size() == 1) {
				for (Deployment deployment : deployments) {
					if (systemModel.getFinalAssembly().exists(deployment.getFinalAssembly())) {
						// Link the deployment to the final assembly.
						systemModel.getDeployment().setAssembly(systemModel.getFinalAssembly());

						if (systemModel.getLogicalSystem().exists(deployment.getLogicalSystem())) {
							// Link the deployment to the logical system.
							systemModel.getDeployment().setLogicalSystem(systemModel.getLogicalSystem());

							processProtectionDomains(systemModel, deployment);

							processPlatformConfiguration(systemModel, deployment);
						} else {
							LOGGER.info("Error processing deployment. Logical System schema - " + deployment.getLogicalSystem() + SEP_PATTERN_01);

						}
					} else {
						LOGGER.info("Error processing deployment. Final Assembly schema - " + deployment.getFinalAssembly() + SEP_PATTERN_01);

					}
				}

				// Generate a map of services a platform provides to other
				// platforms in the system.
				generateProvidedServicesByPlatform(systemModel);

				// Generate a map of services a protection domain provides to
				// other protection domains in the system.
				generateProvidedServicesByPD(systemModel);

				// Generate a list of all protection domains we need to
				// communicate with
				generateListOfPDsCommunicateWith(systemModel);

			} else {
				LOGGER.info("Error processing deployment. Either it does not exist, or there are multiple deployment.xml files");

			}
		}
	}

	private void processPlatformConfiguration(SystemModel systemModel, Deployment deployment) {
		for (PlatformConfiguration pc : deployment.getPlatformConfigurations()) {
			boolean lcpFound = false;

			for (SM_LogicalComputingPlatform lcp : systemModel.getLogicalSystem().getLogicalcomputingPlatforms()) {
				if (lcp.getName().equalsIgnoreCase(pc.getComputingPlatform())) {
					lcpFound = true;
					// Set the notification max number.
					lcp.setNotificationMaxNumber(pc.getNotificationMaxNumber());
				}
			}

			if (!lcpFound) {

				LOGGER.info("Error processing deployment - platform configuration for unknown platform: " + pc.getComputingPlatform());

			}
		}
	}

	private void processProtectionDomains(SystemModel systemModel, Deployment deployment) {
		for (ProtectionDomain pd : deployment.getProtectionDomains()) {
			// Create a protectionDomain object
			SM_ProtectionDomain protectionDomain = new SM_ProtectionDomain(pd.getName());

			// TODO - should eventually be able to remove this check as the
			// intention is to make the executeOn->platform a required attribute
			// in the XML schema
			if (pd.getExecuteOn().getComputingPlatform() == null) {
				LOGGER.info("ERROR - missing \"computingPlatform\" attribute in \"executeOn\" element of deployment.");
				LOGGER.info("Note this is an optional attribute in the current schema, but the intention is to change it to a mandatory attribute in a future revision");
			}

			SM_LogicalComputingPlatform compPlat = systemModel.getLogicalSystem().getLogicalcomputingPlatformByName(pd.getExecuteOn().getComputingPlatform());
			SM_LogicalComputingNode computingNode = compPlat.getLogicalComputingNodeByName(pd.getExecuteOn().getComputingNode());

			if (computingNode != null) {
				protectionDomain.setComputingNode(computingNode);

				for (Object deployedObj : pd.getDeployedModuleInstancesAndDeployedTriggerInstances()) {
					// Process deployed Module Instances
					if (deployedObj instanceof DeployedModuleInstance) {
						processDeployedModInsts(systemModel, protectionDomain, (DeployedModuleInstance) deployedObj);
					}
					// Process deployed Trigger Instances
					else if (deployedObj instanceof DeployedTriggerInstance) {
						processDeployedTrigInsts(systemModel, protectionDomain, (DeployedTriggerInstance) deployedObj);
					}
				}

				// Add the protection domain to the deployment object
				systemModel.getDeployment().addPD(protectionDomain);

				computingNode.addProtectionDomain(protectionDomain);
			} else {
				LOGGER.info("Error processing deployment. Computing Node - " + pd.getExecuteOn().getComputingNode() + SEP_PATTERN_01);

			}
		}
	}

	private void generateProvidedServicesByPlatform(SystemModel systemModel) {
		for (SM_LogicalComputingPlatform lcp : systemModel.getLogicalSystem().getLogicalcomputingPlatforms()) {
			for (SM_ProtectionDomain pd : lcp.getAllProtectionDomains()) {
				for (SM_ComponentInstance ci : pd.getComponentInstances()) {
					for (SM_Wire wire : ci.getTargetWires()) {
						SM_LogicalComputingPlatform remotePlatform = wire.getSource().getProtectionDomain().getLogicalComputingNode().getLogicalComputingPlatform();

						if (lcp != remotePlatform) {
							// We provide a service to this remote platform -
							// add it to the map / update it!
							ArrayList<SM_Wire> listOfProvidedWires = lcp.getMapOfProvidedServicesToPlatform().get(remotePlatform);

							if (listOfProvidedWires != null) {
								// Already added this platform to the map -
								// update it!
								listOfProvidedWires.add(wire);
							} else {
								// This is the first service we provide to the
								// platform - create a new list
								listOfProvidedWires = new ArrayList<SM_Wire>();
								listOfProvidedWires.add(wire);
								lcp.getMapOfProvidedServicesToPlatform().put(remotePlatform, listOfProvidedWires);
							}
						}
					}
				}
			}
		}
	}

	private void generateProvidedServicesByPD(SystemModel systemModel) {
		for (SM_LogicalComputingPlatform lcp : systemModel.getLogicalSystem().getLogicalcomputingPlatforms()) {
			for (SM_LogicalComputingNode lcn : lcp.getLogicalcomputingNodes()) {
				for (SM_ProtectionDomain pd : lcn.getProtectionDomains()) {
					for (SM_ComponentInstance ci : pd.getComponentInstances()) {
						for (SM_Wire wire : ci.getTargetWires()) {
							SM_ProtectionDomain remotePD = wire.getSource().getProtectionDomain();

							if (wire.getTarget().getProtectionDomain() != remotePD) {
								// We provide a service to this remote
								// protection domain - add it to the map /
								// update it!
								ArrayList<SM_Wire> listOfProvidedWires = pd.getMapOfProvidedServicesToPD().get(remotePD);

								if (listOfProvidedWires != null) {
									// Already added this platform to the map -
									// update it!
									listOfProvidedWires.add(wire);
								} else {
									// This is the first service we provide to
									// the platform - create a new list
									listOfProvidedWires = new ArrayList<SM_Wire>();
									listOfProvidedWires.add(wire);
									pd.getMapOfProvidedServicesToPD().put(remotePD, listOfProvidedWires);
								}
							}
						}
					}
				}
			}
		}
	}

	private void generateListOfPDsCommunicateWith(SystemModel systemModel) {
		for (SM_LogicalComputingPlatform lcp : systemModel.getLogicalSystem().getLogicalcomputingPlatforms()) {
			for (SM_LogicalComputingNode lcn : lcp.getLogicalcomputingNodes()) {
				for (SM_ProtectionDomain pd : lcn.getProtectionDomains()) {
					for (SM_ComponentInstance ci : pd.getComponentInstances()) {
						for (SM_Wire wire : ci.getTargetWires()) {
							SM_ProtectionDomain remotePD = wire.getSource().getProtectionDomain();

							if (wire.getTarget().getProtectionDomain() != remotePD) {
								// Only add if PD is in the same platform!
								if (remotePD.getLogicalComputingNode().getLogicalComputingPlatform() == lcp) {
									pd.addPDsCommunicateWith(remotePD);
								}
							}
						}
						for (SM_Wire wire : ci.getSourceWires()) {
							SM_ProtectionDomain remotePD = wire.getTarget().getProtectionDomain();

							if (wire.getSource().getProtectionDomain() != remotePD) {
								// Only add if PD is in the same platform!
								if (remotePD.getLogicalComputingNode().getLogicalComputingPlatform() == lcp) {
									pd.addPDsCommunicateWith(remotePD);
								}
							}
						}
					}
				}
			}
		}
	}

}
