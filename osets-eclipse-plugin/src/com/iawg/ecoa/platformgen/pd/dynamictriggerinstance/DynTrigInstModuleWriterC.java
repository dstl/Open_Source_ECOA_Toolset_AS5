/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.dynamictriggerinstance;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.TypesProcessorC;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.DynamicTriggerInstanceILI;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventReceivedOp;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedTrigInst;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class DynTrigInstModuleWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_111 = "void ";

	private SM_ProtectionDomain protectionDomain;
	// private SM_DeployedTrigInst deployedTrigInst;
	private SM_DynamicTriggerInstance dynTrigInstance;
	private SM_ComponentInstance compInst;
	private SM_ComponentImplementation compImpl;
	private boolean isHeader;

	private ArrayList<String> includeList = new ArrayList<String>();
	private Generic_Platform underlyingPlatform;

	public DynTrigInstModuleWriterC(PlatformGenerator platformGenerator, boolean isHeader, Path outputDir, SM_DeployedTrigInst deployedTrigInst, SM_ProtectionDomain pd, DynamicTriggerInstanceILI dynTrigInstILI) {
		super(outputDir);
		this.underlyingPlatform = platformGenerator.getunderlyingPlatformInstantiation();

		this.protectionDomain = pd;
		// this.deployedTrigInst = deployedTrigInst;
		this.dynTrigInstance = deployedTrigInst.getDynTrigInstance();
		this.compInst = deployedTrigInst.getCompInstance();
		this.compImpl = compInst.getImplementation();
		this.isHeader = isHeader;

		setFileStructure();
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve(dynTrigInstance.getName() + "/" + compInst.getName() + "_" + dynTrigInstance.getName() + "_DynTrigModule.h"));
		} else {
			super.openFile(outputDir.resolve(dynTrigInstance.getName() + "/" + compInst.getName() + "_" + dynTrigInstance.getName() + "_DynTrigModule.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#CONTEXTDECL#" + LF + "#PRIVATE_DECLS#" + LF + "#LIFECYCLE_OPS#" + LF + "#DYNTRIG#" + LF + "#POSTAMBLE#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#CONTEXTDECL#" + LF + "#LIFECYCLE_OPS#" + LF +
			// "#PRIVATE_DECLS#" + LF +
					"#DYNTRIG#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + compInst.getName() + "_" + dynTrigInstance.getName() + "_DynTrigModule.h */" + LF + LF + "#ifndef " + compInst.getName().toUpperCase() + "_" + dynTrigInstance.getName().toUpperCase() + "_DynTrigModule".toUpperCase() + "_H" + LF + "#define " + compInst.getName().toUpperCase() + "_" + dynTrigInstance.getName().toUpperCase() + "_DynTrigModule".toUpperCase() + "_H" + LF;
		} else {
			preambleText += "/* File " + compInst.getName() + "_" + dynTrigInstance.getName() + "_DynTrigModule.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeIncludes() {
		if (!isHeader) {
			includeList.add("ECOA_time_utils");
			includeList.add(compInst.getName() + "_" + dynTrigInstance.getName() + "_DynTrigModule");
			includeList.add(compInst.getName() + "_" + dynTrigInstance.getName() + "_Controller");
			includeList.addAll(underlyingPlatform.addIncludesModInstCont());
			includeList.add(compImpl.getName() + "_Module_Instance_ID");
			includeList.add(compImpl.getName() + "_Module_Instance_Operation_UID");
			includeList.add(protectionDomain.getName() + "_Timer_Event_Manager");
			includeList.add("time");
			includeList.add("string");
		} else {
			includeList.add(dynTrigInstance.getName() + "_ILI");
			includeList.add("ECOA");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void WriteContextDecl() {
		String contextText = "";

		if (isHeader) {
			contextText = "typedef struct " + compInst.getName() + "_" + dynTrigInstance.getName() + "__context_t {" + LF + "	void* paramRef[" + dynTrigInstance.getSize() + "];" + LF + "} " + compInst.getName() + "_" + dynTrigInstance.getName() + "__context;" + LF + LF;
		}
		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#CONTEXTDECL#", contextText);
	}

	public void WriteLifecycleOps() {
		String lifecycleOpsText = "";

		// INITIALIZE operation
		lifecycleOpsText += SEP_PATTERN_111 + compInst.getName() + "_" + dynTrigInstance.getName() + "__INITIALIZE__received(" + compInst.getName() + "_" + dynTrigInstance.getName() + "__context *context)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "	ECOA__int32 timerID;" + LF + "	/**/" + LF + "	for( timerID = 0; timerID < " + dynTrigInstance.getSize() + "; timerID++ )" + LF + "		context->paramRef[timerID] = NULL;" + LF + "//	printf(\"" + compInst.getName() + "_" + dynTrigInstance.getName() + "__INITIALIZE__received\\n\");" + LF + "}" + LF + LF;
		}

		// START operation
		lifecycleOpsText += SEP_PATTERN_111 + compInst.getName() + "_" + dynTrigInstance.getName() + "__START__received(" + compInst.getName() + "_" + dynTrigInstance.getName() + "__context *context)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "//	printf(\"" + compInst.getName() + "_" + dynTrigInstance.getName() + "__START__received\\n\");" + LF + "}" + LF + LF;
		}

		// STOP operation
		lifecycleOpsText += SEP_PATTERN_111 + compInst.getName() + "_" + dynTrigInstance.getName() + "__STOP__received(" + compInst.getName() + "_" + dynTrigInstance.getName() + "__context *context)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "//	printf(\"" + compInst.getName() + "_" + dynTrigInstance.getName() + "__STOP__received\\n\");" + LF + "}" + LF + LF;
		}

		// SHUTDOWN operation
		lifecycleOpsText += SEP_PATTERN_111 + compInst.getName() + "_" + dynTrigInstance.getName() + "__SHUTDOWN__received(" + compInst.getName() + "_" + dynTrigInstance.getName() + "__context *context)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "//	printf(\"" + compInst.getName() + "_" + dynTrigInstance.getName() + "__SHUTDOWN__received\\n\");" + LF + "}" + LF + LF;
		}

		// REINITIALIZE operation
		lifecycleOpsText += SEP_PATTERN_111 + compInst.getName() + "_" + dynTrigInstance.getName() + "__REINITIALIZE__received(" + compInst.getName() + "_" + dynTrigInstance.getName() + "__context *context)";

		if (isHeader) {
			lifecycleOpsText += ";" + LF;
		} else {
			lifecycleOpsText += LF + "{" + LF + "//	printf(\"" + compInst.getName() + "_" + dynTrigInstance.getName() + "__REINITIALIZE__received\\n\");" + LF + "}" + LF + LF;
		}

		// Replace the #LIFECYCLE_OPS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#LIFECYCLE_OPS#", lifecycleOpsText);
	}

	public void writePrivateDecls() {
		String privateDeclsText = "";

		if (isHeader) {
			privateDeclsText += LF + "typedef struct " + compInst.getName() + "_" + dynTrigInstance.getName() + "__TriggerParams_t {" + LF;

			for (SM_EventReceivedOp evRx : dynTrigInstance.getModuleType().getEventReceivedOps()) {
				if (evRx.getInputs() != null) {
					for (SM_OperationParameter opParam : evRx.getInputs()) {
						privateDeclsText += "	" + TypesProcessorC.convertParameterToC(opParam.getType()) + " " + opParam.getName() + ";" + LF;
					}
				}
			}
			privateDeclsText += "} " + compInst.getName() + "_" + dynTrigInstance.getName() + "__TriggerParams;" + LF + LF;
		}
		// Replace the #PRIVATE_DECLS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PRIVATE_DECLS#", privateDeclsText);
	}

	public void writeTriggerOps(Generic_Platform underlyingPlatformInstantiation) {
		String eventOpText = "";

		for (SM_EventReceivedOp evRx : dynTrigInstance.getModuleType().getEventReceivedOps()) {
			eventOpText += SEP_PATTERN_111 + compInst.getName() + "_" + dynTrigInstance.getName() + "__" + evRx.getName() + "__received(" + compInst.getName() + "_" + dynTrigInstance.getName() + "__context *context";

			if (evRx.getInputs() != null) {
				for (SM_OperationParameter opParam : evRx.getInputs()) {
					eventOpText += CLanguageSupport.writeConstParam(opParam);
				}
			}

			if (isHeader) {
				eventOpText += ");" + LF;
			} else {
				if (evRx.getName().equals("in")) {
					eventOpText += ")" + LF + "{" + LF + "	" + compInst.getName() + "_" + dynTrigInstance.getName() + "__TriggerParams* paramRef;" + LF + "	unsigned char msg[128];" + LF + "	int size, timerID, timerCount = 0;" + LF + "	ECOA__duration delay_;" + LF + "	ECOA__return_status erc = 0;" + LF + LF + "	/* Got a spare timer? */" + LF + "//	for( timerID = 0; timerID < " + dynTrigInstance.getSize() + "; timerID++ ){" + LF + "//		if( context->paramRef[timerID] != NULL ) timerCount++;" + LF + "//	}" + LF + "	for( timerID = 0; timerID < " + dynTrigInstance.getSize() + "; timerID++ ){" + LF + "		if( context->paramRef[timerID] == NULL ) break;" + LF + "	}" + LF + "	if( timerID >= " + dynTrigInstance.getSize() + " ){" + LF + "		size = sprintf( (char*)msg, \"" + compInst.getName() + "_" + dynTrigInstance.getName() + " out of timers (" + dynTrigInstance.getSize() + " max)...\007\\n\" );" + LF + "	 	ecoaLog( msg, size, LOG_LEVEL_ERROR, 0/*modInstId*/ );" + LF + "		return;" + LF + "	}" + LF + "	context->paramRef[timerID] = paramRef = (" + compInst.getName() + "_" + dynTrigInstance.getName() + "__TriggerParams*)malloc(sizeof(struct "
							+ compInst.getName() + "_" + dynTrigInstance.getName() + "__TriggerParams_t));" + LF + "	/*" + LF + "	 * We MUST send in the actual value of parameters, NOT pointers to them." + LF + "	 * By the time the delay expires, that which was pointed to may be gone..." + LF + "	 */" + LF;

					if (evRx.getInputs() != null) {
						for (SM_OperationParameter opParam : evRx.getInputs()) {
							if (opParam.getType().isSimple()) {
								eventOpText += "	paramRef->" + opParam.getName() + " = " + opParam.getName() + ";" + LF;
							} else {
								eventOpText += "	memcpy( &(paramRef->" + opParam.getName() + "), " + opParam.getName() + ", sizeof(" + TypesProcessorC.convertParameterToC(opParam.getType()) + "));" + LF;
							}
						}
					}

					eventOpText += LF + "	/* Reserve and start a timer */" + LF + "	if( delay->seconds == 0 && delay->nanoseconds == 0 ){ /* {0,0} will DISARM a timer.  Not what we want... */" + LF + "	   delay_ = (ECOA__duration){0,1};                    /* We can't just call the \"out\" routine, 'cos we */" + LF + "	} else {                                              /* wouldn't get the scheduling point.              */" + LF + "	   delay_ = *delay;" + LF + "	}" + LF + "	" + protectionDomain.getName() + "_Timer_Event_Manager__Setup_Timer( delay_, DYNTRIG_TIMER, " + "CI_" + compInst.getName().toUpperCase() + "_ID, " + compInst.getImplementation().getName().toUpperCase() + "_" + dynTrigInstance.getName().toUpperCase() + "_ID, " + compInst.getImplementation().getName().toUpperCase() + "_" + dynTrigInstance.getName().toUpperCase() + "_OUT_UID, " + "0, (void*)&(context->paramRef[timerID]), &erc );" + LF + LF + "	/* All well? */" + LF + "	if( erc == ECOA__return_status_OK ){" + LF + "		;//printf(\"" + compInst.getName() + "_" + dynTrigInstance.getName() + "__" + evRx.getName() + "__received(): " + "new timerID = %d (%d of " + dynTrigInstance.getSize()
							+ " in use)\\n\", timerID, ++timerCount );" + LF + "	}else{" + LF + "		context->paramRef[timerID] = NULL;" + LF + "		size = sprintf( (char*)msg, \"" + compInst.getName() + "_" + dynTrigInstance.getName() + " failed to allocate a timer.  Error Code = %d\007\\n\", erc );" + LF + "		ecoaLog( msg, size, LOG_LEVEL_ERROR, 0/*modInstId*/ );" + LF + "	}" + LF + "}" + LF + LF;
				}
				if (evRx.getName().equals("reset")) {
					eventOpText += ")" + LF + "{" + LF + "	ECOA__int32 timerID;" + LF + LF + "	/* Delete ALL our timers... */" + LF + "	" + protectionDomain.getName() + "_Timer_Event_Manager__Delete_Timer_ID( DYNTRIG_TIMER, " + "CI_" + compInst.getName().toUpperCase() + "_ID, " + compInst.getImplementation().getName().toUpperCase() + "_" + dynTrigInstance.getName().toUpperCase() + "_ID, " + compInst.getImplementation().getName().toUpperCase() + "_" + dynTrigInstance.getName().toUpperCase() + "_OUT_UID, " + "0 );" + LF + LF + "	for( timerID = 0; timerID < " + dynTrigInstance.getSize() + "; timerID++ )" + LF + "		context->paramRef[timerID] = NULL;" + LF + "}" + LF + LF;
				}
			}
		}
		// Replace the #DYNTRIG# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#DYNTRIG#", eventOpText);
	}

	public void writePostamble() {
		String postambleText = "";
		if (isHeader) {
			postambleText += "#endif" + LF;
		}
		// Replace the #DYNTRIG# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#POSTAMBLE#", postambleText);
	}
}
