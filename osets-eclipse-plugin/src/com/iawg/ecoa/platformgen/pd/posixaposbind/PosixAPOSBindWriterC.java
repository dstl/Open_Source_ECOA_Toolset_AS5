/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.posixaposbind;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.gen.posixaposbind.Templates;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class PosixAPOSBindWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_111 = "#WAIT_FOR_EVENT#";
	private boolean isHeader;
	private SM_ProtectionDomain pd;

	private ArrayList<String> includeList = new ArrayList<String>();

	public PosixAPOSBindWriterC(PlatformGenerator platformGenerator, boolean isHeader, Path outputDir, SM_ProtectionDomain pd) {
		super(outputDir);
		this.isHeader = isHeader;
		this.pd = pd;

		setFileStructure();
	}

	@Override
	public void close() {
		String closeText = "#endif  /* _POSIX_APOS_BINDING_H */" + LF + LF;

		// Replace the #CLOSE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#CLOSE#", closeText);

		super.close();
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve("posix_apos_binding.h"));
		} else {
			super.openFile(outputDir.resolve("posix_apos_binding.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#APOS_TYPES#" + LF + "#SEMAPHORE_APIS#" + LF + "#RECEIVE_MESSAGE#" + LF + "#SEND_MESSAGE_NON_BLOCK#" + LF + "#SCALE_PRIORITIES#" + LF + "#CREATE_THREAD#" + LF + "#EXIT_THREAD#" + LF + "#TIMER_APIS#" + LF + SEP_PATTERN_111 + LF + "#INITIALISE#" + LF + "#CLOSE#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#SOCKET_DECLS#" + LF + "#TIMER_TYPES_DECL#" + LF + "#SEMAPHORE_APIS#" + LF + "#RECEIVE_MESSAGE#" + LF + "#SEND_MESSAGE_NON_BLOCK#" + LF + "#SCALE_PRIORITIES#" + LF + "#CREATE_THREAD#" + LF + "#EXIT_THREAD#" + LF + "#TIMER_APIS#" + LF + SEP_PATTERN_111 + LF + "#SIG_HANDLER#" + LF + "#INITIALISE#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeAPOSTypes() {
		// Replace the #APOS_TYPES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#APOS_TYPES#", Templates.getTemplateString("PosixAPOSBindWriterC.APOSTypes_Header"));
	}

	public void writeScalePriorities() {
		String spText = "int Scale_ThreadPriority" + LF + "   (unsigned char Thread_Priority," + LF + "    int Sched_Policy)";
		if (isHeader) {
			spText = Templates.getTemplateString("PosixAPOSBindWriterC.ScaleThreadPriority_Header");
		} else {
			spText = Templates.getTemplateString("PosixAPOSBindWriterC.ScaleThreadPriority_Body");
		}
		// Replace the #SCALE_PRIORITIES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SCALE_PRIORITIES#", spText);
	}

	public void writeCreateThread() {
		String ctText = null;
		if (isHeader) {
			ctText = Templates.getTemplateString("PosixAPOSBindWriterC.CreateThread_Header");
		} else {
			ctText = Templates.getTemplateString("PosixAPOSBindWriterC.CreateThread_Body");
		}

		// Replace the #CREATE_THREAD# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#CREATE_THREAD#", ctText);
	}

	public void writeExitThread() {
		String etText = null;
		if (isHeader) {
			etText = Templates.getTemplateString("PosixAPOSBindWriterC.ExitThread_Header");
		} else {
			etText = Templates.getTemplateString("PosixAPOSBindWriterC.ExitThread_Body");
		}

		// Replace the #EXIT_THREAD# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#EXIT_THREAD#", etText);

	}

	public void writeIncludes() {
		if (!isHeader) {
			includeList.add("posix_apos_binding");
			includeList.add("sys/socket");
			includeList.add("netinet/in");
			includeList.add("arpa/inet"); // for inet_aton()
			includeList.add("errno");
			includeList.add("semaphore");
			includeList.add("stdio");
			includeList.add("stdlib");
			includeList.add("string");
			includeList.add("message_queue");
			includeList.add("pthread");
			includeList.add("signal");
			includeList.add("time");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);
		includeText += "#if defined( VXW )" + LF + "  /* VxWorks is picky, and doesn't include some prototypes in the POSIX headers... */" + LF + "#include <sockLib.h>" + LF + "#include <inetLib.h>" + LF + "#endif" + LF;

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	private String writeCreateRxSocket() {
		return Templates.getTemplateString("PosixAPOSBindWriterC.CreateRxSocket_Body");
	}

	private String writeCreateTxSocket() {
		return Templates.getTemplateString("PosixAPOSBindWriterC.CreateTxSocket_Body");
	}

	private String writeSetupTxSocket() {
		return Templates.getTemplateString("PosixAPOSBindWriterC.SetupTxSocket_Body");
	}

	public void writeInitialise() {
		includeList.add(pd.getName() + "_VC_IDS");
		includeList.add("PORT_NO");

		String initialiseText = "";

		if (isHeader) {
			initialiseText += "void posix_apos_binding__Initialise();" + LF + LF;
		} else {
			initialiseText += writeSignalHandlerWaitForEvent() + writeCreateRxSocket() + writeCreateTxSocket() + writeSetupTxSocket() +

					"void posix_apos_binding__Initialise()" + LF + "{" + LF + LF +

					"   /* Create a receive socket for each protection domain we communicate with. */" + LF + generatePDRxInit() + LF +

					"   /* Create a receive socket for the platform manager. */" + LF + generatePlatformManagerRxInit() + LF +

					"   /* Create a transmit socket */" + LF + "   create_tx_socket();" + LF + LF +

					"   /* Create a destination sockaddr endpoint for each protection domain we communicate with*/" + LF + generatePDTxInit() + LF +

					"   /* Create a transmit socket address for comms with Platform Manager. */" + LF + generatePlatformManagerTxInit() + LF +

					"   /* Setup signal handler and wait for event queues */" + LF + "   setup_signal_handler();" + LF + "}" + LF;
		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);
	}

	private String generatePlatformManagerTxInit() {
		return "   setup_tx_addr(&txAddr_PF_Manager" + ", \"127.0.0.1\", BASE_PORT + PORT_NO__" //$NON-NLS-2$
				+ pd.getLogicalComputingNode().getLogicalComputingPlatform().getName().toUpperCase() + "__" //$NON-NLS-1$
				+ pd.getName().toUpperCase() + ");" + LF; //$NON-NLS-1$
	}

	private String generatePDTxInit() {
		String initialiseText = "";

		for (SM_ProtectionDomain remotePD : pd.getListOfPDsCommunicateWith()) {
			initialiseText += "   setup_tx_addr(&txAddr_" + remotePD.getName() + ", \"127.0.0.1\", BASE_PORT + PORT_NO__" + remotePD.getName().toUpperCase() + "__" //$NON-NLS-2$ //$NON-NLS-3$
					+ pd.getName().toUpperCase() + ");" + LF; //$NON-NLS-1$
		}

		return initialiseText;
	}

	private String generatePlatformManagerRxInit() {
		return "   create_rx_socket(&rxSock_PF_Manager, BASE_PORT + PORT_NO__" + pd.getName().toUpperCase() + "__" //$NON-NLS-2$
				+ pd.getLogicalComputingNode().getLogicalComputingPlatform().getName().toUpperCase() + ");" + LF; //$NON-NLS-1$
	}

	private String generatePDRxInit() {
		String initialiseText = "";

		for (SM_ProtectionDomain remotePD : pd.getListOfPDsCommunicateWith()) {
			initialiseText += "   create_rx_socket(&rxSock_" + remotePD.getName() + ", BASE_PORT + PORT_NO__" //$NON-NLS-2$
					+ pd.getName().toUpperCase() + "__" + remotePD.getName().toUpperCase() + ");" + LF; //$NON-NLS-1$ //$NON-NLS-2$
		}

		return initialiseText;
	}

	private String writeSignalHandlerWaitForEvent() {
		return Templates.getTemplateString("PosixAPOSBindWriterC.SetupSigHandler_Body");
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File posix_apos_binding.h */" + LF + "/* This is the Posix-APOS binding */" + LF + LF +

					"#if !defined(_POSIX_APOS_BINDING_H)" + LF + "#define _POSIX_APOS_BINDING_H" + LF + LF;
		} else {
			preambleText += "/* File posix_apos_binding.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeReceiveMessage() {
		String rmText = null;

		if (isHeader) {
			rmText = Templates.getTemplateString("PosixAPOSBindWriterC.ReceiveMessage_Header");
		} else {
			rmText = Templates.getTemplateString("PosixAPOSBindWriterC.ReceiveMessage_BodyDecl") + "{" + LF + LF +

					"   struct sockaddr_in source;" + LF + "   socklen_t sourcelen = sizeof(source);" + LF + "   int numbytes;" + LF + "   int rxSock;" + LF + LF +

					"   /* Determine which socket to receive on from the VC_ID */" + LF + "   switch (VC_ID)" + LF + "   {" + LF;

			for (SM_ProtectionDomain remotePD : pd.getListOfPDsCommunicateWith()) {
				rmText += "      case VC_IDS__" + remotePD.getName().toUpperCase() + "_RECEIVE:" + LF + //$NON-NLS-2$
						"         rxSock = rxSock_" + remotePD.getName() + ";" + LF + //$NON-NLS-2$
						"         break;" + LF;
			}
			// Case for receiving from Platform Manager.
			rmText += "      case VC_IDS__PLATFORM_MANAGER_RECEIVE:" + LF + "         rxSock = rxSock_PF_Manager;" + LF + "         break;" + LF;

			rmText += "   }" + LF + LF +

					"   numbytes = recvfrom(rxSock, Msg_Buffer, Msg_Length_Available, 0, (struct sockaddr *)&source, &sourcelen);" + LF + LF +

					"   if (numbytes>0)" + LF + "   {" + LF +
					// TODO - remove this debug.
					// " printf(\"received from vc%d\\n\", VC_ID);" + LF +
					"      *Msg_Length = numbytes;" + LF + "      *Status = Blocking_Message_Received_OK;" + LF + "   } " + "   else" + LF + "   {" + LF + "      struct timespec ONESEC = (struct timespec){1,0};" + LF + "      *Status = Received_Blocking_Timeout;" + LF + "      // Delay for a second otherwise can enter an infite loop at a very high priority!" + LF + "      printf(\"failed to receive message - errno = %d\\n\", errno);" + LF + "      nanosleep(&ONESEC,NULL);" + LF + "   }" + LF + "}" + LF;
		}

		// Replace the #RECEIVE_MESSAGE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#RECEIVE_MESSAGE#", rmText);

	}

	public void writerTimerTypesDecls() {
		String ttText = "static sigset_t set;" + LF + "static Message_Queue_ID_Type Wait_For_Event__QueueID;" + LF + LF +

				"// list to hold current timers setup..." + LF + "typedef struct" + LF + "{" + LF + "   timer_t timerID;" + LF + "   struct itimerspec time;" + LF + "} timerListItemType;" + LF + LF +

				"#define TIMER_LIST_SIZE 100" + LF + "typedef timerListItemType timerListType[TIMER_LIST_SIZE];" + LF + "static timerListType timerList = {0};" + LF + LF;

		// Replace the #TIMER_TYPES_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#TIMER_TYPES_DECL#", ttText);
	}

	public void writeSemaphoreAPIs() {
		String semaphoreText = "";

		if (isHeader) {
			semaphoreText += Templates.getTemplateString("PosixAPOSBindWriterC.CreateSemaphore_Header") + LF + Templates.getTemplateString("PosixAPOSBindWriterC.WaitForSemaphore_Header") + LF + Templates.getTemplateString("PosixAPOSBindWriterC.PostSemaphore_Header") + LF + Templates.getTemplateString("PosixAPOSBindWriterC.DeleteSemaphore_Header");
		} else {
			semaphoreText += "sem_t mutexes[500];" + LF + "int mutindex = 0;" + LF + LF +

					Templates.getTemplateString("PosixAPOSBindWriterC.CreateSemaphore_Body") + LF +

					"static Wait_For_Semaphore_Status_Type WFSError()" + LF + "{" + LF + "  switch( errno ){" + LF + "    case EINTR: return Wait_For_Semaphore_Invalid_Timeout; break;" + LF + "    case EINVAL: return Wait_For_Semaphore_Invalid_Param; break;" + LF + "    case ETIMEDOUT: return Wait_For_Semaphore_Timeout_Expired; break;" + LF + "    default: return Wait_For_Semaphore_Failed; break;" + LF + "  }" + LF + LF + "}" + LF + LF +

					Templates.getTemplateString("PosixAPOSBindWriterC.WaitForSemaphore_Body") + LF + Templates.getTemplateString("PosixAPOSBindWriterC.PostSemaphore_Body") + LF + Templates.getTemplateString("PosixAPOSBindWriterC.DeleteSemaphore_Body");
		}

		// Replace the #SEMAPHORE_APIS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEMAPHORE_APIS#", semaphoreText);
	}

	public void writeSendMessageNonBlock() {
		String smnbText = null;

		if (isHeader) {
			smnbText = Templates.getTemplateString("PosixAPOSBindWriterC.SendMessageNB_Header");
		} else {
			smnbText = Templates.getTemplateString("PosixAPOSBindWriterC.SendMessageNB_BodyDecl") + "{" + LF + LF + "   switch (vc_id)" + LF + "   {" + LF;

			// Determine which socket to send to baed on VC ID.
			for (SM_ProtectionDomain remotePD : pd.getListOfPDsCommunicateWith()) {
				String txAddr = "txAddr_" + remotePD.getName();

				smnbText += "      case VC_IDS__" + remotePD.getName().toUpperCase() + "_SEND" + " :" + LF + //$NON-NLS-2$ //$NON-NLS-3$
						"         if (sendto(txSock, Msg_Buffer, Msg_Length, 0, (const struct sockaddr *)&" + txAddr + ", sizeof(" + txAddr + "))==-1)" + LF + //$NON-NLS-2$ //$NON-NLS-3$
						"         {" + LF + "            printf(\"sendto failed\\n\");" + LF + "         }" + LF + "         break;" + LF;
			}
			// Case for Platform Manager
			smnbText += "      case VC_IDS__PLATFORM_MANAGER_SEND" + " :" + LF + //$NON-NLS-2$
					"         if (sendto(txSock, Msg_Buffer, Msg_Length, 0, (const struct sockaddr *)&txAddr_PF_Manager, sizeof(txAddr_PF_Manager))==-1)" + LF + "         {" + LF + "            printf(\"sendto failed\\n\");" + LF + "         }" + LF + "         break;" + LF;

			smnbText += "   }" + LF + "   *Status = Non_Blocking_Message_Sent_OK;" + LF + "}" + LF + LF;
		}

		// Replace the #SEND_MESSAGE_NON_BLOCK# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEND_MESSAGE_NON_BLOCK#", smnbText);

	}

	public void writeSigHandlerAPI() {
		// Replace the #SIG_HANDLER# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SIG_HANDLER#", Templates.getTemplateString("PosixAPOSBindWriterC.TimerSigHandler_Body"));
	}

	public void writeSocketDecls() {
		String socketDeclsText = "static int txSock, rxSock_PF_Manager;" + LF + "static struct sockaddr_in txAddr_PF_Manager;" + LF;

		// Create a destination sockaddr for each protection domain + a socket
		// for receiving from each protection domain.
		for (SM_ProtectionDomain remotePD : pd.getListOfPDsCommunicateWith()) {
			socketDeclsText += "static struct sockaddr_in txAddr_" + remotePD.getName() + ";" + LF; //$NON-NLS-2$
			socketDeclsText += "static int rxSock_" + remotePD.getName() + ";" + LF; //$NON-NLS-2$
		}

		socketDeclsText += "static ECOA__uint32 BASE_PORT = 60000;" + LF;

		// Replace the #SOCKET_DECLS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SOCKET_DECLS#", socketDeclsText);

	}

	public void writeTimerAPIs() {
		String timerText = null;

		if (isHeader) {
			timerText = Templates.getTemplateString("PosixAPOSBindWriterC.CreateTimer_Header") + LF + Templates.getTemplateString("PosixAPOSBindWriterC.StartTimer_Header") + LF + Templates.getTemplateString("PosixAPOSBindWriterC.StopTimer_Header") + LF + Templates.getTemplateString("PosixAPOSBindWriterC.DeleteTimer_Header");
		} else {
			// TODO - tidy up the nasty casting to int, as this will probably
			// not always work...
			timerText = Templates.getTemplateString("PosixAPOSBindWriterC.CreateTimer_Body") + LF + Templates.getTemplateString("PosixAPOSBindWriterC.StartTimer_Body") + LF + Templates.getTemplateString("PosixAPOSBindWriterC.StopTimer_Body") + LF + Templates.getTemplateString("PosixAPOSBindWriterC.DeleteTimer_Body");
		}

		// Replace the #TIMER_APIS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#TIMER_APIS#", timerText);

	}

	public void writeWaitForEventAPI() {
		String wfeText = null;
		if (isHeader) {
			wfeText = Templates.getTemplateString("PosixAPOSBindWriterC.WaitForEvent_Header");
		} else {
			wfeText = Templates.getTemplateString("PosixAPOSBindWriterC.WaitForEvent_Body");
		}

		// Replace the #WAIT_FOR_EVENT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, SEP_PATTERN_111, wfeText);

	}
}
