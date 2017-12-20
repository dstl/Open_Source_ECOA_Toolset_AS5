/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.clienttype.serviceopuid;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;

public class ServiceInstanceOpUIDGenerator {

	private PlatformGenerator platformGenerator;
	private SM_ComponentImplementation compImpl;

	public ServiceInstanceOpUIDGenerator(PlatformGenerator platformGenerator, SM_ComponentImplementation compImpl) {
		this.platformGenerator = platformGenerator;
		this.compImpl = compImpl;
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir().resolve("src-gen/" + compImpl.getName() + "/");
		ServiceInstanceOpUIDWriterC serviceInstUIDWriter = new ServiceInstanceOpUIDWriterC(directory, compImpl);

		serviceInstUIDWriter.open();
		serviceInstUIDWriter.writePreamble();
		serviceInstUIDWriter.writeUIDs();
		serviceInstUIDWriter.writeIncludes();
		serviceInstUIDWriter.close();
	}

}
