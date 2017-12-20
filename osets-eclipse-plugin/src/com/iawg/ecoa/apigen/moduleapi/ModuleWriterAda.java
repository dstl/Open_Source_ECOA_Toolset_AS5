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
import com.iawg.ecoa.TypesProcessorAda;
import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventReceivedOp;
import com.iawg.ecoa.systemmodel.types.SM_Namespace;

/**
 * This class extends the abstract class ModuleHeaderWriter and implements the
 * methods of that class in a way that is specific to the Ada language.
 *
 */
public class ModuleWriterAda extends ModuleWriter {
	private static final String SEP_PATTERN_111 = "with ";
	private static final String SEP_PATTERN_A = "      null; -- User Code Here";
	private static final String SEP_PATTERN_B = "_Container.Context_Type;";
	private static final String SEP_PATTERN_C = "_Container.Context_Type";
	private static final String SEP_PATTERN_D = "   end ";
	private static final String SEP_PATTERN_E = "   procedure ";
	private static final String SEP_PATTERN_F = "      (Context   : in out ";
	private static final String SEP_PATTERN_G = "-------------------------------------------------------------------";
	private static final String SEP_PATTERN_H = "header";
	private static final String SEP_PATTERN_I = "   is";
	private static final String SEP_PATTERN_J = "   begin";
	private static final String SEP_PATTERN_K = "      (Context : in out ";
	private String opName = null;

	public ModuleWriterAda(SystemModel systemModel, ToolConfig toolConfig, String isType, Path outputDir, SM_ComponentImplementation compImpl, SM_ModuleImpl moduleImpl) {
		super(systemModel, toolConfig, isType, outputDir, compImpl, moduleImpl);
	}

	@Override
	public void close() {
		codeStringBuilder.append("end " + moduleImplName + ";" + LF);
		super.close();
	}

	@Override
	public void open() {
		if (isType.equals(SEP_PATTERN_H)) {
			super.openFile(outputDir.resolve(moduleImplName + ".ads"));
		} else if (isType.equals("body")) {
			if (!Files.exists(outputDir.resolve(moduleImplName + ".adb")) || toolConfig.isOverwriteFiles()) {
				super.openFile(outputDir.resolve(moduleImplName + ".adb"));
			} else {
				super.openFile(outputDir.resolve(moduleImplName + ".adb.new"));
			}
		}
	}

	@Override
	protected void setFileStructure() {
		// Not required for API generation - TODO look at ways to stop this
		// being required.
	}

	private void setOpName(String name) {
		opName = name;
	}

	@Override
	public void writeConstParameter(SM_OperationParameter para) {
		// Write to header or body
		codeStringBuilder.append(";" + LF + "       " + para.getName() + " : in " + TypesProcessorAda.convertParameterToAda(para.getType()));
	}

