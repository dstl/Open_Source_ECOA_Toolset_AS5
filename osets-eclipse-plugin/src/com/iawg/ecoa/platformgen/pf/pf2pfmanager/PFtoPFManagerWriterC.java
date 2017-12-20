/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.pf2pfmanager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map.Entry;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class PFtoPFManagerWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_331 = "void ";
	private boolean isHeader;
	private String pf2pfManagerName;
	private PlatformManagerGenerator pfManagerGenerator;
	private SM_LogicalComputingPlatform lcp;
	private ArrayList<String> includeList = new ArrayList<String>();

	public PFtoPFManagerWriterC(PlatformManagerGenerator pfManagerGenerator, boolean isHeader, Path outputDir, SM_LogicalComputingPlatform lcp) {
		super(outputDir);
		this.isHeader = isHeader;
		this.lcp = lcp;
		this.pfManagerGenerator = pfManagerGenerator;
		this.pf2pfManagerName = lcp.getName() + "_PFtoPF_Manager";

		setFileStructure();
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve(pf2pfManagerName + ".h"));
		} else {
			super.openFile(outputDir.resolve(pf2pfManagerName + ".c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#PLATFORM_ID#" + LF + "#PF_AVAIL_STRUCT#" + LF + "#SEND_SERVICE_AVAIL_REQ#" + LF + "#SEND_VERSIONED_DATA_REQ#" + LF + "#GET_PF_STATUS#" + LF + "#SET_PF_STATUS#" + LF + "#SEND_PF_STATUS#" + LF + "#SEND_SERVICE_AVAIL#" + LF + "#SEND_SINGLE_SERVICE_AVAIL#" + LF + "#INITIALISE#" + LF + "#POSTSCRIPT#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#PF_AVAIL_DECL#" + LF + "#SEND_SERVICE_AVAIL_REQ#" + LF + "#SEND_VERSIONED_DATA_REQ#" + LF + "#GET_PF_STATUS#" + LF + "#SET_PF_STATUS#" + LF + "#SEND_PF_STATUS#" + LF + "#SEND_SERVICE_AVAIL#" + LF + "#SEND_SINGLE_SERVICE_AVAIL#" + LF + "#INITIALISE#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeGetPFStatus() {
		String getPFText = SEP_PATTERN_331 + pf2pfManagerName + "__Get_Platform_Status(ECOA__uint32 platformID, ELI_Message__PlatformStatus *status)";

		if (isHeader) {
			getPFText += ";" + LF + LF;
		} else {
			getPFText += LF + "{" + LF + "   int x;" + LF + "   for (x = 0; x < Platform_Availability_List_MAXSIZE; x++) " + LF + "   {" + LF + "      if (" + lcp.getName() + "_platformAvailabilityList[x].platformID == platformID)" + LF + "      {" + LF + "         *status = " + lcp.getName() + "_platformAvailabilityList[x].platformStatus;" + LF + "      }" + LF + "   }" + LF + "}" + LF + LF;
		}

		// Replace the #GET_PF_STATUS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GET_PF_STATUS#", getPFText);
	}

	public void writeIncludes() {

		if (isHeader) {
			includeList.add("ECOA");
			includeList.add("ELI_Message");
		} else {
			includeList.add(pf2pfManagerName);
			includeList.add("ecoaByteswap");
			includeList.add("Component_Instance_ID");
			includeList.add("Service_UID");
			// includeList.add(lcp.getName() + "_Service_Op_UID");
			includeList.add("PD_IDS");
			includeList.add("ecoaLog");
			includeList.add("stdlib");
			includeList.add("string");
			includeList.add("stdio");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeInitialise() {

		String initialiseText = "";

		if (isHeader) {
			initialiseText += SEP_PATTERN_331 + pf2pfManagerName + "__Initialise();" + LF + LF;
		} else {
			// Generate the Initialise function.
			initialiseText += SEP_PATTERN_331 + pf2pfManagerName + "__Initialise()" + LF + "{" + LF + LF +

					"   /* Initialise the PF availability list */" + LF;
			int platNum = 0;
			for (SM_LogicalComputingPlatform logCompPlat : pfManagerGenerator.getSystemModel().getLogicalSystem().getLogicalcomputingPlatforms()) {
				initialiseText += "   /* Platform ID = " + logCompPlat.getRelatedUDPBinding().getPlatformID() + " */" + LF + "   " + lcp.getName() + "_platformAvailabilityList[" + platNum + "].platformID = " + logCompPlat.getRelatedUDPBinding().getPlatformID() + ";" + LF + "   " + lcp.getName() + "_platformAvailabilityList[" + platNum + "].platformStatus = ELI_Message__PlatformStatus_DOWN;" + LF + "   " + lcp.getName() + "_platformAvailabilityList[" + platNum + "].compositeID = 0;" + LF;
				platNum++;
			}
			initialiseText += "}" + LF;

		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);
	}

	public void writePFAvailabilityStruct() {
		int numLogicalPlatforms = pfManagerGenerator.getSystemModel().getLogicalSystem().getLogicalcomputingPlatforms().size();

		String platAvailStructText = "/* Define the platform Availability Structure */" + LF +

				"typedef struct" + LF + "{" + LF + "   ECOA__uint32 platformID;" + LF + "   ELI_Message__PlatformStatus platformStatus;" + LF + "   ECOA__uint32 compositeID;" + LF + "} Platform_Availability_Type;" + LF + LF +

				"#define Platform_Availability_List_MAXSIZE " + numLogicalPlatforms + LF + "typedef Platform_Availability_Type Platform_Availability_List_Type[Platform_Availability_List_MAXSIZE];" + LF + LF;

		// Replace the #PF_AVAIL_STRUCT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PF_AVAIL_STRUCT#", platAvailStructText);

	}

	public void writePFAvailablityDecl() {
		String pfAvailDeclText = "/* Declare the Platform Availability List */" + LF + "static Platform_Availability_List_Type " + lcp.getName() + "_platformAvailabilityList;" + LF + LF;

		// Replace the #PF_AVAIL_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PF_AVAIL_DECL#", pfAvailDeclText);

	}

	public void writePostscript() {
		String postscriptText = "";

		if (isHeader) {
			postscriptText += "#endif /* " + lcp.getName().toUpperCase() + "_PFtoPF_MANAGER_H_ */" + LF;
		}

		// Replace the #POSTSCRIPT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#POSTSCRIPT#", postscriptText);
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + lcp.getName() + "_PFtoPF_Manager.h */" + LF + LF + "#ifndef " + lcp.getName().toUpperCase() + "_PFtoPF_MANAGER_H_" + LF + "#define " + lcp.getName().toUpperCase() + "_PFtoPF_MANAGER_H_" + LF;
		} else {
			preambleText += "/* File " + lcp.getName() + "_PFtoPF_Manager.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeSendPFStatus() {
		String pfStateText = SEP_PATTERN_331 + pf2pfManagerName + "__Send_Platform_Status(ECOA__uint32 platformID)";

		if (isHeader) {
			pfStateText += ";" + LF + LF;
		} else {
			includeList.add("ECOA_time_utils");
			includeList.add(lcp.getName() + "_ELI_Support");
			pfStateText += LF + "{" + LF + "   ECOA__timestamp timestamp;" + LF + "   PlatformStatus platformStatus;" + LF + "   ELIHeader header;" + LF + LF +

					"   int num_bytes = sizeof(ELIHeader) + sizeof(PlatformStatus);" + LF +

					"   unsigned char *message_buffer = (unsigned char *)malloc(num_bytes);" + LF + "   unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

					"   header.ecoaMark = bswap16(0xEC0A);" + LF + "   header.version_domain = 0x10;" + LF + LF +

					"   header.logicalPlatform = " + lcp.getRelatedUDPBinding().getPlatformID() + ";" + LF + "   header.ID = bswap32(ELI_Message__PlatformMessageID_PLATFORM_STATUS);" + LF +

					"   /* Timestamp point */" + LF + "   ECOA_setTimestamp(&timestamp);" + LF + "   header.seconds = bswap32(timestamp.seconds);" + LF + "   header.nanoseconds = bswap32(timestamp.nanoseconds);" + LF +
					// Don't use sequence number for platform status message
					"   header.sequenceNumber = 0;" + LF + LF +

					"   /* Change the message_buffer_ptr to point after header */" + LF + "   message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF +

					"   /* Get our platform status */" + LF + "   " + pf2pfManagerName + "__Get_Platform_Status(" + lcp.getRelatedUDPBinding().getPlatformID() + ", &platformStatus);" + LF + LF +

					"   PlatformELIPayload *platformStatusPayloadPtr = (PlatformELIPayload *)message_buffer_ptr;" + LF + "   platformStatusPayloadPtr->platformStatus.status = bswap32(platformStatus.status);" + LF +
					// TODO - currently the system model only holds one
					// composite - this may be incorrect.
					"   platformStatusPayloadPtr->platformStatus.compositeID = bswap32(" + pfManagerGenerator.getSystemModel().getFinalAssembly().getUID() + ");" + LF + LF +

					"   header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "   memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "   /* We can now send the ELI message */" + LF + "   " + lcp.getName() + "_ELI_Support__SendPlatformELIMessage(message_buffer, num_bytes, platformID);" + LF + "   free(message_buffer);" + LF + "}" + LF + LF;
		}

		// Replace the #SEND_PF_STATUS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_PF_STATUS#", pfStateText);
	}

	public void writeSendServiceAvailability() {
		String sendServiceAvailabilityText = SEP_PATTERN_331 + pf2pfManagerName + "__Send_Service_Availability(ECOA__uint32 platformID)";

		if (isHeader) {
			sendServiceAvailabilityText += ";" + LF + LF;
		} else {
			sendServiceAvailabilityText += LF + "{" + LF + "   ECOA__timestamp timestamp;" + LF + "   int num_bytes;" + LF + "   ECOA__boolean8 availability;" + LF + "   ELIHeader header;" + LF + LF +

					"   unsigned char *message_buffer = (unsigned char *)malloc(1024*1024);" + LF + "   unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

					"   header.ecoaMark = bswap16(0xEC0A);" + LF + "   header.version_domain = 0x10;" + LF + LF +

					"   header.logicalPlatform = " + lcp.getRelatedUDPBinding().getPlatformID() + ";" + LF + "   header.ID = bswap32(ELI_Message__PlatformMessageID_AVAILABILITY_STATUS);" + LF + LF +

					"   /* Timestamp point */" + LF + "   ECOA_setTimestamp(&timestamp);" + LF + "   header.seconds = bswap32(timestamp.seconds);" + LF + "   header.nanoseconds = bswap32(timestamp.nanoseconds);" + LF +
					// Don't use sequence number for platform status message
					"   header.sequenceNumber = 0;" + LF + LF +

					"   /* Change the message_buffer_ptr to point after header */" + LF + "   message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF +

					"   ECOA__uint32 *providedServicePtr = (ECOA__uint32 *)message_buffer_ptr;" + LF + "   ServiceAvailability *serviceAvailabilityPayloadPtr = (ServiceAvailability *)(message_buffer_ptr + sizeof(AvailabilityStatus));" + LF + LF +

					"   switch (platformID)" + LF + "   {" + LF;

			includeList.add(lcp.getName() + "_PF_Service_Manager");

			for (Entry<SM_LogicalComputingPlatform, ArrayList<SM_Wire>> entry : lcp.getMapOfProvidedServicesToPlatform().entrySet()) {
				int numOfServicesProvided = 0;

				sendServiceAvailabilityText += "      case " + pf2pfManagerName + "__Platform_ID_Type" + "_" + entry.getKey().getName() + ":" + LF;

				for (SM_Wire wire : entry.getValue()) {
					String serviceUIDString = wire.getTarget().getName().toUpperCase() + "_" + wire.getTargetOp().getName().toUpperCase() + "_UID";

					sendServiceAvailabilityText += "         " + lcp.getName() + "_PF_Service_Manager__Get_Availability(" + serviceUIDString + ", &availability);" + LF + "         serviceAvailabilityPayloadPtr->serviceID = bswap32(" + serviceUIDString + ");" + LF + "         serviceAvailabilityPayloadPtr->availabilityState = bswap32(availability);" + LF + "         /* Move the pointer on... */" + LF + "         serviceAvailabilityPayloadPtr++;" + LF + LF;
					numOfServicesProvided++;
				}

				sendServiceAvailabilityText += "         *providedServicePtr = bswap32(" + numOfServicesProvided + ");" + LF + "         break;" + LF;
			}

			sendServiceAvailabilityText +=
					// close switch on platformID
					"      default:" + LF + "         *providedServicePtr = bswap32(0);" + LF + "         break;" + LF + "   }" + LF + LF +

							"   num_bytes = (unsigned char *)serviceAvailabilityPayloadPtr - message_buffer;" + LF + "   header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "   memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF +

							"   /* We can now send the ELI message */" + LF + "   " + lcp.getName() + "_ELI_Support__SendPlatformELIMessage(message_buffer, num_bytes, platformID);" + LF + "   free(message_buffer);" + LF +

							"}" + LF + LF;
		}

		// Replace the #SEND_SERVICE_AVAIL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_SERVICE_AVAIL#", sendServiceAvailabilityText);
	}

	public void writeSendServiceAvailabilityRequest() {
		String sendServiceAvailReqText = SEP_PATTERN_331 + pf2pfManagerName + "__Send_Service_Availability_Request(ECOA__uint32 platformID)";

		if (isHeader) {
			sendServiceAvailReqText += ";" + LF + LF;
		} else {
			sendServiceAvailReqText += LF + "{" + LF + "   ECOA__timestamp timestamp;" + LF + "   int num_bytes;" + LF + "   ELIHeader header;" + LF + LF +

					"   unsigned char *message_buffer = (unsigned char *)malloc(1024*1024);" + LF + "   unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

					"   header.ecoaMark = bswap16(0xEC0A);" + LF + "   header.version_domain = 0x10;" + LF + LF +

					"   header.logicalPlatform = " + lcp.getRelatedUDPBinding().getPlatformID() + ";" + LF + "   header.ID = bswap32(ELI_Message__PlatformMessageID_AVAILABILITY_STATUS_REQUEST);" + LF + LF +

					"   /* Timestamp point */" + LF + "   ECOA_setTimestamp(&timestamp);" + LF + "   header.seconds = bswap32(timestamp.seconds);" + LF + "   header.nanoseconds = bswap32(timestamp.nanoseconds);" + LF +
					// Don't use sequence number for platform status message
					"   header.sequenceNumber = 0;" + LF + LF +

					"   /* Change the message_buffer_ptr to point after header */" + LF + "   message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF +

					"   AvailabilityStatusRequest *availabilityStatusRequestPtr = (AvailabilityStatusRequest *)message_buffer_ptr;" + LF + "   availabilityStatusRequestPtr->serviceID = 0xFFFFFFFF;" + LF +

					"   num_bytes = sizeof(ELIHeader) + sizeof(AvailabilityStatusRequest);" + LF + "   header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "   memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "   /* We can now send the ELI message */" + LF + "   " + lcp.getName() + "_ELI_Support__SendPlatformELIMessage(message_buffer, num_bytes, platformID);" + LF + "   free(message_buffer);" + LF + "}" + LF + LF;
		}

		// Replace the #SEND_SERVICE_AVAIL_REQ# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_SERVICE_AVAIL_REQ#", sendServiceAvailReqText);
	}

	public void writeSendSingleServiceAvailability() {
		String sendSingleServiceAvailabilityText = SEP_PATTERN_331 + pf2pfManagerName + "__Send_Single_Service_Availability(ECOA__uint32 serviceUID, ECOA__boolean8 availability, ECOA__timestamp *timestamp, ECOA__uint32 platformID)";

		if (isHeader) {
			sendSingleServiceAvailabilityText += ";" + LF + LF;
		} else {
			sendSingleServiceAvailabilityText += LF + "{" + LF + "   ELIHeader header;" + LF + LF +

					"   int num_bytes = sizeof(ELIHeader) + sizeof(AvailabilityStatus) + sizeof(ServiceAvailability);" + LF +

					"   unsigned char *message_buffer = (unsigned char *)malloc(num_bytes);" + LF + "   unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

					"   header.ecoaMark = bswap16(0xEC0A);" + LF + "   header.version_domain = 0x10;" + LF + LF +

					"   header.logicalPlatform = " + lcp.getRelatedUDPBinding().getPlatformID() + ";" + LF + "   header.ID = bswap32(ELI_Message__PlatformMessageID_AVAILABILITY_STATUS);" + LF + LF +

					"   /* Timestamp point */" + LF + "   header.seconds = bswap32(timestamp->seconds);" + LF + "   header.nanoseconds = bswap32(timestamp->nanoseconds);" + LF +
					// Don't use sequence number for platform status message
					"   header.sequenceNumber = 0;" + LF + LF +

					"   /* Change the message_buffer_ptr to point after header */" + LF + "   message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF +

					"   ECOA__uint32 *providedServicePtr = (ECOA__uint32 *)message_buffer_ptr;" + LF + "   ServiceAvailability *serviceAvailabilityPayloadPtr = (ServiceAvailability *)(message_buffer_ptr + sizeof(AvailabilityStatus));" + LF + LF +

					"   serviceAvailabilityPayloadPtr->serviceID = bswap32(serviceUID);" + LF + "   serviceAvailabilityPayloadPtr->availabilityState = bswap32(availability);" + LF + LF +

					"   *providedServicePtr = bswap32(1);" + LF + "   header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "   memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "   /* We can now send the ELI message */" + LF + "   " + lcp.getName() + "_ELI_Support__SendPlatformELIMessage(message_buffer, num_bytes, platformID);" + LF + "   free(message_buffer);" + LF + "}" + LF + LF;
		}

		// Replace the #SEND_SINGLE_SERVICE_AVAIL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_SINGLE_SERVICE_AVAIL#", sendSingleServiceAvailabilityText);
	}

	public void writeSendVersionedDataRequest() {
		String sendVDReqText = SEP_PATTERN_331 + pf2pfManagerName + "__Send_Versioned_Data_Request(ECOA__uint32 platformID)";

		if (isHeader) {
			sendVDReqText += ";" + LF + LF;
		} else {
			sendVDReqText += LF + "{" + LF + "   ECOA__timestamp timestamp;" + LF + "   int num_bytes;" + LF + "   ELIHeader header;" + LF + LF +

					"   unsigned char *message_buffer = (unsigned char *)malloc(1024*1024);" + LF + "   unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

					"   header.ecoaMark = bswap16(0xEC0A);" + LF + "   header.version_domain = 0x10;" + LF + LF +

					"   header.logicalPlatform = " + lcp.getRelatedUDPBinding().getPlatformID() + ";" + LF + "   header.ID = bswap32(ELI_Message__PlatformMessageID_VERSIONED_DATA_PULL);" + LF + LF +

					"   /* Timestamp point */" + LF + "   ECOA_setTimestamp(&timestamp);" + LF + "   header.seconds = bswap32(timestamp.seconds);" + LF + "   header.nanoseconds = bswap32(timestamp.nanoseconds);" + LF +
					// Don't use sequence number for platform status message
					"   header.sequenceNumber = 0;" + LF + LF +

					"   /* Change the message_buffer_ptr to point after header */" + LF + "   message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF +

					"   VersionedDataPull *versionedDataPullPtr = (VersionedDataPull *)message_buffer_ptr;" + LF + "   versionedDataPullPtr->euid = 0xFFFFFFFF;" + LF +

					"   num_bytes = sizeof(ELIHeader) + sizeof(VersionedDataPull);" + LF + "   header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "   memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "   /* We can now send the ELI message */" + LF + "   " + lcp.getName() + "_ELI_Support__SendPlatformELIMessage(message_buffer, num_bytes, platformID);" + LF + "   free(message_buffer);" + LF + "}" + LF + LF;
		}

		// Replace the #SEND_VERSIONED_DATA_REQ# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_VERSIONED_DATA_REQ#", sendVDReqText);
	}

	public void writeSetPFStatus() {
		String updatePlatformText = SEP_PATTERN_331 + pf2pfManagerName + "__Set_Platform_Status(ECOA__uint32 platformID, ELI_Message__PlatformStatus status, ECOA__uint32 compositeID)";

		if (isHeader) {
			updatePlatformText += ";" + LF + LF;
		} else {
			updatePlatformText += LF + "{" + LF + "   int x;" + LF + "   for (x = 0; x < Platform_Availability_List_MAXSIZE; x++) " + LF + "   {" + LF + "      if (" + lcp.getName() + "_platformAvailabilityList[x].platformID == platformID)" + LF + "      {" + LF + "         ELI_Message__PlatformStatus previousState = " + lcp.getName() + "_platformAvailabilityList[x].platformStatus;" + LF + "         " + lcp.getName() + "_platformAvailabilityList[x].platformStatus = status;" + LF + "         " + lcp.getName() + "_platformAvailabilityList[x].compositeID = compositeID;" + LF + LF +

					"         if (previousState == ELI_Message__PlatformStatus_DOWN &&" + LF + "             status == ELI_Message__PlatformStatus_UP &&" + LF + "             platformID != " + lcp.getRelatedUDPBinding().getPlatformID() + ")" + LF + "         {" + LF + "            " + pf2pfManagerName + "__Send_Platform_Status(platformID);" + LF + "            " + pf2pfManagerName + "__Send_Service_Availability_Request(platformID);" + LF + "            " + pf2pfManagerName + "__Send_Versioned_Data_Request(platformID);" + LF + "         }" + LF + "         else if (previousState != status &&" + LF + "                  platformID == " + lcp.getRelatedUDPBinding().getPlatformID() + ")" + LF + "         {" + LF + "            /* Send our platform status as it has changed */" + LF;

			for (SM_LogicalComputingPlatform remoteLCP : pfManagerGenerator.getSystemModel().getLogicalSystem().getLogicalcomputingPlatforms()) {
				if (remoteLCP != lcp) {
					updatePlatformText += "            " + pf2pfManagerName + "__Send_Platform_Status(" + remoteLCP.getRelatedUDPBinding().getPlatformID() + ");" + LF;
				}
			}

			// Also, send a message to the monitor!
			updatePlatformText += "            unsigned char buffer[255];" + LF + "            int size;" + LF + LF +

					"            /* Log the Platform Availability Change */" + LF + "            if (status)" + LF + "            {" + LF + "               size = sprintf((char*)buffer, (char*)\"$1_" + lcp.getName() + ":UP\");" + LF + "            }" + LF + "            else" + LF + "            {" + LF + "               size = sprintf((char*)buffer, (char*)\"$1_" + lcp.getName() + ":DOWN\");" + LF + "            }" + LF + "            ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_MONITOR, 0);" + LF + LF +

					"         }" + LF + "      }" + LF + "   }" + LF + "}" + LF + LF;
		}

		// Replace the #SET_PF_STATUS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SET_PF_STATUS#", updatePlatformText);
	}

	public void writePlatformIDEnum() {
		String enumName = pf2pfManagerName + "__Platform_ID_Type";

		String platformIDText = "typedef ECOA__uint32 " + enumName + ";" + LF;

		for (SM_LogicalComputingPlatform logCompPlat : pfManagerGenerator.getSystemModel().getLogicalSystem().getLogicalcomputingPlatforms()) {
			platformIDText += "#define " + enumName + "_" + logCompPlat.getName() + " (" + logCompPlat.getRelatedUDPBinding().getPlatformID() + ")" + LF;
		}

		// Replace the #PLATFORM_ID# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PLATFORM_ID#", platformIDText);
	}

}
