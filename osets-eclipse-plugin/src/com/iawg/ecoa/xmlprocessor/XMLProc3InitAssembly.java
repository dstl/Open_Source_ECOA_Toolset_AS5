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

import com.iawg.ecoa.jaxbclasses.step3InitAssembly.Component;
import com.iawg.ecoa.jaxbclasses.step3InitAssembly.Composite;
import com.iawg.ecoa.jaxbclasses.step3InitAssembly.Instance;
import com.iawg.ecoa.jaxbclasses.step3InitAssembly.Wire;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.assembly.SM_AssemblySchema;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;

/**
 * This class processes a step 3 XML file that defines all of the composites
 * used in an ECOA project.
 * 
 * @author Shaun Cullimore
 */
public class XMLProc3InitAssembly extends XMLProc35bGenericAssembly {
	private static final Logger LOGGER = LogManager.getLogger(XMLProc3InitAssembly.class);
	private static final String SEP_PATTERN_41 = "Error in creating wire with:";
	private Composite composite;

	public void parseFile(Path assySchemaFile) {
		XMLFileProcessor pxfp = new XMLFileProcessor("sca/ecoa-sca-1.0.xsd", "com.iawg.ecoa.jaxbclasses.step3InitAssembly");

		composite = (Composite) pxfp.parseFile(assySchemaFile);
	}

	public void updateSystemModel(SystemModel systemModel) {
		if (composite != null) {
			// Set the name of the assembly composite
			systemModel.setInitialAssembly(new SM_AssemblySchema(composite.getName()));

			for (Object obj : composite.getServicesAndPropertiesAndComponents()) {
				// Process components
				if (obj instanceof Component) {
					Component comp = (Component) obj;
					String instanceName = comp.getName();
					Instance inst = (Instance) comp.getImplementation().getValue();
					String compTypeName = inst.getComponentType();

					if (systemModel.getComponentDefinitions().componentTypeExists(compTypeName)) {
						SM_ComponentInstance componentInstance = new SM_ComponentInstance(instanceName, systemModel.getComponentDefinitions().getComponentType(compTypeName));
						systemModel.getInitialAssembly().addInstance(componentInstance);
					} else {
						LOGGER.info("Error in creating instance " + instanceName + " type " + compTypeName + " does not exist");
						
					}
				}

				// Process wires
				if (obj instanceof Wire) {
					SM_ComponentInstance sourceCompInst = null;
					SM_ComponentInstance targetCompInst = null;
					SM_ServiceInstance referenceInstance = null;
					SM_ServiceInstance serviceInstance = null;

					String sourceText = ((Wire) obj).getSource();
					String sourceCompInstName = getComponentName(sourceText);
					String sourceServiceInstanceName = getServiceName(sourceText);

					String targetText = ((Wire) obj).getTarget();
					String targetCompInstName = getComponentName(targetText);
					String targetServiceInstanceName = getServiceName(targetText);

					if (systemModel.getInitialAssembly().getInstanceByName(sourceCompInstName) != null) {
						sourceCompInst = systemModel.getInitialAssembly().getInstanceByName(sourceCompInstName);
					} else {
						LOGGER.info(SEP_PATTERN_41);
						LOGGER.info("          Source component: " + sourceCompInstName);
						LOGGER.info("          Reference: " + sourceServiceInstanceName);
						LOGGER.info("          Target component: " + targetCompInstName);
						LOGGER.info("          Service: " + targetServiceInstanceName);
						LOGGER.info("Source component does not exist");
						
					}

					if (systemModel.getInitialAssembly().getInstanceByName(targetCompInstName) != null) {
						targetCompInst = systemModel.getInitialAssembly().getInstanceByName(targetCompInstName);
					} else {
						LOGGER.info(SEP_PATTERN_41);
						LOGGER.info("          Source component: " + sourceCompInstName);
						LOGGER.info("          Reference: " + sourceServiceInstanceName);
						LOGGER.info("          Target component: " + targetCompInstName);
						LOGGER.info("          Target operation: " + targetServiceInstanceName);
						LOGGER.info("Target component does not exist");
						
					}

					if (systemModel.getComponentDefinitions().getComponentType(sourceCompInst.getCompType().getName()).getReferenceInstanceNames().contains(sourceServiceInstanceName)) {
						referenceInstance = systemModel.getComponentDefinitions().getComponentType(sourceCompInst.getCompType().getName()).getReferenceInstanceByName(sourceServiceInstanceName);
					} else {
						LOGGER.info(SEP_PATTERN_41);
						LOGGER.info("          Source component: " + sourceCompInstName);
						LOGGER.info("          Reference: " + sourceServiceInstanceName);
						LOGGER.info("          Target component: " + targetCompInstName);
						LOGGER.info("          service: " + targetServiceInstanceName);
						LOGGER.info("Reference does not exist in component " + sourceCompInstName);
						
					}

					if (systemModel.getComponentDefinitions().getComponentType(targetCompInst.getCompType().getName()).getServiceInstanceByName(targetServiceInstanceName) != null) {
						serviceInstance = systemModel.getComponentDefinitions().getComponentType(targetCompInst.getCompType().getName()).getServiceInstanceByName(targetServiceInstanceName);
					} else {
						LOGGER.info(SEP_PATTERN_41);
						LOGGER.info("          Source component: " + sourceCompInstName);
						LOGGER.info("          Reference: " + sourceServiceInstanceName);
						LOGGER.info("          Target component: " + targetCompInstName);
						LOGGER.info("          service: " + targetServiceInstanceName);
						LOGGER.info("Service does not exist");
						
					}

					if (serviceInstance.getServiceInterface() != referenceInstance.getServiceInterface()) {
						LOGGER.info("Services not of same type when creating wire with:");
						LOGGER.info("          Source component: " + sourceCompInstName);
						LOGGER.info("          Reference: " + sourceServiceInstanceName);
						LOGGER.info("          Target component: " + targetCompInstName);
						LOGGER.info("          service: " + targetServiceInstanceName);
						
					}

					SM_Wire wire = new SM_Wire(sourceCompInst, referenceInstance, targetCompInst, serviceInstance);
					systemModel.getInitialAssembly().addWire(wire);
				}
			}
		}
	}
}
