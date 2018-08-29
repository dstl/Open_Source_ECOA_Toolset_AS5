/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.xmlprocessor;

import java.math.BigInteger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.ECOA_System_Model.ImplLanguage;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.ComponentImplementation;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.DataLink;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.DynamicTriggerInstance;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.Event;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.EventLink;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.ModuleImplementation;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.ModuleInstance;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.ModuleType;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.ModuleType.Operations.DataRead;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.ModuleType.Operations.EventReceived;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.ModuleType.Operations.RequestReceived;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.ModuleType.Operations.RequestSent;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.OpRef;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.OpRefActivatable;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.OpRefActivatableFifo;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.OpRefActivatingFifo;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.OpRefExternal;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.OpRefServer;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.OpRefTrigger;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.Parameter;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.PinfoValue;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.PrivatePinfo;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.PropertyValue;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.PublicPinfo;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.RequestLink;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.RequestLink.Server;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.RequestResponse;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.ServiceQoS;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.TriggerInstance;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.Use;
import com.iawg.ecoa.jaxbclasses.step4aCompImpl.VersionedData;
import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentdefinition.SM_ComponentType;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerType;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstanceProperty;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleType;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleTypeProperty;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_Pinfo;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_PinfoValue;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_VDRepository;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_DataLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_ReaderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_ReaderModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_ReaderService;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_WriterModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_WriterService;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverDynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverService;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderDynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderExternal;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderService;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ClientModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ClientService;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_RequestLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ServerInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ServerModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ServerService;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataReadOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataWrittenOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventReceivedOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventSentOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestReceivedOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestSentOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_DataServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;

/**
 * This class processes a step 4a XML file that defines all of the module
 * implementations used in an ECOA project.
 * 
 * @author Shaun Cullimore
 */
public class XMLProc4aCompImpl {
	private static final Logger LOGGER = LogManager.getLogger(XMLProc4aCompImpl.class);
	private static final String SEP_PATTERN_41 = "moduleInstance";
	private List<ECOAFile> componentImplementations = new ArrayList<ECOAFile>();
	private SystemModel systemModel;
	private Integer repoID = 1;

	public XMLProc4aCompImpl(SystemModel systemModel) {
		this.systemModel = systemModel;
	}

	private void checkReaderNotInOtherDataLinks(SM_ComponentImplementation compImpl, SM_Object readerInstance, SM_Object readerOp) {
		for (SM_DataLink dlink : compImpl.getDataLinks()) {
			if (dlink.getReaders() != null) {
				for (SM_ReaderInterface rlink : dlink.getReaders()) {

					if (rlink.getReaderInst() == readerInstance && rlink.getReaderOp() == readerOp) {
						LOGGER.info("ERROR - a data reader has appeared in 2 links!");
						LOGGER.info("Reader is - " + readerInstance.getName() + " " + readerOp.getName());
						
					}
				}
			}
		}
	}

	private void checkWriterNotInOtherDataLinks(SM_ComponentImplementation compImpl, SM_Object writerInstance, SM_Object writerOp) {
		for (SM_DataLink dlink : compImpl.getDataLinks()) {
			if (dlink.getWriter() != null) {
				if (dlink.getWriter().getWriterInst() == writerInstance && dlink.getWriter().getWriterOp() == writerOp) {
					LOGGER.info("ERROR - a data writer has appeared in 2 links");
					LOGGER.info("Writer is - " + writerInstance.getName() + " " + writerOp.getName());
					
				}
			}
		}
	}

	private String getComponentNameByFilename(Path name) {
		String sa[] = name.getFileName().toString().split("/");

		// The last sub-string
		if ((sa[sa.length - 1]).endsWith(".impl.xml")) {
			String temp = sa[sa.length - 1];
			return temp.substring(0, temp.length() - 9);
		} else {
			return null;
		}
	}

