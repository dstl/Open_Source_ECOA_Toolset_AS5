/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.pdmanager;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map.Entry;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingNode;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_DataServiceOp;
import com.iawg.ecoa.systemmodel.uid.SM_UIDServiceOp;

public class PDManagerWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_351 = "void ";
	private boolean isHeader;
	private SM_ProtectionDomain pd;
	private String pdManagerName;
	private PlatformGenerator platformGenerator;
	private SM_LogicalComputingPlatform lcp;
	private ArrayList<String> includeList = new ArrayList<String>();

	public PDManagerWriterC(PlatformGenerator platformGenerator, boolean isHeader, Path outputDir, SM_ProtectionDomain pd) {
		super(outputDir);
		this.isHeader = isHeader;
		this.pd = pd;
		this.platformGenerator = platformGenerator;
		this.lcp = pd.getLogicalComputingNode().getLogicalComputingPlatform();
		this.pdManagerName = pd.getName() + "_PD_Manager";

		setFileStructure();
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve(pdManagerName + ".h"));
		} else {
			super.openFile(outputDir.resolve(pdManagerName + ".c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#PD_AVAIL_STRUCT#" + LF + "#SEND_SERVICE_AVAIL_REQ#" + LF + "#SEND_VERSIONED_DATA_REQ#" + LF + "#GET_PD_STATUS#" + LF + "#SET_PD_STATUS#" + LF + "#SEND_PD_STATUS#" + LF + "#SEND_SERVICE_AVAIL#" + LF + "#SEND_SINGLE_SERVICE_AVAIL#" + LF + "#SEND_VERSIONED_DATA#" + LF + "#SEND_SINGLE_VERSIONED_DATA#" + LF + "#INITIALISE#" + LF + "#POSTSCRIPT#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#PD_AVAIL_DECL#" + LF + "#SEND_SERVICE_AVAIL_REQ#" + LF + "#SEND_VERSIONED_DATA_REQ#" + LF + "#GET_PD_STATUS#" + LF + "#SET_PD_STATUS#" + LF + "#SEND_PD_STATUS#" + LF + "#SEND_SERVICE_AVAIL#" + LF + "#SEND_SINGLE_SERVICE_AVAIL#" + LF + "#SEND_VERSIONED_DATA#" + LF + "#SEND_SINGLE_VERSIONED_DATA#" + LF + "#INITIALISE#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeGetPDStatus() {
		String getPDText = SEP_PATTERN_351 + pdManagerName + "__Get_PD_Status(ECOA__uint32 pdID, ELI_Message__PlatformStatus *status)";

		if (isHeader) {
			getPDText += ";" + LF + LF;
		} else {
			getPDText += LF + "{" + LF + "   int x;" + LF + "   for (x = 0; x < PD_Availability_List_MAXSIZE; x++) " + LF + "   {" + LF + "      if (" + pd.getName() + "_pdAvailabilityList[x].pdID == pdID)" + LF + "      {" + LF + "         *status = " + pd.getName() + "_pdAvailabilityList[x].pdStatus;" + LF + "      }" + LF + "   }" + LF + "}" + LF + LF;
		}

		// Replace the #GET_PD_STATUS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GET_PD_STATUS#", getPDText);
	}

	public void writeIncludes() {

		if (isHeader) {
			includeList.add("ECOA");
			includeList.add("ELI_Message");
		} else {
			includeList.add(pdManagerName);
			includeList.add("ecoaByteswap");
			includeList.add("Component_Instance_ID");
			includeList.add("Service_UID");
			includeList.add(pd.getName() + "_Service_Op_UID");
			includeList.add("PD_IDS");
			includeList.add("stdlib");
			includeList.add("string");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeInitialise() {

		String initialiseText = "";

		if (isHeader) {
			initialiseText += SEP_PATTERN_351 + pdManagerName + "__Initialise();" + LF + LF;
		} else {
			// Generate the Initialise function.
			initialiseText += SEP_PATTERN_351 + pdManagerName + "__Initialise()" + LF + "{" + LF + LF +

					"   /* Initialise the PD availability list */" + LF + "   " + pd.getName() + "_pdAvailabilityList[0].pdID = PD_IDS__" + lcp.getName().toUpperCase() + ";" + LF + "   " + pd.getName() + "_pdAvailabilityList[0].pdStatus = ELI_Message__PlatformStatus_DOWN;" + LF;
			int pdNum = 1;
			for (SM_LogicalComputingNode node : lcp.getLogicalcomputingNodes()) {
				for (SM_ProtectionDomain aPDInThePlatform : node.getProtectionDomains()) {
					initialiseText += "   " + pd.getName() + "_pdAvailabilityList[" + pdNum + "].pdID = PD_IDS__" + aPDInThePlatform.getName().toUpperCase() + ";" + LF + "   " + pd.getName() + "_pdAvailabilityList[" + pdNum + "].pdStatus = ELI_Message__PlatformStatus_DOWN;" + LF;
					pdNum++;
				}
			}
			initialiseText += "}" + LF;

		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);
	}

	public void writePDAvailabilityStruct() {
		// Always need an extra 1 for the Platform Manager!
		int numPDsInPlatform = 1;

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
		String pdAvailDeclText = "/* Declare the PD Availability List */" + LF + "static PD_Availability_List_Type " + pd.getName() + "_pdAvailabilityList;" + LF + LF;

		// Replace the #PD_AVAIL_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PD_AVAIL_DECL#", pdAvailDeclText);

	}

	public void writePostscript() {
		String postscriptText = "";

		if (isHeader) {
			postscriptText += "#endif /* " + pd.getName().toUpperCase() + "_PD_MANAGER_H_ */" + LF;
		}

		// Replace the #POSTSCRIPT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#POSTSCRIPT#", postscriptText);
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + pd.getName() + "_PD_Manager.h */" + LF + LF + "#ifndef " + pd.getName().toUpperCase() + "_PD_MANAGER_H_" + LF + "#define " + pd.getName().toUpperCase() + "_PD_MANAGER_H_" + LF;
		} else {
			preambleText += "/* File " + pd.getName() + "_PD_Manager.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeSendPDStatus() {
		String pdStateText = SEP_PATTERN_351 + pdManagerName + "__Send_PD_Status(ECOA__uint32 pdID)";

		if (isHeader) {
			pdStateText += ";" + LF + LF;
		} else {
			includeList.add("ECOA_time_utils");
			includeList.add(pd.getName() + "_ELI_Support");
			pdStateText += LF + "{" + LF + "   ECOA__timestamp timestamp;" + LF + "   PlatformStatus pdStatus;" + LF + "   ELIHeader header;" + LF + LF +

					"   int num_bytes = sizeof(ELIHeader) + sizeof(pdStatus);" + LF +

					"   unsigned char *message_buffer = (unsigned char *)malloc(num_bytes);" + LF + "   unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

					"   header.ecoaMark = bswap16(0xEC0A);" + LF + "   header.version_domain = 0x12;" + LF + LF +

					"   header.logicalPlatform = PD_IDS__" + pd.getName().toUpperCase() + ";" + LF + "   header.ID = bswap32(ELI_Message__PlatformMessageID_PLATFORM_STATUS);" + LF + LF +

					"   /* Timestamp point */" + LF + "   ECOA_setTimestamp(&timestamp);" + LF + "   header.seconds = bswap32(timestamp.seconds);" + LF + "   header.nanoseconds = bswap32(timestamp.nanoseconds);" + LF +
					// Don't use sequence number for pd status message
					"   header.sequenceNumber = 0;" + LF + LF +

					"   /* Change the message_buffer_ptr to point after header */" + LF + "   message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF +

					"   /* Get our pd status */" + LF + "   " + pdManagerName + "__Get_PD_Status(PD_IDS__" + pd.getName().toUpperCase() + ", &pdStatus);" + LF + LF +

					"   PlatformELIPayload *pdStatusPayloadPtr = (PlatformELIPayload *)message_buffer_ptr;" + LF + "   pdStatusPayloadPtr->platformStatus.status = bswap32(pdStatus.status);" + LF +
					// TODO - currently the system model only holds one
					// composite - this may be incorrect.
					"   pdStatusPayloadPtr->platformStatus.compositeID = bswap32(" + platformGenerator.getSystemModel().getFinalAssembly().getUID() + ");" + LF + LF +

					"   header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "   memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "   /* We can now send the ELI message */" + LF + "   " + pd.getName() + "_ELI_Support__SendPDELIMessage(message_buffer, num_bytes, pdID);" + LF + "   free(message_buffer);" + LF + "}" + LF + LF;
		}

		// Replace the #SEND_PD_STATUS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_PD_STATUS#", pdStateText);
	}

	public void writeSendServiceAvailability() {
		String sendServiceAvailabilityText = SEP_PATTERN_351 + pdManagerName + "__Send_Service_Availability(ECOA__uint32 pdID)";

		if (isHeader) {
			sendServiceAvailabilityText += ";" + LF + LF;
		} else {
			sendServiceAvailabilityText += LF + "{" + LF + "   ECOA__timestamp timestamp;" + LF + "   int num_bytes;" + LF + "   ECOA__boolean8 availability;" + LF + "   ELIHeader header;" + LF + LF +

					"   unsigned char *message_buffer = (unsigned char *)malloc(1024*1024);" + LF + "   unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

					"   header.ecoaMark = bswap16(0xEC0A);" + LF + "   header.version_domain = 0x12;" + LF + LF +

					"   header.logicalPlatform = PD_IDS__" + pd.getName().toUpperCase() + ";" + LF + "   header.ID = bswap32(ELI_Message__PlatformMessageID_AVAILABILITY_STATUS);" + LF + LF +

					"   /* Timestamp point */" + LF + "   ECOA_setTimestamp(&timestamp);" + LF + "   header.seconds = bswap32(timestamp.seconds);" + LF + "   header.nanoseconds = bswap32(timestamp.nanoseconds);" + LF +
					// Don't use sequence number for platform status message
					"   header.sequenceNumber = 0;" + LF + LF +

					"   /* Change the message_buffer_ptr to point after header */" + LF + "   message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF +

					"   ECOA__uint32 *providedServicePtr = (ECOA__uint32 *)message_buffer_ptr;" + LF + "   ServiceAvailability *serviceAvailabilityPayloadPtr = (ServiceAvailability *)(message_buffer_ptr + sizeof(AvailabilityStatus));" + LF + LF +

					"   switch (pdID)" + LF + "   {" + LF;

			includeList.add(pd.getName() + "_Service_Manager");

			String viaPlatformManagerCase = "      case PD_IDS__" + lcp.getName().toUpperCase() + ":" + LF;
			int numOfPlatformServicesProvided = 0;
			for (Entry<SM_ProtectionDomain, ArrayList<SM_Wire>> entry : pd.getMapOfProvidedServicesToPD().entrySet()) {
				int numOfServicesProvided = 0;

				// Only generate case if within the platform... Otherwise send
				// via Platform Manager.
				if (entry.getKey().getLogicalComputingNode().getLogicalComputingPlatform() == lcp) {
					sendServiceAvailabilityText += "      case PD_IDS__" + entry.getKey().getName().toUpperCase() + ":" + LF;

					for (SM_Wire wire : entry.getValue()) {
						String serviceUIDString = wire.getTarget().getName().toUpperCase() + "_" + wire.getTargetOp().getName().toUpperCase() + "_UID";

						sendServiceAvailabilityText += "         " + pd.getName() + "_Service_Manager__Get_Availability(" + serviceUIDString + ", &availability);" + LF + "         serviceAvailabilityPayloadPtr->serviceID = bswap32(" + serviceUIDString + ");" + LF + "         serviceAvailabilityPayloadPtr->availabilityState = bswap32(availability);" + LF + "         /* Move the pointer on... */" + LF + "         serviceAvailabilityPayloadPtr++;" + LF + LF;
						numOfServicesProvided++;
					}

					sendServiceAvailabilityText += "         *providedServicePtr = bswap32(" + numOfServicesProvided + ");" + LF + "         break;" + LF;

				}
				// Add to the string for services being sent via Platform
				// Manager.
				else {
					for (SM_Wire wire : entry.getValue()) {
						String serviceUIDString = wire.getTarget().getName().toUpperCase() + "_" + wire.getTargetOp().getName().toUpperCase() + "_UID";

						viaPlatformManagerCase += "         " + pd.getName() + "_Service_Manager__Get_Availability(" + serviceUIDString + ", &availability);" + LF + "         serviceAvailabilityPayloadPtr->serviceID = bswap32(" + serviceUIDString + ");" + LF + "         serviceAvailabilityPayloadPtr->availabilityState = bswap32(availability);" + LF + "         /* Move the pointer on... */" + LF + "         serviceAvailabilityPayloadPtr++;" + LF + LF;
						numOfPlatformServicesProvided++;
					}
				}

			}

			// If there is at least one service provided to another platform,
			// add the case statement.
			if (numOfPlatformServicesProvided > 0) {
				sendServiceAvailabilityText += viaPlatformManagerCase + "         *providedServicePtr = bswap32(" + numOfPlatformServicesProvided + ");" + LF + "         break;" + LF;
			}

			sendServiceAvailabilityText +=
					// close switch on pdID
					"      default:" + LF + "         *providedServicePtr = bswap32(0);" + LF + "         break;" + LF + "   }" + LF + LF +

							"   num_bytes = (unsigned char *)serviceAvailabilityPayloadPtr - message_buffer;" + LF + "   header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "   memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF +

							"   /* We can now send the ELI message */" + LF + "   " + pd.getName() + "_ELI_Support__SendPDELIMessage(message_buffer, num_bytes, pdID);" + LF + "   free(message_buffer);" + LF +

							"}" + LF + LF;
		}

		// Replace the #SEND_SERVICE_AVAIL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_SERVICE_AVAIL#", sendServiceAvailabilityText);
	}

	public void writeSendServiceAvailabilityRequest() {
		String sendServiceAvailReqText = SEP_PATTERN_351 + pdManagerName + "__Send_Service_Availability_Request(ECOA__uint32 pdID)";

		if (isHeader) {
			sendServiceAvailReqText += ";" + LF + LF;
		} else {
			sendServiceAvailReqText += LF + "{" + LF + "   ECOA__timestamp timestamp;" + LF + "   int num_bytes;" + LF + "   ELIHeader header;" + LF + LF +

					"   unsigned char *message_buffer = (unsigned char *)malloc(1024*1024);" + LF + "   unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

					"   header.ecoaMark = bswap16(0xEC0A);" + LF + "   header.version_domain = 0x12;" + LF + LF +

					"   header.logicalPlatform = PD_IDS__" + pd.getName().toUpperCase() + ";" + LF + "   header.ID = bswap32(ELI_Message__PlatformMessageID_AVAILABILITY_STATUS_REQUEST);" + LF + LF +

					"   /* Timestamp point */" + LF + "   ECOA_setTimestamp(&timestamp);" + LF + "   header.seconds = bswap32(timestamp.seconds);" + LF + "   header.nanoseconds = bswap32(timestamp.nanoseconds);" + LF +
					// Don't use sequence number for platform status message
					"   header.sequenceNumber = 0;" + LF + LF +

					"   /* Change the message_buffer_ptr to point after header */" + LF + "   message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF +

					"   AvailabilityStatusRequest *availabilityStatusRequestPtr = (AvailabilityStatusRequest *)message_buffer_ptr;" + LF + "   availabilityStatusRequestPtr->serviceID = 0xFFFFFFFF;" + LF +

					"   num_bytes = sizeof(ELIHeader) + sizeof(AvailabilityStatusRequest);" + LF + "   header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "   memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "   /* We can now send the ELI message */" + LF + "   " + pd.getName() + "_ELI_Support__SendPDELIMessage(message_buffer, num_bytes, pdID);" + LF + "   free(message_buffer);" + LF + "}" + LF + LF;
		}

		// Replace the #SEND_SERVICE_AVAIL_REQ# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_SERVICE_AVAIL_REQ#", sendServiceAvailReqText);
	}

	public void writeSendSingleServiceAvailability() {
		String sendSingleServiceAvailabilityText = SEP_PATTERN_351 + pdManagerName + "__Send_Single_Service_Availability(ECOA__uint32 serviceUID, ECOA__boolean8 availability, ECOA__timestamp *timestamp, ECOA__uint32 pdID)";

		if (isHeader) {
			sendSingleServiceAvailabilityText += ";" + LF + LF;
		} else {
			sendSingleServiceAvailabilityText += LF + "{" + LF + "   ELIHeader header;" + LF + LF +

					"   int num_bytes = sizeof(ELIHeader) + sizeof(AvailabilityStatus) + sizeof(ServiceAvailability);" + LF +

					"   unsigned char *message_buffer = (unsigned char *)malloc(num_bytes);" + LF + "   unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

					"   header.ecoaMark = bswap16(0xEC0A);" + LF + "   header.version_domain = 0x12;" + LF + LF +

					"   header.logicalPlatform = PD_IDS__" + pd.getName().toUpperCase() + ";" + LF + "   header.ID = bswap32(ELI_Message__PlatformMessageID_AVAILABILITY_STATUS);" + LF + LF +

					"   /* Timestamp point */" + LF + "   header.seconds = bswap32(timestamp->seconds);" + LF + "   header.nanoseconds = bswap32(timestamp->nanoseconds);" + LF +
					// Don't use sequence number for platform status message
					"   header.sequenceNumber = 0;" + LF + LF +

					"   /* Change the message_buffer_ptr to point after header */" + LF + "   message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF +

					"   ECOA__uint32 *providedServicePtr = (ECOA__uint32 *)message_buffer_ptr;" + LF + "   ServiceAvailability *serviceAvailabilityPayloadPtr = (ServiceAvailability *)(message_buffer_ptr + sizeof(AvailabilityStatus));" + LF + LF +

					"   serviceAvailabilityPayloadPtr->serviceID = bswap32(serviceUID);" + LF + "   serviceAvailabilityPayloadPtr->availabilityState = bswap32(availability);" + LF + LF +

					"   *providedServicePtr = bswap32(1);" + LF + "   header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "   memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "   /* We can now send the ELI message */" + LF + "   " + pd.getName() + "_ELI_Support__SendPDELIMessage(message_buffer, num_bytes, pdID);" + LF + "   free(message_buffer);" + LF + "}" + LF + LF;
		}

		// Replace the #SEND_SINGLE_SERVICE_AVAIL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_SINGLE_SERVICE_AVAIL#", sendSingleServiceAvailabilityText);
	}

	public void writeSendSingleVersionedData() {
		String sendSingleVDText = SEP_PATTERN_351 + pdManagerName + "__Send_Single_Versioned_Data(ECOA__uint32 euid)";

		if (isHeader) {
			sendSingleVDText += ";" + LF + LF;
		} else {
			sendSingleVDText += LF + "{" + LF + LF +

					"   switch (euid)" + LF + "   {" + LF;

			for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
				for (SM_Wire wire : compInst.getTargetWires()) {
					// Only generate for this wire if at requirer is off
					// Protection domain
					if (!pd.getComponentInstances().contains(wire.getSource())) {
						SM_ServiceInstance providedServiceInst = wire.getTargetOp();

						includeList.add(compInst.getName() + "_" + providedServiceInst.getName() + "_Controller");
						// Process all service versioned data ops (data written)
						for (SM_DataServiceOp dataOp : providedServiceInst.getServiceInterface().getDataOps()) {
							SM_UIDServiceOp uid = wire.getUID(dataOp);

							sendSingleVDText += "      case " + uid.getUIDDefString() + ":" + LF + "         /* Send versioned data for compInst: " + compInst.getName() + ", operation: " + dataOp.getName() + " */" + LF + "         " + compInst.getName() + "_" + providedServiceInst.getName() + "_" + dataOp.getName() + "__versioned_data_publish();" + LF + "         break;" + LF;
						}
					}
				}
			}
			sendSingleVDText += LF +
			// Close switch on euid
					"   }" + LF +
					// Close function
					"}" + LF;
		}

		// Replace the #SEND_SINGLE_VERSIONED_DATA# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_SINGLE_VERSIONED_DATA#", sendSingleVDText);
	}

	public void writeSendVersionedData() {
		String sendVDText = SEP_PATTERN_351 + pdManagerName + "__Send_Versioned_Data(ECOA__uint32 pdID)";

		if (isHeader) {
			sendVDText += ";" + LF + LF;
		} else {
			includeList.add("ecoaByteswap");

			sendVDText += LF + "{" + LF + "   /* Send all versioned data we provide */" + LF + "   switch (pdID)" + LF + "   {" + LF;

			for (SM_ProtectionDomain remotePD : pd.getListOfPDsCommunicateWith()) {
				sendVDText += "      case PD_IDS__" + remotePD.getName().toUpperCase() + ":" + LF;

				for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
					for (SM_Wire wire : compInst.getTargetWires()) {
						// Only generate for this wire if a requirer is off
						// Protection domain
						if (remotePD.getComponentInstances().contains(wire.getSource())) {
							SM_ServiceInstance providedServiceInst = wire.getTargetOp();

							// Process all service versioned data ops (data
							// written)
							for (SM_DataServiceOp dataOp : providedServiceInst.getServiceInterface().getDataOps()) {
								sendVDText += "         /* Send versioned data for compInst: " + compInst.getName() + ", operation: " + dataOp.getName() + " */" + LF + "         " + compInst.getName() + "_" + providedServiceInst.getName() + "_" + dataOp.getName() + "__versioned_data_publish();" + LF;
							}
						}
					}
				}
				sendVDText += "      break;" + LF;
			}
			sendVDText +=
					// Close switch on pdID
					"   }" + LF +
					// Close function
							"}" + LF;
		}

		// Replace the #SEND_VERSIONED_DATA# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_VERSIONED_DATA#", sendVDText);
	}

	public void writeSendVersionedDataRequest() {
		String sendVDReqText = SEP_PATTERN_351 + pdManagerName + "__Send_Versioned_Data_Request(ECOA__uint32 pdID)";

		if (isHeader) {
			sendVDReqText += ";" + LF + LF;
		} else {
			sendVDReqText += LF + "{" + LF + "   ECOA__timestamp timestamp;" + LF + "   int num_bytes;" + LF + "   ELIHeader header;" + LF + LF +

					"   unsigned char *message_buffer = (unsigned char *)malloc(1024*1024);" + LF + "   unsigned char *message_buffer_ptr = message_buffer;" + LF + LF +

					"   header.ecoaMark = bswap16(0xEC0A);" + LF + "   header.version_domain = 0x12;" + LF + LF +

					"   header.logicalPlatform = PD_IDS__" + pd.getName().toUpperCase() + ";" + LF + "   header.ID = bswap32(ELI_Message__PlatformMessageID_VERSIONED_DATA_PULL);" + LF + LF +

					"   /* Timestamp point */" + LF + "   ECOA_setTimestamp(&timestamp);" + LF + "   header.seconds = bswap32(timestamp.seconds);" + LF + "   header.nanoseconds = bswap32(timestamp.nanoseconds);" + LF +
					// Don't use sequence number for platform status message
					"   header.sequenceNumber = 0;" + LF + LF +

					"   /* Change the message_buffer_ptr to point after header */" + LF + "   message_buffer_ptr = message_buffer + sizeof(header);" + LF + LF +

					"   VersionedDataPull *versionedDataPullPtr = (VersionedDataPull *)message_buffer_ptr;" + LF + "   versionedDataPullPtr->euid = 0xFFFFFFFF;" + LF +

					"   num_bytes = sizeof(ELIHeader) + sizeof(VersionedDataPull);" + LF + "   header.payloadSize = bswap32(num_bytes - sizeof(ELIHeader));" + LF + "   memcpy(message_buffer, &header, sizeof(ELIHeader));" + LF + "   /* We can now send the ELI message */" + LF + "   " + pd.getName() + "_ELI_Support__SendPDELIMessage(message_buffer, num_bytes, pdID);" + LF + "   free(message_buffer);" + LF + "}" + LF + LF;
		}

		// Replace the #SEND_VERSIONED_DATA_REQ# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_VERSIONED_DATA_REQ#", sendVDReqText);
	}

	public void writeSetPDStatus() {
		String updatePDText = SEP_PATTERN_351 + pdManagerName + "__Set_PD_Status(ECOA__uint32 pdID, ELI_Message__PlatformStatus status)";

		if (isHeader) {
			updatePDText += ";" + LF + LF;
		} else {
			updatePDText += LF + "{" + LF + "   int x;" + LF + "   for (x = 0; x < PD_Availability_List_MAXSIZE; x++) " + LF + "   {" + LF + "      if (" + pd.getName() + "_pdAvailabilityList[x].pdID == pdID)" + LF + "      {" + LF + "         ELI_Message__PlatformStatus previousState = " + pd.getName() + "_pdAvailabilityList[x].pdStatus;" + LF + "         " + pd.getName() + "_pdAvailabilityList[x].pdStatus = status;" + LF + LF +

					"         if (previousState == ELI_Message__PlatformStatus_DOWN &&" + LF + "             status == ELI_Message__PlatformStatus_UP &&" + LF + "             pdID != PD_IDS__" + pd.getName().toUpperCase() + ")" + LF + "         {" + LF + "            " + pdManagerName + "__Send_PD_Status(pdID);" + LF + "            // Don't send service availability request/VD pull request if platform manager" + LF + "            if (pdID != PD_IDS__" + lcp.getName().toUpperCase() + ")" + LF + "            {" + LF + "               " + pdManagerName + "__Send_Service_Availability_Request(pdID);" + LF + "               " + pdManagerName + "__Send_Versioned_Data_Request(pdID);" + LF + "            }" + LF + "         }" + LF + "         else if (previousState != status &&" + LF + "                  pdID == PD_IDS__" + pd.getName().toUpperCase() + ")" + LF + "         {" + LF + "            /* Send our PD status as it has changed */" + LF;

			for (SM_ProtectionDomain remotePD : pd.getListOfPDsCommunicateWith()) {
				updatePDText += "            " + pdManagerName + "__Send_PD_Status(PD_IDS__" + remotePD.getName().toUpperCase() + ");" + LF;
			}

			updatePDText += "         }" + LF + "      }" + LF + "   }" + LF + "}" + LF + LF;
		}

		// Replace the #SET_PD_STATUS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SET_PD_STATUS#", updatePDText);
	}
}
