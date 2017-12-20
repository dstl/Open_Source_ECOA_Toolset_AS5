/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.xmlprocessor;

import java.nio.file.Path;

import com.iawg.ecoa.jaxbclasses.step2bServiceQOS.RequestResponse;
import com.iawg.ecoa.jaxbclasses.step2bServiceQOS.ServiceInstanceQoS;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.qos.SM_QualityOfService;
import com.iawg.ecoa.systemmodel.qos.SM_RequestResponseQoS;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;

/**
 * 
 * @author Daniel.Clarke
 *
 */
public class XMLProc4aCompImplQOS {
	private ServiceInstanceQoS serviceInstanceQoS;
	private Path serviceQoSFile;

	public void parseFile(Path serviceQoSFile) {
		XMLFileProcessor pxfp = new XMLFileProcessor("ecoa-interface-qos-1.0.xsd", "com.iawg.ecoa.jaxbclasses.step2bServiceQOS");

		this.serviceQoSFile = serviceQoSFile;
		this.serviceInstanceQoS = (ServiceInstanceQoS) pxfp.parseFile(serviceQoSFile);
	}

	public void updateSystemModel(SystemModel systemModel, SM_ServiceInstance requiredServiceInst, SM_ComponentImplementation compImpl) {
		SM_QualityOfService qos = new SM_QualityOfService(serviceQoSFile.getFileName().toString(), requiredServiceInst);

		// Only process request-response QoS for now.
		for (Object op : serviceInstanceQoS.getOperations().getDatasAndEventsAndRequestresponses()) {
			if (op instanceof RequestResponse) {
				RequestResponse rrQOS = (RequestResponse) op;

				// Get the related rr operation.
				SM_RRServiceOp rrServiceOp = requiredServiceInst.getServiceInterface().getRROperation(rrQOS.getName());

				SM_RequestResponseQoS smRRQOS = new SM_RequestResponseQoS(rrQOS.getName(), rrQOS.getMaxResponseTime(), rrServiceOp);

				// Add the rr qos to the qos file.
				qos.getRequestResponseQoSMap().put(rrServiceOp, smRRQOS);

			}
			// TODO - currently only handle import of request response.
		}

		// Add the QoS to the component implementation
		compImpl.getRequiredQOSMap().put(requiredServiceInst, qos);

	}

}