	public void parseFile(Path compImpFile) {
		XMLFileProcessor pxfp = new XMLFileProcessor("ecoa-implementation-1.0.xsd", "com.iawg.ecoa.jaxbclasses.step4aCompImpl");

		ComponentImplementation componentImp = (ComponentImplementation) pxfp.parseFile(compImpFile);
		ECOAFile componentImpFile = new ECOAFile(compImpFile, componentImp);
		componentImplementations.add(componentImpFile);
	}

	private void processClients(SM_ComponentImplementation compImpl, RequestLink requestLink, SM_RequestLink smRequestLink) {
		for (OpRef clientOpRef : requestLink.getClients().getServicesAndModuleInstances()) {
			if (clientOpRef instanceof OpRefActivatable) {
				SM_ModuleInstance modInstance = compImpl.getModuleInstance(clientOpRef.getInstanceName());

				SM_RequestSentOp requestOp = modInstance.getModuleType().getRequestSentOpByName(clientOpRef.getOperationName());

				// Add the client to the request link.
				smRequestLink.addClient(new SM_ClientModuleInstance(modInstance, requestOp));

				// Add the link to the module instance
				modInstance.addRequestLink(smRequestLink);
			} else {
				// Process any required services that are clients (type = OpRef)
				SM_ServiceInstance serviceInstance = compImpl.getCompType().getServiceInstanceByName(clientOpRef.getInstanceName());

				SM_RRServiceOp rrServiceOp = serviceInstance.getServiceInterface().getRROperation(clientOpRef.getOperationName());

				// Add the client to the request link.
				smRequestLink.addClient(new SM_ClientService(serviceInstance, rrServiceOp));
			}
		}
	}

	private void processDataLink(SM_ComponentImplementation compImpl, DataLink dataLink) {
		SM_DataLink smDataLink = new SM_DataLink(compImpl);

		// Add any writers
		processWriters(compImpl, dataLink, smDataLink);

		// Add any readers
		processReaders(compImpl, dataLink, smDataLink);
	}

	private void processEventLink(SM_ComponentImplementation compImpl, EventLink eventLink) {
		// Create a new event link
		SM_EventLink smEventLink = new SM_EventLink(compImpl);

		// Add any senders
		processSenders(compImpl, eventLink, smEventLink);

		// Add any receivers
		processReceivers(compImpl, eventLink, smEventLink);
	}

	private void processModInstPinfo(ModuleInstance min, SM_ModuleInstance inst) {
		if (min.getPinfo().getPublicPinfos() != null) {
			for (PinfoValue pinfoValue : min.getPinfo().getPublicPinfos()) {
				// Get the module type pinfo associated with this value.
				SM_Pinfo mtPinfo = inst.getModuleType().getPublicPinfo(pinfoValue.getName());
				inst.addPublicPinfoValue(new SM_PinfoValue(mtPinfo, pinfoValue.getValue(), inst));
			}
		}

		if (min.getPinfo().getPrivatePinfos() != null) {
			for (PinfoValue pinfoValue : min.getPinfo().getPrivatePinfos()) {
				// Get the module type pinfo associated with this value.
				SM_Pinfo mtPinfo = inst.getModuleType().getPrivatePinfo(pinfoValue.getName());
				inst.addPrivatePinfoValue(new SM_PinfoValue(mtPinfo, pinfoValue.getValue(), inst));
			}
		}
	}

	private void processModTypePinfo(ModuleType mt, SM_ModuleType moduleType) {
		if (mt.getPinfo().getPublicPinfos() != null) {
			for (PublicPinfo pinfo : mt.getPinfo().getPublicPinfos()) {
				moduleType.addPublicPinfo(new SM_Pinfo(pinfo.getName(), false, BigInteger.valueOf(0), false));
			}
		}

		if (mt.getPinfo().getPrivatePinfos() != null) {
			for (PrivatePinfo pinfo : mt.getPinfo().getPrivatePinfos()) {
				if (pinfo.isWriteAccess()) {
					moduleType.addPrivatePinfo(new SM_Pinfo(pinfo.getName(), true, pinfo.getCapacity(), true));
				} else {
					moduleType.addPrivatePinfo(new SM_Pinfo(pinfo.getName(), false, pinfo.getCapacity(), true));
				}
			}
		}
	}

