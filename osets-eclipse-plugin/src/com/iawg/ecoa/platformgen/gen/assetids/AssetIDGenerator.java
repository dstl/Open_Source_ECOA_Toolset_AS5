/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.assetids;

import com.iawg.ecoa.platformgen.PlatformGenerator;

public class AssetIDGenerator {

	private PlatformGenerator platformGenerator;

	public AssetIDGenerator(PlatformGenerator platformGenerator) {
		this.platformGenerator = platformGenerator;
	}

	public void generate() {
		AssetIDWriter assetIDWriter = new AssetIDWriterC(platformGenerator);
		generateAssetIDs(assetIDWriter);

		assetIDWriter = new AssetIDWriterAda(platformGenerator);
		generateAssetIDs(assetIDWriter);

		assetIDWriter = new AssetIDWriterCPP(platformGenerator);
		generateAssetIDs(assetIDWriter);
	}

	private void generateAssetIDs(AssetIDWriter assetIDWriter) {
		assetIDWriter.open();
		assetIDWriter.writePreamble();
		assetIDWriter.writeCompInstIDs();
		assetIDWriter.writePDIDs();
		assetIDWriter.writeCNs();
		assetIDWriter.writePFs();
		assetIDWriter.writeOPs();
		assetIDWriter.writeDeps();
		assetIDWriter.writeIncludes();
		assetIDWriter.close();
	}

}
