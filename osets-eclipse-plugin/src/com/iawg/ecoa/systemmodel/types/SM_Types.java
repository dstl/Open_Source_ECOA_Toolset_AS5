/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.systemmodel.types.SM_Type.nameAndType;

public class SM_Types {
	private static final Logger LOGGER = LogManager.getLogger(SM_Types.class);
	private static final String SEP_PATTERN_101 = "uint8";

	private Map<String, SM_Namespace> namespaces = new HashMap<String, SM_Namespace>();

	public SM_Types() {

		SM_Namespace ecoaNamespace = new SM_Namespace("ECOA");
		namespaces.put("ECOA", ecoaNamespace);

		ecoaNamespace.addType("boolean8", new SM_Base_Type("boolean8", 1, ecoaNamespace));
		ecoaNamespace.addType("int8", new SM_Base_Type("int8", 1, ecoaNamespace));
		ecoaNamespace.addType("char8", new SM_Base_Type("char8", 1, ecoaNamespace));
		ecoaNamespace.addType("byte", new SM_Base_Type("byte", 1, ecoaNamespace));
		ecoaNamespace.addType("int16", new SM_Base_Type("int16", 2, ecoaNamespace));
		ecoaNamespace.addType("int32", new SM_Base_Type("int32", 4, ecoaNamespace));
		ecoaNamespace.addType("int64", new SM_Base_Type("int64", 8, ecoaNamespace));
		ecoaNamespace.addType(SEP_PATTERN_101, new SM_Base_Type(SEP_PATTERN_101, 1, ecoaNamespace));
		ecoaNamespace.addType("uint16", new SM_Base_Type("uint16", 2, ecoaNamespace));
		ecoaNamespace.addType("uint32", new SM_Base_Type("uint32", 4, ecoaNamespace));
		ecoaNamespace.addType("uint64", new SM_Base_Type("uint64", 8, ecoaNamespace));
		ecoaNamespace.addType("float32", new SM_Base_Type("float32", 4, ecoaNamespace));
		ecoaNamespace.addType("double64", new SM_Base_Type("double64", 8, ecoaNamespace));

		ecoaNamespace.addType("TRUE", new SM_Constant_Type("TRUE", null, 1, ecoaNamespace));
		ecoaNamespace.addType("FALSE", new SM_Constant_Type("FALSE", null, 0, ecoaNamespace));
		ecoaNamespace.addType("INT8_MIN", new SM_Constant_Type("INT8_MIN", getType("int8"), -127, ecoaNamespace));
		ecoaNamespace.addType("INT8_MAX", new SM_Constant_Type("INT8_MAX", getType("int8"), 127, ecoaNamespace));
		ecoaNamespace.addType("CHAR8_MIN", new SM_Constant_Type("CHAR8_MIN", getType("char8"), 0, ecoaNamespace));
		ecoaNamespace.addType("CHAR8_MAX", new SM_Constant_Type("CHAR8_MAX", getType("char8"), 255, ecoaNamespace));
		ecoaNamespace.addType("BYTE_MIN", new SM_Constant_Type("BYTE_MIN", getType("byte"), 0, ecoaNamespace));
		ecoaNamespace.addType("BYTE_MAX", new SM_Constant_Type("BYTE_MAX", getType("byte"), 255, ecoaNamespace));
		ecoaNamespace.addType("INT16_MIN", new SM_Constant_Type("INT16_MIN", getType("int16"), -32767, ecoaNamespace));
		ecoaNamespace.addType("INT16_MAX", new SM_Constant_Type("INT16_MAX", getType("int16"), 32767, ecoaNamespace));
		ecoaNamespace.addType("INT32_MIN", new SM_Constant_Type("INT32_MIN", getType("int32"), -2147483647, ecoaNamespace));
		ecoaNamespace.addType("INT32_MAX", new SM_Constant_Type("INT32_MAX", getType("int32"), 2147483647, ecoaNamespace));
		ecoaNamespace.addType("INT64_MIN", new SM_Constant_Type("INT64_MIN", getType("int64"), -9223372036854775807L - 1, ecoaNamespace));
		ecoaNamespace.addType("INT64_MAX", new SM_Constant_Type("INT64_MAX", getType("int64"), 9223372036854775807L, ecoaNamespace));
		ecoaNamespace.addType("UINT8_MIN", new SM_Constant_Type("UINT8_MIN", getType(SEP_PATTERN_101), 0, ecoaNamespace));
		ecoaNamespace.addType("UINT8_MAX", new SM_Constant_Type("UINT8_MAX", getType(SEP_PATTERN_101), 255, ecoaNamespace));
		ecoaNamespace.addType("UINT16_MIN", new SM_Constant_Type("UINT16_MIN", getType("uint16"), 0, ecoaNamespace));
		ecoaNamespace.addType("UINT16_MAX", new SM_Constant_Type("UINT16_MAX", getType("uint16"), 65535, ecoaNamespace));
		ecoaNamespace.addType("UINT32_MIN", new SM_Constant_Type("UINT32_MIN", getType("uint32"), 0, ecoaNamespace));
		ecoaNamespace.addType("UINT32_MAX", new SM_Constant_Type("UINT32_MAX", getType("uint32"), 4294967295L, ecoaNamespace));
		ecoaNamespace.addType("UINT64_MIN", new SM_Constant_Type("UINT64_MIN", getType("uint64"), 0, ecoaNamespace));
		ecoaNamespace.addType("UINT64_MAX", new SM_Constant_Type("UINT64_MAX", getType("uint64"), 2 ^ 64 - 1, ecoaNamespace));
		ecoaNamespace.addType("FLOAT32_MIN", new SM_Constant_Type("FLOAT32_MIN", getType("float32"), -3.402823466e+38F, ecoaNamespace));
		ecoaNamespace.addType("FLOAT32_MAX", new SM_Constant_Type("FLOAT32_MAX", getType("float32"), 3.402823466e+38F, ecoaNamespace));
		ecoaNamespace.addType("DOUBLE64_MIN", new SM_Constant_Type("DOUBLE64_MIN", getType("double64"), -1.7976931348623158e+308, ecoaNamespace));
		ecoaNamespace.addType("DOUBLE64_MAX", new SM_Constant_Type("DOUBLE64_MAX", getType("double64"), 1.7976931348623158e+308, ecoaNamespace));

		SM_Enum_Type errType = new SM_Enum_Type("return_status", getType("ECOA", "uint32"), ecoaNamespace);
		errType.addEnumValue("ECOA:OK", "0");
		errType.addEnumValue("ECOA:INVALID_HANDLE", "1");
		errType.addEnumValue("ECOA:DATA_NOT_INITIALIZED", "2");
		errType.addEnumValue("ECOA:NO_DATA", "3");
		errType.addEnumValue("ECOA:INVALID_IDENTIFIER", "4");
		errType.addEnumValue("ECOA:NO_RESPONSE", "5");
		errType.addEnumValue("ECOA:OPERATION_ALREADY_PENDING", "6");
		errType.addEnumValue("ECOA:INVALID_SERVICE_ID", "7");
		errType.addEnumValue("ECOA:CLOCK_UNSYNCHRONIZED", "8");
		errType.addEnumValue("ECOA:INVALID_SERVICE_ID", "9");
		errType.addEnumValue("ECOA:RESOURCE_NOT_AVAILABLE", "9");
		errType.addEnumValue("ECOA:OPERATION_NOT_AVAILABLE", "10");
		errType.addEnumValue("ECOA:PENDING_STATE_TRANSITION", "11");
		ecoaNamespace.addType("error", errType);

		// Time types
		List<nameAndType> timeRecord = new ArrayList<nameAndType>();
		nameAndType NandTSecs = new nameAndType("seconds");
		NandTSecs.setReferencedType(getType("ECOA", "uint32"));
		nameAndType NandTNanoSecs = new nameAndType("nanoseconds");
		NandTNanoSecs.setReferencedType(getType("ECOA", "uint32"));

		timeRecord.add(NandTSecs);
		timeRecord.add(NandTNanoSecs);

		ecoaNamespace.addType("hr_time", new SM_Record_Type("hr_time", timeRecord, ecoaNamespace));
		ecoaNamespace.addType("global_time", new SM_Record_Type("global_time", timeRecord, ecoaNamespace));
		ecoaNamespace.addType("duration", new SM_Record_Type("duration", timeRecord, ecoaNamespace));
		ecoaNamespace.addType("timestamp", new SM_Record_Type("timestamp", timeRecord, ecoaNamespace));
		// End of Time Types

		ecoaNamespace.addType("log", new SM_Array_Type("log", getType("ECOA", "char8"), 256, ecoaNamespace));

		SM_Enum_Type modType = new SM_Enum_Type("module_states_type", getType("ECOA", "uint32"), ecoaNamespace);
		modType.addEnumValue("ECOA:IDLE", "0");
		modType.addEnumValue("ECOA:READY", "1");
		modType.addEnumValue("ECOA:RUNNING", "2");
		ecoaNamespace.addType("module_states_type", modType);

		// Fault Management Types
		SM_Enum_Type moduleErrorType = new SM_Enum_Type("module_error_type", getType("ECOA", "uint32"), ecoaNamespace);
		moduleErrorType.addEnumValue("ECOA:ERROR", "1");
		moduleErrorType.addEnumValue("ECOA:FATAL_ERROR", "2");
		ecoaNamespace.addType("module_error_type", moduleErrorType);

		SM_Simple_Type errorIDType = new SM_Simple_Type("error_id", true, getType("uint32"), ecoaNamespace);
		errorIDType.setMinRange("0");
		ecoaNamespace.addType("error_id", errorIDType);

		SM_Enum_Type errorType = new SM_Enum_Type("error_type", getType("ECOA", "uint32"), ecoaNamespace);
		errorType.addEnumValue("ECOA:RESOURCE_NOT_AVAILABLE", "0");
		errorType.addEnumValue("ECOA:UNAVAILABLE", "1");
		errorType.addEnumValue("ECOA:MEMORY_VIOLATION", "2");
		errorType.addEnumValue("ECOA:NUMERICAL_ERROR", "3");
		errorType.addEnumValue("ECOA:ILLEGAL_INSTRUCTION", "4");
		errorType.addEnumValue("ECOA:STACK_OVERFLOW", "5");
		errorType.addEnumValue("ECOA:DEADLINE_VIOLATION", "6");
		errorType.addEnumValue("ECOA:OVERFLOW", "7");
		errorType.addEnumValue("ECOA:UNDERFLOW", "8");
		errorType.addEnumValue("ECOA:ILLEGAL_INPUT_ARGS", "9");
		errorType.addEnumValue("ECOA:ILLEGAL_OUTPUT_ARGS", "10");
		errorType.addEnumValue("ECOA:ERROR", "11");
		errorType.addEnumValue("ECOA:FATAL_ERROR", "12");
		errorType.addEnumValue("ECOA:HARDWARE_FAULT", "13");
		errorType.addEnumValue("ECOA:POWER_FAIL", "14");
		errorType.addEnumValue("ECOA:COMMUNICATION_ERROR", "15");
		errorType.addEnumValue("ECOA:INVALID_CONFIG", "16");
		errorType.addEnumValue("ECOA:INITIALISATION_PROBLEM", "17");
		errorType.addEnumValue("ECOA:CLOCK_UNSYNCHRONIZED", "18");
		errorType.addEnumValue("ECOA:UNKNOWN_OPERATION", "19");
		errorType.addEnumValue("ECOA:OPERATION_OVERRATED", "20");
		errorType.addEnumValue("ECOA:OPERATION_UNDERRATED", "21");
		ecoaNamespace.addType("error_type", errorType);

		SM_Simple_Type assetIDType = new SM_Simple_Type("asset_id", true, getType("uint32"), ecoaNamespace);
		assetIDType.setMinRange("0");
		ecoaNamespace.addType("asset_id", assetIDType);

		SM_Enum_Type assetType = new SM_Enum_Type("asset_type", getType("ECOA", "uint32"), ecoaNamespace);
		assetType.addEnumValue("ECOA:COMPONENT", "0");
		assetType.addEnumValue("ECOA:PROTECTION_DOMAIN", "1");
		assetType.addEnumValue("ECOA:NODE", "2");
		assetType.addEnumValue("ECOA:PLATFORM", "3");
		assetType.addEnumValue("ECOA:SERVICE", "4");
		assetType.addEnumValue("ECOA:DEPLOYMENT", "5");
		ecoaNamespace.addType("asset_type", assetType);

		SM_Enum_Type recoveryActionType = new SM_Enum_Type("recovery_action_type", getType("ECOA", "uint32"), ecoaNamespace);
		recoveryActionType.addEnumValue("ECOA:SHUTDOWN", "0");
		recoveryActionType.addEnumValue("ECOA:COLD_RESTART", "1");
		recoveryActionType.addEnumValue("ECOA:WARM_RESTART", "2");
		recoveryActionType.addEnumValue("ECOA:CHANGE_DEPLOYMENT", "3");
		ecoaNamespace.addType("recovery_action_type", recoveryActionType);
		// End of Fault Management Types

		// PINFO Types
		ecoaNamespace.addType("pinfo_filename", new SM_Array_Type("pinfo_filename", getType("ECOA", "char8"), 256, ecoaNamespace));

		SM_Enum_Type seekWhenceType = new SM_Enum_Type("seek_whence_type", getType("ECOA", "uint32"), ecoaNamespace);
		seekWhenceType.addEnumValue("ECOA:SEEK_SET", "0");
		seekWhenceType.addEnumValue("ECOA:SEEK_CUR", "1");
		seekWhenceType.addEnumValue("ECOA:SEEK_END", "2");
		ecoaNamespace.addType("seek_whence_type", seekWhenceType);
		// End of PINFO Types

	}

