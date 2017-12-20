/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.eliin;

import java.nio.file.Path;
import java.util.ArrayList;

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
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_DataServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp.EventDirection;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;

public class ELIInWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_141 = "void ";

	private boolean isHeader;
	private SM_ProtectionDomain pd;

	private ArrayList<String> includeList = new ArrayList<String>();
	private Generic_Platform underlyingPlatform;

	public ELIInWriterC(boolean isHeader, PlatformGenerator platformGenerator, SM_ProtectionDomain protectionDomain, Path outputDir) {
		super(outputDir);
		this.underlyingPlatform = platformGenerator.getunderlyingPlatformInstantiation();
		this.isHeader = isHeader;
		this.pd = protectionDomain;

		setFileStructure();
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve(pd.getName() + "_ELI_In.h"));
		} else {
			super.openFile(outputDir.resolve(pd.getName() + "_ELI_In.c"));
		}
	}

	private String processProvidedServices(SM_ComponentInstance compInst) {
		String providedServicesText = "";

		includeList.add("ELI_In__deserialiser");

		for (SM_Wire wire : compInst.getTargetWires()) {
			// Only generate for this wire if at requirer is off Protection
			// domain
			if (!pd.getComponentInstances().contains(wire.getSource())) {
				SM_ServiceInstance providedServiceInst = wire.getSourceOp();

				includeList.add(compInst.getName() + "_" + wire.getTargetOp().getName() + "_Controller");

				// Process all service events (received by provider)
				for (SM_EventServiceOp eventOp : providedServiceInst.getServiceInterface().getEventOps()) {
					if (eventOp.getDirection() == EventDirection.RECEIVED_BY_PROVIDER) {
						providedServicesText += "      case " + wire.getUID(eventOp).getUIDDefString() + ":" + LF + "         {" + LF + "            // Deserialise parameters" + LF;

						for (SM_OperationParameter param : eventOp.getInputs()) {
							String typeName = TypesProcessorC.convertParameterToC(param.getType());
							includeList.add(param.getType().getNamespace().getName());

							providedServicesText += "            " + typeName + " " + param.getName() + ";" + LF + "            deserialise_" + typeName + "(&payload_ptr, &" + param.getName() + ");" + LF;
						}

						providedServicesText += "            // Invoke service API" + LF + "            " + compInst.getName() + "_" + wire.getTargetOp().getName() + "_" + eventOp.getName() + "__event_received(&timestamp";

						for (SM_OperationParameter param : eventOp.getInputs()) {
							if (param.getType().isSimple()) {
								providedServicesText += ", " + param.getName();
							} else {
								providedServicesText += ", &" + param.getName();
							}
						}

						providedServicesText += ");" + LF + "         }" + LF + "         break;" + LF;
					}
				}

				// Process all service request-responses (server - request
				// received)
				for (SM_RRServiceOp rrOp : providedServiceInst.getServiceInterface().getRROps()) {
					providedServicesText += "      case " + wire.getUID(rrOp).getUIDDefString() + ":" + LF + "         {" + LF + "            // Deserialise parameters" + LF;

					for (SM_OperationParameter param : rrOp.getInputs()) {
						String typeName = TypesProcessorC.convertParameterToC(param.getType());
						includeList.add(param.getType().getNamespace().getName());

						providedServicesText += "            " + typeName + " " + param.getName() + ";" + LF + "            deserialise_" + typeName + "(&payload_ptr, &" + param.getName() + ");" + LF;
					}
					includeList.add("Client_Info_Type");
					providedServicesText += "            Client_Info_Type serviceClientInfo;" + LF + "            serviceClientInfo.type = Client_Type__SERVICE_OPERATION;" + LF + "            serviceClientInfo.ID = 0; // Not used for service operations" + LF + "            serviceClientInfo.serviceUID = " + wire.getUID(rrOp).getUIDDefString() + ";" + LF + "            serviceClientInfo.localSeqNum = 0; // Set in the service API" + LF + "            serviceClientInfo.globalSeqNum = seqNum; // This is the ELI seq number" + LF +

							"            // Invoke service API" + LF + "            " + compInst.getName() + "_" + wire.getTargetOp().getName() + "_" + rrOp.getName() + "__request_received(&timestamp, &serviceClientInfo";

					for (SM_OperationParameter param : rrOp.getInputs()) {
						if (param.getType().isSimple()) {
							providedServicesText += ", " + param.getName();
						} else {
							providedServicesText += ", &" + param.getName();
						}
					}

					providedServicesText += ");" + LF + "         }" + LF + "         break;" + LF;
				}

				// Versioned Data is always published from the writer, hence
				// nothing to handle here.
			}
		}
		return providedServicesText;
	}

	private String processRequiredServices(SM_ComponentInstance compInst) {
		String requiredServicesText = "";

		for (SM_Wire wire : compInst.getSourceWires()) {
			// Only generate for this wire if at provider is off Protection
			// domain
			includeList.add(compInst.getName() + "_" + wire.getSourceOp().getName() + "_Controller");
			if (!pd.getComponentInstances().contains(wire.getTarget())) {
				SM_ServiceInstance requiredServiceInst = wire.getSourceOp();

				// Process all service events (sent by provider)
				for (SM_EventServiceOp eventOp : requiredServiceInst.getServiceInterface().getEventOps()) {
					if (eventOp.getDirection() == EventDirection.SENT_BY_PROVIDER) {
						requiredServicesText += "      case " + wire.getUID(eventOp).getUIDDefString() + ":" + LF + "         {" + LF + "            // Deserialise parameters" + LF;

						for (SM_OperationParameter param : eventOp.getInputs()) {
							String typeName = TypesProcessorC.convertParameterToC(param.getType());
							includeList.add(param.getType().getNamespace().getName());

							requiredServicesText += "            " + typeName + " " + param.getName() + ";" + LF + "            deserialise_" + typeName + "(&payload_ptr, &" + param.getName() + ");" + LF;
						}

						requiredServicesText += "            ECOA__uint32 uid = " + wire.getUID(eventOp).getUIDDefString() + ";" + LF + "            // Invoke service API" + LF + "            " + compInst.getName() + "_" + wire.getSourceOp().getName() + "_" + eventOp.getName() + "__event_received(uid, &timestamp";

						for (SM_OperationParameter param : eventOp.getInputs()) {
							if (param.getType().isSimple()) {
								requiredServicesText += ", " + param.getName();
							} else {
								requiredServicesText += ", &" + param.getName();
							}
						}

						requiredServicesText += ");" + LF + "         }" + LF + "         break;" + LF;
					}
				}

				// Process all service request-responses (client - response
				// received)
				for (SM_RRServiceOp rrOp : requiredServiceInst.getServiceInterface().getRROps()) {
					requiredServicesText += "      case " + wire.getUID(rrOp).getUIDDefString() + ":" + LF + "         {" + LF + "            // Deserialise parameters" + LF;

					for (SM_OperationParameter param : rrOp.getOutputs()) {
						String typeName = TypesProcessorC.convertParameterToC(param.getType());
						includeList.add(param.getType().getNamespace().getName());

						requiredServicesText += "            " + typeName + " " + param.getName() + ";" + LF + "            deserialise_" + typeName + "(&payload_ptr, &" + param.getName() + ");" + LF;
					}

					requiredServicesText += "            ECOA__uint32 uid = " + wire.getUID(rrOp).getUIDDefString() + ";" + LF + "            ECOA__return_status responseStatus = ECOA__return_status_OK;" + LF + "            // Invoke service API" + LF + "            " + compInst.getName() + "_" + wire.getSourceOp().getName() + "_" + rrOp.getName() + "__response_received(uid, &timestamp, responseStatus, seqNum";

					for (SM_OperationParameter param : rrOp.getOutputs()) {
						requiredServicesText += ", &" + param.getName();
					}

					requiredServicesText += ");" + LF + "         }" + LF + "         break;" + LF;
				}

				// Process all service versioned data (reader - update received)
				for (SM_DataServiceOp dataOp : requiredServiceInst.getServiceInterface().getDataOps()) {
					String typeName = TypesProcessorC.convertParameterToC(dataOp.getData().getType());
					includeList.add(dataOp.getData().getType().getNamespace().getName());

					requiredServicesText += "      case " + wire.getUID(dataOp).getUIDDefString() + ":" + LF + "         {" + LF + "            if (payloadSize != 0)" + LF + "            {" + LF + "               // Deserialise data" + LF + "               " + typeName + " " + dataOp.getName() + ";" + LF + "               deserialise_" + typeName + "(&payload_ptr, &" + dataOp.getName() + ");" + LF + LF +

							"               ECOA__uint32 uid = " + wire.getUID(dataOp).getUIDDefString() + ";" + LF + "               // Invoke service API" + LF + "               " + compInst.getName() + "_" + wire.getSourceOp().getName() + "_" + dataOp.getName() + "__versioned_data_update(uid, &" + dataOp.getName() + ", &timestamp);" + LF + "            }" + LF + "         }" + LF + "         break;" + LF;
				}
			}
		}

		return requiredServicesText;
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#PROCESS_ELI#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#PROCESS_PLATFORM_ELI#" + LF + "#PROCESS_SERVICEOP_ELI#" + LF + "#PROCESS_ELI#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeIncludes() {
		if (!isHeader) {
			includeList.addAll(underlyingPlatform.addIncludesELIIn());
			includeList.add(pd.getName() + "_Service_Op_UID");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + pd.getName() + "_ELI_In.h */" + LF;
		} else {
			preambleText += "/* File " + pd.getName() + "_ELI_In.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeProcessELI() {
		String processELIText = "";

		if (isHeader) {
			processELIText += SEP_PATTERN_141 + pd.getName() + "_processELIMessage(unsigned char *message, unsigned int messageSize);" + LF;
		} else {
			includeList.add("ELI_Message");
			includeList.add(pd.getName() + "_PD_Manager");
			includeList.add("PD_IDS");

			processELIText += SEP_PATTERN_141 + pd.getName() + "_processELIMessage(unsigned char *message, unsigned int messageSize)" + LF + "{" + LF + "   ELI_Message__PlatformStatus pdStatus;" + LF +

					"   " + pd.getName() + "_PD_Manager__Get_PD_Status(PD_IDS__" + pd.getName().toUpperCase() + ", &pdStatus);" + LF + LF +

					"   /* Only proccess ELI messages if our PD is up */" + LF + "   if (pdStatus == ELI_Message__PlatformStatus_UP)" + LF + "   {" + LF + "      unsigned char *payload_ptr = message + sizeof(ELIHeader);" + LF + "      ELIHeader *header = (ELIHeader *)message;" + LF + LF +

					"      /* switch on the domain */" + LF + "      switch ((header->version_domain & 0xF))" + LF + "      {" + LF + "         case ELI_Message__Domain_PROTECTION_DOMAIN:" + LF + "            " + pd.getName() + "_processPDELI(payload_ptr, header);" + LF + "            break;" + LF + "         case ELI_Message__Domain_SERVICE_OP:" + LF + "            " + pd.getName() + "_processServiceOpELI(payload_ptr, header);" + LF + "            break;" + LF + "      }" + LF + "   }" + LF + LF +

					"   free(message);" + LF + "}" + LF + LF;
		}

		// Replace the #PROCESS_ELI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PROCESS_ELI#", processELIText);
	}

	public void writeProcessPDELI() {
		String processPDELIText = "";

		includeList.add("stdio");
		includeList.add("ECOA_time_utils");
		includeList.add(pd.getName() + "_PD_Manager");
		includeList.add(pd.getName() + "_Service_Manager");

		processPDELIText += SEP_PATTERN_141 + pd.getName() + "_processPDELI(unsigned char *payload_ptr, ELIHeader *header)" + LF + "{" + LF + "   PlatformELIPayload *PlatformELIPayloadptr = (PlatformELIPayload *)payload_ptr;" + LF + "   ECOA__uint32 ID = bswap32(header->ID);" + LF + "   unsigned char pdID = header->logicalPlatform;" + LF +

				"   unsigned char buffer[255];" + LF + "   int size;" + LF + LF +

				"   switch(ID) " + LF + "   {" + LF + "      case ELI_Message__PlatformMessageID_PLATFORM_STATUS:" + LF + "         /* Update the state of the remote PD */" + LF + "         " + pd.getName() + "_PD_Manager__Set_PD_Status(pdID, bswap32(PlatformELIPayloadptr->platformStatus.status));" + LF + "         break;" + LF + "      case ELI_Message__PlatformMessageID_PLATFORM_STATUS_REQUEST:" + LF + "         /* Send our PD status */" + LF + "         " + pd.getName() + "_PD_Manager__Send_PD_Status(pdID);" + LF + "         break;" + LF + "      case ELI_Message__PlatformMessageID_AVAILABILITY_STATUS:" + LF + "         {" + LF + "            int num;" + LF + "            payload_ptr += sizeof(ECOA__uint32);" + LF + LF +

				"            for(num=0; num < bswap32(PlatformELIPayloadptr->availabilityStatus.providedServices); num++)" + LF + "            {" + LF + "               ServiceAvailability *availState = (ServiceAvailability *)payload_ptr;" + LF + "               " + pd.getName() + "_Service_Manager__Set_Availability(bswap32(availState->serviceID), bswap32(availState->availabilityState));" + LF + "               payload_ptr += sizeof(ServiceAvailability);" + LF + "            }" + LF + "         }" + LF + "         break;" + LF + "      case ELI_Message__PlatformMessageID_AVAILABILITY_STATUS_REQUEST:" + LF + "         /* Send the availability status of services we provide */" + LF + "         if (PlatformELIPayloadptr->availabilityStatusRequest.serviceID == 0xFFFFFFFF)" + LF + "         {" + LF + "            /* Send all service availabilities */" + LF + "            " + pd.getName() + "_PD_Manager__Send_Service_Availability(pdID);" + LF + "         }" + LF + "         else" + LF + "         {" + LF + "            /* Send single service availability */" + LF + "            ECOA__boolean8 available;" + LF + "            ECOA__timestamp opTimestamp;" + LF + "            "
				+ pd.getName() + "_Service_Manager__Get_Availability(PlatformELIPayloadptr->availabilityStatusRequest.serviceID, &available);" + LF + "            ECOA_setTimestamp(&opTimestamp);" + LF + "            " + pd.getName() + "_PD_Manager__Send_Single_Service_Availability(bswap32(PlatformELIPayloadptr->availabilityStatusRequest.serviceID), available, &opTimestamp, pdID);" + LF + "         }" + LF + "         break;" + LF + "      case ELI_Message__PlatformMessageID_VERSIONED_DATA_PULL:" + LF + "         /* Send the versioned data of repositories we provide */" + LF + "         if (PlatformELIPayloadptr->versionedDataPull.euid == 0xFFFFFFFF)" + LF + "         {" + LF + "            /* Send all versioned data */" + LF + "            " + pd.getName() + "_PD_Manager__Send_Versioned_Data(pdID);" + LF + "         }" + LF + "         else" + LF + "         {" + LF + "            /* Send single versioned data */" + LF + "            " + pd.getName() + "_PD_Manager__Send_Single_Versioned_Data(bswap32(PlatformELIPayloadptr->versionedDataPull.euid));" + LF + "         }" + LF + "         break;" + LF + "      case ELI_Message__PlatformMessageID_UNKNOWN_OPERATION:" + LF +
				// TODO - this should be somehow sent back to requesting
				// module?!
				"         size = sprintf((char*)buffer, (char*)\"ELI_In received UNKNOWN_OPERATION error\");" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "         break;" + LF + "      case ELI_Message__PlatformMessageID_SERVICE_NOT_AVAILABLE:" + LF +
				// TODO - this should be somehow sent back to requesting
				// module?!
				"         size = sprintf((char*)buffer, (char*)\"ELI_In received SERVICE_NOT_AVAILABLE error\");" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "         break;" + LF + "      default : " + LF + "         size = sprintf((char*)buffer, (char*)\"ELI_In received an unhandled message type\");" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "   }" + LF + "}" + LF;

		// Replace the #PROCESS_PLATFORM_ELI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PROCESS_PLATFORM_ELI#", processPDELIText);
	}

	public void writeProcessServiceOpELI() {
		String processServiceOpELIText = "";

		processServiceOpELIText += SEP_PATTERN_141 + pd.getName() + "_processServiceOpELI(unsigned char *payload_ptr, ELIHeader *header)" + LF + "{" + LF + "   unsigned char buffer[255];" + LF + "   int size;" + LF + LF +

				"   ECOA__timestamp timestamp;" + LF + "   ECOA__uint32 seqNum, payloadSize = 0;" + LF + "   ECOA__uint32 ID = bswap32(header->ID);" + LF + LF +

				"   timestamp.seconds = bswap32(header->seconds);" + LF + "   timestamp.nanoseconds = bswap32(header->nanoseconds);" + LF + "   seqNum = bswap32(header->sequenceNumber);" + LF + "   payloadSize = bswap32(header->payloadSize);" + LF + LF +

				"   switch(ID)" + LF + "   {" + LF + "      // Deserialize and invoke Service API" + LF;

		for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
			// Process all provided services
			processServiceOpELIText += processProvidedServices(compInst);

			// Process all required services
			processServiceOpELIText += processRequiredServices(compInst);
		}

		processServiceOpELIText += "      default : " + LF + "         size = sprintf((char*)buffer, (char*)\"ELI_In received an unhandled service operation UID - %d \\n\", ID);" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "   }" + LF + "}" + LF;

		// Replace the #PROCESS_SERVICEOP_ELI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PROCESS_SERVICEOP_ELI#", processServiceOpELIText);
	}

}
