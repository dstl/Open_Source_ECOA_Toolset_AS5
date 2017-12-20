/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.pf2pdmanager;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingNode;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_DataServiceOp;
import com.iawg.ecoa.systemmodel.uid.SM_UIDServiceOp;

public class PFtoPDManagerWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_311 = "void ";
	private boolean isHeader;
	private String pf2pdManagerName;
	private PlatformManagerGenerator pfManagerGenerator;
	private SM_LogicalComputingPlatform lcp;
	private ArrayList<String> includeList = new ArrayList<String>();

	public PFtoPDManagerWriterC(PlatformManagerGenerator pfManagerGenerator, boolean isHeader, Path outputDir, SM_LogicalComputingPlatform lcp) {
		super(outputDir);
		this.isHeader = isHeader;
		this.lcp = lcp;
		this.pfManagerGenerator = pfManagerGenerator;
		this.pf2pdManagerName = lcp.getName() + "_PFtoPD_Manager";

		setFileStructure();
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve(pf2pdManagerName + ".h"));
		} else {
			super.openFile(outputDir.resolve(pf2pdManagerName + ".c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#PD_AVAIL_STRUCT#" + LF + "#GET_PD_STATUS#" + LF + "#SET_PD_STATUS#" + LF + "#SEND_PD_STATUS#" + LF + "#SEND_SERVICE_AVAIL_REQ#" + LF + "#SEND_SINGLE_SERVICE_AVAIL#" + LF + "#SEND_VERSIONED_DATA#" + LF + "#SEND_SINGLE_VERSIONED_DATA#" + LF + "#INITIALISE#" + LF + "#POSTSCRIPT#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#PD_AVAIL_DECL#" + LF + "#PDS_UP_DECL#" + LF + "#GET_PD_STATUS#" + LF + "#SET_PD_STATUS#" + LF + "#SEND_PD_STATUS#" + LF + "#SEND_SERVICE_AVAIL_REQ#" + LF + "#SEND_SINGLE_SERVICE_AVAIL#" + LF + "#SEND_VERSIONED_DATA#" + LF + "#SEND_SINGLE_VERSIONED_DATA#" + LF + "#INITIALISE#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeGetPDStatus() {
		String getPDText = SEP_PATTERN_311 + pf2pdManagerName + "__Get_PD_Status(ECOA__uint32 pdID, ELI_Message__PlatformStatus *status)";

		if (isHeader) {
			getPDText += ";" + LF + LF;
		} else {
			getPDText += LF + "{" + LF + "   int x;" + LF + "   for (x = 0; x < PD_Availability_List_MAXSIZE; x++) " + LF + "   {" + LF + "      if (" + lcp.getName() + "_pdAvailabilityList[x].pdID == pdID)" + LF + "      {" + LF + "         *status = " + lcp.getName() + "_pdAvailabilityList[x].pdStatus;" + LF + "      }" + LF + "   }" + LF + "}" + LF + LF;
		}

		// Replace the #GET_PD_STATUS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GET_PD_STATUS#", getPDText);
	}

	public void writeIncludes() {

		if (isHeader) {
			includeList.add("ECOA");
			includeList.add("ELI_Message");
		} else {
			includeList.add(pf2pdManagerName);
			includeList.add("ecoaByteswap");
			includeList.add("Component_Instance_ID");
			includeList.add("Service_UID");
			includeList.add(lcp.getName() + "_Service_Op_UID");
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
			initialiseText += SEP_PATTERN_311 + pf2pdManagerName + "__Initialise();" + LF + LF;
		} else {
			// Generate the Initialise function.
			initialiseText += SEP_PATTERN_311 + pf2pdManagerName + "__Initialise()" + LF + "{" + LF + LF +

					"   /* Initialise the PD availability list */" + LF;
			int pdNum = 0;
			for (SM_LogicalComputingNode node : lcp.getLogicalcomputingNodes()) {
				for (SM_ProtectionDomain aPDInThePlatform : node.getProtectionDomains()) {
					initialiseText += "   " + lcp.getName() + "_pdAvailabilityList[" + pdNum + "].pdID = PD_IDS__" + aPDInThePlatform.getName().toUpperCase() + ";" + LF + "   " + lcp.getName() + "_pdAvailabilityList[" + pdNum + "].pdStatus = ELI_Message__PlatformStatus_DOWN;" + LF;
					pdNum++;
				}
			}
			initialiseText += "   // Set the number of PDs up to 0." + LF + "   numOfPDsUp = 0;" + LF + "}" + LF;

		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);
	}

	public void writePDAvailabilityStruct() {
		int numPDsInPlatform = 0;

		for (SM_LogicalComputingNode node : lcp.getLogicalcomputingNodes()) {
			numPDsInPlatform += node.getProtectionDomains().size();
		}

		String pdAvailStructText = "/* Define the PD Availability Structure */" + LF +

				"typedef struct" + LF + "{" + LF + "   ECOA__uint32 pdID;" + LF + "   ELI_Message__PlatformStatus pdStatus;" + LF + "} PD_Availability_Type;" + LF + LF +

				"#define PD_Availability_List_MAXSIZE " + numPDsInPlatform + LF + "typedef PD_Availability_Type PD_Availability_List_Type[PD_Availability_List_MAXSIZE];" + LF + LF;

		// Replace the #PD_AVAIL_STRUCT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PD_AVAIL_STRUCT#", pdAvailStructText);

	}

	public void writePDAvailablityDecl() {
		String pdAvailDeclText = "/* Declare the PD Availability List */" + LF + "static PD_Availability_List_Type " + lcp.getName() + "_pdAvailabilityList;" + LF + LF;

		// Replace the #PD_AVAIL_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PD_AVAIL_DECL#", pdAvailDeclText);

	}

	public void writePostscript() {
		String postscriptText = "";

		if (isHeader) {
			postscriptText += "#endif /* " + lcp.getName().toUpperCase() + "_PF_MANAGER_H_ */" + LF;
		}

		// Replace the #POSTSCRIPT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#POSTSCRIPT#", postscriptText);
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + lcp.getName() + "_PFtoPD_Manager.h */" + LF + LF + "#ifndef " + lcp.getName().toUpperCase() + "_PFtoPD_MANAGER_H_" + LF + "#define " + lcp.getName().toUpperCase() + "_PFtoPD_MANAGER_H_" + LF;
		} else {
			preambleText += "/* File " + lcp.getName() + "_PFtoPD_Manager.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeNumOfPDsUpDecl() {

		String numOfPDsUpText = "   static int numOfPDsUp = 0;" + LF;

		// Replace the #PDS_UP_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PDS_UP_DECL#", numOfPDsUpText);
	}

	public void writeSendPDStatus() {
		String pdStateText = SEP_PATTERN_311 + pf2pdManagerName + "__Send_PD_Status(ECOA__uint32 pdID)";

		if (isHeader) {
			pdStateText += ";" + LF + LF;
		} else {
			includeList.add("ECOA_time_utils");
			includeList.add(lcp.getName() + "_ELI_Support");
			pdStateText += LF + "{" + LF + "   ECOA__timestamp timestamp;" + LF + "   PlatformStatus pdStatus;" + LF + "   ELIHeader header;" + LF + LF +

					"   int num_bytes = sizeof(ELIHeader) + sizeof(pdStatus);" + LF +

					"   unsigned char *message_buffer = (unsigned char *)malloc(num_bytes);" + LF + "   unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

					"   header.ecoaMark = bswap16(0xEC0A);" + LF + "   header.version_domain = 0x12;" + LF + LF +

					"   header.logicalPlatform = PD_IDS__" + lcp.getName().toUpperCase() + ";" + LF + "   header.ID = bswap32(ELI_Message__PlatformMessageID_PLATFORM_STATUS);" + LF + LF +

					"   /* Timestamp point */" + LF + "   ECOA_setTimestamp(&timestamp);" + LF + "   header.seconds = bswap32(timestamp.seconds);" + LF + "   header.nanoseconds = bswap32(timestamp.nanoseconds);" + LF +
					// Don't use sequence number for pd status message
					"   header.sequenceNumber = 0;" + LF + LF +

					"   /* Change the message_buffer_ptr to point after header */" + LF + "   message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF +

					"   /* Note that our PD status is always UP! */" + LF + "   PlatformELIPayload *pdStatusPayloadPtr = (PlatformELIPayload *)message_buffer_ptr;" + LF + "   pdStatusPayloadPtr->platformStatus.status = bswap32(ELI_Message__PlatformStatus_UP);" + LF +
					// TODO - currently the system model only holds one
					// composite - this may be incorrect.
					"   pdStatusPayloadPtr->platformStatus.compositeID = bswap32(" + pfManagerGenerator.getSystemModel().getFinalAssembly().getUID() + ");" + LF + LF +

					"   header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "   memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "   /* We can now send the ELI message */" + LF + "   " + lcp.getName() + "_ELI_Support__SendPDELIMessage(message_buffer, num_bytes, pdID);" + LF + "   free(message_buffer);" + LF + "}" + LF + LF;
		}

		// Replace the #SEND_PD_STATUS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_PD_STATUS#", pdStateText);
	}

	public void writeSendServiceAvailabilityRequest() {
		String sendServiceAvailReqText = SEP_PATTERN_311 + pf2pdManagerName + "__Send_Service_Availability_Request(ECOA__uint32 pdID)";

		if (isHeader) {
			sendServiceAvailReqText += ";" + LF + LF;
		} else {
			sendServiceAvailReqText += LF + "{" + LF + "   ECOA__timestamp timestamp;" + LF + "   int num_bytes;" + LF + "   ELIHeader header;" + LF + LF +

					"   unsigned char *message_buffer = (unsigned char *)malloc(1024*1024);" + LF + "   unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

					"   header.ecoaMark = bswap16(0xEC0A);" + LF + "   header.version_domain = 0x12;" + LF + LF +

					"   header.logicalPlatform = PD_IDS__" + lcp.getName().toUpperCase() + ";" + LF + "   header.ID = bswap32(ELI_Message__PlatformMessageID_AVAILABILITY_STATUS_REQUEST);" + LF + LF +

					"   /* Timestamp point */" + LF + "   ECOA_setTimestamp(&timestamp);" + LF + "   header.seconds = bswap32(timestamp.seconds);" + LF + "   header.nanoseconds = bswap32(timestamp.nanoseconds);" + LF +
					// Don't use sequence number for platform status message
					"   header.sequenceNumber = 0;" + LF + LF +

					"   /* Change the message_buffer_ptr to point after header */" + LF + "   message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF +

					"   AvailabilityStatusRequest *availabilityStatusRequestPtr = (AvailabilityStatusRequest *)message_buffer_ptr;" + LF + "   availabilityStatusRequestPtr->serviceID = 0xFFFFFFFF;" + LF +

					"   num_bytes = sizeof(ELIHeader) + sizeof(AvailabilityStatusRequest);" + LF + "   header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "   memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "   /* We can now send the ELI message */" + LF + "   " + lcp.getName() + "_ELI_Support__SendPDELIMessage(message_buffer, num_bytes, pdID);" + LF + "   free(message_buffer);" + LF + "}" + LF + LF;
		}

		// Replace the #SEND_SERVICE_AVAIL_REQ# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_SERVICE_AVAIL_REQ#", sendServiceAvailReqText);
	}

	public void writeSendSingleServiceAvailability() {
		String sendSingleServiceAvailabilityText = SEP_PATTERN_311 + pf2pdManagerName + "__Send_Single_Service_Availability(ECOA__uint32 serviceUID, ECOA__boolean8 availability, ECOA__timestamp *timestamp, ECOA__uint32 pdID)";

		if (isHeader) {
			sendSingleServiceAvailabilityText += ";" + LF + LF;
		} else {
			sendSingleServiceAvailabilityText += LF + "{" + LF + "   ELIHeader header;" + LF + LF +

					"   int num_bytes = sizeof(ELIHeader) + sizeof(AvailabilityStatus) + sizeof(ServiceAvailability);" + LF +

					"   unsigned char *message_buffer = (unsigned char *)malloc(num_bytes);" + LF + "   unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

					"   header.ecoaMark = bswap16(0xEC0A);" + LF + "   header.version_domain = 0x12;" + LF + LF +

					"   header.logicalPlatform = PD_IDS__" + lcp.getName().toUpperCase() + ";" + LF + "   header.ID = bswap32(ELI_Message__PlatformMessageID_AVAILABILITY_STATUS);" + LF + LF +

					"   /* Timestamp point */" + LF + "   header.seconds = bswap32(timestamp->seconds);" + LF + "   header.nanoseconds = bswap32(timestamp->nanoseconds);" + LF +
					// Don't use sequence number for platform status message
					"   header.sequenceNumber = 0;" + LF + LF +

					"   /* Change the message_buffer_ptr to point after header */" + LF + "   message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF +

					"   ECOA__uint32 *providedServicePtr = (ECOA__uint32 *)message_buffer_ptr;" + LF + "   ServiceAvailability *serviceAvailabilityPayloadPtr = (ServiceAvailability *)(message_buffer_ptr + sizeof(AvailabilityStatus));" + LF + LF +

					"   serviceAvailabilityPayloadPtr->serviceID = bswap32(serviceUID);" + LF + "   serviceAvailabilityPayloadPtr->availabilityState = bswap32(availability);" + LF + LF +

					"   *providedServicePtr = bswap32(1);" + LF + "   header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "   memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "   /* We can now send the ELI message */" + LF + "   " + lcp.getName() + "_ELI_Support__SendPDELIMessage(message_buffer, num_bytes, pdID);" + LF + "   free(message_buffer);" + LF + "}" + LF + LF;
		}

		// Replace the #SEND_SINGLE_SERVICE_AVAIL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_SINGLE_SERVICE_AVAIL#", sendSingleServiceAvailabilityText);
	}

	public void writeSendSingleVersionedData() {
		String sendSingleVDText = SEP_PATTERN_311 + pf2pdManagerName + "__Send_Single_Versioned_Data(ECOA__uint32 euid)";

		if (isHeader) {
			sendSingleVDText += ";" + LF + LF;
		} else {
			sendSingleVDText += LF + "{" + LF + LF + "   ECOA__uint32 pdID;" + LF + LF +

					"   ECOA__timestamp timestamp;" + LF + "   int num_bytes;" + LF + "   ELIHeader header;" + LF + LF +

					"   unsigned char *message_buffer = (unsigned char *)malloc(1024*1024);" + LF + "   unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

					"   header.ecoaMark = bswap16(0xEC0A);" + LF + "   header.version_domain = 0x12;" + LF + LF +

					"   header.logicalPlatform = PD_IDS__" + lcp.getName().toUpperCase() + ";" + LF + "   header.ID = bswap32(ELI_Message__PlatformMessageID_VERSIONED_DATA_PULL);" + LF + LF +

					"   /* Timestamp point */" + LF + "   ECOA_setTimestamp(&timestamp);" + LF + "   header.seconds = bswap32(timestamp.seconds);" + LF + "   header.nanoseconds = bswap32(timestamp.nanoseconds);" + LF +
					// Don't use sequence number for platform status message
					"   header.sequenceNumber = 0;" + LF + LF +

					"   /* Change the message_buffer_ptr to point after header */" + LF + "   message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF +

					"   VersionedDataPull *versionedDataPullPtr = (VersionedDataPull *)message_buffer_ptr;" + LF +

					"   num_bytes = sizeof(ELIHeader) + sizeof(VersionedDataPull);" + LF + "   header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + LF +

					"   switch (euid)" + LF + "   {" + LF;

			for (SM_ProtectionDomain pd : lcp.getAllProtectionDomains()) {
				for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
					for (SM_Wire wire : compInst.getTargetWires()) {
						// Only generate for this wire if at requirer is off
						// Platform
						if (!lcp.getAllProtectionDomains().contains(wire.getSource().getProtectionDomain())) {
							SM_ServiceInstance providedServiceInst = wire.getTargetOp();

							// Process all service versioned data ops (data
							// written)
							for (SM_DataServiceOp dataOp : providedServiceInst.getServiceInterface().getDataOps()) {
								SM_UIDServiceOp uid = wire.getUID(dataOp);

								sendSingleVDText += "      case " + uid.getUIDDefString() + ":" + LF + "         // Send a VD pull to the providing PD" + LF + "         versionedDataPullPtr->euid = bswap32(" + uid.getUIDDefString() + ");" + LF + "         pdID = PD_IDS__" + wire.getTarget().getProtectionDomain().getName().toUpperCase() + ";" + LF + "         break;" + LF;
							}
						}
					}
				}
			}
			sendSingleVDText += LF +
			// Close switch on euid
					"   }" + LF +

					"   memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "   /* We can now send the ELI message */" + LF + "   " + lcp.getName() + "_ELI_Support__SendPDELIMessage(message_buffer, num_bytes, pdID);" + LF + "   free(message_buffer);" + LF +

					// Close function
					"}" + LF;
		}

		// Replace the #SEND_SINGLE_VERSIONED_DATA# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_SINGLE_VERSIONED_DATA#", sendSingleVDText);
	}

	public void writeSendVersionedData() {
		String sendVDText = SEP_PATTERN_311 + pf2pdManagerName + "__Send_Versioned_Data(ECOA__uint32 platformID)";

		if (isHeader) {
			sendVDText += ";" + LF + LF;
		} else {
			includeList.add("ecoaByteswap");

			sendVDText += LF + "{" + LF + "   /* Send all versioned data we provide to the requesting platform - do this via single VD pull requests to the providing PD */" + LF + "   switch (platformID)" + LF + "   {" + LF;

			for (SM_LogicalComputingPlatform remoteLCP : pfManagerGenerator.getSystemModel().getLogicalSystem().getLogicalcomputingPlatforms()) {
				// Don't process for our platform...
				if (remoteLCP != lcp) {
					long remotePlatformID = remoteLCP.getRelatedUDPBinding().getPlatformID();
					sendVDText += "      case " + remotePlatformID + ":" + LF;

					for (SM_ProtectionDomain pd : lcp.getAllProtectionDomains()) {
						for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
							for (SM_Wire wire : compInst.getTargetWires()) {
								// Only generate for this wire if a requirer is
								// on the remote platform
								if (remoteLCP.getAllProtectionDomains().contains(wire.getSource().getProtectionDomain())) {
									SM_ServiceInstance providedServiceInst = wire.getTargetOp();

									// Process all service versioned data ops
									// (data written)
									for (SM_DataServiceOp dataOp : providedServiceInst.getServiceInterface().getDataOps()) {
										SM_UIDServiceOp uid = wire.getUID(dataOp);

										sendVDText += "         /* Send versioned data pull request for compInst: " + compInst.getName() + ", operation: " + dataOp.getName() + " */" + LF + "         // Send a VD pull to the providing PD" + LF + "         " + pf2pdManagerName + "__Send_Single_Versioned_Data(" + uid.getUIDDefString() + ");" + LF;
									}
								}
							}
						}
					}
					sendVDText += "      break;" + LF;
				}
			}
			sendVDText +=
					// Close switch on platformID
					"   }" + LF +
					// Close function
							"}" + LF;
		}

		// Replace the #SEND_VERSIONED_DATA# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_VERSIONED_DATA#", sendVDText);
	}

	public void writeSetPDStatus() {
		String updatePDText = SEP_PATTERN_311 + pf2pdManagerName + "__Set_PD_Status(ECOA__uint32 pdID, ELI_Message__PlatformStatus status)";

		if (isHeader) {
			updatePDText += ";" + LF + LF;
		} else {
			includeList.add(lcp.getName() + "_PFtoPF_Manager");
			updatePDText += LF + "{" + LF + "   int x;" + LF + "   for (x = 0; x < PD_Availability_List_MAXSIZE; x++) " + LF + "   {" + LF + "      if (" + lcp.getName() + "_pdAvailabilityList[x].pdID == pdID)" + LF + "      {" + LF + "         ELI_Message__PlatformStatus previousState = " + lcp.getName() + "_pdAvailabilityList[x].pdStatus;" + LF + "         " + lcp.getName() + "_pdAvailabilityList[x].pdStatus = status;" + LF + LF +

					"         if (previousState == ELI_Message__PlatformStatus_DOWN &&" + LF + "             status == ELI_Message__PlatformStatus_UP &&" + LF + "             pdID != PD_IDS__" + lcp.getName().toUpperCase() + ")" + LF + "         {" + LF + "            // Increment the number of PDs up" + LF + "            numOfPDsUp++;" + LF + "            " + pf2pdManagerName + "__Send_PD_Status(pdID);" + LF + "            " + pf2pdManagerName + "__Send_Service_Availability_Request(pdID);" + LF +
					// TODO - don't think we need to do this as not storing it
					// locally?!
					// " " + pf2pdManagerName +
					// "__Send_Versioned_Data_Request(pdID);" + LF +
					"         }" + LF + "         else if (previousState == ELI_Message__PlatformStatus_UP &&" + LF + "                status == ELI_Message__PlatformStatus_DOWN)" + LF + "         {" + LF + "            // Decrement the number of PDs up" + LF + "            numOfPDsUp--;" + LF + "         }" + LF + "         else if (previousState != status &&" + LF + "                  pdID == PD_IDS__" + lcp.getName().toUpperCase() + ")" + LF + "         {" + LF + "            /* Send our PD status as it has changed */" + LF;

			for (SM_ProtectionDomain remotePD : lcp.getAllProtectionDomains()) {
				updatePDText += "            " + pf2pdManagerName + "__Send_PD_Status(PD_IDS__" + remotePD.getName().toUpperCase() + ");" + LF;
			}
			// Also, send a message to the monitor!
			updatePDText += "         }" + LF + LF + "         unsigned char buffer[255];" + LF + "         int size;" + LF + LF +

					"         /* Log the PD Availability Change */" + LF + "         if (status)" + LF + "         {" + LF + "            size = sprintf((char*)buffer, (char*)\"$2_" + lcp.getName() + "/%d:UP\", pdID);" + LF + "         }" + LF + "         else" + LF + "         {" + LF + "            size = sprintf((char*)buffer, (char*)\"$2_" + lcp.getName() + "/%d:DOWN\", pdID);" + LF + "         }" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_MONITOR, 0);" + LF + LF +

					"      }" + LF + "   }" + LF + LF +

					"   // Determine if the platform is now up (i.e. all our protection domains are up)" + LF + "   if (numOfPDsUp == PD_Availability_List_MAXSIZE)" + LF + "   {" + LF + "      // Set the platform up" + LF + "      " + lcp.getName() + "_PFtoPF_Manager__Set_Platform_Status(" + lcp.getRelatedUDPBinding().getPlatformID() + ", ELI_Message__PlatformStatus_UP, 0);" + LF + "   }" + LF + "   else" + LF + "   {" + LF + "      // Set the platform down" + LF + "      " + lcp.getName() + "_PFtoPF_Manager__Set_Platform_Status(" + lcp.getRelatedUDPBinding().getPlatformID() + ", ELI_Message__PlatformStatus_DOWN, 0);" + LF + "   }" + LF +

					"}" + LF + LF;
		}

		// Replace the #SET_PD_STATUS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SET_PD_STATUS#", updatePDText);
	}
}
