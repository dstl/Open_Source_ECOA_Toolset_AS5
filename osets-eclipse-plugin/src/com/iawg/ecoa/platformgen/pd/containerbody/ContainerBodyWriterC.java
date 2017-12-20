/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.containerbody;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.TypesProcessorC;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.common.underlyingplatform.C_Posix;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ILIMessage;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ModuleInstanceILI;
import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstanceProperty;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstanceProperty;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleType;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleTypeProperty;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_Pinfo;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_VDRepository;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_DataLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_ReaderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_ReaderService;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverService;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ClientInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ClientModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ClientService;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_RequestLink;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataReadOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataWrittenOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventSentOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestReceivedOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestSentOp;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class ContainerBodyWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_551 = "void ";
	private static final String SEP_PATTERN_A = "   // Need to null terminate the string";
	private static final String SEP_PATTERN_B = "__context *context,";
	private static final String SEP_PATTERN_C = "      Exit_Thread(0);";
	private static final String SEP_PATTERN_D = "         {";
	private static final String SEP_PATTERN_E = "      if (privateContext->moduleInstanceID == ";
	private static final String SEP_PATTERN_F = "__Set_Availability(ECOA__FALSE);";
	private static final String SEP_PATTERN_G = "__context *context)";
	private static final String SEP_PATTERN_H = "   ECOA_setTimestamp(&timestamp);";
	private static final String SEP_PATTERN_I = "_container__";
	private static final String SEP_PATTERN_J = "nodeName";
	private static final String SEP_PATTERN_K = "         ";
	private static final String SEP_PATTERN_L = "()\\n\");";
	private static final String SEP_PATTERN_M = "      ";
	private static final String SEP_PATTERN_N = "         /* Call the Service API */";
	private static final String SEP_PATTERN_O = "    ECOA__boolean8 isWarmRestart)";
	private static final String SEP_PATTERN_P = "            return ";
	private static final String SEP_PATTERN_Q = "   ECOA__timestamp timestamp;";
	private static final String SEP_PATTERN_R = "Client_Info_Type";
	private static final String SEP_PATTERN_S = "               break;";
	private static final String SEP_PATTERN_T = "_Controller__Reinitialise(ECOA__FALSE);";
	private static final String SEP_PATTERN_U = "\\\":\\\"%s\\\"\\n\\0\", ";
	private static final String SEP_PATTERN_V = "_Module_Instance_Operation_UID";
	private static final String SEP_PATTERN_W = "__context* context, const ECOA__log log)";
	private static final String SEP_PATTERN_X = "_Controller__INITIALIZE_received(&timestamp);";
	private static final String SEP_PATTERN_Y = "ECOA__return_status ";
	private static final String SEP_PATTERN_Z = "_handle *data_handle)";
	private static final String SEP_PATTERN_AA = "         return ";
	private static final String SEP_PATTERN_AB = "         break;";
	private static final String SEP_PATTERN_AC = "   int bufLen, size;";
	private static final String SEP_PATTERN_AD = "   localLogData.current_size = log.current_size;";
	private static final String SEP_PATTERN_AE = "   localLogData.data[localLogData.current_size] = '\\0';";
	private static final String SEP_PATTERN_AF = "   /* Timestamp point */";
	private static final String SEP_PATTERN_AG = "__context* context,";
	private static final String SEP_PATTERN_AH = "   ECOA__log localLogData;";
	private static final String SEP_PATTERN_AI = "#TIME_SERVICES#";
	private static final String SEP_PATTERN_AJ = "_container__performPDShutdown(context, asset_id);";
	private static final String SEP_PATTERN_AK = "   strncpy(localLogData.data, log.data, localLogData.current_size);";
	private static final String SEP_PATTERN_AL = "   return ECOA__return_status_OK;";
	private static final String SEP_PATTERN_AM = "_Controller";
	private static final String SEP_PATTERN_AN = "_Platform_Manager__Set_Platform_Status(";
	private static final String SEP_PATTERN_AO = "    ECOA__asset_id asset_id)";
	private static final String SEP_PATTERN_AP = "    ECOA__asset_id asset_id,";
	private static final String SEP_PATTERN_AQ = "      }";
	private static final String SEP_PATTERN_AR = "\\\":\\\"";
	private static final String SEP_PATTERN_AS = ", localLogData.data);";
	private static final String SEP_PATTERN_AT = "_Controller__";
	private static final String SEP_PATTERN_AU = "         }";
	private static final String SEP_PATTERN_AV = "   return ECOA__return_status_OPERATION_NOT_AVAILABLE;";
	private static final String SEP_PATTERN_AW = "      {";
	private static final String SEP_PATTERN_AX = "ECOA__FaultHandler";
	private static final String SEP_PATTERN_AY = "   unsigned char buffer[512];";
	private static final String SEP_PATTERN_AZ = "         /* Call the Module Instance Queue Operation */";
	private static final String SEP_PATTERN_BA = "   if (privateContext->componentInstanceID == CI_";
	private static final String SEP_PATTERN_BB = "   printf(\"<<<<<<< ";
	private static final String SEP_PATTERN_BC = "   // Get system time";

	private HashMap<SM_ModuleInstance, ModuleInstanceILI> modInstILIMap;
	private ArrayList<SM_ComponentInstance> compInstList = new ArrayList<SM_ComponentInstance>();
	private SM_ProtectionDomain pd;

	private ArrayList<String> includeList = new ArrayList<String>();
	private Generic_Platform underlyingPlatform;
	private SM_ModuleImpl moduleImpl;
	private SM_ModuleType moduleType;
	private SM_ComponentImplementation compImpl;
	private PlatformGenerator platformGenerator;
	private SM_ProtectionDomain protectionDomain;

	public ContainerBodyWriterC(PlatformGenerator platformGenerator, SM_ProtectionDomain pd, SM_ModuleImpl moduleImpl, HashMap<SM_ModuleInstance, ModuleInstanceILI> modInstILIMap) {
		super(platformGenerator.getPdOutputDir());
		this.platformGenerator = platformGenerator;
		this.underlyingPlatform = platformGenerator.getunderlyingPlatformInstantiation();
		this.moduleImpl = moduleImpl;
		this.moduleType = moduleImpl.getModuleType();
		this.compImpl = moduleImpl.getComponentImplementation();
		this.protectionDomain = pd;

		this.modInstILIMap = modInstILIMap;
		this.pd = pd;

		// Populate the component instances list
		for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
			if (compInst.getImplementation().equals(compImpl)) {
				compInstList.add(compInst);
			}
		}

		setFileStructure();
	}

	private String completeLogFunction(String messageLevel) {
		String nodeName = SEP_PATTERN_J;

		String errorFaultFunctionString = "{" + LF + SEP_PATTERN_AY + LF + SEP_PATTERN_AC + LF + LF;

		if (moduleImpl.isInstrument()) {
			errorFaultFunctionString += SEP_PATTERN_BB + moduleImpl.getName() + "_container__log_";

			if (messageLevel.equals("TRACE")) {
				errorFaultFunctionString += "trace()\\n\");" + LF + LF;
			} else if (messageLevel.equals("DEBUG")) {
				errorFaultFunctionString += "debug()\\n\");" + LF + LF;
			} else if (messageLevel.equals("INFO")) {
				errorFaultFunctionString += "info()\\n\");" + LF + LF;
			} else if (messageLevel.equals("WARNING")) {
				errorFaultFunctionString += "warning()\\n\");" + LF + LF;
			}
		}

		errorFaultFunctionString += SEP_PATTERN_A + LF + SEP_PATTERN_AH + LF + SEP_PATTERN_AD + LF + SEP_PATTERN_AK + LF + SEP_PATTERN_AE + LF + LF +

				SEP_PATTERN_BC + LF + underlyingPlatform.generateGALTAttributes() + underlyingPlatform.generateGALT() + underlyingPlatform.checkGALTStatusCALL_ONLY() +
				// TODO - should work out the module instance ID and pass
				// through to ecoaLog - need to inspect private context!
				"      size = sprintf((char*)buffer, (char*)\"" + "\\\"%li seconds, %li nanoseconds\\\":0:\\\"" + messageLevel + SEP_PATTERN_AR + nodeName + SEP_PATTERN_AR + protectionDomain.getName() + SEP_PATTERN_U + underlyingPlatform.getTimeSecondsVar() + ", " + underlyingPlatform.getTimeNanoVar() + SEP_PATTERN_AS + LF + "      ecoaLog(buffer, size, LOG_LEVEL_" + messageLevel + ", 0);" + LF + "   }" + LF + "}" + LF + LF;

		return errorFaultFunctionString;
	}

	private String completeRaiseError() {
		String nodeName = SEP_PATTERN_J;

		String raiseErrorString = "{" + LF +
		// Always log the error
				SEP_PATTERN_AY + LF + SEP_PATTERN_AC + LF + LF;

		if (moduleImpl.isInstrument()) {
			raiseErrorString += SEP_PATTERN_BB + moduleImpl.getName() + "_container__raise_error()\\n\");" + LF + LF;
		}

		raiseErrorString += SEP_PATTERN_A + LF + SEP_PATTERN_AH + LF + SEP_PATTERN_AD + LF + SEP_PATTERN_AK + LF + SEP_PATTERN_AE + LF + LF +

				SEP_PATTERN_BC + LF + underlyingPlatform.generateGALTAttributes() + underlyingPlatform.generateGALT() + underlyingPlatform.checkGALTStatusCALL_ONLY() +
				// TODO - should work out the module instance ID and pass
				// through to ecoaLog - need to inspect private context!
				"      size = sprintf((char*)buffer, \"" + "\\\"%i seconds, %i nanoseconds\\\":0:ERROR:" + nodeName + SEP_PATTERN_AR + protectionDomain.getName() + SEP_PATTERN_U + underlyingPlatform.getTimeSecondsVar() + ", " + underlyingPlatform.getTimeNanoVar() + SEP_PATTERN_AS + LF + "      ecoaLog(buffer, size, LOG_LEVEL_ERROR, 0);" + LF + "   }" + LF + LF;

		// If non-supervision module has called raise_error, raise to
		// supervision.
		if (!moduleType.getIsSupervisor()) {
			raiseErrorString += SEP_PATTERN_Q + LF + LF +

					SEP_PATTERN_AF + LF + SEP_PATTERN_H + LF + LF +

					generateGetPrivateContext();

			for (SM_ComponentInstance compInst : compInstList) {
				String supervisionControllerName = compInst.getName() + "_" + compImpl.getSupervisorModule().getName() + SEP_PATTERN_AM;
				includeList.add(supervisionControllerName);

				raiseErrorString += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

				for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
					raiseErrorString += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF + "         // Invoke the module instance queue operation." + LF + SEP_PATTERN_K + supervisionControllerName + "__Error_Notification_" + modInst.getName() + "(&timestamp, ECOA__module_error_type_ERROR);" + LF + SEP_PATTERN_AQ + LF;
				}

				// End if on component instance
				raiseErrorString += "   }" + LF;
			}
		}
		// if it is not a fault handler, then it must be a supervision module,
		// so raise to the fault handler if possible
		else if (!moduleType.getIsFaultHandler()) {
			// need to find the fault handler
			boolean foundit = false;

			for (SM_ComponentInstance compInst : protectionDomain.getComponentInstances()) {
				for (SM_ModuleInstance modInst : compInst.getImplementation().getModuleInstances().values()) {
					if (modInst.getModuleType().getIsFaultHandler()) {
						includeList.add(SEP_PATTERN_AX);

						raiseErrorString += SEP_PATTERN_Q + LF + LF +

								SEP_PATTERN_AF + LF + SEP_PATTERN_H + LF + LF +

								generateGetPrivateContext() + LF + LF;

						// found it!, now call it!
						String modInstContName = compInst.getName() + "_" + modInst.getName() + SEP_PATTERN_AM;
						includeList.add(modInstContName);

						for (SM_ComponentInstance cInst : compInstList) {
							String supervisionControllerName = cInst.getName() + "_" + compImpl.getSupervisorModule().getName() + SEP_PATTERN_AM;
							includeList.add(supervisionControllerName);

							raiseErrorString += SEP_PATTERN_BA + cInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

							raiseErrorString += SEP_PATTERN_M + modInstContName + "__Error_Notification(0, &timestamp, ECOA__FaultHandler__IDs_CMP_" + cInst.getName() + ", ECOA__asset_type_COMPONENT, ECOA__error_type_ERROR);" + LF + LF;

							// End if on component instance
							raiseErrorString += "   }" + LF;
						}

						foundit = true;
						break;
					}
				}
			}

			if (!foundit) {
				raiseErrorString += "   printf(\"FAULT HANDLER NOT FOUND\\n\");" + LF + LF;
			}
		}
		// if we get here then it was the fault handler - Oh dear....
		else {
			raiseErrorString += "   printf(\"FAULT HANDLER RAISED AN ERROR\\n\");" + LF + LF;
		}

		raiseErrorString += "}" + LF + LF;

		return raiseErrorString;
	}

	private String completeRaiseFatalError() {
		String nodeName = SEP_PATTERN_J;

		String raiseFatalErrorString = "{" + LF +
		// Always log the error
				SEP_PATTERN_AY + LF + SEP_PATTERN_AC + LF + LF;

		if (moduleImpl.isInstrument()) {
			raiseFatalErrorString += SEP_PATTERN_BB + moduleImpl.getName() + "_container__raise_error()\\n\");" + LF + LF;
		}

		raiseFatalErrorString += SEP_PATTERN_A + LF + SEP_PATTERN_AH + LF + SEP_PATTERN_AD + LF + SEP_PATTERN_AK + LF + SEP_PATTERN_AE + LF + LF +

				SEP_PATTERN_BC + LF + underlyingPlatform.generateGALTAttributes() + underlyingPlatform.generateGALT() + underlyingPlatform.checkGALTStatusCALL_ONLY() +
				// TODO - should work out the module instance ID and pass
				// through to ecoaLog - need to inspect private context!
				"      size = sprintf((char*)buffer, \"" + "\\\"%i seconds, %i nanoseconds\\\":0:FATAL:" + nodeName + SEP_PATTERN_AR + protectionDomain.getName() + SEP_PATTERN_U + underlyingPlatform.getTimeSecondsVar() + ", " + underlyingPlatform.getTimeNanoVar() + SEP_PATTERN_AS + LF + "      ecoaLog(buffer, size, LOG_LEVEL_FATAL, 0);" + LF + "   }" + LF + LF +

				SEP_PATTERN_Q + LF + LF +

				SEP_PATTERN_AF + LF + SEP_PATTERN_H + LF + LF +

				generateGetPrivateContext();

		for (SM_ComponentInstance compInst : compInstList) {
			raiseFatalErrorString += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

			for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
				if (!moduleType.getIsSupervisor()) {
					String supervisionControllerName = compInst.getName() + "_" + compImpl.getSupervisorModule().getName() + SEP_PATTERN_AM;
					includeList.add(supervisionControllerName);

					raiseFatalErrorString += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF + "         // Invoke the module instance queue operation." + LF + SEP_PATTERN_K + supervisionControllerName + "__Error_Notification_" + modInst.getName() + "(&timestamp, ECOA__module_error_type_FATAL_ERROR);" + LF + SEP_PATTERN_AQ + LF + LF +

							"      // Destroy the module thread." + LF + SEP_PATTERN_C + LF + LF;
				} else if (!moduleType.getIsFaultHandler()) {
					// Supervision module has raised a fatal error
					raiseFatalErrorString += "      // \"Shutdown\" any non-supervision modules/triggers - i.e. set IDLE / clear the queues" + LF;
					for (SM_Object modTrig : compImpl.getNonSupervisionModuleTriggerInstances()) {
						raiseFatalErrorString += SEP_PATTERN_M + compInst.getName() + "_" + modTrig.getName() + SEP_PATTERN_T + LF;
					}

					raiseFatalErrorString += LF + "      // Set any provided services unavailable" + LF;
					for (SM_ServiceInstance provService : compImpl.getCompType().getServiceInstancesList()) {
						raiseFatalErrorString += SEP_PATTERN_M + compInst.getName() + "_" + provService.getName() + SEP_PATTERN_F + LF;
					}

					// at this point we need to invoke the "error handler" -
					// whatever that is...
					// need to find the fault handler

					boolean foundit = false;
					for (SM_ComponentInstance cInst : protectionDomain.getComponentInstances()) {
						for (SM_ModuleInstance mInst : cInst.getImplementation().getModuleInstances().values()) {
							if (mInst.getModuleType().getIsFaultHandler()) {
								// found it!, now call it!
								String modInstContName = cInst.getName() + "_" + mInst.getName() + SEP_PATTERN_AM;
								includeList.add(modInstContName);

								raiseFatalErrorString += SEP_PATTERN_M + modInstContName + "__Error_Notification(0, &timestamp, ECOA__FaultHandler__IDs_CMP_" + compInst.getName() + ", ECOA__asset_type_COMPONENT, ECOA__error_type_FATAL_ERROR);" + LF + LF;

								foundit = true;
								break;
							}
						}
					}

					if (!foundit) {
						raiseFatalErrorString += "      printf(\"FAULT HANDLER NOT FOUND\\n\");" + LF;

					}

					// Destroy the supervision module thread.
					raiseFatalErrorString += LF + "      // Destroy the module thread." + LF + SEP_PATTERN_C + LF + LF;
				}
				// if we get here then it was the fault handler - Oh dear....
				else {
					raiseFatalErrorString += "      printf(\"FAULT HANDLER RAISED A FATAL ERROR ERROR\\n\");" + LF + SEP_PATTERN_C + LF + LF;
				}

			}

			// End if on component instance
			raiseFatalErrorString += "   }" + LF;
		}

		raiseFatalErrorString += "}" + LF + LF;

		return raiseFatalErrorString;
	}

	private String determineEventSentDestsForModInst(SM_EventSentOp eventSentOp, SM_ModuleInstance modInst, SM_ComponentInstance compInst) {
		String outputString = "";

		// Get event links for this module instance
		for (SM_EventLink eventLink : modInst.getEventLinksForSenderOp(eventSentOp)) {
			for (SM_ReceiverInterface receiver : eventLink.getReceivers()) {
				includeList.add(compInst.getName() + "_" + receiver.getReceiverInst().getName() + SEP_PATTERN_AM);
				if (receiver instanceof SM_ReceiverService) {
					outputString += SEP_PATTERN_N + LF + SEP_PATTERN_K + compInst.getName() + "_" + receiver.getReceiverInst().getName() + "_" + receiver.getReceiverOp().getName() + "__event_send(&timestamp";

					for (SM_OperationParameter opParam : eventSentOp.getInputs()) {
						outputString += ", " + opParam.getName();
					}
					outputString += ");" + LF;
				} else if (receiver.getReceiverInst() instanceof SM_ModuleInstance) {
					includeList.add(compImpl.getName() + SEP_PATTERN_V);
					String modUID = compImpl.getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_" + eventSentOp.getName().toUpperCase() + "_UID";

					outputString += "         modUID = " + modUID + ";" + LF + SEP_PATTERN_AZ + LF + SEP_PATTERN_K + compInst.getName() + "_" + receiver.getReceiverInst().getName() + SEP_PATTERN_AT + receiver.getReceiverOp().getName() + "__event_received(&timestamp," + modUID;

					for (SM_OperationParameter opParam : eventSentOp.getInputs()) {
						outputString += ", " + opParam.getName();
					}
					outputString += ");" + LF;
				} else if (receiver.getReceiverInst() instanceof SM_DynamicTriggerInstance) {
					includeList.add(compImpl.getName() + SEP_PATTERN_V);
					String modUID = compImpl.getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_" + eventSentOp.getName().toUpperCase() + "_UID";

					outputString += "         /* Call the Timer Queue Operation */" + LF + SEP_PATTERN_K + compInst.getName() + "_" + receiver.getReceiverInst().getName() + SEP_PATTERN_AT + receiver.getReceiverOp().getName() + "__event_received(&timestamp, " + modUID;
					for (SM_OperationParameter opParam : eventSentOp.getInputs()) {
						outputString += ", " + opParam.getName();
					}
					outputString += ");" + LF;
				}
			}
		}

		return outputString;
	}

	private String determinePropertyValueForModInst(SM_ModuleInstanceProperty modInstProperty, SM_ModuleInstance modInst, SM_ComponentInstance compInst) {
		String outputString = "";

		if (modInstProperty.getRelatedCompTypeProperty() != null) {
			for (SM_ComponentInstanceProperty cip : compInst.getProperties()) {
				if (cip.getComponentTypeProperty().equals(modInstProperty.getRelatedCompTypeProperty())) {
					// TODO - this needs more logic - for now just use literal
					// value.
					// Use Component Instance level property value (which may be
					// referencing an assembly level property!)
					outputString += "         /* Use the literal value of the component  property */" + LF + "         *value = " + cip.getValue() + ";" + LF;
				}
			}
		} else {
			// TODO - this needs more logic - for now just use literal value.
			outputString += "         /* Use the literal value of the module property */" + LF + "         *value = " + modInst.getModInstPropertyByName(modInstProperty.getModuleTypeProperty().getName()).getValue() + ";" + LF;
		}

		return outputString;
	}

	private String determineRequestAsyncDestinations(SM_RequestSentOp requestSentOp, SM_ModuleInstance modInst, SM_ComponentInstance compInst) {
		String outputString = "";

		// Get request links for this module instance
		SM_RequestLink requestLink = modInst.getRequestLinkForClientOp(requestSentOp);

		if (requestLink.getServer().getServerInst() instanceof SM_ServiceInstance) {
			outputString += "      /* Call the Service API */" + LF + SEP_PATTERN_M + compInst.getName() + "_" + ((SM_ServiceInstance) requestLink.getServer().getServerInst()).getName() + "_" + requestLink.getServer().getServerOp().getName() + "__request_send(&timestamp, &moduleClientInfo";

			for (SM_OperationParameter opParam : requestSentOp.getInputs()) {
				outputString += ", " + opParam.getName();
			}
			outputString += ");" + LF;

		} else if (requestLink.getServer().getServerInst() instanceof SM_ModuleInstance) {
			outputString += SEP_PATTERN_AZ + LF + SEP_PATTERN_K + compInst.getName() + "_" + requestLink.getServer().getServerInst().getName() + SEP_PATTERN_AT + requestLink.getServer().getServerOp().getName() + "__request_received(&timestamp, &moduleClientInfo";

			for (SM_OperationParameter opParam : requestSentOp.getInputs()) {
				outputString += ", " + opParam.getName();
			}
			outputString += ");" + LF;
		}

		return outputString;
	}

	private String determineRequestSyncDestsForModInst(SM_RequestSentOp requestSentOp, SM_ModuleInstance modInst, SM_ComponentInstance compInst) {
		String outputString = "";

		// Get request links for this module instance
		SM_RequestLink requestLink = modInst.getRequestLinkForClientOp(requestSentOp);

		includeList.add(compInst.getName() + "_" + requestLink.getServer().getServerInst().getName() + SEP_PATTERN_AM);

		if (requestLink.getServer().getServerInst() instanceof SM_ServiceInstance) {
			outputString += SEP_PATTERN_N + LF + SEP_PATTERN_K + compInst.getName() + "_" + ((SM_ServiceInstance) requestLink.getServer().getServerInst()).getName() + "_" + requestLink.getServer().getServerOp().getName() + "__request_send(&timestamp, &moduleClientInfo";

			for (SM_OperationParameter opParam : requestSentOp.getInputs()) {
				outputString += ", " + opParam.getName();
			}
			outputString += ");" + LF;

		} else if (requestLink.getServer().getServerInst() instanceof SM_ModuleInstance) {
			outputString += SEP_PATTERN_AZ + LF + SEP_PATTERN_K + compInst.getName() + "_" + requestLink.getServer().getServerInst().getName() + SEP_PATTERN_AT + requestLink.getServer().getServerOp().getName() + "__request_received(&timestamp, &moduleClientInfo";

			for (SM_OperationParameter opParam : requestSentOp.getInputs()) {
				outputString += ", " + opParam.getName();
			}
			outputString += ");" + LF;

		}

		return outputString;
	}

	private String determineResponseSendDestsForModInst(SM_RequestReceivedOp requestReceivedOp, SM_ModuleInstance modInst, SM_ComponentInstance compInst) {
		includeList.add(SEP_PATTERN_R);
		includeList.add(compInst.getName() + "_" + modInst.getName() + SEP_PATTERN_AM);
		includeList.add(compImpl.getName() + "_Service_Instance_Operation_UID");
		String outputString = "         /* Get the seq number to client mapping */" + LF + "         Client_Info_Type clientInfo;" + LF + SEP_PATTERN_K + compInst.getName() + "_" + modInst.getName() + "_Controller__Get_Client_Lookup(ID, &clientInfo);" + LF + LF +

				"         if (clientInfo.type == Client_Type__MODULE_OPERATION)" + LF + SEP_PATTERN_D + LF + "            switch (clientInfo.ID)" + LF + "            {" + LF;

		// Get request links for this module instance
		for (SM_RequestLink requestLink : modInst.getRequestLinksForServerOp(requestReceivedOp)) {
			for (SM_ClientInterface clientInterface : requestLink.getClients()) {
				if (clientInterface instanceof SM_ClientModuleInstance) {
					includeList.add(compImpl.getName() + SEP_PATTERN_V);

					outputString += "               case " + compImpl.getName().toUpperCase() + "_" + clientInterface.getClientInst().getName().toUpperCase() + "_" + clientInterface.getClientOp().getName().toUpperCase() + "_UID :" + LF + "                  /* Call the Module Instance Queue Operation */" + LF + "                  " + compInst.getName() + "_" + clientInterface.getClientInst().getName() + SEP_PATTERN_AT + clientInterface.getClientOp().getName() + "__response_received(&timestamp, &responseStatus, ID";

					for (SM_OperationParameter opParam : requestReceivedOp.getOutputs()) {
						if (opParam.getType().isSimple()) {
							outputString += ", &" + opParam.getName();
						} else {
							outputString += ", " + opParam.getName();
						}
					}
					outputString += ");" + LF + "                  break;" + LF;
				}
			}
		}

		outputString += "            }" + LF + SEP_PATTERN_AU + LF + "         else if (clientInfo.type == Client_Type__SERVICE_OPERATION)" + LF + SEP_PATTERN_D + LF + "            switch (clientInfo.ID)" + LF + "            {" + LF;

		// Get request links for this module instance
		for (SM_RequestLink requestLink : modInst.getRequestLinksForServerOp(requestReceivedOp)) {
			for (SM_ClientInterface clientInterface : requestLink.getClients()) {
				if (clientInterface instanceof SM_ClientService) {
					includeList.add(compInst.getName() + "_" + clientInterface.getClientInst().getName() + SEP_PATTERN_AM);

					outputString += "               case " + compImpl.getName().toUpperCase() + "_" + clientInterface.getClientInst().getName().toUpperCase() + "_" + clientInterface.getClientOp().getName().toUpperCase() + "_UID:" + LF + "                  /* Call the Service API */" + LF + "                  status = " + compInst.getName() + "_" + clientInterface.getClientInst().getName() + "_" + clientInterface.getClientOp().getName() + "__response_send(&timestamp, &clientInfo";

					for (SM_OperationParameter opParam : requestReceivedOp.getOutputs()) {
						if (opParam.getType().isSimple()) {
							outputString += ", (" + TypesProcessorC.convertParameterToC(opParam.getType()) + "*)&" + opParam.getName();
						} else {
							outputString += ", (" + TypesProcessorC.convertParameterToC(opParam.getType()) + "*)" + opParam.getName();
						}
					}
					outputString += ");" + LF + "                  break;" + LF;
				}
			}
		}

		outputString += "            }" + LF + SEP_PATTERN_AU + LF;

		return outputString;
	}

	private String generateCancelWriteAccess(SM_DataWrittenOp dataWrittenOp) {
		// Generate function header
		String cwaString = "";

		cwaString += SEP_PATTERN_Y + moduleImpl.getName() + SEP_PATTERN_I + dataWrittenOp.getName() + "__cancel_write_access(" + moduleImpl.getName() + SEP_PATTERN_B + moduleImpl.getName() + SEP_PATTERN_I + dataWrittenOp.getName() + SEP_PATTERN_Z + LF + "{" + LF;

		if (moduleImpl.isInstrument()) {
			cwaString += SEP_PATTERN_BB + moduleImpl.getName() + SEP_PATTERN_I + dataWrittenOp.getName() + "__cancel_write_access()\\n\");" + LF + LF;
		}

		cwaString += "   if (data_handle->data != 0)" + LF + "   {" + LF + "      /* Free memory for data item */" + LF + "      free(data_handle->data);" + LF + "      data_handle->data = 0;" + LF + "   }" + LF + SEP_PATTERN_AL + LF + "}" + LF + LF;
		return cwaString;
	}

	private String generateGetPrivateContext() {
		return "   /* A pointer to the private context is located just before the module context */" + LF + "   private_context *privateContext = *(private_context **)(((unsigned char*)context) - sizeof(privateContext));" + LF + LF;
	}

	private String generateGetReadAccess(SM_DataReadOp dataReadOp) {
		String graString = "";
		String internal = "";

		if (moduleImpl.isInstrument()) {
			internal += "internal_";
		}

		graString += SEP_PATTERN_Y + moduleImpl.getName() + SEP_PATTERN_I + dataReadOp.getName() + "__get_" + internal + "read_access(" + moduleImpl.getName() + SEP_PATTERN_B + moduleImpl.getName() + SEP_PATTERN_I + dataReadOp.getName() + SEP_PATTERN_Z + LF + "{" + LF + generateGetPrivateContext();

		for (SM_ComponentInstance compInst : compInstList) {
			graString += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

			for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
				graString += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF;

				for (SM_DataLink dataLink : modInst.getDataLinksForReaderOp(dataReadOp)) {
					for (SM_ReaderInterface reader : dataLink.getReaders()) {
						if (reader.getReaderInst() instanceof SM_ModuleInstance) {
							if ((SM_ModuleInstance) reader.getReaderInst() == modInst) {
								SM_VDRepository vdRepo = dataLink.getVDRepo();
								String vdName = protectionDomain.getName() + "_" + compInst.getName() + "_VD" + vdRepo.getName();
								String dataType = vdRepo.getDataType().getNamespace().getName().replaceAll("\\.", "__") + "__" + vdRepo.getDataType().getName();
								String vdDataType = vdName + "_DataType";
								includeList.add(vdName);

								graString += "         ECOA__return_status status;" + LF + SEP_PATTERN_K + vdDataType + " newData;" + LF + "         /* Allocate memory for data item */" + LF + "         data_handle->data = (" + dataType + "*)malloc(sizeof(" + dataType + "));" + LF + LF +

										"         if (data_handle->data > 0)" + LF + SEP_PATTERN_D + LF + "            status = " + vdName + "__Read((data_handle->data), &(data_handle->timestamp));" + LF + SEP_PATTERN_AU + LF + "         else" + LF + SEP_PATTERN_D + LF + "            printf(\"ERROR " + moduleImpl.getName() + SEP_PATTERN_I + dataReadOp.getName() + "__get_read_access: data_handle->data <= 0\\n\");" + LF + SEP_PATTERN_AU + LF + LF +

										// If there is no data written to
										// repository, deallocate memory and set
										// data pointer to 0.
										"         if (status == ECOA__return_status_DATA_NOT_INITIALIZED)" + LF + SEP_PATTERN_D + LF + "            status = ECOA__return_status_NO_DATA;" + LF + "            free(data_handle->data);" + LF + "            data_handle->data = 0;" + LF + SEP_PATTERN_AU + LF + LF +

										"         return status;" + LF;
							}
						}
					}
				}
				// Close if on mod inst check
				graString += SEP_PATTERN_AQ + LF;
			}
			// Close if on comp inst check
			graString += "   }" + LF;
		}

		graString += "}" + LF + LF;

		if (moduleImpl.isInstrument()) {
			graString += SEP_PATTERN_Y + moduleImpl.getName() + SEP_PATTERN_I + dataReadOp.getName() + "__get_read_access(" + moduleImpl.getName() + SEP_PATTERN_B + moduleImpl.getName() + SEP_PATTERN_I + dataReadOp.getName() + SEP_PATTERN_Z + LF + "{" + LF;

			graString += SEP_PATTERN_BB + moduleImpl.getName() + SEP_PATTERN_I + dataReadOp.getName() + "__get_read_access()\\n\");" + LF + LF;

			graString += "   return " + moduleImpl.getName() + SEP_PATTERN_I + dataReadOp.getName() + "__get_internal_read_access(context, data_handle);" + LF +

					"}" + LF + LF;
		}

		return graString;
	}

	private String generateGetWriteAccess(SM_DataWrittenOp dataWrittenOp) {

		// Generate function header.
		String gwaString = "";

		gwaString += SEP_PATTERN_Y + moduleImpl.getName() + SEP_PATTERN_I + dataWrittenOp.getName() + "__get_write_access(" + moduleImpl.getName() + SEP_PATTERN_B + moduleImpl.getName() + SEP_PATTERN_I + dataWrittenOp.getName() + SEP_PATTERN_Z + LF + "{" + LF + generateGetPrivateContext();

		if (moduleImpl.isInstrument()) {
			gwaString += SEP_PATTERN_BB + moduleImpl.getName() + SEP_PATTERN_I + dataWrittenOp.getName() + "__get_write_access()\\n\");" + LF + LF;
		}

		for (SM_ComponentInstance compInst : compInstList) {
			gwaString += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

			for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
				gwaString += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF;

				for (SM_DataLink dataLink : modInst.getDataLinksForWriterOp(dataWrittenOp)) {
					if (dataLink.getWriter().getWriterInst() instanceof SM_ModuleInstance) {
						if ((SM_ModuleInstance) dataLink.getWriter().getWriterInst() == modInst) {
							SM_VDRepository vdRepo = dataLink.getVDRepo();
							String vdName = protectionDomain.getName() + "_" + compInst.getName() + "_VD" + vdRepo.getName();
							String vdDataType = vdName + "_DataType";
							includeList.add(vdName);

							gwaString += "         ECOA__return_status status;" + LF + SEP_PATTERN_K + vdDataType + " newData;" + LF + "         /* Allocate memory for data item */" + LF + "         data_handle->data = (" + vdRepo.getDataTypeName() + "*)malloc(sizeof(" + vdRepo.getDataTypeName() + "));" + LF + LF +

									"         if (data_handle->data > 0)" + LF + SEP_PATTERN_D + LF + "            status = " + vdName + "__Read((data_handle->data), &(data_handle->timestamp));" + LF + SEP_PATTERN_AU + LF + "         else" + LF + SEP_PATTERN_D + LF + "            printf(\"ERROR " + moduleImpl.getName() + SEP_PATTERN_I + dataWrittenOp.getName() + "__get_write_access: data_handle->data <= 0\\n\");" + LF + SEP_PATTERN_AU + LF + LF +

									"         return status;" + LF;
						}
					}
				}
				// Close if on mod inst check
				gwaString += SEP_PATTERN_AQ + LF;
			}

			// Close if on comp inst check
			gwaString += "   }" + LF;
		}

		gwaString += "}" + LF;

		return gwaString;
	}

	private String generateLifecycleOpCalls(String lifecycleString, SM_Object nonSupervisor) {
		lifecycleString += SEP_PATTERN_Y + moduleImpl.getName() + "_container__INITIALIZE__" + nonSupervisor.getName() + "(" + moduleImpl.getName() + SEP_PATTERN_G + LF + "{" + LF;

		if (moduleImpl.isInstrument()) {
			lifecycleString += SEP_PATTERN_BB + moduleImpl.getName() + "_container__INITIALIZE__" + nonSupervisor.getName() + SEP_PATTERN_L + LF + LF;
		}

		lifecycleString += SEP_PATTERN_Q + LF + SEP_PATTERN_AF + LF + SEP_PATTERN_H + LF + LF +

				generateGetPrivateContext();

		for (SM_ComponentInstance compInst : compInstList) {
			includeList.add(compInst.getName() + "_" + nonSupervisor.getName() + SEP_PATTERN_AM);
			lifecycleString += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

			for (SM_ModuleInstance supervisorModInst : moduleImpl.getModuleInstances()) {
				lifecycleString += SEP_PATTERN_E + supervisorModInst.getComponentImplementation().getName().toUpperCase() + "_" + supervisorModInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF + SEP_PATTERN_AZ + LF + SEP_PATTERN_AA + compInst.getName() + "_" + nonSupervisor.getName() + SEP_PATTERN_X + LF + SEP_PATTERN_AQ + LF;
			}
			// Close if on comp inst check
			lifecycleString += "   }" + LF;
		}
		// Close operation
		lifecycleString += SEP_PATTERN_AV + LF + "}" + LF + LF;

		lifecycleString += SEP_PATTERN_Y + moduleImpl.getName() + "_container__START__" + nonSupervisor.getName() + "(" + moduleImpl.getName() + SEP_PATTERN_G + LF + "{" + LF;

		if (moduleImpl.isInstrument()) {
			lifecycleString += SEP_PATTERN_BB + moduleImpl.getName() + "_container__START__" + nonSupervisor.getName() + SEP_PATTERN_L + LF + LF;
		}

		lifecycleString += SEP_PATTERN_Q + LF + SEP_PATTERN_AF + LF + SEP_PATTERN_H + LF + LF +

				generateGetPrivateContext();

		for (SM_ComponentInstance compInst : compInstList) {
			lifecycleString += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

			for (SM_ModuleInstance supervisorModInst : moduleImpl.getModuleInstances()) {
				lifecycleString += SEP_PATTERN_E + supervisorModInst.getComponentImplementation().getName().toUpperCase() + "_" + supervisorModInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF + SEP_PATTERN_AZ + LF + SEP_PATTERN_AA + compInst.getName() + "_" + nonSupervisor.getName() + "_Controller__START_received(&timestamp);" + LF + SEP_PATTERN_AQ + LF;
			}
			// Close if on comp inst check
			lifecycleString += "   }" + LF;
		}

		// Close operation
		lifecycleString += SEP_PATTERN_AV + LF + "}" + LF + LF;

		lifecycleString += SEP_PATTERN_Y + moduleImpl.getName() + "_container__STOP__" + nonSupervisor.getName() + "(" + moduleImpl.getName() + SEP_PATTERN_G + LF + "{" + LF;

		if (moduleImpl.isInstrument()) {
			lifecycleString += SEP_PATTERN_BB + moduleImpl.getName() + "_container__STOP__" + nonSupervisor.getName() + SEP_PATTERN_L + LF + LF;
		}

		lifecycleString += SEP_PATTERN_Q + LF + SEP_PATTERN_AF + LF + SEP_PATTERN_H + LF + LF +

				generateGetPrivateContext();

		for (SM_ComponentInstance compInst : compInstList) {
			lifecycleString += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

			for (SM_ModuleInstance supervisorModInst : moduleImpl.getModuleInstances()) {
				lifecycleString += SEP_PATTERN_E + supervisorModInst.getComponentImplementation().getName().toUpperCase() + "_" + supervisorModInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF + SEP_PATTERN_AZ + LF + SEP_PATTERN_AA + compInst.getName() + "_" + nonSupervisor.getName() + "_Controller__STOP_received(&timestamp);" + LF + SEP_PATTERN_AQ + LF;
			}
			// Close if on comp inst check
			lifecycleString += "   }" + LF;
		}
		// Close operation
		lifecycleString += SEP_PATTERN_AV + LF + "}" + LF + LF;

		lifecycleString += SEP_PATTERN_Y + moduleImpl.getName() + "_container__SHUTDOWN__" + nonSupervisor.getName() + "(" + moduleImpl.getName() + SEP_PATTERN_G + LF + "{" + LF;

		if (moduleImpl.isInstrument()) {
			lifecycleString += SEP_PATTERN_BB + moduleImpl.getName() + "_container__SHUTDOWN__" + nonSupervisor.getName() + SEP_PATTERN_L + LF + LF;
		}

		lifecycleString += SEP_PATTERN_Q + LF + SEP_PATTERN_AF + LF + SEP_PATTERN_H + LF + LF +

				generateGetPrivateContext();

		for (SM_ComponentInstance compInst : compInstList) {
			lifecycleString += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

			for (SM_ModuleInstance supervisorModInst : moduleImpl.getModuleInstances()) {
				lifecycleString += SEP_PATTERN_E + supervisorModInst.getComponentImplementation().getName().toUpperCase() + "_" + supervisorModInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF + SEP_PATTERN_AZ + LF + SEP_PATTERN_AA + compInst.getName() + "_" + nonSupervisor.getName() + "_Controller__SHUTDOWN_received(&timestamp);" + LF + SEP_PATTERN_AQ + LF;
			}
			// Close if on comp inst check
			lifecycleString += "   }" + LF;
		}
		// Close operation
		lifecycleString += SEP_PATTERN_AV + LF + "}" + LF + LF;

		lifecycleString += SEP_PATTERN_551 + moduleImpl.getName() + "_container__get_lifecycle_state__" + nonSupervisor.getName() + "(" + moduleImpl.getName() + "__context *context, ECOA__module_states_type *current_state)" + LF + "{" + LF + generateGetPrivateContext();

		for (SM_ComponentInstance compInst : compInstList) {
			lifecycleString += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

			for (SM_ModuleInstance supervisorModInst : moduleImpl.getModuleInstances()) {
				lifecycleString += SEP_PATTERN_E + supervisorModInst.getComponentImplementation().getName().toUpperCase() + "_" + supervisorModInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF + SEP_PATTERN_AZ + LF + SEP_PATTERN_K + compInst.getName() + "_" + nonSupervisor.getName() + "_Controller__Get_Lifecycle_State(current_state);" + LF + SEP_PATTERN_AQ + LF;
			}
			// Close if on comp inst check
			lifecycleString += "   }" + LF;
		}

		lifecycleString += "}" + LF + LF;

		return lifecycleString;
	}

	private String generatePublishWriteAccess(SM_DataWrittenOp dataWrittenOp) {
		// Generate function header
		String pwaString = "";

		pwaString += SEP_PATTERN_Y + moduleImpl.getName() + SEP_PATTERN_I + dataWrittenOp.getName() + "__publish_write_access(" + moduleImpl.getName() + SEP_PATTERN_B + moduleImpl.getName() + SEP_PATTERN_I + dataWrittenOp.getName() + SEP_PATTERN_Z + LF + "{" + LF + "   ECOA__return_status status;" + LF + generateGetPrivateContext();

		if (moduleImpl.isInstrument()) {
			pwaString += SEP_PATTERN_BB + moduleImpl.getName() + SEP_PATTERN_I + dataWrittenOp.getName() + "__publish_write_access()\\n\");" + LF + LF;
		}

		pwaString += "   /* Set timestamp of publication */" + LF + "   ECOA_setTimestamp(&(data_handle->timestamp));" + LF + LF;

		for (SM_ComponentInstance compInst : compInstList) {
			pwaString += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

			for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
				pwaString += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF;

				for (SM_DataLink dataLink : modInst.getDataLinksForWriterOp(dataWrittenOp)) {
					// Always update the local repository
					SM_VDRepository vdRepo = dataLink.getVDRepo();
					String vdName = protectionDomain.getName() + "_" + compInst.getName() + "_VD" + vdRepo.getName();

					pwaString += "         if (data_handle->data != 0)" + LF + SEP_PATTERN_D + LF +

							"            /* Write to the local repository */" + LF + "            " + vdName + "__Write((data_handle->data), &(data_handle->timestamp));" + LF + LF +

							"            /* Free memory for data item */" + LF + "            free(data_handle->data);" + LF + "            data_handle->data = 0;" + LF + SEP_PATTERN_AU + LF + LF;

					// If any readers are a service - generate a call to the
					// service API.
					for (SM_ReaderInterface reader : dataLink.getReaders()) {
						if (reader instanceof SM_ReaderService) {
							includeList.add(compInst.getName() + "_" + reader.getReaderInst().getName() + SEP_PATTERN_AM);
							pwaString += SEP_PATTERN_N + LF + "         status = " + compInst.getName() + "_" + reader.getReaderInst().getName() + "_" + reader.getReaderOp().getName() + "__versioned_data_publish();" + LF;
						}
					}
				}
				// Close if on mod inst check
				pwaString += "   }" + LF + LF;
			}
			// Close if on comp inst check
			pwaString += "   }" + LF + LF;
		}
		pwaString += "   return status;" + LF + "}" + LF + LF;

		return pwaString;
	}

	private String generateReleaseReadAccess(SM_DataReadOp dataReadOp) {
		String rraString = "";
		String internal = "";

		if (moduleImpl.isInstrument()) {
			internal += "internal_";
		}

		rraString += SEP_PATTERN_Y + moduleImpl.getName() + SEP_PATTERN_I + dataReadOp.getName() + "__release_" + internal + "read_access(" + moduleImpl.getName() + SEP_PATTERN_B + moduleImpl.getName() + SEP_PATTERN_I + dataReadOp.getName() + SEP_PATTERN_Z + LF + "{" + LF;

		rraString += "   if (data_handle->data != 0)" + LF + "   {" + LF + "      /* Free memory for data item */" + LF + "      free(data_handle->data);" + LF + "      data_handle->data = 0;" + LF + "      return ECOA__return_status_OK;" + LF + "   }" + LF + "   return ECOA__return_status_INVALID_HANDLE;" + LF + "}" + LF + LF;

		if (moduleImpl.isInstrument()) {
			rraString += SEP_PATTERN_Y + moduleImpl.getName() + SEP_PATTERN_I + dataReadOp.getName() + "__release_read_access(" + moduleImpl.getName() + SEP_PATTERN_B + moduleImpl.getName() + SEP_PATTERN_I + dataReadOp.getName() + SEP_PATTERN_Z + LF + "{" + LF;

			rraString += SEP_PATTERN_BB + moduleImpl.getName() + SEP_PATTERN_I + dataReadOp.getName() + "__release_read_access()\\n\");" + LF + LF;

			rraString += "  return " + moduleImpl.getName() + SEP_PATTERN_I + dataReadOp.getName() + "__release_internal_read_access(context, data_handle);" + LF +

					"}" + LF + LF;
		}

		return rraString;
	}

	@Override
	public void open() {
		Path directory = outputDir.resolve("src-gen/" + compImpl.getName() + "/");
		super.openFile(directory.resolve(moduleImpl.getName() + "_container.c"));
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#EVENT_SENT#" + LF + "#RESPONSE_SEND#" + LF + "#REQUEST_SYNC#" + LF + "#REQUEST_ASYNC#" + LF + "#VD_READ#" + LF + "#VD_WRITE#" + LF + "#GET_PROPERTIES#" + LF + "#SET_AVAILABILITY#" + LF + "#GET_AVAILABILITY#" + LF + "#LIFECYCLE#" + LF + SEP_PATTERN_AI + LF + "#FAULT_LOGGING_SERVICES#" + LF + "#RECOVERY_ACTION#" + LF + "#PINFO#" + LF + "#SAVE_NON_VOL#" + LF;
		codeStringBuilder.append(fileStructure);
	}

	public void writeAsyncRequests() {
		String requestAsyncString = "";

		for (SM_RequestSentOp requestSentOp : moduleImpl.getModuleType().getAsyncRequestSentOps()) {
			// Generate function header.
			requestAsyncString += SEP_PATTERN_Y + moduleImpl.getName() + SEP_PATTERN_I + requestSentOp.getName() + "__request_async(" + moduleImpl.getName() + "__context *context, ECOA__uint32 *ID";

			for (SM_OperationParameter opParam : requestSentOp.getInputs()) {
				requestAsyncString += CLanguageSupport.writeConstParam(opParam);
			}
			requestAsyncString += ")" + LF + "{" + LF + LF + SEP_PATTERN_Q + LF;

			if (moduleImpl.isInstrument()) {
				requestAsyncString += "printf(\"<<<<<<< " + moduleImpl.getName() + SEP_PATTERN_I + requestSentOp.getName() + "__request_async()\\n\");" + LF + LF;
			}

			requestAsyncString += SEP_PATTERN_AF + LF + SEP_PATTERN_H + LF + LF +

					"   /* Allocate a sequence number */" + LF + "   " + protectionDomain.getName() + "_PD_Controller__Allocate_Sequence_Number(ID);" + LF + LF +

					generateGetPrivateContext();

			for (SM_ComponentInstance compInst : compInstList) {
				requestAsyncString += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

				for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
					includeList.add(SEP_PATTERN_R);
					includeList.add(compImpl.getName() + SEP_PATTERN_V);

					requestAsyncString += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF +

							"         Client_Info_Type moduleClientInfo;" + LF + "         moduleClientInfo.type = Client_Type__MODULE_OPERATION;" + LF + "         moduleClientInfo.ID = " + compImpl.getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_" + requestSentOp.getName().toUpperCase() + "_UID;" + LF + "         moduleClientInfo.serviceUID = 0; // not used for module operations" + LF + "         moduleClientInfo.localSeqNum = *ID;" + LF + "         moduleClientInfo.globalSeqNum = 0; // not used for module operations" + LF + LF;

					// add the response timeout
					if (requestSentOp.getTimeout() > 0) {
						String compInstID = "CI_" + compInst.getName().toUpperCase() + "_ID";
						String modInstID = modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID";
						String modOpUID = compImpl.getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_" + requestSentOp.getName().toUpperCase() + "_UID";

						requestAsyncString += "         /* Setup the reponse timeout */" + LF + "         ECOA__duration duration;" + LF + "         ECOA__return_status st_error;" + LF + "         duration.seconds = " + requestSentOp.getTimeoutSec() + ";" + LF + "         duration.nanoseconds = " + requestSentOp.getTimeoutNano() + ";" + LF + LF +

								SEP_PATTERN_K + protectionDomain.getName() + "_Timer_Event_Manager__Setup_Timer(duration, REQUEST_TIMEOUT, " + compInstID + ", " + modInstID + ", " + modOpUID + ", *ID, (void*)0, &st_error);" + LF + LF;
					}

					requestAsyncString += determineRequestAsyncDestinations(requestSentOp, modInst, compInst) + SEP_PATTERN_AQ + LF;
				}
				// End if on component instance
				requestAsyncString += "   }" + LF;
			}

			requestAsyncString += SEP_PATTERN_AL + LF + "}" + LF + LF;
		}

		// Replace the #REQUEST_ASYNC# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#REQUEST_ASYNC#", requestAsyncString);

	}

	public void writeEventSents() {
		String eventSentString = "";

		for (SM_EventSentOp eventSentOp : moduleImpl.getModuleType().getEventSentOps()) {
			// Generate function header.
			eventSentString += SEP_PATTERN_551 + moduleImpl.getName() + SEP_PATTERN_I + eventSentOp.getName() + "__send(" + moduleImpl.getName() + "__context *context";

			for (SM_OperationParameter opParam : eventSentOp.getInputs()) {
				eventSentString += CLanguageSupport.writeConstParam(opParam);
			}
			eventSentString += ")" + LF + "{" + LF + SEP_PATTERN_Q + LF + "   ECOA__uint32 modUID = 0;" + LF + LF;

			if (moduleImpl.isInstrument()) {
				eventSentString += SEP_PATTERN_BB + moduleImpl.getName() + SEP_PATTERN_I + eventSentOp.getName() + "__send()\\n\");" + LF + LF;
			}

			eventSentString += SEP_PATTERN_AF + LF + SEP_PATTERN_H + LF + LF +

					generateGetPrivateContext();

			for (SM_ComponentInstance compInst : compInstList) {
				eventSentString += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

				for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
					eventSentString += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF + determineEventSentDestsForModInst(eventSentOp, modInst, compInst) + SEP_PATTERN_AQ + LF;
				}

				// End if on component instance
				eventSentString += "   }" + LF;
			}

			eventSentString += "}" + LF + LF;
		}

		// Replace the #EVENT_SENT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#EVENT_SENT#", eventSentString);
	}

	public void writeFaultLoggingManagment() {
		String faultLogServicesString = "/* Logging and fault management services API */" + LF + SEP_PATTERN_551 + moduleImpl.getName() + "_container__log_trace(" + moduleImpl.getName() + SEP_PATTERN_W + LF + completeLogFunction("TRACE") + LF +

				SEP_PATTERN_551 + moduleImpl.getName() + "_container__log_debug(" + moduleImpl.getName() + SEP_PATTERN_W + LF + completeLogFunction("DEBUG") + LF +

				SEP_PATTERN_551 + moduleImpl.getName() + "_container__log_info(" + moduleImpl.getName() + SEP_PATTERN_W + LF + completeLogFunction("INFO") + LF +

				SEP_PATTERN_551 + moduleImpl.getName() + "_container__log_warning(" + moduleImpl.getName() + SEP_PATTERN_W + LF + completeLogFunction("WARNING") + LF +

				SEP_PATTERN_551 + moduleImpl.getName() + "_container__raise_error(" + moduleImpl.getName() + SEP_PATTERN_W + LF + completeRaiseError() + LF +

				SEP_PATTERN_551 + moduleImpl.getName() + "_container__raise_fatal_error(" + moduleImpl.getName() + SEP_PATTERN_W + LF + completeRaiseFatalError() + LF;

		// Replace the #FAULT_LOG_SERVICES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#FAULT_LOGGING_SERVICES#", faultLogServicesString);
	}

	public void writeGetProperties() {

		// TODO WARNING this implementation is very simple, it only deals with
		// property values of float,
		// and even then it only handles the situation where there is one value!
		String propertiesString = "";

		for (SM_ModuleTypeProperty property : moduleType.getModuleProperties()) {
			propertiesString += SEP_PATTERN_551 + moduleImpl.getName() + "_container__get_" + property.getName() + "_value" + "(" + moduleImpl.getName() + SEP_PATTERN_AG + TypesProcessorC.convertParameterToC(property.getType()) + "* value)" + LF + "{" + LF + generateGetPrivateContext();

			if (moduleImpl.isInstrument()) {
				propertiesString += SEP_PATTERN_BB + moduleImpl.getName() + "_container__get_" + property.getName() + "_value()\\n\");" + LF + LF;
			}

			for (SM_ComponentInstance compInst : compInstList) {
				propertiesString += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

				for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
					SM_ModuleInstanceProperty modInstProperty = modInst.getModInstPropertyByName(property.getName());

					propertiesString += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF + determinePropertyValueForModInst(modInstProperty, modInst, compInst) + SEP_PATTERN_AQ + LF;
				}
				// Close if on comp inst check
				propertiesString += "   }" + LF;
			}
			propertiesString += "}" + LF;
		}

		// Replace the #GET_PROPERTIES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GET_PROPERTIES#", propertiesString);

	}

	public void writeGetRequiredAvailability() {
		String getRequiredAvailabilityText = "";

		// Only generate if there is at least one required service
		if (compImpl.getCompType().getReferenceInstancesList().size() > 0) {
			if (moduleType.getIsSupervisor()) {
				getRequiredAvailabilityText += SEP_PATTERN_Y + moduleImpl.getName() + "_container__get_service_availability(" + moduleImpl.getName() + "__context* context, " + moduleImpl.getName() + "_container__reference_id instance, ECOA__boolean8 *available)" + LF + "{" + LF + generateGetPrivateContext();

				if (moduleImpl.isInstrument()) {
					getRequiredAvailabilityText += SEP_PATTERN_BB + moduleImpl.getName() + "_container__get_service_availability()\\n\");" + LF + LF;
				}

				for (SM_ComponentInstance compInst : compInstList) {
					getRequiredAvailabilityText += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

					for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
						getRequiredAvailabilityText += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF + "         switch (instance)" + LF + SEP_PATTERN_D + LF;

						for (SM_ServiceInstance servInstance : compImpl.getCompType().getReferenceInstancesList()) {
							includeList.add(compInst.getName() + "_" + servInstance.getName() + SEP_PATTERN_AM);
							getRequiredAvailabilityText += "            case " + moduleImpl.getName() + "_container__reference_id__" + servInstance.getName() + " :" + LF + "               /* Call the service API */" + LF + "               return " + compInst.getName() + "_" + servInstance.getName() + "__Get_Availability(available);" + LF + SEP_PATTERN_S + LF;
						}

						getRequiredAvailabilityText += "            default:" + LF + "               return ECOA__return_status_INVALID_SERVICE_ID;" + LF + SEP_PATTERN_S + LF + SEP_PATTERN_AU + LF +

						// Close if on mod inst check
								SEP_PATTERN_AQ + LF;
					}
					// Close if on comp inst check
					getRequiredAvailabilityText += "   }" + LF;
				}
				getRequiredAvailabilityText += "}" + LF;
			}
		}

		// Replace the #GET_AVAILABILITY# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GET_AVAILABILITY#", getRequiredAvailabilityText);

	}

	public void writeIncludes() {
		includeList.addAll(underlyingPlatform.addIncludesContainerBody());
		includeList.add(moduleImpl.getName() + "_container");
		includeList.add(protectionDomain.getName() + "_PD_Controller");
		includeList.add(compImpl.getName() + "_Module_Instance_ID");
		includeList.add(protectionDomain.getName() + "_Timer_Event_Manager");
		if (underlyingPlatform instanceof C_Posix) {
			includeList.add("posix_apos_binding");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeLifecycle() {
		String lifecycleString = "";

		// Note - only generate for supervisors!
		if (moduleImpl.getModuleType().getIsSupervisor()) {
			for (SM_ModuleInstance nonSupervisorModInst : moduleImpl.getComponentImplementation().getModuleInstances().values()) {
				// Only generate if it too is not a supervisor (note: we can
				// handle multiple supervisors atm anyway...)
				if (!nonSupervisorModInst.getImplementation().getModuleType().getIsSupervisor()) {
					lifecycleString = generateLifecycleOpCalls(lifecycleString, nonSupervisorModInst);
				}
			}
			// Do the same for any triggers instances!
			for (SM_TriggerInstance triggerInstance : moduleImpl.getComponentImplementation().getTriggerInstances().values()) {
				lifecycleString = generateLifecycleOpCalls(lifecycleString, triggerInstance);
			}
			// Do the same for any dynamic triggers instances!
			for (SM_DynamicTriggerInstance dynTrigInstance : moduleImpl.getComponentImplementation().getDynamicTriggerInstances().values()) {
				lifecycleString = generateLifecycleOpCalls(lifecycleString, dynTrigInstance);
			}
		}
		// Replace the #LIFECYCLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#LIFECYCLE#", lifecycleString);
	}

	public String writePerformComponentRestart() {
		String restartText = "";

		if (moduleType.getIsFaultHandler()) {
			restartText += SEP_PATTERN_Y + moduleImpl.getName() + "_container__performComponentRestart" + LF + "   (" + moduleImpl.getName() + SEP_PATTERN_AG + LF + SEP_PATTERN_AP + LF + SEP_PATTERN_O + LF + "{" + LF + SEP_PATTERN_Q + LF + LF +

					"   switch (asset_id)" + LF + "   {" + LF;

			includeList.add(SEP_PATTERN_AX);
			for (SM_ComponentInstance ci : pd.getComponentInstances()) {
				// Don't generate for the component with a fault handler in.
				if (!ci.getImplementation().equals(compImpl)) {
					restartText += "      case ECOA__FaultHandler__IDs_CMP_" + ci.getName() + " :" + LF + SEP_PATTERN_AW + LF + "         // Call reinitialise on each of the module controllers " + LF;

					for (SM_Object modTrig : ci.getImplementation().getModuleTriggerInstances()) {
						restartText += SEP_PATTERN_K + ci.getName() + "_" + modTrig.getName() + "_Controller__Reinitialise(ECOA__TRUE);" + LF;
					}

					restartText += LF + "         // Set any provided services unavailable" + LF;
					for (SM_ServiceInstance provService : ci.getCompType().getServiceInstancesList()) {
						restartText += SEP_PATTERN_K + ci.getName() + "_" + provService.getName() + "__Set_Availability(ECOA__TRUE);" + LF;
					}

					SM_ModuleInstance supervision = ci.getImplementation().getSupervisorModule();
					restartText += LF + "         // If not warm restart; zero the warm start context!" + LF + "         if (!isWarmRestart)" + LF + SEP_PATTERN_D + LF;

					for (SM_ModuleInstance mi : ci.getImplementation().getModuleInstances().values()) {
						restartText += "            " + ci.getName() + "_" + mi.getName() + "_Controller__Zero_Warm_Context();" + LF;
					}

					restartText += SEP_PATTERN_AU + LF + LF + "         // Initialize the supervision module" + LF + "         /* Timestamp point */" + LF + "         ECOA_setTimestamp(&timestamp);" + LF + LF +

							SEP_PATTERN_K + ci.getName() + "_" + supervision.getName() + SEP_PATTERN_X + LF + SEP_PATTERN_AQ + LF + "      break;" + LF;
				}
			}

			restartText += "   }" + LF + SEP_PATTERN_AL + LF + "}" + LF + LF;
		}

		return restartText;

	}

	public String writePerformComponentShutdown() {
		String shutdownText = "";

		if (moduleType.getIsFaultHandler()) {
			shutdownText += SEP_PATTERN_Y + moduleImpl.getName() + "_container__performComponentShutdown" + LF + "   (" + moduleImpl.getName() + SEP_PATTERN_AG + LF + SEP_PATTERN_AO + LF + "{" + LF + "   switch (asset_id)" + LF + "   {" + LF;

			includeList.add(SEP_PATTERN_AX);
			for (SM_ComponentInstance ci : pd.getComponentInstances()) {
				// Don't generate for the component with a fault handler in.
				if (!ci.getImplementation().equals(compImpl)) {
					shutdownText += "      case ECOA__FaultHandler__IDs_CMP_" + ci.getName() + " :" + LF + SEP_PATTERN_AW + LF + "         // Call reinitialise on each of the module controllers " + LF;

					for (SM_Object modTrig : ci.getImplementation().getModuleTriggerInstances()) {
						shutdownText += SEP_PATTERN_K + ci.getName() + "_" + modTrig.getName() + SEP_PATTERN_T + LF;
					}

					shutdownText += LF + "         // Set any provided services unavailable" + LF;
					for (SM_ServiceInstance provService : ci.getCompType().getServiceInstancesList()) {
						shutdownText += SEP_PATTERN_K + ci.getName() + "_" + provService.getName() + SEP_PATTERN_F + LF;
					}

					shutdownText += LF + SEP_PATTERN_AQ + LF + "      break;" + LF;
				}
			}

			shutdownText += "   }" + LF + SEP_PATTERN_AL + LF + "}" + LF + LF;
		}

		return shutdownText;
	}

	private String writePerformPDRestart() {
		String restartText = "";

		if (moduleType.getIsFaultHandler()) {
			restartText += SEP_PATTERN_Y + moduleImpl.getName() + "_container__performPDRestart" + LF + "   (" + moduleImpl.getName() + SEP_PATTERN_AG + LF + SEP_PATTERN_AP + LF + SEP_PATTERN_O + LF + "{" + LF + SEP_PATTERN_Q + LF + LF;

			includeList.add(SEP_PATTERN_AX);

			// TODO - currently we only have one PD, which is equal to one
			// platform so can ignore the asset ID
			// This will obviously need changing if/when we implement support
			// for multiple protection domains.

			for (SM_ComponentInstance ci : pd.getComponentInstances()) {
				// Don't generate for the component with a fault handler in.
				if (!ci.getImplementation().equals(compImpl)) {
					restartText += "   /* Component Instance = " + ci.getName() + " */" + LF + "   // Call reinitialise on each of the module controllers " + LF;

					for (SM_Object modTrig : ci.getImplementation().getModuleTriggerInstances()) {
						restartText += "   " + ci.getName() + "_" + modTrig.getName() + "_Controller__Reinitialise(ECOA__TRUE);" + LF;
					}

					restartText += LF + "   // Set any provided services unavailable" + LF;
					for (SM_ServiceInstance provService : ci.getCompType().getServiceInstancesList()) {
						restartText += "   " + ci.getName() + "_" + provService.getName() + "__Set_Availability(ECOA__TRUE);" + LF;
					}

					SM_ModuleInstance supervision = ci.getImplementation().getSupervisorModule();
					restartText += LF + "   // If not warm restart; zero the warm start context!" + LF + "   if (!isWarmRestart)" + LF + "   {" + LF;

					for (SM_ModuleInstance mi : ci.getImplementation().getModuleInstances().values()) {
						restartText += SEP_PATTERN_M + ci.getName() + "_" + mi.getName() + "_Controller__Zero_Warm_Context();" + LF;
					}

					restartText += LF + "   }" + LF + LF +

							"   // Initialize the supervision module" + LF + SEP_PATTERN_AF + LF + SEP_PATTERN_H + LF + LF +

							"   " + ci.getName() + "_" + supervision.getName() + SEP_PATTERN_X + LF + LF;
				}
			}

			restartText += SEP_PATTERN_AL + LF + "}" + LF + LF;
		}

		return restartText;
	}

	private String writePerformPDShutdown() {
		String shutdownText = "";

		if (moduleType.getIsFaultHandler()) {
			shutdownText += SEP_PATTERN_Y + moduleImpl.getName() + "_container__performPDShutdown" + LF + "   (" + moduleImpl.getName() + SEP_PATTERN_AG + LF + SEP_PATTERN_AO + LF + "{" + LF;

			includeList.add(SEP_PATTERN_AX);

			// TODO - currently we only have one PD, which is equal to one
			// platform so can ignore the asset ID
			// This will obviously need changing if/when we implement support
			// for multiple protection domains.

			for (SM_ComponentInstance ci : pd.getComponentInstances()) {
				// Don't generate for the component with a fault handler in.
				if (!ci.getImplementation().equals(compImpl)) {
					shutdownText += "   /* Component Instance = " + ci.getName() + " */" + LF + "   // Call reinitialise on each of the module controllers " + LF;

					for (SM_Object modTrig : ci.getImplementation().getModuleTriggerInstances()) {
						shutdownText += "   " + ci.getName() + "_" + modTrig.getName() + SEP_PATTERN_T + LF;
					}

					shutdownText += LF + "   // Set any provided services unavailable" + LF;
					for (SM_ServiceInstance provService : ci.getCompType().getServiceInstancesList()) {
						shutdownText += "   " + ci.getName() + "_" + provService.getName() + SEP_PATTERN_F + LF;
					}

					shutdownText += LF;
				}
			}

			shutdownText += SEP_PATTERN_AL + LF + "}" + LF + LF;
		}

		return shutdownText;
	}

	private String writePerformPlatformRestart() {
		String restartText = "";

		if (moduleType.getIsFaultHandler()) {
			includeList.add("ELI_Message");
			SM_LogicalComputingPlatform lcp = pd.getLogicalComputingNode().getLogicalComputingPlatform();

			restartText += SEP_PATTERN_Y + moduleImpl.getName() + "_container__performPlatformRestart" + LF + "   (" + moduleImpl.getName() + SEP_PATTERN_AG + LF + SEP_PATTERN_AP + LF + SEP_PATTERN_O + LF + "{" + LF;
			restartText += "   /* Set the Platform Status to DOWN */" + LF + "   " + lcp.getName() + SEP_PATTERN_AN + lcp.getRelatedUDPBinding().getPlatformID() + ", ELI_Message__PlatformStatus_DOWN, 0);" + LF + LF +

					"   /* Perform the same actions as a PD Cold Restart */" + LF + "   " + moduleImpl.getName() + "_container__performPDRestart(context, asset_id, isWarmRestart);" + LF +

					"   /* Also need to set the Platform Status to UP */" + LF + "   " + lcp.getName() + SEP_PATTERN_AN + lcp.getRelatedUDPBinding().getPlatformID() + ", ELI_Message__PlatformStatus_UP, 0);" + LF + LF +

					SEP_PATTERN_AL + LF + "}" + LF + LF;
		}

		return restartText;
	}

	private String writePerformPlatformShutdown() {
		String shutdownText = "";

		if (moduleType.getIsFaultHandler()) {
			includeList.add("ELI_Message");
			SM_LogicalComputingPlatform lcp = pd.getLogicalComputingNode().getLogicalComputingPlatform();

			shutdownText += SEP_PATTERN_Y + moduleImpl.getName() + "_container__performPlatformShutdown" + LF + "   (" + moduleImpl.getName() + SEP_PATTERN_AG + LF + SEP_PATTERN_AO + LF + "{" + LF;
			shutdownText += "   /* Perform the same actions as a PD shutdown */" + LF + "   " + moduleImpl.getName() + SEP_PATTERN_AJ + LF +

					"   /* Also need to set the Platform Status to DOWN */" + LF + "   " + lcp.getName() + SEP_PATTERN_AN + lcp.getRelatedUDPBinding().getPlatformID() + ", ELI_Message__PlatformStatus_DOWN, 0);" + LF + LF +

					SEP_PATTERN_AL + LF + "}" + LF + LF;
		}

		return shutdownText;
	}

	public void writePINFO() {
		String pinfoText = "";

		// TODO - need to add RETURN STATUSES!
		for (SM_Pinfo pinfo : moduleType.getAllPinfos()) {
			pinfoText = writeReadPINFO(pinfoText, pinfo);

			pinfoText = writeSeekPINFO(pinfoText, pinfo);

			if (pinfo.isWriteable()) {
				pinfoText = writeWritePINFO(pinfoText, pinfo);
			}
		}

		// Replace the #PINFO# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PINFO#", pinfoText);

	}

	public void writePreamble() {
		String preambleText = "/* File " + moduleImpl.getName() + "_container.c */" + LF;

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	private String writeReadPINFO(String pinfoText, SM_Pinfo pinfo) {
		pinfoText += SEP_PATTERN_Y + moduleImpl.getName() + "_container__read_" + pinfo.getName() + LF + "   (" + moduleImpl.getName() + SEP_PATTERN_AG + LF + "    ECOA__byte *memory_address," + LF + "    ECOA__uint32 in_size," + LF + "    ECOA__uint32 *out_size)" + LF + "{" + LF +

				generateGetPrivateContext();

		for (SM_ComponentInstance compInst : compInstList) {
			pinfoText += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

			for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
				pinfoText += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF + SEP_PATTERN_AA + compInst.getName() + "_" + modInst.getName() + "_Controller__read_" + pinfo.getName() + "(memory_address, in_size, out_size);" + LF + SEP_PATTERN_AQ + LF;
			}

			// End if on component instance
			pinfoText += "   }" + LF;
		}

		pinfoText += "}" + LF + LF;
		return pinfoText;
	}

	public void writeRecoveryAction() {
		String recoveryActionText = "";

		if (moduleType.getIsFaultHandler()) {
			recoveryActionText += writePerformComponentRestart();
			recoveryActionText += writePerformComponentShutdown();
			recoveryActionText += writePerformPDRestart();
			recoveryActionText += writePerformPDShutdown();

			// Only generate if multiple platforms.
			if (platformGenerator.getSystemModel().getLogicalSystem().getLogicalcomputingPlatforms().size() > 1) {
				recoveryActionText += writePerformPlatformRestart();
				recoveryActionText += writePerformPlatformShutdown();
			}

			recoveryActionText += SEP_PATTERN_Y + moduleImpl.getName() + "_container__recovery_action" + LF + "   (" + moduleImpl.getName() + SEP_PATTERN_AG + LF + "    ECOA__recovery_action_type recovery_action," + LF + SEP_PATTERN_AP + LF + "    ECOA__asset_type asset_type)" + LF + "{" + LF;

			if (moduleImpl.isInstrument()) {
				recoveryActionText += SEP_PATTERN_BB + moduleImpl.getName() + "_container__recovery_action()\\n\");" + LF + LF;
			}

			recoveryActionText += "   if (recovery_action == ECOA__recovery_action_type_SHUTDOWN)" + LF + "   {" + LF + "      switch (asset_type)" + LF + SEP_PATTERN_AW + LF + "         case ECOA__asset_type_COMPONENT :" + LF + SEP_PATTERN_D + LF + SEP_PATTERN_P + moduleImpl.getName() + "_container__performComponentShutdown(context, asset_id);" + LF + SEP_PATTERN_AU + LF + SEP_PATTERN_AB + LF + "         case ECOA__asset_type_NODE : " + LF + SEP_PATTERN_D + LF + "            // For now do the same as PD..." + LF + SEP_PATTERN_P + moduleImpl.getName() + SEP_PATTERN_AJ + LF + SEP_PATTERN_AU + LF + SEP_PATTERN_AB + LF + "         case ECOA__asset_type_PROTECTION_DOMAIN :" + LF + SEP_PATTERN_D + LF + SEP_PATTERN_P + moduleImpl.getName() + SEP_PATTERN_AJ + LF + SEP_PATTERN_AU + LF + SEP_PATTERN_AB + LF + "         case ECOA__asset_type_PLATFORM :" + LF + SEP_PATTERN_D + LF;
			if (platformGenerator.getSystemModel().getLogicalSystem().getLogicalcomputingPlatforms().size() > 1) {
				recoveryActionText += SEP_PATTERN_P + moduleImpl.getName() + "_container__performPlatformShutdown(context, asset_id);" + LF;
			} else {
				recoveryActionText += "            // Only one platform - therefore not available" + LF + "            ECOA__return_status_OPERATION_NOT_AVAILABLE;" + LF;
			}
			recoveryActionText += SEP_PATTERN_AU + LF + SEP_PATTERN_AB + LF + SEP_PATTERN_AQ + LF + "   }" + LF + "   else if (recovery_action == ECOA__recovery_action_type_COLD_RESTART || recovery_action == ECOA__recovery_action_type_WARM_RESTART)" + LF + "   {" + LF + "      switch (asset_type)" + LF + SEP_PATTERN_AW + LF + "         case ECOA__asset_type_COMPONENT :" + LF + SEP_PATTERN_D + LF + SEP_PATTERN_P + moduleImpl.getName() + "_container__performComponentRestart(context, asset_id, recovery_action == ECOA__recovery_action_type_WARM_RESTART ? ECOA__TRUE : ECOA__FALSE);" + LF + SEP_PATTERN_AU + LF + SEP_PATTERN_AB + LF + "         case ECOA__asset_type_NODE : " + LF + SEP_PATTERN_D + LF + "            // For now do the same as PD..." + LF + SEP_PATTERN_P + moduleImpl.getName() + "_container__performPDRestart(context, asset_id, recovery_action == ECOA__recovery_action_type_WARM_RESTART ? ECOA__TRUE : ECOA__FALSE);" + LF + SEP_PATTERN_AU + LF + SEP_PATTERN_AB + LF + "         case ECOA__asset_type_PROTECTION_DOMAIN :" + LF + SEP_PATTERN_D + LF + SEP_PATTERN_P + moduleImpl.getName()
					+ "_container__performPDRestart(context, asset_id, recovery_action == ECOA__recovery_action_type_WARM_RESTART ? ECOA__TRUE : ECOA__FALSE);" + LF + SEP_PATTERN_AU + LF + SEP_PATTERN_AB + LF + "         case ECOA__asset_type_PLATFORM : " + LF + SEP_PATTERN_D + LF;
			if (platformGenerator.getSystemModel().getLogicalSystem().getLogicalcomputingPlatforms().size() > 1) {
				recoveryActionText += SEP_PATTERN_P + moduleImpl.getName() + "_container__performPlatformRestart(context, asset_id, recovery_action == ECOA__recovery_action_type_WARM_RESTART ? ECOA__TRUE : ECOA__FALSE);" + LF;
			} else {
				recoveryActionText += "            // Only one platform - therefore not available" + LF + "            ECOA__return_status_OPERATION_NOT_AVAILABLE;" + LF;
			}
			recoveryActionText += SEP_PATTERN_AU + LF + SEP_PATTERN_AB + LF + SEP_PATTERN_AQ + LF + "   }" + LF + SEP_PATTERN_AV + LF + "}" + LF + LF;
		}

		// Replace the #RECOVERY_ACTION# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#RECOVERY_ACTION#", recoveryActionText);

	}

	public void writeResponseSents() {
		String responseSendString = "";

		for (SM_RequestReceivedOp requestReceivedOp : moduleImpl.getModuleType().getRequestReceivedOps()) {
			// Generate function header.

			responseSendString += SEP_PATTERN_Y + moduleImpl.getName() + SEP_PATTERN_I + requestReceivedOp.getName() + "__response_send(" + moduleImpl.getName() + "__context *context, const ECOA__uint32 ID";

			for (SM_OperationParameter opParam : requestReceivedOp.getOutputs()) {
				responseSendString += CLanguageSupport.writeConstParam(opParam);
			}
			responseSendString += ")" + LF + "{" + LF + "   ECOA__return_status status;" + LF + "   ECOA__return_status responseStatus = ECOA__return_status_OK;" + LF + SEP_PATTERN_Q + LF;

			if (moduleImpl.isInstrument()) {
				responseSendString += SEP_PATTERN_BB + moduleImpl.getName() + SEP_PATTERN_I + requestReceivedOp.getName() + "__response_send()\\n\");" + LF + LF;
			}

			responseSendString += SEP_PATTERN_AF + LF + SEP_PATTERN_H + LF + LF +

					generateGetPrivateContext();

			for (SM_ComponentInstance compInst : compInstList) {
				responseSendString += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

				for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
					responseSendString += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF + determineResponseSendDestsForModInst(requestReceivedOp, modInst, compInst) + SEP_PATTERN_AQ + LF;
				}
				// Close if on comp inst check
				responseSendString += "}" + LF;
			}

			responseSendString += "   return status;" + LF + "}" + LF + LF;
		}

		// Replace the #RESPONSE_SEND# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#RESPONSE_SEND#", responseSendString);
	}

	public void writeSaveNonVolatileContext() {
		includeList.add("ECOA_file_handler");
		String saveNonVolText = SEP_PATTERN_551 + moduleImpl.getName() + "_container__save_non_volatile_context(" + moduleImpl.getName() + "__context* context)" + LF + "{" + LF + generateGetPrivateContext();

		for (SM_ComponentInstance compInst : compInstList) {
			saveNonVolText += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

			for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
				saveNonVolText += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF + "         ECOA_write_context(\"" + compInst.getName() + "_" + modInst.getName() + "_warm_context.bin\", &(context->warm_start), sizeof(" + moduleImpl.getName() + "_warm_start_context));" + LF + SEP_PATTERN_AQ + LF;
			}

			// End if on component instance
			saveNonVolText += "   }" + LF;
		}
		saveNonVolText += "}" + LF + LF;

		// Replace the #SAVE_NON_VOL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SAVE_NON_VOL#", saveNonVolText);
	}

	private String writeSeekPINFO(String pinfoText, SM_Pinfo pinfo) {
		pinfoText += SEP_PATTERN_Y + moduleImpl.getName() + "_container__seek_" + pinfo.getName() + LF + "   (" + moduleImpl.getName() + SEP_PATTERN_AG + LF + "    ECOA__int32 offset," + LF + "    ECOA__seek_whence_type whence," + LF + "    ECOA__uint32 *new_position)" + LF + "{" + LF +

				generateGetPrivateContext();

		for (SM_ComponentInstance compInst : compInstList) {
			pinfoText += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

			for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
				pinfoText += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF + SEP_PATTERN_AA + compInst.getName() + "_" + modInst.getName() + "_Controller__seek_" + pinfo.getName() + "(offset, whence, new_position);" + LF + SEP_PATTERN_AQ + LF;
			}

			// End if on component instance
			pinfoText += "   }" + LF;
		}

		pinfoText += "}" + LF + LF;
		return pinfoText;
	}

	public void writeSetProvidedAvailability() {
		String setProvidedAvailabilityText = "";

		// Only generate if there is at least one provided service
		if (compImpl.getCompType().getServiceInstancesList().size() > 0) {
			if (moduleType.getIsSupervisor()) {
				setProvidedAvailabilityText += SEP_PATTERN_Y + moduleImpl.getName() + "_container__set_service_availability(" + moduleImpl.getName() + "__context* context, " + moduleImpl.getName() + "_container__service_id instance, ECOA__boolean8 available)" + LF + "{" + LF + generateGetPrivateContext();

				if (moduleImpl.isInstrument()) {
					setProvidedAvailabilityText += SEP_PATTERN_BB + moduleImpl.getName() + "_container__set_service_availability()\\n\");" + LF + LF;
				}

				for (SM_ComponentInstance compInst : compInstList) {
					setProvidedAvailabilityText += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

					for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
						setProvidedAvailabilityText += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF +

								"         switch (instance)" + LF + SEP_PATTERN_D + LF;

						for (SM_ServiceInstance servInstance : compImpl.getCompType().getServiceInstancesList()) {
							includeList.add(compInst.getName() + "_" + servInstance.getName() + SEP_PATTERN_AM);
							setProvidedAvailabilityText += "            case " + moduleImpl.getName() + "_container__service_id__" + servInstance.getName() + " :" + LF + "               /* Call the service API */" + LF + "               return " + compInst.getName() + "_" + servInstance.getName() + "__Set_Availability(available);" + LF + SEP_PATTERN_S + LF;
						}

						setProvidedAvailabilityText += "            default:" + LF + "               return ECOA__return_status_INVALID_SERVICE_ID;" + LF + SEP_PATTERN_S + LF + SEP_PATTERN_AU + LF +

						// Close if on mod inst check
								SEP_PATTERN_AQ + LF;
					}
					// Close if on comp inst check
					setProvidedAvailabilityText += "   }" + LF;
				}
				setProvidedAvailabilityText += "}" + LF;
			}
		}

		// Replace the #SET_AVAILABILITY# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SET_AVAILABILITY#", setProvidedAvailabilityText);

	}

	public void writeSyncRequests() {
		String requestSyncString = "";

		for (SM_RequestSentOp requestSentOp : moduleImpl.getModuleType().getSyncRequestSentOps()) {

			// Generate function header.
			requestSyncString += SEP_PATTERN_Y + moduleImpl.getName() + SEP_PATTERN_I + requestSentOp.getName() + "__request_sync(" + moduleImpl.getName() + "__context *context";

			for (SM_OperationParameter opParam : requestSentOp.getInputs()) {
				requestSyncString += CLanguageSupport.writeConstParam(opParam);
			}
			for (SM_OperationParameter opParam : requestSentOp.getOutputs()) {
				requestSyncString += CLanguageSupport.writeParam(opParam);
			}
			requestSyncString += ")" + LF + "{" + LF +
			// This is a bit rubbish - look at changing!
					"   ECOA__return_status responseStatusData;" + LF + "   ECOA__return_status *responseStatus = &responseStatusData;" + LF + SEP_PATTERN_Q + LF + "   ECOA__uint32 seqNum;" + LF;

			if (moduleImpl.isInstrument()) {
				requestSyncString += SEP_PATTERN_BB + moduleImpl.getName() + SEP_PATTERN_I + requestSentOp.getName() + "__request_sync()\\n\");" + LF + LF;
			}

			requestSyncString += SEP_PATTERN_AF + LF + SEP_PATTERN_H + LF + LF +

					"   /* Allocate a sequence number */" + LF + "   " + protectionDomain.getName() + "_PD_Controller__Allocate_Sequence_Number(&seqNum);" + LF + LF +

					generateGetPrivateContext();

			for (SM_ComponentInstance compInst : compInstList) {
				includeList.add("ILI_Message");
				includeList.add("message_queue");

				requestSyncString += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

				for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
					includeList.add(SEP_PATTERN_R);
					includeList.add(compImpl.getName() + SEP_PATTERN_V);
					includeList.add(modInst.getName() + "_ILI");

					requestSyncString += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF +

							"         Client_Info_Type moduleClientInfo;" + LF + "         moduleClientInfo.type = Client_Type__MODULE_OPERATION;" + LF + "         moduleClientInfo.ID = " + compImpl.getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_" + requestSentOp.getName().toUpperCase() + "_UID;" + LF + "         moduleClientInfo.serviceUID = 0; // not used for module operations" + LF + "         moduleClientInfo.localSeqNum = seqNum;" + LF + "         moduleClientInfo.globalSeqNum = 0; // not used for module operations" + LF + LF;

					// add the response timeout
					if (requestSentOp.getTimeout() > 0) {
						String compInstID = "CI_" + compInst.getName().toUpperCase() + "_ID";
						String modInstID = modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID";
						String modOpUID = compImpl.getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_" + requestSentOp.getName().toUpperCase() + "_UID";

						requestSyncString += "         /* Setup the reponse timeout */" + LF + "         ECOA__duration duration;" + LF + "         ECOA__return_status st_error;" + LF + "         duration.seconds = " + requestSentOp.getTimeoutSec() + ";" + LF + "         duration.nanoseconds = " + requestSentOp.getTimeoutNano() + ";" + LF + LF +

								SEP_PATTERN_K + protectionDomain.getName() + "_Timer_Event_Manager__Setup_Timer(duration, REQUEST_TIMEOUT, " + compInstID + ", " + modInstID + ", " + modOpUID + ", seqNum, (void*)0, &st_error);" + LF + LF;
					}

					includeList.add(compInst.getName() + "_" + modInst.getName() + SEP_PATTERN_AM);
					requestSyncString += determineRequestSyncDestsForModInst(requestSentOp, modInst, compInst) + LF +

							"         // Block on the synchronous message queue waiting for the response" + LF + "         ILI_Message responseMessage;" + LF + "         Receive_Message_Queue_Status_Type RMQ_Status;" + LF +

							"         /* Synchronous call - need to block at this point */" + LF + "         Receive_Message_Queue(" + compInst.getName() + "_" + modInst.getName() + "_Controller__get_SyncQueueID()," + LF + "            &responseMessage," + LF + "            sizeof(ILI_Message)," + LF + "            &RMQ_Status);" + LF + LF +

							"         /* Assign the outputs */" + LF;

					// Get the module instance ILI from the hashmap
					ModuleInstanceILI modInstILI = modInstILIMap.get(modInst);
					ILIMessage ili = modInstILI.getILIForResponseLink(modInst.getRequestLinkForClientOp(requestSentOp));

					int paramNum = 0;
					for (SM_OperationParameter opParam : ili.getParams()) {
						requestSyncString += "         *" + opParam.getName() + " = ((" + modInst.getName() + "_ILI_" + ili.getMessageID() + "_params*)responseMessage.messageDataPointer)->" + ili.getParams().get(paramNum).getName() + ";" + LF;
						paramNum++;
					}

					requestSyncString += LF + "         free(responseMessage.messageDataPointer);" + LF +

							SEP_PATTERN_AQ + LF;
				}
				// End if on component instance
				requestSyncString += "   }" + LF;
			}

			requestSyncString += "   return *responseStatus;" + LF + "}" + LF + LF;
		}

		// Replace the #REQUEST_SYNC# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#REQUEST_SYNC#", requestSyncString);

	}

	public void writeTimeResolutionServices() {
		String timeServicesString = "/* Time resolution services API */" + LF +
		// Get Relative Local Time Resolution
				SEP_PATTERN_551 + moduleImpl.getName() + "_container__get_relative_local_time_resolution(" + moduleImpl.getName() + "__context* context, ECOA__duration* relative_local_time_resolution)" + LF + "{" + LF;

		if (moduleImpl.isInstrument()) {
			timeServicesString += SEP_PATTERN_BB + moduleImpl.getName() + "_container__get_relative_local_time_resolution()\\n\");" + LF + LF;
		}

		timeServicesString += "   relative_local_time_resolution->seconds = 0;" + LF + "   relative_local_time_resolution->nanoseconds = 1000;" + LF + "}" + LF + LF +

		// Get UTC Time Resolution
				SEP_PATTERN_551 + moduleImpl.getName() + "_container__get_UTC_time_resolution(" + moduleImpl.getName() + "__context* context, ECOA__duration* utc_time_resolution)" + LF + "{" + LF;

		if (moduleImpl.isInstrument()) {
			timeServicesString += SEP_PATTERN_BB + moduleImpl.getName() + "_container__get_UTC_time_resolution()\\n\");" + LF + LF;
		}

		timeServicesString += "   utc_time_resolution->seconds = 0;" + LF + "   utc_time_resolution->nanoseconds = 1000;" + LF + "}" + LF + LF +

		// Get Absolute System Time Resolution
				SEP_PATTERN_551 + moduleImpl.getName() + "_container__get_absolute_system_time_resolution(" + moduleImpl.getName() + "__context* context, ECOA__duration* absolute_system_time_resolution)" + LF + "{" + LF;

		if (moduleImpl.isInstrument()) {
			timeServicesString += SEP_PATTERN_BB + moduleImpl.getName() + "_container__get_absolute_system_time_resolution()\\n\");" + LF + LF;
		}

		timeServicesString += "   absolute_system_time_resolution->seconds = 0;" + LF + "   absolute_system_time_resolution->nanoseconds = 1000;" + LF + "}" + LF + LF;

		// Replace the #TIME_SERVICES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, SEP_PATTERN_AI, timeServicesString);
	}

	public void writeTimeServices() {
		String timeServicesString = "/* Time services API */" + LF +
		// Get Relative Local Time
				SEP_PATTERN_551 + moduleImpl.getName() + "_container__get_relative_local_time(" + moduleImpl.getName() + "__context* context, ECOA__hr_time* relative_local_time)" + LF + "{" + LF;

		if (moduleImpl.isInstrument()) {
			timeServicesString += SEP_PATTERN_BB + moduleImpl.getName() + "_container__get_relative_local_time()\\n\");" + LF + LF;
		}

		timeServicesString += "   ECOA_get_relative_local_time(relative_local_time);" + LF + "}" + LF + LF +

		// Get UTC Time
				SEP_PATTERN_Y + moduleImpl.getName() + "_container__get_UTC_time(" + moduleImpl.getName() + "__context* context, ECOA__global_time* utc_time)" + LF + "{" + LF;

		if (moduleImpl.isInstrument()) {
			timeServicesString += SEP_PATTERN_BB + moduleImpl.getName() + "_container__get_UTC_time()\\n\");" + LF + LF;
		}

		timeServicesString += "   return ECOA_get_UTC_time(utc_time);" + LF + "}" + LF + LF +

		// Get Absolute System Time
				SEP_PATTERN_Y + moduleImpl.getName() + "_container__get_absolute_system_time(" + moduleImpl.getName() + "__context* context, ECOA__global_time* absolute_system_time)" + LF + "{" + LF;

		if (moduleImpl.isInstrument()) {
			timeServicesString += SEP_PATTERN_BB + moduleImpl.getName() + "_container__get_absolute_system_time()\\n\");" + LF + LF;
		}

		timeServicesString += "   return ECOA_get_absolute_system_time(absolute_system_time);" + LF + "}" + LF + LF;

		// Replace the #TIME_SERVICES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, SEP_PATTERN_AI, timeServicesString);
	}

	public void writeVDReads() {
		String vdReadString = "";

		for (SM_DataReadOp dataReadOp : moduleImpl.getModuleType().getDataReadOps()) {
			vdReadString += generateGetReadAccess(dataReadOp) + generateReleaseReadAccess(dataReadOp);
		}

		// Replace the #VD_READ# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#VD_READ#", vdReadString);
	}

	public void writeVDWrites() {
		String vdWriteString = "";

		for (SM_DataWrittenOp dataWrittenOp : moduleImpl.getModuleType().getDataWrittenOps()) {
			vdWriteString += generateGetWriteAccess(dataWrittenOp) + generateCancelWriteAccess(dataWrittenOp) + generatePublishWriteAccess(dataWrittenOp);
		}

		// Replace the #VD_WRITE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#VD_WRITE#", vdWriteString);
	}

	private String writeWritePINFO(String pinfoText, SM_Pinfo pinfo) {
		pinfoText += SEP_PATTERN_Y + moduleImpl.getName() + "_container__write_" + pinfo.getName() + LF + "   (" + moduleImpl.getName() + SEP_PATTERN_AG + LF + "    ECOA__byte *memory_address," + LF + "    ECOA__uint32 in_size)" + LF + "{" + LF +

				generateGetPrivateContext();

		for (SM_ComponentInstance compInst : compInstList) {
			pinfoText += SEP_PATTERN_BA + compInst.getName().toUpperCase() + "_ID)" + LF + "   {" + LF;

			for (SM_ModuleInstance modInst : moduleImpl.getModuleInstances()) {
				pinfoText += SEP_PATTERN_E + modInst.getComponentImplementation().getName().toUpperCase() + "_" + modInst.getName().toUpperCase() + "_ID)" + LF + SEP_PATTERN_AW + LF + SEP_PATTERN_AA + compInst.getName() + "_" + modInst.getName() + "_Controller__write_" + pinfo.getName() + "(memory_address, in_size);" + LF + SEP_PATTERN_AQ + LF;
			}

			// End if on component instance
			pinfoText += "   }" + LF;
		}

		pinfoText += "}" + LF + LF;
		return pinfoText;
	}

}
