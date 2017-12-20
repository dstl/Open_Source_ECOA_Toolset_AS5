/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.types.SM_Type;

public class CLanguageSupport {
	private static String LF = System.lineSeparator();

	public static String generateIncludes(ArrayList<String> includeList) {
		String includeText = "";

		// Add to hashset to remove duplicate entries.
		HashSet<String> hs = new HashSet<String>();
		hs.addAll(includeList);
		includeList.clear();
		includeList.addAll(hs);

		// Sort alphabetically for neatness (as hashmap screws any ordering up).
		Collections.sort(includeList);

		for (String includeName : includeList) {
			includeText += "#include \"" + includeName.replaceAll("\\.", "__") + ".h\"" + LF;
		}

		return includeText;
	}

	public static String writeAssignment(SM_OperationParameter opParam) {
		if (opParam.getType().isSimple()) {
			return " " + opParam.getName() + ";" + LF;
		} else {
			return " *" + opParam.getName() + ";" + LF;
		}
	}

	public static String writeConstParam(SM_OperationParameter opParam) {
		return writeConstParam(opParam, true);
	}

	public static String writeConstParam(SM_OperationParameter opParam, boolean leadingComma) {
		String paramString = "";

		if (leadingComma) {
			paramString += "," + LF;
		} else {
			paramString += LF;
		}

		paramString += "    const " + TypesProcessorC.convertParameterToC(opParam.getType());
		if (opParam.getType().isSimple()) {
			paramString += " ";
		} else {
			paramString += " *";
		}
		return paramString += opParam.getName();

	}

	public static String writeOutAssignment(SM_OperationParameter opParam) {
		return " *" + opParam.getName() + ";" + LF;
	}

	public static String writeParam(SM_OperationParameter opParam) {
		String paramString = "," + LF + "    " + TypesProcessorC.convertParameterToC(opParam.getType()) + " *" + opParam.getName();

		return paramString;
	}

	public static String writeType(SM_Type type) {
		return type.getNamespace().getName().replaceAll("\\.", "__") + "__" + type.getName();
	}

	// Suppress default constructor for noninstantiability
	private CLanguageSupport() {
		throw new AssertionError();
	}

}