	private void processReaders(SM_ComponentImplementation compImpl, DataLink dataLink, SM_DataLink smDataLink) {
		for (OpRef readerOpRef : dataLink.getReaders().getServicesAndModuleInstances()) {
			// Determine if this reader is a module instance within this
			// component
			// or a provided service.
			if (readerOpRef instanceof OpRefActivatableFifo) {
				SM_ModuleInstance modInstance = compImpl.getModuleInstance(readerOpRef.getInstanceName());

				SM_DataOp dataOp = modInstance.getModuleType().getDataReaderOpByName(readerOpRef.getOperationName());

				SM_ReaderModuleInstance reader = new SM_ReaderModuleInstance(modInstance, dataOp, ((OpRefActivatableFifo) readerOpRef).getFifoSize());
				smDataLink.getVDRepo().addReader(reader);

				// Add to the list of readers
				checkReaderNotInOtherDataLinks(compImpl, modInstance, dataOp);
				smDataLink.addReader(reader);
				// Add the link to the module instance
				modInstance.addDataLink(smDataLink);
			} else {
				// Process any provided services that are readers (type = OpRef)
				SM_ServiceInstance serviceInstance = compImpl.getCompType().getServiceInstanceByName(readerOpRef.getInstanceName());

				SM_DataServiceOp dataServiceOp = serviceInstance.getServiceInterface().getDataOperation(readerOpRef.getOperationName());

				SM_ReaderService reader = new SM_ReaderService(serviceInstance, dataServiceOp);
				smDataLink.getVDRepo().addReader(reader);

				// Add to the list of readers
				checkReaderNotInOtherDataLinks(compImpl, serviceInstance, dataServiceOp);
				smDataLink.addReader(reader);
			}
		}

	}

	private void processReceivers(SM_ComponentImplementation compImpl, EventLink eventLink, SM_EventLink smEventLink) {
		for (JAXBElement<? extends OpRef> jbElement : eventLink.getReceivers().getServicesAndReferencesAndModuleInstances()) {
			// Determine if this receiver is a module instance (or trigger
			// instance)
			// within this component or a required service or a provided service
			if (jbElement.getName().getLocalPart().equals(SEP_PATTERN_41)) {
				OpRefActivatableFifo receiverOpRef = (OpRefActivatableFifo) jbElement.getValue();
				SM_ModuleInstance modInstance = compImpl.getModuleInstance(receiverOpRef.getInstanceName());
				SM_EventReceivedOp eventOp = modInstance.getModuleType().getEventReceivedOpByName(receiverOpRef.getOperationName());
				smEventLink.addReceiver(new SM_ReceiverModuleInstance(modInstance, eventOp, receiverOpRef.getFifoSize()));
				modInstance.addEventLink(smEventLink);
			} else if (jbElement.getName().getLocalPart().equals("dynamicTrigger")) {
				// Treat the dynamic trigger just like a module - except where
				// it isn't...
				// OpRef will change following WS#17...
				OpRefActivatingFifo receiverOpRef = (OpRefActivatingFifo) jbElement.getValue();
				SM_DynamicTriggerInstance dynTriggerInstance = compImpl.getDynamicTriggerInstanceByName(receiverOpRef.getInstanceName());
				SM_EventReceivedOp eventOp = dynTriggerInstance.getModuleType().getEventReceivedOpByName(receiverOpRef.getOperationName());
				// Note fifosize. Reusing 'size' matches the French platform (I
				// believe), but will change following WS#17...
				smEventLink.addReceiver(new SM_ReceiverDynamicTriggerInstance(dynTriggerInstance, eventOp, dynTriggerInstance.getSize()));
				dynTriggerInstance.addLink(smEventLink);
			} else if (jbElement.getName().getLocalPart().equals("service")) {
				// This sender is a provided service
				OpRef receiverOpRef = jbElement.getValue();
				SM_ServiceInstance serviceInstance = compImpl.getCompType().getServiceInstanceByName(receiverOpRef.getInstanceName());
				SM_EventServiceOp eventServiceOp = serviceInstance.getServiceInterface().getEventOperation(receiverOpRef.getOperationName());
				smEventLink.addReceiver(new SM_ReceiverService(serviceInstance, eventServiceOp));
			} else if (jbElement.getName().getLocalPart().equals("reference")) {
				// This sender is a required service
				OpRef receiverOpRef = jbElement.getValue();
				SM_ServiceInstance serviceInstance = compImpl.getCompType().getReferenceInstanceByName(receiverOpRef.getInstanceName());
				SM_EventServiceOp eventServiceOp = serviceInstance.getServiceInterface().getEventOperation(receiverOpRef.getOperationName());
				smEventLink.addReceiver(new SM_ReceiverService(serviceInstance, eventServiceOp));
			}
		}
	}

