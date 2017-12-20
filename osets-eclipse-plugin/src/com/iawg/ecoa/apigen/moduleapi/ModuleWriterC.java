/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.moduleapi;

import java.nio.file.Files;
import java.nio.file.Path;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.ToolConfig;
import com.iawg.ecoa.TypesProcessorC;
import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventReceivedOp;
import com.iawg.ecoa.systemmodel.types.SM_Namespace;

/**
 * This class extends the abstract class ModuleWriter and implements the methods
 * of that class in a way that is specific to the C language.
 * 
 * @author Shaun Cullimore
 *
 */
public class ModuleWriterC extends ModuleWriter {
	private static final String SEP_PATTERN_131 = "void ";
	private static final String SEP_PATTERN_A = "__context* context,";
	private static final String SEP_PATTERN_B = "   ECOA__log logMessage;";
	private static final String SEP_PATTERN_C = "_container__log_debug(context, logMessage);";
	private static final String SEP_PATTERN_D = "      /*  Log the state change */";
	private static final String SEP_PATTERN_E = "      ECOA__log logMessage;";
	private static final String SEP_PATTERN_F = "header";
	private static final String SEP_PATTERN_G = "      logMessage.current_size = sprintf(&logMessage.data, \"";
	private static final String SEP_PATTERN_H = "__context *context)";
	private static final String SEP_PATTERN_I = "   /* User Code Here */";
	private static final String SEP_PATTERN_J = "#include \"";
	private static final String SEP_PATTERN_K = "      /*  Publish state (component lifecycle) */";
	private static final String SEP_PATTERN_L = "      ";
	private static final String SEP_PATTERN_M = "   logMessage.current_size = sprintf(&logMessage.data, \"";

	// set the following to true to generate the template supervision module
	// code
	private boolean generateTemplateModule = false;

	public ModuleWriterC(SystemModel systemModel, ToolConfig toolConfig, String isType, Path outputDir, SM_ComponentImplementation compImpl, SM_ModuleImpl moduleImpl) {
		super(systemModel, toolConfig, isType, outputDir, compImpl, moduleImpl);

		generateTemplateModule = toolConfig.isGenerateTemplateCModules();
	}

	@Override
	public void close() {
		// Header
		if (isType.equals(SEP_PATTERN_F)) {
			codeStringBuilder.append("#if defined(__cplusplus)" + LF + "}" + LF + "#endif /* __cplusplus */" + LF + LF +

					"#endif  /* _" + moduleImplName.toUpperCase() + "_H */" + LF);
		}
		super.close();
	}

	@Override
	public void open() {
		if (isType.equals(SEP_PATTERN_F)) {
			super.openFile(outputDir.resolve(moduleImplName + ".h"));
		} else if (isType.equals("body")) {
			if (!Files.exists(outputDir.resolve(moduleImplName + ".c")) || toolConfig.isOverwriteFiles()) {
				super.openFile(outputDir.resolve(moduleImplName + ".c"));
			} else {
				super.openFile(outputDir.resolve(moduleImplName + ".c.new"));
			}
		}
	}

	@Override
	protected void setFileStructure() {
		// Not required for API generation - TODO look at ways to stop this
		// being required.
	}

	@Override
	public void writeConstParameter(SM_OperationParameter para) {
		// Write to header or body
		codeStringBuilder.append("," + LF + "    const " + TypesProcessorC.convertParameterToC(para.getType()));
		if (para.getType().isSimple()) {
			codeStringBuilder.append(" ");
		} else {
			codeStringBuilder.append("* ");
		}
		codeStringBuilder.append(para.getName());
	}

