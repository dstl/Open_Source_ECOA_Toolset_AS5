/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.containerapi;

import java.nio.file.Path;

import com.iawg.ecoa.ToolConfig;
import com.iawg.ecoa.TypesProcessorC;
import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleTypeProperty;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;
import com.iawg.ecoa.systemmodel.types.SM_Namespace;

/**
 * This class extends the abstract class ContainerWriter and implements the
 * methods of that class in a way that is specific to the C language.
 * 
 * @author Shaun Cullimore
 * 
 */
public class ContainerWriterC extends ContainerWriter {
	private static final String SEP_PATTERN_91 = "void ";
	private static final String SEP_PATTERN_A = "    */";
	private static final String SEP_PATTERN_B = "_handle* data_handle";
	private static final String SEP_PATTERN_C = "__context* context,";
	private static final String SEP_PATTERN_D = "header";
	private static final String SEP_PATTERN_E = "_container__";
	private static final String SEP_PATTERN_F = "__context* context, const ECOA__log log";
	private static final String SEP_PATTERN_G = "ECOA__return_status ";
	private static final String SEP_PATTERN_H = "#include \"";
	private static final String SEP_PATTERN_I = "__context* context";

	public ContainerWriterC(SystemModel systemModel, ToolConfig toolConfig, String isType, Path outputDir, SM_ComponentImplementation compImpl, SM_ModuleImpl moduleImpl) {
		super(systemModel, toolConfig, isType, outputDir, compImpl, moduleImpl);
	}

	@Override
	public void close() {
		if (isType.equals(SEP_PATTERN_D)) {
			// Close the header
			codeStringBuilder.append("#if defined(__cplusplus)" + LF + "}" + LF + "#endif /* __cplusplus */" + LF + LF +

					"#endif  /* _" + moduleImplName.concat("_container").toUpperCase() + "_H */" + LF);
		}

		super.close();
	}

