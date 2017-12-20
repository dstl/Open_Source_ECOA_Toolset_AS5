/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.xmlprocessor;

import java.math.BigInteger;
import java.nio.file.Path;

import javax.xml.namespace.QName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.jaxbclasses.step5bFinalAssembly.Component;
import com.iawg.ecoa.jaxbclasses.step5bFinalAssembly.Composite;
import com.iawg.ecoa.jaxbclasses.step5bFinalAssembly.Instance;
import com.iawg.ecoa.jaxbclasses.step5bFinalAssembly.Property;
import com.iawg.ecoa.jaxbclasses.step5bFinalAssembly.PropertyValue;
import com.iawg.ecoa.jaxbclasses.step5bFinalAssembly.Value;
import com.iawg.ecoa.jaxbclasses.step5bFinalAssembly.Wire;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.assembly.SM_AssemblyPropertyValue;
import com.iawg.ecoa.systemmodel.assembly.SM_AssemblySchema;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstanceProperty;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentdefinition.SM_ComponentTypeProperty;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

/**
 * Class to represent a parsed step 5b XML file.
 * 
 * @author Shaun Cullimore
 */
@SuppressWarnings("unused")
public class XMLProc5bFinalAssembly extends XMLProc35bGenericAssembly {
	private static final Logger LOGGER = LogManager.getLogger(XMLProc5bFinalAssembly.class);
	private static final String SEP_PATTERN_41 = "Error in creating wire with:";
	private Composite composite;

	public void parseFile(Path assySchemaFile) {
		XMLFileProcessor pxfp = new XMLFileProcessor("sca/ecoa-sca-1.0.xsd", "com.iawg.ecoa.jaxbclasses.step5bFinalAssembly");

		composite = (Composite) pxfp.parseFile(assySchemaFile);

		// Get the file name minus the impl.composite.xml
		String compositeName = assySchemaFile.getFileName().toString().substring(0, assySchemaFile.getFileName().toString().indexOf("."));

		if (!composite.getName().equals(compositeName)) {
			LOGGER.info("Error processing - " + assySchemaFile);
			LOGGER.info("The csa:composite name attribute does not match the filename");

		}

	}

	public void updateSystemModel(SystemModel systemModel) {
		if (composite != null) {
			// Set the name of the assembly composite
			systemModel.setFinalAssembly(new SM_AssemblySchema(composite.getName()));

			for (Object obj : composite.getServicesAndPropertiesAndComponents()) {
				if (obj instanceof Property) {
					Property prop = (Property) obj;

					QName name = new QName("http://www.ecoa.technology/sca", "type", "ecoa-sca");

					// TODO - should only be one value specified - should add
					// some validation to this?
					for (Object item : prop.getContent()) {
						if (item instanceof Value) {
							Value value = (Value) item;
							String filename = (String) value.getContent().get(0);
							systemModel.getFinalAssembly().addPropertyValue(new SM_AssemblyPropertyValue(prop.getName(), filename));
						}
					}
				}

				// Process Component Instances
				if (obj instanceof Component) {
					Component comp = (Component) obj;
					String instanceName = comp.getName();
					Instance inst = (Instance) comp.getImplementation().getValue();
					String compImplName = null;

					if (inst.getImplementation() != null) {
						compImplName = inst.getImplementation().getName();
					} else {
						LOGGER.info("Error processing impl.composite - " + composite.getName() + " in Step5b");
						LOGGER.info("Component Instance - " + instanceName + " does not define an implementation.");

					}

					if (systemModel.getComponentImplementations().implementationExists(compImplName)) {
						SM_ComponentInstance componentInstance = new SM_ComponentInstance(instanceName, systemModel.getComponentImplementations().getImplementationByName(compImplName));

						for (Object item : comp.getServicesAndReferencesAndProperties()) {
							if (item instanceof PropertyValue) {
								PropertyValue prop = (PropertyValue) item;

								// Get the componentTypeProperty
								SM_ComponentTypeProperty compTypeProperty = componentInstance.getCompType().getPropertyByName(prop.getName());

								if (prop.getSource() != null) {
									// Reference to an assembly level property
									// value
									componentInstance.addProperty(new SM_ComponentInstanceProperty(compTypeProperty, prop.getSource(), componentInstance, systemModel.getFinalAssembly()));
								} else {
									// Component Property
									// TODO This is a bit of a hack at the
									// moment - it only gets the 1st value!
									for (Object propObj : prop.getContent()) {
										if (propObj instanceof Value) {
											Value value = (Value) propObj;
											String valStr = (String) value.getContent().get(0);
											SM_ComponentInstanceProperty compInstProperty = new SM_ComponentInstanceProperty(compTypeProperty, valStr, componentInstance);
											componentInstance.addProperty(compInstProperty);
										}
									}
								}
							}
						}
						systemModel.getFinalAssembly().addInstance(componentInstance);
					} else {
						LOGGER.info("Error in creating instance " + instanceName + " in Step5b");

					}
				}
				// Process Wires
				else if (obj instanceof Wire) {
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

					boolean eventsMulticast = ((Wire) obj).isAllEventsMulticasted();
					BigInteger rank = ((Wire) obj).getRank();

					if (systemModel.getFinalAssembly().getInstanceByName(sourceCompInstName) != null) {
						sourceCompInst = systemModel.getFinalAssembly().getInstanceByName(sourceCompInstName);
					} else {
						LOGGER.info(SEP_PATTERN_41);
						LOGGER.info("          Source component: " + sourceCompInstName);
						LOGGER.info("          Reference: " + sourceServiceInstanceName);
						LOGGER.info("          Target component: " + targetCompInstName);
						LOGGER.info("          Service: " + targetServiceInstanceName);
						LOGGER.info("Source component does not exist");

					}

					if (systemModel.getFinalAssembly().getInstanceByName(targetCompInstName) != null) {
						targetCompInst = systemModel.getFinalAssembly().getInstanceByName(targetCompInstName);
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

					SM_Wire wire = new SM_Wire(sourceCompInst, referenceInstance, targetCompInst, serviceInstance, eventsMulticast, rank.intValue());
					systemModel.getFinalAssembly().addWire(wire);
				}
			}
		}
	}
}
