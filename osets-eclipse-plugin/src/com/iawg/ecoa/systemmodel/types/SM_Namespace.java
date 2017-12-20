/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.types;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SM_Namespace {
	private static final Logger LOGGER = LogManager.getLogger(SM_Namespace.class);

	private Map<String, SM_Type> types = new LinkedHashMap<String, SM_Type>();
	private String name;
	private List<SM_Namespace> usesList = new ArrayList<SM_Namespace>();

	public SM_Namespace(String namespaceName) {
		this.name = namespaceName;
	}

	public String getName() {
		return name;
	}

	public void addType(String name, SM_Type type) {
		if (types.containsKey(name)) {
			LOGGER.info("ERROR - type \"" + name + "\" multiply declared");
			

		} else {
			types.put(name, type);
		}
	}

	public SM_Type getType(String typeName) {
		if (types.containsKey(typeName)) {
			return types.get(typeName);

		} else {
			LOGGER.info("ERROR - type \"" + typeName + "\" does not exist in namespace " + name);
			
			return null;
		}
	}

	public boolean typeExists(String typeName) {
		if (types.containsKey(typeName)) {
			return true;

		} else {
			return false;
		}
	}

	public Map<String, SM_Type> getTypes() {
		return types;
	}

	public void addUse(SM_Namespace namespace) {
		usesList.add(namespace);
	}

	public List<SM_Namespace> getUses() {
		return usesList;
	}
}