	public boolean typeExists(String typeName) {
		// Check to see if fully qualified
		if (typeName.contains(":")) {
			String[] s = typeName.split(":");
			String qualNamespace = s[0];
			String qualTypeName = s[1];

			SM_Namespace namespace = getNamespace(qualNamespace);
			return namespace.typeExists(qualTypeName);
		} else {
			// Unqualified - check to see if it's in ECOA namespace
			if (namespaces.get("ECOA").typeExists(typeName)) {
				return namespaces.get("ECOA").typeExists(typeName);
			} else {
				LOGGER.info("ERROR - type " + typeName + " does not exist");
				
				return false;
			}
		}
	}

	public SM_Type getType(String namespace, String typeName) {

		if (namespaces.containsKey(namespace)) {
			return namespaces.get(namespace).getType(typeName);
		} else {
			LOGGER.info("ERROR - namespace \"" + namespace + "\" does not exist");
			
			return null;
		}
	}

	public SM_Type getType(String typeName) {
		// Check to see if fully qualified
		if (typeName.contains(":")) {
			String[] s = typeName.split(":");
			String qualNamespace = s[0];
			String qualTypeName = s[1];

			SM_Namespace namespace = getNamespace(qualNamespace);
			return namespace.getType(qualTypeName);
		} else {
			// Unqualified - check to see if it's in ECOA namespace
			if (namespaces.get("ECOA").typeExists(typeName)) {
				return namespaces.get("ECOA").getType(typeName);
			} else {
				LOGGER.info("ERROR - type " + typeName + " does not exist");
				
				return null;
			}
		}
	}

	public SM_Namespace getNamespace(String namespaceName) {
		if (namespaces.containsKey(namespaceName)) {
			return namespaces.get(namespaceName);
		} else {
			LOGGER.info("ERROR - namespace " + namespaceName + " does not exist");
			
			return null;
		}
	}

	public Map<String, SM_Namespace> getNamespaces() {
		return namespaces;
	}

	public void addNamespace(SM_Namespace namespace) {
		if (!namespaces.containsKey(namespace.getName())) {
			namespaces.put(namespace.getName(), namespace);
		} else {
			LOGGER.info("ERROR - namespace " + namespace.getName() + " already exists");
			
		}

	}

	public boolean namespaceExists(String namespaceName) {
		if (namespaces.get(namespaceName) != null) {
			return true;
		} else {
			return false;
		}
	}

}
