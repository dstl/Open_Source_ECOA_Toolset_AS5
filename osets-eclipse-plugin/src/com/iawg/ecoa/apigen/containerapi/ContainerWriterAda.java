/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.containerapi;

import java.nio.file.Path;

import com.iawg.ecoa.ToolConfig;
import com.iawg.ecoa.TypesProcessorAda;
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
 * methods of that class in a way that is specific to the Ada language.
 * 
 *
 */
public class ContainerWriterAda extends ContainerWriter {
	private static final String SEP_PATTERN_151 = "with ";
	private static final String SEP_PATTERN_A = "      null; -- Container Code Here";
	private static final String SEP_PATTERN_B = "      (Context        : in out Context_Type";
	private static final String SEP_PATTERN_C = "      (Context     : in out Context_Type;";
	private static final String SEP_PATTERN_D = "      (Context        : in out Context_Type;";
	private static final String SEP_PATTERN_E = "       Data_Handle : in     ";
	private static final String SEP_PATTERN_F = "_Container_Types.";
	private static final String SEP_PATTERN_G = "   end ";
	private static final String SEP_PATTERN_H = "   procedure ";
	private static final String SEP_PATTERN_I = "_Handle_Type";
	private static final String SEP_PATTERN_J = "-------------------------------------------------------------------";
	private static final String SEP_PATTERN_K = "header";
	private static final String SEP_PATTERN_L = "      (Context : in out Context_Type";
	private static final String SEP_PATTERN_M = "   is";
	private static final String SEP_PATTERN_N = "      (Context  : in out Context_Type";
	private static final String SEP_PATTERN_O = "   begin";
	private String opName = null;

	public ContainerWriterAda(SystemModel systemModel, ToolConfig toolConfig, String isType, Path outputDir, SM_ComponentImplementation compImpl, SM_ModuleImpl moduleImpl) {
		super(systemModel, toolConfig, isType, outputDir, compImpl, moduleImpl);
	}

	@Override
	public void close() {
		codeStringBuilder.append("end " + moduleImplName + "_Container;" + LF);
		super.close();
	}

