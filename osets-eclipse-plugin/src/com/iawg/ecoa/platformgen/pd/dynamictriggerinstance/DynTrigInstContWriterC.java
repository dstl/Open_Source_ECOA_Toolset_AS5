/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.dynamictriggerinstance;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.TypesProcessorC;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.DynamicTriggerInstanceILI;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.EventILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.LifecycleILIMessage;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverDynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderExternal;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventReceivedOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventSentOp;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedTrigInst;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.types.SM_Array_Type;
import com.iawg.ecoa.systemmodel.types.SM_Fixed_Array_Type;

public class DynTrigInstContWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_561 = "void ";
	private static final String SEP_PATTERN_A = "      if (";
	private static final String SEP_PATTERN_B = "_moduleState = ECOA__module_states_type_IDLE;";
	private static final String SEP_PATTERN_C = "                 ";
	private static final String SEP_PATTERN_D = "_moduleState;";
	private static final String SEP_PATTERN_E = "                 /* Call the Module Instance Queue Operation */";
	private static final String SEP_PATTERN_F = "      return ECOA__return_status_INVALID_TRANSITION;";
	private static final String SEP_PATTERN_G = "                 /* Set the operation timestamp */";
	private static final String SEP_PATTERN_H = "                 ECOA_setTimestamp(&timestamp);";
	private static final String SEP_PATTERN_I = "         {";
	private static final String SEP_PATTERN_J = "#DYNTRIGOP#";
	private static final String SEP_PATTERN_K = "   int size;";
	private static final String SEP_PATTERN_L = "      return ";
	private static final String SEP_PATTERN_M = "         ";
	private static final String SEP_PATTERN_N = "      ";
	private static final String SEP_PATTERN_O = "#REINIT#";
	private static final String SEP_PATTERN_P = "_moduleState);";
	private static final String SEP_PATTERN_Q = "                 /* Timestamp point */";

	private SM_ProtectionDomain protectionDomain;
	// private SM_DeployedTrigInst deployedTrigInst;
	private SM_DynamicTriggerInstance dynTrigInstance;
	private SM_ComponentInstance compInst;
	private SM_ComponentImplementation compImpl;
	private boolean isHeader;
	private String iliName;
	private DynamicTriggerInstanceILI dynTrigInstILI = null;
	private String dynTrigInstContName;

	private ArrayList<String> includeList = new ArrayList<String>();
	private Generic_Platform underlyingPlatform;

	public DynTrigInstContWriterC(PlatformGenerator platformGenerator, boolean isHeader, Path outputDir, SM_DeployedTrigInst deployedTrigInst, SM_ProtectionDomain pd, DynamicTriggerInstanceILI dynTrigInstILI) {
		super(outputDir);
		this.underlyingPlatform = platformGenerator.getunderlyingPlatformInstantiation();

		this.protectionDomain = pd;
		// this.deployedTrigInst = deployedTrigInst;
		this.dynTrigInstance = deployedTrigInst.getDynTrigInstance();
		this.compInst = deployedTrigInst.getCompInstance();
		this.compImpl = compInst.getImplementation();
		this.isHeader = isHeader;
		this.dynTrigInstILI = dynTrigInstILI;

		this.iliName = dynTrigInstance.getName() + "_ILI";
		this.dynTrigInstContName = compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller";

		setFileStructure();
	}

	private String assignInputParamsToMessage(List<SM_OperationParameter> inputParams, ILIMessage ili) {
		String inputParamsString = "";

		if (inputParams.size() > 0) {
			inputParamsString += "         /* Malloc memory for the input params */" + LF + "         message.messageDataPointer = malloc(sizeof(" + iliName + "_" + ili.getMessageID() + "_params));" + LF + "         if (message.messageDataPointer != 0)" + LF + SEP_PATTERN_I + LF;

			for (SM_OperationParameter opParam : inputParams) {
				if (opParam.getType() instanceof SM_Fixed_Array_Type || opParam.getType() instanceof SM_Array_Type) {
					inputParamsString += "            memcpy(&(((" + iliName + "_" + ili.getMessageID() + "_params*)message.messageDataPointer)->" + opParam.getName() + "), " + opParam.getName() + ", sizeof(" + CLanguageSupport.writeType(opParam.getType()) + "));" + LF;

				} else {
					inputParamsString += "            ((" + iliName + "_" + ili.getMessageID() + "_params*)message.messageDataPointer)->" + opParam.getName() + " =" + CLanguageSupport.writeAssignment(opParam);
				}
			}

			inputParamsString += "         }" + LF + "         else" + LF + SEP_PATTERN_I + LF + "            printf(\"malloc failed in " + dynTrigInstContName + "\\n\");" + LF + "         }" + LF + LF;

		} else {
			inputParamsString += "         message.messageDataPointer = 0;" + LF;
		}

		return inputParamsString;
	}

	private String generateINITIALIZEHandler(SM_ModuleInstance supervisorModuleInstance) {
		return "            case " + iliName + "_1 :" + LF + "               if (!" + compInst.getName() + "_" + dynTrigInstance.getName() + "_ModuleDead)" + LF + "               {" + LF + "                 /* Unset Pending State Transition flag */" + LF + SEP_PATTERN_C + compInst.getName() + "_" + dynTrigInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

				"                 /* INITIALIZE RECEIVED - store the previous state for use in the notification */" + LF + "                 previousState = " + compInst.getName() + "_" + dynTrigInstance.getName() + SEP_PATTERN_D + LF + LF +

				"                 /* Update the module state */" + LF + SEP_PATTERN_C + compInst.getName() + "_" + dynTrigInstance.getName() + "_moduleState = ECOA__module_states_type_READY;" + LF + LF +

				SEP_PATTERN_G + LF + SEP_PATTERN_C + compInst.getName() + "_" + dynTrigInstance.getName() + "_operation_timestamp =  queueEntry.timestamp;" + LF + LF +

				"                 if (previousState == ECOA__module_states_type_IDLE)" + LF + "                 {" + LF + "                    /* Call the INITIALIZE function */" + LF + "                    " + compInst.getName() + "_" + dynTrigInstance.getName() + "__INITIALIZE__received(&context);" + LF + "                 }" + LF + "                 else" + LF + "                 {" + LF + "                    /* Call the REINITIALIZE function */" + LF + "                    " + compInst.getName() + "_" + dynTrigInstance.getName() + "__REINITIALIZE__received(&context);" + LF + "                 }" + LF + LF +

				SEP_PATTERN_Q + LF + SEP_PATTERN_H + LF + LF +

				SEP_PATTERN_E + LF + SEP_PATTERN_C + compInst.getName() + "_" + supervisorModuleInstance.getName() + "_Controller__Lifecycle_Notification_" + dynTrigInstance.getName() + "(&timestamp, previousState, " + compInst.getName() + "_" + dynTrigInstance.getName() + SEP_PATTERN_P + LF + "               }" + LF + "               break;" + LF + LF;
	}

	private String generateSHUTDOWNHandler(SM_ModuleInstance supervisorModuleInstance) {
		return "            case " + iliName + "_4 :" + LF + "               if (!" + compInst.getName() + "_" + dynTrigInstance.getName() + "_ModuleDead)" + LF + "               {" + LF + "                 /* Unset Pending State Transition flag */" + LF + SEP_PATTERN_C + compInst.getName() + "_" + dynTrigInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

				"                 /* SHUTDOWN RECEIVED - store the previous state for use in the notification */" + LF + "                 previousState = " + compInst.getName() + "_" + dynTrigInstance.getName() + SEP_PATTERN_D + LF + LF +

				"                 /* Update the module state */" + LF + SEP_PATTERN_C + compInst.getName() + "_" + dynTrigInstance.getName() + SEP_PATTERN_B + LF + LF +

				SEP_PATTERN_G + LF + SEP_PATTERN_C + compInst.getName() + "_" + dynTrigInstance.getName() + "_operation_timestamp =  queueEntry.timestamp;" + LF + LF +

				"                 /* Call the SHUTDOWN function */" + LF + SEP_PATTERN_C + compInst.getName() + "_" + dynTrigInstance.getName() + "__SHUTDOWN__received(&context);" + LF + LF +

				SEP_PATTERN_Q + LF + SEP_PATTERN_H + LF + LF +

				SEP_PATTERN_E + LF + SEP_PATTERN_C + compInst.getName() + "_" + supervisorModuleInstance.getName() + "_Controller__Lifecycle_Notification_" + dynTrigInstance.getName() + "(&timestamp, previousState, " + compInst.getName() + "_" + dynTrigInstance.getName() + SEP_PATTERN_P + LF + "               }" + LF + "               break;" + LF + LF;
	}

	private String generateSTARTHandler(SM_ModuleInstance supervisorModuleInstance) {
		return "            case " + iliName + "_2 :" + LF + "               if (!" + compInst.getName() + "_" + dynTrigInstance.getName() + "_ModuleDead)" + LF + "               {" + LF + "                 /* Unset Pending State Transition flag */" + LF + SEP_PATTERN_C + compInst.getName() + "_" + dynTrigInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

				"                 /* START RECEIVED - store the previous state for use in the notification */" + LF + "                 previousState = " + compInst.getName() + "_" + dynTrigInstance.getName() + SEP_PATTERN_D + LF + LF +

				"                 /* Update the module state */" + LF + SEP_PATTERN_C + compInst.getName() + "_" + dynTrigInstance.getName() + "_moduleState = ECOA__module_states_type_RUNNING;" + LF + LF +

				SEP_PATTERN_G + LF + SEP_PATTERN_C + compInst.getName() + "_" + dynTrigInstance.getName() + "_operation_timestamp =  queueEntry.timestamp;" + LF + LF +

				"                 /* Call the START function */" + LF + SEP_PATTERN_C + compInst.getName() + "_" + dynTrigInstance.getName() + "__START__received(&context);" + LF + LF +

				SEP_PATTERN_Q + LF + SEP_PATTERN_H + LF + LF +

				SEP_PATTERN_E + LF + SEP_PATTERN_C + compInst.getName() + "_" + supervisorModuleInstance.getName() + "_Controller__Lifecycle_Notification_" + dynTrigInstance.getName() + "(&timestamp, previousState, " + compInst.getName() + "_" + dynTrigInstance.getName() + SEP_PATTERN_P + LF + "               }" + LF + "               break;" + LF + LF;
	}

	private String generateSTOPHandler(SM_ModuleInstance supervisorModuleInstance) {
		return "            case " + iliName + "_3 :" + LF + "               if (!" + compInst.getName() + "_" + dynTrigInstance.getName() + "_ModuleDead)" + LF + "               {" + LF + "                 /* Unset Pending State Transition flag */" + LF + SEP_PATTERN_C + compInst.getName() + "_" + dynTrigInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

				"                 /* STOP RECEIVED - store the previous state for use in the notification */" + LF + "                 previousState = " + compInst.getName() + "_" + dynTrigInstance.getName() + SEP_PATTERN_D + LF + LF +

				"                 /* Update the module state */" + LF + SEP_PATTERN_C + compInst.getName() + "_" + dynTrigInstance.getName() + "_moduleState = ECOA__module_states_type_READY;" + LF + LF +

				SEP_PATTERN_G + LF + SEP_PATTERN_C + compInst.getName() + "_" + dynTrigInstance.getName() + "_operation_timestamp =  queueEntry.timestamp;" + LF + LF +

				"                 /* Call the STOP function */" + LF + SEP_PATTERN_C + compInst.getName() + "_" + dynTrigInstance.getName() + "__STOP__received(&context);" + LF + LF +

				SEP_PATTERN_Q + LF + SEP_PATTERN_H + LF + LF +

				SEP_PATTERN_E + LF + SEP_PATTERN_C + compInst.getName() + "_" + supervisorModuleInstance.getName() + "_Controller__Lifecycle_Notification_" + dynTrigInstance.getName() + "(&timestamp, previousState, " + compInst.getName() + "_" + dynTrigInstance.getName() + SEP_PATTERN_P + LF + "               }" + LF + "               break;" + LF + LF;
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
			super.openFile(outputDir.resolve(dynTrigInstance.getName() + "/" + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller.h"));
		} else {
			super.openFile(outputDir.resolve(dynTrigInstance.getName() + "/" + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#GET_STATE#" + LF + "#PROCESS_QUEUE#" + LF + "#QUEUE_MESSAGE#" + LF + "#DYNTRIG#" + LF + SEP_PATTERN_J + LF + "#LIFECYCLE_OPS#" + LF + "#INITIALISE#" + LF + SEP_PATTERN_O + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#FIFO_DEF#" + LF + "#ILI_MESSAGE_DECL#" + LF + "#DYNTRIG_QUEUE_ID_DECLARATION#" + LF + "#DYNTRIG_OP_TIMESTAMP#" + LF + "#DYNTRIG_OP_CONTEXT#" + LF + "#DYNTRIG_MODULE_STATE#" + LF + LF + "#FIFO_ACCESSORS#" + LF + "#GET_STATE#" + LF + "#IS_QUEUE_FULL#" + LF + "#TIMELEFT#" + LF + "#PROCESS_QUEUE#" + LF + "#QUEUE_MESSAGE#" + LF + "#DYNTRIG#" + LF + SEP_PATTERN_J + LF + "#LIFECYCLE_OPS#" + LF + "#INITIALISE#" + LF + SEP_PATTERN_O + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeFIFOAccessors() {
		String fifoAccessorsText = "";

		if (!isHeader) {
			fifoAccessorsText += SEP_PATTERN_561 + dynTrigInstContName + "__Increment_FIFO(int ID)" + LF + "{" + LF + "   Wait_For_Semaphore_Status_Type Wait_For_Semaphore_Status;" + LF + "   Post_Semaphore_Status_Type Post_Semaphore_Status;" + LF + "   Time_Type Timeout = {MAX_SECONDS, MAX_NANOSECONDS};" + LF + LF +

					"   Wait_For_Semaphore(" + compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFOSizeListSemaphore," + LF + "      &Timeout," + LF + "      &Wait_For_Semaphore_Status);" + LF + LF +

					"   " + compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFOSizeList[ID].currentSize++;" + LF + LF +

					"   Post_Semaphore(" + compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFOSizeListSemaphore," + LF + "      &Post_Semaphore_Status);" + LF + LF + "}" + LF + LF +

					SEP_PATTERN_561 + dynTrigInstContName + "__Decrement_FIFO(int ID)" + LF + "{" + LF + "   Wait_For_Semaphore_Status_Type Wait_For_Semaphore_Status;" + LF + "   Post_Semaphore_Status_Type Post_Semaphore_Status;" + LF + "   Time_Type Timeout = {MAX_SECONDS, MAX_NANOSECONDS};" + LF + LF +

					"   Wait_For_Semaphore(" + compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFOSizeListSemaphore," + LF + "      &Timeout," + LF + "      &Wait_For_Semaphore_Status);" + LF + LF +

					"   " + compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFOSizeList[ID].currentSize--;" + LF + LF +

					"   Post_Semaphore(" + compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFOSizeListSemaphore," + LF + "      &Post_Semaphore_Status);" + LF + LF + "}" + LF;
		}

		// Replace the #FIFO_ACCESSORS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#FIFO_ACCESSORS#", fifoAccessorsText);
	}

	public void writeFifoListDefinition() {
		String fifoString = "";

		if (!isHeader) {
			// Create the FIFO list type definition.
			fifoString += "typedef struct" + LF + "{" + LF + "   ECOA__uint32 currentSize;" + LF + "   ECOA__uint32 maxSize;" + LF + "} fifoSizeType;" + LF + LF +

			// NOTE - have to + 1 to size so can index into array on ILI ID
			// (which starts at 1)
					"#define " + compInst.getName() + "_" + dynTrigInstance.getName() + "_ILI_ID_SIZE " + (dynTrigInstILI.getILIMessageList().size() + 1) + LF + "typedef fifoSizeType " + compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFO_Queue_Size_List_Type[" + compInst.getName() + "_" + dynTrigInstance.getName() + "_ILI_ID_SIZE];" + LF + LF +

					// Create the FIFO list declaration
					compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFO_Queue_Size_List_Type " + compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFOSizeList;" + LF + LF +

					"/* Create a variable to hold if a pending state transition request is queued */" + LF + "static ECOA__boolean8 " + compInst.getName() + "_" + dynTrigInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

					"/* Create fifoSizeList Access Semaphore */" + LF + "static int " + compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFOSizeListSemaphore;" + LF;
		}

		// Replace the #FIFO_DEF# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#FIFO_DEF#", fifoString);
	}

	public void writeGetState() {
		String getStateText = "";

		if (isHeader) {
			getStateText += SEP_PATTERN_561 + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__Get_Lifecycle_State(ECOA__module_states_type *moduleState);" + LF;
		} else {
			getStateText += SEP_PATTERN_561 + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__Get_Lifecycle_State(ECOA__module_states_type *moduleState)" + LF + "{" + LF + "   *moduleState = " + compInst.getName() + "_" + dynTrigInstance.getName() + SEP_PATTERN_D + LF + "}" + LF;
		}

		// Replace the #TRIG_INST_ILI# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GET_STATE#", getStateText);
	}

	public void writeILIMessageDecls() {
		String iliMessageDecl = "/* ILI Messages for use in ProcessQueue */" + LF + "static ILI_Message queueEntry;" + LF + "static ILI_Message lifecycleMessage;";

		// Replace the #ILI_MESSAGE_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#ILI_MESSAGE_DECL#", iliMessageDecl);
	}

	public void writeIncludes() {
		if (!isHeader) {
			includeList.addAll(underlyingPlatform.addIncludesModInstCont());
			includeList.addAll(underlyingPlatform.addIncludesTrigInstCont());
			includeList.add(compImpl.getName() + "_Module_Instance_ID");
			includeList.add(protectionDomain.getName() + "_PD_Controller");
			includeList.add(compImpl.getName() + "_Module_Instance_Operation_UID");
			includeList.add("ECOA_time_utils");
			includeList.add("string");
		} else {
			includeList.add(iliName);
			includeList.add("ECOA");
			includeList.add("ILI_Message");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);

	}

	public void writeInitialise(Generic_Platform underlyingPlatformInstantiation) {

		String initialiseText = "";

		if (isHeader) {
			initialiseText += SEP_PATTERN_561 + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__Initialise();" + LF;
		} else {
			initialiseText += SEP_PATTERN_561 + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__Initialise() {" + LF + LF +

					"   unsigned char buffer[255];" + LF + "   int size, i;" + LF + "   Create_Message_Queue_Status_Type CMQ_Status;" + LF + LF +

					"   /* Set the module state to idle */" + LF + "   " + compInst.getName() + "_" + dynTrigInstance.getName() + SEP_PATTERN_B + LF + LF +

					"   /* Create the Dynamic Trigger Instance Message Queue */" + LF + "   Create_Message_Queue(50," + LF + // TODO
					// -
					// work
					// out
					// depth
					// of
					// message
					// queue.
					"       sizeof(ILI_Message)," + LF + "       &" + compInst.getName() + "_" + dynTrigInstance.getName() + "__QueueID," + LF + "       &CMQ_Status);" + LF + LF;

			// Initialise the FIFO size list
			initialiseText += "   /* Initialise the FIFO size list */" + LF;

			initialiseText += "   for (i=0;i<" + compInst.getName() + "_" + dynTrigInstance.getName() + "_ILI_ID_SIZE; i++)" + LF + "   {" + LF + SEP_PATTERN_N + compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFOSizeList[i].currentSize = 0;" + LF + "   }" + LF + LF;

			// Add a case statement for each message which can be received by
			// this module instance.
			for (ILIMessage iliMessage : dynTrigInstILI.getILIMessageList()) {
				if (iliMessage instanceof LifecycleILIMessage) {
					initialiseText += "   " + compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFOSizeList[" + iliMessage.getMessageID() + "].maxSize = 1;" + LF;
				} else if (iliMessage instanceof EventILIMessage) {
					SM_ReceiverDynamicTriggerInstance receiver = ((EventILIMessage) iliMessage).getEventLink().getReceiver(dynTrigInstance);
					if (receiver != null) {
						initialiseText += "   " + compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFOSizeList[" + iliMessage.getMessageID() + "].maxSize = " + receiver.getFifoSize() + ";" + LF;
					}
				}
			}

			// Create fifoSizeListSemaphore
			initialiseText += "   /* Create a semaphore for managing access to the fifoSizeList */" + LF + "   Create_Semaphore_Status_Type Create_Semaphore_Status;" + LF + "   Create_Semaphore(1," + LF + "      1," + LF + "      Queuing_Discipline_FIFO," + LF + "      &" + compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFOSizeListSemaphore," + LF + "      &Create_Semaphore_Status);" + LF + LF +

					"   if (Create_Semaphore_Status != Create_Semaphore_OK)" + LF + "   {" + LF + "      printf(\"ERROR creating fifoSizeList access semaphore\\n\");" + LF + "   }" + LF + LF;

			initialiseText += "}";

			includeList.add(compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller");
		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);

	}

	public void writeIsQueueFull() {
		String isQueueFullString = "ECOA__boolean8 " + compInst.getName() + "_" + dynTrigInstance.getName() + "_isQueueFull(ECOA__uint32 iliID)" + LF + "{" + LF + "   if (" + compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFOSizeList[iliID].currentSize >= " + compInst.getName() + "_" + dynTrigInstance.getName() + "_FIFOSizeList[iliID].maxSize)" + LF + "   {" + LF + "      return ECOA__TRUE;" + LF + "   }" + LF + "   else" + LF + "   {" + LF + "      return ECOA__FALSE;" + LF + "   }" + LF + "}" + LF;

		// Replace the #IS_QUEUE_FULL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#IS_QUEUE_FULL#", isQueueFullString);
	}

	public void WriteLifecycleOps() {
		String lifecycleOpsText = "";

		includeList.add(compImpl.getName() + "_" + dynTrigInstance.getName() + "_DynTrigModule");

		// INITIALIZE operation
		lifecycleOpsText += "ECOA__return_status " + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__INITIALIZE_received(ECOA__timestamp *timestamp)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "   /* Setup the ILI message and call queue message */" + LF + "   ILI_Message message;" + LF +

					"   /* Set the messageID & timestamp */" + LF + "   message.messageID = " + iliName + "_1;" + LF + "   message.timestamp = *timestamp;" + LF + "   message.messageDataPointer = 0;" + LF + LF +

					"   return " + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__Queue_Message(&message);" + LF + "}" + LF + LF;
		}

		// START operation
		lifecycleOpsText += "ECOA__return_status " + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__START_received(ECOA__timestamp *timestamp)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "   if (" + compInst.getName() + "_" + dynTrigInstance.getName() + "_moduleState == ECOA__module_states_type_READY)" + LF + "   {" + LF + "      /* Setup the ILI message and call queue message */" + LF + "      ILI_Message message;" + LF +

					"      /* Set the messageID & timestamp */" + LF + "      message.messageID = " + iliName + "_2;" + LF + "      message.timestamp = *timestamp;" + LF + "      message.messageDataPointer = 0;" + LF + LF +

					SEP_PATTERN_L + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__Queue_Message(&message);" + LF + "   }" + LF + "   else" + LF + "   {" + LF + SEP_PATTERN_F + LF + "   }" + LF + "}" + LF + LF;
		}

		// STOP operation
		lifecycleOpsText += "ECOA__return_status " + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__STOP_received(ECOA__timestamp *timestamp)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "   if (" + compInst.getName() + "_" + dynTrigInstance.getName() + "_moduleState == ECOA__module_states_type_RUNNING)" + LF + "   {" + LF + "      /* Setup the ILI message and call queue message */" + LF + "      ILI_Message message;" + LF +

					"      /* Set the messageID & timestamp */" + LF + "      message.messageID = " + iliName + "_3;" + LF + "      message.timestamp = *timestamp;" + LF + "      message.messageDataPointer = 0;" + LF + LF +

					SEP_PATTERN_L + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__Queue_Message(&message);" + LF + "   }" + LF + "   else" + LF + "   {" + LF + SEP_PATTERN_F + LF + "   }" + LF + "}" + LF + LF;
		}

		// SHUTDOWN operation
		lifecycleOpsText += "ECOA__return_status " + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__SHUTDOWN_received(ECOA__timestamp *timestamp)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "   if (" + compInst.getName() + "_" + dynTrigInstance.getName() + "_moduleState == ECOA__module_states_type_READY ||" + LF + "       " + compInst.getName() + "_" + dynTrigInstance.getName() + "_moduleState == ECOA__module_states_type_RUNNING)" + LF + "   {" + LF + "      /* Setup the ILI message and call queue message */" + LF + "      ILI_Message message;" + LF +

					"      /* Set the messageID & timestamp */" + LF + "      message.messageID = " + iliName + "_4;" + LF + "      message.timestamp = *timestamp;" + LF + "      message.messageDataPointer = 0;" + LF + LF +

					SEP_PATTERN_L + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__Queue_Message(&message);" + LF + "   }" + LF + "   else" + LF + "   {" + LF + SEP_PATTERN_F + LF + "   }" + LF + "}" + LF + LF;
		}

		// Replace the #LIFECYCLE_OPS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#LIFECYCLE_OPS#", lifecycleOpsText);
	}

	public void writeModuleOpTimestampDecl() {
		String OpTimestampDecl = LF + "/* Create an operation timestamp variable */" + LF + "static ECOA__timestamp " + compInst.getName() + "_" + dynTrigInstance.getName() + "_operation_timestamp = {0,0};";
		// Replace the #DYNTRIG_OP_TIMESTAMP# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#DYNTRIG_OP_TIMESTAMP#", OpTimestampDecl);
	}

	public void writeModuleOpContextDecl() {
		String OpTimestampDecl = LF + "/* Create an context holder variable */" + LF + "static " + compInst.getName() + "_" + dynTrigInstance.getName() + "__context context;";
		// Replace the #DYNTRIG_OP_CONTEXT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#DYNTRIG_OP_CONTEXT#", OpTimestampDecl);
	}

	public void writeModuleStateDecl() {
		String moduleStateDecl = LF + "/* Create a dynamic trigger \"module\" state variable */" + LF + "static ECOA__module_states_type " + compInst.getName() + "_" + dynTrigInstance.getName() + SEP_PATTERN_D;

		// Replace the #DYNTRIG_MODULE_STATE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#DYNTRIG_MODULE_STATE#", moduleStateDecl);
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller.h */" + LF;
		} else {
			preambleText += "/* File " + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeTimeLeft() {
		String timeLeftString = "";

		if (!isHeader) {
			timeLeftString += "static ECOA__duration timeLeft( ECOA__duration delay, ECOA__timestamp refTime )" + LF + "{" + LF + "   ECOA__timestamp now;" + LF + "   ECOA__duration timeRemaining;" + LF + "   /*" + LF + "    * Has the due time already passed? */" + LF + "   ECOA_setTimestamp( &now );" + LF + "   if( now.seconds > refTime.seconds + delay.seconds ||" + LF + "     ( now.seconds == refTime.seconds + delay.seconds &&" + LF + "       now.nanoseconds > refTime.nanoseconds + delay.nanoseconds )){" + LF + "      timeRemaining = (ECOA__duration){0,0};" + LF + "   } else {" + LF + "      /* If not, then wait the remainder... */" + LF + "      timeRemaining.seconds     = delay.seconds     + refTime.seconds     - now.seconds;" + LF + "      timeRemaining.nanoseconds = delay.nanoseconds + refTime.nanoseconds - now.nanoseconds;" + LF + "      while( timeRemaining.nanoseconds > 1000000000 ){" + LF + "         timeRemaining.nanoseconds -= 1000000000;" + LF + "         timeRemaining.seconds += 1;" + LF + "      }" + LF + "   }" + LF + "   return timeRemaining;" + LF + "}" + LF;

		}
		// Replace the #TIMELEFT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#TIMELEFT#", timeLeftString);
	}

	public void writeProcessQueue() {

		String processQueueString = "";

		if (isHeader) {
			processQueueString += SEP_PATTERN_561 + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__ProcessQueue();" + LF;
		} else {
			SM_ModuleInstance supervisorModuleInstance = null;
			// Set the supervisor module instance
			for (SM_ModuleInstance modInst : dynTrigInstance.getComponentImplementation().getModuleInstances().values()) {
				// NOTE - can only handle one supervisor module instance at the
				// moment.
				if (modInst.getModuleType().getIsSupervisor() == true) {
					supervisorModuleInstance = modInst;
					includeList.add(compInst.getName() + "_" + supervisorModuleInstance.getName() + "_Controller");
					break;
				}
			}

			processQueueString += SEP_PATTERN_561 + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__ProcessQueue() { " + LF + LF + "   ECOA__timestamp timestamp;" + LF + "   ECOA__module_states_type previousState;" + LF + "   Receive_Message_Queue_Status_Type RMQ_Status;" + LF + "   unsigned char buffer[256];" + LF + SEP_PATTERN_K + LF + LF +

					"   /* Main queue processing loop */" + LF + "   while (1) {" + LF + LF +

					"      Receive_Message_Queue(" + compInst.getName() + "_" + dynTrigInstance.getName() + "__QueueID," + LF + "         (void *)&queueEntry," + LF + "         sizeof(ILI_Message)," + LF + "         &RMQ_Status);" + LF + LF +

					"      /* Decrement the current queue size */" + LF + SEP_PATTERN_N + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__Decrement_FIFO(queueEntry.messageID);" + LF + LF +

					"      /* If \"dead\", decrement the currentqueuesize */" + LF + SEP_PATTERN_A + compInst.getName() + "_" + dynTrigInstance.getName() + "_ModuleDead)" + LF + "      {" + LF + "         if (" + compInst.getName() + "_" + dynTrigInstance.getName() + "_CurrentQueueSize == 0)" + LF + SEP_PATTERN_I + LF + "            " + compInst.getName() + "_" + dynTrigInstance.getName() + "_ModuleDead = ECOA__FALSE;" + LF + "         }" + LF + "         else" + LF + SEP_PATTERN_I + LF + "            " + compInst.getName() + "_" + dynTrigInstance.getName() + "_CurrentQueueSize--;" + LF + "         }" + LF + "      }" + LF + LF +

					"      if (RMQ_Status == Receive_Message_Queue_OK)" + LF + "      {" + LF + "         switch (queueEntry.messageID) {" + LF + generateINITIALIZEHandler(supervisorModuleInstance) + generateSTARTHandler(supervisorModuleInstance) + generateSTOPHandler(supervisorModuleInstance) + generateSHUTDOWNHandler(supervisorModuleInstance);

			// Add a case statement for each _remaining_ message which can be
			// received by this dyn. trig. instance.
			// Obviously this should be just 'in' and 'reset' - assuming that
			// the names don't change
			for (ILIMessage iliMessage : dynTrigInstILI.getILIMessageList()) {
				if (iliMessage.getMessageID() > 4) {
					EventILIMessage eventMessage = ((EventILIMessage) iliMessage);
					for (SM_ReceiverInterface receiver : eventMessage.getEventLink().getReceivers()) {
						if (receiver.getReceiverInst() == dynTrigInstance) {
							String iliNameAndID = iliName + "_" + iliMessage.getMessageID();

							processQueueString += "            case " + iliName + "_" + iliMessage.getMessageID() + ":" + LF + "            {" + LF + "               /* Check the dyn. trig. is still in the \"RUNNING\" state */" + LF + "               if (" + compInst.getName() + "_" + dynTrigInstance.getName() + "_moduleState == ECOA__module_states_type_RUNNING)" + LF + "               {" + LF;

							// Create a pointer to the params (if there is any)
							if (iliMessage.getParams().size() > 0) {
								processQueueString += "                  " + iliNameAndID + "_params *" + iliNameAndID + "_params_ptr = (" + iliNameAndID + "_params*)queueEntry.messageDataPointer;" + LF;
							}

							processQueueString += "                  /* Set the operation timestamp */" + LF + "                  " + compInst.getName() + "_" + dynTrigInstance.getName() + "_operation_timestamp = queueEntry.timestamp;" + LF;

							if (receiver.getReceiverOp().getName().equalsIgnoreCase("in")) {
								processQueueString += "                  /* At this point we must reduce the delay value by the amount of time taken to get here... */" + LF;
								processQueueString += "                  ECOA__duration delay_ = timeLeft( " + iliNameAndID + "_params_ptr->delay, " + compInst.getName() + "_" + dynTrigInstance.getName() + "_operation_timestamp );" + LF;
							}
							processQueueString += "                  " + compInst.getName() + "_" + dynTrigInstance.getName() + "__" + receiver.getReceiverOp().getName() + "__received(&context";

							for (SM_OperationParameter param : iliMessage.getParams()) {
								if (receiver.getReceiverOp().getName().equalsIgnoreCase("in") && param.getName().equals("delay")) {
									processQueueString += ", &delay_";
								} else {
									if (param.getType().isSimple()) {
										processQueueString += ", " + iliNameAndID + "_params_ptr->" + param.getName();
									} else {
										processQueueString += ", &" + iliNameAndID + "_params_ptr->" + param.getName();
									}
								}
							}
							processQueueString += ");" + LF + "               }" + LF + "               break;" + LF + "            }" + LF + LF;
						}
					}
				}
			}

			processQueueString += "         }" + LF + "      }" + LF + "      if (queueEntry.messageDataPointer != 0)" + LF + "      {" + LF + "         free(queueEntry.messageDataPointer);" + LF + "      }" + LF + LF + "   }" + LF + "}" + LF + LF;
		}

		// Replace the #PROCESS_QUEUE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PROCESS_QUEUE#", processQueueString);
	}

	public void writeQueueMessage() {
		String queueMessageText = "";

		if (isHeader) {
			queueMessageText += "ECOA__return_status " + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__Queue_Message(ILI_Message *iliMessage);" + LF;
		} else {
			queueMessageText += "ECOA__return_status " + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__Queue_Message(ILI_Message *iliMessage) {" + LF + LF +

					"   Send_Message_Queue_Status_Type smqStatus;" + LF + LF + "   ECOA__return_status queueStatus = ECOA__return_status_OK;" + LF + LF +

					"   unsigned char logBuffer[512];" + LF + SEP_PATTERN_K + LF + LF +

					"   /* Lifecycle messages - always queue these */" + LF + "   if (iliMessage->messageID >= 1 && iliMessage->messageID <= 4)" + "   {" + LF + SEP_PATTERN_A + compInst.getName() + "_" + dynTrigInstance.getName() + "_PendingStateTransition == ECOA__TRUE)" + LF + "      {" + LF + "         size = sprintf((char*)logBuffer, \"Pending Module Lifecycle operation in queue (message being discarded!) - " + compInst.getName() + " " + dynTrigInstance.getName() + "\\n\");" + LF + "         ecoaLog(logBuffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "         queueStatus = ECOA__return_status_PENDING_STATE_TRANSITION;" + LF + "      }" + LF + "      else" + LF + "      {" + LF + SEP_PATTERN_M + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__Increment_FIFO(iliMessage->messageID);" + LF + "         /* Set Pending State Transition flag */" + LF + SEP_PATTERN_M + compInst.getName() + "_" + dynTrigInstance.getName() + "_PendingStateTransition = ECOA__TRUE;" + LF + "         /* Queue all lifecycle messages */" + LF + "         Send_Message_Queue(" + compInst.getName() + "_" + dynTrigInstance.getName() + "__QueueID," + LF + "            iliMessage," + LF
					+ "            sizeof(ILI_Message)," + LF + "            &smqStatus);" + LF + "      }" + LF + "   }" + LF + "   /* Otherwise check the state to see if we need to queue the message */" + LF + "   else if (" + compInst.getName() + "_" + dynTrigInstance.getName() + "_moduleState == ECOA__module_states_type_RUNNING)" + LF + "   {" + LF + SEP_PATTERN_A + compInst.getName() + "_" + dynTrigInstance.getName() + "_isQueueFull(iliMessage->messageID))" + LF + "      {" + LF + "         size = sprintf((char*)logBuffer, \"Queue Full for ILI_Message_%d in " + compInst.getName() + " " + dynTrigInstance.getName() + "\\n\", iliMessage->messageID);" + LF + "         ecoaLog(logBuffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "         queueStatus = ECOA__return_status_OPERATION_NOT_AVAILABLE;" + LF + "         if (iliMessage->messageDataPointer != 0)" + LF + SEP_PATTERN_I + LF + "            free(iliMessage->messageDataPointer);" + LF + "         }" + LF + "      }" + LF + "      else" + LF + "      {" + LF + SEP_PATTERN_M + dynTrigInstContName + "__Increment_FIFO(iliMessage->messageID);" + LF + "         /* Queue this message */" + LF + "         Send_Message_Queue("
					+ compInst.getName() + "_" + dynTrigInstance.getName() + "__QueueID," + LF + "            iliMessage," + LF + "            sizeof(ILI_Message)," + LF + "            &smqStatus);" + LF + LF +

					"         if (smqStatus != Send_Message_Queue_OK)" + LF + SEP_PATTERN_I + LF + "            printf(\"ERROR - Failed to queue message in " + dynTrigInstContName + "__Queue_Message\\n\");" + LF + "            queueStatus = ECOA__return_status_OPERATION_NOT_AVAILABLE;" + LF + "            if (iliMessage->messageDataPointer != 0)" + LF + "            {" + LF + "               free(iliMessage->messageDataPointer);" + LF + "            }" + LF + "         }" + LF + "      }" + LF + "   }" + LF + "   else" + LF + "   {" + LF + "      /* Not in the running state - DO NOT QUEUE */" + LF + "      size = sprintf((char*)logBuffer, \"" + dynTrigInstContName + " - ILI_Message_%d not queued\\n\", iliMessage->messageID);" + LF + "      ecoaLog(logBuffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "      queueStatus = ECOA__return_status_OPERATION_NOT_AVAILABLE;" + LF + "      if (iliMessage->messageDataPointer != 0)" + LF + "      {" + LF + "         free(iliMessage->messageDataPointer);" + LF + "      }" + LF + "   }" + LF + LF +

					"   return queueStatus;" + LF + "}" + LF + LF;
		}

		// Replace the #QUEUE_MESSAGE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#QUEUE_MESSAGE#", queueMessageText);
	}

	public void writeReinitialise() {
		String reinitText = "";

		if (isHeader) {
			reinitText += SEP_PATTERN_561 + dynTrigInstContName + "__Reinitialise(ECOA__boolean8 restartThread);" + LF;
		} else {
			SM_DeployedTrigInst depTrigInst = null;
			// Get the associated deployed dynTrig instance (as we need thread
			// priority)
			for (SM_DeployedTrigInst dti : compInst.getDeployedTrigInsts()) {
				if (dti.getDynTrigInstance() == dynTrigInstance) {
					depTrigInst = dti;
					break;
				}
			}

			Integer priority = depTrigInst.getPriority();

			String modQueueThreadName = null;
			if (!depTrigInst.isDynamicTriggerInstance()) {
				if (depTrigInst.getTrigInstance().getName().length() < 12) {
					modQueueThreadName = depTrigInst.getTrigInstance().getName() + "_MQ";
				} else {
					modQueueThreadName = depTrigInst.getTrigInstance().getName().substring(0, 12) + "_MQ";
				}
			} else {
				if (depTrigInst.getDynTrigInstance().getName().length() < 12) {
					modQueueThreadName = depTrigInst.getDynTrigInstance().getName() + "_DQ";
				} else {
					modQueueThreadName = depTrigInst.getDynTrigInstance().getName().substring(0, 12) + "_DQ";
				}
			}

			reinitText += SEP_PATTERN_561 + dynTrigInstContName + "__Reinitialise(ECOA__boolean8 restartThread)" + LF + "{" + LF + "   unsigned char buffer[255];" + LF + SEP_PATTERN_K + LF + LF +

					"   /* Set the Module State IDLE */" + LF + "   " + compInst.getName() + "_" + dynTrigInstance.getName() + SEP_PATTERN_B + LF + LF +

					"   /* Set the pending state transition flag false */" + LF + "   " + compInst.getName() + "_" + dynTrigInstance.getName() + "_PendingStateTransition = ECOA__FALSE;" + LF + LF +

					"   /* Set the module \"dead\" flag */" + LF + "   " + compInst.getName() + "_" + dynTrigInstance.getName() + "_ModuleDead = ECOA__TRUE;" + LF + LF +

					"   /* Determine how many items are currently on the queue (as we must \"clear\" the queue upto this point\") */" + LF + "   " + compInst.getName() + "_" + dynTrigInstance.getName() + "_CurrentQueueSize = Get_Queue_Size(" + compInst.getName() + "_" + dynTrigInstance.getName() + "__QueueID);" + LF + LF +

					"   if (restartThread)" + LF + "   {" + LF + "      /* Restart the dynTrig thread (process queue thread) */" + LF + SEP_PATTERN_N + protectionDomain.getName() + "_startThread(" + compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller__ProcessQueue, " + priority.toString() + ", \"" + modQueueThreadName + "\", 0);" + LF + LF + "   }" + LF + "}" + LF + LF;

		}

		// Replace the #REINIT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, SEP_PATTERN_O, reinitText);
	}

	public void writeTriggerOps(Generic_Platform underlyingPlatformInstantiation) {
		String eventRxText = "";

		for (SM_EventReceivedOp evRx : dynTrigInstance.getModuleType().getEventReceivedOps()) {
			eventRxText += SEP_PATTERN_561 + dynTrigInstContName + "__" + evRx.getName() + "__event_received(ECOA__timestamp *timestamp, ECOA__uint32 senderID";

			if (evRx.getInputs() != null) {
				for (SM_OperationParameter opParam : evRx.getInputs()) {
					eventRxText += CLanguageSupport.writeConstParam(opParam);
				}
			}

			if (isHeader) {
				eventRxText += ");" + LF;
			} else {
				eventRxText += ")" + LF + "{" + LF + "   /* Setup the ILI message and call queue message */" + LF + "   ILI_Message message;" + LF + LF +

						"   switch (senderID)" + LF + "   {" + LF;

				ILIMessage ili = null;
				for (SM_EventLink evLink : dynTrigInstance.getLinksForReceiverOp(evRx)) {
					for (SM_SenderInterface senderInterface : evLink.getSenders()) {
						ili = dynTrigInstILI.getILIForEventLink(evLink);
						eventRxText += "      case " + getUID(senderInterface) + " : " + LF + "      {" + LF + "         /* Set the messageID & timestamp */" + LF + "         message.messageID = " + iliName + "_" + ili.getMessageID() + ";" + LF + "         message.timestamp = *timestamp;" + LF;
						if (evRx.getInputs() != null) {
							eventRxText += LF + assignInputParamsToMessage(evRx.getInputs(), ili);
						} else {
							eventRxText += "         message.messageDataPointer = 0;" + LF + LF;
						}
						eventRxText += SEP_PATTERN_M + dynTrigInstContName + "__Queue_Message(&message);" + LF + "         break;" + LF + "      }" + LF + LF;
					}
				}
				eventRxText +=
						// end the switch statement
						"   }" + LF + "}" + LF + LF;
			}
		}

		// Replace the #DYNTRIG# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#DYNTRIG#", eventRxText);

		String eventOpText = "";

		for (SM_EventSentOp evSnd : dynTrigInstance.getModuleType().getEventSentOps()) {
			eventOpText += SEP_PATTERN_561 + compInst.getName() + "_" + dynTrigInstance.getName() + "__" + evSnd.getName() + "__send(" + "ECOA__timestamp *timestamp, void* timerArgs";

			if (isHeader) {
				eventOpText += ");" + LF;
			} else {
				eventOpText += ")" + LF + "{" + LF + "	" + compInst.getName() + "_" + dynTrigInstance.getName() + "__TriggerParams* paramRef = *((" + compInst.getName() + "_" + dynTrigInstance.getName() + "__TriggerParams**)timerArgs);" + LF;

				if (evSnd.getInputs() != null) {
					for (SM_OperationParameter opParam : evSnd.getInputs()) {
						eventOpText += "	" + TypesProcessorC.convertParameterToC(opParam.getType()) + " " + opParam.getName() + ";" + LF;
					}
					eventOpText += LF + "	/* Recover the client params */" + LF;
					for (SM_OperationParameter opParam : evSnd.getInputs()) {
						if (opParam.getType().isSimple()) {
							eventOpText += "	" + opParam.getName() + " = ((" + compInst.getName() + "_" + dynTrigInstance.getName() + "__TriggerParams*)paramRef)->" + opParam.getName() + ";" + LF;
						} else {
							eventOpText += "	memcpy( &" + opParam.getName() + ", &(((" + compInst.getName() + "_" + dynTrigInstance.getName() + "__TriggerParams*)paramRef)->" + opParam.getName() + "), sizeof(" + TypesProcessorC.convertParameterToC(opParam.getType()) + "));" + LF;
						}
					}
				}
				eventOpText += LF + "	/* Clean up, and free the timer */" + LF + "	free( paramRef );" + LF + "	*((" + compInst.getName() + "_" + dynTrigInstance.getName() + "__TriggerParams**)timerArgs) = (" + compInst.getName() + "_" + dynTrigInstance.getName() + "__TriggerParams*)NULL;" + LF;

				eventOpText += LF + "	/* Call the Module Instance Queue Operations */" + LF;

				for (SM_EventLink evLnk : dynTrigInstance.getLinksForSenderOp(evSnd)) {
					for (SM_ReceiverInterface evRcvr : evLnk.getReceivers()) {
						String targetModName = compImpl.getName() + "_" + evRcvr.getReceiverInst().getName() + "_Controller";
						eventOpText += "	" + targetModName + "__" + evRcvr.getReceiverOp().getName();

						eventOpText += "__event_received(timestamp," + compInst.getName().toUpperCase() + "_" + dynTrigInstance.getName().toUpperCase() + "_" + "OUT" + "_UID";

						if (evSnd.getInputs() != null) {
							for (SM_OperationParameter opParam : evSnd.getInputs()) {
								if (opParam.getType().isSimple()) {
									eventOpText += ", " + opParam.getName();
								} else {
									eventOpText += ", &" + opParam.getName();
								}
							}
						}
						eventOpText += " );" + LF;
					}
				}
				eventOpText += "}" + LF + LF;
			}
		}
		// Replace the #DYNTRIGOP# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, SEP_PATTERN_J, eventOpText);

	}

	public void writeTriggerQueueIDDecl() {
		String dynTrigQueueIDDecl = LF + "/* Create some queue control variables */" + LF + "static Message_Queue_ID_Type " + compInst.getName() + "_" + dynTrigInstance.getName() + "__QueueID;" + LF +

				"static ECOA__boolean8 " + compInst.getName() + "_" + dynTrigInstance.getName() + "_ModuleDead = ECOA__FALSE;" + LF +

				"static int " + compInst.getName() + "_" + dynTrigInstance.getName() + "_CurrentQueueSize = 0;";

		// Replace the #DYNTRIG_QUEUE_ID_DECLARATION# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#DYNTRIG_QUEUE_ID_DECLARATION#", dynTrigQueueIDDecl);

	}

}
