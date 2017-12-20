/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa;

import java.util.HashMap;

import com.iawg.ecoa.systemmodel.types.SM_Type;

/**
 * This class contains any static methods used for the conversion of
 * language-agnostic types specified in the XML to C language-specific types.
 * 
 */
public class TypesProcessorAda {
	private static HashMap<String, String> typeConverter;

	public static String convertParameterToAda(SM_Type smType) {
		if (typeConverter == null) {
			setupTypesProcessorAda();
		}

		if (smType.getNamespace().getName().equals("ECOA")) {
			return smType.getNamespace().getName() + "." + typeConverter.get(smType.getName());
		} else {
			return smType.getNamespace().getName() + "." + smType.getName();
		}
	}

	private static void setupTypesProcessorAda() {

		typeConverter = new HashMap<String, String>();

		typeConverter.put("boolean8", "Boolean_8_Type");
		typeConverter.put("int8", "Signed_8_Type");
		typeConverter.put("char8", "Character_8_Type");
		typeConverter.put("byte", "Byte_Type");
		typeConverter.put("int16", "Signed_16_Type");
		typeConverter.put("int32", "Signed_32_Type");
		typeConverter.put("int64", "Signed_64_Type");
		typeConverter.put("uint8", "Unsigned_8_Type");
		typeConverter.put("uint16", "Unsigned_16_Type");
		typeConverter.put("uint32", "Unsigned_32_Type");
		typeConverter.put("float32", "Float_32_Type");
		typeConverter.put("double64", "Float_64_Type");
		typeConverter.put("component_states_type", "Component_States_Type");
		typeConverter.put("module_states_type", "Module_States_Type");
		typeConverter.put("hr_time", "Hr_Time_Type");
		typeConverter.put("global_time", "Global_Time_Type");
		typeConverter.put("timestamp", "Timestamp_Type");
		typeConverter.put("log", "Log_Type");

	}

}