	@Override
	public void writeEndParameters() {
		if (isType.equals(SEP_PATTERN_H)) {
			codeStringBuilder.append(");" + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(")" + LF + SEP_PATTERN_I + LF + SEP_PATTERN_J + LF + SEP_PATTERN_A + LF + SEP_PATTERN_D + opName + ";" + LF);

		}
		codeStringBuilder.append(LF);
	}

	@Override
	public void writeErrorNotification(SM_Object instance) {
		codeStringBuilder.append("   procedure Error_Notification_" + instance.getName() + LF + SEP_PATTERN_F + moduleImplName + SEP_PATTERN_B + LF + "       Module_Error : in     ECOA.Module_Error_Type)");
		if (isType.equals(SEP_PATTERN_H)) {
			codeStringBuilder.append(";" + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(LF + SEP_PATTERN_I + LF + SEP_PATTERN_J + LF + SEP_PATTERN_A + LF + SEP_PATTERN_D + "Error_Notification_" + instance.getName() + ";" + LF);
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
		// Write to header or body
		codeStringBuilder.append(SEP_PATTERN_E + name + "_Received" + LF + SEP_PATTERN_K + moduleImplName + SEP_PATTERN_C);
		setOpName(name + "_Received");
	}

	@Override
	public void writeFaultHandlerNotification() {

		codeStringBuilder.append("   procedure Error_Notification" + LF + "      (Context    : in out " + moduleImplName + SEP_PATTERN_B + LF + "       Error_Id   : in     ECOA.Error_Id_Type;" + LF + "       Timestamp  : in     ECOA.Timestamp_Type;" + LF + "       Asset_Id   : in     ECOA.Asset_Id_Type;" + LF + "       Asset_Type : in     ECOA.Asset_Type;" + LF + "       Error_type : in     ECOA.Error_Type)");
		if (isType.equals(SEP_PATTERN_H)) {
			codeStringBuilder.append(";");
		} else if (isType.equals("body")) {
			codeStringBuilder.append(LF + SEP_PATTERN_I + LF + SEP_PATTERN_J + LF + SEP_PATTERN_A + LF + SEP_PATTERN_D + "Error_Notification;");
		}
		codeStringBuilder.append(LF);
	}

	@Override
	public void writeLifecycleNotification(SM_Object instance) {
		codeStringBuilder.append("   procedure Lifecycle_Notification_" + instance.getName() + LF + "      (Context        : in out " + moduleImplName + SEP_PATTERN_B + LF + "       Previous_State : in     ECOA.Module_States_Type;" + LF);
		if (isType.equals(SEP_PATTERN_H)) {
			codeStringBuilder.append("       New_State      : in     ECOA.Module_States_Type);" + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append("       New_State      : in     ECOA.Module_States_Type)" + LF + SEP_PATTERN_I + LF + SEP_PATTERN_J + LF + SEP_PATTERN_A + LF + SEP_PATTERN_D + "Lifecycle_Notification_" + instance.getName() + ";");
		}
		codeStringBuilder.append(LF);
	}

	@Override
	public void writeLifecycleServices() {
		// Write the runtime lifecycle API
		codeStringBuilder.append("   -- Runtime lifecycle API" + LF + LF +

				"   procedure INITIALIZE_received" + LF + SEP_PATTERN_K + moduleImplName + SEP_PATTERN_C);
		setOpName("INITIALIZE_received");
		writeEndParameters();

		codeStringBuilder.append("   procedure START_received" + LF + SEP_PATTERN_K + moduleImplName + SEP_PATTERN_C);
		setOpName("START_received");
		writeEndParameters();

		codeStringBuilder.append("   procedure STOP_received" + LF + SEP_PATTERN_K + moduleImplName + SEP_PATTERN_C);
		setOpName("STOP_received");
		writeEndParameters();

		codeStringBuilder.append("   procedure SHUTDOWN_received" + LF + SEP_PATTERN_K + moduleImplName + SEP_PATTERN_C);
		setOpName("SHUTDOWN_received");
		writeEndParameters();

		codeStringBuilder.append("   procedure REINITIALIZE_received" + LF + SEP_PATTERN_K + moduleImplName + SEP_PATTERN_C);
		setOpName("REINITIALIZE_received");
		writeEndParameters();
	}

	@Override
	public void writeParameter(SM_OperationParameter para) {
		// Write to header or body
		codeStringBuilder.append(";" + LF + "       " + para.getName() + " : out " + TypesProcessorAda.convertParameterToAda(para.getType()));
	}

	@Override
	public void writePreamble() {

		if (isType.equals(SEP_PATTERN_H)) {
			codeStringBuilder.append(SEP_PATTERN_G + LF + "-- @file " + moduleImplName + ".ads" + LF + "-- Module Interface package specification for Module" + moduleImplName + LF + "-- Generated automatically from specification; do not modify here" + LF + SEP_PATTERN_G + LF + LF);

			for (SM_Namespace use : componentImplementation.getUses()) {
				codeStringBuilder.append(SEP_PATTERN_111 + use.getName() + ";" + LF + LF);
			}

			codeStringBuilder.append("-- Include Container Types" + LF + SEP_PATTERN_111 + moduleImplName + "_Container_Types;" + LF + LF +

					"-- Include container" + LF + SEP_PATTERN_111 + moduleImplName + "_Container;" + LF + LF +

					"package " + moduleImplName + " is" + LF + LF);
		} else if (isType.equals("body")) {
			// Body file
			codeStringBuilder.append(SEP_PATTERN_G + LF + "-- @file " + moduleImplName + ".adb" + LF + "-- Module Interface package for Module " + moduleImplName + LF + SEP_PATTERN_G + LF + LF +

					"package body " + moduleImplName + " is" + LF + LF);
		}

	}

	@Override
	public void writeRequestReceived(String name) {
		// Write to header or body
		codeStringBuilder.append(SEP_PATTERN_E + name + "_Request_Received" + LF + SEP_PATTERN_K + moduleImplName + SEP_PATTERN_B + LF + "       ID      : in     ECOA.Unsigned_32_Type");
		setOpName(name + "_Request_Received");
	}

	@Override
	public void writeResponseReceivedAsynchonous(String name) {
		// Write to header or body
		codeStringBuilder.append(SEP_PATTERN_E + name + "_Response_Received" + LF + SEP_PATTERN_K + moduleImplName + SEP_PATTERN_B + LF + "       ID      : in ECOA.Unsigned_32_Type;" + LF + "       Status  : in ECOA.Return_Status_Type");
		setOpName(name + "_Response_Received");
	}

	@Override
	public void writeServiceAvailabilityNotifications() {
		// Only generate if there is at least one required service
		if (componentImplementation.getCompType().getReferenceInstancesList().size() > 0) {
			codeStringBuilder.append("   -- Service Availability API" + LF);

			// Service availability changed
			codeStringBuilder.append("   procedure Service_Availability_Changed" + LF + SEP_PATTERN_F + moduleImplName + SEP_PATTERN_B + LF + "       Instance  : in     " + moduleImplName + "_Container_Types.Reference_ID_Type;" + LF + "       Available : in     ECOA.Boolean_8_Type)");

			if (isType.equals(SEP_PATTERN_H)) {
				codeStringBuilder.append(";" + LF);
			} else if (isType.equals("body")) {
				codeStringBuilder.append(LF + SEP_PATTERN_I + LF + SEP_PATTERN_J + LF + SEP_PATTERN_A + LF + "   end Service_Availability_Changed;" + LF + LF);
			}
			codeStringBuilder.append(LF);

			// Service provider changed
			codeStringBuilder.append("   procedure Service_Provider_Changed" + LF + SEP_PATTERN_F + moduleImplName + SEP_PATTERN_B + LF + "       Instance  : in     " + moduleImplName + "_Container_Types.Reference_ID_Type)");

			if (isType.equals(SEP_PATTERN_H)) {
				codeStringBuilder.append(";" + LF);
			} else if (isType.equals("body")) {
				codeStringBuilder.append(LF + SEP_PATTERN_I + LF + SEP_PATTERN_J + LF + SEP_PATTERN_A + LF + "   end Service_Provider_Changed;" + LF + LF);
			}
			codeStringBuilder.append(LF);
		}
	}

	@Override
	public void writeVDUpdated(String vdOpName) {
		codeStringBuilder.append(SEP_PATTERN_E + vdOpName + "_Updated" + LF + "      (Context     : in out " + moduleImplName + SEP_PATTERN_B + LF + "       Status      : in     ECOA.Return_Status_Type;" + LF + "       Data_Handle : in     " + moduleImplName + "_Container_Types." + vdOpName + "_Handle_Type)");

		if (isType.equals(SEP_PATTERN_H)) {
			codeStringBuilder.append(";" + LF + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(LF + SEP_PATTERN_I + LF + SEP_PATTERN_J + LF + SEP_PATTERN_A + LF + SEP_PATTERN_D + vdOpName + "_Updated;" + LF + LF);
		}
	}

}
