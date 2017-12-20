/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.serialiser;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.SystemModel;

public class SerialiserGenerator {

	private PlatformGenerator platformGenerator;

	public SerialiserGenerator(PlatformGenerator platformGenerator) {
		this.platformGenerator = platformGenerator;
	}

	public void generate(SystemModel systemModel) {
		SerialiserWriterC serialiserHeaderWriter = new SerialiserWriterC(systemModel, true, platformGenerator.getOutputDir().resolve("include/"));
		SerialiserWriterC serialiserBodyWriter = new SerialiserWriterC(systemModel, false, platformGenerator.getOutputDir().resolve("src/"));
		DeserialiserWriterC deserialiserHeaderWriter = new DeserialiserWriterC(systemModel, true, platformGenerator.getOutputDir().resolve("include/"));
		DeserialiserWriterC deserialiserBodyWriter = new DeserialiserWriterC(systemModel, false, platformGenerator.getOutputDir().resolve("src/"));

		// Open the file
		serialiserHeaderWriter.open();
		serialiserBodyWriter.open();
		deserialiserHeaderWriter.open();
		deserialiserBodyWriter.open();

		serialiserHeaderWriter.writePreamble();
		serialiserBodyWriter.writePreamble();
		deserialiserHeaderWriter.writePreamble();
		deserialiserBodyWriter.writePreamble();

		serialiserHeaderWriter.writeSerialiseFunctions();
		serialiserBodyWriter.writeSerialiseFunctions();
		deserialiserHeaderWriter.writeDeserialiseFunctions();
		deserialiserBodyWriter.writeDeserialiseFunctions();

		serialiserHeaderWriter.writeIncludes();
		serialiserBodyWriter.writeIncludes();
		deserialiserHeaderWriter.writeIncludes();
		deserialiserBodyWriter.writeIncludes();

		// Close the file.
		serialiserHeaderWriter.close();
		serialiserBodyWriter.close();
		deserialiserHeaderWriter.close();
		deserialiserBodyWriter.close();

	}

}
