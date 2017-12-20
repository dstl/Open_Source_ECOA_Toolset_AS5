/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.eliin;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingNode;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_DataServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp.EventDirection;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;

public class ELIInWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_201 = "void ";

	private boolean isHeader;
	private SM_LogicalComputingPlatform lcp;

	private ArrayList<String> includeList = new ArrayList<String>();
	private Generic_Platform underlyingPlatform;

	public ELIInWriterC(boolean isHeader, PlatformManagerGenerator pfManagerGenerator, SM_LogicalComputingPlatform lcp, Path outputDir) {
		super(outputDir);
		this.underlyingPlatform = pfManagerGenerator.getUnderlyingPlatformInstantiation();
		this.isHeader = isHeader;
		this.lcp = lcp;

		setFileStructure();
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve(lcp.getName() + "_ELI_In.h"));
		} else {
			super.openFile(outputDir.resolve(lcp.getName() + "_ELI_In.c"));
		}
	}

	private String processProvidedServices(SM_ComponentInstance compInst) {
		String providedServicesText = "";

		includeList.add("ELI_In__deserialiser");
		includeList.add(lcp.getName() + "_ELI_Support");

		for (SM_Wire wire : compInst.getTargetWires()) {
			// Only generate for this wire if requirer is off Platform
			if (!lcp.getAllProtectionDomains().contains(wire.getSource().getProtectionDomain())) {
				SM_ServiceInstance providedServiceInst = wire.getSourceOp();

				// Process all service events (received by provider)
				for (SM_EventServiceOp eventOp : providedServiceInst.getServiceInterface().getEventOps()) {
					if (eventOp.getDirection() == EventDirection.RECEIVED_BY_PROVIDER) {
						providedServicesText += "      case " + wire.getUID(eventOp).getUIDDefString() + ":" + LF + "         {" + LF + "            // Service Operation messages always use real ELI structure - simply send onwards..." + LF + "            " + lcp.getName() + "_ELI_Support__SendELIMessage((unsigned char*)header, messageSize, isFromPlatform);" + LF + "         }" + LF + "         break;" + LF;
					} else {
						providedServicesText += "      case " + wire.getUID(eventOp).getUIDDefString() + ":" + LF + "         {" + LF + "            // Service Operation messages always use real ELI structure - simply send onwards..." + LF + "            " + lcp.getName() + "_ELI_Support__SendELIMessage((unsigned char*)header, messageSize, isFromPlatform);" + LF + "         }" + LF + "         break;" + LF;
					}
				}

				// Process all service request-responses (server - request
				// received)
				for (SM_RRServiceOp rrOp : providedServiceInst.getServiceInterface().getRROps()) {
					// For request-response we need to determine if it's the
					// request or response (as the UID is the same).
					// We will do this using the "isFromPlatform" flag...
					providedServicesText += "      case " + wire.getUID(rrOp).getUIDDefString() + ":" + LF + "         {" + LF + "            // Service Operation messages always use real ELI structure - simply send onwards..." + LF + "            " + lcp.getName() + "_ELI_Support__SendELIMessage((unsigned char*)header, messageSize, isFromPlatform);" + LF + "         }" + LF + "         break;" + LF;
				}
				// Versioned Data is always published from the writer
				for (SM_DataServiceOp dataOp : providedServiceInst.getServiceInterface().getDataOps()) {
					providedServicesText += "      case " + wire.getUID(dataOp).getUIDDefString() + ":" + LF + "         {" + LF + "            // Service Operation messages always use real ELI structure - simply send onwards..." + LF + "            " + lcp.getName() + "_ELI_Support__SendELIMessage((unsigned char*)header, messageSize, isFromPlatform);" + LF + "         }" + LF + "         break;" + LF;
				}
			}
		}
		return providedServicesText;
	}

	// TODO
	private String processRequiredServices(SM_ComponentInstance compInst) {
		String requiredServicesText = "";

		for (SM_Wire wire : compInst.getSourceWires()) {
			// Only generate for this wire if provider is off Platform
			if (!lcp.getAllProtectionDomains().contains(wire.getTarget().getProtectionDomain())) {
				SM_ServiceInstance requiredServiceInst = wire.getSourceOp();

				// Process all service events (sent by provider)
				for (SM_EventServiceOp eventOp : requiredServiceInst.getServiceInterface().getEventOps()) {
					if (eventOp.getDirection() == EventDirection.SENT_BY_PROVIDER) {
						requiredServicesText += "      case " + wire.getUID(eventOp).getUIDDefString() + ":" + LF + "         {" + LF + "            // Service Operation messages always use real ELI structure - simply send onwards..." + LF + "            " + lcp.getName() + "_ELI_Support__SendELIMessage((unsigned char*)header, messageSize, isFromPlatform);" + LF + "         }" + LF + "         break;" + LF;
					} else {
						requiredServicesText += "      case " + wire.getUID(eventOp).getUIDDefString() + ":" + LF + "         {" + LF + "            // Service Operation messages always use real ELI structure - simply send onwards..." + LF + "            " + lcp.getName() + "_ELI_Support__SendELIMessage((unsigned char*)header, messageSize, isFromPlatform);" + LF + "         }" + LF + "         break;" + LF;
					}
				}

				// Process all service request-responses (client - response
				// received)
				for (SM_RRServiceOp rrOp : requiredServiceInst.getServiceInterface().getRROps()) {
					// For request-response we need to determine if it's the
					// request or response (as the UID is the same).
					// We will do this using the "isFromPlatform" flag...
					requiredServicesText += "      case " + wire.getUID(rrOp).getUIDDefString() + ":" + LF + "         {" + LF + "            // Service Operation messages always use real ELI structure - simply send onwards..." + LF + "            " + lcp.getName() + "_ELI_Support__SendELIMessage((unsigned char*)header, messageSize, isFromPlatform);" + LF + "         }" + LF + "         break;" + LF;
				}

				// Process all service versioned data (reader - update received)
				for (SM_DataServiceOp dataOp : requiredServiceInst.getServiceInterface().getDataOps()) {
					requiredServicesText += "      case " + wire.getUID(dataOp).getUIDDefString() + ":" + LF + "         {" + LF + "            // Service Operation messages always use real ELI structure - simply send onwards..." + LF + "            " + lcp.getName() + "_ELI_Support__SendELIMessage((unsigned char*)header, messageSize, isFromPlatform);" + LF + "         }" + LF + "         break;" + LF;
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
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#PROCESS_PLATFORM_ELI#" + LF + "#PROCESS_SERVICEOP_ELI#" + LF + "#PROCESS_PD_ELI#" + LF + "#PROCESS_ELI#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeIncludes() {
		if (!isHeader) {
			includeList.addAll(underlyingPlatform.addIncludesELIIn());
			includeList.add(lcp.getName() + "_Service_Op_UID");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + lcp.getName() + "_ELI_In.h */" + LF;
		} else {
			preambleText += "/* File " + lcp.getName() + "_ELI_In.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeProcessELI() {
		String processELIText = "";

		if (isHeader) {
			processELIText += SEP_PATTERN_201 + lcp.getName() + "_processELIMessage(unsigned char *message, unsigned int messageSize, ECOA__boolean8 isFromPlatform);" + LF;
		} else {
			includeList.add("ELI_Message");
			includeList.add(lcp.getName() + "_PFtoPD_Manager");
			includeList.add(lcp.getName() + "_PFtoPF_Manager");
			includeList.add("PD_IDS");

			processELIText += SEP_PATTERN_201 + lcp.getName() + "_processELIMessage(unsigned char *message, unsigned int messageSize, ECOA__boolean8 isFromPlatform)" + LF + "{" + LF + "   unsigned char *payload_ptr = message + sizeof(ELIHeader);" + LF + "   ELIHeader *header = (ELIHeader *)message;" + LF + LF +

					"   /* switch on the domain */" + LF + "   switch ((header->version_domain & 0xF))" + LF + "   {" + LF + "      case ELI_Message__Domain_PLATFORM:" + LF + "         " + lcp.getName() + "_processPlatformELI(payload_ptr, header);" + LF + "         break;" + LF + "      case ELI_Message__Domain_SERVICE_OP:" + LF + "         " + lcp.getName() + "_processServiceOpELI(payload_ptr, header, messageSize, isFromPlatform);" + LF + "         break;" + LF + "      case ELI_Message__Domain_PROTECTION_DOMAIN:" + LF + "         " + lcp.getName() + "_processPDELI(payload_ptr, header);" + LF + "         break;" + LF + "   }" + LF + LF +

					"   free(message);" + LF + "}" + LF + LF;
		}

		// Replace the #PROCESS_ELI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PROCESS_ELI#", processELIText);
	}

	public void writeProcessPlatformELI() {
		String processPlatformELIText = "";

		includeList.add("stdio");
		includeList.add("ECOA_time_utils");
		includeList.add(lcp.getName() + "_PFtoPF_Manager");
		includeList.add(lcp.getName() + "_PF_Service_Manager");

		processPlatformELIText += SEP_PATTERN_201 + lcp.getName() + "_processPlatformELI(unsigned char *payload_ptr, ELIHeader *header)" + LF + "{" + LF + "   PlatformELIPayload *PlatformELIPayloadptr = (PlatformELIPayload *)payload_ptr;" + LF + "   ECOA__uint32 ID = bswap32(header->ID);" + LF + "   unsigned char platformID = header->logicalPlatform;" + LF +

				"   unsigned char buffer[255];" + LF + "   int size;" + LF + LF +

				"   ELI_Message__PlatformStatus ourPlatformStatus;" + LF + "   " + lcp.getName() + "_PFtoPF_Manager__Get_Platform_Status(" + lcp.getRelatedUDPBinding().getPlatformID() + ", &ourPlatformStatus);" + LF + LF +

				"   /* Only proccess ELI messages if our platform is up */" + LF + "   if (ourPlatformStatus == ELI_Message__PlatformStatus_UP)" + LF + "   {" + LF + "      switch(ID) " + LF + "      {" + LF + "         case ELI_Message__PlatformMessageID_PLATFORM_STATUS:" + LF + "            /* Update the state of the remote platform */" + LF + "            " + lcp.getName() + "_PFtoPF_Manager__Set_Platform_Status(platformID, bswap32(PlatformELIPayloadptr->platformStatus.status), bswap32(PlatformELIPayloadptr->platformStatus.compositeID));" + LF + "            break;" + LF + "         case ELI_Message__PlatformMessageID_PLATFORM_STATUS_REQUEST:" + LF + "            /* Send our platform status */" + LF + "            " + lcp.getName() + "_PFtoPF_Manager__Send_Platform_Status(platformID);" + LF + "            break;" + LF + "         case ELI_Message__PlatformMessageID_AVAILABILITY_STATUS:" + LF + "            {" + LF + "               int num;" + LF + "               payload_ptr += sizeof(ECOA__uint32);" + LF + LF +

				"               for(num=0; num < bswap32(PlatformELIPayloadptr->availabilityStatus.providedServices); num++)" + LF + "               {" + LF + "                  ServiceAvailability *availState = (ServiceAvailability *)payload_ptr;" + LF + "                  " + lcp.getName() + "_PF_Service_Manager__Set_Availability(bswap32(availState->serviceID), bswap32(availState->availabilityState));" + LF + "                  payload_ptr += sizeof(ServiceAvailability);" + LF + "               }" + LF + "            }" + LF + "            break;" + LF + "         case ELI_Message__PlatformMessageID_AVAILABILITY_STATUS_REQUEST:" + LF + "            /* Send the availability status of services we provide */" + LF + "            if (PlatformELIPayloadptr->availabilityStatusRequest.serviceID == 0xFFFFFFFF)" + LF + "            {" + LF + "               /* Send all service availabilities */" + LF + "               " + lcp.getName() + "_PFtoPF_Manager__Send_Service_Availability(platformID);" + LF + "            }" + LF + "            else" + LF + "            {" + LF + "               /* Send single service availability */" + LF + "               ECOA__boolean8 available;" + LF
				+ "               ECOA__timestamp opTimestamp;" + LF + "               " + lcp.getName() + "_PF_Service_Manager__Get_Availability(PlatformELIPayloadptr->availabilityStatusRequest.serviceID, &available);" + LF + "               ECOA_setTimestamp(&opTimestamp);" + LF + "               " + lcp.getName() + "_PFtoPF_Manager__Send_Single_Service_Availability(bswap32(PlatformELIPayloadptr->availabilityStatusRequest.serviceID), available, &opTimestamp, platformID);" + LF + "            }" + LF + "            break;" + LF + "         case ELI_Message__PlatformMessageID_VERSIONED_DATA_PULL:" + LF + "            /* Send the versioned data of repositories we provide */" + LF + "            if (PlatformELIPayloadptr->versionedDataPull.euid == 0xFFFFFFFF)" + LF + "            {" + LF + "               /* Forward a VD pull request to any protection domain which provides Versioned Data to the requesting platform. */" + LF + "               " + lcp.getName() + "_PFtoPD_Manager__Send_Versioned_Data(platformID);" + LF + "            }" + LF + "            else" + LF + "            {" + LF + "               /* Forward a single versioned data pull request to the relevent PD*/" + LF
				+ "               " + lcp.getName() + "_PFtoPD_Manager__Send_Single_Versioned_Data(bswap32(PlatformELIPayloadptr->versionedDataPull.euid));" + LF + "            }" + LF + "            break;" + LF + "         case ELI_Message__PlatformMessageID_UNKNOWN_OPERATION:" + LF +
				// TODO - this should be somehow sent back to requesting
				// module?!
				"            size = sprintf((char*)buffer, (char*)\"ELI_In received UNKNOWN_OPERATION error\");" + LF + "            ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "            break;" + LF + "         case ELI_Message__PlatformMessageID_SERVICE_NOT_AVAILABLE:" + LF +
				// TODO - this should be somehow sent back to requesting
				// module?!
				"            size = sprintf((char*)buffer, (char*)\"ELI_In received SERVICE_NOT_AVAILABLE error\");" + LF + "            ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "            break;" + LF + "         default : " + LF + "            size = sprintf((char*)buffer, (char*)\"ELI_In received an unhandled message type\");" + LF + "            ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "      }" + LF + "   }" + LF + "}" + LF;

		// Replace the #PROCESS_PLATFORM_ELI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PROCESS_PLATFORM_ELI#", processPlatformELIText);
	}

	public void writeProcessPDELI() {
		String processPDELIText = "";

		includeList.add("stdio");
		includeList.add("ECOA_time_utils");
		includeList.add(lcp.getName() + "_PFtoPD_Manager");
		includeList.add(lcp.getName() + "_PF_Service_Manager");

		processPDELIText += SEP_PATTERN_201 + lcp.getName() + "_processPDELI(unsigned char *payload_ptr, ELIHeader *header)" + LF + "{" + LF + "   PlatformELIPayload *PlatformELIPayloadptr = (PlatformELIPayload *)payload_ptr;" + LF + "   ECOA__uint32 ID = bswap32(header->ID);" + LF + "   unsigned char pdID = header->logicalPlatform;" + LF +

				"   unsigned char buffer[255];" + LF + "   int size;" + LF + LF +

				"   switch(ID) " + LF + "   {" + LF + "      case ELI_Message__PlatformMessageID_PLATFORM_STATUS:" + LF + "         /* Update the state of the remote PD */" + LF + "         " + lcp.getName() + "_PFtoPD_Manager__Set_PD_Status(pdID, bswap32(PlatformELIPayloadptr->platformStatus.status));" + LF + "         break;" + LF + "      case ELI_Message__PlatformMessageID_PLATFORM_STATUS_REQUEST:" + LF + "         /* Send our PD status */" + LF + "         " + lcp.getName() + "_PFtoPD_Manager__Send_PD_Status(pdID);" + LF + "         break;" + LF + "      case ELI_Message__PlatformMessageID_AVAILABILITY_STATUS:" + LF + "         {" + LF + "            int num;" + LF + "            payload_ptr += sizeof(ECOA__uint32);" + LF + LF +

				"            for(num=0; num < bswap32(PlatformELIPayloadptr->availabilityStatus.providedServices); num++)" + LF + "            {" + LF + "               ServiceAvailability *availState = (ServiceAvailability *)payload_ptr;" + LF + "               " + lcp.getName() + "_PF_Service_Manager__Set_Availability(bswap32(availState->serviceID), bswap32(availState->availabilityState));" + LF + "               payload_ptr += sizeof(ServiceAvailability);" + LF + "            }" + LF + "         }" + LF + "         break;" + LF + "      case ELI_Message__PlatformMessageID_AVAILABILITY_STATUS_REQUEST:" + LF + "         size = sprintf((char*)buffer, (char*)\"PF Manager ELI_In received AVAILABILITY_STATUS_REQUEST from a PD which is invalid\\n\");" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "         break;" + LF + "         break;" + LF + "      case ELI_Message__PlatformMessageID_VERSIONED_DATA_PULL:" + LF + "         size = sprintf((char*)buffer, (char*)\"PF Manager ELI_In received VERSIONED_DATA_PULL from a PD which is invalid\\n\");" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "         break;" + LF
				+ "      case ELI_Message__PlatformMessageID_UNKNOWN_OPERATION:" + LF +
				// TODO - this should be somehow sent back to requesting
				// module?!
				"         size = sprintf((char*)buffer, (char*)\"ELI_In received UNKNOWN_OPERATION error\");" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "         break;" + LF + "      case ELI_Message__PlatformMessageID_SERVICE_NOT_AVAILABLE:" + LF +
				// TODO - this should be somehow sent back to requesting
				// module?!
				"         size = sprintf((char*)buffer, (char*)\"ELI_In received SERVICE_NOT_AVAILABLE error\");" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "         break;" + LF + "      default : " + LF + "         size = sprintf((char*)buffer, (char*)\"ELI_In received an unhandled message type\");" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "   }" + LF + "}" + LF;

		// Replace the #PROCESS_PD_ELI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PROCESS_PD_ELI#", processPDELIText);
	}

	public void writeProcessServiceOpELI() {
		String processServiceOpELIText = "";

		processServiceOpELIText += SEP_PATTERN_201 + lcp.getName() + "_processServiceOpELI(unsigned char *payload_ptr, ELIHeader *header, unsigned int messageSize, ECOA__boolean8 isFromPlatform)" + LF + "{" + LF + "   unsigned char buffer[255];" + LF + "   int size;" + LF + LF +

				"   ECOA__uint32 ID = bswap32(header->ID);" + LF + LF +

				"   switch(ID)" + LF + "   {" + LF + "      // Forward the Service Operation to the relevant place" + LF;

		// Need to forward this to correct PD or platform...
		for (SM_LogicalComputingNode node : lcp.getLogicalcomputingNodes()) {
			for (SM_ProtectionDomain pd : node.getProtectionDomains()) {
				for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
					// Process all provided services
					processServiceOpELIText += processProvidedServices(compInst);

					// Process all required services
					processServiceOpELIText += processRequiredServices(compInst);
				}
			}
		}

		processServiceOpELIText += "      default : " + LF + "         size = sprintf((char*)buffer, (char*)\"ELI_In received an unhandled service operation UID - %d \\n\", ID);" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "   }" + LF + "}" + LF;

		// Replace the #PROCESS_SERVICEOP_ELI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PROCESS_SERVICEOP_ELI#", processServiceOpELIText);
	}
}
