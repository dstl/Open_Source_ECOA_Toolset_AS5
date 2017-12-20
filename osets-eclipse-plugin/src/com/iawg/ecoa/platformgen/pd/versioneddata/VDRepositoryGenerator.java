/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.versioneddata;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_VDRepository;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedModInst;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class VDRepositoryGenerator {

	private PlatformGenerator platformGenerator;
	private SM_ProtectionDomain protectionDomain;

	public VDRepositoryGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd) {
		this.platformGenerator = platformGenerator;
		this.protectionDomain = pd;
	}

	public void generate() {
		ArrayList<SM_ComponentInstance> processedCompInstancesList = new ArrayList<SM_ComponentInstance>();

		// Generate a file for each VD repository required
		for (SM_DeployedModInst depModInst : protectionDomain.getDeployedModInsts()) {
			if (!processedCompInstancesList.contains(depModInst.getCompInstance())) {
				// Add it to the list of processed compInstances.
				processedCompInstancesList.add(depModInst.getCompInstance());

				for (SM_VDRepository vdRepo : depModInst.getCompInstance().getImplementation().getVdRepositories()) {
					Path directory = platformGenerator.getPdOutputDir().resolve("src-gen/" + depModInst.getCompInstance().getName());

					VDRepoWriterC vdRepoHeaderWriter = new VDRepoWriterC(true, directory, vdRepo, platformGenerator.getunderlyingPlatformInstantiation(), protectionDomain, depModInst.getCompInstance());
					VDRepoWriterC vdRepoBodyWriter = new VDRepoWriterC(false, directory, vdRepo, platformGenerator.getunderlyingPlatformInstantiation(), protectionDomain, depModInst.getCompInstance());

					vdRepoHeaderWriter.open();
					vdRepoBodyWriter.open();

					vdRepoHeaderWriter.writePreamble();
					vdRepoBodyWriter.writePreamble();

					vdRepoHeaderWriter.writeDataTypeDecl();
					vdRepoBodyWriter.writeDataTypeDecl();

					vdRepoBodyWriter.writeSemaphoreIDDecl();
					vdRepoBodyWriter.writeFirstTimeDecl();

					vdRepoHeaderWriter.writeWriteFunction();
					vdRepoBodyWriter.writeWriteFunction();

					vdRepoHeaderWriter.writeReadFunction();
					vdRepoBodyWriter.writeReadFunction();

					vdRepoHeaderWriter.writeInitialise();
					vdRepoBodyWriter.writeInitialise();

					vdRepoHeaderWriter.writeIncludes();
					vdRepoBodyWriter.writeIncludes();

					vdRepoHeaderWriter.close();
					vdRepoBodyWriter.close();
				}
			}
		}
	}
}
