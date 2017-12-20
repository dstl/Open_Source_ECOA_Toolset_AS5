/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.moduleiliwriter;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.DataILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ErrorNotificationILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.EventILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.FaultNotificationILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.LifecycleILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.LifecycleILIMessage.ILIMessageType;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ModuleInstanceILI;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.RequestILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ResponseILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ServiceAvailNotificationILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ServiceProviderNotificationILIMessage;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_DataLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_ReaderModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ClientInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_RequestLink;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataReadOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestOp;

public class ModInstanceILIGenerator {
	private static final String SEP_PATTERN_01 = "module_states_type";

	private PlatformGenerator platformGenerator;
	private ModuleInstanceILI modInstILI;
	private ArrayList<ILIMessage> iliMessageList = new ArrayList<ILIMessage>();
	private SM_ModuleInstance modInst;

	public ModInstanceILIGenerator(PlatformGenerator platformGenerator, SM_ModuleInstance modInst) {
		this.platformGenerator = platformGenerator;
		this.modInstILI = new ModuleInstanceILI(modInst);
		this.modInst = modInst;
	}

	private int addErrorNotifMessage(int msgNum) {
		if (modInst.getModuleType().getIsSupervisor()) {
			for (SM_ModuleInstance supModInst : modInst.getComponentImplementation().getModuleInstances().values()) {
				if (!supModInst.getModuleType().getIsSupervisor()) {
					// Create a new ILI Message for the error notification
					ErrorNotificationILIMessage iliErrorNotif = new ErrorNotificationILIMessage(msgNum++, supModInst);
					// Add the error type parameter (error or fatal error)
					iliErrorNotif.addParam(new SM_OperationParameter("Module_Error", platformGenerator.getSystemModel().getTypes().getType(SEP_PATTERN_01)));

					iliMessageList.add(iliErrorNotif);
				}
			}
		}
		return msgNum;
	}

	private int addFaultNotifMessage(int msgNum) {
		if (modInst.getModuleType().getIsFaultHandler()) {
			// Create a new ILI Message for the error notification
			FaultNotificationILIMessage iliFaultNotif = new FaultNotificationILIMessage(msgNum++);
			// Add the error type parameter (error or fatal error)
			iliFaultNotif.addParam(new SM_OperationParameter("error_id", platformGenerator.getSystemModel().getTypes().getType("error_id")));
			iliFaultNotif.addParam(new SM_OperationParameter("timestamp", platformGenerator.getSystemModel().getTypes().getType("timestamp")));
			iliFaultNotif.addParam(new SM_OperationParameter("asset_id", platformGenerator.getSystemModel().getTypes().getType("asset_id")));
			iliFaultNotif.addParam(new SM_OperationParameter("asset_type", platformGenerator.getSystemModel().getTypes().getType("asset_type")));
			iliFaultNotif.addParam(new SM_OperationParameter("error_type", platformGenerator.getSystemModel().getTypes().getType("error_type")));

			iliMessageList.add(iliFaultNotif);
		}
		return msgNum;
	}

	private int addModuleLifecycleMessages(int msgNum) {
		// Add 5 messages for module state
		// 1 - START_module
		// 2 - STOP_module
		// 3 - INITIALIZE_module
		// 4 - SHUTDOWN_module
		// 5 - LIFECYCLE_NOTIFICATION
		iliMessageList.add(new LifecycleILIMessage(ILIMessageType.INITIALIZE_MODULE, msgNum++));
		iliMessageList.add(new LifecycleILIMessage(ILIMessageType.START_MODULE, msgNum++));
		iliMessageList.add(new LifecycleILIMessage(ILIMessageType.STOP_MODULE, msgNum++));
		iliMessageList.add(new LifecycleILIMessage(ILIMessageType.SHUTDOWN_MODULE, msgNum++));

		if (modInst.getModuleType().getIsSupervisor()) {
			// Need to add params to this message for previous/new module state
			// + module instance.
			ILIMessage lifecycleNotifyMessage = new LifecycleILIMessage(ILIMessageType.LIFECYCLE_NOTIFICATION, msgNum++);
			lifecycleNotifyMessage.addParam(new SM_OperationParameter("previous_state", platformGenerator.getSystemModel().getTypes().getType("ECOA", SEP_PATTERN_01)));
			lifecycleNotifyMessage.addParam(new SM_OperationParameter("new_state", platformGenerator.getSystemModel().getTypes().getType("ECOA", SEP_PATTERN_01)));
			lifecycleNotifyMessage.addParam(new SM_OperationParameter("moduleInstanceId", platformGenerator.getSystemModel().getTypes().getType("ECOA", "uint32")));
			iliMessageList.add(lifecycleNotifyMessage);
		}

		return msgNum;
	}

