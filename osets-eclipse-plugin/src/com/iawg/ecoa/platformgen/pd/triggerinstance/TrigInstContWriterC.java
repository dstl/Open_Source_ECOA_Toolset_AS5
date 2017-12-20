/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.triggerinstance;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverService;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderTriggerInstance;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedTrigInst;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class TrigInstContWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_391 = "void ";

	private SM_ProtectionDomain protectionDomain;
	private SM_TriggerInstance triggerInstance;
	private SM_ComponentInstance compInst;
	private SM_ComponentImplementation compImpl;
	private boolean isHeader;
	private String iliName;
	private String trigInstContName;

	private ArrayList<String> includeList = new ArrayList<String>();
	private Generic_Platform underlyingPlatform;

	public TrigInstContWriterC(PlatformGenerator platformGenerator, boolean isHeader, Path outputDir, SM_DeployedTrigInst deployedTrigInst, SM_ProtectionDomain pd) {
		super(outputDir);
		this.underlyingPlatform = platformGenerator.getunderlyingPlatformInstantiation();

		this.protectionDomain = pd;
		this.triggerInstance = deployedTrigInst.getTrigInstance();
		this.compInst = deployedTrigInst.getCompInstance();
		this.compImpl = compInst.getImplementation();
		this.isHeader = isHeader;

		this.iliName = compImpl.getName() + "_" + triggerInstance.getName() + "_ILI";
		this.trigInstContName = compInst.getName() + "_" + triggerInstance.getName() + "_Controller";

		setFileStructure();
	}

	private String generateINITIALIZEHandler(SM_ModuleInstance supervisorModuleInstance) {
		return "            case " + iliName + "_1 :" + LF + "               " + compInst.getName() + "_" + triggerInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

				"               /* INITIALIZE RECEIVED - store the previous state for use in the notification */" + LF + "               previousState = " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState;" + LF + LF +

				"               /* Update the module state */" + LF + "               " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState = ECOA__module_states_type_READY;" + LF + LF +

				"               /* Call the Module Instance Queue Operation */" + LF + "               " + compInst.getName() + "_" + supervisorModuleInstance.getName() + "_Controller__Lifecycle_Notification_" + triggerInstance.getName() + "(&timestamp, previousState, " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState);" + LF + "               break;" + LF;
	}

	private String generateSHUTDOWNHandler(SM_ModuleInstance supervisorModuleInstance) {
		return "            case " + iliName + "_4 :" + LF + "               " + compInst.getName() + "_" + triggerInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

				"               /* SHUTDOWN RECEIVED - store the previous state for use in the notification */" + LF + "               previousState = " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState;" + LF + LF +

				"               /* Update the module state */" + LF + "               " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState = ECOA__module_states_type_IDLE;" + LF + LF +

				"               /* Call the Module Instance Queue Operation */" + LF + "               " + compInst.getName() + "_" + supervisorModuleInstance.getName() + "_Controller__Lifecycle_Notification_" + triggerInstance.getName() + "(&timestamp, previousState, " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState);" + LF + "               break;" + LF;
	}

	private String generateSTARTHandler(SM_ModuleInstance supervisorModuleInstance) {
		return "            case " + iliName + "_2 :" + LF + "               " + compInst.getName() + "_" + triggerInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

				"               /* START RECEIVED - store the previous state for use in the notification */" + LF + "               previousState = " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState;" + LF + LF +

				"               /* Update the module state */" + LF + "               " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState = ECOA__module_states_type_RUNNING;" + LF + LF +

				"               /* Call the Module Instance Queue Operation */" + LF + "               " + compInst.getName() + "_" + supervisorModuleInstance.getName() + "_Controller__Lifecycle_Notification_" + triggerInstance.getName() + "(&timestamp, previousState, " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState);" + LF + "               break;" + LF;
	}

	private String generateSTOPHandler(SM_ModuleInstance supervisorModuleInstance) {
		return "            case " + iliName + "_3 :" + LF + "               " + compInst.getName() + "_" + triggerInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

				"               /* STOP RECEIVED - store the previous state for use in the notification */" + LF + "               previousState = " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState;" + LF + LF +

				"               /* Update the module state */" + LF + "               " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState = ECOA__module_states_type_READY;" + LF + LF +

				"               /* Call the Module Instance Queue Operation */" + LF + "               " + compInst.getName() + "_" + supervisorModuleInstance.getName() + "_Controller__Lifecycle_Notification_" + triggerInstance.getName() + "(&timestamp, previousState, " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState);" + LF + "               break;" + LF;
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve(triggerInstance.getName() + "/" + compInst.getName() + "_" + triggerInstance.getName() + "_Controller.h"));
		} else {
			super.openFile(outputDir.resolve(triggerInstance.getName() + "/" + compInst.getName() + "_" + triggerInstance.getName() + "_Controller.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#GET_STATE#" + LF + "#PROCESS_QUEUE#" + LF + "#QUEUE_MESSAGE#" + LF + "#TRIGGER#" + LF + "#LIFECYCLE_OPS#" + LF + "#INITIALISE#" + LF + "#REINIT#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#TRIGGER_QUEUE_ID_DECLARATION#" + LF + "#ILI_MESSAGE_DECL#" + LF + "#TRIGGER_MODULE_STATE#" + LF + "#GET_STATE#" + LF + "#PROCESS_QUEUE#" + LF + "#QUEUE_MESSAGE#" + LF + "#TRIGGER#" + LF + "#LIFECYCLE_OPS#" + LF + "#INITIALISE#" + LF + "#REINIT#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeGetState() {
		String getStateText = "";

		if (isHeader) {
			getStateText += SEP_PATTERN_391 + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__Get_Lifecycle_State(ECOA__module_states_type *moduleState);" + LF;
		} else {
			getStateText += SEP_PATTERN_391 + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__Get_Lifecycle_State(ECOA__module_states_type *moduleState)" + LF + "{" + LF + "   *moduleState = " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState;" + LF + "}" + LF;
		}

		// Replace the #TRIG_INST_ILI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GET_STATE#", getStateText);
	}

	public void writeILIMessageDecls() {
		String iliMessageDecl = "/* ILI Messages for use in ProcessQueue */" + LF + "static ILI_Message queueEntry;" + LF + "static ILI_Message lifecycleMessage;" + LF + LF +

				"/* Create a variable to hold if a pending state transition request is queued */" + LF + "static ECOA__boolean8 " + compInst.getName() + "_" + triggerInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF;

		// Replace the #ILI_MESSAGE_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#ILI_MESSAGE_DECL#", iliMessageDecl);
	}

	public void writeIncludes() {
		if (!isHeader) {
			includeList.addAll(underlyingPlatform.addIncludesTrigInstCont());
			includeList.add(compImpl.getName() + "_Module_Instance_ID");
			includeList.add(protectionDomain.getName() + "_PD_Controller");
		} else {
			includeList.add(iliName);
			includeList.add("ECOA");
			includeList.add("ECOA_time_utils");
			includeList.add("ILI_Message");
			includeList.add(compImpl.getName() + "_Module_Instance_Operation_UID");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);

	}

	public void writeInitialise(Generic_Platform underlyingPlatformInstantiation) {

		String initialiseText = "";

		if (isHeader) {
			initialiseText += SEP_PATTERN_391 + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__Initialise();" + LF + LF;
		} else {
			initialiseText += SEP_PATTERN_391 + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__Initialise() {" + LF + LF +

					"   Create_Message_Queue_Status_Type CMQ_Status;" + LF + LF +

					"   /* Set the module state to idle */" + LF + "   " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState = ECOA__module_states_type_IDLE;" + LF + LF +

					"   /* Create the Trigger Instance Message Queue */" + LF + "   Create_Message_Queue(50," + LF + // TODO
					// -
					// work
					// out
					// depth
					// of
					// message
					// queue.
					"       sizeof(ILI_Message)," + LF + "       &" + compInst.getName() + "_" + triggerInstance.getName() + "__QueueID," + LF + "       &CMQ_Status);" + LF + LF +

					"}";

			includeList.add(compInst.getName() + "_" + triggerInstance.getName() + "_Controller");
		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);

	}

	public void WriteLifecycleOps() {
		String lifecycleOpsText = "";

		// INITIALIZE operation
		lifecycleOpsText += "ECOA__return_status " + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__INITIALIZE_received(ECOA__timestamp *timestamp)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "   // Setup the ILI message and call queue message" + LF + "   ILI_Message message;" + LF +

					"   /* Set the messageID & timestamp */" + LF + "   message.messageID = " + iliName + "_1;" + LF + "   message.timestamp = *timestamp;" + LF + LF +

					"   return " + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__Queue_Message(&message);" + LF + "}" + LF + LF;
		}

		// START operation
		lifecycleOpsText += "ECOA__return_status " + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__START_received(ECOA__timestamp *timestamp)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "   // Setup the ILI message and call queue message" + LF + "   ILI_Message message;" + LF +

					"   /* Set the messageID & timestamp */" + LF + "   message.messageID = " + iliName + "_2;" + LF + "   message.timestamp = *timestamp;" + LF + LF +

					"   return " + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__Queue_Message(&message);" + LF + "}" + LF + LF;
		}

		// STOP operation
		lifecycleOpsText += "ECOA__return_status " + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__STOP_received(ECOA__timestamp *timestamp)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "   // Setup the ILI message and call queue message" + LF + "   ILI_Message message;" + LF +

					"   /* Set the messageID & timestamp */" + LF + "   message.messageID = " + iliName + "_3;" + LF + "   message.timestamp = *timestamp;" + LF + LF +

					"   return " + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__Queue_Message(&message);" + LF + "}" + LF + LF;
		}

		// SHUTDOWN operation
		lifecycleOpsText += "ECOA__return_status " + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__SHUTDOWN_received(ECOA__timestamp *timestamp)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "   // Setup the ILI message and call queue message" + LF + "   ILI_Message message;" + LF +

					"   /* Set the messageID & timestamp */" + LF + "   message.messageID = " + iliName + "_4;" + LF + "   message.timestamp = *timestamp;" + LF + LF +

					"   return " + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__Queue_Message(&message);" + LF + "}" + LF + LF;
		}

		// Replace the #LIFECYCLE_OPS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#LIFECYCLE_OPS#", lifecycleOpsText);
	}

	public void writeModuleStateDecl() {
		String moduleStateDecl = "static ECOA__module_states_type " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState;" + LF;

		// Replace the #TRIGGER_MODULE_STATE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#TRIGGER_MODULE_STATE#", moduleStateDecl);
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + compInst.getName() + "_" + triggerInstance.getName() + "_Controller.h */" + LF;
		} else {
			preambleText += "/* File " + compInst.getName() + "_" + triggerInstance.getName() + "_Controller.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeProcessQueue() {

		String processQueueString = "";

		if (isHeader) {
			processQueueString += SEP_PATTERN_391 + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__ProcessQueue();" + LF;
		} else {
			SM_ModuleInstance supervisorModuleInstance = null;
			// Set the supervisor module instance
			for (SM_ModuleInstance modInst : triggerInstance.getComponentImplementation().getModuleInstances().values()) {
				// NOTE - can only handle one supervisor module instance at the
				// moment.
				if (modInst.getModuleType().getIsSupervisor() == true) {
					supervisorModuleInstance = modInst;
					includeList.add(compInst.getName() + "_" + supervisorModuleInstance.getName() + "_Controller");
					break;
				}
			}

			processQueueString += SEP_PATTERN_391 + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__ProcessQueue() { " + LF + LF + "   ECOA__timestamp timestamp;" + LF + "   ECOA__module_states_type previousState;" + LF + "   Receive_Message_Queue_Status_Type RMQ_Status;" + LF + LF +

					"   /* Main queue processing loop */" + LF + "   while (1) {" + LF + LF +

					"      Receive_Message_Queue(" + compInst.getName() + "_" + triggerInstance.getName() + "__QueueID," + LF + "         (void *)&queueEntry," + LF + "         sizeof(ILI_Message)," + LF + "         &RMQ_Status);" + LF + LF +

					"      /* If \"dead\", decrement the currentqueuesize */" + LF + "      if (" + compInst.getName() + "_" + triggerInstance.getName() + "_ModuleDead)" + LF + "      {" + LF + "         if (" + compInst.getName() + "_" + triggerInstance.getName() + "_CurrentQueueSize == 0)" + LF + "         {" + LF + "            " + compInst.getName() + "_" + triggerInstance.getName() + "_ModuleDead = ECOA__FALSE;" + LF + "         }" + LF + "         else" + LF + "         {" + LF + "            " + compInst.getName() + "_" + triggerInstance.getName() + "_CurrentQueueSize--;" + LF + "         }" + LF + "      }" + LF + LF +

					"      if (RMQ_Status == Receive_Message_Queue_OK)" + LF + "      {" + LF + "         switch (queueEntry.messageID) {" + LF + generateINITIALIZEHandler(supervisorModuleInstance) + generateSTARTHandler(supervisorModuleInstance) + generateSTOPHandler(supervisorModuleInstance) + generateSHUTDOWNHandler(supervisorModuleInstance) + "         }" + LF + "      }" + LF + "   }" + LF + "}" + LF + LF;
		}

		// Replace the #PROCESS_QUEUE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PROCESS_QUEUE#", processQueueString);
	}

	public void writeQueueMessage() {
		String queueMessageText = "";

		if (isHeader) {
			queueMessageText += "ECOA__return_status " + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__Queue_Message(ILI_Message *iliMessage);" + LF;
		} else {
			queueMessageText += "ECOA__return_status " + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__Queue_Message(ILI_Message *iliMessage) {" + LF + LF +

					"   Send_Message_Queue_Status_Type smqStatus;" + LF + LF + "   ECOA__return_status queueStatus = ECOA__return_status_OK;" + LF + LF +

					"   unsigned char logBuffer[512];" + LF + "   int size;" + LF + LF +

					"   /* Should only ever receive lifecycle messages - always queue these */" + LF + "   if (iliMessage->messageID >= 1 && iliMessage->messageID <= 4)" + "   {" + LF + "      if (" + compInst.getName() + "_" + triggerInstance.getName() + "_PendingStateTransition == ECOA__TRUE)" + LF + "      {" + LF + "         size = sprintf((char*)logBuffer, \"Pending Module Lifecycle operation in queue (message being discarded!) - " + compInst.getName() + " " + triggerInstance.getName() + "\\n\");" + LF + "         ecoaLog(logBuffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "         queueStatus = ECOA__return_status_PENDING_STATE_TRANSITION;" + LF + "      }" + LF + "      else" + LF + "      {" + LF + "         " + compInst.getName() + "_" + triggerInstance.getName() + "_PendingStateTransition = ECOA__TRUE;" + LF + "         /* Queue all lifecycle messages */" + LF + "         Send_Message_Queue(" + compInst.getName() + "_" + triggerInstance.getName() + "__QueueID," + LF + "            iliMessage," + LF + "            sizeof(ILI_Message)," + LF + "            &smqStatus);" + LF + "      }" + LF + "   }" + LF + "   else" + LF + "   {" + LF
					+ "      /* Should not receive non-lifecycle messages */" + LF +

					"      size = sprintf((char*)logBuffer, \"" + compInst.getName() + "_" + triggerInstance.getName() + "_Controller - ILI_Message_%d not queued\\n\", iliMessage->messageID);" + LF + "      ecoaLog(logBuffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + LF + "      queueStatus = ECOA__return_status_OPERATION_NOT_AVAILABLE;" + LF + "   }" + LF + LF +

					"   return queueStatus;" + LF + "}" + LF + LF;
		}

		// Replace the #QUEUE_MESSAGE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#QUEUE_MESSAGE#", queueMessageText);
	}

	public void writeReinitialise() {
		String reinitText = "";

		if (isHeader) {
			reinitText += SEP_PATTERN_391 + trigInstContName + "__Reinitialise(ECOA__boolean8 restartThread);" + LF;
		} else {
			SM_DeployedTrigInst depTrigInst = null;
			// Get the associated deployed trigger instance (as we need thread
			// priority)
			for (SM_DeployedTrigInst dti : compInst.getDeployedTrigInsts()) {
				if (dti.getTrigInstance() == triggerInstance) {
					depTrigInst = dti;
					break;
				}
			}

			Integer priority = depTrigInst.getPriority();

			String modQueueThreadName = null;
			if (depTrigInst.getTrigInstance().getName().length() < 12) {
				modQueueThreadName = depTrigInst.getTrigInstance().getName() + "_MQ";
			} else {
				modQueueThreadName = depTrigInst.getTrigInstance().getName().substring(0, 12) + "_MQ";
			}

			reinitText += SEP_PATTERN_391 + trigInstContName + "__Reinitialise(ECOA__boolean8 restartThread)" + LF + "{" + LF + "   /* Set the Module State IDLE */" + LF + "   " + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState = ECOA__module_states_type_IDLE;" + LF + LF +

					"   /* Set the pending state transition flag false */" + LF + "   " + compInst.getName() + "_" + triggerInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

					"   /* Set the module \"dead\" flag */" + LF + "   " + compInst.getName() + "_" + triggerInstance.getName() + "_ModuleDead = ECOA__TRUE;" + LF + LF +

					"   /* Determine how many items are currently on the queue (as we must \"clear\" the queue upto this point\") */" + LF + "   " + compInst.getName() + "_" + triggerInstance.getName() + "_CurrentQueueSize = Get_Queue_Size(" + compInst.getName() + "_" + triggerInstance.getName() + "__QueueID);" + LF + LF +

					"   if (restartThread)" + LF + "   {" + LF + "      /* Restart the trigger thread (process queue thread) */" + LF + "      " + protectionDomain.getName() + "_startThread(" + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__ProcessQueue, " + priority.toString() + ", \"" + modQueueThreadName + "\", 0);" + LF + LF + "   }" + LF + "}" + LF + LF;

		}

		// Replace the #REINIT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#REINIT#", reinitText);
	}

	public void writeTriggerOps(Generic_Platform underlyingPlatformInstantiation) {

		String triggerString = "";
		int triggerFuncNum = 0;
		for (SM_EventLink eventLink : triggerInstance.getEventLinks()) {

			// Get the trigger instance sender
			SM_SenderTriggerInstance senderTrig = eventLink.getSenderTriggerInstance(triggerInstance);

			if (isHeader) {
				triggerString += SEP_PATTERN_391 + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__Trigger_" + triggerFuncNum + "();" + LF;
			} else {
				triggerString += SEP_PATTERN_391 + compInst.getName() + "_" + triggerInstance.getName() + "_Controller__Trigger_" + triggerFuncNum + "() {" + LF + LF +

						underlyingPlatformInstantiation.generateSleepVariableDecls() + LF +

						"   Send_Message_Queue_Status_Type SMQ_Status;" + LF + "   ECOA__int32 carry;" + LF + LF +

						underlyingPlatformInstantiation.generateSleepAttributes(senderTrig.getTriggerPeriodSec(), senderTrig.getTriggerPeriodNano()) + LF + underlyingPlatformInstantiation.generateGALTStatusAttribute() + underlyingPlatformInstantiation.generateGALTTimeAttribute("timeNow") + underlyingPlatformInstantiation.generateGALTTimeAttribute("nextTime") + underlyingPlatformInstantiation.generateSleepTimeAttribute("delayTime") + LF +

						underlyingPlatformInstantiation.generateGALT("nextTime") + LF +

						"   /* Main trigger loop */" + LF + "   while (1) {" + LF + LF +

						"      if (" + compInst.getName() + "_" + triggerInstance.getName() + "_moduleState == ECOA__module_states_type_RUNNING)" + LF + "      {" + LF + "         ECOA__timestamp timestamp;" + LF +

						"         /* Timestamp point */" + LF + "         ECOA_setTimestamp(&timestamp);" + LF + LF;

				for (SM_ReceiverInterface receiver : eventLink.getReceivers()) {
					includeList.add(compInst.getName() + "_" + receiver.getReceiverInst().getName() + "_Controller");
					if (receiver instanceof SM_ReceiverService) {
						triggerString += "         /* Call the Service API */" + LF + "         " + compInst.getName() + "_" + receiver.getReceiverInst().getName() + "_" + receiver.getReceiverOp().getName() + "__event_send(&timestamp);" + LF;
					} else if (receiver.getReceiverInst() instanceof SM_ModuleInstance) {
						String triggerUID = compImpl.getName().toUpperCase() + "_" + triggerInstance.getName().toUpperCase() + "_TRIGGEROP_UID";

						triggerString += "         /* Call the Module Instance Queue Operation */" + LF + "         " + compInst.getName() + "_" + receiver.getReceiverInst().getName() + "_Controller__" + receiver.getReceiverOp().getName() + "__event_received(&timestamp, " + triggerUID + ");" + LF;
					}
				}

				triggerString += "      }" + LF + LF +

						"      // Determine the next time we should run" + LF + underlyingPlatformInstantiation.determineNextTimeToTrigger("nextTime") + LF + LF +

						"      // Get the time now" + LF + "   " + underlyingPlatformInstantiation.generateGALT("timeNow") + LF +

						"      // Determine the time we need to sleep for" + LF + underlyingPlatformInstantiation.determineDelayTimeForTrigger("delayTime", "nextTime", "timeNow") + LF + LF +

						"      // Ensure the trigger time has not passed" + LF + underlyingPlatformInstantiation.checkTriggerInFuture("delayTime", "nextTime", "timeNow") + "         " + underlyingPlatformInstantiation.generateSleepCall("delayTime") + "      }" + LF + "   }" + LF + "}" + LF;
			}
			triggerFuncNum++;
		}

		// Replace the #TRIGGER# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#TRIGGER#", triggerString);
	}

	public void writeTriggerQueueIDDecl() {
		String triggerQueueIDDecl = "static Message_Queue_ID_Type " + compInst.getName() + "_" + triggerInstance.getName() + "__QueueID;" + LF + LF +

				"static ECOA__boolean8 " + compInst.getName() + "_" + triggerInstance.getName() + "_ModuleDead = ECOA__FALSE;" + LF + LF +

				"static int " + compInst.getName() + "_" + triggerInstance.getName() + "_CurrentQueueSize = 0;" + LF;

		// Replace the #TRIGGER_QUEUE_ID_DECLARATION# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#TRIGGER_QUEUE_ID_DECLARATION#", triggerQueueIDDecl);

	}

}
