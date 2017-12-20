/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.elisupport;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.common.underlyingplatform.C_Posix;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.systemmodel.SM_Object;
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
	private static final String SEP_PATTERN_121 = "void ";

	private SM_ProtectionDomain pd;
	private boolean isHeader;

	private ArrayList<String> includeList = new ArrayList<String>();
	private Generic_Platform underlyingPlatform;

	private SM_LogicalComputingPlatform lcp;

	public ELISupportWriterC(PlatformGenerator platformGenerator, boolean isHeader, Path outputDir, SM_LogicalComputingPlatform lcp, SM_ProtectionDomain pd) {
		super(outputDir);
		this.underlyingPlatform = platformGenerator.getunderlyingPlatformInstantiation();
		this.pd = pd;
		this.lcp = lcp;
		this.isHeader = isHeader;

		setFileStructure();
	}

	@Override
	public void open() {

		if (isHeader) {
			super.openFile(outputDir.resolve(pd.getName() + "_ELI_Support.h"));
		} else {
			super.openFile(outputDir.resolve(pd.getName() + "_ELI_Support.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#SEND_ELI#" + LF + "#SEND_PD_ELI#" + LF + "#RECEIVE_ELI#" + LF + LF + "#INITIALISE#";
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#SEMAPHORE_DECL#" + LF + "#CHANNEL_COUNT#" + LF + "#UID_TO_VC#" + LF + "#SEND_ELI#" + LF + "#SEND_PD_ELI#" + LF + "#RECEIVE_ELI#" + LF + "#INITIALISE#";
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
			includeList.add(pd.getName() + "_ELI_Support");
			includeList.add(pd.getName() + "_Service_Op_UID");
			includeList.add("PD_IDS");
			includeList.add(pd.getName() + "_VC_IDS");

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

		String semaphoreID = pd.getName() + "_Send_ELI_SemaphoreID";

		if (isHeader) {
			initialiseText += SEP_PATTERN_121 + pd.getName() + "_ELI_Support__Initialise();" + LF;
		} else {
			initialiseText += SEP_PATTERN_121 + pd.getName() + "_ELI_Support__Initialise()" + LF + "{" + LF + LF + "   int i;" + LF + LF + underlyingPlatform.generateCreateSemaphoreAttributes() + underlyingPlatform.generateCreateSemphore(1, 1, semaphoreID) + underlyingPlatform.checkCreateSemphoreStatus(semaphoreID) + "   for(i=0;i<15;i++)" + LF + "   {" + LF + "      count[i] = 0;" + LF + "   }" + LF + "}" + LF + LF;
		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);

	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + pd.getName() + "_ELI_Support.h */" + LF;
		} else {
			preambleText += "/* File " + pd.getName() + "_ELI_Support.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeReceiveELI() {
		String receiveELIText = "";

		if (isHeader) {
			receiveELIText += SEP_PATTERN_121 + pd.getName() + "_ELI_Support__ReceiveELIMessage(int *vcID);" + LF;
		} else {
			includeList.add(pd.getName() + "_ELI_In");
			receiveELIText += SEP_PATTERN_121 + pd.getName() + "_ELI_Support__ReceiveELIMessage(int *vcID)" + LF + "{" + LF + LF +

					"   unsigned char Rx_Message[65507];" + LF + "   int Msg_Len_Avail;" + LF + "   unsigned char *assembled_message = NULL;" + LF + "   unsigned int assembled_message_size;" + LF +

					underlyingPlatform.generateTimeoutVars() + LF + underlyingPlatform.generateReceiveMessageVars("*vcID") + LF +

					"   while (1)" + LF + "   {" + LF + underlyingPlatform.generateReceiveMessageCall() + LF +

					underlyingPlatform.generateRMCheckStatusOK() + "      {" + LF + "         reassemble(Rx_Message, (unsigned int)Msg_Len_Avail, &assembled_message, &assembled_message_size);" + LF + "         if (assembled_message != NULL)" + LF + "         {" + LF + "            " + pd.getName() + "_processELIMessage(assembled_message, assembled_message_size);" + LF + "         }" + LF + "      }" + LF + "   }" + LF + "}" + LF + LF;
		}

		// Replace the #RECEIVE_ELI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#RECEIVE_ELI#", receiveELIText);
	}

	public void writeSemaphoreIDDecl() {
		String semaphoreIDDeclText = "static int " + pd.getName() + "_Send_ELI_SemaphoreID;" + LF;

		// Replace the #SEMAPHORE_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEMAPHORE_DECL#", semaphoreIDDeclText);
	}

	public void writeSendELI() {
		String sendELIText = "";

		if (isHeader) {
			sendELIText += SEP_PATTERN_121 + pd.getName() + "_ELI_Support__SendELIMessage(unsigned char *Message_Buffer, int Num_Bytes);" + LF;
		} else {
			sendELIText += SEP_PATTERN_121 + pd.getName() + "_ELI_Support__SendELIMessage(unsigned char *Message_Buffer, int Num_Bytes)" + LF + "{" + LF + LF + "   ELIHeader *header = (ELIHeader *)Message_Buffer;" + LF + "   fragmentObj_t Fragments[5];" + LF + "   int i, Num_Frags;" + LF + "   ELI_Message__PlatformStatus pdstatus;" + LF + underlyingPlatform.generateWaitForSemaphoreAttributes() + underlyingPlatform.generatePostSemaphoreAttributes() + underlyingPlatform.generateSendMessageNonBlockVars() + LF +

					"   /* Determine the destination from the service operation UID */" + LF + "   VC_ID = " + pd.getName() + "_ELI_Support__UIDToVC(bswap32(header->ID));" + LF + LF +

					pd.getName() + "_PD_Manager__Get_PD_Status(PD_IDS__" + pd.getName().toUpperCase() + ", &pdstatus);" + LF + "  if(pdstatus == 1) /* only send if pd is up */" + LF + "  {" + LF +

					underlyingPlatform.generateWaitForSemaphore(pd.getName() + "_Send_ELI_SemaphoreID") + underlyingPlatform.checkWaitForSemaphoreStatusCALL_ONLY() + "   {" + LF + LF +

					// Use the local platform ID for the UDP header and use the
					// PD_ID as the channel ID.
					"      Num_Frags = fragment(" + lcp.getRelatedUDPBinding().getPlatformID() + ", PD_IDS__" + pd.getName().toUpperCase() + ", &count[VC_ID], Message_Buffer, Num_Bytes, Fragments);" + LF + LF +

					"      for (i = 0; i < Num_Frags; i++)" + LF + "      {" + LF + underlyingPlatform.generateSendMessageNonBlockCall("         ") + LF + LF +

					"         if (SMNB_Status != Non_Blocking_Message_Sent_OK)" + LF + "         {" + LF + "            printf(\"ERROR - PD " + pd.getName() + " failed to send message %d\\n\", SMNB_Status);" + LF + "         }" + LF + LF +

					"         free(Fragments[i].fragment);" + LF + "      }" + LF +

					"   " + underlyingPlatform.generatePostSemaphore(pd.getName() + "_Send_ELI_SemaphoreID") + "   " + underlyingPlatform.checkPostSemaphoreStatus(pd.getName() + "_Send_ELI_SemaphoreID") +

					"   }" + LF + "   else" + LF + "   {" + LF + "      printf(\"ERROR - getting " + pd.getName() + "_Send_ELI_SemaphoreID\\n\");" + LF + "   }" + LF + LF + "  }" + LF + "}" + LF + LF;
		}

		// Replace the #SEND_ELI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_ELI#", sendELIText);
	}

	public void writeSendPDELI() {
		String sendPlatformELIText = "";

		if (isHeader) {
			sendPlatformELIText += SEP_PATTERN_121 + pd.getName() + "_ELI_Support__SendPDELIMessage(unsigned char *Message_Buffer, int Num_Bytes, ECOA__uint32 pdID);" + LF;
		} else {
			includeList.add(pd.getName() + "_PD_Manager");
			sendPlatformELIText += SEP_PATTERN_121 + pd.getName() + "_ELI_Support__SendPDELIMessage(unsigned char *Message_Buffer, int Num_Bytes, ECOA__uint32 pdID)" + LF + "{" + LF + LF + "   ELIHeader *header = (ELIHeader *)Message_Buffer;" + LF + "   fragmentObj_t Fragments[5];" + LF + "   int i, Num_Frags;" + LF + "   ELI_Message__PlatformStatus pdStatus;" + LF + "   ECOA__uint32 pdMessageID = *((ECOA__uint32 *)(Message_Buffer + 4));" + LF + underlyingPlatform.generateWaitForSemaphoreAttributes() + underlyingPlatform.generatePostSemaphoreAttributes() + underlyingPlatform.generateSendMessageNonBlockVars() + LF +

					"   /* Determine the destination pdID */" + LF + "   VC_ID = Get_Send_VC_ID(pdID);" + LF + LF +

					"  " + pd.getName() + "_PD_Manager__Get_PD_Status(pdID, &pdStatus);" + LF + LF +

					"  pdMessageID = bswap32(pdMessageID);" + LF + "  if(pdStatus == 1 || pdMessageID == 1) /* only send if PD is up, or it is a PD status message */" + LF + "  {" + LF +

					underlyingPlatform.generateWaitForSemaphore(pd.getName() + "_Send_ELI_SemaphoreID") + underlyingPlatform.checkWaitForSemaphoreStatusCALL_ONLY() + "   {" + LF + LF +

					// Use the local platform ID for the UDP header and use the
					// PD_ID as the channel ID.
					"      Num_Frags = fragment(" + lcp.getRelatedUDPBinding().getPlatformID() + ", PD_IDS__" + pd.getName().toUpperCase() + ", &count[VC_ID], Message_Buffer, Num_Bytes, Fragments);" + LF + LF +

					"      for (i = 0; i < Num_Frags; i++)" + LF + "      {" + LF + underlyingPlatform.generateSendMessageNonBlockCall("         ") + LF + LF +

					"         if (SMNB_Status != Non_Blocking_Message_Sent_OK)" + LF + "         {" + LF + "            printf(\"ERROR - PD " + pd.getName() + " failed to send message %d\\n\", SMNB_Status);" + LF + "         }" + LF + LF +

					"         free(Fragments[i].fragment);" + LF + LF + "      }" + LF +

					"   " + underlyingPlatform.generatePostSemaphore(pd.getName() + "_Send_ELI_SemaphoreID") + "   " + underlyingPlatform.checkPostSemaphoreStatus(pd.getName() + "_Send_ELI_SemaphoreID") +

					"   }" + LF + "   else" + LF + "   {" + LF + "      printf(\"ERROR - getting " + pd.getName() + "_Send_ELI_SemaphoreID\\n\");" + LF + "   }" + LF + LF + "  }" + LF + LF + "}" + LF + LF;
		}

		// Replace the #SEND_PD_ELI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_PD_ELI#", sendPlatformELIText);
	}

	public void writeUIDtoVC() {
		includeList.add(pd.getName() + "_VC_IDS");
		String uidToVCString = "int " + pd.getName() + "_ELI_Support__UIDToVC(int uid)" + LF + "{" + LF + "   unsigned char buffer[255];" + LF + "   int size;" + LF + LF +

				"   switch (uid)" + LF + "   {" + LF;

		for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
			// Get the target wires (where compInst is a provider)
			for (SM_Wire wire : compInst.getTargetWires()) {
				// Only generate if the destination is not on the same
				// protection domain
				if (wire.getSource().getProtectionDomain() != pd) {
					// Get the service associated with the wire
					SM_ServiceInstance serviceInst = wire.getTargetOp();

					// Events (received by provider)
					for (SM_EventServiceOp eventOp : serviceInst.getServiceInterface().getEventOps()) {
						if (eventOp.getDirection() == EventDirection.SENT_BY_PROVIDER) {
							uidToVCString += generateProvidedUIDCase(wire, eventOp);
						}
					}

					// Request received responses (server)
					for (SM_RRServiceOp requestOp : serviceInst.getServiceInterface().getRROps()) {
						uidToVCString += generateProvidedUIDCase(wire, requestOp);
					}

					// Versioned data writes
					for (SM_DataServiceOp dataOp : serviceInst.getServiceInterface().getDataOps()) {
						uidToVCString += generateProvidedUIDCase(wire, dataOp);
					}
				}
			}

			// Get the target wires (where compInst is a requirer)
			for (SM_Wire wire : compInst.getSourceWires()) {
				// Only generate if the destination is not on the same
				// protection domain
				if (wire.getTarget().getProtectionDomain() != pd) {
					// Get the service associated with the wire
					SM_ServiceInstance serviceInst = wire.getSourceOp();

					// Events (sent by provider)
					for (SM_EventServiceOp eventOp : serviceInst.getServiceInterface().getEventOps()) {
						if (eventOp.getDirection() == EventDirection.RECEIVED_BY_PROVIDER) {
							uidToVCString += generateRequiredUIDCase(wire, eventOp);
						}
					}

					// Request sends (client)
					for (SM_RRServiceOp requestOp : serviceInst.getServiceInterface().getRROps()) {
						uidToVCString += generateRequiredUIDCase(wire, requestOp);
					}

					// Versioned data not required for client (always sent by
					// provider)
				}
			}
		}

		uidToVCString += "      default :" + LF + "         size = sprintf((char *)buffer, (char *)\"ELI_Support - couldn't complete UID to VC mapping\");" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "         return 0;" + LF + "   }" + LF + "}" + LF + LF;

		// Replace the #UID_TO_VC# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#UID_TO_VC#", uidToVCString);
	}

	private String generateProvidedUIDCase(SM_Wire wire, SM_Object serviceOp) {
		String uidToVCString = "";

		SM_UIDServiceOp uid = wire.getUID(serviceOp);

		// Send direct if within the platform, via Platform Manager otherwise.
		if (wire.getSource().getProtectionDomain().getLogicalComputingNode().getLogicalComputingPlatform() == lcp) {
			uidToVCString += "      case " + uid.getUIDDefString() + ":" + LF + "         return VC_IDS__" + wire.getSource().getProtectionDomain().getName().toUpperCase() + "_SEND;" + LF;
		} else {
			uidToVCString += "      case " + uid.getUIDDefString() + ":" + LF + "         return VC_IDS__PLATFORM_MANAGER_SEND;" + LF;
		}
		return uidToVCString;
	}

	private String generateRequiredUIDCase(SM_Wire wire, SM_Object serviceOp) {
		String uidToVCString = "";

		SM_UIDServiceOp uid = wire.getUID(serviceOp);

		// Send direct if within the platform, via Platform Manager otherwise.
		if (wire.getTarget().getProtectionDomain().getLogicalComputingNode().getLogicalComputingPlatform() == lcp) {
			uidToVCString += "      case " + uid.getUIDDefString() + ":" + LF + "         return VC_IDS__" + wire.getTarget().getProtectionDomain().getName().toUpperCase() + "_SEND;" + LF;
		} else {
			uidToVCString += "      case " + uid.getUIDDefString() + ":" + LF + "         return VC_IDS__PLATFORM_MANAGER_SEND;" + LF;
		}
		return uidToVCString;
	}

}
