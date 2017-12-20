/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.timereventhandler;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance.WireRank;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ServerInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ServerService;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventSentOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestSentOp;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedModInst;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedTrigInst;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.qos.SM_QualityOfService;
import com.iawg.ecoa.systemmodel.qos.SM_RequestResponseQoS;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;
import com.iawg.ecoa.systemmodel.uid.SM_UIDServiceOp;

public class TimerEventHandlerWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_151 = "void ";
	private static final String SEP_PATTERN = "                     ";
	private boolean isHeader;
	private SM_ProtectionDomain protectionDomain;
	private Generic_Platform underlyingPlatform;
	private String timerHandlerName;
	private String timerManName;

	private ArrayList<String> includeList = new ArrayList<String>();

	public TimerEventHandlerWriterC(PlatformGenerator platformGenerator, boolean isHeader, Path outputDir, SM_ProtectionDomain protectionDomain) {
		super(outputDir);
		this.isHeader = isHeader;
		this.protectionDomain = protectionDomain;
		this.underlyingPlatform = platformGenerator.getunderlyingPlatformInstantiation();

		this.timerHandlerName = protectionDomain.getName() + "_Timer_Event_Handler";
		this.timerManName = protectionDomain.getName() + "_Timer_Event_Manager";

		setFileStructure();
	}

	private void generateHandleRequestQoSTimeout() {
		includeList.add(protectionDomain.getName() + "_Service_Op_UID");

		String requestQOSTimeoutString = "static void handleRequestQoSTimeout(ECOA__uint32 serviceOpUID, ECOA__uint32 seqNum)" + LF + "{" + LF + "   ECOA__timestamp timestamp;" + LF + "   ECOA__return_status responseStatus = ECOA__return_status_NO_RESPONSE;" + LF + LF +

				"   switch (serviceOpUID)" + LF + "   {" + LF;

		for (SM_ComponentInstance ci : protectionDomain.getComponentInstances()) {
			// For now only handle required services...
			for (SM_QualityOfService reqQoS : ci.getImplementation().getRequiredQOSMap().values()) {
				SM_ServiceInstance reqService = reqQoS.getReferencedServiceInstance();

				for (WireRank wireRank : ci.getSourceWiresByRank(reqService)) {
					for (SM_RequestResponseQoS rrQoS : reqQoS.getRequestResponseQoSMap().values()) {
						SM_Wire wire = wireRank.getWire();
						SM_RRServiceOp rrOp = rrQoS.getReferenceRRServiceOp();
						SM_UIDServiceOp uid = wire.getUID(rrOp);

						requestQOSTimeoutString += "      case " + uid.getUIDDefString() + " :" + LF + "      {" + LF;

						// Define local variables for the response output params
						for (SM_OperationParameter opParam : rrOp.getOutputs()) {
							requestQOSTimeoutString += "         " + CLanguageSupport.writeType(opParam.getType()) + " " + opParam.getName() + ";" + LF + "         default_" + CLanguageSupport.writeType(opParam.getType()) + "(&" + opParam.getName() + ");" + LF;
						}

						requestQOSTimeoutString += LF + "         ECOA_setTimestamp(&timestamp);" + LF + "         // Invoke the response received service operation to notify of timeout." + LF + "         " + ci.getName() + "_" + reqService.getName() + "_" + rrOp.getName() + "__response_received(serviceOpUID, &timestamp, responseStatus, seqNum";

						for (SM_OperationParameter opParam : rrOp.getOutputs()) {

							requestQOSTimeoutString += ", &" + opParam.getName();
						}
						requestQOSTimeoutString += ");" + LF + LF +

								"         break;" + LF + "      }" + LF;
					}
				}
			}

		}

		requestQOSTimeoutString +=
				// end switch on serviceOpUID
				"  }" + LF + "}" + LF;

		// Replace the #HANDLE_REQUEST_QOS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#HANDLE_REQUEST_QOS#", requestQOSTimeoutString);
	}

	private void generateHandleRequestTimeout() {
		String requestTimeoutString = "static void handleRequestTimeout(ECOA__uint32 compInstID, ECOA__uint32 modInstID, ECOA__uint32 operationID, ECOA__uint32 seqNum)" + LF + "{" + LF + "   ECOA__timestamp timestamp;" + LF + "   ECOA__return_status responseStatus = ECOA__return_status_NO_RESPONSE;" + LF + LF +

				"   switch (compInstID)" + LF + "   {" + LF;

		for (SM_ComponentInstance ci : protectionDomain.getComponentInstances()) {
			includeList.add("Component_Instance_ID");
			includeList.add(ci.getImplementation().getName() + "_Module_Instance_ID");
			includeList.add(ci.getImplementation().getName() + "_Module_Instance_Operation_UID");

			requestTimeoutString += "      case CI_" + ci.getName().toUpperCase() + "_ID :" + LF + "      {" + LF + "         switch (modInstID)" + LF + "         {" + LF;

			for (SM_DeployedModInst mi : ci.getDeployedModInsts()) {
				requestTimeoutString += "            case " + ci.getImplementation().getName().toUpperCase() + "_" + mi.getModInstance().getName().toUpperCase() + "_ID : " + LF + "            {" + LF + "               switch (operationID)" + LF + "               {" + LF;

				for (SM_RequestSentOp reqSent : mi.getModInstance().getModuleType().getRequestSentOps()) {

					String modInstContName = ci.getName() + "_" + mi.getModInstance().getName() + "_Controller";
					includeList.add(modInstContName);

					requestTimeoutString += "                  case " + ci.getImplementation().getName().toUpperCase() + "_" + mi.getModInstance().getName().toUpperCase() + "_" + reqSent.getName().toUpperCase() + "_UID : " + LF + "                  {" + LF;

					// Define local variables for the response output params
					for (SM_OperationParameter opParam : reqSent.getOutputs()) {

						includeList.add("Defaulter");

						requestTimeoutString += SEP_PATTERN + CLanguageSupport.writeType(opParam.getType()) + " " + opParam.getName() + ";" + LF + "                     default_" + CLanguageSupport.writeType(opParam.getType()) + "(&" + opParam.getName() + ");" + LF;
					}

					SM_ServerInterface so = mi.getModInstance().getRequestLinkForClientOp(reqSent).getServer();
					if (so instanceof SM_ServerService) {
						includeList.add(ci.getName() + "_" + so.getServerInst().getName() + "_Controller");

						requestTimeoutString += LF + "                     // Remove client ID." + LF + SEP_PATTERN + ci.getName() + "_" + so.getServerInst().getName() + "__Remove_Client_Lookup(seqNum);";

					}

					requestTimeoutString += LF + "                     ECOA_setTimestamp(&timestamp);" + LF + "                     // Invoke the response received operation to notify of timeout." + LF + SEP_PATTERN + modInstContName + "__" + reqSent.getName() + "__response_received(&timestamp, &responseStatus, seqNum";

					for (SM_OperationParameter opParam : reqSent.getOutputs()) {
						requestTimeoutString += ", &" + opParam.getName();
					}
					requestTimeoutString += ");" + LF + "                     break;" + LF + "                  }" + LF;
				}

				requestTimeoutString +=
						// close operation id switch
						"               }" + LF +
						// close mod instance case
								"               break;" + LF +

								"            }" + LF;
			}

			requestTimeoutString +=
					// close switch on mod instance
					"         }" + LF +
					// close comp instance case
							"         break;" + LF + "      }" + LF;

		}

		requestTimeoutString +=
				// close switch on comp instance
				"   }" + LF +
				// close function
						"}" + LF;

		// Replace the #HANDLE_REQUEST# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#HANDLE_REQUEST#", requestTimeoutString);
	}

	private void generateHandleDynTrigTimer() {
		String requestTimeoutString = "static void handleDynTrigTimeout(ECOA__uint32 compInstID, ECOA__uint32 dynTrigInstID, ECOA__uint32 operationID, ECOA__uint32 seqNum, void* timerArgs )" + LF + "{" + LF + "   ECOA__timestamp timestamp;" + LF + "   ECOA__return_status responseStatus = ECOA__return_status_NO_RESPONSE;" + LF + LF +

				"   switch (compInstID)" + LF + "   {" + LF;

		for (SM_ComponentInstance ci : protectionDomain.getComponentInstances()) {
			includeList.add("Component_Instance_ID");
			includeList.add(ci.getImplementation().getName() + "_Module_Instance_ID");
			includeList.add(ci.getImplementation().getName() + "_Module_Instance_Operation_UID");

			requestTimeoutString += "      case CI_" + ci.getName().toUpperCase() + "_ID :" + LF + "      {" + LF + "         switch (dynTrigInstID)" + LF + "         {" + LF;

			for (SM_DeployedTrigInst mi : ci.getDeployedTrigInsts()) {
				if (mi.getDynTrigInstance() != null) {
					requestTimeoutString += "            case " + ci.getImplementation().getName().toUpperCase() + "_" + mi.getDynTrigInstance().getName().toUpperCase() + "_ID : " + LF + "            {" + LF + "               switch (operationID)" + LF + "               {" + LF;

					for (SM_EventSentOp evSent : mi.getDynTrigInstance().getModuleType().getEventSentOps()) {
						requestTimeoutString += "                  case " + ci.getImplementation().getName().toUpperCase() + "_" + mi.getDynTrigInstance().getName().toUpperCase() + "_" + evSent.getName().toUpperCase() + "_UID : " + LF + "                  {" + LF;

						requestTimeoutString += LF + "                     ECOA_setTimestamp(&timestamp);" + LF + "                     // Invoke the response received operation to notify of timeout." + LF + SEP_PATTERN + ci.getName() + "_" + mi.getDynTrigInstance().getName() + "__" + evSent.getName() + "__send(&timestamp, timerArgs";

						requestTimeoutString += ");" + LF + "                     break;" + LF + "                  }" + LF;
					}
					requestTimeoutString +=
							// close operation id switch
							"               }" + LF +
							// close dyntrig instance case
									"               break;" + LF +

									"            }" + LF;
				}
			}

			requestTimeoutString +=
					// close switch on dyntrig instance
					"         }" + LF +
					// close comp instance case
							"         break;" + LF + "      }" + LF;

		}
		requestTimeoutString +=
				// close switch on comp instance
				"   }" + LF +
				// close function
						"}" + LF;

		// Replace the #HANDLE_DYNTRIGS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#HANDLE_DYNTRIGS#", requestTimeoutString);
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve(timerHandlerName + ".h"));
		} else {
			super.openFile(outputDir.resolve(timerHandlerName + ".c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#EVENT_HANDLER#" + LF + "#INITIALISE#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#HANDLE_REQUEST#" + LF + "#HANDLE_REQUEST_QOS#" + LF + "#HANDLE_DYNTRIGS#" + LF + "#EVENT_HANDLER#" + LF + "#INITIALISE#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeEventHandler() {

		String eventHandlerText = "";

		if (isHeader) {
			eventHandlerText += SEP_PATTERN_151 + timerHandlerName + "__Event_Handler();" + LF + LF;
		} else {
			// generate the request handlers...
			generateHandleRequestTimeout();
			generateHandleRequestQoSTimeout();
			generateHandleDynTrigTimer();

			// Generate the handler function.
			eventHandlerText += SEP_PATTERN_151 + timerHandlerName + "__Event_Handler()" + LF + "{" + LF + LF +

					"   Event_Type event;" + LF + "   unsigned int timerID;" + LF + "   Timer_Type type;" + LF + "   ECOA__uint32 compInstID, modInstID, operationID, serviceOpUID, seqNum;" + LF + "   ECOA__return_status erc;" + LF + "   WFE_Status_Type status;" + LF + "   Timer_Manager_Error_Type gtStatus;" + LF + "   void* timerArgs;" + LF + LF +

					"   while (1)" + LF + "   {" + LF + "      Wait_For_Event(&event, &timerID, &status);" + LF +

					"      if (status == Wait_For_Event_OK)" + LF + "      {" + LF + "         // Need to get the associated information for this timer..." + LF + "         gtStatus = " + timerManName + "__Get_Timer(timerID, &type, &compInstID, &modInstID, &operationID, &serviceOpUID, &seqNum, &timerArgs);" + LF + LF +

					"         if (gtStatus == Timer_Manager_Error_Type_OK)" + LF + "         {" + LF + "//          printf(\"timer expired %d\\n\", timerID);" + LF + "            switch (type)" + LF + "            {" + LF + "               case REQUEST_QOS_TIMEOUT :" + LF + "                  handleRequestQoSTimeout(serviceOpUID, seqNum);" + LF + "                  break;" + LF + "               case REQUEST_TIMEOUT :" + LF + "                  handleRequestTimeout(compInstID, modInstID, operationID, seqNum);" + LF + "                  break;" + LF + "               case DYNTRIG_TIMER :" + LF + "                  handleDynTrigTimeout(compInstID, modInstID, operationID, seqNum, timerArgs);" + LF + "                  break;" + LF +
					// close timer type switch
					"            }" + LF + "            /* Delete the timer */" + LF + "            " + protectionDomain.getName() + "_Timer_Event_Manager__Delete_Timer_ByID(timerID, &erc);" + LF +
					// close if on get timer status.
					"         }" + LF + "         else" + LF + "         {" + LF + "            printf(\"INFO - CAUGHT TIMER EXPIRED BUT RESPONSE ALREADY RECEIVED - timerID %d\\n\", timerID);" + LF + "         }" + LF + "      }" + LF + "      else" + LF + "      {" + LF + "         printf(\"Wait_For_Event failed - status = %d\\n\", (long)status);" + LF + "      }" + LF +

					"   }" + LF + "}" + LF + LF;
		}

		// Replace the #EVENT_HANDLER# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#EVENT_HANDLER#", eventHandlerText);
	}

	public void writeIncludes() {
		if (!isHeader) {
			includeList.add(timerManName);
			includeList.add(protectionDomain.getName() + "_Service_Op_UID");
			includeList.addAll(underlyingPlatform.addIncludesTimerEventHandlerBody());
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeInitialise(Generic_Platform underlyingPlatformInstantiation) {
		String initialiseText = "";

		if (isHeader) {
			initialiseText += SEP_PATTERN_151 + timerHandlerName + "__Initialise();" + LF + LF;
		} else {
			// Generate the Initialise function.
			initialiseText += SEP_PATTERN_151 + timerHandlerName + "__Initialise()" + LF + "{" + LF + LF + "   // TODO - do we need any initialisation for timer event handler?!" + LF + "}" + LF;
		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + timerHandlerName + ".h */" + LF + "/* This is the timer event handler functionality */" + LF;
		} else {
			preambleText += "/* File " + timerHandlerName + ".c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

}