	@Override
	public void writeEndParameters() {
		if (isType.equals(SEP_PATTERN_F)) {
			codeStringBuilder.append(");" + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(")" + LF + "{" + LF + SEP_PATTERN_I + LF + "}");
		}
		codeStringBuilder.append(LF);
	}

	@Override
	public void writeErrorNotification(SM_Object instance) {
		codeStringBuilder.append(SEP_PATTERN_131 + moduleImplName + "__error_notification__" + instance.getName() + LF + "   (" + moduleImplName + SEP_PATTERN_A + LF + "    ECOA__module_error_type module_error_type)");

		if (isType.equals(SEP_PATTERN_F)) {
			codeStringBuilder.append(";" + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(LF + "{" + LF + SEP_PATTERN_I + LF + "}" + LF);
		}
		codeStringBuilder.append(LF);
	}

	@Override
	public void writeEventReceived(SM_EventReceivedOp eventReceiveOp) {
		String eventReceivedString = "";

		// Generate function header.
		eventReceivedString += SEP_PATTERN_131 + moduleImpl.getName() + "__" + eventReceiveOp.getName() + "__received(" + moduleImpl.getName() + "__context *context";

		for (SM_OperationParameter opParam : eventReceiveOp.getInputs()) {
			eventReceivedString += CLanguageSupport.writeConstParam(opParam);
		}
		eventReceivedString += ")";

		if (isType.equals(SEP_PATTERN_F)) {
			eventReceivedString += ";" + LF + LF;
		} else {
			if (generateTemplateModule) {
				String initOpName = "";
				String startOpName = "";

				for (SM_EventLink evLink : componentImplementation.getEventLinks()) {
					for (SM_SenderInterface sender : evLink.getSenders()) {
						if (sender.getSenderOpName().equals("initialize_component")) {

							initOpName = evLink.getReceivers().get(0).getReceiverOp().getName();
						}
						if (sender.getSenderOpName().equals("start_component")) {
							startOpName = evLink.getReceivers().get(0).getReceiverOp().getName();

						}
					}
				}

				if (eventReceiveOp.getName().equals(initOpName)) {
					eventReceivedString += writeTemplateInitComp();
				} else if (eventReceiveOp.getName().equals(startOpName)) {
					eventReceivedString += writeTemplateStartComp();
				} else {
					eventReceivedString += LF + "{" + LF + SEP_PATTERN_I + LF + "}" + LF + LF;
				}

			} else {
				eventReceivedString += LF + "{" + LF + SEP_PATTERN_I + LF + "}" + LF + LF;
			}
		}

		codeStringBuilder.append(eventReceivedString);
	}

	@Override
	public void writeFaultHandlerNotification() {
		codeStringBuilder.append(SEP_PATTERN_131 + moduleImplName + "__error_notification" + LF + "   (" + moduleImplName + SEP_PATTERN_A + LF + "    ECOA__error_id error_id," + LF + "    const ECOA__timestamp * timestamp," + LF + "    ECOA__asset_id asset_id," + LF + "    ECOA__asset_type asset_type," + LF + "    ECOA__error_type error_type)");

		if (isType.equals(SEP_PATTERN_F)) {
			codeStringBuilder.append(";" + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(LF + "{" + LF + SEP_PATTERN_I + LF + "}" + LF);
		}
		codeStringBuilder.append(LF);
	}

	@Override
	public void writeLifecycleNotification(SM_Object instance) {

		codeStringBuilder.append(SEP_PATTERN_131 + moduleImplName + "__lifecycle_notification__" + instance.getName() + LF + "   (" + moduleImplName + SEP_PATTERN_A + LF + "    ECOA__module_states_type previous_state," + LF + "    ECOA__module_states_type new_state)");

		if (isType.equals(SEP_PATTERN_F)) {
			codeStringBuilder.append(";" + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(LF);
			if (generateTemplateModule) {
				codeStringBuilder.append("{" + LF + "   " + moduleImpl.getName() + "_determineComponentState(context);" + LF + "}" + LF);
			} else {
				codeStringBuilder.append("{" + LF + SEP_PATTERN_I + LF + "}" + LF);
			}
		}
		codeStringBuilder.append(LF);
	}

	@Override
	public void writeLifecycleServices() {
		String modLifecycleString = "";

		// Generate the determine component state function (if templated
		// supervision module)
		if (moduleImpl.getModuleType().getIsSupervisor() && generateTemplateModule && !isType.equals(SEP_PATTERN_F)) {
			codeStringBuilder.append(writeTemplateDetermineCompState() + LF);
		}

		// Generate function headers.
		modLifecycleString += SEP_PATTERN_131 + moduleImpl.getName() + "__INITIALIZE__received(" + moduleImpl.getName() + SEP_PATTERN_H;

		if (isType.equals(SEP_PATTERN_F)) {
			modLifecycleString += ";" + LF + LF;
		} else {
			if (generateTemplateModule && moduleType.getIsSupervisor()) {
				modLifecycleString += LF + "{" + LF + SEP_PATTERN_B + LF + LF +

						SEP_PATTERN_M + componentImplementation.getName() + " (TEMPLATED COMPONENT)\");" + LF + "   " + moduleImpl.getName() + SEP_PATTERN_C + LF + "}" + LF + LF;
			} else {
				modLifecycleString += LF + "{" + LF + SEP_PATTERN_I + LF + "}" + LF + LF;
			}
		}

		modLifecycleString += SEP_PATTERN_131 + moduleImpl.getName() + "__START__received(" + moduleImpl.getName() + SEP_PATTERN_H;

		if (isType.equals(SEP_PATTERN_F)) {
			modLifecycleString += ";" + LF + LF;
		} else {
			if (generateTemplateModule && moduleType.getIsSupervisor()) {
				String lifecycleServiceIDString = "";

				// Get the lifecycle service instance ID
				for (SM_ServiceInstance serviceInstance : componentImplementation.getCompType().getServiceInstancesList()) {
					if (serviceInstance.getServiceInterface().getName().equals("ECOA_System_Management")) {
						lifecycleServiceIDString = moduleImpl.getName() + "_container__service_id__" + serviceInstance.getName();
						break;
					}
				}

				String opName = "";

				for (SM_EventLink evLink : componentImplementation.getEventLinks()) {
					for (SM_SenderInterface sender : evLink.getSenders()) {
						if (sender.getSenderOpName().equals("initialize_component")) {
							opName = evLink.getReceivers().get(0).getReceiverOp().getName();
							break;
						}
					}
					if (!opName.equals("")) {
						break;
					}
				}

				modLifecycleString += LF + "{" + LF + SEP_PATTERN_B + LF + LF +

						"   /* Publish default state (component lifecycle) */" + LF + "   set_component_state(context, ECOA_System_Management__component_states_type_IDLE);" + LF + LF +

						SEP_PATTERN_M + componentImplementation.getName() + ": New comp state = IDLE\");" + LF + "   " + moduleImpl.getName() + SEP_PATTERN_C + LF + LF +

						"   /* Set lifecycle service as available */" + LF + "   " + moduleImpl.getName() + "_container__set_service_availability(context, " + lifecycleServiceIDString + ", ECOA__TRUE);" + LF + LF +

						"   /* Auto-initialise the component */" + LF + "   " + moduleImpl.getName() + "__" + opName + "__received(context);" + LF + "}" + LF + LF;
			} else {
				modLifecycleString += LF + "{" + LF + SEP_PATTERN_I + LF + "}" + LF + LF;
			}
		}

		modLifecycleString += SEP_PATTERN_131 + moduleImpl.getName() + "__STOP__received(" + moduleImpl.getName() + SEP_PATTERN_H;

		if (isType.equals(SEP_PATTERN_F)) {
			modLifecycleString += ";" + LF + LF;
		} else {
			modLifecycleString += LF + "{" + LF + SEP_PATTERN_I + LF + "}" + LF + LF;
		}

		modLifecycleString += SEP_PATTERN_131 + moduleImpl.getName() + "__SHUTDOWN__received(" + moduleImpl.getName() + SEP_PATTERN_H;

		if (isType.equals(SEP_PATTERN_F)) {
			modLifecycleString += ";" + LF + LF;
		} else {
			modLifecycleString += LF + "{" + LF + SEP_PATTERN_I + LF + "}" + LF + LF;
		}

		modLifecycleString += SEP_PATTERN_131 + moduleImpl.getName() + "__REINITIALIZE__received(" + moduleImpl.getName() + SEP_PATTERN_H;

		if (isType.equals(SEP_PATTERN_F)) {
			modLifecycleString += ";" + LF + LF;
		} else {
			modLifecycleString += LF + "{" + LF + SEP_PATTERN_I + LF + "}" + LF + LF;
		}

		codeStringBuilder.append(modLifecycleString);

	}

	@Override
	public void writeParameter(SM_OperationParameter para) {
		// Write to header or body
		codeStringBuilder.append("," + LF + "    " + TypesProcessorC.convertParameterToC(para.getType()) + "* " + para.getName());
	}

	@Override
	public void writePreamble() {
		if (isType.equals(SEP_PATTERN_F)) {
			codeStringBuilder.append("/*" + LF + " * @file " + moduleImplName + ".h" + LF + " * Module Interface header for Module " + moduleImplName + LF + " * Generated automatically from specification; do not modify here" + LF + " */" + LF + LF +

					"#if !defined(_" + moduleImplName.toUpperCase() + "_H)" + LF + "#define _" + moduleImplName.toUpperCase() + "_H" + LF);

			for (SM_Namespace use : componentImplementation.getUses()) {
				codeStringBuilder.append(SEP_PATTERN_J + use.getName().replaceAll("\\.", "__") + ".h" + "\"" + LF + LF);
			}

			codeStringBuilder.append(SEP_PATTERN_J + moduleImplName + "_container.h\"" + LF + LF +

					"#if defined(__cplusplus)" + LF + "extern \"C\" {" + LF + "#endif /* __cplusplus */" + LF + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append("/*" + LF + " * @file " + moduleImplName + ".c" + LF + " * Module Interface for Module " + moduleImplName + LF + " */" + LF + LF +

					SEP_PATTERN_J + moduleImplName + ".h" + "\"" + LF + LF);

			if (generateTemplateModule && moduleType.getIsSupervisor()) {
				writeSetCompState();
			}
		}
	}

	private void writeSetCompState() {
		codeStringBuilder.append("static void set_component_state" + LF + "   (" + moduleImpl.getName() + "__context* context, ECOA_System_Management__component_states_type newState)" + LF + "{" + LF + "   ECOA__return_status status;" + LF + "   " + moduleImpl.getName() + "_container__component_state_handle stateHandle;" + LF + "   status = " + moduleImpl.getName() + "_container__component_state__get_write_access(context, &stateHandle);" + LF + "   if (status == ECOA__return_status_OK || status == ECOA__return_status_DATA_NOT_INITIALIZED)" + LF + "   {" + LF + "      *(stateHandle.data) = newState;" + LF + "      status = " + moduleImpl.getName() + "_container__component_state__publish_write_access(context, &stateHandle);" + LF + "      if (status != ECOA__return_status_OK)" + LF + "      {" + LF + "         int size;" + LF + "         ECOA__log log;" + LF + "         log.current_size = sprintf(&log.data, \"Failed to publish write access for component state\");" + LF + "         " + moduleImpl.getName() + "_container__log_warning(context, log);" + LF + "      }" + LF + "    }" + LF + "     else" + LF + "   {" + LF + "      int size;" + LF + "      ECOA__log log;" + LF
				+ "      log.current_size = sprintf(&log.data, \"Failed to get write access for component state\");" + LF + SEP_PATTERN_L + moduleImpl.getName() + "_container__log_warning(context, log);" + LF + "   }" + LF + "}" + LF + LF);
	}

	@Override
	public void writeRequestReceived(String name) {
		codeStringBuilder.append(SEP_PATTERN_131 + moduleImplName + "__" + name + "__request_received" + LF + "   (" + moduleImplName + SEP_PATTERN_A + LF + "    const ECOA__uint32 ID");
	}

	@Override
	public void writeResponseReceivedAsynchonous(String name) {
		codeStringBuilder.append(SEP_PATTERN_131 + moduleImplName + "__" + name + "__response_received" + LF + "   (" + moduleImplName + SEP_PATTERN_A + LF + "    const ECOA__uint32 ID, const ECOA__return_status status");
	}

	@Override
	public void writeServiceAvailabilityNotifications() {
		// Only generate if there is at least one required service
		if (componentImplementation.getCompType().getReferenceInstancesList().size() > 0) {
			// Service availability changed
			codeStringBuilder.append(SEP_PATTERN_131 + moduleImplName + "__service_availability_changed" + LF + "   (" + moduleImplName + SEP_PATTERN_A + LF + "    " + moduleImplName + "_container__reference_id instance," + LF + "    ECOA__boolean8 available)");

			if (isType.equals(SEP_PATTERN_F)) {
				codeStringBuilder.append(";");
			} else if (isType.equals("body")) {
				codeStringBuilder.append(LF + "{" + LF + SEP_PATTERN_I + LF + "}");
			}
			codeStringBuilder.append(LF);

			// Service provider changed
			codeStringBuilder.append(SEP_PATTERN_131 + moduleImplName + "__service_provider_changed" + LF + "   (" + moduleImplName + SEP_PATTERN_A + LF + "    " + moduleImplName + "_container__reference_id instance)");

			if (isType.equals(SEP_PATTERN_F)) {
				codeStringBuilder.append(";");
			} else if (isType.equals("body")) {
				codeStringBuilder.append(LF + "{" + LF + SEP_PATTERN_I + LF + "}");
			}
			codeStringBuilder.append(LF);
		}
	}

	private String writeTemplateDetermineCompState() {
		String determineStateString = LF + SEP_PATTERN_131 + moduleImpl.getName() + "_determineComponentState(" + moduleImpl.getName() + SEP_PATTERN_H + LF + "{" + LF;

		for (SM_Object modTrigInst : componentImplementation.getNonSupervisionModuleTriggerInstances()) {
			determineStateString += "   ECOA__module_states_type " + modTrigInst.getName() + "_state;" + LF + "   " + moduleImpl.getName() + "_container__get_lifecycle_state__" + modTrigInst.getName() + "(context, &" + modTrigInst.getName() + "_state);" + LF + LF;
		}

		if (componentImplementation.getNonSupervisionModuleTriggerInstances().size() > 0) {
			determineStateString += "   if (";

			int count = 0;
			for (SM_Object modTrigInst : componentImplementation.getNonSupervisionModuleTriggerInstances()) {
				if (count == 0) {
					determineStateString += modTrigInst.getName() + "_state == ECOA__module_states_type_READY &&" + LF;
				} else {
					determineStateString += SEP_PATTERN_L + modTrigInst.getName() + "_state == ECOA__module_states_type_READY &&" + LF;
				}
				count++;
			}

			// Remove the last && and replace with a closing brace
			determineStateString = determineStateString.substring(0, determineStateString.lastIndexOf("&&"));

			String opName = "";

			for (SM_EventLink evLink : componentImplementation.getEventLinks()) {
				for (SM_SenderInterface sender : evLink.getSenders()) {
					if (sender.getSenderOpName().equals("start_component")) {
						opName = evLink.getReceivers().get(0).getReceiverOp().getName();
						break;
					}
				}
				if (!opName.equals("")) {
					break;
				}
			}

			determineStateString += ")" + LF + "   {" + LF + SEP_PATTERN_K + LF + "      set_component_state(context, ECOA_System_Management__component_states_type_STOPPED);" + LF + LF +

					SEP_PATTERN_D + LF + SEP_PATTERN_E + LF + SEP_PATTERN_G + componentImplementation.getName() + ": New comp state = STOPPED\");" + LF + SEP_PATTERN_L + moduleImpl.getName() + SEP_PATTERN_C + LF + LF +

					"      /* Auto-start here if we are a \"manager\" component */" + LF + "      /* " + moduleImpl.getName() + "__" + opName + "__received(context); */" + LF + "   }" + LF + "   else if (";

			count = 0;
			for (SM_Object modTrigInst : componentImplementation.getNonSupervisionModuleTriggerInstances()) {
				if (count == 0) {
					determineStateString += modTrigInst.getName() + "_state == ECOA__module_states_type_RUNNING &&" + LF;
				} else {
					determineStateString += SEP_PATTERN_L + modTrigInst.getName() + "_state == ECOA__module_states_type_RUNNING &&" + LF;
				}
				count++;
			}

			// Remove the last && and replace with a closing brace
			determineStateString = determineStateString.substring(0, determineStateString.lastIndexOf("&&"));

			determineStateString += "   )" + LF + "   {" + LF +

					"      /* Set our provided services as available (NOTE: logic may be required here if dependent upon required services...) */" + LF;

			for (SM_ServiceInstance serviceInst : componentImplementation.getCompType().getServiceInstancesList()) {
				determineStateString += SEP_PATTERN_L + moduleImpl.getName() + "_container__set_service_availability(context, " + moduleImpl.getName() + "_container__service_id__" + serviceInst.getName() + ", ECOA__TRUE);" + LF;
			}

			determineStateString += LF + SEP_PATTERN_K + LF + "      set_component_state(context, ECOA_System_Management__component_states_type_RUNNING);" + LF + LF +

					SEP_PATTERN_D + LF + SEP_PATTERN_E + LF + SEP_PATTERN_G + componentImplementation.getName() + ": New comp state = RUNNING\");" + LF + SEP_PATTERN_L + moduleImpl.getName() + SEP_PATTERN_C + LF + LF +

					"      /* Start any subordinate components here if we are a \"manager\" component */" + LF + "      /* " + moduleImpl.getName() + "_container__XXX__send(context); */" + LF + "   }" + LF + LF;

		}

		determineStateString += LF + "}" + LF + LF;

		return determineStateString;
	}

	// Template writers

	private String writeTemplateInitComp() {
		String initCompString = LF + "{" + LF + "   /*  Publish state (component lifecycle) */" + LF + "   set_component_state(context, ECOA_System_Management__component_states_type_INITIALIZING);" + LF + LF +

				"   /*  Log the state change */" + LF + SEP_PATTERN_B + LF + SEP_PATTERN_M + componentImplementation.getName() + ": New comp state = INITIALIZING\");" + LF + "   " + moduleImpl.getName() + SEP_PATTERN_C + LF + LF;

		// Initialise each of the non-supervision modules
		for (SM_Object modTrigInst : componentImplementation.getNonSupervisionModuleTriggerInstances()) {
			initCompString += "   " + moduleImpl.getName() + "_container__INITIALIZE__" + modTrigInst.getName() + "(context);" + LF;
		}

		initCompString += LF + "}" + LF + LF;

		return initCompString;
	}

	private String writeTemplateStartComp() {
		String startCompString = LF + "{" + LF +

				"  " + moduleImpl.getName() + "_container__component_state_handle stateHandle;" + LF + "  " + moduleImpl.getName() + "_container__component_state__get_write_access(context, &stateHandle);" + LF + LF +

				"   if (*(stateHandle.data) == ECOA_System_Management__component_states_type_STOPPED)" + LF + "   {" + LF + SEP_PATTERN_K + LF + "      set_component_state(context, ECOA_System_Management__component_states_type_STARTING);" + LF + LF +

				SEP_PATTERN_D + LF + SEP_PATTERN_E + LF + SEP_PATTERN_G + componentImplementation.getName() + ": New comp state = STARTING\");" + LF + SEP_PATTERN_L + moduleImpl.getName() + SEP_PATTERN_C + LF + LF;

		// Initialise each of the non-supervision modules
		for (SM_Object modTrigInstance : componentImplementation.getNonSupervisionModuleTriggerInstances()) {
			startCompString += SEP_PATTERN_L + moduleImpl.getName() + "_container__START__" + modTrigInstance.getName() + "(context);" + LF;
		}

		startCompString += "   }" + LF + "   else" + LF + "   {" + LF + "      /*  Log that we have been called in the incorrect state */" + LF + SEP_PATTERN_E + LF + SEP_PATTERN_G + componentImplementation.getName() + ": Incorrect comp state change order = START\");" + LF + SEP_PATTERN_L + moduleImpl.getName() + "_container__log_warning(context, logMessage);" + LF + "   }" + LF + LF +

				"   " + moduleImpl.getName() + "_container__component_state__cancel_write_access(context, &stateHandle);" + LF + LF +

				"}" + LF + LF;
		return startCompString;
	}

	@Override
	public void writeVDUpdated(String vdOpName) {
		codeStringBuilder.append(SEP_PATTERN_131 + moduleImplName + "__" + vdOpName + "__updated(" + moduleImplName + "__context* context, const ECOA__return_status status, " + moduleImplName + "_container__" + vdOpName + "_handle *data_handle)");

		if (isType.equals(SEP_PATTERN_F)) {
			codeStringBuilder.append(";" + LF + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(LF + "{" + LF + SEP_PATTERN_I + LF + "}" + LF + LF);
		}
	}

}