	private void processRequesLink(SM_ComponentImplementation compImpl, RequestLink requestLink) {
		SM_RequestLink smRequestLink = new SM_RequestLink(compImpl);

		// Add clients
		processClients(compImpl, requestLink, smRequestLink);

		// Add the server
		processServer(compImpl, requestLink, smRequestLink);

	}

	private void processSenders(SM_ComponentImplementation compImpl, EventLink eventLink, SM_EventLink smEventLink) {
		for (JAXBElement<?> jbElement : eventLink.getSenders().getServicesAndReferencesAndModuleInstances()) {
			// Determine if this sender is a module instance (or trigger
			// instance) within this component or a required service or a
			// provided service
			if (jbElement.getName().getLocalPart().equals(SEP_PATTERN_41)) {
				// The sender is a module instance
				OpRef senderOpRef = (OpRef) jbElement.getValue();
				SM_ModuleInstance modInstance = compImpl.getModuleInstance(senderOpRef.getInstanceName());
				SM_EventSentOp eventOp = modInstance.getModuleType().getEventSentOpByName(senderOpRef.getOperationName());
				smEventLink.addSender(new SM_SenderModuleInstance(modInstance, eventOp));
				modInstance.addEventLink(smEventLink);
			} else if (jbElement.getName().getLocalPart().equals("trigger")) {
				// The sender is a trigger instance
				OpRefTrigger senderOpRef = (OpRefTrigger) jbElement.getValue();
				SM_TriggerInstance smTrigInst = compImpl.getTriggerInstanceByName(senderOpRef.getInstanceName());
				smEventLink.addSender(new SM_SenderTriggerInstance(smTrigInst, senderOpRef.getPeriod()));
				smTrigInst.addEventLink(smEventLink);
			} else if (jbElement.getName().getLocalPart().equals("dynamicTrigger")) {
				// The sender is a dynamic trigger instance ('out' event)
				OpRef senderOpRef = (OpRef) jbElement.getValue();
				SM_DynamicTriggerInstance smDynTrigInst = compImpl.getDynamicTriggerInstanceByName(senderOpRef.getInstanceName());
				SM_EventSentOp eventOp = smDynTrigInst.getModuleType().getEventSentOpByName(senderOpRef.getOperationName());
				smEventLink.addSender(new SM_SenderDynamicTriggerInstance(smDynTrigInst, eventOp));
				smDynTrigInst.addLink(smEventLink);
			} else if (jbElement.getName().getLocalPart().equals("service")) {
				// This sender is a provided service
				OpRef senderOpRef = (OpRef) jbElement.getValue();
				SM_ServiceInstance serviceInstance = compImpl.getCompType().getServiceInstanceByName(senderOpRef.getInstanceName());
				SM_EventServiceOp eventServiceOp = serviceInstance.getServiceInterface().getEventOperation(senderOpRef.getOperationName());
				smEventLink.addSender(new SM_SenderService(serviceInstance, eventServiceOp));
			} else if (jbElement.getName().getLocalPart().equals("reference")) {
				// This sender is a required service
				OpRef senderOpRef = (OpRef) jbElement.getValue();
				SM_ServiceInstance serviceInstance = compImpl.getCompType().getReferenceInstanceByName(senderOpRef.getInstanceName());
				SM_EventServiceOp eventServiceOp = serviceInstance.getServiceInterface().getEventOperation(senderOpRef.getOperationName());

				smEventLink.addSender(new SM_SenderService(serviceInstance, eventServiceOp));
			} else if (jbElement.getName().getLocalPart().equals("external")) {
				// This sender is an external to ECOA entity
				OpRefExternal senderOpRef = (OpRefExternal) jbElement.getValue();
				smEventLink.addSender(new SM_SenderExternal(smEventLink, senderOpRef.getOperationName(), senderOpRef.getLanguage()));
			}
		}
	}

