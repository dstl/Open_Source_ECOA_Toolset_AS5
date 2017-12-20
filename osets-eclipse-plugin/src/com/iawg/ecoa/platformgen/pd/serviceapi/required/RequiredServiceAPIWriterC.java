/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.serviceapi.required;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.TypesProcessorC;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance.WireRank;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_VDRepository;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_DataLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ClientInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_RequestLink;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;
import com.iawg.ecoa.systemmodel.qos.SM_QualityOfService;
import com.iawg.ecoa.systemmodel.qos.SM_RequestResponseQoS;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_DataServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp.EventDirection;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;
import com.iawg.ecoa.systemmodel.uid.SM_UIDServiceOp;

public class RequiredServiceAPIWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_381 = "void ";

	private SM_ProtectionDomain pd;
	private SM_ServiceInstance serviceInst;
	private SM_ComponentInstance compInst;
	private boolean isHeader;
	private String serviceAPIName;
	private SM_LogicalComputingPlatform lcp;
	private SM_QualityOfService qos;

	private ArrayList<String> includeList = new ArrayList<String>();
	private Generic_Platform underlyingPlatform;

	public RequiredServiceAPIWriterC(PlatformGenerator platformGenerator, boolean isHeader, Path outputDir, SM_ComponentInstance compInst, SM_ProtectionDomain pd, SM_ServiceInstance serviceInst) {
		super(outputDir);
		this.underlyingPlatform = platformGenerator.getunderlyingPlatformInstantiation();
		this.pd = pd;
		this.compInst = compInst;
		this.serviceInst = serviceInst;
		this.isHeader = isHeader;
		this.serviceAPIName = compInst.getName() + "_" + serviceInst.getName();

		this.lcp = pd.getLogicalComputingNode().getLogicalComputingPlatform();

		// Determine if a QoS has been defined for this components service.
		qos = compInst.getImplementation().getRequiredQOSMap().get(serviceInst);

		setFileStructure();
	}

	private void addTypeIncludes(List<SM_OperationParameter> opParamsList) {
		for (SM_OperationParameter opParam : opParamsList) {
			includeList.add(opParam.getType().getNamespace().getName());
		}
	}

	private void generateEventReceiveds() {
		String eventReceivedsText = "/* Event Received Operations */" + LF + LF;

		for (SM_EventServiceOp eventOp : serviceInst.getServiceInterface().getEventOps()) {
			// Only generate for event sent (received by provider for required
			// services)
			if (eventOp.getDirection() == EventDirection.SENT_BY_PROVIDER) {
				eventReceivedsText += "ECOA__return_status " + serviceAPIName + "_" + eventOp.getName() + "__event_received(ECOA__uint32 uid, ECOA__timestamp *timestamp";

				for (SM_OperationParameter opParam : eventOp.getInputs()) {
					eventReceivedsText += CLanguageSupport.writeConstParam(opParam);
				}

				if (isHeader) {
					eventReceivedsText += ");" + LF + LF;
					addTypeIncludes(eventOp.getInputs());
				} else {
					eventReceivedsText += ")" + LF + "{" + LF + "   ECOA__boolean8 available = ECOA__FALSE;" + LF + LF;

					for (WireRank wireRank : compInst.getSourceWiresByRank(serviceInst)) {
						SM_Wire wire = wireRank.getWire();

						// Get the UID of the providing components service.
						String serviceUIDString = wire.getTarget().getName().toUpperCase() + "_" + wire.getTargetOp().getName().toUpperCase() + "_UID";

						// First determine if the providing service is available
						// (highest rank first)
						if (wire.getAreEventsMulticast()) {
							eventReceivedsText += "   /* Get the availability of the provided service (always do this as eventMulticast = true) */" + LF + "   " + pd.getName() + "_Service_Manager__Get_Availability(" + serviceUIDString + ", &available);" + LF + LF;
						} else {
							eventReceivedsText += "   if (available != ECOA__TRUE)" + LF + "   {" + LF + "      /* Get the availability of the provided service (only if a higher rank wire has not already been determined as available) */" + LF + "      " + pd.getName() + "_Service_Manager__Get_Availability(" + serviceUIDString + ", &available);" + LF + LF;
						}

						eventReceivedsText += "      if (available && uid == " + wire.getUID(eventOp).getUIDDefString() + ")" + LF + "      {" + LF;

						// Find all the event links which are connected to the
						// service instance.
						for (SM_EventLink eventLink : compInst.getImplementation().getEventLinks()) {
							for (SM_SenderInterface senderInterface : eventLink.getSenders()) {
								if (senderInterface.getSenderOpName().equals(eventOp.getName()) && senderInterface.getSenderInst() == serviceInst) {
									// Send an ILI message to each module
									// instance queue
									for (SM_ReceiverInterface receiver : eventLink.getReceivers()) {
										includeList.add(compInst.getImplementation().getName() + "_Service_Instance_Operation_UID");

										String referenceUID = compInst.getImplementation().getName().toUpperCase() + "_" + serviceInst.getName().toUpperCase() + "_" + eventOp.getName().toUpperCase() + "_UID";

										eventReceivedsText += "         /* Call the Module Instance Queue Operation */" + LF + "         " + compInst.getName() + "_" + receiver.getReceiverInst().getName() + "_Controller__" + receiver.getReceiverOp().getName() + "__event_received(timestamp, " + referenceUID;

										for (SM_OperationParameter opParam : eventOp.getInputs()) {
											eventReceivedsText += ", " + opParam.getName();
										}
										eventReceivedsText += ");" + LF;
									}
								}
							}
						}

						eventReceivedsText +=
								// Close if check on availability + sender
								// instance
								"      }" + LF;

						if (wire.getAreEventsMulticast() == false) {
							eventReceivedsText +=
									// Close if check on availability flag
									"   }" + LF;
						}
					}
					eventReceivedsText += "   return ECOA__return_status_OK;" + LF;
					eventReceivedsText += "}" + LF + LF;
				}
			}
		}

		// Replace the #EVENT_RECEIVEDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#EVENT_RECEIVEDS#", eventReceivedsText);
	}

	private void generateEventSends() {
		String eventSendsText = "/* Event Send Operations */" + LF + LF;

		for (SM_EventServiceOp eventOp : serviceInst.getServiceInterface().getEventOps()) {
			// Only generate for event sent (received by provider for required
			// services)
			if (eventOp.getDirection() == EventDirection.RECEIVED_BY_PROVIDER) {
				eventSendsText += SEP_PATTERN_381 + serviceAPIName + "_" + eventOp.getName() + "__event_send(ECOA__timestamp *timestamp";

				for (SM_OperationParameter opParam : eventOp.getInputs()) {
					eventSendsText += CLanguageSupport.writeConstParam(opParam);
				}

				if (isHeader) {
					eventSendsText += ");" + LF + LF;
					addTypeIncludes(eventOp.getInputs());
				} else {
					eventSendsText += ")" + LF + "{" + LF + "   ECOA__boolean8 available = ECOA__FALSE;" + LF + LF;

					// Get any wires for this component instance/service
					// instance.
					for (WireRank wireRank : compInst.getSourceWiresByRank(serviceInst)) {
						SM_Wire wire = wireRank.getWire();

						// Get the UID of the providing components service.
						String serviceUIDString = wire.getTarget().getName().toUpperCase() + "_" + wire.getTargetOp().getName().toUpperCase() + "_UID";

						// First determine if the providing service is available
						// (highest rank first)
						if (wire.getAreEventsMulticast()) {
							eventSendsText += "   /* Get the availability of the provided service (always do this as eventMulticast = true) */" + LF + "   " + pd.getName() + "_Service_Manager__Get_Availability(" + serviceUIDString + ", &available);" + LF;
						} else {
							eventSendsText += "   if (available != ECOA__TRUE)" + LF + "   {" + LF + "      /* Get the availability of the provided service (only if a higher rank wire has not already been determined as available) */" + LF + "      " + pd.getName() + "_Service_Manager__Get_Availability(" + serviceUIDString + ", &available);" + LF;
						}

						eventSendsText += "      if (available)" + LF + "      {" + LF;

						// Determine if destination component is in this
						// protection domain or not
						if (wire.getTarget().getProtectionDomain() == pd) {
							String destServiceAPIName = wire.getTarget().getName() + "_" + wire.getTargetOp().getName();

							// The destination is local - call the service
							// operation directly.
							eventSendsText += "         // Call the service operation directly as within our PD" + LF + "         " + destServiceAPIName + "_" + eventOp.getName() + "__event_received(timestamp";

							for (SM_OperationParameter opParam : eventOp.getInputs()) {
								eventSendsText += ", " + opParam.getName();
							}
							eventSendsText += ");" + LF;

							includeList.add(destServiceAPIName + "_Controller");
						} else {
							// The destination is not local - use ELI.
							eventSendsText += generateEventSentELI(eventOp, wire);
						}
						eventSendsText +=
								// Close if check on service availability of
								// provider
								"      }" + LF;

						if (wire.getAreEventsMulticast() == false) {
							eventSendsText +=
									// Close if check on availability flag
									"   }" + LF;
						}

					}
					eventSendsText += "}" + LF + LF;
				}
			}
		}

		// Replace the #EVENT_SENDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#EVENT_SENDS#", eventSendsText);
	}

	private String generateEventSentELI(SM_EventServiceOp eventOp, SM_Wire wire) {
		SM_UIDServiceOp uid = wire.getUID(eventOp);

		includeList.add("ELI_Message");
		includeList.add("ELI_Out__serialiser");
		includeList.add(pd.getName() + "_ELI_Support");

		String eventSendELI = "         int num_bytes;" + LF + "         ELIHeader header;" + LF + LF;

		if (eventOp.getInputs().size() > 0) {
			eventSendELI += "         unsigned int bufferSize = sizeof(ELIHeader)";
			for (SM_OperationParameter opParam : eventOp.getInputs()) {
				eventSendELI += " + sizeof(" + TypesProcessorC.convertParameterToC(opParam.getType()) + ")";
			}
			eventSendELI += ";" + LF;
		} else {
			eventSendELI += "         unsigned int bufferSize = sizeof(ELIHeader);" + LF;
		}

		eventSendELI += "         unsigned char *message_buffer = (unsigned char *)malloc(bufferSize);" + LF + "         unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

				"         header.ecoaMark = bswap16(0xEC0A);" + LF + "         header.version_domain = 0x11;" + LF + LF +

				// Always use real ELI messages for service operations - i.e.
				// set to platform ID (not PD ID)
				"         header.logicalPlatform = " + lcp.getRelatedUDPBinding().getPlatformID() + ";" + LF + "         header.ID = bswap32(" + uid.getUIDDefString() + ");" + LF + LF +

				"         header.seconds = bswap32(timestamp->seconds);" + LF + "         header.nanoseconds = bswap32(timestamp->nanoseconds);" + LF +
				// Don't use sequence number for events
				"         header.sequenceNumber = 0;" + LF + LF +

				"         /* Change the message_buffer_ptr to point after header */" + LF + "         message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF;

		for (SM_OperationParameter opParam : eventOp.getInputs()) {
			String typeName = TypesProcessorC.convertParameterToC(opParam.getType());

			if (opParam.getType().isSimple()) {
				eventSendELI += "         serialise_" + typeName + "(" + opParam.getName() + ", &message_buffer_ptr);" + LF;
			} else {
				eventSendELI += "         serialise_" + typeName + "(*" + opParam.getName() + ", &message_buffer_ptr);" + LF;
			}
		}

		eventSendELI += "         num_bytes = message_buffer_ptr - message_buffer;" + LF + "         header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "         memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "         /* We can now send the ELI message */" + LF + "         " + pd.getName() + "_ELI_Support__SendELIMessage(message_buffer, num_bytes);" + LF + LF +

				"         /* Free the message buffer pointer */" + LF + "         free(message_buffer);" + LF + LF;

		return eventSendELI;
	}

	private void generateRequestSends() {
		SM_RequestResponseQoS qosRR = null;

		String requestSendsText = "/* Request Send Operations */" + LF + LF;

		for (SM_RRServiceOp requestOp : serviceInst.getServiceInterface().getRROps()) {
			// See if there is a QoS defined for this operation
			if (qos != null) {
				qosRR = qos.getRequestResponseQoSMap().get(requestOp);
			}

			requestSendsText += SEP_PATTERN_381 + serviceAPIName + "_" + requestOp.getName() + "__request_send(ECOA__timestamp *timestamp, Client_Info_Type *moduleClientInfo";

			for (SM_OperationParameter opParam : requestOp.getInputs()) {
				requestSendsText += CLanguageSupport.writeConstParam(opParam);
			}

			if (isHeader) {
				requestSendsText += ");" + LF + LF;
				addTypeIncludes(requestOp.getInputs());
				includeList.add("Client_Info_Type");
			} else {
				requestSendsText += ")" + LF + "{" + LF + "   ECOA__boolean8 available = ECOA__FALSE;" + LF + LF +

						"   /* Store the sequence number with the module instance operation ID */" + LF + "   " + serviceAPIName + "__Set_Client_Lookup(moduleClientInfo->localSeqNum, moduleClientInfo->ID);" + LF + LF;

				// Get any wires for this component instance/service instance.
				for (WireRank wireRank : compInst.getSourceWiresByRank(serviceInst)) {
					SM_Wire wire = wireRank.getWire();

					// Get the UID of the providing components service.
					String serviceUIDString = wire.getTarget().getName().toUpperCase() + "_" + wire.getTargetOp().getName().toUpperCase() + "_UID";

					// First determine if the providing service is available
					// (highest rank first)
					requestSendsText += "   if (available != ECOA__TRUE)" + LF + "   {" + LF + "      /* Get the availability of the provided service (only if a higher rank wire has not already been determined as available) */" + LF + "      " + pd.getName() + "_Service_Manager__Get_Availability(" + serviceUIDString + ", &available);" + LF + "      if (available)" + LF + "      {" + LF;

					SM_UIDServiceOp uid = wire.getUID(requestOp);

					// add the response timeout
					if (qosRR != null) {
						requestSendsText += "         /* Setup the reponse timeout */" + LF + "         ECOA__duration duration;" + LF + "         ECOA__return_status st_error;" + LF + "         duration.seconds = " + qosRR.getMaxResponseTimeSec() + ";" + LF + "         duration.nanoseconds = " + qosRR.getMaxResponseTimeNano() + ";" + LF + LF +

								"         " + pd.getName() + "_Timer_Event_Manager__Setup_RequestQoS_Timer(duration, REQUEST_QOS_TIMEOUT, " + uid.getUIDDefString() + ", moduleClientInfo->localSeqNum, &st_error);" + LF + LF;
					}

					// Determine if destination component is in this protection
					// domain or not
					if (wire.getTarget().getProtectionDomain() == pd) {
						String destServiceAPIName = wire.getTarget().getName() + "_" + wire.getTargetOp().getName();

						// The destination is local - call the service operation
						// directly.
						requestSendsText += "         // Call the service operation directly as within our PD" + LF + "         Client_Info_Type serviceClientInfo;" + LF + "         serviceClientInfo.type = Client_Type__SERVICE_OPERATION;" + LF + "         serviceClientInfo.ID = 0; // Not used for service operations" + LF + "         serviceClientInfo.serviceUID = " + uid.getUIDDefString() + ";" + LF + "         serviceClientInfo.localSeqNum = 0;" + LF + "         serviceClientInfo.globalSeqNum = moduleClientInfo->localSeqNum;" + LF + LF;

						requestSendsText += "         " + destServiceAPIName + "_" + requestOp.getName() + "__request_received(timestamp, &serviceClientInfo";

						for (SM_OperationParameter opParam : requestOp.getInputs()) {
							requestSendsText += ", " + opParam.getName();
						}
						requestSendsText += ");" + LF;

						includeList.add(destServiceAPIName + "_Controller");
					} else {
						// The destination is not local - use ELI.
						requestSendsText += generateRequestSentELI(requestOp, uid, wire);
					}
					requestSendsText +=
							// Close if check on service availability of
							// provider
							"      }" + LF +
							// Close if check on availability flag
									"   }" + LF + LF;
				}

				requestSendsText += "   /* Check that \"something\" was available, otherwise send NO_RESPONSE error */" + LF + "   if (available != ECOA__TRUE)" + LF + "   {" + LF;

				// Define local variables for the response output params
				for (SM_OperationParameter opParam : requestOp.getOutputs()) {
					includeList.add("Defaulter");

					requestSendsText += "      " + CLanguageSupport.writeType(opParam.getType()) + " " + opParam.getName() + ";" + LF + "      default_" + CLanguageSupport.writeType(opParam.getType()) + "(&" + opParam.getName() + ");" + LF;
				}

				requestSendsText += "      " + serviceAPIName + "_" + requestOp.getName() + "__response_received(0, timestamp, ECOA__return_status_NO_RESPONSE, moduleClientInfo->localSeqNum";

				for (SM_OperationParameter opParam : requestOp.getOutputs()) {
					requestSendsText += ", &" + opParam.getName();
				}
				requestSendsText += ");" + LF +
				// close if on available (final catch for determining if
				// no
				// response is required)
						"   }" + LF;

				requestSendsText += "}" + LF + LF;

			}
		}

		// Replace the #REQUEST_SENDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#REQUEST_SENDS#", requestSendsText);
	}

	private String generateRequestSentELI(SM_RRServiceOp requestOp, SM_UIDServiceOp uid, SM_Wire wire) {

		includeList.add("ELI_Message");
		includeList.add("ELI_Out__serialiser");
		includeList.add(pd.getName() + "_ELI_Support");

		String requestSendELI = "         int num_bytes;" + LF + "         ELIHeader header;" + LF + LF;

		if (requestOp.getInputs().size() > 0) {
			requestSendELI += "         unsigned int bufferSize = sizeof(ELIHeader)";
			for (SM_OperationParameter opParam : requestOp.getInputs()) {
				requestSendELI += " + sizeof(" + TypesProcessorC.convertParameterToC(opParam.getType()) + ")";
			}
			requestSendELI += ";" + LF;
		} else {
			requestSendELI += "         unsigned int bufferSize = sizeof(ELIHeader);" + LF;
		}

		requestSendELI += "         unsigned char *message_buffer = (unsigned char *)malloc(bufferSize);" + LF + "         unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

				"         header.ecoaMark = bswap16(0xEC0A);" + LF + "         header.version_domain = 0x11;" + LF + LF +

				// Always use real ELI messages for service operations - i.e.
				// set to platform ID (not PD ID)
				"         header.logicalPlatform = " + lcp.getRelatedUDPBinding().getPlatformID() + ";" + LF + "         header.ID = bswap32(" + uid.getUIDDefString() + ");" + LF + LF +

				"         header.seconds = bswap32(timestamp->seconds);" + LF + "         header.nanoseconds = bswap32(timestamp->nanoseconds);" + LF +
				// Set the sequence number
				"         header.sequenceNumber = bswap32(moduleClientInfo->localSeqNum);" + LF + LF +

				"         /* Change the message_buffer_ptr to point after header */" + LF + "         message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF;

		for (SM_OperationParameter opParam : requestOp.getInputs()) {
			String typeName = TypesProcessorC.convertParameterToC(opParam.getType());

			if (opParam.getType().isSimple()) {
				requestSendELI += "         serialise_" + typeName + "(" + opParam.getName() + ", &message_buffer_ptr);" + LF;
			} else {
				requestSendELI += "         serialise_" + typeName + "(*" + opParam.getName() + ", &message_buffer_ptr);" + LF;
			}
		}

		requestSendELI += "         num_bytes = message_buffer_ptr - message_buffer;" + LF + "         header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "         memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "         /* We can now send the ELI message */" + LF + "         " + pd.getName() + "_ELI_Support__SendELIMessage(message_buffer, num_bytes);" + LF + "         free(message_buffer);" + LF + LF;

		return requestSendELI;
	}

	private void generateResponseReceiveds() {
		SM_RequestResponseQoS qosRR = null;

		String responseReceivedsText = "/* Response Received Operations */" + LF + LF;

		for (SM_RRServiceOp requestOp : serviceInst.getServiceInterface().getRROps()) {
			responseReceivedsText += "ECOA__return_status " + serviceAPIName + "_" + requestOp.getName() + "__response_received(ECOA__uint32 uid, ECOA__timestamp *timestamp, ECOA__return_status responseStatus, ECOA__uint32 seqNum";

			for (SM_OperationParameter opParam : requestOp.getOutputs()) {
				responseReceivedsText += CLanguageSupport.writeParam(opParam);
			}

			if (isHeader) {
				responseReceivedsText += ");" + LF + LF;
				addTypeIncludes(requestOp.getOutputs());
			} else {
				// See if there is a QoS defined for this operation
				if (qos != null) {
					qosRR = qos.getRequestResponseQoSMap().get(requestOp);
				}

				includeList.add(compInst.getImplementation().getName() + "_Module_Instance_Operation_UID");

				responseReceivedsText += ")" + LF + "{" + LF + "   ECOA__uint32 moduleInstanceOpID;" + LF + "   ECOA__return_status status;" + LF +

						"   if (uid != 0)" + LF + "   {" + LF;
				// add the response timeout
				if (qosRR != null) {
					String timerManName = pd.getName() + "_Timer_Event_Manager";
					includeList.add(timerManName);

					responseReceivedsText += "      Timer_Manager_Error_Type dtStatus;" + LF + LF + "      // Need to delete the associated timer for this request QoS operation" + LF + "      dtStatus = " + timerManName + "__Delete_RequestQoS_Timer_ID(uid, seqNum);" + LF + LF +

							"      if (dtStatus == Timer_Manager_Error_Type_OK)" + LF + "      {" + LF;
				}

				responseReceivedsText += "      /* Get the sequence number to module instance operation ID */" + LF + "      " + serviceAPIName + "__Get_Client_Lookup(seqNum, &moduleInstanceOpID, &status);" + LF + LF +

						"      if (status == ECOA__return_status_OK)" + LF + "      {" + LF + "         switch (moduleInstanceOpID)" + LF + "         {" + LF;

				// Find all the request links which are connected to the service
				// instance.
				for (SM_RequestLink requestLink : compInst.getImplementation().getRequestLinks()) {
					if (requestLink.getServer().getServerOp() == requestOp && requestLink.getServer().getServerInst() == serviceInst) {
						for (SM_ClientInterface clientInterface : requestLink.getClients()) {
							includeList.add(compInst.getName() + "_" + clientInterface.getClientInst().getName() + "_Controller");
							responseReceivedsText += "            case " + compInst.getImplementation().getName().toUpperCase() + "_" + clientInterface.getClientInst().getName().toUpperCase() + "_" + clientInterface.getClientOp().getName().toUpperCase() + "_UID:" + LF + "               /* Call the Module Instance Queue Operation */" + LF + "               " + compInst.getName() + "_" + clientInterface.getClientInst().getName() + "_Controller__" + clientInterface.getClientOp().getName() + "__response_received(timestamp, &responseStatus, seqNum";

							for (SM_OperationParameter opParam : requestOp.getOutputs()) {
								responseReceivedsText += ", " + opParam.getName();
							}
							responseReceivedsText += ");" + LF + "            break;" + LF;
						}
					}
				}

				responseReceivedsText +=
						// Close switch on moduleInstanceOpID
						"         }" + LF;
				if (qosRR != null) {
					// Close if on delete timer status
					responseReceivedsText += "      }" + LF + "      else" + LF + "      {" + LF +
					// TODO - NOT SURE THIS IS THE BEHAVIOUR WE WANT IN
					// THE DEMO
					// SYSTEM - MAY NEED A FLAG TO TURN THIS ON/OFF?!
					// TODO - need to switch service off at this point?!
							"        printf(\"INFO - GOT SERVICE-LEVEL RESPONSE AFTER TIMEOUT - RESPONSE DISCARDED - PROVIDED SERVICE NOW BEING SET UNAVAILABLE!!!\\n\");" + LF + "        " + pd.getName() + "_Service_Manager__Set_Provider_Unavailabile(uid);" + LF + "      }" + LF;
				}

				responseReceivedsText +=
						// Close if on status
						"      }" + LF + "      else" + LF + "      {" + LF + "         printf(\"REQUIRED SERVICE RECEIVED RESPONSE WITH NO OUTSTANDING REQUEST\\n\");" + LF + "      }" + LF +
						// close if on uid == 0
								"   }" + LF + "   /* This was a NO_RESPONSE error as no provider was available */" + LF + "   else" + LF + "   {" + LF + "      /* Get the sequence number to module instance operation ID */" + LF + "      " + serviceAPIName + "__Get_Client_Lookup(seqNum, &moduleInstanceOpID, &status);" + LF + LF +

								"      if (status == ECOA__return_status_OK)" + LF + "      {" + LF + "         switch (moduleInstanceOpID)" + LF + "         {" + LF;

				// Find all the request links which are connected to the service
				// instance.
				for (SM_RequestLink requestLink : compInst.getImplementation().getRequestLinks()) {
					if (requestLink.getServer().getServerOp() == requestOp && requestLink.getServer().getServerInst() == serviceInst) {
						for (SM_ClientInterface clientInterface : requestLink.getClients()) {
							includeList.add(compInst.getName() + "_" + clientInterface.getClientInst().getName() + "_Controller");
							responseReceivedsText += "            case " + compInst.getImplementation().getName().toUpperCase() + "_" + clientInterface.getClientInst().getName().toUpperCase() + "_" + clientInterface.getClientOp().getName().toUpperCase() + "_UID:" + LF + "               /* Call the Module Instance Queue Operation */" + LF + "               " + compInst.getName() + "_" + clientInterface.getClientInst().getName() + "_Controller__" + clientInterface.getClientOp().getName() + "__response_received(timestamp, &responseStatus, seqNum";

							for (SM_OperationParameter opParam : requestOp.getOutputs()) {
								responseReceivedsText += ", " + opParam.getName();
							}
							responseReceivedsText += ");" + LF + "            break;" + LF;
						}
					}
				}

				responseReceivedsText +=
						// Close switch on moduleInstanceOpID
						"         }" + LF +
						// Close if on status
								"      }" + LF + "      else" + LF + "      {" + LF + "         printf(\"REQUIRED SERVICE RECEIVED RESPONSE WITH NO OUTSTANDING REQUEST\\n\");" + LF + "      }" + LF +
								// Close else on uid == 0
								"   }" + LF +
								// Close function
								"}" + LF + LF;
			}
		}

		// Replace the #RESPONSE_RECEIVEDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#RESPONSE_RECEIVEDS#", responseReceivedsText);
	}

	private void generateVersionedDataUpdates() {
		String versionDataUpdateText = "";

		versionDataUpdateText += "/* Versioned Data Read (Updated) Operations */" + LF + LF;

		for (SM_DataServiceOp vdOp : serviceInst.getServiceInterface().getDataOps()) {
			String typeName = TypesProcessorC.convertParameterToC(vdOp.getData().getType());
			includeList.add(vdOp.getData().getType().getNamespace().getName());

			versionDataUpdateText += "ECOA__return_status " + serviceAPIName + "_" + vdOp.getName() + "__versioned_data_update(ECOA__uint32 uid, " + typeName + " *data, ECOA__timestamp *timestamp)";

			if (isHeader) {
				versionDataUpdateText += ";" + LF;
				includeList.add(vdOp.getData().getType().getNamespace().getName());
			} else {
				versionDataUpdateText += LF + "{" + LF + "   ECOA__boolean8 available = ECOA__FALSE;" + LF + LF;

				for (WireRank wireRank : compInst.getSourceWiresByRank(serviceInst)) {
					SM_Wire wire = wireRank.getWire();

					// Get the UID of the providing components service.
					String serviceUIDString = wire.getTarget().getName().toUpperCase() + "_" + wire.getTargetOp().getName().toUpperCase() + "_UID";

					// First determine if the providing service is available
					// (highest rank first)
					versionDataUpdateText += "      if (available != ECOA__TRUE)" + LF + "      {" + LF + "         /* Get the availability of the provided service (only if a higher rank wire has not already been determined as available) */" + LF + "         " + pd.getName() + "_Service_Manager__Get_Availability(" + serviceUIDString + ", &available);" + LF + LF +

							"         if (available && uid == " + wire.getUID(vdOp).getUIDDefString() + ")" + LF + "         {" + LF;

					for (SM_DataLink dataLink : compInst.getImplementation().getDataLinks()) {
						if (dataLink.getWriter().getWriterInst() == serviceInst && dataLink.getWriter().getWriterOp() == vdOp) {

							SM_VDRepository vdRepo = dataLink.getVDRepo();
							String vdName = pd.getName() + "_" + compInst.getName() + "_VD" + vdRepo.getName();
							includeList.add(vdName);

							versionDataUpdateText += "            /* Write to the local repository */" + LF + "            " + vdName + "__Write(data, timestamp);" + LF + LF;
						}
					}

					versionDataUpdateText +=
							// Close if check on availability + sender instance
							"         }" + LF +
							// Close if check on availability flag
									"      }" + LF;
				}

				versionDataUpdateText += "   return ECOA__return_status_OK;" + LF + "}" + LF + LF;
			}
		}

		// Replace the #VD_READS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#VD_READS#", versionDataUpdateText);
	}

	@Override
	public void open() {

		if (isHeader) {
			super.openFile(outputDir.resolve("service/req/inc/" + serviceAPIName + "_Controller.h"));
		} else {
			super.openFile(outputDir.resolve("service/req/" + serviceAPIName + "_Controller.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#GET_SERVICE_AVAILABILITY#" + LF + "#SERVICE_AVAIL_NOTIFICATION#" + LF + "#CONTAINER_OPS#" + LF + "#EVENT_SENDS#" + LF + "#REQUEST_SENDS#" + LF + "#MODULE_OPS#" + LF + "#EVENT_RECEIVEDS#" + LF + "#RESPONSE_RECEIVEDS#" + LF + "#VD_READS#" + LF + LF + "#REMOVE_LOOKUP#" + LF + "#INITIALISE#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#ACTIVE_PROVIDER_DECL#" + LF + "#CLIENT_LOOKUP_DECL#" + LF + "#SET_LOOKUP#" + LF + "#GET_LOOKUP#" + LF + "#REMOVE_LOOKUP#" + LF + "#GET_SERVICE_AVAILABILITY#" + LF + "#SERVICE_AVAIL_NOTIFICATION#" + LF + "#CONTAINER_OPS#" + LF + "#EVENT_SENDS#" + LF + "#REQUEST_SENDS#" + LF + "#MODULE_OPS#" + LF + "#EVENT_RECEIVEDS#" + LF + "#RESPONSE_RECEIVEDS#" + LF + "#VD_READS#" + LF + LF + "#INITIALISE#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeActiveServiceProviderDecl() {
		String activeProviderText = "/* Variable to hold the active provider component instance ID */" + LF + "static ECOA__int32 " + compInst.getName() + "_activeProviderCompInstID = -1;" + LF;

		// Replace the #ACTIVE_PROVIDER_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#ACTIVE_PROVIDER_DECL#", activeProviderText);
	}

	public void WriteClientLookupDecl() {
		String clientLookText =
				// Client lookup list
				"/* Define the Client Lookup Structure */" + LF + "typedef struct {" + LF + "   ECOA__uint32 ID;" + LF + "   ECOA__uint32 moduleInstanceOpID;" + LF + "} Client_Lookup_Type;" + LF + LF +

				// TODO - should probably work this out somehow...
						"#define " + serviceAPIName + "_Client_Lookup_MAXSIZE 100" + LF + "typedef Client_Lookup_Type Client_Lookup_List_Type[" + serviceAPIName + "_Client_Lookup_MAXSIZE];" + LF + LF +

						"/* Declare the Client Lookup List */" + LF + "static Client_Lookup_List_Type " + serviceAPIName + "_clientLookupList;" + LF + LF;

		// Replace the #CLIENT_LOOKUP_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#CLIENT_LOOKUP_DECL#", clientLookText);
	}

	public void writeContainerToModuleCalls() {
		String containerToModuleCallsText = "/* ---------------------------------------------------------------------------------- */" + LF + "/* Container to Module Interface Operations */" + LF + "/* - The following functions are invoked by the container on a module instance */" + LF + LF;

		// Replace the #MODULE_OPS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#MODULE_OPS#", containerToModuleCallsText);

		generateEventReceiveds();
		generateResponseReceiveds();
		generateVersionedDataUpdates();

	}

	public void WriteGetClientLookup() {
		String getLookText = SEP_PATTERN_381 + serviceAPIName + "__Get_Client_Lookup(ECOA__uint32 ID, ECOA__uint32 *moduleInstanceOpID, ECOA__return_status *status)" + LF + "{" + LF + "   int i;" + LF + "   ECOA__return_status stat = ECOA__return_status_NO_DATA;" + LF + "   for (i = 0; i < " + serviceAPIName + "_Client_Lookup_MAXSIZE; i++)" + LF + "   {" + LF + "      if (" + serviceAPIName + "_clientLookupList[i].ID == ID)" + LF + "      {" + LF + "         /* Set the seqNum to -1 (empty) and set the moduleInstanceOpID return*/" + LF + "         " + serviceAPIName + "_clientLookupList[i].ID = -1;" + LF + "         *moduleInstanceOpID = " + serviceAPIName + "_clientLookupList[i].moduleInstanceOpID;" + LF + "         stat = ECOA__return_status_OK;" + LF + "         break;" + LF + "      }" + LF + "   }" + LF + LF +

				"   *status = stat;" + LF + "}" + LF;

		// Replace the #GET_LOOKUP# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GET_LOOKUP#", getLookText);
	}

	public void writeGetServiceAvailability() {
		String serviceAvailabilityText = "";

		serviceAvailabilityText += "ECOA__return_status " + serviceAPIName + "__Get_Availability(ECOA__boolean8 *available)";

		if (isHeader) {
			serviceAvailabilityText += ";" + LF;
		} else {
			serviceAvailabilityText += LF + "{" + LF + "   /* Determine the availability of the providing service */" + LF;

			for (SM_Wire wire : compInst.getSourceWires(serviceInst)) {
				SM_ComponentInstance providingCompInst = wire.getTarget();

				String serviceUIDString = providingCompInst.getName().toUpperCase() + "_" + wire.getTargetOp().getName().toUpperCase() + "_UID";

				serviceAvailabilityText += "   " + pd.getName() + "_Service_Manager__Get_Availability(" + serviceUIDString + ", available);" + LF + "   if (*available)" + LF + "   {" + LF + "      return ECOA__return_status_OK;" + LF + "   }" + LF;
			}

			serviceAvailabilityText += "   return ECOA__return_status_OK;" + LF + "}" + LF + LF;
		}

		// Replace the #GET_SERVICE_AVAILABILITY# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GET_SERVICE_AVAILABILITY#", serviceAvailabilityText);
	}

	public void writeIncludes() {
		if (isHeader) {
			includeList.addAll(underlyingPlatform.addIncludesServiceAPIHeader());
		} else {
			includeList.addAll(underlyingPlatform.addIncludesServiceAPIBody());

			includeList.add(serviceAPIName + "_Controller");
			includeList.add("Service_UID");
			includeList.add("ecoaByteswap");
			includeList.add("ecoaLog");
			includeList.add(pd.getName() + "_Service_Op_UID");
			includeList.add(pd.getName() + "_Timer_Event_Manager");
			includeList.add(pd.getName() + "_Service_Manager");
			includeList.add(compInst.getName() + "_" + compInst.getImplementation().getSupervisorModule().getName() + "_Controller");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeInitialise() {
		String initialiseText = SEP_PATTERN_381 + serviceAPIName + "__Initialise()";

		if (isHeader) {
			initialiseText += ";" + LF + LF;
		} else {
			initialiseText += LF + "{" + LF + "   /* Initialise the client lookup list */" + LF + "   int i;" + LF + "   for (i = 0; i < " + serviceAPIName + "_Client_Lookup_MAXSIZE; i++)" + LF + "   {" + LF + "      " + serviceAPIName + "_clientLookupList[i].ID = -1;" + LF + "      " + serviceAPIName + "_clientLookupList[i].moduleInstanceOpID = 0;" + LF + "   }" + LF + "}" + LF + LF;
		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);

	}

	public void writeModuleToContainerCalls() {
		String moduleToContainerCallsText = "/* ---------------------------------------------------------------------------------- */" + LF + "/* Module to Container Interface Operations */" + LF + "/* - The following functions are called by the container */" + LF + LF;

		// Replace the #CONTAINER_OPS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#CONTAINER_OPS#", moduleToContainerCallsText);

		generateEventSends();
		generateRequestSends();
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + serviceAPIName + "_Controller.h */" + LF + "/* This is the Required Service API (component instance - service instance) */" + LF;
		} else {
			preambleText += "/* File " + serviceAPIName + "_Controller.c */" + LF + "/* This is the Required Service API (component instance - service instance) */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void WriteRemoveClientLookup() {
		String removeLookText = SEP_PATTERN_381 + serviceAPIName + "__Remove_Client_Lookup(ECOA__uint32 ID)";
		if (isHeader) {
			removeLookText += ";" + LF + LF;
		} else {

			removeLookText += "{" + LF + "   int i;" + LF + "   for (i = 0; i < " + serviceAPIName + "_Client_Lookup_MAXSIZE; i++)" + LF + "   {" + LF + "      if (" + serviceAPIName + "_clientLookupList[i].ID == ID)" + LF + "      {" + LF + "         /* Set the seqNum to -1 (empty) and set the moduleInstanceOpID return*/" + LF + "         " + serviceAPIName + "_clientLookupList[i].ID = -1;" + LF + "         break;" + LF + "      }" + LF + "   }" + LF + "}" + LF;
		}
		// Replace the #REMOVE_LOOKUP# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#REMOVE_LOOKUP#", removeLookText);
	}

	public void writeServiceAvailabilityNotificationCalls() {
		String serviceAvailNotificationText = "";

		serviceAvailNotificationText += "ECOA__return_status " + serviceAPIName + "__Service_Availability_Changed(ECOA__timestamp *opTimestamp)";

		if (isHeader) {
			serviceAvailNotificationText += ";" + LF;
		} else {
			serviceAvailNotificationText += LF + "{" + LF + "   ECOA__boolean8 available = ECOA__FALSE;" + LF + "   ECOA__boolean8 servicePreviouslyAvailable = ECOA__FALSE;" + LF + LF +

					"   /* Set the flag to determine if service previously available */" + LF + "   if (" + compInst.getName() + "_activeProviderCompInstID != -1)" + LF + "   {" + LF + "      servicePreviouslyAvailable = ECOA__TRUE;" + LF + "   }" + LF + LF;

			includeList.add(compInst.getImplementation().getSupervisorModule().getImplementation().getName() + "_container");
			includeList.add(compInst.getName() + "_" + compInst.getImplementation().getSupervisorModule().getName() + "_Controller");

			for (WireRank wireRank : compInst.getSourceWiresByRank(serviceInst)) {
				SM_Wire wire = wireRank.getWire();

				// Get the UID of the providing components service.
				String serviceUIDString = wire.getTarget().getName().toUpperCase() + "_" + wire.getTargetOp().getName().toUpperCase() + "_UID";

				// First determine if the providing service is available
				// (highest rank first)
				serviceAvailNotificationText += "   if (available != ECOA__TRUE)" + LF + "   {" + LF + "      /* Get the availability of the provided service (only if a higher rank wire has not already been determined as available) */" + LF + "      " + pd.getName() + "_Service_Manager__Get_Availability(" + serviceUIDString + ", &available);" + LF + LF +

						"      if (available)" + LF + "      {" + LF + "         if ((" + compInst.getName() + "_activeProviderCompInstID != CI_" + wire.getTarget().getName().toUpperCase() + "_ID) && (servicePreviouslyAvailable == ECOA__TRUE))" + LF + "         {" + LF + "            /* Notify the supervisor module that the active provider has changed */" + LF + "            " + compInst.getName() + "_" + compInst.getImplementation().getSupervisorModule().getName() + "_Controller__Service_Provider_Changed_" + serviceInst.getName() + "(opTimestamp);" + LF + "         }" + LF + LF +

						"         /* Set the current active provider */" + LF + "         " + compInst.getName() + "_activeProviderCompInstID = CI_" + wire.getTarget().getName().toUpperCase() + "_ID;" + LF + LF +

						// Close if check on available return from
						// Get_Availability
						"      }" + LF +
						// Close if check on available flag
						"   }" + LF;
			}

			serviceAvailNotificationText += LF + "   /* If the available flag is still false and the service was previously available, notify the supervisor that the service has become unavailable */" + LF + "   if (available == ECOA__FALSE && servicePreviouslyAvailable == ECOA__TRUE)" + LF + "   {" + LF +

					"      /* Set the activeProvider invalid */" + LF + "      " + compInst.getName() + "_activeProviderCompInstID = -1;" + LF + LF +

					"      /* Notify the supervisor module that the availability has changed to unavailable */" + LF + "      " + compInst.getName() + "_" + compInst.getImplementation().getSupervisorModule().getName() + "_Controller__Service_Availability_Changed_" + serviceInst.getName() + "(opTimestamp, available);" + LF + "   }" + LF + LF +

					"   /* If the available flag is true and the service was not previously available, notify the supervisor that the service has become available */" + LF + "   if (available == ECOA__TRUE && servicePreviouslyAvailable == ECOA__FALSE)" + LF + "   {" + LF +

					"      /* Notify the supervisor module that the availability has changed to available */" + LF + "      " + compInst.getName() + "_" + compInst.getImplementation().getSupervisorModule().getName() + "_Controller__Service_Availability_Changed_" + serviceInst.getName() + "(opTimestamp, available);" + LF + "   }" + LF + "   return ECOA__return_status_OK;" + LF + "}" + LF + LF;
		}

		// Replace the #SERVICE_AVAIL_NOTIFICATION# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SERVICE_AVAIL_NOTIFICATION#", serviceAvailNotificationText);
	}

	public void WriteSetClientLookup() {
		String setLookText = SEP_PATTERN_381 + serviceAPIName + "__Set_Client_Lookup(ECOA__uint32 ID, ECOA__uint32 moduleInstanceOpID)" + LF + "{" + LF + "   int i;" + LF + "   for (i = 0; i < " + serviceAPIName + "_Client_Lookup_MAXSIZE; i++)" + LF + "   {" + LF + "      if (" + serviceAPIName + "_clientLookupList[i].ID == -1)" + LF + "      {" + LF + "         /* Store the seqNum/clientInfo in this empty slot */" + LF + "         " + serviceAPIName + "_clientLookupList[i].ID = ID;" + LF + "         " + serviceAPIName + "_clientLookupList[i].moduleInstanceOpID = moduleInstanceOpID;" + LF + "         break;" + LF + "      }" + LF + "   }" + LF + "}" + LF;

		// Replace the #SET_LOOKUP# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SET_LOOKUP#", setLookText);
	}

}
