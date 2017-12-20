/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.serviceapi.provided;

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
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_VDRepository;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_DataLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_ReaderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_ReaderService;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ClientInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_RequestLink;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_DataServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp.EventDirection;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;
import com.iawg.ecoa.systemmodel.uid.SM_UIDServiceOp;

@SuppressWarnings("unused")
public class ProvidedServiceAPIWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_201 = "ELI_Out__serialiser";

	private SM_ProtectionDomain pd;
	private SM_ServiceInstance serviceInst;
	private SM_ComponentInstance compInst;
	private boolean isHeader;
	private String serviceAPIName;
	private SM_LogicalComputingPlatform lcp;

	private ArrayList<String> includeList = new ArrayList<String>();
	private Generic_Platform underlyingPlatform;

	public ProvidedServiceAPIWriterC(PlatformGenerator platformGenerator, boolean isHeader, Path outputDir, SM_ComponentInstance compInst, SM_ProtectionDomain pd, SM_ServiceInstance serviceInst) {
		super(outputDir);
		this.underlyingPlatform = platformGenerator.getunderlyingPlatformInstantiation();
		this.pd = pd;
		this.compInst = compInst;
		this.serviceInst = serviceInst;
		this.isHeader = isHeader;
		this.serviceAPIName = compInst.getName() + "_" + serviceInst.getName();

		this.lcp = pd.getLogicalComputingNode().getLogicalComputingPlatform();

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
			// Only generate for event received (sent by provider for provided
			// services)
			if (eventOp.getDirection() == EventDirection.RECEIVED_BY_PROVIDER) {
				eventReceivedsText += "ECOA__return_status " + serviceAPIName + "_" + eventOp.getName() + "__event_received(ECOA__timestamp *timestamp";

				for (SM_OperationParameter opParam : eventOp.getInputs()) {
					eventReceivedsText += CLanguageSupport.writeConstParam(opParam);
				}

				if (isHeader) {
					eventReceivedsText += ");" + LF + LF;
					addTypeIncludes(eventOp.getInputs());
				} else {
					eventReceivedsText += ")" + LF + "{" + LF;

					for (SM_EventLink eventLink : compInst.getImplementation().getEventLinks()) {
						for (SM_SenderInterface senderInterface : eventLink.getSenders()) {
							if (senderInterface.getSenderOpName().equals(eventOp.getName()) && senderInterface.getSenderInst() == serviceInst) {
								for (SM_ReceiverInterface receiver : eventLink.getReceivers()) {
									includeList.add(compInst.getImplementation().getName() + "_Service_Instance_Operation_UID");
									includeList.add(compInst.getName() + "_" + receiver.getReceiverInst().getName() + "_Controller");

									String serviceUID = compInst.getImplementation().getName().toUpperCase() + "_" + serviceInst.getName().toUpperCase() + "_" + eventOp.getName().toUpperCase() + "_UID";

									eventReceivedsText += "   /* Call the Module Instance Queue Operation */" + LF + "   " + compInst.getName() + "_" + receiver.getReceiverInst().getName() + "_Controller__" + receiver.getReceiverOp().getName() + "__event_received(timestamp, " + serviceUID;

									for (SM_OperationParameter opParam : eventOp.getInputs()) {
										eventReceivedsText += ", " + opParam.getName();
									}
									eventReceivedsText += ");" + LF;
								}
							}
						}
					}
					eventReceivedsText += "   return ECOA__return_status_OK;" + LF + "}" + LF + LF;
				}
			}
		}

		// Replace the #EVENT_RECEIVEDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#EVENT_RECEIVEDS#", eventReceivedsText);
	}

	private void generateEventSends() {
		String eventSendsText = "/* Event Send Operations */" + LF + LF;

		for (SM_EventServiceOp eventOp : serviceInst.getServiceInterface().getEventOps()) {
			// Only generate for event sent (sent by provider for provided
			// services)
			if (eventOp.getDirection() == EventDirection.SENT_BY_PROVIDER) {
				eventSendsText += "void " + serviceAPIName + "_" + eventOp.getName() + "__event_send(ECOA__timestamp *timestamp";

				for (SM_OperationParameter opParam : eventOp.getInputs()) {
					eventSendsText += CLanguageSupport.writeConstParam(opParam);
				}

				if (isHeader) {
					eventSendsText += ");" + LF + LF;
					addTypeIncludes(eventOp.getInputs());
				} else {
					eventSendsText += ")" + LF + "{" + LF;

					boolean firstELI = true;

					// Get any wires for this component instance/service
					// instance.
					for (SM_Wire wire : compInst.getTargetWires(serviceInst)) {
						// Determine if destination component is in this
						// protection domain or not
						if (wire.getSource().getProtectionDomain() == pd) {
							String destServiceAPIName = wire.getSource().getName() + "_" + wire.getSourceOp().getName();

							// The destination is local - call the service
							// operation directly.
							eventSendsText += "      {" + LF + "        ECOA__uint32 uid = " + wire.getUID(eventOp).getUIDDefString() + ";" + LF + "        // Call the service operation directly as within our PD" + LF + "        " + destServiceAPIName + "_" + eventOp.getName() + "__event_received(uid, timestamp";

							for (SM_OperationParameter opParam : eventOp.getInputs()) {
								eventSendsText += ", " + opParam.getName();
							}

							eventSendsText += ");" + LF + "      }" + LF;

							includeList.add(destServiceAPIName + "_Controller");
						} else {
							// The destination is not local - use ELI.
							eventSendsText += generateEventSentELI(eventOp, wire, firstELI);
							firstELI = false;
						}
					}

					if (firstELI == false) {
						eventSendsText += LF + LF + "      /* Free the message buffer pointer */" + LF + "      free(message_buffer);" + LF + LF;
					}

					eventSendsText += "}" + LF + LF;
				}
			}
		}

		// Replace the #EVENT_SENDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#EVENT_SENDS#", eventSendsText);
	}

	private String generateEventSentELI(SM_EventServiceOp eventOp, SM_Wire wire, boolean firstELI) {
		String eventSendELI = "";
		SM_UIDServiceOp uid = wire.getUID(eventOp);

		includeList.add("ELI_Message");
		includeList.add(SEP_PATTERN_201);
		includeList.add(pd.getName() + "_ELI_Support");

		if (firstELI) {
			eventSendELI += "      int num_bytes;" + LF + "      ELIHeader header;" + LF + LF;

			if (eventOp.getInputs().size() > 0) {
				eventSendELI += "      unsigned int bufferSize = sizeof(ELIHeader)";
				for (SM_OperationParameter opParam : eventOp.getInputs()) {
					eventSendELI += " + sizeof(" + TypesProcessorC.convertParameterToC(opParam.getType()) + ")";
				}
				eventSendELI += ";" + LF;
			} else {
				eventSendELI += "      unsigned int bufferSize = sizeof(ELIHeader);" + LF;
			}

			eventSendELI += "      unsigned char *message_buffer = (unsigned char *)malloc(bufferSize);" + LF + "      unsigned char *message_buffer_ptr = message_buffer;" + LF + LF;
		}

		eventSendELI += "      header.ecoaMark = bswap16(0xEC0A);" + LF + "      header.version_domain = 0x11;" + LF + LF +

		// Always use real ELI messages for service operations - i.e. set to
		// platform ID (not PD ID)
				"      header.logicalPlatform = " + lcp.getRelatedUDPBinding().getPlatformID() + ";" + LF +

				"      header.ID = bswap32(" + uid.getUIDDefString() + ");" + LF + LF +

				"      header.seconds = bswap32(timestamp->seconds);" + LF + "      header.nanoseconds = bswap32(timestamp->nanoseconds);" + LF +
				// Don't use sequence number for events
				"      header.sequenceNumber = 0;" + LF + LF;

		if (firstELI) {
			eventSendELI += "      /* Change the message_buffer_ptr to point after header */" + LF + "      message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF;

			for (SM_OperationParameter opParam : eventOp.getInputs()) {
				String typeName = TypesProcessorC.convertParameterToC(opParam.getType());

				if (opParam.getType().isSimple()) {
					eventSendELI += "      serialise_" + typeName + "(" + opParam.getName() + ", &message_buffer_ptr);" + LF;
				} else {
					eventSendELI += "      serialise_" + typeName + "(*" + opParam.getName() + ", &message_buffer_ptr);" + LF;
				}
			}

			eventSendELI += "      num_bytes = message_buffer_ptr - message_buffer;" + LF + "      header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF;
		}

		eventSendELI += "      memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "      /* We can now send the ELI message */" + LF + "      " + pd.getName() + "_ELI_Support__SendELIMessage(message_buffer, num_bytes);" + LF + LF;

		return eventSendELI;
	}

	private void generateRequestReceiveds() {
		String requestReceivedsText = "";

		requestReceivedsText += "/* Request Received Operations */" + LF + LF;

		for (SM_RRServiceOp requestOp : serviceInst.getServiceInterface().getRROps()) {
			requestReceivedsText += "ECOA__return_status " + serviceAPIName + "_" + requestOp.getName() + "__request_received(ECOA__timestamp *timestamp, Client_Info_Type *serviceClientInfo";

			for (SM_OperationParameter opParam : requestOp.getInputs()) {
				requestReceivedsText += CLanguageSupport.writeConstParam(opParam);
			}

			if (isHeader) {
				requestReceivedsText += ");" + LF + LF;
				addTypeIncludes(requestOp.getInputs());
				includeList.add("Client_Info_Type");
			} else {
				requestReceivedsText += ")" + LF + "{" + LF;

				// Find all the request links which are connected to the service
				// instance.
				for (SM_RequestLink requestLink : compInst.getImplementation().getRequestLinks()) {
					includeList.add(compInst.getName() + "_" + requestLink.getServer().getServerInst().getName() + "_Controller");
					for (SM_ClientInterface clientInterface : requestLink.getClients()) {
						if (clientInterface.getClientInst() == serviceInst && clientInterface.getClientOp() == requestOp) {
							requestReceivedsText += "   /* Allocate a local seq number */" + LF + "   " + pd.getName() + "_PD_Controller__Allocate_Sequence_Number(&(serviceClientInfo->localSeqNum));" + LF + LF +

									"   /* Call the Module Instance Queue Operation */" + LF + "   serviceClientInfo->ID = " + compInst.getImplementation().getName().toUpperCase() + "_" + serviceInst.getName().toUpperCase() + "_" + requestOp.getName().toUpperCase() + "_UID;" + LF + "   " + compInst.getName() + "_" + requestLink.getServer().getServerInst().getName() + "_Controller__" + requestLink.getServer().getServerOp().getName() + "__request_received(timestamp, serviceClientInfo";

							for (SM_OperationParameter opParam : requestOp.getInputs()) {
								requestReceivedsText += ", " + opParam.getName();
							}
							requestReceivedsText += ");" + LF;
						}
					}
				}

				requestReceivedsText += "   return ECOA__return_status_OK;" + LF + "}" + LF + LF;
			}
		}

		// Replace the #REQUEST_RECEIVEDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#REQUEST_RECEIVEDS#", requestReceivedsText);
	}

	private String generateResponseELI(SM_RRServiceOp requestOp, SM_Wire wire) {
		SM_UIDServiceOp uid = wire.getUID(requestOp);

		includeList.add("ELI_Message");
		includeList.add(SEP_PATTERN_201);
		includeList.add(pd.getName() + "_ELI_Support");

		String responseSendELI = "         int num_bytes;" + LF + "         ELIHeader header;" + LF + LF;

		if (requestOp.getOutputs().size() > 0) {
			responseSendELI += "         unsigned int bufferSize = sizeof(ELIHeader)";
			for (SM_OperationParameter opParam : requestOp.getOutputs()) {
				responseSendELI += " + sizeof(" + TypesProcessorC.convertParameterToC(opParam.getType()) + ")";
			}
			responseSendELI += ";" + LF;
		} else {
			responseSendELI += "         unsigned int bufferSize = sizeof(ELIHeader);" + LF;
		}

		responseSendELI += "         unsigned char *message_buffer = (unsigned char *)malloc(bufferSize);" + LF + "         unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

				"         header.ecoaMark = bswap16(0xEC0A);" + LF + "         header.version_domain = 0x11;" + LF + LF +

				// Always use real ELI messages for service operations - i.e.
				// set to platform ID (not PD ID)
				"         header.logicalPlatform = " + lcp.getRelatedUDPBinding().getPlatformID() + ";" + LF + "         header.ID = bswap32(" + uid.getUIDDefString() + ");" + LF + LF +

				"         header.seconds = bswap32(timestamp->seconds);" + LF + "         header.nanoseconds = bswap32(timestamp->nanoseconds);" + LF +
				// Set the sequence number
				"         header.sequenceNumber = bswap32(clientInfo->globalSeqNum);" + LF + LF +

				"         /* Change the message_buffer_ptr to point after header */" + LF + "         message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF;

		for (SM_OperationParameter opParam : requestOp.getOutputs()) {
			String typeName = TypesProcessorC.convertParameterToC(opParam.getType());

			responseSendELI += "         serialise_" + typeName + "(*" + opParam.getName() + ", &message_buffer_ptr);" + LF;

		}

		responseSendELI += "         num_bytes = message_buffer_ptr - message_buffer;" + LF + "         header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "         memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "         /* We can now send the ELI message */" + LF + "         " + pd.getName() + "_ELI_Support__SendELIMessage(message_buffer, num_bytes);" + LF + "         free(message_buffer);" + LF + "         break;" + LF + "      }" + LF + LF;

		return responseSendELI;
	}

	private void generateResponseSends() {
		String responseSendsText = "/* Response Send Operations */" + LF + LF;

		for (SM_RRServiceOp requestOp : serviceInst.getServiceInterface().getRROps()) {
			responseSendsText += "ECOA__return_status " + serviceAPIName + "_" + requestOp.getName() + "__response_send(ECOA__timestamp *timestamp, Client_Info_Type *clientInfo";

			for (SM_OperationParameter opParam : requestOp.getOutputs()) {
				responseSendsText += CLanguageSupport.writeParam(opParam);
			}

			if (isHeader) {
				responseSendsText += ");" + LF + LF;
				addTypeIncludes(requestOp.getOutputs());
			} else {
				responseSendsText += ")" + LF + "{" + LF +

				// TODO - need to have a mapping back to "ELI_SeqNum here...
						"   switch (clientInfo->serviceUID)" + LF + "   {" + LF;

				// Get any wires for this component instance/service instance.
				for (SM_Wire wire : compInst.getTargetWires(serviceInst)) {
					SM_UIDServiceOp uid = wire.getUID(requestOp);

					responseSendsText += "      case " + uid.getUIDDefString() + ":" + LF + "      {" + LF;

					// Determine if destination (client) component is in this
					// protection domain or not
					if (wire.getSource().getProtectionDomain() == pd) {
						String destServiceAPIName = wire.getSource().getName() + "_" + wire.getSourceOp().getName();

						// The destination is local - call the service operation
						// directly.
						responseSendsText += "         ECOA__uint32 uid = " + wire.getUID(requestOp).getUIDDefString() + ";" + LF + "         ECOA__return_status responseStatus = ECOA__return_status_OK;" + LF + "         // Call the service operation directly as within our PD" + LF + "         " + destServiceAPIName + "_" + requestOp.getName() + "__response_received(uid, timestamp, responseStatus, clientInfo->globalSeqNum";

						for (SM_OperationParameter opParam : requestOp.getOutputs()) {
							responseSendsText += ", " + opParam.getName();
						}

						responseSendsText += ");" + LF + "         break;" + LF + "      }" + LF;

						includeList.add(destServiceAPIName + "_Controller");
					} else {
						// The destination is not local - use ELI.
						responseSendsText += generateResponseELI(requestOp, wire);
					}
				}

				responseSendsText += "   }" + LF + "   return ECOA__return_status_OK;" + LF + "}" + LF + LF;
			}
		}

		// Replace the #RESPONSE_SENDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#RESPONSE_SENDS#", responseSendsText);
	}

	private String generateVDWriteELI(SM_DataServiceOp vdOp, SM_Wire wire) {

		SM_UIDServiceOp uid = wire.getUID(vdOp);

		includeList.add("ELI_Message");
		includeList.add(SEP_PATTERN_201);
		includeList.add(pd.getName() + "_ELI_Support");

		String vdWriteELI = "   {" + LF + "   int num_bytes;" + LF + "   ELIHeader header;" + LF + LF +

				"   unsigned int bufferSize = sizeof(ELIHeader) + sizeof(" + TypesProcessorC.convertParameterToC(vdOp.getData().getType()) + ");" + LF + LF +

				"   unsigned char *message_buffer = (unsigned char *)malloc(bufferSize);" + LF + "   unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

				"   /* Set up the message */" + LF + "   header.ecoaMark = bswap16(0xEC0A);" + LF + "   header.version_domain = 0x11;" + LF + LF +

				// Always use real ELI messages for service operations - i.e.
				// set to platform ID (not PD ID)
				"   header.logicalPlatform = " + lcp.getRelatedUDPBinding().getPlatformID() + ";" + LF + "   header.ID = bswap32(" + uid.getUIDDefString() + ");" + LF + LF +

				"   header.seconds = bswap32(timestamp.seconds);" + LF + "   header.nanoseconds = bswap32(timestamp.nanoseconds);" + LF +
				// Don't use sequence number for VD
				"   header.sequenceNumber = 0;" + LF + LF +

				"   /* Change the message_buffer_ptr to point after header */" + LF + "   message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF;

		// Serialise the data
		String typeName = TypesProcessorC.convertParameterToC(vdOp.getData().getType());

		vdWriteELI += "   if (status == ECOA__return_status_OK)" + LF + "   {" + LF + "      serialise_" + typeName + "(newData, &message_buffer_ptr);" + LF + "      num_bytes = message_buffer_ptr - message_buffer;" + LF + "      header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "   }" + LF + "   else" + LF + "   {" + LF + "      num_bytes = sizeof(ELIHeader);" + LF + "      header.payloadSize = 0;" + LF + "   }" + LF + "   memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + LF +

				"   /* We can now send the ELI message */" + LF + "   " + pd.getName() + "_ELI_Support__SendELIMessage(message_buffer, num_bytes);" + LF + LF +

				"   /* Free the message buffer pointer */" + LF + "   free(message_buffer);" + LF + "   }" + LF + LF;

		return vdWriteELI;
	}

	private void generateVersionedDataWrites() {
		String versionDataWriteText = "";

		versionDataWriteText += "/* Versioned Data Write Operations */" + LF + LF;

		for (SM_DataServiceOp vdOp : serviceInst.getServiceInterface().getDataOps()) {
			includeList.add(vdOp.getData().getType().getNamespace().getName());

			versionDataWriteText += "ECOA__return_status " + serviceAPIName + "_" + vdOp.getName() + "__versioned_data_publish()" + LF;

			if (isHeader) {
				versionDataWriteText += ";" + LF;
				includeList.add(vdOp.getData().getType().getNamespace().getName());
			} else {
				versionDataWriteText += "{" + LF;

				// Read from the VD Repo
				for (SM_DataLink dataLink : compInst.getImplementation().getDataLinks()) {
					for (SM_ReaderInterface reader : dataLink.getReaders()) {
						if (reader instanceof SM_ReaderService && reader.getReaderOp() == vdOp) {
							SM_VDRepository vdRepo = dataLink.getVDRepo();
							String vdName = pd.getName() + "_" + compInst.getName() + "_VD" + vdRepo.getName();
							String vdDataType = vdName + "_DataType";
							includeList.add(vdName);

							versionDataWriteText += "   " + TypesProcessorC.convertParameterToC(vdOp.getData().getType()) + " newData;" + LF + "   ECOA__timestamp timestamp;" + LF + "   ECOA__return_status status;" + LF + "   ECOA__uint32 uid;" + LF + "   status = " + vdName + "__Read(&newData, &timestamp);" + LF + LF +

									"   if (status == ECOA__return_status_OK)" + LF + "   {" + LF;

							// Get any wires for this component instance/service
							// instance.
							for (SM_Wire wire : compInst.getTargetWires(serviceInst)) {
								// Determine if destination component is in this
								// protection domain or not
								if (wire.getSource().getProtectionDomain() == pd) {
									String destServiceAPIName = wire.getSource().getName() + "_" + wire.getSourceOp().getName();

									// The destination is local - call the
									// service operation directly.
									versionDataWriteText += "   // Call the service operation directly as within our PD" + LF + "   uid = " + wire.getUID(reader.getReaderOp()).getUIDDefString() + ";" + LF + "   " + destServiceAPIName + "_" + vdOp.getName() + "__versioned_data_update(uid, &newData, &timestamp);" + LF;

									includeList.add(destServiceAPIName + "_Controller");
								} else {
									// The destination is not local - use ELI.
									versionDataWriteText += generateVDWriteELI(vdOp, wire);
								}
							}
							versionDataWriteText +=
									// Close if on status.
									"}" + LF;
						}
					}
				}
				versionDataWriteText += "   return ECOA__return_status_OK;" + LF + "}" + LF + LF;
			}
		}

		// Replace the #VD_WRITES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#VD_WRITES#", versionDataWriteText);
	}

	@Override
	public void open() {

		if (isHeader) {
			super.openFile(outputDir.resolve("service/prov/inc/" + serviceAPIName + "_Controller.h"));
		} else {
			super.openFile(outputDir.resolve("service/prov/" + serviceAPIName + "_Controller.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#SET_SERVICE_AVAILABILITY#" + LF + "#CONTAINER_OPS#" + LF + "#EVENT_SENDS#" + LF + "#VD_WRITES#" + LF + "#MODULE_OPS#" + LF + "#EVENT_RECEIVEDS#" + LF + "#REQUEST_RECEIVEDS#" + LF + "#RESPONSE_SENDS#" + LF + "#INITIALISE#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#SET_SERVICE_AVAILABILITY#" + LF + "#CONTAINER_OPS#" + LF + "#EVENT_SENDS#" + LF + "#VD_WRITES#" + LF + "#MODULE_OPS#" + LF + "#EVENT_RECEIVEDS#" + LF + "#REQUEST_RECEIVEDS#" + LF + "#RESPONSE_SENDS#" + LF + "#INITIALISE#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeContainerToModuleCalls() {
		String containerToModuleCallsText = "/* ---------------------------------------------------------------------------------- */" + LF + "/* Container to Module Interface Operations */" + LF + "/* - The following functions are invoked by the container on a module instance */" + LF + LF;

		// Replace the #MODULE_OPS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#MODULE_OPS#", containerToModuleCallsText);

		generateEventReceiveds();
		generateRequestReceiveds();

	}

	public void writeIncludes() {
		if (isHeader) {
			includeList.addAll(underlyingPlatform.addIncludesServiceAPIHeader());
		} else {
			includeList.addAll(underlyingPlatform.addIncludesServiceAPIBody());
			includeList.add(pd.getName() + "_PD_Controller");

			includeList.add(serviceAPIName + "_Controller");
			includeList.add("Service_UID");
			includeList.add("ecoaByteswap");
			includeList.add("ecoaLog");
			includeList.add(pd.getName() + "_Service_Op_UID");
			includeList.add(compInst.getImplementation().getName() + "_Service_Instance_Operation_UID");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeInitialise() {
		String initialiseText = "void " + serviceAPIName + "__Initialise()";

		if (isHeader) {
			initialiseText += ";" + LF + LF;
		} else {
			initialiseText += LF + "{" + LF +

					"}" + LF + LF;
		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);

	}

	public void writeModuleToContainerCalls() {
		String moduleToContainerCallsText = "/* ---------------------------------------------------------------------------------- */" + LF + "/* Module to Container Interface Operations */" + LF + "/* - The following functions are called by the container */" + LF + LF;

		// Replace the #CONTAINER_OPS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#CONTAINER_OPS#", moduleToContainerCallsText);

		generateEventSends();
		generateResponseSends();
		generateVersionedDataWrites();
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + serviceAPIName + "_Controller.h */" + LF + "/* This is the Provided Service API (component instance - service instance) */" + LF;
		} else {
			preambleText += "/* File " + serviceAPIName + "_Controller.c */" + LF + "/* This is the Provided Service API (component instance - service instance) */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeSetServiceAvailability() {
		String serviceAvailabilityText = "";

		// Generate the set availability call.
		serviceAvailabilityText += "ECOA__return_status " + serviceAPIName + "__Set_Availability(ECOA__boolean8 available)";

		if (isHeader) {
			serviceAvailabilityText += ";" + LF;
		} else {
			serviceAvailabilityText += LF + "{" + LF + "   ECOA__timestamp opTimestamp;" + LF + "   ECOA_setTimestamp(&opTimestamp);" + LF + "   ECOA__boolean8 previouslyAvailable = ECOA__FALSE;" + LF + LF +

					"   /* Set the availability and then notify any components which require this service */" + LF;

			String serviceUIDString = compInst.getName().toUpperCase() + "_" + serviceInst.getName().toUpperCase() + "_UID";

			serviceAvailabilityText += "   // See if we were previously available" + LF + "   " + pd.getName() + "_Service_Manager__Get_Availability(" + serviceUIDString + ", &previouslyAvailable);" + LF + "   " + pd.getName() + "_Service_Manager__Set_Availability(" + serviceUIDString + ", available);" + LF + LF;

			serviceAvailabilityText += "   /* Call the PD manager to distribute the change in availability to requirers on a different PD */" + LF;
			// Get any wires for this component instance/service instance.
			for (SM_Wire wire : compInst.getTargetWires(serviceInst)) {
				// Determine if destination component is in this protection
				// domain or not (use ELI if not!)
				if (wire.getSource().getProtectionDomain() != pd) {
					// The destination is not local - use ELI.
					SM_ProtectionDomain remotePD = wire.getSource().getProtectionDomain();

					// Determine if the remote PD is within our platform (in
					// which case send it to the PD directly).
					if (lcp.getAllProtectionDomains().contains(remotePD)) {
						serviceAvailabilityText += "   " + pd.getName() + "_PD_Manager__Send_Single_Service_Availability(" + serviceUIDString + ", available, &opTimestamp, PD_IDS__" + remotePD.getName().toUpperCase() + ");" + LF + LF;
					}
				}
			}

			serviceAvailabilityText += "// Publish versioned data if the service has just become available." + LF + "   if (!previouslyAvailable && available)" + LF + "   {" + LF;
			for (SM_DataServiceOp vdOp : serviceInst.getServiceInterface().getDataOps()) {
				serviceAvailabilityText += serviceAPIName + "_" + vdOp.getName() + "__versioned_data_publish();" + LF;
			}
			serviceAvailabilityText += "}" + LF + LF;

			includeList.add("PD_IDS");
			includeList.add(pd.getName() + "_PD_Manager");
			serviceAvailabilityText += "   // Always distribute to the Platform Manager" + LF + "   " + pd.getName() + "_PD_Manager__Send_Single_Service_Availability(" + serviceUIDString + ", available, &opTimestamp, PD_IDS__" + lcp.getName().toUpperCase() + ");" + LF + LF +

					"   return ECOA__return_status_OK;" + LF + "}" + LF;
		}

		// Replace the #SET_SERVICE_AVAILABILITY# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SET_SERVICE_AVAILABILITY#", serviceAvailabilityText);
	}

}