	private void processServer(SM_ComponentImplementation compImpl, RequestLink requestLink, SM_RequestLink smRequestLink) {
		SM_ServerInterface smServer = null;

		Server server = requestLink.getServer();

		if (server.getModuleInstance() != null) {
			OpRefServer serverOpRef = server.getModuleInstance();

			SM_ModuleInstance modInstance = compImpl.getModuleInstance(serverOpRef.getInstanceName());

			SM_RequestReceivedOp requestOp = modInstance.getModuleType().getRequestReceivedOpByName(serverOpRef.getOperationName());

			// Create a new server object
			smServer = new SM_ServerModuleInstance(modInstance, requestOp, serverOpRef.getFifoSize());

			// Add the link to the module instance
			modInstance.addRequestLink(smRequestLink);
		} else {
			// The server must be a required service if not a module instance in
			// this component.
			OpRef serverOpRef = server.getReference();

			SM_ServiceInstance serviceInstance = compImpl.getCompType().getReferenceInstanceByName(serverOpRef.getInstanceName());

			SM_RRServiceOp rrServiceOp = serviceInstance.getServiceInterface().getRROperation(serverOpRef.getOperationName());

			// Create a new server object
			smServer = new SM_ServerService(serviceInstance, rrServiceOp);
		}

		// Add the server to the requestlink object
		smRequestLink.addServer(smServer);
	}

	private void processWriters(SM_ComponentImplementation compImpl, DataLink dataLink, SM_DataLink smDataLink) {
		for (JAXBElement<OpRef> jbWriterOpRef : dataLink.getWriters().getReferencesAndModuleInstances()) {
			OpRef writerOpRef = jbWriterOpRef.getValue();

			// Determine if this writer is a module instance within this
			// component
			// or a required service.
			if (jbWriterOpRef.getName().getLocalPart().equals(SEP_PATTERN_41)) {
				SM_ModuleInstance modInstance = compImpl.getModuleInstance(writerOpRef.getInstanceName());
				SM_DataWrittenOp dataOp = modInstance.getModuleType().getDataWrittenOpByName(writerOpRef.getOperationName());

				SM_WriterModuleInstance writer = new SM_WriterModuleInstance(modInstance, dataOp);

				// TODO - not sure we should be polluting the model with VD
				// repos... could remove now fixed links!
				// Create a new repository
				SM_VDRepository vdRepo = new SM_VDRepository(repoID++);
				vdRepo.addWriter(writer);
				vdRepo.setDataType(((SM_DataOp) writer.getWriterOp()).getData());
				compImpl.addVDRepository(vdRepo);

				// Add the writer to the datalink object
				checkWriterNotInOtherDataLinks(compImpl, modInstance, dataOp);
				smDataLink.addWriter(writer);
				smDataLink.addVDRepo(vdRepo);

				// Add the link to the module instance
				modInstance.addDataLink(smDataLink);
			} else {
				// Must be a required service
				SM_ServiceInstance serviceInstance = compImpl.getCompType().getReferenceInstanceByName(writerOpRef.getInstanceName());

				SM_DataServiceOp dataServiceOp = serviceInstance.getServiceInterface().getDataOperation(writerOpRef.getOperationName());

				SM_WriterService writer = new SM_WriterService(serviceInstance, dataServiceOp);

				// TODO - not sure we should be polluting the model with VD
				// repos... could remove now fixed links!
				// Create a new repository
				SM_VDRepository vdRepo = new SM_VDRepository(repoID++);
				vdRepo.addWriter(writer);
				vdRepo.setDataType(writer.getWriterOp().getData().getType());
				compImpl.addVDRepository(vdRepo);

				// Add the writer to the datalink object
				checkWriterNotInOtherDataLinks(compImpl, serviceInstance, dataServiceOp);
				smDataLink.addWriter(writer);
				smDataLink.addVDRepo(vdRepo);
			}
		}
	}