	@Override
	public void open() {
		if (isType.equals(SEP_PATTERN_D)) {
			super.openFile(outputDir.resolve(moduleImplName + "_container.h"));
		} else if (isType.equals("body")) {
			super.openFile(outputDir.resolve(moduleImplName + "_container.c.test"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Not required for API generation - TODO look at ways to stop this
		// being required.
	}

	@Override
	public void writeCancelWriteAccess(String verData) {
		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + SEP_PATTERN_E + verData + "__cancel_write_access" + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    " + moduleImplName + SEP_PATTERN_E + verData + SEP_PATTERN_B);
		writeEndParameters();
	}

	@Override
	public void writeConstParameter(SM_OperationParameter para) {
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
		codeStringBuilder.append(")");
		if (isType.equals(SEP_PATTERN_D)) {
			codeStringBuilder.append(";" + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(LF + "{" + LF + "}" + LF);
		}
		codeStringBuilder.append(LF);
	}

	@Override
	public void writeEndParametersNoStatusReturn() {
		/* C is the same as writeEndParameters... */
		writeEndParameters();
	}

	@Override
	public void writeEventSend(String eventName) {
		codeStringBuilder.append(SEP_PATTERN_91 + moduleImplName + SEP_PATTERN_E + eventName + "__send" + LF + "   (" + moduleImplName + SEP_PATTERN_I);
	}

	@Override
	public void writeGetProperty(SM_ModuleTypeProperty property) {
		codeStringBuilder.append(SEP_PATTERN_91 + moduleImplName + "_container__get_" + property.getName() + "_value" + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    " + TypesProcessorC.convertParameterToC(property.getType()) + "* value");
		writeEndParameters();
	}

	@Override
	public void writeGetReadAccess(String verData) {
		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + SEP_PATTERN_E + verData + "__get_read_access" + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    " + moduleImplName + SEP_PATTERN_E + verData + SEP_PATTERN_B);
		writeEndParameters();
		codeStringBuilder.append(LF);

		if (moduleImpl.isInstrument()) {
			codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + SEP_PATTERN_E + verData + "__get_internal_read_access" + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    " + moduleImplName + SEP_PATTERN_E + verData + SEP_PATTERN_B);
			writeEndParameters();
			codeStringBuilder.append(LF);
		}
	}

	@Override
	public void writeGetRequiredAvailability() {
		// Only generate if there is at least one required service
		if (componentImplementation.getCompType().getReferenceInstancesList().size() > 0) {
			codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + "_container__get_service_availability" + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    " + moduleImplName + "_container__reference_id instance," + LF + "    ECOA__boolean8 *available");
			writeEndParameters();
		}
	}

	@Override
	public void writeGetWriteAccess(String verData) {
		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + SEP_PATTERN_E + verData + "__get_write_access" + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    " + moduleImplName + SEP_PATTERN_E + verData + SEP_PATTERN_B);
		writeEndParameters();
	}

	private void writeLifecycle(SM_Object moduleInstance) {
		codeStringBuilder.append(SEP_PATTERN_91 + moduleImplName + "_container__get_lifecycle_state__" + moduleInstance.getName() + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    ECOA__module_states_type* current_state");
		writeEndParameters();

		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + "_container__STOP__" + moduleInstance.getName() + LF + "   (" + moduleImplName + SEP_PATTERN_I);
		writeEndParameters();

		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + "_container__START__" + moduleInstance.getName() + LF + "   (" + moduleImplName + SEP_PATTERN_I);
		writeEndParameters();

		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + "_container__INITIALIZE__" + moduleInstance.getName() + LF + "   (" + moduleImplName + SEP_PATTERN_I);
		writeEndParameters();

		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + "_container__SHUTDOWN__" + moduleInstance.getName() + LF + "   (" + moduleImplName + SEP_PATTERN_I);
		writeEndParameters();
	}

	@Override
	public void writeLifecycleServices() {

		for (SM_ModuleInstance moduleInstance : componentImplementation.getModuleInstances().values()) {
			// Only do this if it's not a supervisor
			if (!moduleInstance.getImplementation().getModuleType().getIsSupervisor()) {
				writeLifecycle(moduleInstance);
			}
		}

		for (SM_TriggerInstance triggerInstance : componentImplementation.getTriggerInstances().values()) {
			writeLifecycle(triggerInstance);
		}

		for (SM_DynamicTriggerInstance dynamicTriggerInstance : componentImplementation.getDynamicTriggerInstances().values()) {
			writeLifecycle(dynamicTriggerInstance);
		}
	}

	@Override
	public void writeLoggingServices() {
		// Write the logging and fault management services API
		codeStringBuilder.append("/* Logging and fault management services API */" + LF + SEP_PATTERN_91 + moduleImplName + "_container__log_trace" + LF + "   (" + moduleImplName + SEP_PATTERN_F);
		writeEndParameters();

		codeStringBuilder.append(SEP_PATTERN_91 + moduleImplName + "_container__log_debug" + LF + "   (" + moduleImplName + SEP_PATTERN_F);
		writeEndParameters();

		codeStringBuilder.append(SEP_PATTERN_91 + moduleImplName + "_container__log_info" + LF + "   (" + moduleImplName + SEP_PATTERN_F);
		writeEndParameters();

		codeStringBuilder.append(SEP_PATTERN_91 + moduleImplName + "_container__log_warning" + LF + "   (" + moduleImplName + SEP_PATTERN_F);
		writeEndParameters();

		codeStringBuilder.append(SEP_PATTERN_91 + moduleImplName + "_container__raise_error" + LF + "   (" + moduleImplName + SEP_PATTERN_F);
		writeEndParameters();

		codeStringBuilder.append(SEP_PATTERN_91 + moduleImplName + "_container__raise_fatal_error" + LF + "   (" + moduleImplName + SEP_PATTERN_F);
		writeEndParameters();
	}

	@Override
	public void writeParameter(SM_OperationParameter para) {
		codeStringBuilder.append("," + LF + "    " + TypesProcessorC.convertParameterToC(para.getType()) + "* " + para.getName());
	}

	@Override
	public void writePInfo(String PInfoName, boolean writeable) {

		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + "_container__read_" + PInfoName + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    ECOA__byte *memory_address," + LF + "    ECOA__uint32 in_size," + LF + "    ECOA__uint32 *out_size");
		writeEndParameters();

		if (writeable) {
			codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + "_container__write_" + PInfoName + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    ECOA__byte *memory_address," + LF + "    ECOA__uint32 in_size");
			writeEndParameters();
		}

		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + "_container__seek_" + PInfoName + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    ECOA__int32 offset," + LF + "    ECOA__seek_whence_type whence," + LF + "    ECOA__uint32 *new_position");
		writeEndParameters();
	}

	@Override
	public void writePreamble() {

		// TODO error handler function IDs

		if (isType.equals(SEP_PATTERN_D)) {
			codeStringBuilder.append("/* @file " + moduleImplName + "_container.h" + LF + " * Container Interface header for Module " + moduleImplName + LF + " * Generated automatically from specification; do not modify here" + LF + " */" + LF + LF +

					"#if !defined(_" + moduleImplName.toUpperCase() + "_CONTAINER_H)" + LF + "#define _" + moduleImplName.toUpperCase() + "_CONTAINER_H" + LF + LF);

			// Include the types file(s)
			codeStringBuilder.append("/* Include the types from the XML types files */" + LF);

			for (SM_Namespace use : componentImplementation.getUses()) {
				codeStringBuilder.append(SEP_PATTERN_H + use.getName().replaceAll("\\.", "__") + ".h" + "\"" + LF + LF);
			}

			// Include reference to the user-defined part of the context
			codeStringBuilder.append("/* Container Types */" + LF + SEP_PATTERN_H + moduleImplName + "_container_types.h\"" + LF + LF +

					SEP_PATTERN_H + moduleImplName + "_user_context.h\"" + LF + LF +

					"#if defined(__cplusplus)" + LF + "extern \"C\" {" + LF + "#endif /* __cplusplus */" + LF + LF +

					"/* Incomplete definition of the technical (platform-dependent) part of the context" + LF + " * (it will be defined privately by the container) */" + LF + "struct " + moduleImplName + "__platform_hook;" + LF + LF +

					"/* Module Context structure declaration */" + LF + "typedef struct " + LF + "{" + LF + "   /*" + LF + "    * the date of the calling operation" + LF + SEP_PATTERN_A + LF + "   ECOA__timestamp operation_timestamp;" + LF + LF +

					"   /*" + LF + "    * Other container technical data will accessible through the pointer" + LF + "    * defined here" + LF + SEP_PATTERN_A + LF + "   struct " + moduleImplName + "__platform_hook *platform_hook;" + LF + LF +

					"   /* the type " + moduleImplName + "_user_context shall be defined by the user" + LF + "    * in the " + moduleImplName + "_user_context.h file to carry the module" + LF + "    * implementation private data" + LF + SEP_PATTERN_A + LF + "   " + moduleImplName + "_user_context user;" + LF + LF +

					"   " + moduleImplName + "_warm_start_context warm_start;" + LF + LF +

					"} " + moduleImplName + "__context;" + LF + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append("/* @file " + moduleImplName + "_container.c" + LF + " * Container Interface for Module " + moduleImplName + LF + " */" + LF + LF);

			// Include the module-specific headers in the container body
			codeStringBuilder.append(SEP_PATTERN_H + moduleImplName + "_container.h\"" + LF + LF);
		}
	}

	@Override
	public void writePublishWriteAccess(String verData) {
		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + SEP_PATTERN_E + verData + "__publish_write_access" + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    " + moduleImplName + SEP_PATTERN_E + verData + SEP_PATTERN_B);
		writeEndParameters();
	}

	@Override
	public void writeRecoveryAction() {
		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + "_container__recovery_action" + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    ECOA__recovery_action_type recovery_action," + LF + "    ECOA__asset_id asset_id," + LF + "    ECOA__asset_type asset_type");
		writeEndParameters();
	}

	@Override
	public void writeReleaseReadAccess(String verData) {
		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + SEP_PATTERN_E + verData + "__release_read_access" + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    " + moduleImplName + SEP_PATTERN_E + verData + SEP_PATTERN_B);
		writeEndParameters();

		if (moduleImpl.isInstrument()) {
			codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + SEP_PATTERN_E + verData + "__release_internal_read_access" + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    " + moduleImplName + SEP_PATTERN_E + verData + SEP_PATTERN_B);
			writeEndParameters();
		}
	}

	@Override
	public void writeRequestAsynchronous(String reqName) {
		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + SEP_PATTERN_E + reqName + "__request_async" + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    ECOA__uint32* ID");
	}

	@Override
	public void writeRequestSynchronous(String reqName) {
		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + SEP_PATTERN_E + reqName + "__request_sync" + LF + "   (" + moduleImplName + SEP_PATTERN_I);

	}

	@Override
	public void writeResponseSend(String reqName) {
		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + SEP_PATTERN_E + reqName + "__response_send" + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    const ECOA__uint32 ID");
	}

	@Override
	public void writeSaveNonVolatileContext() {

		codeStringBuilder.append(SEP_PATTERN_91 + moduleImplName + "_container__save_non_volatile_context" + LF + "   (" + moduleImplName + SEP_PATTERN_I);
		writeEndParameters();
	}

	@Override
	public void writeSetProvidedAvailability() {
		// Only generate if there is at least one provided service
		if (componentImplementation.getCompType().getServiceInstancesList().size() > 0) {
			codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + "_container__set_service_availability" + LF + "   (" + moduleImplName + SEP_PATTERN_C + LF + "    " + moduleImplName + "_container__service_id instance," + LF + "    ECOA__boolean8 available");
			writeEndParameters();
		}
	}

	@Override
	public void writeTimeResolutionServices() {
		// Write the time resolution services API
		codeStringBuilder.append("/* Time resolution services API */" + LF + SEP_PATTERN_91 + moduleImplName + "_container__get_relative_local_time_resolution" + LF + "   (" + moduleImplName + "__context* context, ECOA__duration* relative_local_time_resolution");
		writeEndParameters();

		codeStringBuilder.append(SEP_PATTERN_91 + moduleImplName + "_container__get_UTC_time_resolution" + LF + "   (" + moduleImplName + "__context* context, ECOA__duration* utc_time_resolution");
		writeEndParameters();

		codeStringBuilder.append(SEP_PATTERN_91 + moduleImplName + "_container__get_absolute_system_time_resolution" + LF + "   (" + moduleImplName + "__context* context, ECOA__duration* absolute_system_time_resolution");
		writeEndParameters();

	}

	@Override
	public void writeTimeServices() {
		// Write the time services API
		codeStringBuilder.append("/* Time services API */" + LF + SEP_PATTERN_91 + moduleImplName + "_container__get_relative_local_time" + LF + "   (" + moduleImplName + "__context* context, ECOA__hr_time* relative_local_time");
		writeEndParameters();

		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + "_container__get_UTC_time" + LF + "   (" + moduleImplName + "__context* context, ECOA__global_time* utc_time");
		writeEndParameters();

		codeStringBuilder.append(SEP_PATTERN_G + moduleImplName + "_container__get_absolute_system_time" + LF + "   (" + moduleImplName + "__context* context, ECOA__global_time* absolute_system_time");
		writeEndParameters();
	}

}
