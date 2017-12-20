/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.servicedefinition;

import java.util.HashMap;
import java.util.Map;

public class SM_ServiceDefinitions {
	/**
	 * Map that contains all of the service definitions (ServiceInterface
	 * objects) in the model: uses the service name as a key
	 */
	private Map<String, SM_ServiceInterface> services = new HashMap<String, SM_ServiceInterface>();

	/**
	 * Add a service definition to the hashtable
	 * 
	 * @param s
	 *            the service definition to be added
	 */
	public void addService(SM_ServiceInterface s) {
		// Add a new service: uses the name of the service as the key to the
		// value
		services.put(s.getName(), s);
	}

	/**
	 * Get a service definition by name
	 * 
	 * @param name
	 *            the name of the required service definition
	 * @return the requested service definition
	 */
	public SM_ServiceInterface getService(String name) {
		return services.get(name);

	}

	/**
	 * Check whether a service definition exists n the model
	 * 
	 * @param name
	 *            the name of the service definition
	 * @return true if the service exists, false otherwise
	 */
	public Boolean serviceExists(String name) {
		return services.get(name) != null;
	}

	/**
	 * Check whether a service definition exists n the model
	 * 
	 * @param name
	 *            the service definition
	 * @return true if the service already exists in the model, false otherwise
	 */
	public Boolean serviceExists(SM_ServiceInterface s) {
		return services.get(s.getName()) != null;
	}

}
