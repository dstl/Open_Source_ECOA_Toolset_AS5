/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.assetids;

import java.util.ArrayList;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.systemmodel.SystemModel;

public abstract class AssetIDWriter extends SourceFileWriter {
	protected ArrayList<String> includeList = new ArrayList<String>();
	protected Generic_Platform underlyingPlatform;
	protected PlatformGenerator platformGenerator;
	protected SystemModel systemModel;

	public AssetIDWriter(PlatformGenerator platformGenerator) {
		super(platformGenerator.getOutputDir());
		this.platformGenerator = platformGenerator;
		this.underlyingPlatform = platformGenerator.getunderlyingPlatformInstantiation();
		this.systemModel = platformGenerator.getSystemModel();
	}

	public abstract void writePreamble();

	public abstract void writeCompInstIDs();

	public abstract void writePDIDs();

	public abstract void writeCNs();

	public abstract void writePFs();

	public abstract void writeOPs();

	public abstract void writeDeps();

	public abstract void writeIncludes();

}
