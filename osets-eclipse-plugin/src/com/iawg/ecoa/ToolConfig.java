/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa;

public class ToolConfig {

	private boolean generateBodies = true;
	private boolean generateTemplateCModules = false;
	private boolean overwriteFiles = false;
	private boolean instrumentAllModules = false;

	public boolean generateBodies() {
		return generateBodies;
	}

	public boolean isGenerateTemplateCModules() {
		return generateTemplateCModules;
	}

	public boolean isInstrumentAllModules() {
		return instrumentAllModules;
	}

	public boolean isOverwriteFiles() {
		return overwriteFiles;
	}

	public void setGenerateBodies(boolean generateBodies) {
		this.generateBodies = generateBodies;
	}

	public void setGenerateTemplateCModules(boolean generateTemplateCModules) {
		this.generateTemplateCModules = generateTemplateCModules;
	}

	public void setInstrumentAllModules(boolean instrumentAllModules) {
		this.instrumentAllModules = instrumentAllModules;
	}

	public void setOverwriteFiles(boolean overwriteFiles) {
		this.overwriteFiles = overwriteFiles;
	}

}
