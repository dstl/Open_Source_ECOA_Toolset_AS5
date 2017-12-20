/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.containerapi;

import java.nio.file.Path;

import com.iawg.ecoa.ToolConfig;
import com.iawg.ecoa.TypesProcessorCPP;
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
 * methods of that class in a way that is specific to the C++ language.
 * 
 *
 */
public class ContainerWriterCPP extends ContainerWriter {
	private static final String SEP_PATTERN_71 = "header";
	private static final String SEP_PATTERN_A = "         (const ECOA::log &log";
	private static final String SEP_PATTERN_B = "      ECOA::return_status ";
	private static final String SEP_PATTERN_C = "ECOA::return_status Container::";
	private static final String SEP_PATTERN_D = "#include \"";
	private static final String SEP_PATTERN_E = "_handle &data_handle";
	private static final String SEP_PATTERN_F = "         ";
	private static final String SEP_PATTERN_G = "         (";
	private boolean addComma = false;

	public ContainerWriterCPP(SystemModel systemModel, ToolConfig toolConfig, String isType, Path outputDir, SM_ComponentImplementation compImpl, SM_ModuleImpl moduleImpl) {
		super(systemModel, toolConfig, isType, outputDir, compImpl, moduleImpl);
	}

	@Override
	public void close() {
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("         // Other container technical data will accessible through the incomplete" + LF + "         // structure defined here:" + LF + "         struct platform_hook;" + LF + LF +

					"         // The constructor of the Container shall have the following signature:" + LF + "         Container(platform_hook* hook);" + LF + LF +

					"      private:" + LF +

					"         // private data for this container implementation is declared as a" + LF + "         // private struct within the implementation" + LF + "         platform_hook *hook;" + LF + LF +

					"   }; /* Container */" + LF + "} /* " + moduleImplName + " */" + LF + LF +

					"#endif  /* _" + moduleImplName.toUpperCase() + "_CONTAINER_HPP */" + LF);
		} else {
			codeStringBuilder.append("} /* " + moduleImplName + " */" + LF + LF);
		}