	public void updateSystemModel(boolean instrumentAllModules) {
		for (ECOAFile cif : componentImplementations) {
			// Reset the Versioned Data repository ID.
			repoID = 1;

			ComponentImplementation ci = (ComponentImplementation) cif.getObject();
			SM_ComponentType compType = null;
			ArrayList<SM_ModuleType> modTypes = new ArrayList<SM_ModuleType>();

			String compImplName = getComponentNameByFilename(cif.getName());
			String compTypeName = ci.getComponentDefinition();

			if (systemModel.getComponentDefinitions().componentTypeExists(compTypeName)) {
				compType = systemModel.getComponentDefinitions().getComponentType(compTypeName);
			} else {
				LOGGER.info("Error when creating component implementation: " + compImplName);
				LOGGER.info("component definition " + compTypeName + " does not exist.");
				
			}

			// Populate all module types.
			for (ModuleType mt : ci.getModuleTypes()) {
				String modName = mt.getName();
				List<SM_Object> operations = new ArrayList<SM_Object>();

				for (Object op : mt.getOperations().getDataWrittensAndDataReadsAndEventSents()) {
					if (op instanceof Event) {
						Event e = (Event) op;
						List<SM_OperationParameter> paramList = new ArrayList<SM_OperationParameter>();

						for (Parameter p : e.getInputs()) {
							SM_OperationParameter oP;
							if (systemModel.getTypes().typeExists(p.getType())) {
								oP = new SM_OperationParameter(p.getName(), systemModel.getTypes().getType(p.getType()));
								paramList.add(oP);
							} else {
								LOGGER.info("Invalid parameter " + p.getType() + "when creating event ");
								LOGGER.info(e.getName() + " in module type " + modName + " for ");
								LOGGER.info("component implementation " + compImplName);
								
							}
						}
						if (e instanceof EventReceived) {
							operations.add(new SM_EventReceivedOp(e.getName(), paramList));
						} else {
							operations.add(new SM_EventSentOp(e.getName(), paramList));
						}
					}

					if (op instanceof VersionedData) {
						VersionedData vD = (VersionedData) op;
						if (systemModel.getTypes().typeExists(vD.getType())) {
							if (vD instanceof DataRead) {
								operations.add(new SM_DataReadOp(vD.getName(), systemModel.getTypes().getType(vD.getType()), ((DataRead) vD).isNotifying()));
							} else {
								operations.add(new SM_DataWrittenOp(vD.getName(), systemModel.getTypes().getType(vD.getType())));
							}
						} else {
							LOGGER.info("Invalid parameter " + vD.getType() + "when creating versioned data ");
							LOGGER.info(vD.getName() + " in module type " + modName + " for ");
							LOGGER.info("component implementation " + compImplName);
							
						}
					}

					if (op instanceof RequestResponse) {
						RequestResponse rr = (RequestResponse) op;

						List<SM_OperationParameter> inputParamList = new ArrayList<SM_OperationParameter>();
						List<SM_OperationParameter> outputParamList = new ArrayList<SM_OperationParameter>();

						// Process inputs
						for (Parameter p : rr.getInputs()) {
							SM_OperationParameter oP;
							if (systemModel.getTypes().typeExists(p.getType())) {
								oP = new SM_OperationParameter(p.getName(), systemModel.getTypes().getType(p.getType()));
								inputParamList.add(oP);
							} else {
								LOGGER.info("Invalid parameter " + p.getType() + "when creating request response ");
								LOGGER.info(rr.getName() + " in module type " + modName + " for ");
								LOGGER.info("component implementation " + compImplName);
								
							}
						}

						// Process outputs
						for (Parameter p : rr.getOutputs()) {
							SM_OperationParameter oP;
							if (systemModel.getTypes().typeExists(p.getType())) {
								oP = new SM_OperationParameter(p.getName(), systemModel.getTypes().getType(p.getType()));
								outputParamList.add(oP);
							} else {
								LOGGER.info("Invalid parameter " + p.getType() + "when creating request response ");
								LOGGER.info(rr.getName() + " in module type " + modName + " for ");
								LOGGER.info("component implementation " + compImplName);
								
							}
						}
						if (rr instanceof RequestReceived) {
							operations.add(new SM_RequestReceivedOp(rr.getName(), inputParamList, outputParamList));
						} else {
							RequestSent reqSent = (RequestSent) rr;
							operations.add(new SM_RequestSentOp(rr.getName(), inputParamList, outputParamList, reqSent.isIsSynchronous(), reqSent.getTimeout(), reqSent.getMaxConcurrentRequests()));
						}
					}
				}

				SM_ModuleType moduleType = new SM_ModuleType(modName, operations, mt.isIsSupervisionModule(), mt.isIsFaultHandler());

				// Set the optional flags of a supervision module if specified
				// (for activating).
				if (mt.isIsSupervisionModule()) {
					moduleType.setActivatingErrorNotifs(mt.isActivatingErrorNotifs());
					moduleType.setActivatingSvcAvailNotifs(mt.isActivatingSvcAvailNotifs());
					moduleType.setEnableModuleLifeCycleNotifs(mt.isEnableModuleLifeCycleNotifs());
				}

				if (mt.getProperties() != null) {
					for (Parameter property : mt.getProperties().getProperties()) {
						moduleType.addModuleProperty(new SM_ModuleTypeProperty(property.getName(), systemModel.getTypes().getType(property.getType()), moduleType));
					}
				}

				if (mt.getPinfo() != null) {
					processModTypePinfo(mt, moduleType);
				}

				modTypes.add(moduleType);
			}
			SM_ComponentImplementation compImpl = new SM_ComponentImplementation(compImplName, compType, modTypes);

			// Get any required service QoS
			for (ServiceQoS qos : ci.getReferences()) {
				// Need to load in the QoS at this stage.
				XMLProc4aCompImplQOS compImplQoSProc = new XMLProc4aCompImplQOS();
				// Get the directory to search for the file in.
				compImplQoSProc.parseFile(cif.getName().getParent().resolve(qos.getNewQoS()));

				// Get the assosiated required service instance.
				SM_ServiceInstance reqService = compType.getReferenceInstanceByName(qos.getName());

				compImplQoSProc.updateSystemModel(systemModel, reqService, compImpl);

			}

			// add the default ECOA namespace
			compImpl.addUse(systemModel.getTypes().getNamespace("ECOA"));
			// Add any use libraries
			for (Use use : ci.getUses()) {
				if (systemModel.getTypes().namespaceExists(use.getLibrary())) {
					compImpl.addUse(systemModel.getTypes().getNamespace(use.getLibrary()));
				} else {
					LOGGER.info("Use library " + use.getLibrary() + " not found in component implementation" + compImplName);
					
				}
			}

			// Store the directory where the output should be placed (as not
			// necessarily the same as the component implementation name!)
			compImpl.setContainingDir(cif.getName().getParent());
			systemModel.getComponentImplementations().addImplementation(compImpl);

			// Populate all module implementations and instances
			for (ModuleImplementation mi : ci.getModuleImplementations()) {
				SM_ModuleType modType = compImpl.getModuleType(mi.getModuleType());

				SM_ModuleImpl moduleImpl = null;
				if (compImpl.moduleTypeExists(modType.getName())) {
					String lang = mi.getLanguage();
					if (lang.equals("C")) {
						moduleImpl = new SM_ModuleImpl(mi.getName(), ImplLanguage.C, modType, compImpl);
					} else if (lang.equals("C++")) {
						moduleImpl = new SM_ModuleImpl(mi.getName(), ImplLanguage.CPP, modType, compImpl);
					} else if (lang.equals("Ada")) {
						moduleImpl = new SM_ModuleImpl(mi.getName(), ImplLanguage.ADA, modType, compImpl);
					} else {
						LOGGER.info("ERROR: Module Implementation Language " + lang + " not currently supported");
						
					}
				}
				compImpl.addModuleImplementation(moduleImpl);
			}

			// Populate all module instances
			for (ModuleInstance min : ci.getModuleInstances()) {
				SM_ModuleImpl moduleImpl = compImpl.getModuleImplementationByName(min.getImplementationName());

				SM_ModuleType mtd = moduleImpl.getModuleType();

				// Set the intrument flag if required.
				if (instrumentAllModules || (min.getModuleBehaviour() != null && min.getModuleBehaviour().equalsIgnoreCase("instrument"))) {
					moduleImpl.setInstrument(true);
				}

				SM_ModuleInstance inst = new SM_ModuleInstance(min.getName(), mtd, moduleImpl, compImpl);

				if (min.getPropertyValues() != null) {
					for (PropertyValue propertyVal : min.getPropertyValues().getPropertyValues()) {
						inst.addModInstPropertyValue(new SM_ModuleInstanceProperty(mtd.getModulePropertyByName(propertyVal.getName()), propertyVal.getValue(), inst));
					}
				}

				if (min.getPinfo() != null) {
					processModInstPinfo(min, inst);
				}

				compImpl.addModuleInstance(inst);
				moduleImpl.addModuleInstance(inst);
			}

			// Populate all trigger instances
			for (TriggerInstance triggerInst : ci.getTriggerInstances()) {
				compImpl.addTriggerInstance(new SM_TriggerInstance(triggerInst.getName(), compImpl));
			}

			// Populate all dynamic trigger instances
			for (DynamicTriggerInstance dynTriggerInst : ci.getDynamicTriggerInstances()) {
				double maxDelay = 0;
				List<SM_OperationParameter> paramList = new ArrayList<SM_OperationParameter>();

				for (Parameter p : dynTriggerInst.getParameters()) {
					SM_OperationParameter oP;
					if (systemModel.getTypes().typeExists(p.getType())) {
						oP = new SM_OperationParameter(p.getName(), systemModel.getTypes().getType(p.getType()));
						paramList.add(oP);
					} else {
						LOGGER.info("Invalid parameter type '" + p.getType() + "' when creating parameter '");
						LOGGER.info(p.getName() + "' in dynamic trigger '" + dynTriggerInst.getName() + "' for ");
						LOGGER.info("component implementation '" + compImplName + "'");
						
					}
				}

				SM_DynamicTriggerType dtt = new SM_DynamicTriggerType(dynTriggerInst.getName(), paramList, systemModel);

				if (dynTriggerInst.getDelayMax() != null) {
					maxDelay = dynTriggerInst.getDelayMax();
				} else {
					maxDelay = 01e100;
				}

				compImpl.addDynamicTriggerInstance(new SM_DynamicTriggerInstance(dynTriggerInst.getName(), dtt, dynTriggerInst.getSize(), dynTriggerInst.getDelayMin(), maxDelay, compImpl));
			}

			// Populate all links (eventLink, requestLink, dataLink)
			for (Object linkObj : ci.getDataLinksAndEventLinksAndRequestLinks()) {
				if (linkObj instanceof RequestLink) {
					processRequesLink(compImpl, (RequestLink) linkObj);
				} else if (linkObj instanceof DataLink) {
					processDataLink(compImpl, (DataLink) linkObj);
				} else if (linkObj instanceof EventLink) {
					processEventLink(compImpl, (EventLink) linkObj);
				} else {
					LOGGER.info("      - Uninterpreted class " + linkObj.getClass().getName());
				}
			}
		}
	}
}
