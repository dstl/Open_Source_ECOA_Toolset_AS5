/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.pdcontrol;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_VDRepository;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedModInst;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedTrigInst;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

@SuppressWarnings("unused")
public class PDControllerWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_91 = "void ";
	private boolean isHeader;
	private SM_ProtectionDomain pd;
	private Generic_Platform underlyingPlatform;

	private ArrayList<String> includeList = new ArrayList<String>();

	public PDControllerWriterC(PlatformGenerator platformGenerator, boolean isHeader, Path outputDir, SM_ProtectionDomain protectionDomain) {
		super(outputDir);
		this.isHeader = isHeader;
		this.pd = protectionDomain;
		this.underlyingPlatform = platformGenerator.getunderlyingPlatformInstantiation();

		setFileStructure();
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve(pd.getName() + "_PD_Controller.h"));
		} else {
			super.openFile(outputDir.resolve(pd.getName() + "_PD_Controller.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#ALLOCATE_SEQUENCE_NUM#" + LF + "#INITIALISE#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#SEQUENCE_NUM_SEMAPHORE_DECL#" + LF + "#ALLOCATE_SEQUENCE_NUM#" + LF + "#INITIALISE#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeAllocateSequenceNum() {
		String allocateSequenceNumText = "";

		if (isHeader) {
			allocateSequenceNumText += SEP_PATTERN_91 + pd.getName() + "_PD_Controller__Allocate_Sequence_Number(ECOA__uint32 *SeqNum);" + LF;
		} else {
			allocateSequenceNumText += SEP_PATTERN_91 + pd.getName() + "_PD_Controller__Allocate_Sequence_Number(ECOA__uint32 *SeqNum)" + LF + "{" + LF + "   Wait_For_Semaphore_Status_Type Wait_For_Semaphore_Status;" + LF + "   Post_Semaphore_Status_Type Post_Semaphore_Status;" + LF + "   Time_Type Timeout = {MAX_SECONDS, MAX_NANOSECONDS};" + LF + LF +

					"   Wait_For_Semaphore(Sequence_Number_Access_Semaphore," + LF + "      &Timeout," + LF + "      &Wait_For_Semaphore_Status);" + LF + LF +

					"   *SeqNum = Sequence_Number++;" + LF +

					"   /* Wrap around at max value of uint32, but skip 0... */" + LF + "   if (Sequence_Number == 0xFFFFFFFF)" + LF + "   {" + LF + "      Sequence_Number = 1;" + LF + "   }" + LF + LF +

					"   Post_Semaphore(Sequence_Number_Access_Semaphore," + LF + "      &Post_Semaphore_Status);" + LF + LF +

					"}" + LF;
		}

		// Replace the #ALLOCATE_SEQUENCE_NUM# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#ALLOCATE_SEQUENCE_NUM#", allocateSequenceNumText);
	}

	public void writeIncludes() {
		if (isHeader) {
			includeList.addAll(underlyingPlatform.addIncludesPDControllerHeader());
		} else {
			includeList.addAll(underlyingPlatform.addIncludesPDControllerBody());
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeInitialise(Generic_Platform underlyingPlatformInstantiation) {

		String initialiseText = "";

		includeList.add(pd.getName() + "_Service_Manager");

		if (isHeader) {
			initialiseText += SEP_PATTERN_91 + pd.getName() + "_PD_Controller__Initialise();" + LF + LF + SEP_PATTERN_91 + pd.getName() + "_startThread(void (*moduleInstance)(), int priority, char *threadName, void *arg);" + LF + LF;
		} else {
			initialiseText +=

					SEP_PATTERN_91 + pd.getName() + "_delay(unsigned int seconds, unsigned int nanoseconds)" + LF + "{" + LF + underlyingPlatformInstantiation.generateSleepVariableDecls() + LF + underlyingPlatformInstantiation.generateSleepAttributes("seconds", "nanoseconds") + LF + underlyingPlatformInstantiation.generateSleepCall() + LF + "}" + LF + LF +

							SEP_PATTERN_91 + pd.getName() + "_startThread(void (*moduleInstance)(), int priority, char *threadName, void *arg)" + LF + "{" + LF + LF + "   /* Create the Process Queue Thread */" + LF + underlyingPlatformInstantiation.generateThreadVariableDecls("   ") + LF + underlyingPlatformInstantiation.generateThreadAttributes("   ", "priority") + LF + underlyingPlatformInstantiation.generateThreadCreate("   ", "moduleInstance") + LF + "}" + LF + LF;

			// Generate the Initialise function.
			initialiseText += SEP_PATTERN_91 + pd.getName() + "_PD_Controller__Initialise() {" + LF + LF +

					underlyingPlatformInstantiation.generateSetOwnPriority("", "49") + LF + LF +

					// Create a semaphore for managing access to the async
					// request ID
					"   /* Create a semaphore for managing access to the async request ID */" + LF + "   Create_Semaphore_Status_Type Create_Semaphore_Status;" + LF + "   Create_Semaphore(1," + LF + "      1," + LF + "      Queuing_Discipline_FIFO," + LF + "      &Sequence_Number_Access_Semaphore," + LF + "      &Create_Semaphore_Status);" + LF + LF +

					"   if (Create_Semaphore_Status != Create_Semaphore_OK)" + LF + "   {" + LF + "      printf(\"ERROR creating sequence number access semaphore\\n\");" + LF + "   }" + LF + LF +

					// Initialise Versioned Data Repositories
					"   /* Versioned Data Initialisation */" + LF;

			ArrayList<SM_ComponentInstance> processedCompInsts = new ArrayList<SM_ComponentInstance>();
			for (SM_DeployedModInst depModInst : pd.getDeployedModInsts()) {
				if (!processedCompInsts.contains(depModInst.getCompInstance())) {
					SM_ComponentInstance compInst = depModInst.getCompInstance();
					processedCompInsts.add(compInst);

					for (SM_VDRepository vdRepo : depModInst.getCompInstance().getImplementation().getVdRepositories()) {
						String vdName = pd.getName() + "_" + compInst.getName() + "_VD" + vdRepo.getName();

						initialiseText += "   " + vdName + "__Initialise();" + LF;

						includeList.add(vdName);
					}
				}
			}

			// Initialise the service API's
			initialiseText += LF + "   /* Service API Initialisation */" + LF;
			for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
				for (SM_ServiceInstance servRefInst : compInst.getCompType().getServiceAndReferenceInstancesList()) {
					initialiseText += "   " + compInst.getName() + "_" + servRefInst.getName() + "__Initialise();" + LF;

					// Add an include
					includeList.add(compInst.getName() + "_" + servRefInst.getName() + "_Controller");
				}
			}

			initialiseText += LF + "   /* PD Manager initialisation */" + LF + "   " + pd.getName() + "_PD_Manager__Initialise();" + LF + LF;

			// Initialise the service manager
			initialiseText += "   /* Service Manager initialisation */" + LF + "   " + pd.getName() + "_Service_Manager__Initialise();" + LF + LF;

			// Create the timer event handler thread
			includeList.add(pd.getName() + "_Timer_Event_Manager");
			includeList.add(pd.getName() + "_Timer_Event_Handler");
			initialiseText += "   /* Initialse the Timer Event Manager */" + LF + "   " + pd.getName() + "_Timer_Event_Manager__Initialise();" + LF + LF +

					"   /* Timer Event Handler Thread */" + LF + "   " + pd.getName() + "_startThread(" + pd.getName() + "_Timer_Event_Handler__Event_Handler, " + underlyingPlatformInstantiation.getELIReceiverPriority() + ", \"EventHandler\", 0);" + LF;

			// Initialise Module Instance Controllers
			initialiseText += LF + "   /* Module Instance Initialisation */" + LF;
			for (SM_DeployedModInst depModInst : pd.getDeployedModInsts()) {
				initialiseText += "   " + depModInst.getCompInstance().getName() + "_" + depModInst.getModInstance().getName() + "_Controller__Initialise();" + LF;

				// Add to the list of includes to generate
				includeList.add(depModInst.getCompInstance().getName() + "_" + depModInst.getModInstance().getName() + "_Controller");
			}

			// Initialise Trigger Instance Controllers
			initialiseText += LF + "   /* Trigger Instance Initialisation */" + LF;
			for (SM_DeployedTrigInst depTrigInst : pd.getDeployedTrigInsts()) {
				initialiseText += "   " + depTrigInst.getCompInstance().getName() + "_" + depTrigInst.getTrigInstance().getName() + "_Controller__Initialise();" + LF;

				// Add to the list of includes to generate
				includeList.add(depTrigInst.getCompInstance().getName() + "_" + depTrigInst.getTrigInstance().getName() + "_Controller");
			}

			// Initialise Dynamic Trigger Instance Controllers
			initialiseText += LF + "   /* Dynamic Trigger Instance Initialisation */" + LF;
			for (SM_DeployedTrigInst depTrigInst : pd.getDeployedDynTrigInsts()) {
				initialiseText += "   " + depTrigInst.getCompInstance().getName() + "_" + depTrigInst.getDynTrigInstance().getName() + "_Controller__Initialise();" + LF;

				// Add to the list of includes to generate
				includeList.add(depTrigInst.getCompInstance().getName() + "_" + depTrigInst.getDynTrigInstance().getName() + "_Controller");
			}

			// Start Module Instance Controllers
			initialiseText += LF + "   /* Module Instance - Start */" + LF;
			for (SM_DeployedModInst depModInst : pd.getDeployedModInsts()) {
				// Determine the thread priority
				Integer priority = depModInst.getPriority();

				String modQueueThreadName = null;
				if (depModInst.getModInstance().getName().length() < 12) {
					modQueueThreadName = depModInst.getModInstance().getName() + "_MQ";
				} else {
					modQueueThreadName = depModInst.getModInstance().getName().substring(0, 12) + "_MQ";
				}

				initialiseText += "   " + pd.getName() + "_startThread(" + depModInst.getCompInstance().getName() + "_" + depModInst.getModInstance().getName() + "_Controller__ProcessQueue, " + priority.toString() + ", \"" + modQueueThreadName + "\", 0);" + LF;
			}

			// Start Trigger Instance Controllers
			initialiseText += LF + "   /* Trigger Instance - Start */" + LF;
			for (SM_DeployedTrigInst depTrigInst : pd.getDeployedTrigInsts()) {
				Integer priority = depTrigInst.getPriority();
				int triggerFuncNum = 0;
				for (SM_EventLink eventLink : depTrigInst.getTrigInstance().getEventLinks()) {
					// Start a thread for each trigger entry point (one per
					// distinct/different period).
					String trigOpThreadName = null;
					if (depTrigInst.getTrigInstance().getName().length() < 12) {
						trigOpThreadName = depTrigInst.getTrigInstance().getName() + "_TO";
					} else {
						trigOpThreadName = depTrigInst.getTrigInstance().getName().substring(0, 12) + "_TO";
					}

					initialiseText += "   " + pd.getName() + "_startThread(" + depTrigInst.getCompInstance().getName() + "_" + depTrigInst.getTrigInstance().getName() + "_Controller__Trigger_" + triggerFuncNum + ", " + priority.toString() + ", \"" + trigOpThreadName + "\", 0);" + LF;

					triggerFuncNum++;
				}

				// Start a thread for each trigger entry point (one per
				// distinct/different period).
				String trigQueueThreadName = null;
				if (depTrigInst.getTrigInstance().getName().length() < 12) {
					trigQueueThreadName = depTrigInst.getTrigInstance().getName() + "_TQ";
				} else {
					trigQueueThreadName = depTrigInst.getTrigInstance().getName().substring(0, 12) + "_TQ";
				}

				// Start the process queue thread for the trigger (only one per
				// trigger instance object).
				initialiseText += "   " + pd.getName() + "_startThread(" + depTrigInst.getCompInstance().getName() + "_" + depTrigInst.getTrigInstance().getName() + "_Controller__ProcessQueue, " + priority.toString() + ", \"" + trigQueueThreadName + "\", 0);" + LF;
			}

			// Start Dynamic Trigger Instance Controllers
			initialiseText += LF + "   /* Dynamic Trigger Instance - Start */" + LF;
			for (SM_DeployedTrigInst depTrigInst : pd.getDeployedDynTrigInsts()) {
				Integer priority = depTrigInst.getPriority();
				//
				// Start a process thread for each dynamic trigger.
				String trigQueueThreadName = null;
				if (depTrigInst.getDynTrigInstance().getName().length() < 12) {
					trigQueueThreadName = depTrigInst.getDynTrigInstance().getName() + "_DQ";
				} else {
					trigQueueThreadName = depTrigInst.getDynTrigInstance().getName().substring(0, 12) + "_DQ";
				}
				// Start the process queue thread for the trigger (only one per
				// trigger instance object).
				initialiseText += "   " + pd.getName() + "_startThread(" + depTrigInst.getCompInstance().getName() + "_" + depTrigInst.getDynTrigInstance().getName() + "_Controller__ProcessQueue, " + priority.toString() + ", \"" + trigQueueThreadName + "\", 0);" + LF;
			}
			initialiseText += LF;

			// Start the ELI receiver thread
			includeList.add(pd.getName() + "_ELI_Support");
			includeList.add("PD_IDS");

			initialiseText += "   /* Delay so that Module Instances can get to RUNNING state */" + LF + "   " + pd.getName() + "_delay(0, 200000);" + LF + LF +

					"   /* Initialise ELI Support */" + LF + "   " + pd.getName() + "_ELI_Support__Initialise();" + LF + LF;

			for (SM_ProtectionDomain remotePD : pd.getListOfPDsCommunicateWith()) {
				includeList.add(pd.getName() + "_VC_IDS");
				initialiseText += "   /*  Start the ELI receiver - passing in the VC ID to receive from */" + LF + "   int *vcID_" + remotePD.getName() + " = (int *)malloc(sizeof(int));" + LF + "   *vcID_" + remotePD.getName() + " = VC_IDS__" + remotePD.getName().toUpperCase() + "_RECEIVE;" + LF + "   " + pd.getName() + "_startThread(" + pd.getName() + "_ELI_Support__ReceiveELIMessage, " + underlyingPlatformInstantiation.getELIReceiverPriority() + ", \"ELI_Rx\", (void *)vcID_" + remotePD.getName() + ");" + LF + LF;
			}

			// Start the ELI receive thread for Platform Manager comms.
			includeList.add(pd.getName() + "_VC_IDS");
			initialiseText += "   /*  Start the ELI receiver - passing in the VC ID to receive from */" + LF + "   int *vcID_pfManager = (int *)malloc(sizeof(int));" + LF + "   *vcID_pfManager = VC_IDS__PLATFORM_MANAGER_RECEIVE;" + LF + "   " + pd.getName() + "_startThread(" + pd.getName() + "_ELI_Support__ReceiveELIMessage, " + underlyingPlatformInstantiation.getELIReceiverPriority() + ", \"ELI_Rx\", (void *)vcID_pfManager);" + LF + LF;

			// Declare the PD as UP
			includeList.add(pd.getName() + "_PD_Manager");
			includeList.add("ecoaLog");

			initialiseText += "   /* Send \"DOWN\" status to other protection domains*/" + LF + LF +

					generateSendPDStatusCalls("   ") + LF +

					"   /* Declare the protection domain as \"UP\" */" + LF + "   " + pd.getName() + "_PD_Manager__Set_PD_Status(PD_IDS__" + pd.getName().toUpperCase() + ", ELI_Message__PlatformStatus_UP);" + LF + LF +

					"   unsigned char buffer[255];" + LF + "   int size;" + LF + "   size = sprintf((char *)buffer, \"alive - sent PD status\\n\");" + LF + LF +

					"   /* Enter an infinite loop periodically sending PD status */" + LF + "   while (1)" + LF + "   {" + LF + LF +

					generateSendPDStatusCalls("      ") + LF +

					"      ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_LEVEL_1, 0);" + LF + "      " + pd.getName() + "_delay(5, 0);" + LF + "   }" + LF;

			initialiseText += "}" + LF;

		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);
	}

	private String generateSendPDStatusCalls(String indent) {
		String pdStatusCallText = "";

		for (SM_ProtectionDomain remotePD : pd.getListOfPDsCommunicateWith()) {
			pdStatusCallText += indent + pd.getName() + "_PD_Manager__Send_PD_Status(PD_IDS__" + remotePD.getName().toUpperCase() + ");" + LF;
		}
		// Always send to teh Platform Manager!
		pdStatusCallText += indent + pd.getName() + "_PD_Manager__Send_PD_Status(PD_IDS__" + pd.getLogicalComputingNode().getLogicalComputingPlatform().getName().toUpperCase() + ");" + LF;

		return pdStatusCallText;
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + pd.getName() + "_PD_Controller.h */" + LF + "/* This is the main entry point for the ECOA application */" + LF;
		} else {
			preambleText += "/* File " + pd.getName() + "_PD_Controller.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeSequenceNumDecl() {
		String sequenceNumDeclText = "/* Sequence Number Declaration */" + LF + "static ECOA__uint32 Sequence_Number = 1;" + LF + "static int Sequence_Number_Access_Semaphore;" + LF;

		// Replace the #SEQUENCE_NUM_SEMAPHORE_DECL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SEQUENCE_NUM_SEMAPHORE_DECL#", sequenceNumDeclText);
	}

}