	private int addServiceAvailMessages(int msgNum) {
		if (modInst.getModuleType().getIsSupervisor()) {
			for (SM_ServiceInstance servInst : modInst.getComponentImplementation().getCompType().getReferenceInstancesList()) {
				// Create a new ILI Message for the service availability changed
				ServiceAvailNotificationILIMessage iliServiceAvail = new ServiceAvailNotificationILIMessage(msgNum++, servInst);
				// Add the available parameter
				iliServiceAvail.addParam(new SM_OperationParameter("available", platformGenerator.getSystemModel().getTypes().getType("boolean8")));

				iliMessageList.add(iliServiceAvail);

				// Create a new ILI Message for the service provider change
				ServiceProviderNotificationILIMessage iliServiceProviderNotification = new ServiceProviderNotificationILIMessage(msgNum++, servInst);
				iliMessageList.add(iliServiceProviderNotification);

			}
		}
		return msgNum;
	}

	public void generate() {
		// Get a list of all ILI messages required for this module type
		getILIMessages();

		Path directory = platformGenerator.getPdOutputDir().resolve("src-gen/" + modInst.getComponentImplementation().getName() + "/" + modInst.getName());

		ModInstanceILIWriterC iliWriter = new ModInstanceILIWriterC(directory, modInstILI.getModInst());

		iliWriter.open();
		iliWriter.writePreamble();
		iliWriter.setILIMessages(iliMessageList);
		iliWriter.writeMessageDefinition();
		iliWriter.writeMessageStructure();
		iliWriter.writeIncludes();
		iliWriter.close();

		// Set the ILI Message list in the module type
		modInstILI.setILIMessageList(iliMessageList);

	}

	public void getILIMessages() {

		// Offset other ILI messages by the number of module lifecycle messages
		int msgNum = 1;

		// Add module lifecycle ILI messages (these will always be the first 5
		// messages)
		msgNum = addModuleLifecycleMessages(msgNum);

		// Process all event links of this component implementation
		msgNum = processEventLinks(msgNum);

		// Process all request links of this component implementation
		msgNum = processRequestLinks(msgNum);

		// Process all data links of this component implementation
		msgNum = processDataLinks(msgNum);

		// Add service notification messages
		msgNum = addServiceAvailMessages(msgNum);

		msgNum = addErrorNotifMessage(msgNum);

		msgNum = addFaultNotifMessage(msgNum);

	}

	public ModuleInstanceILI getModInstILI() {
		return modInstILI;
	}

	private int processDataLinks(int msgNum) {
		// If there are any local readers (i.e. moduleInstance) of this data
		// link with notifying set true, create an ILI
		for (SM_DataLink dataLink : modInst.getComponentImplementation().getDataLinks()) {
			for (SM_ReaderModuleInstance reader : dataLink.getLocalReaders()) {
				if (reader.getReaderInst() == modInst) {
					if (((SM_DataReadOp) reader.getReaderOp()).getIsNotifying()) {
						// Create a new ILI Message
						DataILIMessage ili = new DataILIMessage(msgNum++, dataLink);

						iliMessageList.add(ili);
						break;
					}
				}
			}
		}
		return msgNum;
	}

	private int processEventLinks(int msgNum) {
		// If there are any local receivers (i.e. moduleInstance) of this event
		// link, create an ILI
		for (SM_EventLink eventLink : modInst.getComponentImplementation().getEventLinks()) {
			for (SM_ReceiverInterface receiver : eventLink.getReceivers()) {
				if (receiver instanceof SM_ReceiverModuleInstance) {
					if (((SM_ReceiverModuleInstance) receiver).getReceiverInst() == modInst) {
						// Create a new ILI Message
						ILIMessage ili = new EventILIMessage(msgNum++, eventLink);

						// Add any parameters (inputs)
						for (SM_OperationParameter param : ((SM_EventOp) receiver.getReceiverOp()).getInputs()) {
							ili.addParam(param);
						}

						iliMessageList.add(ili);
						break;
					}
				}
			}
		}
		return msgNum;
	}

	private int processRequestLinks(int msgNum) {
		// If the server is local (i.e. moduleInstance), create an ILI
		for (SM_RequestLink requestLink : modInst.getComponentImplementation().getRequestLinks()) {
			if (requestLink.getServer().getServerInst() instanceof SM_ModuleInstance) {
				if (requestLink.getServer().getServerInst() == modInst) {
					// Create a new ILI Message for the request received
					ILIMessage iliReqReceived = new RequestILIMessage(msgNum++, requestLink);

					// Add any parameters
					for (SM_OperationParameter param : ((SM_RequestOp) requestLink.getServer().getServerOp()).getInputs()) {
						iliReqReceived.addParam(param);
					}

					iliMessageList.add(iliReqReceived);
				}
			}

			// Create a response message if we're a client
			for (SM_ClientInterface clientInterface : requestLink.getClients()) {
				if (clientInterface.getClientInst() == modInst) {
					// Create a new response ILI Message for the response
					// received.
					ILIMessage iliRespReceived = new ResponseILIMessage(msgNum++, requestLink);

					iliRespReceived.addParam(new SM_OperationParameter("responseStatus", platformGenerator.getSystemModel().getTypes().getType("ECOA:error")));

					for (SM_OperationParameter param : ((SM_RequestOp) clientInterface.getClientOp()).getOutputs()) {
						iliRespReceived.addParam(param);
					}
					iliMessageList.add(iliRespReceived);
				}
			}
		}
		return msgNum;
	}
}
