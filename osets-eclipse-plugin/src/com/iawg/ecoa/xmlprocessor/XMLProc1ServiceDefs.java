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

import com.iawg.ecoa.jaxbclasses.step1ServiceDefs.Data;
import com.iawg.ecoa.jaxbclasses.step1ServiceDefs.EEventDirection;
import com.iawg.ecoa.jaxbclasses.step1ServiceDefs.Event;
import com.iawg.ecoa.jaxbclasses.step1ServiceDefs.Operation;
import com.iawg.ecoa.jaxbclasses.step1ServiceDefs.Parameter;
import com.iawg.ecoa.jaxbclasses.step1ServiceDefs.RequestResponse;
import com.iawg.ecoa.jaxbclasses.step1ServiceDefs.ServiceDefinition;
import com.iawg.ecoa.jaxbclasses.step1ServiceDefs.Use;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.servicedefinition.SM_ServiceInterface;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_DataServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp.EventDirection;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;

/**
 * This class processes a step 1 XML file that defines all of the service
 * definitions used in an ECOA project.
 */
public class XMLProc1ServiceDefs {
	private static final Logger LOGGER = LogManager.getLogger(XMLProc1ServiceDefs.class);
	private static final String SEP_PATTERN_21 = "Service ";
	private List<ECOAFile> serviceDefinitions = new ArrayList<ECOAFile>();

	private List<Data> getData(ServiceDefinition sd) {
		List<Data> dataList = new ArrayList<Data>();

		for (Operation o : sd.getOperations().getDatasAndEventsAndRequestresponses()) {
			if (o instanceof Data) {
				dataList.add((Data) o);
			}
		}
		return dataList;
	}

	private List<Event> getEvents(ServiceDefinition sd) {
		List<Event> eventList = new ArrayList<Event>();

		for (Operation o : sd.getOperations().getDatasAndEventsAndRequestresponses()) {
			if (o instanceof Event) {
				eventList.add((Event) o);
			}
		}

		return eventList;
	}

	private List<RequestResponse> getRequestResponse(ServiceDefinition sd) {
		List<RequestResponse> rrList = new ArrayList<RequestResponse>();

		for (Operation o : sd.getOperations().getDatasAndEventsAndRequestresponses()) {
			if (o instanceof RequestResponse) {
				rrList.add((RequestResponse) o);
			}
		}
		return rrList;
	}

	private String getServiceNameFromFilename(Path path) {
		String sa[] = path.getFileName().toString().split("/");

		// The last sub-string
		if ((sa[sa.length - 1]).endsWith(".interface.xml"))// &&
		// ((sa[sa.length - 1]).startsWith("svc_")))
		{
			String temp = sa[sa.length - 1];
			return temp.substring(0, temp.length() - 14);
		} else {
			return null;
		}
	}

	public void parseFile(Path serviceDefFileName) {
		XMLFileProcessor pxfp = new XMLFileProcessor("ecoa-interface-1.0.xsd", "com.iawg.ecoa.jaxbclasses.step1ServiceDefs");

		ServiceDefinition serviceDef = (ServiceDefinition) pxfp.parseFile(serviceDefFileName);
		ECOAFile serDefFile = new ECOAFile(serviceDefFileName, serviceDef);
		serviceDefinitions.add(serDefFile);
	}

	public void updateSystemModel(SystemModel systemModel) {
		for (ECOAFile sdf : serviceDefinitions) {
			String serviceName = getServiceNameFromFilename(sdf.getName());
			ServiceDefinition sd = (ServiceDefinition) sdf.getObject();

			List<SM_EventServiceOp> eventOpList = new ArrayList<SM_EventServiceOp>();
			List<SM_DataServiceOp> dataOpList = new ArrayList<SM_DataServiceOp>();
			List<SM_RRServiceOp> rrOpList = new ArrayList<SM_RRServiceOp>();

			if (systemModel.getServiceDefinitions().serviceExists(serviceName)) {
				LOGGER.info("Error service " + serviceName + " already exists");
				
			}

			for (Event e : getEvents(sd)) {
				String opName = e.getName();
				List<Parameter> paramList = e.getInputs();
				List<SM_OperationParameter> inParams = new ArrayList<SM_OperationParameter>();
				SM_EventServiceOp eventOp;
				if (paramList != null) {
					for (Parameter p : paramList) {
						if (systemModel.getTypes().typeExists(p.getType())) {
							SM_OperationParameter param = new SM_OperationParameter(p.getName(), systemModel.getTypes().getType(p.getType()));
							inParams.add(param);
						} else {
							LOGGER.info(SEP_PATTERN_21 + serviceName + ": type " + p.getType() + " does not exist.");
							
						}
					}
					if (e.getDirection() == EEventDirection.SENT_BY_PROVIDER) {
						eventOp = new SM_EventServiceOp(opName, EventDirection.SENT_BY_PROVIDER, inParams);
					} else {
						eventOp = new SM_EventServiceOp(opName, EventDirection.RECEIVED_BY_PROVIDER, inParams);
					}
					eventOpList.add(eventOp);
				}
			}

			for (Data d : getData(sd)) {
				SM_OperationParameter param;
				if (systemModel.getTypes().typeExists(d.getType())) {
					param = new SM_OperationParameter(d.getName(), systemModel.getTypes().getType(d.getType()));
					SM_DataServiceOp dataOp = new SM_DataServiceOp(d.getName(), param);
					dataOpList.add(dataOp);
				} else {
					LOGGER.info(SEP_PATTERN_21 + serviceName + ": type " + d.getType() + " does not exist.");
					
				}
			}

			for (RequestResponse rr : getRequestResponse(sd)) {
				List<Parameter> inParamList = rr.getInputs();
				List<Parameter> outParamList = rr.getOutputs();
				List<SM_OperationParameter> inParams = new ArrayList<SM_OperationParameter>();
				List<SM_OperationParameter> outParams = new ArrayList<SM_OperationParameter>();
				SM_RRServiceOp rrOp;
				if (inParamList != null) {
					for (Parameter p : inParamList) {
						if (systemModel.getTypes().typeExists(p.getType())) {
							SM_OperationParameter param = new SM_OperationParameter(p.getName(), systemModel.getTypes().getType(p.getType()));
							inParams.add(param);
						} else {
							LOGGER.info(SEP_PATTERN_21 + serviceName + ": type " + p.getType() + " does not exist.");
							
						}
					}
				}
				if (outParamList != null) {
					for (Parameter p : outParamList) {
						if (systemModel.getTypes().typeExists(p.getType())) {
							SM_OperationParameter param = new SM_OperationParameter(p.getName(), systemModel.getTypes().getType(p.getType()));
							outParams.add(param);
						} else {
							LOGGER.info(SEP_PATTERN_21 + serviceName + ": type " + p.getType() + " does not exist.");
							
						}
					}
				}
				rrOp = new SM_RRServiceOp(rr.getName(), inParams, outParams);
				rrOpList.add(rrOp);
			}

			SM_ServiceInterface serviceDefinition = new SM_ServiceInterface(serviceName, eventOpList, dataOpList, rrOpList);

			// Add any use libraries
			for (Use use : sd.getUses()) {
				if (systemModel.getTypes().namespaceExists(use.getLibrary())) {
					serviceDefinition.addUse(systemModel.getTypes().getNamespace(use.getLibrary()));
				} else {
					LOGGER.info("Use library " + use.getLibrary() + " not found in service definition" + sdf.getName());
					
				}
			}

			systemModel.getServiceDefinitions().addService(serviceDefinition);
		}
	}
}
