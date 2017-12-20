/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.common;

import java.util.HashMap;

public class NamespacePrefixMapper extends com.sun.xml.internal.bind.marshaller.NamespacePrefixMapper {

	private HashMap<String, String> map = new HashMap<String, String>();

	public NamespacePrefixMapper() {
		map.put("http://docs.oasis-open.org/ns/opencsa/sca/200912", "sca");
		map.put("http://www.ecoa.technology/sca", "ecoa-sca");
		map.put("http://www.ecoa.technology/implementation-1.0", "cimpl");
		map.put("http://www.ecoa.technology/deployment-1.0", "deploy");
		map.put("http://www.ecoa.technology/logicalsystem-1.0", "lsys");
		map.put("http://www.ecoa.technology/udpbinding-1.0", "ubind");
		map.put("http://www.w3.org/2001/XMLSchema", "xs");
	}

	@Override
	public String getPreferredPrefix(String uri, String prefix, boolean requires) {
		return map.getOrDefault(uri, prefix);
	}

}
