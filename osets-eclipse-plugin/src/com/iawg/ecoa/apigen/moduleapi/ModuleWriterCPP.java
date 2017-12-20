/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.moduleapi;

import java.nio.file.Files;
import java.nio.file.Path;

import com.iawg.ecoa.ToolConfig;
import com.iawg.ecoa.TypesProcessorCPP;
import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventReceivedOp;
import com.iawg.ecoa.systemmodel.types.SM_Namespace;

/**
 * This class extends the abstract class ModuleWriter and implements the methods
 * of that class in a way that is specific to the C++ language.
 */
public class ModuleWriterCPP extends ModuleWriter {
	private static final String SEP_PATTERN_51 = "void Module::";
	private static final String SEP_PATTERN_A = "void ";
	private static final String SEP_PATTERN_B = "      void ";
	private static final String SEP_PATTERN_C = "header";
	private static final String SEP_PATTERN_D = "   /* User Code Here */";
	private static final String SEP_PATTERN_E = "#include \"";

	private boolean addComma = false;

	public ModuleWriterCPP(SystemModel systemModel, ToolConfig toolConfig, String isType, Path outputDir, SM_ComponentImplementation compImpl, SM_ModuleImpl moduleImpl) {
		super(systemModel, toolConfig, isType, outputDir, compImpl, moduleImpl);
	}

	@Override
	public void close() {
		if (isType.equals(SEP_PATTERN_C)) {
			// Header
			codeStringBuilder.append("      private:" + LF + "         // the Module Implementation shall hold a Container pointer" + LF + "         // which is passed within the constructor" + LF + "         Container* container;" + LF + LF +

					"         // user data (which does not belong to the warm start context) for this" + LF + "         // module implementation must be declared here within a standard structure:" + LF + "         user_context user;" + LF + LF +

					"   }; /* Module */" + LF + LF +

					"   extern \"C\" {" + LF + LF +

					"      Module* " + moduleImplName + "__new_instance" + LF + "         (Container* container);" + LF + LF +

					"   }" + LF + LF +

					"} /* " + moduleImplName + " */" + LF + LF +

					"#endif  /* _" + moduleImplName.toUpperCase() + "_HPP */" + LF);
		} else {
			codeStringBuilder.append("} /* " + moduleImplName + " */" + LF + LF);
		}

		super.close();
	}