	@Override
	public void open() {
		if (isType.equals(SEP_PATTERN_K)) {
			super.openFile(outputDir.resolve(moduleImplName + "_Container.ads"));
		} else if (isType.equals("body")) {
			super.openFile(outputDir.resolve(moduleImplName + "_Container.adb.test"));
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
	public void writeCancelWriteAccess(String verData) {
		codeStringBuilder.append(SEP_PATTERN_H + verData + "_Cancel_Write_Access" + LF + SEP_PATTERN_C + LF + SEP_PATTERN_E + moduleImplName + SEP_PATTERN_F + verData + SEP_PATTERN_I);
		setOpName(verData + "_Cancel_Write_Access");
		writeEndParameters();
	}

	@Override
	public void writeConstParameter(SM_OperationParameter para) {
		codeStringBuilder.append(";" + LF + "       " + para.getName() + " : in " + TypesProcessorAda.convertParameterToAda(para.getType()));
	}

	@Override
	public void writeEndParameters() {
		if (isType.equals(SEP_PATTERN_K)) {
			codeStringBuilder.append(";" + LF + "       Status  :    out ECOA.Return_Status_Type);" + LF + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(";" + LF + "       Status  :    out ECOA.Return_Status_Type)" + LF + SEP_PATTERN_M + LF + SEP_PATTERN_O + LF + SEP_PATTERN_A + LF + SEP_PATTERN_G + opName + ";" + LF + LF);
		}
	}

	@Override
	public void writeEndParametersNoStatusReturn() {
		if (isType.equals(SEP_PATTERN_K)) {
			codeStringBuilder.append(");" + LF + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(")" + LF + SEP_PATTERN_M + LF + SEP_PATTERN_O + LF + SEP_PATTERN_A + LF + SEP_PATTERN_G + opName + ";" + LF + LF);
		}
	}

	@Override
	public void writeEventSend(String eventName) {
		codeStringBuilder.append(SEP_PATTERN_H + eventName + "_Send" + LF + "      (Context : in out " + "Context_Type");
		setOpName(eventName + "_Send");
	}

	@Override
	public void writeGetProperty(SM_ModuleTypeProperty property) {
		codeStringBuilder.append("   procedure Get_" + property.getName() + "_Value" + LF + SEP_PATTERN_C + LF + "       Value       :    out " + TypesProcessorAda.convertParameterToAda(property.getType()));
		setOpName("Get_" + property.getName() + "_Value");
		writeEndParametersNoStatusReturn();
	}

	@Override
	public void writeGetReadAccess(String verData) {
		codeStringBuilder.append(SEP_PATTERN_H + verData + "_Get_Read_Access" + LF + SEP_PATTERN_C + LF + "       Data_Handle :    out " + moduleImplName + SEP_PATTERN_F + verData + SEP_PATTERN_I);
		setOpName(verData + "_Get_Read_Access");
		writeEndParameters();
	}

	@Override
	public void writeGetRequiredAvailability() {
		// Only generate if there is at least one required service
		if (componentImplementation.getCompType().getReferenceInstancesList().size() > 0) {
			codeStringBuilder.append("   procedure Get_Service_Availability" + LF + "      (Context   : in out Context_Type;" + LF + "       Instance  : in     " + moduleImplName + "_Container_Types.Reference_ID_Type;" + LF + "       Available :    out ECOA.Boolean_8_Type");
			setOpName("Get_Service_Availability");
			writeEndParameters();
		}
	}

	@Override
	public void writeGetWriteAccess(String verData) {
		codeStringBuilder.append(SEP_PATTERN_H + verData + "_Get_Write_Access" + LF + SEP_PATTERN_C + LF + "       Data_Handle :    out " + moduleImplName + SEP_PATTERN_F + verData + SEP_PATTERN_I);
		setOpName(verData + "_Get_Write_Access");
		writeEndParameters();
	}

	private void writeLifecycle(SM_Object moduleInstance) {
		codeStringBuilder.append("   procedure Get_Lifecycle_State_" + moduleInstance.getName() + LF + SEP_PATTERN_D + LF + "       Current_State :    out ECOA.Module_States_Type");
		setOpName("Get_Lifecycle_State_" + moduleInstance.getName());
		writeEndParametersNoStatusReturn();

		codeStringBuilder.append("   procedure Stop_" + moduleInstance.getName() + LF + SEP_PATTERN_B);
		setOpName("Stop_" + moduleInstance.getName());
		writeEndParameters();

		codeStringBuilder.append("   procedure Start_" + moduleInstance.getName() + LF + SEP_PATTERN_B);
		setOpName("Start_" + moduleInstance.getName());
		writeEndParameters();

		codeStringBuilder.append("   procedure Initialize_" + moduleInstance.getName() + LF + SEP_PATTERN_B);
		setOpName("Initialize_" + moduleInstance.getName());
		writeEndParameters();

		codeStringBuilder.append("   procedure Shutdown_" + moduleInstance.getName() + LF + SEP_PATTERN_B);
		setOpName("Shutdown_" + moduleInstance.getName());
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

	public void writeLogEndParameters() {
		if (isType.equals(SEP_PATTERN_K)) {
			codeStringBuilder.append(";" + LF + "       Log      : in ECOA.Log_Type);" + LF + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(";" + LF + "       Log      : in ECOA.Log_Type)" + LF + SEP_PATTERN_M + LF + SEP_PATTERN_O + LF + SEP_PATTERN_A + LF + SEP_PATTERN_G + opName + ";" + LF + LF);
		}
	}

	@Override
	public void writeLoggingServices() {
		// Write the logging and fault management services API
		codeStringBuilder.append("   -- Logging and fault management services API" + LF + LF +

				"   procedure Log_Trace" + LF + SEP_PATTERN_L);
		setOpName("Log_Trace");
		writeLogEndParameters();

		codeStringBuilder.append("   procedure Log_Debug" + LF + SEP_PATTERN_L);
		setOpName("Log_Debug");
		writeLogEndParameters();

		codeStringBuilder.append("   procedure Log_Info" + LF + SEP_PATTERN_N);
		setOpName("Log_Info");
		writeLogEndParameters();

		codeStringBuilder.append("   procedure Log_Warning" + LF + SEP_PATTERN_N);
		setOpName("Log_Warning");
		writeLogEndParameters();

		codeStringBuilder.append("   procedure Raise_Error" + LF + SEP_PATTERN_N);
		setOpName("Raise_Error");
		writeLogEndParameters();

		codeStringBuilder.append("   procedure Raise_Fatal_Error" + LF + SEP_PATTERN_N);
		setOpName("Raise_Fatal_Error");
		writeLogEndParameters();

	}

	@Override
	public void writeParameter(SM_OperationParameter para) {
		codeStringBuilder.append(";" + LF + "       " + para.getName() + " : out " + TypesProcessorAda.convertParameterToAda(para.getType()));
	}

	@Override
	public void writePInfo(String PInfoName, boolean writeable) {

		codeStringBuilder.append("   procedure Read_" + PInfoName + LF + SEP_PATTERN_D + LF + "       Memory_Address : in     System.Address;" + LF + "       In_Size        : in     ECOA.Unsigned_32_Type;" + LF + "       Out_Size       :    out ECOA.Unsigned_32_Type");
		setOpName("read_" + PInfoName);
		writeEndParameters();

		if (writeable) {
			codeStringBuilder.append("   procedure Write_" + PInfoName + LF + SEP_PATTERN_D + LF + "       Memory_Address : in     System.Address;" + LF + "       In_Size        : in     ECOA.Unsigned_32_Type");
			setOpName("write_" + PInfoName);
			writeEndParameters();
		}

		codeStringBuilder.append("   procedure Seek_" + PInfoName + LF + "      (Context      : in out Context_Type;" + LF + "       Offset       : in     System.Address;" + LF + "       Whence       : in     ECOA.Seek_Whence_Type;" + LF + "       New_Position : in     ECOA.Unsigned_32_Type");
		setOpName("seek_" + PInfoName);
		writeEndParameters();

	}

	@Override
	public void writePreamble() {
		// TODO error handler function IDs
		if (isType.equals(SEP_PATTERN_K)) {
			codeStringBuilder.append(SEP_PATTERN_J + LF + "-- @file " + moduleImplName + "_Container.ads" + LF + "-- Container Interface package specification for Module " + moduleImplName + LF + "-- Generated automatically from specification; do not modify here" + LF + SEP_PATTERN_J + LF +

					"with System;" + LF + LF +

					"-- Include Container Types" + LF + SEP_PATTERN_151 + moduleImplName + "_Container_Types;" + LF);

			for (SM_Namespace use : componentImplementation.getUses()) {
				codeStringBuilder.append(SEP_PATTERN_151 + use.getName() + ";" + LF + LF);
			}

			// Include reference to the user-defined part of the context
			codeStringBuilder.append(SEP_PATTERN_151 + moduleImplName + "_User_Context;" + LF + LF +

					"package " + moduleImplName + "_Container is" + LF + LF +

					"   type Context_Type is" + LF + "      record" + LF + "         -- Standard container context information" + LF + "         Operation_Timestamp : ECOA.Timestamp_Type;" + LF + LF +

					"         -- A hook to implementation dependant private data" + LF + "         Platform_Hook       : System.Address;" + LF + LF +

					"         -- Information that is private to a module implementation" + LF + "         User_Context        : " + moduleImplName + "_User_Context.User_Context_Type;" + LF +

					"         Warm_Start_Context  : " + moduleImplName + "_User_Context.Warm_Start_Context_Type;" + LF + LF +

					"      end record;" + LF + LF);
		} else if (isType.equals("body")) {
			codeStringBuilder.append(SEP_PATTERN_J + LF + "-- @file " + moduleImplName + "_Container.adb" + LF + "-- Container Interface package body for Module " + moduleImplName + LF + SEP_PATTERN_J + LF +

					"package body " + moduleImplName + "_Container is" + LF + LF);
		}
	}

	@Override
	public void writePublishWriteAccess(String verData) {
		codeStringBuilder.append(SEP_PATTERN_H + verData + "_Publish_Write_Access" + LF + SEP_PATTERN_C + LF + SEP_PATTERN_E + moduleImplName + SEP_PATTERN_F + verData + SEP_PATTERN_I);
		setOpName(verData + "_Publish_Write_Access");
		writeEndParameters();
	}

	@Override
	public void writeRecoveryAction() {
		codeStringBuilder.append("   procedure Recovery_Action" + LF + "      (Context         : in out Context_Type;" + LF + "       Recovery_Action : in     ECOA.Recovery_Action_Type;" + LF + "       Asset_Id        : in     ECOA.Asset_Id_Type;" + LF + "       Asset_Type      : in     ECOA.Asset_type");
		setOpName("Recovery_Action");
		writeEndParameters();
	}

	@Override
	public void writeReleaseReadAccess(String verData) {
		codeStringBuilder.append(SEP_PATTERN_H + verData + "_Release_Read_Access" + LF + SEP_PATTERN_C + LF + SEP_PATTERN_E + moduleImplName + SEP_PATTERN_F + verData + SEP_PATTERN_I);
		setOpName(verData + "_Release_Read_Access");
		writeEndParameters();
	}

	@Override
	public void writeRequestAsynchronous(String reqName) {
		codeStringBuilder.append(SEP_PATTERN_H + reqName + "_Request_Async" + LF + "      (Context : in out Context_Type;" + LF + "       ID      :    out ECOA.Unsigned_32_Type");
		setOpName(reqName + "_Request_Async");
	}

	@Override
	public void writeRequestSynchronous(String reqName) {
		codeStringBuilder.append(SEP_PATTERN_H + reqName + "_Request_Sync" + LF + SEP_PATTERN_L);
		setOpName(reqName + "_Request_Sync");
	}

	@Override
	public void writeResponseSend(String reqName) {
		codeStringBuilder.append(SEP_PATTERN_H + reqName + "_Response_Send" + LF + "      (Context : in out Context_Type;" + LF + "       ID      : in     ECOA.Unsigned_32_Type");
		setOpName(reqName + "_Response_Send");
	}

	@Override
	public void writeSaveNonVolatileContext() {
		codeStringBuilder.append("   procedure save_non_volatile_context" + LF + "      (Context      : in out Context_Type");
		setOpName("Save_Non_Volatile_Context");
		writeEndParametersNoStatusReturn();

	}

	@Override
	public void writeSetProvidedAvailability() {
		// Only generate if there is at least one provided service
		if (componentImplementation.getCompType().getServiceInstancesList().size() > 0) {
			codeStringBuilder.append("   procedure Set_Service_Availability" + LF + "      (Context   : in out Context_Type;" + LF + "       Instance  : in     " + moduleImplName + "_Container_Types.Service_ID_Type;" + LF + "       Available : in     ECOA.Boolean_8_Type");
			setOpName("Set_Service_Availability");
			writeEndParameters();
		}
	}

	@Override
	public void writeTimeResolutionServices() {
		codeStringBuilder.append("   -- Time resolution services API" + LF + LF +

				"   procedure Get_Relative_Local_Time_Resolution" + LF + "      (Context                        : in out Context_Type;" + LF + "       Relative_Local_Time_Resolution :    out ECOA.Duration_Type");
		setOpName("Get_Relative_Local_Time_Resolution");
		writeEndParametersNoStatusReturn();

		codeStringBuilder.append("   procedure Get_UTC_Time_Resolution" + LF + "      (Context                : in out Context_Type;" + LF + "       UTC_Time_Resolution :    out ECOA.Duration_Type");
		setOpName("Get_UTC_Time_Resolution");
		writeEndParametersNoStatusReturn();

		codeStringBuilder.append("   procedure Get_Absolute_System_Time_Resolution" + LF + "      (Context                         : in out Context_Type;" + LF + "       Absolute_System_Time_Resolution :    out ECOA.Duration_Type");
		setOpName("Get_Absolute_System_Time_Resolution");
		writeEndParametersNoStatusReturn();
	}

	@Override
	public void writeTimeServices() {
		codeStringBuilder.append("   -- Time services API" + LF + LF +

				"   procedure Get_Relative_Local_Time" + LF + "      (Context             : in out Context_Type;" + LF + "       Relative_Local_Time :    out ECOA.HR_Time_Type");
		setOpName("Get_Relative_Local_Time");
		writeEndParametersNoStatusReturn();

		codeStringBuilder.append("   procedure Get_UTC_Time" + LF + "      (Context  : in out Context_Type;" + LF + "       UTC_Time :    out ECOA.Global_Time_Type");
		setOpName("Get_UTC_Time");
		writeEndParameters();

		codeStringBuilder.append("   procedure Get_Absolute_System_Time" + LF + "      (Context              : in out Context_Type;" + LF + "       Absolute_System_Time :    out ECOA.Global_Time_Type");
		setOpName("Get_Absolute_System_Time");
		writeEndParameters();
	}

}
