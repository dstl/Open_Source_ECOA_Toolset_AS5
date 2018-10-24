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

import javax.xml.namespace.QName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.jaxbclasses.step2aCompDefs.ComponentType;
import com.iawg.ecoa.jaxbclasses.step2aCompDefs.ComponentType.Service;
import com.iawg.ecoa.jaxbclasses.step2aCompDefs.ComponentTypeReference;
import com.iawg.ecoa.jaxbclasses.step2aCompDefs.Property;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentdefinition.SM_ComponentType;
import com.iawg.ecoa.systemmodel.componentdefinition.SM_ComponentTypeProperty;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ProvidedServiceInstance;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_RequiredServiceInstance;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;

/**
 * This class processes a step 2 XML file that defines all of the component
 * types used in an ECOA project.
 *
 * @author Shaun Cullimore
 */
public class XMLProc2aCompDefs {
	private static final Logger LOGGER = LogManager.getLogger(XMLProc2aCompDefs.class);
	private List<ECOAFile> componentDefinitions = new ArrayList<ECOAFile>();

	private String getComponentNameFromFilename(Path name) {
		String sa[] = name.getFileName().toString().split("/");

		// The last sub-string
		if ((sa[sa.length - 1]).endsWith(".componentType")) {
			String temp = sa[sa.length - 1];
			return temp.substring(0, temp.length() - 14);
		} else {
			return null;
		}
	}

	private List<Property> getProperties(ComponentType ct) {
		List<Property> properties = new ArrayList<Property>();

		List<Object> lo = ct.getServicesAndReferencesAndProperties();
		for (Object o : lo) {
			if (o instanceof Property) {
				properties.add((Property) o);
			}
		}

		return properties;
	}

	private List<ComponentType.Service> getProvidedServices(ComponentType ct) {
		List<ComponentType.Service> providedServices = new ArrayList<ComponentType.Service>();

		List<Object> lo = ct.getServicesAndReferencesAndProperties();
		for (Object o : lo) {
			if (o instanceof ComponentType.Service) {
				providedServices.add((ComponentType.Service) o);
			}
		}
		return providedServices;
	}

	private List<ComponentTypeReference> getRequiredServices(ComponentType ct) {
		List<ComponentTypeReference> requiredServices = new ArrayList<ComponentTypeReference>();

		List<Object> lo = ct.getServicesAndReferencesAndProperties();
		for (Object o : lo) {
			if (o instanceof ComponentTypeReference) {
				requiredServices.add((ComponentTypeReference) o);
			}
		}
		return requiredServices;
	}

	public void parseFile(Path componentDefFile) {
		XMLFileProcessor pxfp = new XMLFileProcessor("sca/sca-core-1.1-cd06-subset.xsd", "com.iawg.ecoa.jaxbclasses.step2aCompDefs");

		ComponentType componentType = (ComponentType) pxfp.parseFile(componentDefFile);
		ECOAFile componentTypeFile = new ECOAFile(componentDefFile, componentType);
		componentDefinitions.add(componentTypeFile);
	}

	public void updateSystemModel(SystemModel systemModel) {
		for (ECOAFile compTypeFile : componentDefinitions) {
			ComponentType ct = (ComponentType) compTypeFile.getObject();
			String compTypeName = getComponentNameFromFilename(compTypeFile.getName());

			SM_ComponentType newCompType = new SM_ComponentType(compTypeName);
			systemModel.getComponentDefinitions().addComponentType(newCompType);

			for (Service s : getProvidedServices(ct)) {
				String serviceName = s.getInterface().getValue().getSyntax();
				if (systemModel.getServiceDefinitions().serviceExists(serviceName)) {
					SM_ServiceInstance serviceInstance = new SM_ProvidedServiceInstance(s.getName(), systemModel.getServiceDefinitions().getService(serviceName));
					newCompType.addServiceInstance(s.getName(), serviceInstance);
				} else {
					LOGGER.info("In definition of: " + compTypeName);
					LOGGER.info("Definition of service " + serviceName + " does not exist");
					
				}
			}

			for (ComponentTypeReference r : getRequiredServices(ct)) {
				String referenceName = r.getInterface().getValue().getSyntax();
				if (systemModel.getServiceDefinitions().serviceExists(referenceName)) {
					SM_ServiceInstance serviceInstance = new SM_RequiredServiceInstance(r.getName(), systemModel.getServiceDefinitions().getService(referenceName));
					newCompType.addReferenceInstance(r.getName(), serviceInstance);
				} else {
					LOGGER.info("In definition of: " + compTypeName);
					LOGGER.info("Definition of reference " + referenceName + " does not exist");
					
				}
			}

			for (Property p : getProperties(ct)) {
				QName name = new QName("http://www.ecoa.technology/sca", "type", "ecoa-sca");
				String typeName = p.getOtherAttributes().get(name);

				newCompType.addProperty(new SM_ComponentTypeProperty(p.getName(), systemModel.getTypes().getType(typeName), newCompType));
			}
		}
	}
}
