/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.moduleinstance;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.DataILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ErrorNotificationILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.EventILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.FaultNotificationILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.LifecycleILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.LifecycleILIMessage.ILIMessageType;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ModuleInstanceILI;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.RequestILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ResponseILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ServiceAvailNotificationILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ServiceProviderNotificationILIMessage;
import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstanceProperty;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_PinfoValue;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_DataLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_ReaderModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderExternal;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ClientInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ClientModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_RequestLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ServerModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataReadOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventReceivedOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestReceivedOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestSentOp;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedModInst;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.types.SM_Array_Type;
import com.iawg.ecoa.systemmodel.types.SM_Fixed_Array_Type;

public class ModInstContWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_1211 = "void ";
	private static final String SEP_PATTERN_A = "                     printf(\">>>>> ";

	private SM_ProtectionDomain protectionDomain;
	private SM_DeployedModInst deployedModInst;
	private SM_ModuleInstance moduleInstance;
	private SM_ComponentInstance compInst;
	private boolean isHeader;
	private String iliName = "";
	private ModuleInstanceILI modInstILI = null;
	private String modInstContName;

	private boolean isSupervisor = false;
	private SM_ModuleInstance supervisorModInst = null;
	private ArrayList<SM_ModuleInstance> subordinateModuleInstances = new ArrayList<SM_ModuleInstance>();
	private ArrayList<SM_TriggerInstance> subordinateTriggerInstances = new ArrayList<SM_TriggerInstance>();
	private ArrayList<SM_DynamicTriggerInstance> subordinateDynamicTriggerInstances = new ArrayList<SM_DynamicTriggerInstance>();

	private ArrayList<String> includeList = new ArrayList<String>();
	private Generic_Platform underlyingPlatform;

	public ModInstContWriterC(PlatformGenerator platformGenerator, boolean isHeader, Path outputDir, SM_DeployedModInst deployedModInst, SM_ProtectionDomain pd, ModuleInstanceILI modInstILI) {
		super(outputDir);
		this.underlyingPlatform = platformGenerator.getunderlyingPlatformInstantiation();
		this.protectionDomain = pd;
		this.deployedModInst = deployedModInst;
		this.compInst = deployedModInst.getCompInstance();
		this.moduleInstance = deployedModInst.getModInstance();
		this.isHeader = isHeader;
		this.iliName = moduleInstance.getName() + "_ILI";
		this.modInstContName = compInst.getName() + "_" + moduleInstance.getName() + "_Controller";

		this.modInstILI = modInstILI;

		setFileStructure();

		// Need to determine if processing a supervisor module instance and if
		// not; which module is the supervisor.
		if (moduleInstance.getModuleType().getIsSupervisor()) {
			isSupervisor = true;

			// Store the list of subordinate module instances.
			for (SM_ModuleInstance subModInst : moduleInstance.getComponentImplementation().getModuleInstances().values()) {
				// Check it's not the module instance currently being processed
				if (subModInst != moduleInstance) {
					// Check that it too is not a supervisor (NOTE - CANNOT
					// HANDLE MORE THAN ONE SUPERVISOR AT THE MOMENT ANYWAY!)
					if (subModInst.getModuleType().getIsSupervisor() != true) {
						subordinateModuleInstances.add(subModInst);
					}
				}
			}
			// Store the list of subordinate trigger instances.
			for (SM_TriggerInstance subTrigInst : moduleInstance.getComponentImplementation().getTriggerInstances().values()) {
				subordinateTriggerInstances.add(subTrigInst);
			}
			// Store the list of subordinate dynamic trigger instances.
			for (SM_DynamicTriggerInstance subTrigInst : moduleInstance.getComponentImplementation().getDynamicTriggerInstances().values()) {
				subordinateDynamicTriggerInstances.add(subTrigInst);
			}
		} else {
			// Set the supervisor module instance
			for (SM_ModuleInstance modInst : moduleInstance.getComponentImplementation().getModuleInstances().values()) {
				// NOTE - can only handle one supervisor module instance at the
				// moment.
				if (modInst.getModuleType().getIsSupervisor() == true) {
					supervisorModInst = modInst;
					includeList.add(compInst.getName() + "_" + supervisorModInst.getName() + "_Controller");
					break;
				}
			}
		}

	}

	private String assignInputParamsToMessage(List<SM_OperationParameter> inputParams, ILIMessage ili) {
		String inputParamsString = "";

		if (inputParams.size() > 0) {
			inputParamsString += "   /* Malloc memory for the input params */" + LF + "   message.messageDataPointer = malloc(sizeof(" + iliName + "_" + ili.getMessageID() + "_params));" + LF + "   if (message.messageDataPointer != 0)" + LF + "   {" + LF;

			for (SM_OperationParameter opParam : inputParams) {
				if (opParam.getType() instanceof SM_Fixed_Array_Type || opParam.getType() instanceof SM_Array_Type) {
					inputParamsString += "      memcpy(&(((" + iliName + "_" + ili.getMessageID() + "_params*)message.messageDataPointer)->" + opParam.getName() + "), " + opParam.getName() + ", sizeof(" + CLanguageSupport.writeType(opParam.getType()) + "));" + LF;

					includeList.add("string");
				} else {
					inputParamsString += "      ((" + iliName + "_" + ili.getMessageID() + "_params*)message.messageDataPointer)->" + opParam.getName() + " =" + CLanguageSupport.writeAssignment(opParam);
				}
			}

			inputParamsString += "   }" + LF + "   else" + LF + "   {" + LF + "      printf(\"malloc failed in " + modInstContName + "\\n\");" + LF + "   }" + LF + LF;

		} else {
			inputParamsString += "   message.messageDataPointer = 0;" + LF;
		}

		return inputParamsString;
	}

	private String assignParamsToResponseMessage(SM_RequestSentOp requestTx, ILIMessage ili) {
		String responseParamsString = "";

		// if (requestTx.getOutputs().size() > 0) // Always true, since there is
		// a hidden (system imposed) responseStatus parameter...
		{
			responseParamsString += "   /* Malloc memory for the output params */" + LF + "   message.messageDataPointer = malloc(sizeof(" + iliName + "_" + ili.getMessageID() + "_params));" + LF + "   if (message.messageDataPointer != 0)" + LF + "   {" + LF;

			for (SM_OperationParameter opParam : ili.getParams()) {
				responseParamsString += "      ((" + iliName + "_" + ili.getMessageID() + "_params*)message.messageDataPointer)->" + opParam.getName() + " =" + CLanguageSupport.writeOutAssignment(opParam);

			}

			responseParamsString += "   }" + LF + "   else" + LF + "   {" + LF + "      printf(\"malloc failed in " + modInstContName + "\\n\");" + LF + "   }" + LF + LF;
		}

		return responseParamsString;
	}

	private int determineQueueSize() {
		// Queue size is the total of the fifoSize of all event received,
		// request received (server), response receive (async request), and
		// notifying VD
		// reads
		int queueSize = 0;

		// Get event received ops fifoSize
		for (SM_ReceiverModuleInstance receiver : moduleInstance.getEventLinksAsReceiver()) {
			queueSize += receiver.getFifoSize().intValue();
		}

		// Get request received ops fifoSize (server)
		for (SM_ServerModuleInstance server : moduleInstance.getRequestLinksAsServer()) {
			queueSize += server.getFifoSize().intValue();
		}

		// Get response received ops fifoSize (async request client)
		for (SM_ClientModuleInstance client : moduleInstance.getRequestLinksAsClient()) {
			if (!client.getClientOp().getIsSynchronous()) {
				queueSize += client.getClientOp().getMaxConcurrentRequests().intValue();
			}
		}

		// Get notifying versioned data ops fifoSize
		for (SM_ReaderModuleInstance reader : moduleInstance.getDataLinksAsReader()) {
			if (((SM_DataReadOp) reader.getReaderOp()).getIsNotifying()) {
				queueSize += reader.getFifoSize().intValue();
			}
		}

		// Add additional queue entry for INITIALIZE, START, STOP, REINITIALIZE
		// (only one operation can be queued at once!).
		queueSize += 1;

		int notificationSize = deployedModInst.getProtectionDomain().getLogicalComputingNode().getLogicalComputingPlatform().getNotificationMaxNumber();

		// If Supervision module, add additional lifecycle notifications +
		// service availability ops fifoSizes...
		if (moduleInstance.getModuleType().getIsSupervisor()) {
			// TODO - need to ensure this logic is correct - ensure it is
			// consistent with setting of values in the "initialise" procedure!

			// Add additional queue entries for service availability changed and
			// service provider changed
			queueSize += notificationSize * 2;

			// Add additional queue entries for error notifications
			queueSize += notificationSize;

			// Add additional queue entries for each non-supervision
			// module/trigger instance.
			queueSize += (moduleInstance.getComponentImplementation().getModuleInstances().size() - 1); // -1
			// for
			// supervision
			// module!
			queueSize += (moduleInstance.getComponentImplementation().getTriggerInstances().size());
			queueSize += (moduleInstance.getComponentImplementation().getDynamicTriggerInstances().size());
		}

		// If Fault Handler module, add additional error notificaiton ops
		// fifoSizes...
		if (moduleInstance.getModuleType().getIsFaultHandler()) {
			queueSize += notificationSize;
		}

		return queueSize;
	}

	private String generateINITIALIZEHandler(ILIMessage iliMessage) {
		String cimiName = compInst.getName() + "_" + moduleInstance.getName();

		String initHandlerText = "            case " + iliName + "_" + iliMessage.getMessageID() + " :" + LF + "               if (!" + compInst.getName() + "_" + moduleInstance.getName() + "_ModuleDead)" + LF + "               {" + LF + "                  /* Unset Pending State Transition flag */" + LF + "                  " + compInst.getName() + "_" + moduleInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

				"                  /* Store the previous state */" + LF + "                  previousState = " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState;" + LF + LF +

				"                  /* Update the module state (within the private context) */" + LF + "                  " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState = ECOA__module_states_type_READY;" + LF + LF +

				"                  /* Set the operation timestamp */" + LF + "                  " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context.operation_timestamp =  queueEntry.timestamp;" + LF + LF +

				"                  /* Reload warm start context */" + LF + "                  ECOA_get_context(\"" + cimiName + "_warm_context.bin\", &" + cimiName + "_contexts." + moduleInstance.getName() + "_context.warm_start, sizeof(" + moduleInstance.getImplementation().getName() + "_warm_start_context));" + LF +

				"                  if (previousState == ECOA__module_states_type_IDLE)" + LF + "                  {" + LF + "                     /* Call the INITIALIZE function */" + LF;
		if (moduleInstance.getImplementation().isInstrument()) {
			initHandlerText += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__INITIALIZE__received()\\n\");" + LF + LF;
		}

		initHandlerText += "                     " + moduleInstance.getImplementation().getName() + "__INITIALIZE__received(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context);" + LF + "                  }" + LF + "                  else" + LF + "                  {" + LF + "                     /* Call the REINITIALIZE function */" + LF;
		if (moduleInstance.getImplementation().isInstrument()) {
			initHandlerText += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__REINITIALIZE__received()\\n\");" + LF + LF;
		}

		initHandlerText += "                     " + moduleInstance.getImplementation().getName() + "__REINITIALIZE__received(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context);" + LF + "                  }" + LF + LF;

		// Generate the lifecycle notification if we are not a supervisor.
		if (!isSupervisor) {
			initHandlerText += "                 /* Timestamp point */" + LF + "                 ECOA_setTimestamp(&timestamp);" + LF + LF +

					"                 /* Call the Module Instance Queue Operation */" + LF;

			if (moduleInstance.getImplementation().isInstrument()) {
				initHandlerText += SEP_PATTERN_A + compInst.getName() + "_" + supervisorModInst.getName() + "_Controller__Lifecycle_Notification_" + moduleInstance.getName() + "()\\n\");" + LF + LF;
			}

			initHandlerText += "                 " + compInst.getName() + "_" + supervisorModInst.getName() + "_Controller__Lifecycle_Notification_" + moduleInstance.getName() + "(&timestamp, previousState, " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState);" + LF + LF;
		} else {
			initHandlerText += "                  /* Timestamp point */" + LF + "                  ECOA_setTimestamp(&timestamp);" + LF + LF +

					"                  /* Call the Supervision Notification function */" + LF + "                  " + modInstContName + "__SupervisionNotification(&timestamp);" + LF;
		}

		initHandlerText += "               }" + LF + "               break;" + LF;
		return initHandlerText;
	}

	private String generateLIFECYCLE_NOTIFICATIONHandler(ILIMessage iliMessage, ArrayList<SM_ModuleInstance> subordinateModuleInstances, ArrayList<SM_TriggerInstance> subordinateTriggerInstances, ArrayList<SM_DynamicTriggerInstance> subordinateDynamicTriggerInstances) {
		// TODO - should we be handling the module dead flag on this message?
		// Supervision only message - needs thinking about...

		String lifecycleNotifyText = "";
		if (subordinateModuleInstances.size() > 0 || subordinateTriggerInstances.size() > 0 || subordinateDynamicTriggerInstances.size() > 0) {
			lifecycleNotifyText = "            case " + iliName + "_" + iliMessage.getMessageID() + " :" + LF + "            {" + LF + "               " + iliName + "_5_params *" + iliName + "_5_params_ptr = (" + iliName + "_5_params*)queueEntry.messageDataPointer;" + LF +

					"               " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context.operation_timestamp =  queueEntry.timestamp;" + LF + LF +

					"               /* Determine which subordinate module/trigger instance this message is from */" + LF + "               switch (" + iliName + "_5_params_ptr->moduleInstanceId)" + LF + "               {" + LF;

			for (SM_ModuleInstance modInstance : subordinateModuleInstances) {
				lifecycleNotifyText += "               case " + modInstance.getComponentImplementation().getName().toUpperCase() + "_" + modInstance.getName().toUpperCase() + "_ID :" + LF;

				if (moduleInstance.getImplementation().isInstrument()) {
					lifecycleNotifyText += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__lifecycle_notification__" + modInstance.getName() + "()\\n\");" + LF + LF;
				}

				lifecycleNotifyText += "                  " + moduleInstance.getImplementation().getName() + "__lifecycle_notification__" + modInstance.getName() + "(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context, " + iliName + "_5_params_ptr->previous_state, " + iliName + "_5_params_ptr->new_state);" + LF + "                  break;" + LF;
			}
			for (SM_TriggerInstance trigInstance : subordinateTriggerInstances) {
				lifecycleNotifyText += "               case " + trigInstance.getComponentImplementation().getName().toUpperCase() + "_" + trigInstance.getName().toUpperCase() + "_ID :" + LF;

				if (moduleInstance.getImplementation().isInstrument()) {
					lifecycleNotifyText += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__lifecycle_notification__" + trigInstance.getName() + "()\\n\");" + LF + LF;
				}

				lifecycleNotifyText += "                  " + moduleInstance.getImplementation().getName() + "__lifecycle_notification__" + trigInstance.getName() + "(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context, " + iliName + "_5_params_ptr->previous_state, " + iliName + "_5_params_ptr->new_state);" + LF + "                  break;" + LF;
			}
			for (SM_DynamicTriggerInstance trigInstance : subordinateDynamicTriggerInstances) {
				lifecycleNotifyText += "               case " + trigInstance.getComponentImplementation().getName().toUpperCase() + "_" + trigInstance.getName().toUpperCase() + "_ID :" + LF;

				if (moduleInstance.getImplementation().isInstrument()) {
					lifecycleNotifyText += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__lifecycle_notification__" + trigInstance.getName() + "()\\n\");" + LF + LF;
				}

				lifecycleNotifyText += "                  " + moduleInstance.getImplementation().getName() + "__lifecycle_notification__" + trigInstance.getName() + "(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context, " + iliName + "_5_params_ptr->previous_state, " + iliName + "_5_params_ptr->new_state);" + LF + "                  break;" + LF;
			}

			lifecycleNotifyText += "               }" + LF + "               break;" + LF + "            }" + LF + LF;
		}

		return lifecycleNotifyText;

	}

	private String generateSHUTDOWNHandler(ILIMessage iliMessage) {
		String shutdownHandlerText = "            case " + iliName + "_" + iliMessage.getMessageID() + " :" + LF + "               if (!" + compInst.getName() + "_" + moduleInstance.getName() + "_ModuleDead)" + LF + "               {" + LF;

		if (!isSupervisor) {
			shutdownHandlerText += "                  /* Store the previous state for use in the notification */" + LF + "                  previousState = " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState;" + LF + LF;
		}

		shutdownHandlerText += "                  /* Unset Pending State Transition flag */" + LF + "                  " + compInst.getName() + "_" + moduleInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

				"                  /* Update the module state (within the private context) */" + LF + "                  " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState = ECOA__module_states_type_IDLE;" + LF + LF +

				"                  /* Set the operation timestamp */" + LF + "                  " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context.operation_timestamp =  queueEntry.timestamp;" + LF + LF +

				"                  /* Call the SHUTDOWN function */" + LF;

		if (moduleInstance.getImplementation().isInstrument()) {
			shutdownHandlerText += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__SHUTDOWN__received()\\n\");" + LF + LF;
		}

		shutdownHandlerText += "                  " + moduleInstance.getImplementation().getName() + "__SHUTDOWN__received(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context);" + LF + LF;

		// Generate the lifecycle notification if we are not a supervisor.
		if (!isSupervisor) {
			shutdownHandlerText += "                 /* Timestamp point */" + LF + "                 ECOA_setTimestamp(&timestamp);" + LF + LF +

					"                 /* Call the Module Instance Queue Operation */" + LF + "                 " + compInst.getName() + "_" + supervisorModInst.getName() + "_Controller__Lifecycle_Notification_" + moduleInstance.getName() + "(&timestamp, previousState, " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState);" + LF + LF;
		}

		shutdownHandlerText += "               }" + LF + "               break;" + LF;
		return shutdownHandlerText;
	}

	private String generateSTARTHandler(ILIMessage iliMessage) {
		String startHandlerText = "            case " + iliName + "_" + iliMessage.getMessageID() + " :" + LF + "               if (!" + compInst.getName() + "_" + moduleInstance.getName() + "_ModuleDead)" + LF + "               {" + LF;

		if (!isSupervisor) {
			startHandlerText += "                  /* Store the previous state for use in the notification */" + LF + "                  previousState = " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState;" + LF + LF;
		}

		startHandlerText += "                  /* Unset Pending State Transition flag */" + LF + "                  " + compInst.getName() + "_" + moduleInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

				"                  /* Update the module state (within the private context) */" + LF + "                  " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState = ECOA__module_states_type_RUNNING;" + LF + LF +

				"                  /* Set the operation timestamp */" + LF + "                  " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context.operation_timestamp =  queueEntry.timestamp;" + LF + LF +

				"                  /* Call the START function */" + LF;

		if (moduleInstance.getImplementation().isInstrument()) {
			startHandlerText += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__START__received()\\n\");" + LF + LF;
		}

		startHandlerText += "                  " + moduleInstance.getImplementation().getName() + "__START__received(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context);" + LF + LF;

		// Generate the lifecycle notification if we are not a supervisor.
		if (!isSupervisor) {
			startHandlerText += "                 /* Timestamp point */" + LF + "                 ECOA_setTimestamp(&timestamp);" + LF + LF +

					"                 /* Call the Module Instance Queue Operation */" + LF + "                 " + compInst.getName() + "_" + supervisorModInst.getName() + "_Controller__Lifecycle_Notification_" + moduleInstance.getName() + "(&timestamp, previousState, " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState);" + LF + LF;
		}

		startHandlerText += "               }" + LF + "               break;" + LF;
		return startHandlerText;
	}

	private String generateSTOPHandler(ILIMessage iliMessage) {
		String stopHandlerText = "            case " + iliName + "_" + iliMessage.getMessageID() + " :" + LF + "               if (!" + compInst.getName() + "_" + moduleInstance.getName() + "_ModuleDead)" + LF + "               {" + LF;

		if (!isSupervisor) {
			stopHandlerText += "                  /* Store the previous state for use in the notification */" + LF + "                  previousState = " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState;" + LF + LF;
		}

		stopHandlerText += "                  /* Unset Pending State Transition flag */" + LF + "                  " + compInst.getName() + "_" + moduleInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

				"                  /* Update the module state (within the private context) */" + LF + "                  " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState = ECOA__module_states_type_READY;" + LF + LF +

				"                  /* Set the operation timestamp */" + LF + "                  " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context.operation_timestamp =  queueEntry.timestamp;" + LF + LF +

				"                  /* Call the STOP function */" + LF;

		if (moduleInstance.getImplementation().isInstrument()) {
			stopHandlerText += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__STOP__received()\\n\");" + LF + LF;
		}

		stopHandlerText += "                  " + moduleInstance.getImplementation().getName() + "__STOP__received(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context);" + LF + LF;

		// Generate the lifecycle notification if we are not a supervisor.
		if (!isSupervisor) {

			stopHandlerText += "                 /* Timestamp point */" + LF + "                 ECOA_setTimestamp(&timestamp);" + LF + LF +

					"                 /* Call the Module Instance Queue Operation */" + LF + "                 " + compInst.getName() + "_" + supervisorModInst.getName() + "_Controller__Lifecycle_Notification_" + moduleInstance.getName() + "(&timestamp, previousState, " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState);" + LF + LF;
		}

		stopHandlerText += "               }" + LF + "               break;" + LF;
		return stopHandlerText;
	}

	private String getUID(SM_SenderInterface senderInterface) {
		if (!(senderInterface instanceof SM_SenderExternal)) {
			return compInst.getImplementation().getName().toUpperCase() + "_" + senderInterface.getSenderInst().getName().toUpperCase() + "_" + senderInterface.getSenderOpName().toUpperCase() + "_UID";
		} else {
			return compInst.getImplementation().getName().toUpperCase() + "_EXTERNAL_" + senderInterface.getSenderOpName().toUpperCase() + "_UID";
		}
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve(modInstContName + ".h"));
		} else {
			super.openFile(outputDir.resolve(modInstContName + ".c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#GET_STATE#" + LF + "#GET_LOOKUP#" + LF + "#PROCESS_QUEUE#" + LF + "#QUEUE_MESSAGE#" + LF + "#EVENT_RECEIVES#" + LF + "#REQUEST_RECEIVES#" + LF + "#RESPONSE_RECEIVES#" + LF + "#UPDATES#" + LF + "#SUPER_OPS#" + LF + "#LIFECYCLE_OPS#" + LF + "#PINFO#" + LF + "#INITIALISE#" + LF + "#REINIT#" + LF + "#ZERO_WARM_CONTEXT#" + LF + "#SUPER_NOTIFY#" + LF + "#SYNC_REQUEST_QUEUE_ID_DECL#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#FIFO_DEF#" + LF + "#MOD_QUEUE_ID_DECL#" + LF + "#SYNC_REQUEST_QUEUE_ID_DECL#" + LF + "#ILI_MESSAGE_DECL#" + LF + "#PRIVATE_CONTEXT_DECL#" + LF + "#CLIENT_LOOKUP_DECL#" + LF + "#PINFO_DECL#" + LF + "#FIFO_ACCESSORS#" + LF + "#GET_STATE#" + LF + "#SET_LOOKUP#" + LF + "#GET_LOOKUP#" + LF + "#PROCESS_QUEUE#" + LF + "#IS_QUEUE_FULL#" + LF + "#QUEUE_MESSAGE#" + LF + "#EVENT_RECEIVES#" + LF + "#REQUEST_RECEIVES#" + LF + "#RESPONSE_RECEIVES#" + LF + "#UPDATES#" + LF + "#SUPER_OPS#" + LF + "#LIFECYCLE_OPS#" + LF + "#SUPER_NOTIFY#" + LF + "#PINFO#" + LF + "#INITIALISE#" + LF + "#REINIT#" + LF + "#ZERO_WARM_CONTEXT#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeClientLookupDecl() {
		String clientLookText =
				// Client lookup list
				"/* Define the Client Lookup Structure */" + LF + "typedef struct {" + LF + "   ECOA__uint32 ID;" + LF + "   Client_Info_Type clientInfo;" + LF + "} Client_Lookup_Type;" + LF + LF +

				// TODO - should probably work this out somehow...
						"#define " + modInstContName + "_Client_Lookup_MAXSIZE 100" + LF + "typedef Client_Lookup_Type Client_Lookup_List_Type[" + modInstContName + "_Client_Lookup_MAXSIZE];" + LF + LF +

						"/* Declare the Client Lookup List */" + LF + "static Client_Lookup_List_Type " + modInstContName + "_clientLookupList;" + LF + LF;

		// Replace the #CLIENT_LOOKUP_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#CLIENT_LOOKUP_DECL#", clientLookText);
	}

	public void WriteEventReceives() {
		String eventRxText = "";

		for (SM_EventReceivedOp evRx : moduleInstance.getModuleType().getEventReceivedOps()) {
			eventRxText += SEP_PATTERN_1211 + modInstContName + "__" + evRx.getName() + "__event_received(ECOA__timestamp *timestamp, ECOA__uint32 senderID";

			for (SM_OperationParameter opParam : evRx.getInputs()) {
				eventRxText += CLanguageSupport.writeConstParam(opParam);
			}

			if (isHeader) {
				eventRxText += ");" + LF;
			} else {
				eventRxText += ")" + LF + "{" + LF + "   // Setup the ILI message and call queue message" + LF + "   ILI_Message message;" + LF + LF +

						"   switch (senderID)" + LF + "   {" + LF;

				ILIMessage ili = null;
				for (SM_EventLink evLink : moduleInstance.getEventLinksForReceiverOp(evRx)) {
					for (SM_SenderInterface senderInterface : evLink.getSenders()) {
						ili = modInstILI.getILIForEventLink(evLink);

						eventRxText += "      case " + getUID(senderInterface) + " : " + LF + "      {" + LF + "         /* Set the messageID & timestamp */" + LF + "         message.messageID = " + iliName + "_" + ili.getMessageID() + ";" + LF + "         message.timestamp = *timestamp;" + LF + LF +

								assignInputParamsToMessage(evRx.getInputs(), ili) + "         " + modInstContName + "__Queue_Message(&message);" + LF + "         break;" + LF + "      }" + LF + LF;
					}
				}
				eventRxText +=
						// end the switch statement
						"   }" + LF + "}" + LF + LF;
			}
		}

		// Replace the #EVENT_RECEIVES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#EVENT_RECEIVES#", eventRxText);
	}

	public void writeFIFOAccessors() {
		String fifoAccessorsText = "";

		if (!isHeader) {
			fifoAccessorsText += SEP_PATTERN_1211 + modInstContName + "__Increment_FIFO(int ID)" + LF + "{" + LF + "   Wait_For_Semaphore_Status_Type Wait_For_Semaphore_Status;" + LF + "   Post_Semaphore_Status_Type Post_Semaphore_Status;" + LF + "   Time_Type Timeout = {MAX_SECONDS, MAX_NANOSECONDS};" + LF + LF +

					"   Wait_For_Semaphore(" + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeListSemaphore," + LF + "      &Timeout," + LF + "      &Wait_For_Semaphore_Status);" + LF + LF +

					"   " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList[ID].currentSize++;" + LF + LF +

					"   Post_Semaphore(" + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeListSemaphore," + LF + "      &Post_Semaphore_Status);" + LF + LF + "}" + LF + LF +

					SEP_PATTERN_1211 + modInstContName + "__Decrement_FIFO(int ID)" + LF + "{" + LF + "   Wait_For_Semaphore_Status_Type Wait_For_Semaphore_Status;" + LF + "   Post_Semaphore_Status_Type Post_Semaphore_Status;" + LF + "   Time_Type Timeout = {MAX_SECONDS, MAX_NANOSECONDS};" + LF + LF +

					"   Wait_For_Semaphore(" + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeListSemaphore," + LF + "      &Timeout," + LF + "      &Wait_For_Semaphore_Status);" + LF + LF +

					"   " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList[ID].currentSize--;" + LF + LF +

					"   Post_Semaphore(" + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeListSemaphore," + LF + "      &Post_Semaphore_Status);" + LF + LF + "}" + LF + LF;
		}

		// Replace the #FIFO_ACCESSORS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#FIFO_ACCESSORS#", fifoAccessorsText);
	}

	public void writeFifoListDefinition() {
		String fifoString = "";

		if (!isHeader) {
			// Create the FIFO list type definition.
			fifoString += LF + "typedef struct" + LF + "{" + LF + "   ECOA__uint32 currentSize;" + LF + "   ECOA__uint32 maxSize;" + LF + "} fifoSizeType;" + LF + LF +

			// NOTE - have to + 1 to size so can index into array on ILI ID
			// (which starts at 1)
					"#define " + compInst.getName() + "_" + moduleInstance.getName() + "_ILI_ID_SIZE " + (modInstILI.getILIMessageList().size() + 1) + LF + "typedef fifoSizeType " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFO_Queue_Size_List_Type[" + compInst.getName() + "_" + moduleInstance.getName() + "_ILI_ID_SIZE];" + LF + LF +

					// Create the FIFO list declaration
					compInst.getName() + "_" + moduleInstance.getName() + "_FIFO_Queue_Size_List_Type " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList;" + LF + LF +

					"/* Create a variable to hold if a pending state transition request is queued */" + LF + "static ECOA__boolean8 " + compInst.getName() + "_" + moduleInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

					"/* Create fifoSizeList Access Semaphore */" + LF + "static int " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeListSemaphore;" + LF;
		}

		// Replace the #FIFO_DEF# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#FIFO_DEF#", fifoString);
	}

	public void writeGetClientLookup() {
		String getLookText = SEP_PATTERN_1211 + modInstContName + "__Get_Client_Lookup(ECOA__uint32 ID, Client_Info_Type *clientInfo)";

		if (isHeader) {
			getLookText += ";" + LF;
			includeList.add("Client_Info_Type");
		} else {
			getLookText += LF + "{" + LF + "   int i;" + LF + "   for (i = 0; i < " + modInstContName + "_Client_Lookup_MAXSIZE; i++)" + LF + "   {" + LF + "      if (" + modInstContName + "_clientLookupList[i].ID == ID)" + LF + "      {" + LF + "         /* Set the seqNum to -1 (empty) and set the clientInfo return*/" + LF + "         " + modInstContName + "_clientLookupList[i].ID = -1;" + LF + "         clientInfo->type = " + modInstContName + "_clientLookupList[i].clientInfo.type;" + LF + "         clientInfo->ID = " + modInstContName + "_clientLookupList[i].clientInfo.ID;" + LF + "         clientInfo->serviceUID = " + modInstContName + "_clientLookupList[i].clientInfo.serviceUID;" + LF + "         clientInfo->localSeqNum = " + modInstContName + "_clientLookupList[i].clientInfo.localSeqNum;" + LF + "         clientInfo->globalSeqNum = " + modInstContName + "_clientLookupList[i].clientInfo.globalSeqNum;" + LF + "         break;" + LF + "      }" + LF + "   }" + LF + "}" + LF;
		}

		// Replace the #GET_LOOKUP# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GET_LOOKUP#", getLookText);
	}

	public void writeGetState() {
		String getStateText = "";

		if (isHeader) {
			getStateText += SEP_PATTERN_1211 + modInstContName + "__Get_Lifecycle_State(ECOA__module_states_type *moduleState);" + LF;
		} else {
			getStateText += SEP_PATTERN_1211 + modInstContName + "__Get_Lifecycle_State(ECOA__module_states_type *moduleState)" + LF + "{" + LF + "   *moduleState = " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState;" + LF + "}" + LF;
		}

		// Replace the #GET_STATE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GET_STATE#", getStateText);
	}

	public void writeILIMessageDecls() {
		String iliMessageDecl = "/* ILI Message for use in ProcessQueue */" + LF + "static ILI_Message queueEntry;" + LF;

		// Generate a lifecycle response message if not the supervisor.
		if (!isSupervisor) {
			iliMessageDecl += "static ILI_Message lifecycleResponse;" + LF;
		}

		// Replace the #ILI_MESSAGE_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#ILI_MESSAGE_DECL#", iliMessageDecl);
	}

	public void writeIncludes() {
		if (isHeader) {
			includeList.add("ECOA");
			includeList.add("message_queue");
			includeList.add(iliName);
			includeList.add("ILI_Message");
		} else {
			includeList.add("ECOA_time_utils");
			includeList.add(moduleInstance.getImplementation().getName());
			includeList.add(moduleInstance.getImplementation().getName() + "_container");
			includeList.add(modInstContName);
			includeList.add(compInst.getImplementation().getName() + "_Module_Instance_ID");
			includeList.add(compInst.getImplementation().getName() + "_Module_Instance_Operation_UID");
			includeList.add(compInst.getImplementation().getName() + "_Service_Instance_Operation_UID");
			includeList.addAll(underlyingPlatform.addIncludesModInstCont());
			includeList.add(protectionDomain.getName() + "_PD_Controller");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeInitialise(Generic_Platform underlyingPlatformInstantiation) {
		String initialiseText = "";

		if (isHeader) {
			initialiseText += SEP_PATTERN_1211 + modInstContName + "__Initialise();" + LF;
		} else {

			int queueSize = determineQueueSize();

			initialiseText += SEP_PATTERN_1211 + modInstContName + "__Initialise() {" + LF + LF +

					"   unsigned char buffer[255];" + LF + "   int size;" + LF + LF + "   Create_Message_Queue_Status_Type CMQ_Status;" + LF + LF +

					"   /* Setup the Module Instance Context */" + LF + "   " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext = &" + compInst.getName() + "_" + moduleInstance.getName() + "__private_context;" + LF + "   " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleInstanceID = " + moduleInstance.getComponentImplementation().getName().toUpperCase() + "_" + moduleInstance.getName().toUpperCase() + "_ID;" + LF + "   " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->componentInstanceID = CI_" + compInst.getName().toUpperCase() + "_ID;" + LF + "   " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState = ECOA__module_states_type_IDLE;" + LF + LF +

					"   /* Create the Module Instance Message Queue */" + LF + "   Create_Message_Queue(" + queueSize + "," + LF + "       sizeof(ILI_Message)," + LF + "       &" + compInst.getName() + "_" + moduleInstance.getName() + "__QueueID," + LF + "       &CMQ_Status);" + LF + LF;

			for (SM_RequestSentOp requestSentOp : moduleInstance.getImplementation().getModuleType().getRequestSentOps()) {
				if (requestSentOp.getIsSynchronous()) {
					// Create a message queue to block on if the module has at
					// least one sync request sent operation.
					initialiseText += "   /* Create a message queue for synchronous requests */" + LF + "   Create_Message_Queue(1," + LF + "       sizeof(ILI_Message)," + LF + "       &" + compInst.getName() + "_" + moduleInstance.getName() + "__SyncQueueID," + LF + "       &CMQ_Status);" + LF + LF;
					break;
				}
			}

			initialiseText += "   /* Initialise the client lookup list */" + LF + "   int i;" + LF + "   for (i = 0; i < " + modInstContName + "_Client_Lookup_MAXSIZE; i++)" + LF + "   {" + LF + "      " + modInstContName + "_clientLookupList[i].ID = -1;" + LF + "      " + modInstContName + "_clientLookupList[i].clientInfo.type = 0;" + LF + "      " + modInstContName + "_clientLookupList[i].clientInfo.ID = 0;" + LF + "      " + modInstContName + "_clientLookupList[i].clientInfo.serviceUID = 0;" + LF + "      " + modInstContName + "_clientLookupList[i].clientInfo.localSeqNum = 0;" + LF + "      " + modInstContName + "_clientLookupList[i].clientInfo.globalSeqNum = 0;" + LF + "   }" + LF;

			// Initialise the FIFO size list
			initialiseText += "   /* Initialise the FIFO size list */" + LF;

			initialiseText += "   for (i=0;i<" + compInst.getName() + "_" + moduleInstance.getName() + "_ILI_ID_SIZE; i++)" + LF + "   {" + LF + "      " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList[i].currentSize = 0;" + LF + "   }" + LF + LF;

			int notificationSize = deployedModInst.getProtectionDomain().getLogicalComputingNode().getLogicalComputingPlatform().getNotificationMaxNumber();

			// Add a case statement for each message which can be received by
			// this module instance.
			for (ILIMessage iliMessage : modInstILI.getILIMessageList()) {
				if (iliMessage instanceof LifecycleILIMessage) {
					// Only generate the notification if supervision module
					if (((LifecycleILIMessage) iliMessage).getMessageType() == ILIMessageType.LIFECYCLE_NOTIFICATION) {
						if (isSupervisor) {
							// -1 for supervision module!
							int lifecyclenotificationSize = ((compInst.getImplementation().getModuleInstances().size() - 1) + compInst.getImplementation().getTriggerInstances().size() + compInst.getImplementation().getDynamicTriggerInstances().size());
							initialiseText += "   " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList[" + iliMessage.getMessageID() + "].maxSize = " + lifecyclenotificationSize + ";" + LF;
						}
					}
					// Always generate other lifecycle ops (INITIALIZE, START,
					// STOP, REINITIALIZE)
					else {
						initialiseText += "   " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList[" + iliMessage.getMessageID() + "].maxSize = 1;" + LF;
					}
				} else if (iliMessage instanceof EventILIMessage) {
					SM_ReceiverModuleInstance receiver = ((EventILIMessage) iliMessage).getEventLink().getReceiver(moduleInstance);
					if (receiver != null) {
						initialiseText += "   " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList[" + iliMessage.getMessageID() + "].maxSize = " + receiver.getFifoSize() + ";" + LF;
					}
				} else if (iliMessage instanceof RequestILIMessage) {
					SM_ServerModuleInstance server = (SM_ServerModuleInstance) ((RequestILIMessage) iliMessage).getRequestLink().getServer();
					if (server.getServerInst() == moduleInstance) {
						initialiseText += "   " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList[" + iliMessage.getMessageID() + "].maxSize = " + server.getFifoSize() + ";" + LF;
					}
				} else if (iliMessage instanceof ResponseILIMessage) {
					for (SM_ClientInterface clientInterface : ((ResponseILIMessage) iliMessage).getRequestLink().getClients()) {
						if (clientInterface.getClientInst() == moduleInstance) {
							// Only generate if async (for response)
							if (!((SM_RequestSentOp) clientInterface.getClientOp()).getIsSynchronous()) {
								initialiseText += "   " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList[" + iliMessage.getMessageID() + "].maxSize = " + ((SM_RequestSentOp) clientInterface.getClientOp()).getMaxConcurrentRequests() + ";" + LF;
							}
						}
					}
				} else if (iliMessage instanceof DataILIMessage) {
					SM_ReaderModuleInstance reader = ((DataILIMessage) iliMessage).getDataLink().getReader(moduleInstance);
					if (reader != null) {
						// Only generate if notifying
						if (((SM_DataReadOp) reader.getReaderOp()).getIsNotifying()) {
							initialiseText += "   " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList[" + iliMessage.getMessageID() + "].maxSize = " + reader.getFifoSize() + ";" + LF;
						}
					}
				}
				// Only receive these if supervision module
				else if (iliMessage instanceof ServiceAvailNotificationILIMessage && isSupervisor) {
					initialiseText += "   " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList[" + iliMessage.getMessageID() + "].maxSize = " + notificationSize + ";" + LF;
				} else if (iliMessage instanceof ServiceProviderNotificationILIMessage && isSupervisor) {
					initialiseText += "   " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList[" + iliMessage.getMessageID() + "].maxSize = " + notificationSize + ";" + LF;
				} else if (iliMessage instanceof ErrorNotificationILIMessage && isSupervisor) {
					initialiseText += "   " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList[" + iliMessage.getMessageID() + "].maxSize = " + notificationSize + ";" + LF;
				} else if (iliMessage instanceof FaultNotificationILIMessage && isSupervisor) {
					initialiseText += "   " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList[" + iliMessage.getMessageID() + "].maxSize = " + notificationSize + ";" + LF;
				}
			}

			// Create fifoSizeListSemaphore
			initialiseText += "   /* Create a semaphore for managing access to the fifoSizeList */" + LF + "   Create_Semaphore_Status_Type Create_Semaphore_Status;" + LF + "   Create_Semaphore(1," + LF + "      1," + LF + "      Queuing_Discipline_FIFO," + LF + "      &" + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeListSemaphore," + LF + "      &Create_Semaphore_Status);" + LF + LF +

					"   if (Create_Semaphore_Status != Create_Semaphore_OK)" + LF + "   {" + LF + "      printf(\"ERROR creating fifoSizeList access semaphore\\n\");" + LF + "   }" + LF + LF;

			// automatically initialise the module if it's a supervision module.
			if (moduleInstance.getImplementation().getModuleType().getIsSupervisor()) {
				initialiseText += "   ECOA__timestamp timestamp;" + LF + "   /* Timestamp point */" + LF + "   ECOA_setTimestamp(&timestamp);" + LF + LF +

						"   /* This is a supervision module so initialise it */" + LF + "   " + modInstContName + "__INITIALIZE_received(&timestamp);" + LF + LF;
			}

			includeList.add("ECOA_file_handler");
			// Zero the warm-start context
			initialiseText += modInstContName + "__Zero_Warm_Context();" + LF + LF;

			// Open any PINFO files
			// Private PINFO
			for (SM_PinfoValue pinfo : moduleInstance.getPrivatePinfoValues()) {
				String filePtr = modInstContName + "_fp_" + pinfo.getName();
				String fileName = "DeployedPinfo/" + compInst.getName() + "/" + pinfo.getPinfoFile().getName();
				String currentSizeName = pinfo.getPinfoFile().getName().replaceAll("\\.", "_").toUpperCase() + "_CURRENT_SIZE";

				if (pinfo.getModuleTypePinfo().isWriteable()) {
					initialiseText += "   " + filePtr + " = fopen(\"" + fileName + "\", \"rb+\");" + LF;
				} else {
					initialiseText += "   " + filePtr + " = fopen(\"" + fileName + "\", \"rb\");" + LF;
				}

				initialiseText += "   if (" + filePtr + " == NULL)" + LF + "   {" + LF + "      printf(\"FAILED TO OPEN PINFO FILE: " + fileName + "\\n\");" + LF + "   }" + LF + "   else" + LF + "   {" + LF + "      // Set the current file size" + LF + "      fseek(" + filePtr + ", 0, SEEK_END);" + LF + "      " + currentSizeName + " = ftell(" + filePtr + ");" + LF + "      fseek(" + filePtr + ", 0, SEEK_SET);" + LF + "   }" + LF;

			}
			// Public PINFO
			for (SM_PinfoValue pinfo : moduleInstance.getPublicPinfoValues()) {
				SM_ComponentInstanceProperty compInstPinfo = compInst.getPropertyByName(pinfo.getRelatedCompTypeProperty().getName());

				// Get the value (either a component instance value or reference
				// to an assembly value.
				String fileName = "DeployedPinfo/" + compInstPinfo.getValue();

				String filePtr = modInstContName + "_fp_" + pinfo.getName();

				String currentSizeName = compInstPinfo.getValue().replaceAll("\\.", "_").toUpperCase() + "_CURRENT_SIZE";

				initialiseText += "   " + filePtr + " = fopen(\"" + fileName + "\", \"rb\");" + LF + "   if (" + filePtr + " == NULL)" + LF + "   {" + LF + "      printf(\"FAILED TO OPEN PINFO FILE: " + fileName + "\\n\");" + LF + "   }" + LF + "   else" + LF + "   {" + LF + "      // Set the current file size" + LF + "      fseek(" + filePtr + ", 0, SEEK_END);" + LF + "      " + currentSizeName + " = ftell(" + filePtr + ");" + LF + "      fseek(" + filePtr + ", 0, SEEK_SET);" + LF + "   }" + LF;
			}

			initialiseText += "}";
		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);

	}

	public void writeIsQueueFull() {
		String isQueueFullString = "ECOA__boolean8 " + compInst.getName() + "_" + moduleInstance.getName() + "_isQueueFull(ECOA__uint32 iliID)" + LF + "{" + LF + "   if (" + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList[iliID].currentSize >= " + compInst.getName() + "_" + moduleInstance.getName() + "_FIFOSizeList[iliID].maxSize)" + LF + "   {" + LF + "      return ECOA__TRUE;" + LF + "   }" + LF + "   else" + LF + "   {" + LF + "      return ECOA__FALSE;" + LF + "   }" + LF + "}" + LF;

		// Replace the #IS_QUEUE_FULL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#IS_QUEUE_FULL#", isQueueFullString);
	}

	public void WriteLifecycleOps() {
		String lifecycleOpsText = "";

		// INITIALIZE operation
		lifecycleOpsText += "ECOA__return_status " + modInstContName + "__INITIALIZE_received(ECOA__timestamp *timestamp)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "   // Setup the ILI message and call queue message" + LF + "   ILI_Message message;" + LF +

					"   /* Set the messageID & timestamp */" + LF + "   message.messageID = " + iliName + "_1;" + LF + "   message.timestamp = *timestamp;" + LF + "   message.messageDataPointer = 0;" + LF + LF +

					"   return " + modInstContName + "__Queue_Message(&message);" + LF + "}" + LF + LF;
		}

		// START operation
		lifecycleOpsText += "ECOA__return_status " + modInstContName + "__START_received(ECOA__timestamp *timestamp)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "   if (" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState == ECOA__module_states_type_READY)" + LF + "   {" + LF + "      // Setup the ILI message and call queue message" + LF + "      ILI_Message message;" + LF +

					"      /* Set the messageID & timestamp */" + LF + "      message.messageID = " + iliName + "_2;" + LF + "      message.timestamp = *timestamp;" + LF + "      message.messageDataPointer = 0;" + LF + LF +

					"      return " + modInstContName + "__Queue_Message(&message);" + LF + "   }" + LF + "   else" + LF + "   {" + LF + "      return ECOA__return_status_INVALID_TRANSITION;" + LF + "   }" + LF + "}" + LF + LF;
		}

		// STOP operation
		lifecycleOpsText += "ECOA__return_status " + modInstContName + "__STOP_received(ECOA__timestamp *timestamp)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "   if (" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState == ECOA__module_states_type_RUNNING)" + LF + "   {" + LF + "      // Setup the ILI message and call queue message" + LF + "      ILI_Message message;" + LF +

					"      /* Set the messageID & timestamp */" + LF + "      message.messageID = " + iliName + "_3;" + LF + "      message.timestamp = *timestamp;" + LF + "      message.messageDataPointer = 0;" + LF + LF +

					"      return " + modInstContName + "__Queue_Message(&message);" + LF + "   }" + LF + "   else" + LF + "   {" + LF + "      return ECOA__return_status_INVALID_TRANSITION;" + LF + "   }" + LF + "}" + LF + LF;
		}

		// SHUTDOWN operation
		lifecycleOpsText += "ECOA__return_status " + modInstContName + "__SHUTDOWN_received(ECOA__timestamp *timestamp)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "   if (" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState == ECOA__module_states_type_READY ||" + LF + "       " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState == ECOA__module_states_type_RUNNING)" + LF + "   {" + LF + "      // Setup the ILI message and call queue message" + LF + "      ILI_Message message;" + LF +

					"      /* Set the messageID & timestamp */" + LF + "      message.messageID = " + iliName + "_4;" + LF + "      message.timestamp = *timestamp;" + LF + "      message.messageDataPointer = 0;" + LF + LF +

					"      return " + modInstContName + "__Queue_Message(&message);" + LF + "   }" + LF + "   else" + LF + "   {" + LF + "      return ECOA__return_status_INVALID_TRANSITION;" + LF + "   }" + LF + "}" + LF + LF;
		}

		// Replace the #LIFECYCLE_OPS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#LIFECYCLE_OPS#", lifecycleOpsText);
	}

	public void writeModuleQueueIDDecl() {
		String moduleQueueIDDecl = "static Message_Queue_ID_Type " + compInst.getName() + "_" + moduleInstance.getName() + "__QueueID;" + LF + LF +

				"static ECOA__boolean8 " + compInst.getName() + "_" + moduleInstance.getName() + "_ModuleDead = ECOA__FALSE;" + LF + LF +

				"static int " + compInst.getName() + "_" + moduleInstance.getName() + "_CurrentQueueSize = 0;" + LF;

		// Replace the #MOD_QUEUE_ID_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#MOD_QUEUE_ID_DECL#", moduleQueueIDDecl);
	}

	public void writePINFO() {
		String pinfoText = "";

		for (SM_PinfoValue pinfo : moduleInstance.getPrivatePinfoValues()) {
			pinfoText = writeReadPinfo(pinfoText, pinfo);

			pinfoText = writeSeekPinfo(pinfoText, pinfo);

			if (pinfo.getModuleTypePinfo().isWriteable()) {
				pinfoText = writeWritePinfo(pinfoText, pinfo);
			}
		}
		for (SM_PinfoValue pinfo : moduleInstance.getPublicPinfoValues()) {
			pinfoText = writeReadPinfo(pinfoText, pinfo);

			pinfoText = writeSeekPinfo(pinfoText, pinfo);

			if (pinfo.getModuleTypePinfo().isWriteable()) {
				pinfoText = writeWritePinfo(pinfoText, pinfo);
			}
		}

		// Replace the #PINFO# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PINFO#", pinfoText);

	}

	public void writePinfoFileDecls() {
		String pinfoDecls = "// PINFO File Declarations" + LF;

		// Open any PINFO files
		// Private
		for (SM_PinfoValue pinfo : moduleInstance.getPrivatePinfoValues()) {
			pinfoDecls += "FILE *" + modInstContName + "_fp_" + pinfo.getName() + ";" + LF + "ECOA__uint32 " + modInstContName + "_fp_" + pinfo.getName() + "_CurrentIndex = 0;" + LF;

			// If writeable, define the max capacity.
			if (pinfo.getModuleTypePinfo().isWriteable()) {
				pinfoDecls += "ECOA__uint32 " + modInstContName + "_fp_" + pinfo.getName() + "_MaxCapacity = " + pinfo.getModuleTypePinfo().getCapacity() + ";" + LF;
			}
		}
		// Public
		for (SM_PinfoValue pinfo : moduleInstance.getPublicPinfoValues()) {
			pinfoDecls += "FILE *" + modInstContName + "_fp_" + pinfo.getName() + ";" + LF + "ECOA__uint32 " + modInstContName + "_fp_" + pinfo.getName() + "_CurrentIndex = 0;" + LF;
		}

		// Replace the #PINFO_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PINFO_DECL#", pinfoDecls);
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + modInstContName + ".h */" + LF;
		} else {
			preambleText += "/* File " + modInstContName + ".c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writePrivateContext() {
		String privateContextDecl = "/* Define a structure to hold the module and private context (so we can associate the two easily) */" + LF + "typedef struct {" + LF + "   /* Padding added to align properly */" + LF + "   ECOA__uint32 padding;" + LF + "   private_context *privateContext;" + LF + "   " + moduleInstance.getImplementation().getName() + "__context " + moduleInstance.getName() + "_context;" + LF + "} " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts_type;" + LF + LF +

				"/* Create a variable to hold the contexts */" + LF + "static " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts_type " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts;" + LF + LF +

				"/* Declare the private context */" + LF + "static private_context " + compInst.getName() + "_" + moduleInstance.getName() + "__private_context;" + LF + LF;

		// Replace the #PRIVATE_CONTEXT_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PRIVATE_CONTEXT_DECL#", privateContextDecl);

	}

	public void writeProcessQueue() {

		String processQueueString = "";
		if (isHeader) {
			processQueueString = SEP_PATTERN_1211 + modInstContName + "__ProcessQueue();" + LF;
		} else {
			processQueueString = SEP_PATTERN_1211 + modInstContName + "__ProcessQueue() {" + LF + LF +

					"   ECOA__timestamp timestamp;" + LF + "   ECOA__module_states_type previousState;" + LF + "   Receive_Message_Queue_Status_Type RMQ_Status;" + LF + LF +

					"   unsigned char buffer[255];" + LF + "   int size;" + LF + LF +

					"   /* Main queue processing loop */" + LF + "   while (1) {" + LF + LF +

					"      Receive_Message_Queue(" + compInst.getName() + "_" + moduleInstance.getName() + "__QueueID," + LF + "         (void *)&queueEntry," + LF + "         sizeof(ILI_Message)," + LF + "         &RMQ_Status);" + LF + LF +

					"      /* Decrement the current queue size */" + LF + "      " + modInstContName + "__Decrement_FIFO(queueEntry.messageID);" + LF + LF +

					"      /* If \"dead\", decrement the currentqueuesize */" + LF + "      if (" + compInst.getName() + "_" + moduleInstance.getName() + "_ModuleDead)" + LF + "      {" + LF + "         if (" + compInst.getName() + "_" + moduleInstance.getName() + "_CurrentQueueSize == 0)" + LF + "         {" + LF + "            " + compInst.getName() + "_" + moduleInstance.getName() + "_ModuleDead = ECOA__FALSE;" + LF + "         }" + LF + "         else" + LF + "         {" + LF + "            " + compInst.getName() + "_" + moduleInstance.getName() + "_CurrentQueueSize--;" + LF + "         }" + LF + "      }" + LF + LF +

					"      if (RMQ_Status == Receive_Message_Queue_OK)" + LF + "      {" + LF + "         switch (queueEntry.messageID) {" + LF + LF;

			// Add a case statement for each message which can be received by
			// this module instance.
			for (ILIMessage iliMessage : modInstILI.getILIMessageList()) {
				// Add the cases for module state messages (message ID's 1-5)
				if (iliMessage instanceof LifecycleILIMessage) {
					LifecycleILIMessage lifecycleMessage = ((LifecycleILIMessage) iliMessage);
					if (lifecycleMessage.getMessageType() == ILIMessageType.INITIALIZE_MODULE) {
						processQueueString += generateINITIALIZEHandler(iliMessage);
					} else if (lifecycleMessage.getMessageType() == ILIMessageType.START_MODULE) {
						processQueueString += generateSTARTHandler(iliMessage);
					} else if (lifecycleMessage.getMessageType() == ILIMessageType.STOP_MODULE) {
						processQueueString += generateSTOPHandler(iliMessage);
					} else if (lifecycleMessage.getMessageType() == ILIMessageType.SHUTDOWN_MODULE) {
						processQueueString += generateSHUTDOWNHandler(iliMessage);
					} else if (lifecycleMessage.getMessageType() == ILIMessageType.LIFECYCLE_NOTIFICATION && isSupervisor) {
						// Only need to generate a handler for the lifecycle
						// notification for supervisor instances.
						processQueueString += generateLIFECYCLE_NOTIFICATIONHandler(iliMessage, subordinateModuleInstances, subordinateTriggerInstances, subordinateDynamicTriggerInstances);
					}
				} else if (iliMessage instanceof EventILIMessage) {
					EventILIMessage eventMessage = ((EventILIMessage) iliMessage);
					for (SM_ReceiverInterface receiver : eventMessage.getEventLink().getReceivers()) {
						if (receiver.getReceiverInst() == moduleInstance) {
							String iliNameAndID = iliName + "_" + iliMessage.getMessageID();

							processQueueString += "            case " + iliName + "_" + iliMessage.getMessageID() + ":" + LF + "            {" + LF + "               /* Check the module is still in the \"RUNNING\" state */" + LF + "               if (" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState == ECOA__module_states_type_RUNNING)" + LF + "               {" + LF;

							// Create a pointer to the params (if there is any)
							if (iliMessage.getParams().size() > 0) {
								processQueueString += "               " + iliNameAndID + "_params *" + iliNameAndID + "_params_ptr = (" + iliNameAndID + "_params*)queueEntry.messageDataPointer;" + LF;
							}

							processQueueString += "                  /* Set the operation timestamp */" + LF + "                  " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context.operation_timestamp = queueEntry.timestamp;" + LF;

							if (moduleInstance.getImplementation().isInstrument()) {
								processQueueString += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__" + receiver.getReceiverOp().getName() + "__received()\\n\");" + LF + LF;
							}

							processQueueString += "                  " + moduleInstance.getImplementation().getName() + "__" + receiver.getReceiverOp().getName() + "__received(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context";

							for (SM_OperationParameter param : iliMessage.getParams()) {
								if (param.getType().isSimple()) {
									processQueueString += ", " + iliNameAndID + "_params_ptr->" + param.getName();
								} else {
									processQueueString += ", &" + iliNameAndID + "_params_ptr->" + param.getName();
								}
							}

							processQueueString += ");" + LF + "               }" + LF + "               break;" + LF + "            }" + LF + LF;
						}
					}
				} else if (iliMessage instanceof RequestILIMessage) {
					RequestILIMessage requestMessage = ((RequestILIMessage) iliMessage);
					if (requestMessage.getRequestLink().getServer().getServerInst() == moduleInstance) {
						String iliNameAndID = iliName + "_" + iliMessage.getMessageID();

						SM_RequestReceivedOp requestOp = (SM_RequestReceivedOp) requestMessage.getRequestLink().getServer().getServerOp();

						processQueueString += "            case " + iliName + "_" + iliMessage.getMessageID() + ":" + LF + "            {" + LF + "               /* Check the module is still in the \"RUNNING\" state */" + LF + "               if (" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState == ECOA__module_states_type_RUNNING)" + LF + "               {" + LF;

						// Create a pointer to the input params (if there is
						// any)
						if (requestOp.getInputs().size() > 0) {
							processQueueString += "                  " + iliNameAndID + "_params *" + iliNameAndID + "_params_ptr = (" + iliNameAndID + "_params*)queueEntry.messageDataPointer;" + LF;
						}

						processQueueString += "                  /* Set the operation timestamp */" + LF + "                  " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context.operation_timestamp = queueEntry.timestamp;" + LF;

						if (moduleInstance.getImplementation().isInstrument()) {
							processQueueString += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__" + requestMessage.getRequestLink().getServer().getServerOp().getName() + "__request_received()\\n\");" + LF + LF;
						}

						processQueueString += "                  " + moduleInstance.getImplementation().getName() + "__" + requestMessage.getRequestLink().getServer().getServerOp().getName() + "__request_received(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context, queueEntry.sequenceNumber";

						// Add inputs from the ILI message
						for (SM_OperationParameter param : requestOp.getInputs()) {
							if (param.getType().isSimple()) {
								processQueueString += ", ";
							} else {
								processQueueString += ", &";
							}
							processQueueString += iliNameAndID + "_params_ptr->" + param.getName();
						}

						// Close the call to module operation
						processQueueString += ");" + LF + LF + "               }" + LF + "               break;" + LF + "            }" + LF + LF;
					}
				} else if (iliMessage instanceof ResponseILIMessage) {
					// Only generate if async...
					ResponseILIMessage responseMessage = ((ResponseILIMessage) iliMessage);
					for (SM_ClientInterface clientInterface : responseMessage.getRequestLink().getClients()) {
						if (clientInterface.getClientInst() == moduleInstance) {
							SM_RequestSentOp requestOp = (SM_RequestSentOp) clientInterface.getClientOp();

							if (!requestOp.getIsSynchronous()) {
								String iliNameAndID = iliName + "_" + iliMessage.getMessageID();

								processQueueString += "            case " + iliName + "_" + iliMessage.getMessageID() + ":" + LF + "            {" + LF + "               /* Check the module is still in the \"RUNNING\" state */" + LF + "               if (" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState == ECOA__module_states_type_RUNNING)" + LF + "               {" + LF + "                  " + iliNameAndID + "_params *" + iliNameAndID + "_params_ptr = (" + iliNameAndID + "_params*)queueEntry.messageDataPointer;" + LF + LF +

										"                  /* Set the operation timestamp */" + LF + "                  " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context.operation_timestamp = queueEntry.timestamp;" + LF;

								if (moduleInstance.getImplementation().isInstrument()) {
									processQueueString += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__" + clientInterface.getClientOp().getName() + "__response_received()\\n\");" + LF + LF;
								}

								processQueueString += "                  " + moduleInstance.getImplementation().getName() + "__" + clientInterface.getClientOp().getName() + "__response_received(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context, queueEntry.sequenceNumber";

								for (SM_OperationParameter param : iliMessage.getParams()) {
									if (param.getType().isSimple()) {
										processQueueString += ", " + iliNameAndID + "_params_ptr->" + param.getName();
									} else {
										processQueueString += ", &" + iliNameAndID + "_params_ptr->" + param.getName();
									}
								}

								processQueueString += ");" + LF + "               }" + LF + "               break;" + LF + "            }" + LF + LF;
							}
						}
					}
				} else if (iliMessage instanceof DataILIMessage) {
					String internal = "";
					if (moduleInstance.getImplementation().isInstrument()) {
						internal += "internal_";
					}

					DataILIMessage dataMessage = ((DataILIMessage) iliMessage);
					for (SM_ReaderModuleInstance reader : dataMessage.getDataLink().getLocalReaders()) {
						if (reader.getReaderInst() == moduleInstance) {
							SM_DataReadOp dataReadOp = (SM_DataReadOp) reader.getReaderOp();

							// TODO - should the data really be passed through -
							// as this is not necessarily the same version - it
							// could have been updated
							// since notification being queued!
							processQueueString += "            case " + iliName + "_" + iliMessage.getMessageID() + ":" + LF + "            {" + LF + "               /* Check the module is still in the \"RUNNING\" state */" + LF + "               if (" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState == ECOA__module_states_type_RUNNING)" + LF + "               {" + LF +

									"                  " + moduleInstance.getImplementation().getName() + "_container__" + dataReadOp.getName() + "_handle data_handle;" + LF + "                  ECOA__return_status status;" + LF + LF +

									"                  /* Get read access */" + LF + "                  status = " + moduleInstance.getImplementation().getName() + "_container__" + dataReadOp.getName() + "__get_" + internal + "read_access(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context, &data_handle);" + LF + LF +

									"                  /* Call the updated notification */" + LF;

							if (moduleInstance.getImplementation().isInstrument()) {
								processQueueString += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__" + dataReadOp.getName() + "__updated()\\n\");" + LF + LF;
							}

							processQueueString += "                  " + moduleInstance.getImplementation().getName() + "__" + dataReadOp.getName() + "__updated(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context, status, &data_handle);" + LF + LF +

									"                  /* Release read access */" + LF + "                  " + moduleInstance.getImplementation().getName() + "_container__" + dataReadOp.getName() + "__release_" + internal + "read_access(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context, &data_handle);" + LF + LF +

									"               }" + LF + "               break;" + LF + "            }" + LF + LF;

						}
					}
				} else if (iliMessage instanceof ServiceAvailNotificationILIMessage) {
					// TODO - should we be handling the module dead flag on this
					// message? Supervision only message - needs thinking
					// about...

					// Only generate this for supervisor modules.
					if (moduleInstance.getModuleType().getIsSupervisor()) {
						String iliNameAndID = iliName + "_" + iliMessage.getMessageID();

						ServiceAvailNotificationILIMessage serviceAvailNotificationMessage = (ServiceAvailNotificationILIMessage) iliMessage;

						String serviceInstanceID = moduleInstance.getImplementation().getName() + "_container__reference_id__" + serviceAvailNotificationMessage.getServiceInstance().getName();

						processQueueString += "            case " + iliNameAndID + ":" + LF + "            {" + LF + "               " + iliNameAndID + "_params *" + iliNameAndID + "_params_ptr = (" + iliNameAndID + "_params*)queueEntry.messageDataPointer;" + LF + LF +

								"               /* Call the service availability changed notification */ " + LF;

						if (moduleInstance.getImplementation().isInstrument()) {
							processQueueString += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__service_availability_changed()\\n\");" + LF + LF;
						}

						processQueueString += "               " + moduleInstance.getImplementation().getName() + "__service_availability_changed(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context, " + serviceInstanceID;

						for (SM_OperationParameter param : iliMessage.getParams()) {
							processQueueString += ", " + iliNameAndID + "_params_ptr->" + param.getName();
						}

						processQueueString += ");" + LF + "               break;" + LF + "            }" + LF + LF;
					}
				} else if (iliMessage instanceof ServiceProviderNotificationILIMessage) {
					// TODO - should we be handling the module dead flag on this
					// message? Supervision only message - needs thinking
					// about...

					// Only generate this for supervisor modules.
					if (moduleInstance.getModuleType().getIsSupervisor()) {
						ServiceProviderNotificationILIMessage serviceProviderNotificationMessage = (ServiceProviderNotificationILIMessage) iliMessage;

						String serviceInstanceID = moduleInstance.getImplementation().getName() + "_container__reference_id__" + serviceProviderNotificationMessage.getServiceInstance().getName();

						processQueueString += "            case " + iliName + "_" + iliMessage.getMessageID() + ":" + LF + "               /* Call the service provider changed notification */ " + LF;

						if (moduleInstance.getImplementation().isInstrument()) {
							processQueueString += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__service_provider_changed()\\n\");" + LF + LF;
						}

						processQueueString += "               " + moduleInstance.getImplementation().getName() + "__service_provider_changed(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context, " + serviceInstanceID + ");" + LF + "               break;" + LF + LF;
					}
				} else if (iliMessage instanceof ErrorNotificationILIMessage) {
					// TODO - should we be handling the module dead flag on this
					// message? Supervision only message - needs thinking
					// about...

					// Only generate this for supervisor modules.
					if (moduleInstance.getModuleType().getIsSupervisor()) {
						String iliNameAndID = iliName + "_" + iliMessage.getMessageID();

						ErrorNotificationILIMessage errorNotificationMessage = (ErrorNotificationILIMessage) iliMessage;

						includeList.add(compInst.getName() + "_" + errorNotificationMessage.getModuleInstance().getName() + "_Controller");

						processQueueString += "            case " + iliNameAndID + ":" + LF + "            {" + LF + "               " + iliNameAndID + "_params *" + iliNameAndID + "_params_ptr = (" + iliNameAndID + "_params*)queueEntry.messageDataPointer;" + LF + LF +

								"               if (" + iliNameAndID + "_params_ptr->Module_Error == ECOA__module_error_type_FATAL_ERROR)" + LF + "               {" + LF + "                  /* Reinitialize the module - clear the queue, create a new thread */" + LF + "                  " + compInst.getName() + "_" + errorNotificationMessage.getModuleInstance().getName() + "_Controller__Reinitialise(ECOA__TRUE);" + LF + LF + "               }" + LF + LF +

								"               /* Call the error notification */ " + LF;

						if (moduleInstance.getImplementation().isInstrument()) {
							processQueueString += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__error_notification__" + errorNotificationMessage.getModuleInstance().getName() + "()\\n\");" + LF + LF;
						}

						processQueueString += "               " + moduleInstance.getImplementation().getName() + "__error_notification__" + errorNotificationMessage.getModuleInstance().getName() + "(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context";

						for (SM_OperationParameter param : iliMessage.getParams()) {
							processQueueString += ", " + iliNameAndID + "_params_ptr->" + param.getName();
						}

						processQueueString += ");" + LF + "               break;" + LF + "            }" + LF + LF;
					}
				} else if (iliMessage instanceof FaultNotificationILIMessage) {

					// Only generate this for fault handler modules.
					if (moduleInstance.getModuleType().getIsFaultHandler()) {
						String iliNameAndID = iliName + "_" + iliMessage.getMessageID();

						processQueueString += "            case " + iliNameAndID + ":" + LF + "            {" + LF + "               " + iliNameAndID + "_params *" + iliNameAndID + "_params_ptr = (" + iliNameAndID + "_params*)queueEntry.messageDataPointer;" + LF + LF +

								"               /* Call the error notification */ " + LF;

						if (moduleInstance.getImplementation().isInstrument()) {
							processQueueString += SEP_PATTERN_A + moduleInstance.getImplementation().getName() + "__error_notification()\\n\");" + LF + LF;
						}

						processQueueString += "               " + moduleInstance.getImplementation().getName() + "__error_notification(&" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts." + moduleInstance.getName() + "_context";

						for (SM_OperationParameter param : iliMessage.getParams()) {
							if (param.getType().isSimple()) {
								processQueueString += ", " + iliNameAndID + "_params_ptr->" + param.getName();
							} else {
								processQueueString += ", &" + iliNameAndID + "_params_ptr->" + param.getName();
							}
						}

						processQueueString += ");" + LF + "               break;" + LF + "            }" + LF + LF;
					}
				}
			}
			processQueueString += "         }" + LF + "      }" + LF + LF +

					"      if (queueEntry.messageDataPointer != 0)" + LF + "      {" + LF + "         free(queueEntry.messageDataPointer);" + LF + "      }" + LF + LF +

					"   }" + LF + "}" + LF;
		}

		// Replace the #PROCESS_QUEUE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PROCESS_QUEUE#", processQueueString);
	}

	public void writeQueueMessage() {
		String queueMessageText = "";

		if (isHeader) {
			queueMessageText += "ECOA__return_status " + modInstContName + "__Queue_Message(ILI_Message *iliMessage);" + LF;
		} else {
			queueMessageText += "ECOA__return_status " + modInstContName + "__Queue_Message(ILI_Message *iliMessage) {" + LF + LF +

					"   Send_Message_Queue_Status_Type smqStatus;" + LF + "   ECOA__return_status queueStatus = ECOA__return_status_OK;" + LF + "   unsigned char buffer[255];" + LF + "   int size;" + LF + LF +

					"   /* First, check if it's a lifecycle message */" + LF + "   if (iliMessage->messageID >= 1 && iliMessage->messageID <= 4)" + "   {" + LF + "      if (" + compInst.getName() + "_" + moduleInstance.getName() + "_PendingStateTransition == ECOA__TRUE)" + LF + "      {" + LF + "         size = sprintf((char*)buffer, \"Pending Module Lifecycle operation in queue (message being discarded!) - " + compInst.getName() + " " + moduleInstance.getName() + "\\n\");" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "         queueStatus = ECOA__return_status_PENDING_STATE_TRANSITION;" + LF + "      }" + LF + "      else" + LF + "      {" + LF + "         " + modInstContName + "__Increment_FIFO(iliMessage->messageID);" + LF + "         /* Set Pending State Transition flag */" + LF + "         " + compInst.getName() + "_" + moduleInstance.getName() + "_PendingStateTransition = ECOA__TRUE;" + LF + "         /* Queue lifecycle message */" + LF + "         Send_Message_Queue(" + compInst.getName() + "_" + moduleInstance.getName() + "__QueueID," + LF + "            iliMessage," + LF + "            sizeof(ILI_Message)," + LF
					+ "            &smqStatus);" + LF + "      }" + LF + "   }" + LF;
			if (isSupervisor) {
				queueMessageText += "   else if (iliMessage->messageID == 5)" + LF + "   {" + LF + "      if (" + compInst.getName() + "_" + moduleInstance.getName() + "_isQueueFull(iliMessage->messageID))" + LF + "      {" + LF + "         size = sprintf((char*)buffer, \"Queue Full for ILI_Message_%d in " + compInst.getName() + " " + moduleInstance.getName() + "\\n\", iliMessage->messageID);" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "         queueStatus = ECOA__return_status_OPERATION_NOT_AVAILABLE;" + LF + "      }" + LF + "      else" + LF + "      {" + LF + "         " + modInstContName + "__Increment_FIFO(iliMessage->messageID);" + LF + "         /* Queue this message */" + LF + "         Send_Message_Queue(" + compInst.getName() + "_" + moduleInstance.getName() + "__QueueID," + LF + "            iliMessage," + LF + "            sizeof(ILI_Message)," + LF + "            &smqStatus);" + LF + LF +

						"         if (smqStatus != Send_Message_Queue_OK)" + LF + "         {" + LF + "            printf(\"ERROR - Failed to queue message in " + modInstContName + "__Queue_Message\\n\");" + LF + "            queueStatus = ECOA__return_status_OPERATION_NOT_AVAILABLE;" + LF + "         }" + LF + "      }" + LF + "   }" + LF;
			}
			queueMessageText += "   /* Otherwise check the state to see if we need to queue the message */" + LF + "   else if (" + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState == ECOA__module_states_type_RUNNING)" + LF + "   {" + LF + "      if (" + compInst.getName() + "_" + moduleInstance.getName() + "_isQueueFull(iliMessage->messageID))" + LF + "      {" + LF + "         size = sprintf((char*)buffer, \"Queue Full for ILI_Message_%d in " + compInst.getName() + " " + moduleInstance.getName() + "\\n\", iliMessage->messageID);" + LF + "         ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "         queueStatus = ECOA__return_status_OPERATION_NOT_AVAILABLE;" + LF + "         if (iliMessage->messageDataPointer != 0)" + LF + "         {" + LF + "            free(iliMessage->messageDataPointer);" + LF + "         }" + LF + "      }" + LF + "      else" + LF + "      {" + LF + "         " + modInstContName + "__Increment_FIFO(iliMessage->messageID);" + LF + "         /* Queue this message */" + LF + "         Send_Message_Queue(" + compInst.getName() + "_" + moduleInstance.getName() + "__QueueID," + LF
					+ "            iliMessage," + LF + "            sizeof(ILI_Message)," + LF + "            &smqStatus);" + LF + LF +

					"         if (smqStatus != Send_Message_Queue_OK)" + LF + "         {" + LF + "            printf(\"ERROR - Failed to queue message in " + modInstContName + "__Queue_Message\\n\");" + LF + "            queueStatus = ECOA__return_status_OPERATION_NOT_AVAILABLE;" + LF + "            if (iliMessage->messageDataPointer != 0)" + LF + "            {" + LF + "               free(iliMessage->messageDataPointer);" + LF + "            }" + LF + "         }" + LF + "      }" + LF + "   }" + LF + "   else" + LF + "   {" + LF + "      /* Not in the running state - DO NOT QUEUE */" + LF + "      size = sprintf((char*)buffer, \"" + modInstContName + " - ILI_Message_%d not queued\\n\", iliMessage->messageID);" + LF + "      ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "      queueStatus = ECOA__return_status_OPERATION_NOT_AVAILABLE;" + LF + "      if (iliMessage->messageDataPointer != 0)" + LF + "      {" + LF + "         free(iliMessage->messageDataPointer);" + LF + "      }" + LF + "   }" + LF + LF +

					"   return queueStatus;" + LF + "}" + LF + LF;
		}

		// Replace the #QUEUE_MESSAGE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#QUEUE_MESSAGE#", queueMessageText);
	}

	private String writeReadPinfo(String pinfoText, SM_PinfoValue pinfo) {
		includeList.add("ECOA_file_handler");

		pinfoText += "ECOA__return_status " + modInstContName + "__read_" + pinfo.getName() + LF + "   (ECOA__byte *memory_address," + LF + "    ECOA__uint32 in_size," + LF + "    ECOA__uint32 *out_size)";

		if (isHeader) {
			pinfoText += ";" + LF;
		} else {
			String filePointerName = modInstContName + "_fp_" + pinfo.getName();

			pinfoText += LF + "{" + LF + "   return ECOA_read_file(" + filePointerName + ", memory_address, in_size, out_size, &" + filePointerName + "_CurrentIndex);" + LF + "}" + LF + LF;
		}
		return pinfoText;
	}

	public void writeReinitialise() {
		String reinitText = "";

		if (isHeader) {
			reinitText += SEP_PATTERN_1211 + modInstContName + "__Reinitialise(ECOA__boolean8 restartThread);" + LF;
		} else {
			SM_DeployedModInst depModInst = null;
			// Get the associated deployed module instance (as we need thread
			// priority)
			for (SM_DeployedModInst dmi : compInst.getDeployedModInsts()) {
				if (dmi.getModInstance() == moduleInstance) {
					depModInst = dmi;
					break;
				}
			}

			Integer priority = depModInst.getPriority();

			String modQueueThreadName = null;
			if (depModInst.getModInstance().getName().length() < 12) {
				modQueueThreadName = depModInst.getModInstance().getName() + "_MQ";
			} else {
				modQueueThreadName = depModInst.getModInstance().getName().substring(0, 12) + "_MQ";
			}

			reinitText += SEP_PATTERN_1211 + modInstContName + "__Reinitialise(ECOA__boolean8 restartThread)" + LF + "{" + LF + "   unsigned char buffer[255];" + LF + "   int size;" + LF + LF +

					"   /* Set the Module State IDLE */" + LF + "   " + compInst.getName() + "_" + moduleInstance.getName() + "_contexts.privateContext->moduleState = ECOA__module_states_type_IDLE;" + LF + LF +

					"   /* Set the pending state transition flag false */" + LF + "   " + compInst.getName() + "_" + moduleInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

					"   /* Set the module \"dead\" flag */" + LF + "   " + compInst.getName() + "_" + moduleInstance.getName() + "_ModuleDead = ECOA__TRUE;" + LF + LF +

					"   /* Determine how many items are currently on the queue (as we must \"clear\" the queue upto this point\") */" + LF + "   " + compInst.getName() + "_" + moduleInstance.getName() + "_CurrentQueueSize = Get_Queue_Size(" + compInst.getName() + "_" + moduleInstance.getName() + "__QueueID);" + LF + LF;

			// Reset any PINFO indexes.
			for (SM_PinfoValue pinfo : moduleInstance.getAllPinfos()) {
				reinitText += "   " + modInstContName + "_fp_" + pinfo.getName() + "_CurrentIndex = 0;" + LF;
			}

			reinitText += "   if (restartThread)" + LF + "   {" + LF + "      /* Restart the module thread */" + LF + "      " + protectionDomain.getName() + "_startThread(" + compInst.getName() + "_" + moduleInstance.getName() + "_Controller__ProcessQueue, " + priority.toString() + ", \"" + modQueueThreadName + "\", 0);" + LF + LF + "   }" + LF + "}" + LF + LF;

		}

		// Replace the #REINIT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#REINIT#", reinitText);
	}

	public void writeZeroWarmContext() {
		includeList.add("ECOA_file_handler");

		String reloadText = SEP_PATTERN_1211 + modInstContName + "__Zero_Warm_Context()";

		if (isHeader) {
			reloadText += ";" + LF;
		} else {
			reloadText += LF + "{" + LF + "   ECOA_write_zeroed_context(\"" + compInst.getName() + "_" + moduleInstance.getName() + "_warm_context.bin\", sizeof(" + moduleInstance.getImplementation().getName() + "_warm_start_context));" + LF + "}" + LF;
		}

		// Replace the #ZERO_WARM_CONTEXT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#ZERO_WARM_CONTEXT#", reloadText);
	}

	public void writeRequestReceives() {
		String requestRxText = "";

		for (SM_RequestReceivedOp rqRx : moduleInstance.getModuleType().getRequestReceivedOps()) {
			requestRxText += SEP_PATTERN_1211 + modInstContName + "__" + rqRx.getName() + "__request_received(ECOA__timestamp *timestamp, Client_Info_Type *clientInfo";

			for (SM_OperationParameter opParam : rqRx.getInputs()) {
				requestRxText += CLanguageSupport.writeConstParam(opParam);
			}

			if (isHeader) {
				includeList.add("Client_Info_Type");
				requestRxText += ");" + LF;
			} else {
				requestRxText += ")" + LF + "{" + LF + "   // Store the sequence number to client info mapping" + LF + "   " + modInstContName + "__Set_Client_Lookup(clientInfo);" + LF + LF +

						"   // Setup the ILI message and call queue message" + LF + "   ILI_Message message;" + LF;

				ILIMessage ili = null;
				for (SM_RequestLink requestLink : moduleInstance.getRequestLinksForServerOp(rqRx)) {
					ili = modInstILI.getILIForRequestLink(requestLink);

					requestRxText += "   /* Set the messageID & timestamp */" + LF + "   message.messageID = " + ili.getMessageID() + ";" + LF + "   message.timestamp = *timestamp;" + LF + "   message.sequenceNumber = clientInfo->localSeqNum;" + LF + LF +

							assignInputParamsToMessage(rqRx.getInputs(), ili) + "   " + modInstContName + "__Queue_Message(&message);" + LF + LF;
				}
				requestRxText += "}" + LF + LF;
			}
		}

		// Replace the #REQUEST_RECEIVES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#REQUEST_RECEIVES#", requestRxText);
	}

	public void writeResponseReceives() {
		String responseRxText = "";

		for (SM_RequestSentOp rqTx : moduleInstance.getModuleType().getRequestSentOps()) {
			responseRxText += SEP_PATTERN_1211 + modInstContName + "__" + rqTx.getName() + "__response_received(ECOA__timestamp *timestamp, ECOA__return_status *responseStatus, ECOA__uint32 seqNum";

			for (SM_OperationParameter opParam : rqTx.getOutputs()) {
				responseRxText += CLanguageSupport.writeParam(opParam);
			}

			if (isHeader) {
				responseRxText += ");" + LF;
			} else {
				responseRxText += ")" + LF + "{" + LF + "   // Setup the ILI message and call queue message" + LF + "   ILI_Message message;" + LF;

				if (rqTx.getTimeout() > 0) {
					String timerManName = protectionDomain.getName() + "_Timer_Event_Manager";
					includeList.add(timerManName);

					responseRxText += "   Timer_Manager_Error_Type dtStatus;" + LF + LF + "  // Need to delete the associated timer for this request operation" + LF + "  dtStatus = " + timerManName + "__Delete_Timer_ID(REQUEST_TIMEOUT, CI_" + compInst.getName().toUpperCase() + "_ID, " + moduleInstance.getComponentImplementation().getName().toUpperCase() + "_" + moduleInstance.getName().toUpperCase() + "_ID, " + compInst.getImplementation().getName().toUpperCase() + "_" + moduleInstance.getName().toUpperCase() + "_" + rqTx.getName().toUpperCase() + "_UID, seqNum);" + LF + LF +

							"  if (dtStatus == Timer_Manager_Error_Type_OK)" + LF + "  {" + LF;
				}

				ILIMessage ili = null;
				SM_RequestLink requestLink = moduleInstance.getRequestLinkForClientOp(rqTx);

				for (SM_ClientInterface clientInterface : requestLink.getClients()) {
					if (clientInterface.getClientInst() == moduleInstance && clientInterface.getClientOp() == rqTx) {
						ili = modInstILI.getILIForResponseLink(requestLink);

						responseRxText += "   /* Set the messageID & timestamp */" + LF + "   message.messageID = " + ili.getMessageID() + ";" + LF + "   message.timestamp = *timestamp;" + LF + LF +

								"   message.sequenceNumber = seqNum;" + LF + LF + assignParamsToResponseMessage(rqTx, ili);

						// If it was a sync call - unblock the sync message
						// queue, otherwise (async) queue to module queue.
						if (rqTx.getIsSynchronous()) {
							responseRxText += "   Send_Message_Queue_Status_Type smqStatus;" + LF +

									"   // Queue to sync message queue" + LF + "   Send_Message_Queue(" + compInst.getName() + "_" + moduleInstance.getName() + "__SyncQueueID," + LF + "      &message," + LF + "      sizeof(ILI_Message)," + LF + "      &smqStatus);" + LF;
						} else {
							responseRxText += "   " + modInstContName + "__Queue_Message(&message);" + LF + LF;
						}
					}

				}

				// Close if on dtStatus
				if (rqTx.getTimeout() > 0) {
					responseRxText += "  }" + LF + "  else" + LF + "  {" + LF + "     printf(\"INFO - GOT RESPONSE AFTER TIMEOUT - RESPONSE DISCARDED!!!\\n\");" + LF + "  }" + LF + LF;
				}

				responseRxText += "}" + LF + LF;
			}
		}

		// Replace the #RESPONSE_RECEIVES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#RESPONSE_RECEIVES#", responseRxText);
	}

	private String writeSeekPinfo(String pinfoText, SM_PinfoValue pinfo) {
		pinfoText += "ECOA__return_status " + modInstContName + "__seek_" + pinfo.getName() + LF + "   (ECOA__int32 offset," + LF + "    ECOA__seek_whence_type whence," + LF + "    ECOA__uint32 *new_position)";

		if (isHeader) {
			pinfoText += ";" + LF;
		} else {
			String filePointerName = modInstContName + "_fp_" + pinfo.getName();

			String currentSizeName = "";
			// Determine the current size name
			if (pinfo.getModuleTypePinfo().isPrivate()) {
				includeList.add(compInst.getName() + "_PINFO_Sizes");
				currentSizeName = pinfo.getPinfoFile().getName().replaceAll("\\.", "_").toUpperCase() + "_CURRENT_SIZE";
			} else {
				includeList.add(compInst.getProtectionDomain().getName() + "_PINFO_Sizes");
				SM_ComponentInstanceProperty compInstPinfo = compInst.getPropertyByName(pinfo.getRelatedCompTypeProperty().getName());

				currentSizeName = compInstPinfo.getValue().replaceAll("\\.", "_").toUpperCase() + "_CURRENT_SIZE";
			}

			pinfoText += LF + "{" + LF + "   ECOA__return_status status;" + LF + "   status = ECOA_seek_file(" + filePointerName + ", offset, whence, &" + filePointerName + "_CurrentIndex, &" + currentSizeName + ");" + LF + "   // Set the output new_position parameter" + LF + "   *new_position = " + filePointerName + "_CurrentIndex;" + LF + "   return status;" + LF + "}" + LF + LF;
		}
		return pinfoText;
	}

	public void writeSetClientLookup() {
		String setLookText = SEP_PATTERN_1211 + modInstContName + "__Set_Client_Lookup(Client_Info_Type *clientInfo)" + LF + "{" + LF + "   int i;" + LF + "   for (i = 0; i < " + modInstContName + "_Client_Lookup_MAXSIZE; i++)" + LF + "   {" + LF + "      if (" + modInstContName + "_clientLookupList[i].ID == -1)" + LF + "      {" + LF + "         /* Store the seqNum/clientInfo in this empty slot */" + LF + "         " + modInstContName + "_clientLookupList[i].ID = clientInfo->localSeqNum;" + LF + "         " + modInstContName + "_clientLookupList[i].clientInfo.type = clientInfo->type;" + LF + "         " + modInstContName + "_clientLookupList[i].clientInfo.ID = clientInfo->ID;" + LF + "         " + modInstContName + "_clientLookupList[i].clientInfo.serviceUID = clientInfo->serviceUID;" + LF + "         " + modInstContName + "_clientLookupList[i].clientInfo.localSeqNum = clientInfo->localSeqNum;" + LF + "         " + modInstContName + "_clientLookupList[i].clientInfo.globalSeqNum = clientInfo->globalSeqNum;" + LF + "         break;" + LF + "      }" + LF + "   }" + LF + "}" + LF;

		// Replace the #SET_LOOKUP# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SET_LOOKUP#", setLookText);
	}

	public void writeSupervisionNotificationFunction() {
		String superNotifyText = "";

		if (isHeader && isSupervisor) {
			superNotifyText += SEP_PATTERN_1211 + modInstContName + "__SupervisionNotification(ECOA__timestamp *timestamp);" + LF;
		}
		if (!isHeader && isSupervisor) {
			superNotifyText += SEP_PATTERN_1211 + modInstContName + "__SupervisionNotification(ECOA__timestamp *timestamp)" + LF + "{" + LF + "   /* TODO - this is a simplistic implementation for now, which only gets called when supervision module has been initialised */" + LF + "   /* Functionality could be extended going forward... */" + LF +

					"   /* Start supervision module on notification it has been initialised */" + LF + LF + "   " + modInstContName + "__START_received(timestamp);" + LF + "}" + LF;
		}

		// Replace the #SUPER_NOTIFY# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SUPER_NOTIFY#", superNotifyText);

	}

	public void writeSupervisionOps() {
		String superOpsText = "";

		// Only generate these operations if supervision.
		if (moduleInstance.getModuleType().getIsSupervisor()) {
			// Lifecycle notifications
			for (SM_Object nonSupervisionInst : moduleInstance.getComponentImplementation().getNonSupervisionModuleTriggerInstances()) {
				superOpsText += SEP_PATTERN_1211 + modInstContName + "__Lifecycle_Notification_" + nonSupervisionInst.getName() + "(ECOA__timestamp *timestamp, ECOA__module_states_type previousState, ECOA__module_states_type newState)";

				if (isHeader) {
					superOpsText += ";" + LF;
					includeList.add(moduleInstance.getImplementation().getName() + "_container");
				} else {
					includeList.add(iliName);

					superOpsText += LF + "{" + LF + "   // Setup the ILI message and call queue message" + LF + "   ILI_Message message;" + LF + LF +

							"   /* Set the messageID & timestamp */" + LF + "   message.messageID = " + iliName + "_5;" + LF + "   message.timestamp = *timestamp;" + LF + LF +

							"   /* Malloc memory for the input params */" + LF + "   message.messageDataPointer = malloc(sizeof(" + iliName + "_5_params));" + LF + "   if (message.messageDataPointer != 0)" + LF + "   {" + LF + "      ((" + iliName + "_5_params*)message.messageDataPointer)->previous_state = previousState;" + LF + "      ((" + iliName + "_5_params*)message.messageDataPointer)->new_state = newState;" + LF + "      ((" + iliName + "_5_params*)message.messageDataPointer)->moduleInstanceId = " + moduleInstance.getComponentImplementation().getName().toUpperCase() + "_" + nonSupervisionInst.getName().toUpperCase() + "_ID;" + LF + "   }" + LF + "   else" + LF + "   {" + LF + "      printf(\"malloc failed in " + modInstContName + "\\n\");" + LF + "   }" + LF + LF +

							"   " + modInstContName + "__Queue_Message(&message);" + LF + "}" + LF + LF;
				}
			}

			// Service Availability notifications
			for (SM_Object reqService : moduleInstance.getComponentImplementation().getCompType().getReferenceInstancesList()) {
				superOpsText += SEP_PATTERN_1211 + modInstContName + "__Service_Availability_Changed_" + reqService.getName() + "(ECOA__timestamp *timestamp, ECOA__boolean8 available)";

				if (isHeader) {
					superOpsText += ";" + LF;
				} else {
					ILIMessage ili = modInstILI.getILIForServiceAvailNotification((SM_ServiceInstance) reqService);

					superOpsText += LF + "{" + LF + "   // Setup the ILI message and call queue message" + LF + "   ILI_Message message;" + LF +

							"   /* Set the messageID & timestamp */" + LF + "   message.messageID = " + ili.getMessageID() + ";" + LF + "   message.timestamp = *timestamp;" + LF + LF +

							"   /* malloc memory for params */" + LF + "   message.messageDataPointer = malloc(sizeof(" + iliName + "_" + ili.getMessageID() + "_params));" + LF + LF +

							"   /* Set the available parameter */" + LF;

					for (SM_OperationParameter opParam : ili.getParams()) {
						superOpsText += "   ((" + iliName + "_" + ili.getMessageID() + "_params*)message.messageDataPointer)->" + opParam.getName() + " = available;" + LF;
					}

					superOpsText += "   " + modInstContName + "__Queue_Message(&message);" + LF + "}" + LF + LF;
				}

				superOpsText += SEP_PATTERN_1211 + modInstContName + "__Service_Provider_Changed_" + reqService.getName() + "(ECOA__timestamp *timestamp)";

				if (isHeader) {
					superOpsText += ";" + LF;
				} else {

					ILIMessage ili = modInstILI.getILIForServiceProviderNotification((SM_ServiceInstance) reqService);

					superOpsText += LF + "{" + LF + "   // Setup the ILI message and call queue message" + LF + "   ILI_Message message;" + LF +

							"   /* Set the messageID & timestamp */" + LF + "   message.messageID = " + ili.getMessageID() + ";" + LF + "   message.timestamp = *timestamp;" + LF + "   message.messageDataPointer = 0;" + LF + LF +

							"   " + modInstContName + "__Queue_Message(&message);" + LF + "}" + LF + LF;
				}
			}

			// Error Notifications
			for (SM_ModuleInstance modInst : compInst.getImplementation().getModuleInstances().values()) {
				if (!modInst.getModuleType().getIsSupervisor()) {
					superOpsText += SEP_PATTERN_1211 + modInstContName + "__Error_Notification_" + modInst.getName() + "(ECOA__timestamp *timestamp, ECOA__module_error_type moduleError)";

					if (isHeader) {
						superOpsText += ";" + LF;
					} else {
						ILIMessage ili = modInstILI.getILIForErrorNotification(modInst);

						superOpsText += LF + "{" + LF + "   // Setup the ILI message and call queue message" + LF + "   ILI_Message message;" + LF +

								"   /* Set the messageID & timestamp */" + LF + "   message.messageID = " + ili.getMessageID() + ";" + LF + "   message.timestamp = *timestamp;" + LF + LF +

								"   /* malloc memory for params */" + LF + "   message.messageDataPointer = malloc(sizeof(" + iliName + "_" + ili.getMessageID() + "_params));" + LF + LF +

								"   /* Set the error type parameter (error or fatal error) */" + LF;

						for (SM_OperationParameter opParam : ili.getParams()) {
							superOpsText += "   ((" + iliName + "_" + ili.getMessageID() + "_params*)message.messageDataPointer)->" + opParam.getName() + " = moduleError;" + LF;
						}

						superOpsText += "   " + modInstContName + "__Queue_Message(&message);" + LF + "}" + LF + LF;
					}

				}
			}

		}

		if (moduleInstance.getModuleType().getIsFaultHandler()) {
			superOpsText += SEP_PATTERN_1211 + modInstContName + "__Error_Notification(ECOA__error_id error_id, ECOA__timestamp *timestamp, ECOA__asset_id asset_id, ECOA__asset_type asset_type, ECOA__error_type error_type)";

			if (isHeader) {
				superOpsText += ";" + LF;
			} else {
				includeList.add(iliName);

				ILIMessage ili = modInstILI.getILIForFaultNotification();

				superOpsText += LF + "{" + LF + "   // Setup the ILI message and call queue message" + LF + "   ILI_Message message;" + LF +

						"   /* Set the messageID & timestamp */" + LF + "   message.messageID = " + ili.getMessageID() + ";" + LF + "   message.timestamp = *timestamp;" + LF + LF +

						assignInputParamsToMessage(ili.getParams(), ili) + "   " + modInstContName + "__Queue_Message(&message);" + LF + "}" + LF + LF;
			}
		}
		// Replace the #SUPER_OPS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SUPER_OPS#", superOpsText);
	}

	public void writeSyncRequestQueueIDDecls() {
		String syncQueueIDDecls = "";

		// Generate a message queue to block on if the module instance has at
		// least one sync request sent op
		for (SM_RequestSentOp requestOp : moduleInstance.getModuleType().getRequestSentOps()) {
			if (requestOp.getIsSynchronous()) {
				if (isHeader) {
					syncQueueIDDecls += "Message_Queue_ID_Type " + modInstContName + "__get_SyncQueueID();" + LF;
				} else {
					syncQueueIDDecls += "static Message_Queue_ID_Type " + compInst.getName() + "_" + moduleInstance.getName() + "__SyncQueueID;" + LF + LF + "Message_Queue_ID_Type " + modInstContName + "__get_SyncQueueID()" + LF + "{" + LF + "   return " + compInst.getName() + "_" + moduleInstance.getName() + "__SyncQueueID;" + LF + "}" + LF + LF;
				}
				break;
			}
		}

		// Replace the #SYNC_REQUEST_QUEUE_ID_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SYNC_REQUEST_QUEUE_ID_DECL#", syncQueueIDDecls);
	}

	public void writeUpdates() {
		String updatesText = "";

		for (SM_DataReadOp dataRead : moduleInstance.getModuleType().getDataReadOps()) {
			if (dataRead.getIsNotifying()) {
				updatesText += SEP_PATTERN_1211 + modInstContName + "__" + dataRead.getName() + "__update()";

				if (isHeader) {
					updatesText += ";" + LF;
				} else {
					updatesText += LF + "{" + LF + "   // Setup the ILI message and call queue message" + LF + "   ILI_Message message;" + LF;

					ILIMessage ili = null;
					for (SM_DataLink dataLink : moduleInstance.getDataLinksForReaderOp(dataRead)) {
						ili = modInstILI.getILIForDataLink(dataLink);

						updatesText += "   /* Set the messageID & timestamp */" + LF + "   message.messageID = " + iliName + "_" + ili.getMessageID() + ";" + LF + "   message.messageDataPointer = 0;" + LF +

								"   " + modInstContName + "__Queue_Message(&message);" + LF;
					}
					updatesText += "}" + LF + LF;
				}
			}
		}

		// Replace the #UPDATES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#UPDATES#", updatesText);
	}

	private String writeWritePinfo(String pinfoText, SM_PinfoValue pinfo) {
		pinfoText += "ECOA__return_status " + modInstContName + "__write_" + pinfo.getName() + LF + "   (ECOA__byte *memory_address," + LF + "    ECOA__uint32 in_size)";

		if (isHeader) {
			pinfoText += ";" + LF;
		} else {
			String filePointerName = modInstContName + "_fp_" + pinfo.getName();

			pinfoText += LF + "{" + LF + "   return ECOA_write_file(" + filePointerName + ", memory_address, in_size, &" + filePointerName + "_CurrentIndex, " + filePointerName + "_MaxCapacity);" + LF + "}" + LF + LF;
		}
		return pinfoText;
	}

}
