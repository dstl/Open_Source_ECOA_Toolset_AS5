/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.util;

import java.io.File;
import java.io.FilenameFilter;

import org.apache.commons.lang3.StringUtils;

public class ExtensionFilter implements FilenameFilter {

	private String ext;

	public ExtensionFilter() {
		super();
	}

	public ExtensionFilter(String ext) {
		super();
		this.ext = ext;
	}

	@Override
	public boolean accept(File dir, String name) {
		return StringUtils.endsWith(name.toLowerCase(), ext.toLowerCase());
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

}
