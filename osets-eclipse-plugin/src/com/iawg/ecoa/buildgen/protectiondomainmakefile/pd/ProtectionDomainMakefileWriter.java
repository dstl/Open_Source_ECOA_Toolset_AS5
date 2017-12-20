/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.protectiondomainmakefile.pd;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentdefinition.SM_ComponentType;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_VDRepository;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedModInst;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedTrigInst;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class ProtectionDomainMakefileWriter extends SourceFileWriter {
	private static final String SEP_PATTERN_111 = "src-gen/";
	private static final String SEP_PATTERN_A = "/4-ComponentImplementations/";
	private static final String SEP_PATTERN_B = "../src-gen";
	private static final String SEP_PATTERN_C = "\t\tobj/";
	private static final String SEP_PATTERN_D = "-Isrc-gen/";
	private static final String SEP_PATTERN_E = "_Controller.o";
	private static final String SEP_PATTERN_F = "../inc-gen";
	private static final String SEP_PATTERN_G = "_Controller";
	private static final String SEP_PATTERN_H = "../../../include";
	private static final String SEP_PATTERN_I = "../../../src";
	private static final String SEP_PATTERN_J = "src-gen";
	private static final String SEP_PATTERN_K = "inc-gen";
	private SM_ProtectionDomain pd;
	private String stepsDirString;

	public ProtectionDomainMakefileWriter(Path outputDir, SM_ProtectionDomain pd, Path stepsDir) {
		super(outputDir);
		this.stepsDirString = outputDir.normalize().relativize(stepsDir).toString().replace("\\", "/");
		this.pd = pd;

		setFileStructure();
	}

	private String generateDepText(String name, String srcDir, String incDir) {
		String outputString = "";

		outputString = "obj/" + name + ".o : " + srcDir + "/" + name + ".c ";
		if (incDir != null) {
			outputString += incDir + "/" + name + ".h";
		}
		outputString += LF + "\t$(CC) $(CFLAGS) -c $< -o $@" + LF + LF;

		return outputString;
	}

	@Override
	public void open() {
		super.openFile((outputDir.resolve("Makefile")));
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		fileStructure = "#PREAMBLE#" + LF + LF + "OBJS=#OBJECTS#" + LF + "TARGET=#TARGET#" + LF + "CC=gcc $(CPPFLAGS)" + LF + "LD=$(CC) -lrt -lpthread -lm -lstdc++" + LF + "PLATINCS=#PLATFORMINCLUDES#" + LF + "COMPINCS=#COMPINCLUDES#" + LF;

		if (pd.getLogicalComputingNode().isLittleEndian()) {
			fileStructure += "CFLAGS=-Iinc-gen -I../inc-gen -I../../include -I../../../include $(PLATINCS) $(COMPINCS) -DLITTLE_ENDIAN -DECOA_64BIT_SUPPORT" + LF + LF;
		} else {
			fileStructure += "CFLAGS=-Iinc-gen -I../inc-gen -I../../include -I../../../include $(PLATINCS) $(COMPINCS) -DECOA_64BIT_SUPPORT" + LF + LF;
		}

		fileStructure += "default: $(TARGET)" + LF + LF + "clean:" + LF + "\trm -f obj/*.o" + LF + LF + "#OBJDEPS#" + LF + LF + "dirCheck:" + LF + "\tmkdir -p obj" + LF + LF + "$(TARGET) : dirCheck $(OBJS)" + LF + "\t$(LD) $(OBJS) -o $@ $(LDLIBS) $(LDMAP)" + LF;

		codeStringBuilder.append(fileStructure);
	}

	public void writeIncludes() {
		String includeText = "";
		HashMap<SM_ComponentType, Boolean> compTypeCheck = new HashMap<SM_ComponentType, Boolean>();
		HashMap<SM_ComponentImplementation, Boolean> compImplCheck = new HashMap<SM_ComponentImplementation, Boolean>();
		HashMap<SM_ModuleImpl, Boolean> modImplCheck = new HashMap<SM_ModuleImpl, Boolean>();

		for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
			SM_ComponentType compType = compInst.getCompType();

			// Only do this once for a compType
			if (!compTypeCheck.containsKey(compType)) {
				compTypeCheck.put(compType, false);
				includeText += " \\" + LF + "\t\t";
				includeText += SEP_PATTERN_D + compType.getName() + "/service/req/inc/ ";
				includeText += SEP_PATTERN_D + compType.getName() + "/service/prov/inc/ ";
			}
			includeText += SEP_PATTERN_D + compInst.getName() + "/service/req/inc/ ";
			includeText += SEP_PATTERN_D + compInst.getName() + "/service/prov/inc/ ";

			includeText += " \\" + LF + "\t\t";
			includeText += SEP_PATTERN_D + compInst.getName() + "/versioned_data/inc/ ";

			SM_ComponentImplementation compImpl = compInst.getImplementation();

			// Only do this once for a compImpl
			if (!compImplCheck.containsKey(compImpl)) {
				compImplCheck.put(compImpl, false);
				includeText += " \\" + LF + "\t\t";
				includeText += SEP_PATTERN_D + compImpl.getName();
			}

			// For each module instance of the component instance
			for (SM_DeployedModInst depModInst : compInst.getDeployedModInsts()) {
				SM_ModuleInstance modInst = depModInst.getModInstance();

				includeText += " \\" + LF + "\t\t";
				includeText += SEP_PATTERN_D + compImpl.getName() + "/" + modInst.getName() + " ";
				includeText += SEP_PATTERN_D + compInst.getName() + "/" + modInst.getName() + " ";

			}

			// For each trigger instance of the component instance
			for (SM_DeployedTrigInst depTrigInst : compInst.getDeployedTrigInsts()) {
				SM_TriggerInstance trigInst = depTrigInst.getTrigInstance();
				if (trigInst != null) {
					includeText += " \\" + LF + "\t\t";
					includeText += SEP_PATTERN_D + compImpl.getName() + "/" + trigInst.getName() + " ";
					includeText += SEP_PATTERN_D + compInst.getName() + "/" + trigInst.getName() + " ";
				}
			}

			// For each dynamic trigger instance of the component instance
			for (SM_DeployedTrigInst depTrigInst : compInst.getDeployedTrigInsts()) {
				SM_DynamicTriggerInstance trigInst = depTrigInst.getDynTrigInstance();
				if (trigInst != null) {
					includeText += " \\" + LF + "\t\t";
					includeText += SEP_PATTERN_D + compImpl.getName() + "/" + trigInst.getName() + " ";
					includeText += SEP_PATTERN_D + compInst.getName() + "/" + trigInst.getName() + " ";
				}
			}

		}

		includeText += " \\" + LF + "\t\t";
		includeText += "-I../inc-gen";

		includeText += LF;

		WriterSupport.replaceText(codeStringBuilder, "#PLATFORMINCLUDES#", includeText);

		includeText = "";

		List<SM_DeployedModInst> depModInsts = pd.getDeployedModInsts();
		for (SM_DeployedModInst depModInst : depModInsts) {
			SM_ModuleInstance modInst = depModInst.getModInstance();
			SM_ComponentImplementation compImpl = modInst.getComponentImplementation();
			SM_ModuleImpl modImpl = modInst.getImplementation();

			// Only do this once for a modImpl
			if (!modImplCheck.containsKey(modImpl)) {
				modImplCheck.put(modImpl, false);
				includeText += " \\" + LF + "\t\t";
				includeText += "-I" + stepsDirString + SEP_PATTERN_A + compImpl.getContainingDir().getName(compImpl.getContainingDir().getNameCount() - 1).toString() + "/" + modImpl.getName();
			}
		}
		includeText += LF;

		WriterSupport.replaceText(codeStringBuilder, "#COMPINCLUDES#", includeText);

	}

	public void writeObjectDeps() {
		String objectText = "";

		// ************************************************
		// Dependencies for generic things

		objectText += generateDepText("ECOA_time_utils", SEP_PATTERN_B, SEP_PATTERN_F);

		objectText += generateDepText("ECOA_file_handler", SEP_PATTERN_B, SEP_PATTERN_F);

		objectText += generateDepText("ecoaLog", SEP_PATTERN_B, SEP_PATTERN_F);

		objectText += generateDepText("Defaulter", SEP_PATTERN_I, SEP_PATTERN_H);

		objectText += generateDepText("ELI_In__deserialiser", SEP_PATTERN_I, SEP_PATTERN_H);

		objectText += generateDepText("ELI_Out__serialiser", SEP_PATTERN_I, SEP_PATTERN_H);

		objectText += generateDepText("message_queue", SEP_PATTERN_B, SEP_PATTERN_F);

		objectText += generateDepText("fragment", SEP_PATTERN_I, SEP_PATTERN_H);

		objectText += generateDepText("reassemble", SEP_PATTERN_I, SEP_PATTERN_H);

		// ************************************************
		// dependencies for PD controller

		objectText += generateDepText(pd.getName() + "_PD_Controller", SEP_PATTERN_J, SEP_PATTERN_K);

		objectText += generateDepText(pd.getName() + "_Timer_Event_Manager", SEP_PATTERN_J, SEP_PATTERN_K);

		objectText += generateDepText(pd.getName() + "_Timer_Event_Handler", SEP_PATTERN_J, SEP_PATTERN_K);

		objectText += generateDepText(pd.getName() + "_ELI_In", SEP_PATTERN_J, SEP_PATTERN_K);

		objectText += generateDepText(pd.getName() + "_Service_Manager", SEP_PATTERN_J, SEP_PATTERN_K);

		// ************************************************
		// dependencies for platform stuff

		objectText += generateDepText("posix_apos_binding", SEP_PATTERN_J, SEP_PATTERN_K);

		objectText += generateDepText(pd.getName() + "_ELI_Support", SEP_PATTERN_J, SEP_PATTERN_K);

		objectText += generateDepText(pd.getName() + "_PD_Manager", SEP_PATTERN_J, SEP_PATTERN_K);

		objectText += generateDepText(pd.getName() + "_VC_IDS", SEP_PATTERN_J, SEP_PATTERN_K);

		// ************************************************
		// dependencies for service controllers

		List<SM_ComponentInstance> compInsts = pd.getComponentInstances();
		Map<SM_ComponentType, Boolean> compTypeCheck = new HashMap<SM_ComponentType, Boolean>();
		Map<SM_ModuleImpl, Boolean> modImplCheck = new HashMap<SM_ModuleImpl, Boolean>();

		for (SM_ComponentInstance compInst : compInsts) {
			SM_ComponentType compType = compInst.getCompType();

			{
				compTypeCheck.put(compType, false);
				for (SM_ServiceInstance servInst : compType.getServiceInstancesList()) {
					String controllerName = compInst.getName() + "_" + servInst.getName() + SEP_PATTERN_G;
					String srcDir = SEP_PATTERN_111 + compInst.getName() + "/service/prov";
					String incDir = SEP_PATTERN_111 + compInst.getName() + "/service/prov/inc";

					objectText += generateDepText(controllerName, srcDir, incDir);

				}

				for (SM_ServiceInstance refServInst : compType.getReferenceInstancesList()) {
					String controllerName = compInst.getName() + "_" + refServInst.getName() + SEP_PATTERN_G;
					String srcDir = SEP_PATTERN_111 + compInst.getName() + "/service/req";
					String incDir = SEP_PATTERN_111 + compInst.getName() + "/service/req/inc";

					objectText += generateDepText(controllerName, srcDir, incDir);
				}
			}

			// ************************************************
			// dependencies for containers
			// only once per module implementation
			SM_ComponentImplementation compImpl = compInst.getImplementation();

			for (SM_ModuleImpl modImpl : compInst.getImplementation().getModuleImplementations().values()) {
				if (!modImplCheck.containsKey(modImpl)) {
					modImplCheck.put(modImpl, false);

					String containerName = modImpl.getName() + "_container";
					String srcDir = SEP_PATTERN_111 + compImpl.getName();
					String incDir = stepsDirString + SEP_PATTERN_A + compImpl.getContainingDir().getName(compImpl.getContainingDir().getNameCount() - 1).toString() + "/" + modImpl.getName();

					objectText += generateDepText(containerName, srcDir, incDir);
				}
			}
			// ************************************************
			// dependencies for module instance controllers
			for (SM_DeployedModInst modInst : compInst.getDeployedModInsts()) {
				String controllerName = compInst.getName() + "_" + modInst.getModInstance().getName() + SEP_PATTERN_G;
				String srcDir = SEP_PATTERN_111 + compInst.getName() + "/" + modInst.getModInstance().getName();
				String incDir = SEP_PATTERN_111 + compInst.getName() + "/" + modInst.getModInstance().getName();

				objectText += generateDepText(controllerName, srcDir, incDir);
			}

			// ************************************************
			// dependencies for trigger instance controllers
			for (SM_DeployedTrigInst trigInst : compInst.getDeployedTrigInsts()) {
				String controllerName = null, srcDir = null, incDir = null, dynTrigModName = null;
				if (trigInst.getTrigInstance() != null) {
					controllerName = compInst.getName() + "_" + trigInst.getTrigInstance().getName() + SEP_PATTERN_G;
					srcDir = SEP_PATTERN_111 + compInst.getName() + "/" + trigInst.getTrigInstance().getName();
					incDir = SEP_PATTERN_111 + compInst.getName() + "/" + trigInst.getTrigInstance().getName();
				}
				if (trigInst.getDynTrigInstance() != null) {
					controllerName = compInst.getName() + "_" + trigInst.getDynTrigInstance().getName() + SEP_PATTERN_G;
					srcDir = SEP_PATTERN_111 + compInst.getName() + "/" + trigInst.getDynTrigInstance().getName();
					incDir = SEP_PATTERN_111 + compInst.getName() + "/" + trigInst.getDynTrigInstance().getName();
				}
				objectText += generateDepText(controllerName, srcDir, incDir);
				//
				// Add dynamic trigger "module" if relevant
				if (trigInst.getDynTrigInstance() != null) {
					//
					dynTrigModName = compInst.getName() + "_" + trigInst.getDynTrigInstance().getName() + "_DynTrigModule";
					srcDir = SEP_PATTERN_111 + compInst.getName() + "/" + trigInst.getDynTrigInstance().getName();
					incDir = SEP_PATTERN_111 + compInst.getName() + "/" + trigInst.getDynTrigInstance().getName();
					objectText += generateDepText(dynTrigModName, srcDir, incDir);
				}
			}

			// ************************************************
			// dependencies for versioned data managers
			for (SM_VDRepository vdRepo : compInst.getImplementation().getVdRepositories()) {
				String repoName = pd.getName() + "_" + compInst.getName() + "_" + "VD" + vdRepo.getName();
				String srcDir = SEP_PATTERN_111 + compInst.getName() + "/versioned_data";
				String incDir = SEP_PATTERN_111 + compInst.getName() + "/versioned_data/inc";

				objectText += generateDepText(repoName, srcDir, incDir);
			}

		}

		objectText += generateDepText("main", SEP_PATTERN_J, null);

		WriterSupport.replaceText(codeStringBuilder, "#OBJDEPS#", objectText);

	}

	public void writeObjects() {
		String objectText = "";

		// ************************************************
		// Objects for generic things
		objectText += "obj/ECOA_time_utils.o " + "obj/ECOA_file_handler.o " + "obj/ecoaLog.o " + "obj/main.o " + "obj/message_queue.o " + "obj/Defaulter.o ";

		objectText += "obj/ELI_In__deserialiser.o " + "obj/ELI_Out__serialiser.o " + "obj/fragment.o " + "obj/reassemble.o ";

		objectText += "\\" + LF;

		// ************************************************
		// Objects for PD controller
		objectText += "\t\t";

		objectText += "obj/" + pd.getName() + "_ELI_In.o ";

		objectText += "obj/" + pd.getName() + "_PD_Controller.o " + "obj/" + pd.getName() + "_Service_Manager.o ";
		objectText += " \\\n\t\t";

		objectText += "obj/" + pd.getName() + "_Timer_Event_Manager.o " + "obj/" + pd.getName() + "_Timer_Event_Handler.o ";
		objectText += " \\\n\t\t";

		// ************************************************

		objectText += "obj/posix_apos_binding.o ";

		objectText += "obj/" + pd.getName() + "_ELI_Support.o " + "obj/" + pd.getName() + "_PD_Manager.o " + "obj/" + pd.getName() + "_VC_IDS.o ";

		List<SM_ComponentInstance> compInsts = pd.getComponentInstances();
		Map<SM_ComponentType, Boolean> compTypeCheck = new HashMap<SM_ComponentType, Boolean>();
		Map<SM_ModuleImpl, Boolean> modImplCheck = new HashMap<SM_ModuleImpl, Boolean>();

		for (SM_ComponentInstance compInst : compInsts) {
			SM_ComponentType compType = compInst.getCompType();

			// ************************************************
			// Objects for Service Controllers
			{
				compTypeCheck.put(compType, false);
				List<SM_ServiceInstance> servInsts = compType.getServiceInstancesList();
				for (SM_ServiceInstance servInst : servInsts) {
					String body = " \\" + LF + SEP_PATTERN_C + compInst.getName() + "_" + servInst.getName() + SEP_PATTERN_G;
					objectText += body + ".o";
				}

				List<SM_ServiceInstance> refServInsts = compType.getReferenceInstancesList();
				for (SM_ServiceInstance refServInst : refServInsts) {
					String body = " \\" + LF + SEP_PATTERN_C + compInst.getName() + "_" + refServInst.getName() + SEP_PATTERN_G;
					objectText += body + ".o";
				}
			}

			// ************************************************
			// Objects for containers and modules
			// only once per module implementation
			for (SM_ModuleImpl modImpl : compInst.getImplementation().getModuleImplementations().values()) {
				if (!modImplCheck.containsKey(modImpl)) {
					modImplCheck.put(modImpl, false);

					objectText += " \\" + LF + SEP_PATTERN_C + modImpl.getName() + "_container.o";
				}
			}

			// ************************************************
			// Objects for module instance controllers
			for (SM_DeployedModInst modInst : compInst.getDeployedModInsts()) {
				objectText += " \\" + LF + SEP_PATTERN_C + compInst.getName() + "_" + modInst.getModInstance().getName() + SEP_PATTERN_E;
			}

			// ************************************************
			// Objects for trigger instance controllers
			for (SM_DeployedTrigInst trigInst : compInst.getDeployedTrigInsts()) {
				if (trigInst.getTrigInstance() != null)
					objectText += " \\" + LF + SEP_PATTERN_C + compInst.getName() + "_" + trigInst.getTrigInstance().getName() + SEP_PATTERN_E;
				if (trigInst.getDynTrigInstance() != null) {
					objectText += " \\" + LF + SEP_PATTERN_C + compInst.getName() + "_" + trigInst.getDynTrigInstance().getName() + SEP_PATTERN_E;
					objectText += " \\" + LF + SEP_PATTERN_C + compInst.getName() + "_" + trigInst.getDynTrigInstance().getName() + "_DynTrigModule.o";
				}
			}

			// ************************************************
			// Objects for versioned data managers
			for (SM_VDRepository vdRepo : compInst.getImplementation().getVdRepositories()) {
				objectText += " \\" + LF + SEP_PATTERN_C + pd.getName() + "_" + compInst.getName() + "_" + "VD" + vdRepo.getName() + ".o";
			}
		}

		modImplCheck.clear();

		for (SM_ComponentInstance compInst : compInsts) {
			SM_ComponentImplementation compImpl = compInst.getImplementation();

			// Objects for containers and modules
			// only once per module implementation
			for (SM_ModuleImpl modImpl : compInst.getImplementation().getModuleImplementations().values()) {
				if (!modImplCheck.containsKey(modImpl)) {
					modImplCheck.put(modImpl, false);

					if (modImpl.isPrebuilt()) {
						for (String objectFile : modImpl.getObjectFileList()) {
							objectText += " \\" + LF + "\t\t" + stepsDirString + "/" + modImpl.getPrebuiltObjLocation() + "/" + objectFile;
						}
					} else {
						objectText += " \\" + LF + "\t\t" + stepsDirString + SEP_PATTERN_A + compImpl.getContainingDir().getName(compImpl.getContainingDir().getNameCount() - 1).toString() + "/" + modImpl.getName() + "/" + modImpl.getName() + ".a";
					}
				}
			}
		}

		objectText += LF;

		WriterSupport.replaceText(codeStringBuilder, "#OBJECTS#", objectText);
	}

	public void writePreamble() {
		String preambleText = "";

		preambleText += "# Generated Makefile for " + pd.getName();

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeTarget() {
		String targetText = "";

		targetText += pd.getName();

		// Replace the #TARGET# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#TARGET#", targetText);
	}

}
