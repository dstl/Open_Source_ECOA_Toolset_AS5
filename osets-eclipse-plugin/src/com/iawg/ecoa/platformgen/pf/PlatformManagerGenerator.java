/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.common.underlyingplatform.C_Posix;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.platformgen.pf.eliin.ELIInGenerator;
import com.iawg.ecoa.platformgen.pf.elisupport.ELISupportGenerator;
import com.iawg.ecoa.platformgen.pf.mainentry.MainEntryPointGenerator;
import com.iawg.ecoa.platformgen.pf.pf2pdmanager.PFtoPDManagerGenerator;
import com.iawg.ecoa.platformgen.pf.pf2pfmanager.PFtoPFManagerGenerator;
import com.iawg.ecoa.platformgen.pf.pfcontrol.PFControllerGenerator;
import com.iawg.ecoa.platformgen.pf.posixaposbind.PosixAPOSBindGenerator;
import com.iawg.ecoa.platformgen.pf.servicemanager.ServiceManagerGenerator;
import com.iawg.ecoa.platformgen.pf.serviceopuid.ServiceOpUIDGenerator;
import com.iawg.ecoa.platformgen.pf.vcid.VCIDGenerator;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class PlatformManagerGenerator {

	private SystemModel systemModel;
	private Path pfManagerOutputDir;
	private SM_LogicalComputingPlatform lcp;
	private Generic_Platform underlyingPlatformInstantiation;

	public PlatformManagerGenerator(SystemModel systemModel, Path pfManagerOutputDir, Generic_Platform underlyingPlatformInstantiation, SM_LogicalComputingPlatform lcp) {
		this.systemModel = systemModel;
		this.pfManagerOutputDir = pfManagerOutputDir;
		this.underlyingPlatformInstantiation = underlyingPlatformInstantiation;
		this.lcp = lcp;
	}

	public void generate() {
		// Generate the Platform Manager Controller.
		PFControllerGenerator pfControlGenerator = new PFControllerGenerator(this, lcp);
		pfControlGenerator.generate();

		// Generate the Platform to Protection Domain Manager.
		PFtoPDManagerGenerator pf2pdManagerGenerator = new PFtoPDManagerGenerator(this, lcp);
		pf2pdManagerGenerator.generate();

		// Generate the Platform to Platform Manager.
		PFtoPFManagerGenerator pf2pfManagerGenerator = new PFtoPFManagerGenerator(this, lcp);
		pf2pfManagerGenerator.generate();

		// Generate the Platform Service Manager.
		ServiceManagerGenerator serviceManagerGenerator = new ServiceManagerGenerator(this, lcp);
		serviceManagerGenerator.generate();

		// Generate the VC IDS.
		VCIDGenerator vcIDGenerator = new VCIDGenerator(this, lcp);
		vcIDGenerator.generate();

		// Generate the Service Operation UIDS.
		ServiceOpUIDGenerator serviceOpUIDGenerator = new ServiceOpUIDGenerator(this, lcp);
		serviceOpUIDGenerator.generate();

		// Generate ELI Support
		ELISupportGenerator eliSupportGenerator = new ELISupportGenerator(this, lcp);
		eliSupportGenerator.generate();

		// Generate ELI In
		ELIInGenerator eliInGenerator = new ELIInGenerator(this, lcp);
		eliInGenerator.generate();

		// If POSIX, generate POSIX-APOS binding and a main entry point
		if (getUnderlyingPlatformInstantiation() instanceof C_Posix) {
			// Generate the POSIX APOS binding
			PosixAPOSBindGenerator posixAposBindGenerator = new PosixAPOSBindGenerator(this, lcp);
			posixAposBindGenerator.generate();

			// Generate the Platform Manager main entry point.
			MainEntryPointGenerator mainEntryGenerator = new MainEntryPointGenerator(this, lcp);
			mainEntryGenerator.generate();
		}

	}

	public Path getOutputDir() {
		return pfManagerOutputDir;
	}

	public SystemModel getSystemModel() {
		return systemModel;
	}

	public Generic_Platform getUnderlyingPlatformInstantiation() {
		return underlyingPlatformInstantiation;
	}
}