		super.close();
	}

	@Override
	public void open() {
		if (isType.equals(SEP_PATTERN_71)) {
			super.openFile(outputDir.resolve(moduleImplName + "_container.hpp"));
		} else if (isType.equals("body")) {
			super.openFile(outputDir.resolve(moduleImplName + "_container.cpp.test"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Not required for API generation - TODO look at ways to stop this
		// being required.
	}

	@Override
	public void writeCancelWriteAccess(String verData) {
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append(SEP_PATTERN_B + verData + "__cancel_write_access" + LF + SEP_PATTERN_G + verData + SEP_PATTERN_E);
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append(SEP_PATTERN_C + verData + "__cancel_write_access" + LF + "   (" + verData + SEP_PATTERN_E);
			writeEndParameters();
		}
	}

	@Override
	public void writeConstParameter(SM_OperationParameter para) {
		String spacer = "";
		if (isType.equals(SEP_PATTERN_71)) {
			spacer = "          ";
		} else if (isType.equals("body")) {
			spacer = "    ";
		}

		if (addComma) {
			codeStringBuilder.append("," + LF);
		} else {
			addComma = true;
			spacer = "";
		}

		codeStringBuilder.append(spacer + "const " + TypesProcessorCPP.convertParameterToCPP(para.getType()));
		if (para.getType().isSimple()) {
			codeStringBuilder.append(" ");
		} else {
			codeStringBuilder.append(" &");
		}
		codeStringBuilder.append(para.getName());
	}

	@Override
	public void writeEndParameters() {
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append(");" + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(")" + LF + "{" + LF + "   // Container code here" + LF + "}");
		}
		codeStringBuilder.append(LF + LF);
	}

	@Override
	public void writeEndParametersNoStatusReturn() {
		/* C++ is the same as writeEndParameters... */
		writeEndParameters();
	}

	@Override
	public void writeEventSend(String eventName) {
		String spacer = "";
		if (isType.equals(SEP_PATTERN_71)) {
			spacer = SEP_PATTERN_F;
			codeStringBuilder.append("      void ");
		} else if (isType.equals("body")) {
			spacer = "   ";
			codeStringBuilder.append("void Container::");
		}
		codeStringBuilder.append(eventName + "__send" + LF + spacer + "(");
		addComma = false;
	}

	@Override
	public void writeGetProperty(SM_ModuleTypeProperty property) {
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      void get_" + property.getName() + "_value(" + TypesProcessorCPP.convertParameterToCPP(property.getType()) + " &value");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Container::get_" + property.getName() + "_value(" + TypesProcessorCPP.convertParameterToCPP(property.getType()) + " &value");
			writeEndParameters();
		}
	}

	@Override
	public void writeGetReadAccess(String verData) {
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append(SEP_PATTERN_B + verData + "__get_read_access" + LF + SEP_PATTERN_G + verData + SEP_PATTERN_E);
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append(SEP_PATTERN_C + verData + "__get_read_access" + LF + "   (" + verData + SEP_PATTERN_E);
			writeEndParameters();
		}
	}

	@Override
	public void writeGetRequiredAvailability() {
		// Only generate if there is at least one required service
		if (componentImplementation.getCompType().getReferenceInstancesList().size() > 0) {
			if (isType.equals(SEP_PATTERN_71)) {
				codeStringBuilder.append("      ECOA::return_status get_service_availability" + LF + "         (reference_id instance," + LF + "          ECOA::boolean8 &available");
				writeEndParameters();
			} else if (isType.equals("body")) {
				codeStringBuilder.append("ECOA::return_status Container::get_service_availability" + LF + "   (reference_id instance," + LF + "    ECOA::boolean8 &available");
				writeEndParameters();
			}
		}
	}

	@Override
	public void writeGetWriteAccess(String verData) {

		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append(SEP_PATTERN_B + verData + "__get_write_access" + LF + SEP_PATTERN_G + verData + SEP_PATTERN_E);
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append(SEP_PATTERN_C + verData + "__get_write_access" + LF + "   (" + verData + SEP_PATTERN_E);
			writeEndParameters();
		}
	}

	private void writeLifecycle(SM_Object instance) {
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      void get_lifecycle_state__" + instance.getName() + LF + "         (ECOA::module_states_type &current_state");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Container::get_lifecycle_state__" + instance.getName() + LF + "   (ECOA::module_states_type &current_state");
			writeEndParameters();

		}

		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      ECOA::return_status STOP__" + instance.getName() + "(");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("ECOA::return_status Container::STOP__" + instance.getName() + "(");
			writeEndParameters();
		}

		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      ECOA::return_status START__" + instance.getName() + "(");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("ECOA::return_status Container::START__" + instance.getName() + "(");
			writeEndParameters();
		}

		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      ECOA::return_status INITIALIZE__" + instance.getName() + "(");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("ECOA::return_status Container::INITIALIZE__" + instance.getName() + "(");
			writeEndParameters();
		}

		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      ECOA::return_status SHUTDOWN__" + instance.getName() + "(");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("ECOA::return_status Container::SHUTDOWN__" + instance.getName() + "(");
			writeEndParameters();
		}
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

		codeStringBuilder.append("      /* Logging and fault management services API */" + LF);

		// Write the logging and fault management services API

		// TRACE
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      void log_trace" + LF + SEP_PATTERN_A);
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Container::log_trace(const ECOA::log &log");
			writeEndParameters();
		}

		// DEBUG
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      void log_debug" + LF + SEP_PATTERN_A);
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Container::log_debug(const ECOA::log &log");
			writeEndParameters();
		}

		// INFO
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      void log_info" + LF + SEP_PATTERN_A);
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Container::log_info(const ECOA::log &log");
			writeEndParameters();
		}

		// WARNING
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      void log_warning" + LF + SEP_PATTERN_A);
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Container::log_warning(const ECOA::log &log");
			writeEndParameters();
		}

		// ERROR
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      void raise_error" + LF + SEP_PATTERN_A);
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Container::raise_error(const ECOA::log &log");
			writeEndParameters();
		}

		// FATAL ERROR
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      void raise_fatal_error" + LF + SEP_PATTERN_A);
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Container::raise_fatal_error(const ECOA::log &log");
			writeEndParameters();
		}
	}

	@Override
	public void writeParameter(SM_OperationParameter para) {
		String spacer = "";
		if (isType.equals(SEP_PATTERN_71)) {
			spacer = "          ";
		} else if (isType.equals("body")) {
			spacer = "    ";
		}

		if (addComma) {
			codeStringBuilder.append("," + LF);
		} else {
			addComma = true;
			spacer = "";
		}

		codeStringBuilder.append(spacer + TypesProcessorCPP.convertParameterToCPP(para.getType()) + " &" + para.getName());
	}

	@Override
	public void writePInfo(String PInfoName, boolean writeable) {
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      ECOA::return_status read_" + PInfoName + LF + "         (ECOA::byte *memory_address," + LF + "          ECOA::uint32 in_size," + LF + "          ECOA::uint32 *out_size");
			writeEndParameters();

			if (writeable) {
				codeStringBuilder.append("      ECOA::return_status write_" + PInfoName + LF + "         (ECOA::byte *memory_address," + LF + "          ECOA::uint32 in_size");
				writeEndParameters();
			}

			codeStringBuilder.append("      ECOA::return_status seek_" + PInfoName + LF + "         (ECOA::int32 offset," + LF + "          ECOA::seek_whence_type whence," + LF + "          ECOA::uint32 *new_position");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("ECOA::return_status Container::read_" + PInfoName + LF + "   (ECOA::byte *memory_address," + LF + "    ECOA::uint32 in_size," + LF + "    ECOA::uint32 *out_size");
			writeEndParameters();

			if (writeable) {
				codeStringBuilder.append("ECOA::return_status Container::write_" + PInfoName + LF + "   (ECOA::byte *memory_address," + LF + "    ECOA::uint32 in_size");
				writeEndParameters();
			}

			codeStringBuilder.append("ECOA::return_status Container::seek_" + PInfoName + LF + "   (ECOA::int32 offset," + LF + "    ECOA::seek_whence_type whence," + LF + "    ECOA::uint32 *new_position");
			writeEndParameters();
		}
	}

	@Override
	public void writePreamble() {
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("/*" + LF + " * @file " + moduleImplName + "_container.hpp" + LF + " * Container Interface class header for Module " + moduleImplName + LF + " * Generated automatically from specification; do not modify here" + LF + " */" + LF + "#if !defined(_" + moduleImplName.toUpperCase() + "_CONTAINER_HPP)" + LF + "#define _" + moduleImplName.toUpperCase() + "_CONTAINER_HPP" + LF + LF +

					SEP_PATTERN_D + moduleImplName + "_container_types.hpp\"" + LF +

					// Include the types file(s)
					"/* Include the types from the XML types files */" + LF);

			for (SM_Namespace use : componentImplementation.getUses()) {
				codeStringBuilder.append(SEP_PATTERN_D + use.getName().replaceAll("\\.", "__") + ".hpp" + "\"" + LF + LF);
			}

			codeStringBuilder.append("namespace " + moduleImplName + LF + "{" + LF + "   class " + "Container" + LF + "   {" + LF + LF +

					"      public:" + LF + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append("/*" + LF + " * @file " + moduleImplName + "_container.cpp" + LF + " * Container Interface class for Module " + moduleImplName + LF + " */" + LF + LF +

					SEP_PATTERN_D + moduleImplName + "_container.hpp\"" + LF + LF + "namespace " + moduleImplName + LF + "{" + LF + LF);

			codeStringBuilder.append("Container::Container(platform_hook *hook");
			writeEndParameters();
		}

		// Write the get_last_operation_timestamp API
		codeStringBuilder.append("      /* get_last_operation_timestamp API */" + LF);
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      void get_last_operation_timestamp(ECOA::timestamp &timestamp");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Container::get_last_operation_timestamp(ECOA::timestamp &timestamp");
			writeEndParameters();
		}

	}

	@Override
	public void writePublishWriteAccess(String verData) {
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append(SEP_PATTERN_B + verData + "__publish_write_access" + LF + SEP_PATTERN_G + verData + "_handle &data_handle");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append(SEP_PATTERN_C + verData + "__publish_write_access" + LF + "   (" + verData + "_handle &data_handle");
			writeEndParameters();
		}
	}

	@Override
	public void writeRecoveryAction() {
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      ECOA::return_status recovery_action" + LF + "         (ECOA::recovery_action_type recovery_action," + LF + "          ECOA::asset_id asset_id," + LF + "          ECOA::asset_type asset_type");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("ECOA::return_status Container::recovery_action" + LF + "         (ECOA::recovery_action_type recovery_action," + LF + "          ECOA::asset_id asset_id," + LF + "          ECOA::asset_type asset_type");
			writeEndParameters();
		}
	}

	@Override
	public void writeReleaseReadAccess(String verData) {

		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append(SEP_PATTERN_B + verData + "__release_read_access" + LF + SEP_PATTERN_G + verData + "_handle &data_handle");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append(SEP_PATTERN_C + verData + "__release_read_access" + LF + "   (" + verData + "_handle &data_handle");
			writeEndParameters();
		}
	}

	@Override
	public void writeRequestAsynchronous(String reqName) {
		String spacer = "";
		if (isType.equals(SEP_PATTERN_71)) {
			spacer = SEP_PATTERN_F;
			codeStringBuilder.append(SEP_PATTERN_B);
		} else if (isType.equals("body")) {
			spacer = "   ";
			codeStringBuilder.append(SEP_PATTERN_C);
		}
		codeStringBuilder.append(reqName + "__request_async" + LF + spacer + "(const ECOA::int32 &ID");
		addComma = true;
	}

	@Override
	public void writeRequestSynchronous(String reqName) {
		String spacer = "";
		if (isType.equals(SEP_PATTERN_71)) {
			spacer = SEP_PATTERN_F;
			codeStringBuilder.append(SEP_PATTERN_B);
		} else if (isType.equals("body")) {
			spacer = "   ";
			codeStringBuilder.append(SEP_PATTERN_C);
		}
		codeStringBuilder.append(reqName + "__request_sync" + LF + spacer + "(");
		addComma = false;

	}

	@Override
	public void writeResponseSend(String reqName) {
		String spacer = "";
		if (isType.equals(SEP_PATTERN_71)) {
			spacer = SEP_PATTERN_F;
			codeStringBuilder.append(SEP_PATTERN_B);
		} else if (isType.equals("body")) {
			spacer = "   ";
			codeStringBuilder.append(SEP_PATTERN_C);
		}
		codeStringBuilder.append(reqName + "__response_send" + LF + spacer + "(const ECOA::uint32 ID");
		addComma = true;

	}

	@Override
	public void writeSaveNonVolatileContext() {
		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      void save_non_volatile_context(");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Container::save_non_volatile_context(");
			writeEndParameters();
		}
	}

	@Override
	public void writeSetProvidedAvailability() {
		// Only generate if there is at least one provided service
		if (componentImplementation.getCompType().getServiceInstancesList().size() > 0) {
			if (isType.equals(SEP_PATTERN_71)) {
				codeStringBuilder.append("      ECOA::return_status set_service_availability" + LF + "         (service_id instance," + LF + "          ECOA::boolean8 available");
				writeEndParameters();
			} else if (isType.equals("body")) {
				codeStringBuilder.append("ECOA::return_status Container::set_service_availability" + LF + "   (service_id instance," + LF + "    ECOA::boolean8 available");
				writeEndParameters();
			}
		}
	}

	@Override
	public void writeTimeResolutionServices() {
		// Write the time resolution services API
		codeStringBuilder.append("      /* Time resolution services API */" + LF);

		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      void get_relative_local_time_resolution" + LF + "         (ECOA::duration &relative_local_time_resolution");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Container::get_relative_local_time_resolution" + LF + "   (ECOA::duration &relative_local_time_resolution");
			writeEndParameters();
		}

		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      void get_UTC_time_resolution" + LF + "         (ECOA::duration &utc_time_resolution");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Container::get_UTC_time_resolution" + LF + "   (ECOA::duration &utc_time_resolution");
			writeEndParameters();
		}

		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      void get_absolute_system_time_resolution" + LF + "         (ECOA::duration &absolute_system_time_resolution");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Container::get_absolute_system_time_resolution" + LF + "   (ECOA::duration &absolute_system_time_resolution");
			writeEndParameters();
		}
	}

	@Override
	public void writeTimeServices() {
		// Write the time services API
		codeStringBuilder.append("      /* Time services API */" + LF);

		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      void get_relative_local_time" + LF + "         (ECOA::hr_time &relative_local_time");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Container::get_relative_local_time" + LF + "   (ECOA::hr_time &relative_local_time");
			writeEndParameters();
		}

		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      ECOA::return_status get_UTC_time" + LF + "         (ECOA::global_time &utc_time");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("ECOA::return_status Container::get_UTC_time" + LF + "   (ECOA::global_time &utc_time");
			writeEndParameters();
		}

		if (isType.equals(SEP_PATTERN_71)) {
			codeStringBuilder.append("      ECOA::return_status get_absolute_system_time" + LF + "         (ECOA::global_time &absolute_system_time");
			writeEndParameters();
		} else if (isType.equals("body")) {
			codeStringBuilder.append("ECOA::return_status Container::get_absolute_system_time" + LF + "   (ECOA::global_time &absolute_system_time");
			writeEndParameters();
		}
	}

}
