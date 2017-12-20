/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.WriterSupport;
import com.iawg.ecoa.platformgen.common.underlyingplatform.C_MAI_APOS;
import com.iawg.ecoa.platformgen.common.underlyingplatform.C_Posix;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.platformgen.gen.assetids.AssetIDGenerator;
import com.iawg.ecoa.platformgen.gen.clienttype.ClientInfoTypeGenerator;
import com.iawg.ecoa.platformgen.gen.compid.CompInstanceIDGenerator;
import com.iawg.ecoa.platformgen.gen.defaulter.DefaulterGenerator;
import com.iawg.ecoa.platformgen.gen.ecoalog.ECOALogGenerator;
import com.iawg.ecoa.platformgen.gen.filehandler.FileHandlerGenerator;
import com.iawg.ecoa.platformgen.gen.frag_reassemble.FragReassembleGenerator;
import com.iawg.ecoa.platformgen.gen.messagequeue.MessageQueueGenerator;
import com.iawg.ecoa.platformgen.gen.pdid.PDIDGenerator;
import com.iawg.ecoa.platformgen.gen.posixaposbind.portno.PortNoGenerator;
import com.iawg.ecoa.platformgen.gen.privatecontext.PrivateContextGenerator;
import com.iawg.ecoa.platformgen.gen.serialiser.SerialiserGenerator;
import com.iawg.ecoa.platformgen.gen.serviceuid.ServiceUIDGenerator;
import com.iawg.ecoa.platformgen.gen.timeutils.TimeUtilsGenerator;
import com.iawg.ecoa.platformgen.pd.clienttype.moduleopuid.ModInstanceOpUIDGenerator;
import com.iawg.ecoa.platformgen.pd.clienttype.serviceopuid.ServiceInstanceOpUIDGenerator;
import com.iawg.ecoa.platformgen.pd.containerbody.ContainerBodyGenerator;
import com.iawg.ecoa.platformgen.pd.dynamictriggerinstance.DynTrigInstControllerGenerator;
import com.iawg.ecoa.platformgen.pd.eliin.ELIInGenerator;
import com.iawg.ecoa.platformgen.pd.elisupport.ELISupportGenerator;
import com.iawg.ecoa.platformgen.pd.externalinterface.ExternalInterfaceBodyGenerator;
import com.iawg.ecoa.platformgen.pd.mainentry.MainEntryPointGenerator;
import com.iawg.ecoa.platformgen.pd.moduleid.CompImplModInstIDGenerator;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.DynTrigInstanceILIGenerator;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ModInstanceILIGenerator;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.TrigInstanceILIGenerator;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ModuleInstanceILI;
import com.iawg.ecoa.platformgen.pd.moduleinstance.ModInstControllerGenerator;
import com.iawg.ecoa.platformgen.pd.pdcontrol.PDControllerGenerator;
import com.iawg.ecoa.platformgen.pd.pdmanager.PDManagerGenerator;
import com.iawg.ecoa.platformgen.pd.pinfo.compinst.CompInstPinfoGenerator;
import com.iawg.ecoa.platformgen.pd.pinfo.pd.PDPinfoGenerator;
import com.iawg.ecoa.platformgen.pd.posixaposbind.PosixAPOSBindGenerator;
import com.iawg.ecoa.platformgen.pd.serviceapi.provided.ProvidedServiceAPIGenerator;
import com.iawg.ecoa.platformgen.pd.serviceapi.required.RequiredServiceAPIGenerator;
import com.iawg.ecoa.platformgen.pd.servicemanager.ServiceManagerGenerator;
import com.iawg.ecoa.platformgen.pd.serviceopuid.ServiceOpUIDGenerator;
import com.iawg.ecoa.platformgen.pd.timereventhandler.TimerEventHandlerGenerator;
import com.iawg.ecoa.platformgen.pd.timereventmanager.TimerEventManagerGenerator;
import com.iawg.ecoa.platformgen.pd.triggerinstance.TrigInstControllerGenerator;
import com.iawg.ecoa.platformgen.pd.vcid.VCIDGenerator;
import com.iawg.ecoa.platformgen.pd.versioneddata.VDRepositoryGenerator;
import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstanceProperty;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_PinfoValue;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedModInst;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedTrigInst;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingNode;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class PlatformGenerator {
	private static final Logger LOGGER = LogManager.getLogger(PlatformGenerator.class);
	private static final String SEP_PATTERN_11 = "linux";
	private static final String SEP_PATTERN_A = "ims-vxworks";

	private SystemModel systemModel;
	private Path containerOutputDir;
	private Path pdOutputDir;
	private Path stepsDir;
	// Currently only support C.
	private Generic_Platform underlyingPlatformInstantiation;

	public PlatformGenerator(SystemModel systemModel, Path containerOutputDir, Path stepsDir) {
		this.systemModel = systemModel;
		this.stepsDir = stepsDir;
		this.containerOutputDir = containerOutputDir;
	}

	private void copyFile(Path source, Path destination) {
		try {
			if (!Files.exists(destination.getParent())) {
				Files.createDirectories(destination.getParent());
			}

			Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			LOGGER.info("PINFO file - " + source.getFileName() + " cannot be found to copy to deployment area");
			
		}
	}

	private void deployPinfoFiles(SM_ProtectionDomain pd) {
		Path destinationRoot = pdOutputDir.resolve("build_" + pd.getLogicalComputingNode().getLogicalComputingPlatform().getName() + "/DeployedPinfo");

		// Need to copy PINFO files to the "deployment" area.
		for (SM_ComponentInstance compInst : systemModel.getFinalAssembly().getComponentInstances()) {
			for (SM_ModuleInstance modInst : compInst.getImplementation().getModuleInstances().values()) {
				// Copy any private (module instance level) PINFOS
				for (SM_PinfoValue pinfoVal : modInst.getPrivatePinfoValues()) {
					// Need to copy the PINFO file from
					// Steps/4-ComponentImplementations
					Path source = Paths.get(stepsDir + "/4-ComponentImplementations/" + compInst.getImplementation().getName() + "/Pinfo/" + pinfoVal.getPinfoFile().getName());
					Path destination = Paths.get(destinationRoot + "/" + compInst.getName() + "/" + pinfoVal.getPinfoFile().getName());

					copyFile(source, destination);
				}

				// Copy any public (assembly level) PINFOS
				for (SM_PinfoValue pinfoVal : modInst.getPublicPinfoValues()) {
					SM_ComponentInstanceProperty compInstProp = compInst.getPropertyByName(pinfoVal.getRelatedCompTypeProperty().getName());

					// Need to copy the PINFO file from Steps/5-Integration
					Path source = Paths.get(stepsDir + "/5-Integration/Pinfo/" + compInstProp.getValue());
					Path destination = Paths.get(destinationRoot + "/" + compInstProp.getValue());

					copyFile(source, destination);
				}
			}
		}
	}

	public void generatePlatformCode() {
		// Generate all the system-wide generic code files.
		generateSystemGenericCodeFiles();

		// Generate all the node-wide generic code files.
		generateNodeGenericCodeFiles();

		// Generate all code files specific to a given protection domain
		generateProtectionDomainFiles();

		// Generate the platform manager code files.
		generatePlatformManagerFiles();
	}

	private void generateNodeGenericCodeFiles() {
		for (SM_LogicalComputingPlatform lcp : systemModel.getLogicalSystem().getLogicalcomputingPlatforms()) {
			for (SM_LogicalComputingNode lcn : lcp.getLogicalcomputingNodes()) {
				switch (lcn.getOs()) {
				case SEP_PATTERN_11:
					underlyingPlatformInstantiation = new C_Posix();
					break;
				case SEP_PATTERN_A:
					underlyingPlatformInstantiation = new C_MAI_APOS();
					break;
				default:
					LOGGER.info("Underlying platform not supported - options are linux or ims-vxworks");
					
					break;
				}

				// Generate the Message Queue Implementation File
				// NOTE: may not be required for all platforms.
				MessageQueueGenerator messageQGen = new MessageQueueGenerator(this, lcn);
				messageQGen.generate();

				// Generate a logging function.
				ECOALogGenerator ecoaLog = new ECOALogGenerator(this, lcn);
				ecoaLog.generate();

				// Generate the ECOA time utilities
				TimeUtilsGenerator ecoaTimeUtils = new TimeUtilsGenerator(this, lcn);
				ecoaTimeUtils.generate();

				// Generate the file handler
				FileHandlerGenerator fileHandler = new FileHandlerGenerator(this, lcn);
				fileHandler.generate();
			}
		}
	}

	/**
	 * 
	 * Generates all the Protection Domain Files.
	 * 
	 */
	private void generateProtectionDomainFiles() {
		for (SM_ProtectionDomain pd : systemModel.getDeployment().getProtectionDomains()) {
			switch (pd.getLogicalComputingNode().getOs()) {
			case SEP_PATTERN_11:
				underlyingPlatformInstantiation = new C_Posix();
				break;
			case SEP_PATTERN_A:
				underlyingPlatformInstantiation = new C_MAI_APOS();
				break;
			default:
				LOGGER.info("Underlying platform not supported - options are POSIX or MAI_APOS");
				
				break;
			}

			String platformName = pd.getLogicalComputingNode().getLogicalComputingPlatform().getName();
			String nodeName = pd.getLogicalComputingNode().getName();
			pdOutputDir = containerOutputDir.resolve(platformName + "/" + nodeName + "/" + pd.getName());

			// Copy the PINFO files to the deployment area
			deployPinfoFiles(pd);

			// Generate a file defining all public PINFO current sizes.
			PDPinfoGenerator pdPinfo = new PDPinfoGenerator(this, pd);
			pdPinfo.generate();

			ArrayList<SM_ComponentImplementation> processedCompImplList = new ArrayList<SM_ComponentImplementation>();
			ArrayList<SM_ModuleImpl> processedModImplList = new ArrayList<SM_ModuleImpl>();
			ArrayList<SM_ComponentInstance> processedCompInstList = new ArrayList<SM_ComponentInstance>();
			HashMap<SM_ModuleInstance, ModuleInstanceILI> modInstILIMap = new HashMap<SM_ModuleInstance, ModuleInstanceILI>();

			for (SM_DeployedModInst depModInst : pd.getDeployedModInsts()) {
				// Generate the component implementation files (if not already
				// generated)
				SM_ComponentImplementation compImpl = depModInst.getCompInstance().getImplementation();

				if (!processedCompImplList.contains(compImpl)) {
					// Generate a file defining all the module instance IDs for
					// this component implementation
					CompImplModInstIDGenerator compImplModInstGenerator = new CompImplModInstIDGenerator(this, compImpl);
					compImplModInstGenerator.generate();

					// Generate a file defining all the module instance
					// operation UIDs for this component implementation
					ModInstanceOpUIDGenerator modInstOpUIDGenerator = new ModInstanceOpUIDGenerator(this, compImpl);
					modInstOpUIDGenerator.generate();

					// Generate a file defining all the service instance
					// operation UIDs for this component implementation
					ServiceInstanceOpUIDGenerator serviceInstOpUIDGenerator = new ServiceInstanceOpUIDGenerator(this, compImpl);
					serviceInstOpUIDGenerator.generate();

					// Generate a file defining all service operation UIDs.
					ServiceOpUIDGenerator serviceOpUID = new ServiceOpUIDGenerator(this, pd);
					serviceOpUID.generate();

					if (!compImpl.getExternalSenders().isEmpty()) {
						ExternalInterfaceBodyGenerator externalInterfacegenerator = new ExternalInterfaceBodyGenerator(this, compImpl, depModInst);
						externalInterfacegenerator.generate();
					}

					processedCompImplList.add(compImpl);
				}

				if (!processedCompInstList.contains(depModInst.getCompInstance())) {
					// Generate service API - 1 per component instance - service
					// instance.
					SM_ComponentInstance compInst = depModInst.getCompInstance();
					// First do provided services
					for (SM_ServiceInstance serviceOrRef : compInst.getCompType().getServiceInstancesList()) {
						ProvidedServiceAPIGenerator provServiceAPIGen = new ProvidedServiceAPIGenerator(this, pd, compInst, serviceOrRef);
						provServiceAPIGen.generate();
					}
					// Now process required services
					for (SM_ServiceInstance serviceOrRef : compInst.getCompType().getReferenceInstancesList()) {
						RequiredServiceAPIGenerator reqServiceAPIGen = new RequiredServiceAPIGenerator(this, pd, compInst, serviceOrRef);
						reqServiceAPIGen.generate();
					}

					// Generate a file defining all private (component instance)
					// PINFO current sizes.
					CompInstPinfoGenerator compInstPinfo = new CompInstPinfoGenerator(this, compInst);
					compInstPinfo.generate();

					processedCompInstList.add(compInst);
				}

				// Generate the module instance ILI messages
				ModInstanceILIGenerator modInstILIGenerator = new ModInstanceILIGenerator(this, depModInst.getModInstance());
				modInstILIGenerator.generate();

				// Add to the hashmap.
				modInstILIMap.put(depModInst.getModInstance(), modInstILIGenerator.getModInstILI());

				// Generate module instance controller - 1 per deployed module
				// instance
				ModInstControllerGenerator modInstCont = new ModInstControllerGenerator(this, pd, depModInst, modInstILIMap.get(depModInst.getModInstance()));
				modInstCont.generate();

			}

			// Generate the module implementation container body (if not already
			// generated by another module instance of same implementation)
			for (Entry<SM_ModuleInstance, ModuleInstanceILI> entry : modInstILIMap.entrySet()) {
				SM_ModuleInstance modInst = entry.getKey();

				SM_ModuleImpl modImpl = modInst.getImplementation();
				if (!processedModImplList.contains(modImpl)) {
					ContainerBodyGenerator contBody = new ContainerBodyGenerator(this, pd, modImpl, modInstILIMap);
					contBody.generate();
				}
			}

			// Generate the trigger instance controllers
			for (SM_DeployedTrigInst deployedTrigInst : pd.getDeployedTrigInsts()) {
				TrigInstControllerGenerator trigInstCont = new TrigInstControllerGenerator(this, pd, deployedTrigInst);
				trigInstCont.generate();

				// Generate the module instance ILI messages (for the trigger)
				TrigInstanceILIGenerator trigInstILIGenerator = new TrigInstanceILIGenerator(this, deployedTrigInst.getTrigInstance());
				trigInstILIGenerator.generate();
			}

			// Generate the dynamic trigger instance controllers
			for (SM_DeployedTrigInst deployedTrigInst : pd.getDeployedDynTrigInsts()) {
				// Generate the module instance ILI messages (for the trigger)
				DynTrigInstanceILIGenerator dynTrigInstILIGenerator = new DynTrigInstanceILIGenerator(this, deployedTrigInst.getDynTrigInstance());
				dynTrigInstILIGenerator.generate();

				DynTrigInstControllerGenerator dynTrigInstCont = new DynTrigInstControllerGenerator(this, pd, deployedTrigInst, dynTrigInstILIGenerator.getTrigInstILI());
				dynTrigInstCont.generate();
			}

			// Generate a protection domain controller - to initialise the
			// system.
			PDControllerGenerator pdController = new PDControllerGenerator(this, pd);
			pdController.generate();

			// Generate the Timer_Event_Handler
			TimerEventHandlerGenerator timerEventHandler = new TimerEventHandlerGenerator(this, pd);
			timerEventHandler.generate();

			// Generate the Timer_Event_Manager
			TimerEventManagerGenerator timerEventManager = new TimerEventManagerGenerator(this, pd);
			timerEventManager.generate();

			// Generate a service manager to hold the state of all services
			ServiceManagerGenerator servManager = new ServiceManagerGenerator(this, pd);
			servManager.generate();

			// Generate the versioned data repositories required for this
			// protection domain
			VDRepositoryGenerator vdRepoGen = new VDRepositoryGenerator(this, pd);
			vdRepoGen.generate();

			// Generate VC IDs
			VCIDGenerator vcIDGenerator = new VCIDGenerator(this, pd);
			vcIDGenerator.generate();

			// Generate ELI Support
			ELISupportGenerator eliSupportGenerator = new ELISupportGenerator(this, pd);
			eliSupportGenerator.generate();

			// Generate ELI In processor
			ELIInGenerator eliInGenerator = new ELIInGenerator(this, pd);
			eliInGenerator.generate();

			// Generate protection domain IDs
			PDIDGenerator pdIDGenerator = new PDIDGenerator(this, pd.getLogicalComputingNode().getLogicalComputingPlatform());
			pdIDGenerator.generate();

			// Generate protection domain manager
			PDManagerGenerator pdGenerator = new PDManagerGenerator(this, pd);
			pdGenerator.generate();

			// If POSIX, always generate POSIX-APOS binding no matter how many
			// PDs/platforms
			if (getunderlyingPlatformInstantiation() instanceof C_Posix) {
				PosixAPOSBindGenerator posixAposBindGenerator = new PosixAPOSBindGenerator(this, pd);
				posixAposBindGenerator.generate();

				// Generate a main entry point for the application
				MainEntryPointGenerator mainEntryPointGenerator = new MainEntryPointGenerator(this, pd);
				mainEntryPointGenerator.generate();

				// Generate the global socket offset file.
				PortNoGenerator sockOffsetGenerator = new PortNoGenerator(this);
				sockOffsetGenerator.generate();
			}
		}
	}

	/**
	 * 
	 * Generate all the Platform Manager files. - These will be generated in
	 * "#output_dir#/#first_node_in_platform_name#/#lcp_name#_PF_Manager"
	 * directory.
	 * 
	 */
	private void generatePlatformManagerFiles() {
		for (SM_LogicalComputingPlatform lcp : systemModel.getLogicalSystem().getLogicalcomputingPlatforms()) {
			// TODO - this isn't ideal but currently don't specify the node
			// which the platform manager is to execute on (so simply take the
			// first one!)
			switch (lcp.getLogicalcomputingNodes().get(0).getOs()) {
			case SEP_PATTERN_11:
				underlyingPlatformInstantiation = new C_Posix();
				break;
			case SEP_PATTERN_A:
				underlyingPlatformInstantiation = new C_MAI_APOS();
				break;
			default:
				LOGGER.info("Underlying platform not supported - options are POSIX or MAI_APOS");
				
				break;
			}

			// TODO - need to be able to specify which node the PF_Manager is to
			// be deployed on - currently default to using the one specified
			// first
			// Could look at adding an option to the tool config for this...
			// Generate the platform manager
			PlatformManagerGenerator pfManagerGenerator = new PlatformManagerGenerator(systemModel, containerOutputDir.resolve(lcp.getName() + "/" + lcp.getLogicalcomputingNodes().get(0).getName() + "/" + lcp.getName() + "_PF_Manager"), underlyingPlatformInstantiation, lcp);
			pfManagerGenerator.generate();
		}
	}

	/**
	 * 
	 * Generate code files which are common to the whole ECOA System. - These
	 * will be generated in "#output_dir#/inc-gen" or "#output_dir#/src-gen"
	 * directories
	 * 
	 */
	private void generateSystemGenericCodeFiles() {
		// Generate the Asset IDs
		AssetIDGenerator assetIDGenerator = new AssetIDGenerator(this);
		assetIDGenerator.generate();

		// Generate defaulter code
		DefaulterGenerator defaulterGenerator = new DefaulterGenerator(this);
		defaulterGenerator.generate(systemModel);

		// Generate the private context declaration used by the module
		// instances.
		PrivateContextGenerator privateContext = new PrivateContextGenerator(this);
		privateContext.generate();

		// Generate the ILI message definition
		WriterSupport.copyResource(containerOutputDir.resolve("include/ILI_Message.h"), "ILI_Message.h");

		// Generate a file defining all component instances and module instances
		// with an ID.
		CompInstanceIDGenerator compInstIDGenerator = new CompInstanceIDGenerator(this);
		compInstIDGenerator.generate();

		// Generate a file defining all service UIDs.
		ServiceUIDGenerator serviceUID = new ServiceUIDGenerator(this);
		serviceUID.generate();

		// Generate a file defining the client info type
		ClientInfoTypeGenerator clientInfoTypeGen = new ClientInfoTypeGenerator(this);
		clientInfoTypeGen.generate();

		// Generate ELI header
		WriterSupport.copyResource(containerOutputDir.resolve("include/ELI_Message.h"), "ELI_Message.h");

		// Generate fragment/reassemble code
		FragReassembleGenerator fragmentReassembleGenerator = new FragReassembleGenerator(this);
		fragmentReassembleGenerator.generate();

		// Generate serialisation code
		SerialiserGenerator serialiser = new SerialiserGenerator(this);
		serialiser.generate(systemModel);

		// Generate byteswap code (always do this for now, as the Service API
		// ALWAYS includes it (probably not right!)
		WriterSupport.copyResource(containerOutputDir.resolve("include/ecoaByteswap.h"), "ecoaByteswap.h");

		// Generate process control
		WriterSupport.copyResource(containerOutputDir.resolve("include/prctl.h"), "prctl.h");
	}

	public Path getOutputDir() {
		return containerOutputDir;
	}

	public Path getPdOutputDir() {
		return pdOutputDir;
	}

	public SystemModel getSystemModel() {
		return systemModel;
	}

	public Generic_Platform getunderlyingPlatformInstantiation() {
		return underlyingPlatformInstantiation;
	}
}
