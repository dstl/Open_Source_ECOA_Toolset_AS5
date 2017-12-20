/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.types;

import java.util.HashMap;
import java.util.List;

public class SM_Variant_Record_Type extends SM_Type {

	private List<nameAndType> referencedTypes;
	private nameAndType selectorType;
	private HashMap<String, List<nameAndType>> unionTypes;

	public enum SelectorTypeType {
		NUMERIC, BOOLEAN, ENUM, ILLEGAL
	};

	public SM_Variant_Record_Type(String name, nameAndType sType, List<nameAndType> rTypes, HashMap<String, List<nameAndType>> uTypes, SM_Namespace namespace) {
		super(name, false, namespace);
		selectorType = sType;
		referencedTypes = rTypes;
		unionTypes = uTypes;
	}

	public List<nameAndType> getTypes() {
		return referencedTypes;
	}

	public nameAndType getSelectorType() {
		return selectorType;
	}

	public HashMap<String, List<nameAndType>> getUnionTypes() {
		return unionTypes;
	}

	/*
	 * Variant record selector processing needs different coding (in C at least)
	 * depending on what sort of type the selector is. This utility operation
	 * returns what sort of type the selector is of.
	 */
	public SelectorTypeType getSelectorTypeType() {
		SM_Type selectorBaseType = null;
		//
		// First we need to resolve simple types to their base ECOA Basic
		// type...
		if (selectorType.getReferencedType() instanceof SM_Simple_Type) {
			selectorBaseType = resolveBasicType(selectorType);
		} else {
			selectorBaseType = selectorType.getReferencedType();
		}
		// Now determine what sort of type the selector is...
		if (selectorBaseType instanceof SM_Enum_Type) {
			return SelectorTypeType.ENUM;
		} else if (selectorBaseType.getName().equals("boolean8")) {
			return SelectorTypeType.BOOLEAN;
		} else if (selectorBaseType.getName().equals("float32") || selectorBaseType.getName().equals("double64")) {
			return SelectorTypeType.ILLEGAL;
		} else if (selectorBaseType instanceof SM_Base_Type) {
			return SelectorTypeType.NUMERIC;
		} else
			return SelectorTypeType.ILLEGAL;
	}

	private SM_Type resolveBasicType(nameAndType selectorType) {
		SM_Simple_Type parentSMType = (SM_Simple_Type) selectorType.getReferencedType();
		//
		// LOGGER.info( "DEBUG: resolving " + parentSMType.getName() );
		while (!(parentSMType.getType() instanceof SM_Base_Type)) {
			// LOGGER.info( " --> " + parentSMType.getType().getName() );
			parentSMType = (SM_Simple_Type) parentSMType.getType();
		}
		// LOGGER.info( " --> " + parentSMType.getType().getName());
		return parentSMType.getType();
	}
}