	@Override
	public void open() {
		if (isType.equals(SEP_PATTERN_C)) {
			if (!Files.exists(outputDir.resolve(moduleImplName + ".hpp")) || toolConfig.isOverwriteFiles()) {
				super.openFile(outputDir.resolve(moduleImplName + ".hpp"));
			} else {
				super.openFile(outputDir.resolve(moduleImplName + ".hpp.new"));
			}
		} else if (isType.equals("body")) {
			if (!Files.exists(outputDir.resolve(moduleImplName + ".cpp")) || toolConfig.isOverwriteFiles()) {
				super.openFile(outputDir.resolve(moduleImplName + ".cpp"));
			} else {
				super.openFile(outputDir.resolve(moduleImplName + ".cpp.new"));
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
		String spacer = "          ";

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
		if (isType.equals(SEP_PATTERN_C)) {
			codeStringBuilder.append(");" + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(")" + LF + "{" + LF + SEP_PATTERN_D + LF + "}" + LF);
		}
		codeStringBuilder.append(LF);
	}

	@Override
	public void writeErrorNotification(SM_Object instance) {
		// Note - this method should only be called if a supervision module...
		if (isType.equals(SEP_PATTERN_C)) {
			codeStringBuilder.append("      void error_notification__" + instance.getName() + LF + "         (const ECOA::module_error_type module_error_type);" + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Module::error_notification__" + instance.getName() + LF + "   (const ECOA::module_error_type module_error_type)" + LF + "{" + LF + SEP_PATTERN_D + LF + "}" + LF);
		}
		codeStringBuilder.append(LF);
	}

	@Override
	public void writeEventReceived(SM_EventReceivedOp eventRxOp) {
		writeEventReceived(eventRxOp.getName());

		// Add parameters
		for (SM_OperationParameter opParam : eventRxOp.getInputs()) {
			writeConstParameter(opParam);
		}
		writeEndParameters();
	}

	private void writeEventReceived(String name) {
		if (isType.equals(SEP_PATTERN_C)) {
			codeStringBuilder.append(SEP_PATTERN_B + name + "__received" + LF + "         (");
		} else if (isType.equals("body")) {

			codeStringBuilder.append(SEP_PATTERN_51 + name + "__received" + LF + "   (");
		}
		addComma = false;
	}

	@Override
	public void writeFaultHandlerNotification() {
		// Note - this method should only be called if a supervision module...
		if (isType.equals(SEP_PATTERN_C)) {
			codeStringBuilder.append("      void error_notification" + LF + "         (const ECOA::timestamp &timestamp," + LF + "          ECOA::asset_id asset_id," + LF + "          ECOA::asset_type asset_type," + LF + "          ECOA::error_type error_type);" + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Module::error_notification" + LF + "         (const ECOA::timestamp &timestamp," + LF + "          ECOA::asset_id asset_id," + LF + "          ECOA::asset_type asset_type," + LF + "          ECOA::error_type error_type)" + LF + "{" + LF + SEP_PATTERN_D + LF + "}" + LF);
		}
		codeStringBuilder.append(LF);

	}

	private void writeHeaderOrBodyLifecycle() {
		if (isType.equals(SEP_PATTERN_C)) {
			codeStringBuilder.append("         ();" + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append("   ()" + LF + "{" + LF + SEP_PATTERN_D + LF + "}");
		}
	}

	@Override
	public void writeLifecycleNotification(SM_Object instance) {
		if (isType.equals(SEP_PATTERN_C)) {
			codeStringBuilder.append("      void lifecycle_notification__" + instance.getName() + LF + "         (ECOA::module_states_type previous_state," + LF + "          ECOA::module_states_type new_state);" + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append("void Module::lifecycle_notification__" + instance.getName() + LF + "   (ECOA::module_states_type previous_state," + LF + "    ECOA::module_states_type new_state)" + LF + "{" + LF + SEP_PATTERN_D + LF + "}" + LF);
		}
		codeStringBuilder.append(LF);
	}

	@Override
	public void writeLifecycleServices() {

		// Write the runtime lifecycle API
		String headerBody = null;
		String spacer = null;

		if (isType.equals(SEP_PATTERN_C)) {
			headerBody = "";
			spacer = "      ";
		} else if (isType.equals("body")) {
			headerBody = "Module::";
			spacer = "";
		}

		codeStringBuilder.append("      /* Runtime lifecycle API */" + LF + spacer + SEP_PATTERN_A + headerBody + "INITIALIZE__received" + LF);
		writeHeaderOrBodyLifecycle();
		codeStringBuilder.append(LF);

		codeStringBuilder.append(spacer + SEP_PATTERN_A + headerBody + "START__received" + LF);
		writeHeaderOrBodyLifecycle();
		codeStringBuilder.append(LF);

		codeStringBuilder.append(spacer + SEP_PATTERN_A + headerBody + "STOP__received" + LF);
		writeHeaderOrBodyLifecycle();
		codeStringBuilder.append(LF);

		codeStringBuilder.append(spacer + SEP_PATTERN_A + headerBody + "SHUTDOWN__received" + LF);
		writeHeaderOrBodyLifecycle();
		codeStringBuilder.append(LF);

		codeStringBuilder.append(spacer + SEP_PATTERN_A + headerBody + "REINITIALIZE__received" + LF);
		writeHeaderOrBodyLifecycle();
		codeStringBuilder.append(LF);

	}

	@Override
	public void writeParameter(SM_OperationParameter para) {
		// Write to header or body
		String spacer = "          ";

		if (addComma) {
			codeStringBuilder.append("," + LF);
		} else {
			addComma = true;
			spacer = "";
		}

		codeStringBuilder.append(spacer + TypesProcessorCPP.convertParameterToCPP(para.getType()) + " &" + para.getName());
	}

	@Override
	public void writePreamble() {
		if (isType.equals(SEP_PATTERN_C)) {
			// Header file
			codeStringBuilder.append("/*" + LF + " * @file " + moduleImplName + ".hpp" + LF + " * Module Interface class header for Module " + moduleImplName + LF + " * The user shall write this concrete class corresponding to the" + LF + " * Module Implementation itself." + LF + " */" + LF + LF +

					"#if !defined(_" + moduleImplName.toUpperCase() + "_HPP)" + LF + "#define _" + moduleImplName.toUpperCase() + "_HPP" + LF + LF +

					SEP_PATTERN_E + moduleImplName + "_user_context.hpp\"" + LF + SEP_PATTERN_E + moduleImplName + "_container.hpp\"" + LF + LF);

			for (SM_Namespace use : componentImplementation.getUses()) {
				codeStringBuilder.append(SEP_PATTERN_E + use.getName().replaceAll("\\.", "__") + ".hpp" + "\"" + LF + LF);
			}

			codeStringBuilder.append("namespace " + moduleImplName + LF + "{" + LF + "   class Module" + LF + "   {" + LF + LF +

					"      public:" + LF +

					"         // The constructor of the Component shall have the following" + LF + "         // signature:" + LF + "         Module(Container* container);" + LF + LF +

					"         // Warm Start data for this module implementation must be declared here as a" + LF + "         // single attribute named 'warm_start' which may be of a user defined type" + LF + "         warm_start_context warm_start;" + LF + LF +

					"         // All the operations for this Module implementation will be" + LF + "         // declared as public concrete methods here" + LF + LF);

		} else if (isType.equals("body")) {
			// Body file
			codeStringBuilder.append("/*" + LF + " * @file " + moduleImplName + ".cpp" + LF + " * Module Implementation class for Module " + moduleImplName + LF + " */" + LF + LF +

					SEP_PATTERN_E + moduleImplName + ".hpp" + "\"" + LF + LF +

					"namespace " + moduleImplName + LF + "{" + LF + LF +

					"extern \"C\" {" + LF + LF +

					"   Module* " + moduleImplName + "__new_instance" + LF + "      (Container* container)" + LF + "   {" + LF + "      return new Module(container);" + LF + "   }" + LF + "}" + LF + LF +

					// Write the constructor
					"Module::Module" + LF + "   (Container* container)" + LF + "{" + LF + "   /* initializes the container pointer */" + LF + "   this->container = container;" + LF + "}" + LF + LF);
		}
	}

	@Override
	public void writeRequestReceived(String name) {
		if (isType.equals(SEP_PATTERN_C)) {
			codeStringBuilder.append(SEP_PATTERN_B + name + "__request_received" + LF + "         (const ECOA::uint32 ID");
		} else if (isType.equals("body")) {
			codeStringBuilder.append(SEP_PATTERN_51 + name + "__request_received" + LF + "   (const ECOA::uint32 ID");

		}
		addComma = true;
	}

	@Override
	public void writeResponseReceivedAsynchonous(String name) {
		if (isType.equals(SEP_PATTERN_C)) {
			codeStringBuilder.append(SEP_PATTERN_B + name + "__response_received" + LF + "         (const ECOA::uint32 ID," + LF + "          const ECOA::return_status status");
		} else if (isType.equals("body")) {
			codeStringBuilder.append(SEP_PATTERN_51 + name + "__response_received" + LF + "   (const ECOA::uint32 ID," + LF + "    const ECOA::return_status status");
		}
		addComma = true;
	}

	@Override
	public void writeServiceAvailabilityNotifications() {
		// Only generate if there is at least one required service
		if (componentImplementation.getCompType().getReferenceInstancesList().size() > 0) {
			if (isType.equals(SEP_PATTERN_C)) {
				// Service availability changed
				codeStringBuilder.append("      void service_availability_changed" + LF + "         (reference_id instance," + LF + "          ECOA::boolean8 available);" + LF + LF);

				// Service provider changed
				codeStringBuilder.append("      void service_provider_changed" + LF + "         (reference_id instance);" + LF + LF);
			} else if (isType.equals("body")) {
				// Service availability changed
				codeStringBuilder.append("void Module::service_availability_changed" + LF + "   (reference_id instance," + LF + "    ECOA::boolean8 available)" + LF + "{" + LF + SEP_PATTERN_D + LF + "}" + LF + LF);

				// Service provider changed
				codeStringBuilder.append("void Module::service_provider_changed" + LF + "   (reference_id instance)" + LF + "{" + LF + SEP_PATTERN_D + LF + "}" + LF + LF);
			}
		}
	}

	@Override
	public void writeVDUpdated(String vdOpName) {

		if (isType.equals(SEP_PATTERN_C)) {
			codeStringBuilder.append(SEP_PATTERN_B + vdOpName + "__updated(const ECOA::return_status status, " + vdOpName + "_handle &data_handle);" + LF + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(SEP_PATTERN_51 + vdOpName + "__updated(const ECOA::return_status status, " + vdOpName + "_handle &data_handle)" + LF + "{" + LF + SEP_PATTERN_D + LF + "}" + LF + LF);
		}
	}

}
