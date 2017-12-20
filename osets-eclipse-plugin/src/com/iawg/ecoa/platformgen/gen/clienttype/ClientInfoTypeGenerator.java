/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.clienttype;

import com.iawg.ecoa.platformgen.PlatformGenerator;

public class ClientInfoTypeGenerator {
	private PlatformGenerator platformGenerator;

	public ClientInfoTypeGenerator(PlatformGenerator platformGenerator) {
		this.platformGenerator = platformGenerator;
	}

	public void generate() {
		ClientInfoTypeWriterC modInstUIDWriter = new ClientInfoTypeWriterC(platformGenerator.getOutputDir().resolve("include/"));

		modInstUIDWriter.open();
		modInstUIDWriter.writePreamble();
		modInstUIDWriter.writeClientInfoType();
		modInstUIDWriter.writeIncludes();
		modInstUIDWriter.close();
	}

}
