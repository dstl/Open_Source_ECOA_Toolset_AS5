/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.util;

import org.apache.commons.lang3.StringUtils;

public class LibraryUtil {

	public static String getLibraryNameFromFile(String file) {
		String ret = new String();
		ret = StringUtils.replace(file, ".types", "");
		ret = StringUtils.replace(ret, " ", "__");
		return ret;
	}

	public static String getLibraryUseNameFromName(String name) {
		String ret = new String();
		ret = StringUtils.replace(name, "__", ".");
		return ret;
	}
}
