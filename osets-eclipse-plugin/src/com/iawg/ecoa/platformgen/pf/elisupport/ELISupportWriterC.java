/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.elisupport;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.common.underlyingplatform.C_Posix;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_DataServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp.EventDirection;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;
import com.iawg.ecoa.systemmodel.uid.SM_UIDServiceOp;

public class ELISupportWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_291 = "void ";
	private boolean isHeader;

	private ArrayList<String> includeList = new ArrayList<String>();
	private Generic_Platform underlyingPlatform;
	private SM_LogicalComputingPlatform lcp;
	private String eliSupportName;

	public ELISupportWriterC(PlatformManagerGenerator pfManagerGenerator, boolean isHeader, Path outputDir, SM_LogicalComputingPlatform lcp) {
		super(outputDir);
		this.underlyingPlatform = pfManagerGenerator.getUnderlyingPlatformInstantiation();
		this.isHeader = isHeader;
		this.lcp = lcp;
		this.eliSupportName = lcp.getName() + "_ELI_Support";

		setFileStructure();
	}

	@Override
	public void open() {

		if (isHeader) {
			super.openFile(outputDir.resolve(eliSupportName + ".h"));
		} else {
			super.openFile(outputDir.resolve(eliSupportName + ".c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#VC_INFO_DECL#" + LF + "#SEND_ELI#" + LF + "#SEND_PD_ELI#" + LF + "#SEND_PLATFORM_ELI#" + LF + "#RECEIVE_ELI#" + LF + "#INITIALISE#" + LF + "#POSTAMBLE#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#SEMAPHORE_DECL#" + LF + "#CHANNEL_COUNT#" + LF + "#UID_TO_VC#" + LF + "#SEND_ELI#" + LF + "#SEND_PD_ELI#" + LF + "#SEND_PLATFORM_ELI#" + LF + "#RECEIVE_ELI#" + LF + "#INITIALISE#";
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeChannelCounterDecl() {
		String channelCounterText = "static unsigned short count[16];" + LF + LF;

		// Replace the #CHANNEL_COUNT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#CHANNEL_COUNT#", channelCounterText);
	}

	public void writeIncludes() {
		if (isHeader) {
			includeList.add("ECOA");
		} else {
			includeList.addAll(underlyingPlatform.addIncludesELISupport());
			includeList.add(eliSupportName);
			includeList.add(lcp.getName() + "_Service_Op_UID");
			includeList.add("PD_IDS");
			includeList.add(lcp.getName() + "_VC_IDS");

			if (underlyingPlatform instanceof C_Posix) {
				includeList.add("posix_apos_binding");
			}
			includeList.add("ecoaByteswap");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeInitialise() {
		String initialiseText = "";

		String semaphoreID = lcp.getName() + "_Send_ELI_SemaphoreID";

		if (isHeader) {
			initialiseText += SEP_PATTERN_291 + eliSupportName + "__Initialise();" + LF;
		} else {
			initialiseText += SEP_PATTERN_291 + eliSupportName + "__Initialise()" + LF + "{" + LF + LF + "   int i;" + LF + LF + underlyingPlatform.generateCreateSemaphoreAttributes() + underlyingPlatform.generateCreateSemphore(1, 1, semaphoreID) + underlyingPlatform.checkCreateSemphoreStatus(semaphoreID) + "   for(i=0;i<15;i++)" + LF + "   {" + LF + "      count[i] = 0;" + LF + "   }" + LF + "}" + LF + LF;
		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);

	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + eliSupportName + ".h */" + LF + LF + "#ifndef " + lcp.getName().toUpperCase() + "_" + "_ELI_In".toUpperCase() + "_H" + LF + "#define " + lcp.getName().toUpperCase() + "_" + "_ELI_In".toUpperCase() + "_H" + LF;
		} else {
			preambleText += "/* File " + eliSupportName + ".c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeReceiveELI() {
		String receiveELIText = "";

		if (isHeader) {
			receiveELIText += SEP_PATTERN_291 + eliSupportName + "__ReceiveELIMessage(vcInfoType *vcInfo);" + LF;
		} else {
			includeList.add(lcp.getName() + "_ELI_In");
			receiveELIText += SEP_PATTERN_291 + eliSupportName + "__ReceiveELIMessage(vcInfoType *vcInfo)" + LF + "{" + LF + LF +

					"   unsigned char Rx_Message[65507];" + LF + "   int Msg_Len_Avail;" + LF + "   unsigned char *assembled_message = NULL;" + LF + "   unsigned int assembled_message_size;" + LF +

					underlyingPlatform.generateTimeoutVars() + LF + underlyingPlatform.generateReceiveMessageVars("vcInfo->vcID") + LF +

					"   while (1)" + LF + "   {" + LF + underlyingPlatform.generateReceiveMessageCall() + LF +

					underlyingPlatform.generateRMCheckStatusOK() + "      {" + LF + "         reassemble(Rx_Message, (unsigned int)Msg_Len_Avail, &assembled_message, &assembled_message_size);" + LF + "         if (assembled_message != NULL)" + LF + "         {" + LF + "            " + lcp.getName() + "_processELIMessage(assembled_message, assembled_message_size, vcInfo->isPlatformVC);" + LF + "         }" + LF + "      }" + LF + "   }" + LF + "}" + LF + LF;
		}

		// Replace the #RECEIVE_ELI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#RECEIVE_ELI#", receiveELIText);
	}

	public void writeSemaphoreIDDecl() {
		String semaphoreIDDeclText = "static int " + lcp.getName() + "_Send_ELI_SemaphoreID;" + LF;

		// Replace the #SEMAPHORE_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEMAPHORE_DECL#", semaphoreIDDeclText);
	}

	public void writeVCInfoStructDecl() {
		String vcInfoDeclText = "typedef struct {" + LF + "   int vcID;" + LF + "   ECOA__boolean8 isPlatformVC;" + LF + "} vcInfoType;" + LF;

		// Replace the #VC_INFO_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#VC_INFO_DECL#", vcInfoDeclText);
	}

	public void writeSendELI() {
		String sendELIText = "";

		if (isHeader) {
			sendELIText += SEP_PATTERN_291 + eliSupportName + "__SendELIMessage(unsigned char *Message_Buffer, int Num_Bytes, ECOA__boolean8 isFromPlatform);" + LF;
		} else {
			sendELIText += SEP_PATTERN_291 + eliSupportName + "__SendELIMessage(unsigned char *Message_Buffer, int Num_Bytes, ECOA__boolean8 isFromPlatform)" + LF + "{" + LF + LF + "   ELIHeader *header = (ELIHeader *)Message_Buffer;" + LF + "   fragmentObj_t Fragments[5];" + LF + "   int i, Num_Frags;" + LF + "   ELI_Message__PlatformStatus platformStatus;" + LF + underlyingPlatform.generateWaitForSemaphoreAttributes() + underlyingPlatform.generatePostSemaphoreAttributes() + underlyingPlatform.generateSendMessageNonBlockVars() + LF +

					"   /* Determine the destination from the service operation UID */" + LF + "   VC_ID = " + eliSupportName + "__UIDToVC(bswap32(header->ID), isFromPlatform);" + LF + LF +

					// TODO - VC ID is not the Platform ID anymore! - needs more
					// logic here! - DEFAULT IT TO 1 FOR NOW!
					lcp.getName() + "_PFtoPF_Manager__Get_Platform_Status(VC_ID, &platformStatus);" + LF + "  platformStatus = 1;" + LF + "  if(platformStatus == 1) /* only send if platform is up */" + LF + "  {" + LF +

					underlyingPlatform.generateWaitForSemaphore(lcp.getName() + "_Send_ELI_SemaphoreID") + underlyingPlatform.checkWaitForSemaphoreStatusCALL_ONLY() + "   {" + LF + LF +

					"      Num_Frags = fragment(" + lcp.getRelatedUDPBinding().getPlatformID() + ", 1, &count[VC_ID], Message_Buffer, Num_Bytes, Fragments);" + LF + LF +

					"      for (i = 0; i < Num_Frags; i++)" + LF + "      {" + LF + underlyingPlatform.generateSendMessageNonBlockCall("         ") + LF + LF +

					"         if (SMNB_Status != Non_Blocking_Message_Sent_OK)" + LF + "         {" + LF + "            printf(\"ERROR - PF Manager failed to send message %d\\n\", SMNB_Status);" + LF + "         }" + LF + LF +

					"         free(Fragments[i].fragment);" + LF + "      }" + LF +

					"   " + underlyingPlatform.generatePostSemaphore(lcp.getName() + "_Send_ELI_SemaphoreID") + "   " + underlyingPlatform.checkPostSemaphoreStatus(lcp.getName() + "_Send_ELI_SemaphoreID") +

					"   }" + LF + "   else" + LF + "   {" + LF + "      printf(\"ERROR - getting " + lcp.getName() + "_Send_ELI_SemaphoreID\\n\");" + LF + "   }" + LF + LF + "  }" + LF + "}" + LF + LF;
		}

		// Replace the #SEND_ELI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_ELI#", sendELIText);
	}

	public void writeSendPDELI() {
		String sendPDELIText = "";

		if (isHeader) {
			sendPDELIText += SEP_PATTERN_291 + eliSupportName + "__SendPDELIMessage(unsigned char *Message_Buffer, int Num_Bytes, ECOA__uint32 pdID);" + LF;
		} else {
			includeList.add(lcp.getName() + "_PFtoPD_Manager");
			sendPDELIText += SEP_PATTERN_291 + eliSupportName + "__SendPDELIMessage(unsigned char *Message_Buffer, int Num_Bytes, ECOA__uint32 pdID)" + LF + "{" + LF + LF + "   ELIHeader *header = (ELIHeader *)Message_Buffer;" + LF + "   fragmentObj_t Fragments[5];" + LF + "   int i, Num_Frags;" + LF + "   ELI_Message__PlatformStatus pdStatus;" + LF + "   ECOA__uint32 pdMessageID = *((ECOA__uint32 *)(Message_Buffer + 4));" + LF + underlyingPlatform.generateWaitForSemaphoreAttributes() + underlyingPlatform.generatePostSemaphoreAttributes() + underlyingPlatform.generateSendMessageNonBlockVars() + LF +

					"   /* Determine the destination pdID */" + LF + "   VC_ID = Get_Send_PD_VC_ID(pdID);" + LF + LF +

					"  pdMessageID = bswap32(pdMessageID);" + LF +

					underlyingPlatform.generateWaitForSemaphore(lcp.getName() + "_Send_ELI_SemaphoreID") + underlyingPlatform.checkWaitForSemaphoreStatusCALL_ONLY() + "   {" + LF + LF +

					"      Num_Frags = fragment(PD_IDS__" + lcp.getName().toUpperCase() + ", 1, &count[VC_ID], Message_Buffer, Num_Bytes, Fragments);" + LF + LF +

					"      for (i = 0; i < Num_Frags; i++)" + LF + "      {" + LF + underlyingPlatform.generateSendMessageNonBlockCall("         ") + LF + LF +

					"         if (SMNB_Status != Non_Blocking_Message_Sent_OK)" + LF + "         {" + LF + "            printf(\"ERROR - PF Manager failed to send message %d\\n\", SMNB_Status);" + LF + "         }" + LF + LF +

					"         free(Fragments[i].fragment);" + LF + LF + "      }" + LF +

					"   " + underlyingPlatform.generatePostSemaphore(lcp.getName() + "_Send_ELI_SemaphoreID") + "   " + underlyingPlatform.checkPostSemaphoreStatus(lcp.getName() + "_Send_ELI_SemaphoreID") +

					"   }" + LF + "   else" + LF + "   {" + LF + "      printf(\"ERROR - getting " + lcp.getName() + "_Send_ELI_SemaphoreID\\n\");" + LF + "   }" + LF + LF + "}" + LF + LF;
		}

		// Replace the #SEND_PD_ELI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_PD_ELI#", sendPDELIText);
	}

	public void writeSendPlatformELI() {
		String sendPlatformELIText = "";

		if (isHeader) {
			sendPlatformELIText += SEP_PATTERN_291 + eliSupportName + "__SendPlatformELIMessage(unsigned char *Message_Buffer, int Num_Bytes, ECOA__uint32 platformID);" + LF;
		} else {
			includeList.add(lcp.getName() + "_PFtoPD_Manager");
			sendPlatformELIText += SEP_PATTERN_291 + eliSupportName + "__SendPlatformELIMessage(unsigned char *Message_Buffer, int Num_Bytes, ECOA__uint32 platformID)" + LF + "{" + LF + LF + "   ELIHeader *header = (ELIHeader *)Message_Buffer;" + LF + "   fragmentObj_t Fragments[5];" + LF + "   int i, Num_Frags;" + LF + "   ELI_Message__PlatformStatus platformStatus;" + LF + "   ECOA__uint32 pfMessageID = *((ECOA__uint32 *)(Message_Buffer + 4));" + LF + underlyingPlatform.generateWaitForSemaphoreAttributes() + underlyingPlatform.generatePostSemaphoreAttributes() + underlyingPlatform.generateSendMessageNonBlockVars() + LF +

					"   /* Determine the destination platformID */" + LF + "   VC_ID = Get_Send_Platform_VC_ID(platformID);" + LF + LF +

					"  " + lcp.getName() + "_PFtoPF_Manager__Get_Platform_Status(platformID, &platformStatus);" + LF + LF +

					"  pfMessageID = bswap32(pfMessageID);" + LF + "  if(platformStatus == 1 || pfMessageID == 1) /* only send if PD is up, or it is a PD status message */" + LF + "  {" + LF +

					underlyingPlatform.generateWaitForSemaphore(lcp.getName() + "_Send_ELI_SemaphoreID") + underlyingPlatform.checkWaitForSemaphoreStatusCALL_ONLY() + "   {" + LF + LF +

					"      Num_Frags = fragment(" + lcp.getRelatedUDPBinding().getPlatformID() + ", 1, &count[VC_ID], Message_Buffer, Num_Bytes, Fragments);" + LF + LF +

					"      for (i = 0; i < Num_Frags; i++)" + LF + "      {" + LF + underlyingPlatform.generateSendMessageNonBlockCall("         ") + LF + LF +

					"         if (SMNB_Status != Non_Blocking_Message_Sent_OK)" + LF + "         {" + LF + "            printf(\"ERROR - PF Manager failed to send message %d\\n\", SMNB_Status);" + LF + "         }" + LF + LF +

					"         free(Fragments[i].fragment);" + LF + LF + "      }" + LF +

					"   " + underlyingPlatform.generatePostSemaphore(lcp.getName() + "_Send_ELI_SemaphoreID") + "   " + underlyingPlatform.checkPostSemaphoreStatus(lcp.getName() + "_Send_ELI_SemaphoreID") +

					"   }" + LF + "   else" + LF + "   {" + LF + "      printf(\"ERROR - getting " + lcp.getName() + "_Send_ELI_SemaphoreID\\n\");" + LF + "   }" + LF + LF + "  }" + LF + LF + "}" + LF + LF;
		}

		// Replace the #SEND_PLATFORM_ELI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_PLATFORM_ELI#", sendPlatformELIText);
	}

	public void writeUIDtoVC() {
		includeList.add(lcp.getName() + "_VC_IDS");
		String uidToVCString = "int " + eliSupportName + "__UIDToVC(int uid, ECOA__boolean8 isFromPlatform)" + LF + "{" + LF + "   unsigned char buffer[255];" + LF + "   int size;" + LF + LF +

				"   switch (uid)" + LF + "   {" + LF;

		for (SM_ProtectionDomain pd : lcp.getAllProtectionDomains()) {
			for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
				// Get the target wires (where compInst is a provider)
				for (SM_Wire wire : compInst.getTargetWires()) {
					// Only generate if the destination is not on the same
					// platform
					if (!lcp.getAllProtectionDomains().contains(wire.getSource().getProtectionDomain())) {
						// Get the service associated with the wire
						SM_ServiceInstance serviceInst = wire.getTargetOp();

						// Events (received by provider)
						for (SM_EventServiceOp eventOp : serviceInst.getServiceInterface().getEventOps()) {
							SM_UIDServiceOp uid = wire.getUID(eventOp);

							if (eventOp.getDirection() == EventDirection.SENT_BY_PROVIDER) {
								uidToVCString += "      case " + uid.getUIDDefString() + ":" + LF + "         return VC_IDS__" + wire.getSource().getProtectionDomain().getLogicalComputingNode().getLogicalComputingPlatform().getName().toUpperCase() + "_SEND;" + LF;
							} else {
								uidToVCString += "      case " + uid.getUIDDefString() + ":" + LF + "         return VC_IDS__" + wire.getTarget().getProtectionDomain().getName().toUpperCase() + "_SEND;" + LF;
							}
						}

						// Request received responses (server)
						for (SM_RRServiceOp requestOp : serviceInst.getServiceInterface().getRROps()) {
							SM_UIDServiceOp uid = wire.getUID(requestOp);

							uidToVCString += "      case " + uid.getUIDDefString() + ":" + LF + "         // For request-response need to determine if from platform or not, as the UID is the same for a request and response!" + LF + "         if (isFromPlatform)" + LF + "         {" + LF + "            return VC_IDS__" + wire.getTarget().getProtectionDomain().getName().toUpperCase() + "_SEND;" + LF + "         }" + LF + "         else" + LF + "         {" + LF + "            return VC_IDS__" + wire.getSource().getProtectionDomain().getLogicalComputingNode().getLogicalComputingPlatform().getName().toUpperCase() + "_SEND;" + LF + "         }" + LF;
						}

						// Versioned data writes
						for (SM_DataServiceOp dataOp : serviceInst.getServiceInterface().getDataOps()) {
							SM_UIDServiceOp uid = wire.getUID(dataOp);

							uidToVCString += "      case " + uid.getUIDDefString() + ":" + LF + "         return VC_IDS__" + wire.getSource().getProtectionDomain().getLogicalComputingNode().getLogicalComputingPlatform().getName().toUpperCase() + "_SEND;" + LF;
						}
					}
				}

				// Get the target wires (where compInst is a requirer)
				for (SM_Wire wire : compInst.getSourceWires()) {
					// Only generate if the destination is not on the same
					// platform
					if (!lcp.getAllProtectionDomains().contains(wire.getTarget().getProtectionDomain())) {
						// Get the service associated with the wire
						SM_ServiceInstance serviceInst = wire.getSourceOp();

						// Events (sent by provider)
						for (SM_EventServiceOp eventOp : serviceInst.getServiceInterface().getEventOps()) {
							SM_UIDServiceOp uid = wire.getUID(eventOp);

							if (eventOp.getDirection() == EventDirection.RECEIVED_BY_PROVIDER) {
								uidToVCString += "      case " + uid.getUIDDefString() + ":" + LF + "         return VC_IDS__" + wire.getTarget().getProtectionDomain().getLogicalComputingNode().getLogicalComputingPlatform().getName().toUpperCase() + "_SEND;" + LF;
							} else {
								uidToVCString += "      case " + uid.getUIDDefString() + ":" + LF + "         return VC_IDS__" + wire.getSource().getProtectionDomain().getName().toUpperCase() + "_SEND;" + LF;
							}
						}

						// Request sends (client)
						for (SM_RRServiceOp requestOp : serviceInst.getServiceInterface().getRROps()) {
							SM_UIDServiceOp uid = wire.getUID(requestOp);

							uidToVCString += "      case " + uid.getUIDDefString() + ":" + LF + "         // For request-response need to determine if from platform or not, as the UID is the same for a request and response!" + LF + "         if (isFromPlatform)" + LF + "         {" + LF + "            return VC_IDS__" + wire.getSource().getProtectionDomain().getName().toUpperCase() + "_SEND;" + LF + "         }" + LF + "         else" + LF + "         {" + LF + "            return VC_IDS__" + wire.getTarget().getProtectionDomain().getLogicalComputingNode().getLogicalComputingPlatform().getName().toUpperCase() + "_SEND;" + LF + "         }" + LF;
						}
						// Versioned data (readers)
						for (SM_DataServiceOp dataOp : serviceInst.getServiceInterface().getDataOps()) {
							SM_UIDServiceOp uid = wire.getUID(dataOp);

							uidToVCString += "      case " + uid.getUIDDefString() + ":" + LF + "            return VC_IDS__" + wire.getSource().getProtectionDomain().getName().toUpperCase() + "_SEND;" + LF;
						}
					}
				}
			}
		}

		uidToVCString += "      default :" + LF + "         size = sprintf((char *)buffer, (char *)\"ELI_Support - couldn't complete UID to VC mapping\\n\");" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "         return 0;" + LF + "   }" + LF + "}" + LF + LF;

		// Replace the #UID_TO_VC# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#UID_TO_VC#", uidToVCString);
	}

	public void writePostamble() {
		String postambleText = "";
		if (isHeader) {
			postambleText += "#endif" + LF;
		}
		// Replace the #DYNTRIG# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#POSTAMBLE#", postambleText);
	}
}
