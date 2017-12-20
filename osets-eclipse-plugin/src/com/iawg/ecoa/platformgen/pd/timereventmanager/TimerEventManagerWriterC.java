/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.timereventmanager;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.common.underlyingplatform.Generic_Platform;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class TimerEventManagerWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_361 = "void ";
	private boolean isHeader;
	private SM_ProtectionDomain protectionDomain;
	private Generic_Platform underlyingPlatform;
	private String timerManName;
	private ArrayList<String> includeList = new ArrayList<String>();

	public TimerEventManagerWriterC(PlatformGenerator platformGenerator, boolean isHeader, Path outputDir, SM_ProtectionDomain protectionDomain) {
		super(outputDir);
		this.isHeader = isHeader;
		this.protectionDomain = protectionDomain;
		this.underlyingPlatform = platformGenerator.getunderlyingPlatformInstantiation();

		this.timerManName = this.protectionDomain.getName() + "_Timer_Event_Manager";
		setFileStructure();
	}

	@Override
	public void open() {
		if (isHeader) {
			super.openFile(outputDir.resolve(timerManName + ".h"));
		} else {
			super.openFile(outputDir.resolve(timerManName + ".c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#TYPE_DEFS#" + LF + "#SETUP_TIMER#" + LF + "#SETUP_REQUESTQOS_TIMER#" + LF + "#DELETE_TIMER#" + LF + "#DELETE_TIMER_WITHLOCK#" + LF + "#GET_TIMER#" + LF + "#DELETE_TIMER_ID#" + LF + "#DELETE_REQUESTQOS_TIMER_ID#" + LF + "#INITIALISE#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#TYPE_DEFS#" + LF + "#SETUP_TIMER#" + LF + "#SETUP_REQUESTQOS_TIMER#" + LF + "#DELETE_TIMER#" + LF + "#DELETE_TIMER_WITHLOCK#" + LF + "#GET_TIMER#" + LF + "#DELETE_TIMER_ID#" + LF + "#DELETE_REQUESTQOS_TIMER_ID#" + LF + "#INITIALISE#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeDeleteRequestQoSTimerID() {
		String dtText = "";

		if (isHeader) {
			dtText += "Timer_Manager_Error_Type " + timerManName + "__Delete_RequestQoS_Timer_ID(ECOA__uint32 serviceOpUID, ECOA__uint32 seqNum);" + LF;
		} else {
			// Generate the Delete Timer function.
			dtText += "Timer_Manager_Error_Type " + timerManName + "__Delete_RequestQoS_Timer_ID(ECOA__uint32 serviceOpUID, ECOA__uint32 seqNum)" + LF + "{" + LF + "   int i = 0;" + LF + "   Timer_Manager_Error_Type dtidStatus = Timer_Manager_Error_Type_FAILED_TO_FIND_TIMER;" + LF + "   ECOA__return_status dtStatus;" + LF + LF +

					"   Wait_For_Semaphore_Status_Type Wait_For_Semaphore_Status;" + LF + "   Post_Semaphore_Status_Type Post_Semaphore_Status;" + LF + "   Time_Type Timeout = {MAX_SECONDS, MAX_NANOSECONDS};" + LF + LF +

					"   Wait_For_Semaphore(Timer_Event_Manager_Access_Semaphore," + LF + "      &Timeout," + LF + "      &Wait_For_Semaphore_Status);" + LF + LF +

					"   // Get the associated information for the specified timer id" + LF + "   for (i = 0; i < " + timerManName + "_MAX_TIMER_SIZE; i++)" + LF + "   {" + LF + "      if (" + timerManName + "_Timer_Lookup_List[i].timerType == REQUEST_QOS_TIMEOUT &&" + LF + "          " + timerManName + "_Timer_Lookup_List[i].serviceOpUID == serviceOpUID &&" + LF + "          " + timerManName + "_Timer_Lookup_List[i].seqNum == seqNum)" + LF + "      {" + LF + "         /* Delete the timer */" + LF + "         " + timerManName + "__Delete_Timer(" + timerManName + "_Timer_Lookup_List[i].timerID, &dtStatus);" + LF + LF +

					"         // Default the timer info so it can be reused." + LF + "         " + timerManName + "_Timer_Lookup_List[i].timerID = -1;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].timerType = REQUEST_TIMEOUT;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].serviceOpUID = 0;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].seqNum = 0;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].timerArgs = (void*)0;" + LF + "         dtidStatus = Timer_Manager_Error_Type_OK;" + LF + "         break;" + LF + "      }" + LF + "   }" + LF + LF +

					"   Post_Semaphore(Timer_Event_Manager_Access_Semaphore," + LF + "      &Post_Semaphore_Status);" + LF + LF +

					"   return dtidStatus;" + LF + "}" + LF;
		}

		// Replace the #DELETE_REQUESTQOS_TIMER_ID# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#DELETE_REQUESTQOS_TIMER_ID#", dtText);
	}

	public void writeDeleteTimer() {
		String dtText = "";

		// Do not provide this function on the header, as it should only be
		// called from Get_Timer().
		if (!isHeader) {
			// Generate the Delete Timer function.
			dtText += "static void " + timerManName + "__Delete_Timer(int aposTimerID, ECOA__return_status *error)" + LF + "{" + LF + LF + "   Stop_Timer_Status_Type stStatus;" + LF + "   Delete_Timer_Status_Type dtStatus;" + LF + LF +

					"   Stop_Timer(aposTimerID, &stStatus);" + LF + "   switch( stStatus ){" + LF + "      case Stop_Timer_OK              : *error = ECOA__return_status_OK; break;" + LF + "      case Stop_Timer_Already_Stopped : *error = ECOA__return_status_INVALID_IDENTIFIER; break;" + LF + "      case Stop_Timer_Invalid_Timer   : *error = ECOA__return_status_INVALID_IDENTIFIER; break;" + LF + "      case Stop_Timer_Failed          : *error = ECOA__return_status_OK; break;" + LF + "      case Invalid_Stop_Timer_Param   : *error = ECOA__return_status_INVALID_PARAMETER; break;" + LF + "   }" + LF + LF +

					"   Delete_Timer(aposTimerID, &dtStatus);" + LF + "   switch( dtStatus ){" + LF + "      case Delete_Timer_OK            : *error = ECOA__return_status_OK; break;" + LF + "      case Delete_Timer_Still_Running : *error = ECOA__return_status_OPERATION_NOT_AVAILABLE; break;" + LF + "      case Delete_Timer_Invalid_Timer : *error = ECOA__return_status_INVALID_IDENTIFIER; break;" + LF + "      case Invalid_Delete_Timer_Param : *error = ECOA__return_status_INVALID_PARAMETER; break;" + LF + "   }" + LF + LF +

					"}" + LF;
		}

		// Replace the #DELETE_TIMER# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#DELETE_TIMER#", dtText);
	}

	public void writeDeleteTimerWithLock() {
		String dtText = "";

		dtText += SEP_PATTERN_361 + timerManName + "__Delete_Timer_ByID(int aposTimerID, ECOA__return_status *error)";
		if (isHeader) {
			dtText += ";" + LF + LF;
		} else {
			// Generate the Delete Timer function.
			dtText += LF + "{" + LF + "   Wait_For_Semaphore_Status_Type Wait_For_Semaphore_Status;" + LF + "   Post_Semaphore_Status_Type Post_Semaphore_Status;" + LF + "   Delete_Timer_Status_Type dtStatus;" + LF + "   Stop_Timer_Status_Type stStatus;" + LF + "   Time_Type Timeout = {MAX_SECONDS, MAX_NANOSECONDS};" + LF + "   int timerID;" + LF + LF +

					"   Wait_For_Semaphore(Timer_Event_Manager_Access_Semaphore," + LF + "      &Timeout," + LF + "      &Wait_For_Semaphore_Status);" + LF + LF +

					"   Stop_Timer(aposTimerID, &stStatus);" + LF + LF + "   switch( stStatus ){" + LF + "      case Stop_Timer_OK              : *error = ECOA__return_status_OK; break;" + LF + "      case Stop_Timer_Already_Stopped : *error = ECOA__return_status_INVALID_IDENTIFIER; break;" + LF + "      case Stop_Timer_Invalid_Timer   : *error = ECOA__return_status_INVALID_IDENTIFIER; break;" + LF + "      case Stop_Timer_Failed          : *error = ECOA__return_status_OK; break;" + LF + "      case Invalid_Stop_Timer_Param   : *error = ECOA__return_status_INVALID_PARAMETER; break;" + LF + "   }" + LF + LF +

					"   Delete_Timer(aposTimerID, &dtStatus);" + LF + LF + "   switch( dtStatus ){" + LF + "      case Delete_Timer_OK            : *error = ECOA__return_status_OK; break;" + LF + "      case Delete_Timer_Still_Running : *error = ECOA__return_status_OPERATION_NOT_AVAILABLE; break;" + LF + "      case Delete_Timer_Invalid_Timer : *error = ECOA__return_status_INVALID_IDENTIFIER; break;" + LF + "      case Invalid_Delete_Timer_Param : *error = ECOA__return_status_INVALID_PARAMETER; break;" + LF + "   }" + LF + LF +

					"   // Default the timer info so it can be reused." + LF + "   for( timerID = 0; timerID < " + timerManName + "_MAX_TIMER_SIZE; timerID++ ){" + LF + "      if(" + timerManName + "_Timer_Lookup_List[timerID].timerID == aposTimerID){" + LF + "         " + timerManName + "_Timer_Lookup_List[timerID].timerID = -1;" + LF + "         " + timerManName + "_Timer_Lookup_List[timerID].timerType = REQUEST_TIMEOUT;" + LF + "         " + timerManName + "_Timer_Lookup_List[timerID].compInstID = 0;" + LF + "         " + timerManName + "_Timer_Lookup_List[timerID].modInstID = 0;" + LF + "         " + timerManName + "_Timer_Lookup_List[timerID].operationID = 0;" + LF + "         " + timerManName + "_Timer_Lookup_List[timerID].seqNum = 0;" + LF + "         " + timerManName + "_Timer_Lookup_List[timerID].timerArgs = (void*)0;" + LF + LF + "//       printf( \"Deleted timer %d\\n\", timerID);" + LF + // TODO
					"      }" + LF + "   }" + LF +

					"   Post_Semaphore(Timer_Event_Manager_Access_Semaphore," + LF + "      &Post_Semaphore_Status);" + LF + LF +

					"}" + LF + LF;
		}

		// Replace the #DELETE_TIMER# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#DELETE_TIMER_WITHLOCK#", dtText);
	}

	public void writeDeleteTimerID() {
		String dtText = "";

		if (isHeader) {
			dtText += "Timer_Manager_Error_Type " + timerManName + "__Delete_Timer_ID(Timer_Type type, ECOA__uint32 compInstID, ECOA__uint32 modInstID, ECOA__uint32 operationID, ECOA__uint32 seqNum);" + LF + LF;
		} else {
			// Generate the Delete Timer function.
			dtText += "Timer_Manager_Error_Type " + timerManName + "__Delete_Timer_ID(Timer_Type type, ECOA__uint32 compInstID, ECOA__uint32 modInstID, ECOA__uint32 operationID, ECOA__uint32 seqNum)" + LF + "{" + LF + "   int i = 0;" + LF + "   Timer_Manager_Error_Type dtidStatus = Timer_Manager_Error_Type_FAILED_TO_FIND_TIMER;" + LF + "   ECOA__return_status dtStatus;" + LF + LF +

					"   Wait_For_Semaphore_Status_Type Wait_For_Semaphore_Status;" + LF + "   Post_Semaphore_Status_Type Post_Semaphore_Status;" + LF + "   Time_Type Timeout = {MAX_SECONDS, MAX_NANOSECONDS};" + LF + LF +

					"   Wait_For_Semaphore(Timer_Event_Manager_Access_Semaphore," + LF + "      &Timeout," + LF + "      &Wait_For_Semaphore_Status);" + LF + LF +

					// For request-response and QoS timeouts there will be only
					// one match.
					// For dynamic triggers there might be multiple matches
					"   /* Delete all matching timers */" + LF + "   for (i = 0; i < " + timerManName + "_MAX_TIMER_SIZE; i++)" + LF + "   {" + LF + "      if (" + timerManName + "_Timer_Lookup_List[i].timerType == type &&" + LF + "            " + timerManName + "_Timer_Lookup_List[i].compInstID == compInstID &&" + LF + "            " + timerManName + "_Timer_Lookup_List[i].modInstID == modInstID &&" + LF + "            " + timerManName + "_Timer_Lookup_List[i].operationID == operationID &&" + LF + "            " + timerManName + "_Timer_Lookup_List[i].seqNum == seqNum)" + LF + "      {" + LF + "         " + timerManName + "__Delete_Timer(" + timerManName + "_Timer_Lookup_List[i].timerID, &dtStatus);" + LF + LF +

					"         // Default the timer info so it can be reused." + LF + "         " + timerManName + "_Timer_Lookup_List[i].timerID = -1;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].timerType = REQUEST_TIMEOUT;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].compInstID = 0;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].modInstID = 0;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].operationID = 0;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].seqNum = 0;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].timerArgs = (void*)0;" + LF + "         dtidStatus = Timer_Manager_Error_Type_OK;" + LF + "//       printf( \"Deleted timer %d\\n\", i);" + LF + // TODO
					"         if( type != DYNTRIG_TIMER ) break;" + LF + "      }" + LF + "   }" + LF + LF +

					"   Post_Semaphore(Timer_Event_Manager_Access_Semaphore," + LF + "      &Post_Semaphore_Status);" + LF + LF +

					"   return dtidStatus;" + LF + "}" + LF;
		}

		// Replace the #DELETE_TIMER_ID# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#DELETE_TIMER_ID#", dtText);
	}

	public void writeGetTimer() {
		String gtText = "";

		if (isHeader) {
			gtText += "Timer_Manager_Error_Type " + timerManName + "__Get_Timer(int timerID, Timer_Type *type, ECOA__uint32 *compInstID, ECOA__uint32 *modInstID, ECOA__uint32 *operationID, ECOA__uint32 *serviceOpUID, ECOA__uint32 *seqNum, void** timerArgs);" + LF + LF;
		} else {
			// Generate the Get Timer function.
			gtText += "Timer_Manager_Error_Type " + timerManName + "__Get_Timer(int timerID, Timer_Type *type, ECOA__uint32 *compInstID, ECOA__uint32 *modInstID, ECOA__uint32 *operationID, ECOA__uint32 *serviceOpUID, ECOA__uint32 *seqNum, void** timerArgs)" + LF + "{" + LF + "   int i = 0;" + LF + "   Timer_Manager_Error_Type gtStatus = Timer_Manager_Error_Type_FAILED_TO_FIND_TIMER;" + LF + "   ECOA__return_status dtStatus;" + LF + LF +

					"   Wait_For_Semaphore_Status_Type Wait_For_Semaphore_Status;" + LF + "   Post_Semaphore_Status_Type Post_Semaphore_Status;" + LF + "   Time_Type Timeout = {MAX_SECONDS, MAX_NANOSECONDS};" + LF + LF +

					"   Wait_For_Semaphore(Timer_Event_Manager_Access_Semaphore," + LF + "      &Timeout," + LF + "      &Wait_For_Semaphore_Status);" + LF + LF +

					"   // Get the associated information for the specified timer id" + LF + "   for (i = 0; i < " + timerManName + "_MAX_TIMER_SIZE; i++)" + LF + "   {" + LF + "      if (" + timerManName + "_Timer_Lookup_List[i].timerID == timerID)" + LF + "      {" + LF + "         /* Set the output params */" + LF + "         *type = " + timerManName + "_Timer_Lookup_List[i].timerType;" + LF + "         *compInstID = " + timerManName + "_Timer_Lookup_List[i].compInstID;" + LF + "         *modInstID = " + timerManName + "_Timer_Lookup_List[i].modInstID;" + LF + "         *operationID = " + timerManName + "_Timer_Lookup_List[i].operationID;" + LF + "         *serviceOpUID = " + timerManName + "_Timer_Lookup_List[i].serviceOpUID;" + LF + "         *seqNum = " + timerManName + "_Timer_Lookup_List[i].seqNum;" + LF + "         *timerArgs = " + timerManName + "_Timer_Lookup_List[i].timerArgs;" + LF + "         gtStatus = Timer_Manager_Error_Type_OK;" + LF + "         break;" + LF + "      }" + LF + "   }" + LF + LF +

					"   Post_Semaphore(Timer_Event_Manager_Access_Semaphore," + LF + "      &Post_Semaphore_Status);" + LF + LF +

					"   return gtStatus;" + LF + "}" + LF;
		}

		// Replace the #GET_TIMER# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GET_TIMER#", gtText);
	}

	public void writeIncludes() {
		if (isHeader) {
			includeList.addAll(underlyingPlatform.addIncludesTimerEventManagerHeader());
		} else {
			includeList.add(timerManName);
			includeList.addAll(underlyingPlatform.addIncludesTimerEventManagerBody());
			includeList.add("stdio");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeInitialise(Generic_Platform underlyingPlatformInstantiation) {
		String initialiseText = "";

		if (isHeader) {
			initialiseText += SEP_PATTERN_361 + timerManName + "__Initialise();" + LF + LF;
		} else {
			// Generate the Initialise function.
			initialiseText += SEP_PATTERN_361 + timerManName + "__Initialise()" + LF + "{" + LF + "   int i;" + LF + LF +

			// Create a semaphore for managing access to the timer event manager
			// data
					"   /* Create a semaphore for managing access to the timer event manager data */" + LF + "   Create_Semaphore_Status_Type Create_Semaphore_Status;" + LF + "   Create_Semaphore(1," + LF + "      1," + LF + "      Queuing_Discipline_FIFO," + LF + "      &Timer_Event_Manager_Access_Semaphore," + LF + "      &Create_Semaphore_Status);" + LF + LF +

					"   if (Create_Semaphore_Status != Create_Semaphore_OK)" + LF + "   {" + LF + "      printf(\"ERROR creating timer event manager access semaphore\\n\");" + LF + "   }" + LF + LF +

					"   /* Initialize the timer list */" + LF + "   for (i = 0; i < " + timerManName + "_MAX_TIMER_SIZE; i++)" + LF + "   {" + LF + "      " + timerManName + "_Timer_Lookup_List[i].timerID = -1;" + LF + "      " + timerManName + "_Timer_Lookup_List[i].timerType = REQUEST_TIMEOUT;" + LF + "      " + timerManName + "_Timer_Lookup_List[i].compInstID = 0;" + LF + "      " + timerManName + "_Timer_Lookup_List[i].modInstID = 0;" + LF + "      " + timerManName + "_Timer_Lookup_List[i].operationID = 0;" + LF + "      " + timerManName + "_Timer_Lookup_List[i].serviceOpUID = 0;" + LF + "      " + timerManName + "_Timer_Lookup_List[i].seqNum = 0;" + LF + "      " + timerManName + "_Timer_Lookup_List[i].timerArgs = (void*)0;" + LF + "   }" + LF + "}" + LF;
		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + timerManName + ".h */" + LF + "/* This is the timer event manager functionality */" + LF;
		} else {
			preambleText += "/* File " + timerManName + ".c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeSetupRequestQoSTimer() {
		String stText = "";

		if (isHeader) {
			stText += SEP_PATTERN_361 + timerManName + "__Setup_RequestQoS_Timer(ECOA__duration time, Timer_Type type, ECOA__uint32 serviceOpUID, ECOA__uint32 seqNum, ECOA__return_status *error);" + LF + LF;
		} else {
			// Generate the Setup Timer function.
			stText += SEP_PATTERN_361 + timerManName + "__Setup_RequestQoS_Timer(ECOA__duration time, Timer_Type type, ECOA__uint32 serviceOpUID, ECOA__uint32 seqNum, ECOA__return_status *error)" + LF + "{" + LF + LF + "   Time_Type aposTime;" + LF + "   aposTime.Seconds = time.seconds;" + LF + "   aposTime.Nanoseconds = time.nanoseconds;" + LF + "   int timerID;" + LF + "   int i = 0;" + LF + LF +

					"   Wait_For_Semaphore_Status_Type Wait_For_Semaphore_Status;" + LF + "   Post_Semaphore_Status_Type Post_Semaphore_Status;" + LF + "   Time_Type Timeout = {MAX_SECONDS, MAX_NANOSECONDS};" + LF + LF +

					"   Create_Timer_Status_Type ctStatus;" + LF + "   Start_Timer_Status_Type stStatus;" + LF + LF +

					"   Wait_For_Semaphore(Timer_Event_Manager_Access_Semaphore," + LF + "      &Timeout," + LF + "      &Wait_For_Semaphore_Status);" + LF + LF +

					// This must be inside the semaphore protection too, as it
					// allocates a timerID...
					"   Create_Timer(aposTime, &timerID, &ctStatus);" + LF +

					"   // Store timer id, type, seqnum and id mapping" + LF + "   for (i = 0; i < " + timerManName + "_MAX_TIMER_SIZE; i++)" + LF + "   {" + LF + "      if (" + timerManName + "_Timer_Lookup_List[i].timerID == -1)" + LF + "      {" + LF + "         /* Store in the lookup list */" + LF + "         " + timerManName + "_Timer_Lookup_List[i].timerID = timerID;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].timerType = type;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].serviceOpUID = serviceOpUID;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].seqNum = seqNum;" + LF + "         break;" + LF + "      }" + LF + "   }" + LF + LF +

					"   Post_Semaphore(Timer_Event_Manager_Access_Semaphore," + LF + "      &Post_Semaphore_Status);" + LF + LF +

					"   Start_Timer(timerID, &stStatus);" + LF + "}" + LF + LF;
		}

		// Replace the #SETUP_REQUESTQOS_TIMER# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SETUP_REQUESTQOS_TIMER#", stText);
	}

	public void writeSetupRequestTimer() {
		String stText = "";

		if (isHeader) {
			stText += SEP_PATTERN_361 + timerManName + "__Setup_Timer(ECOA__duration time, Timer_Type type, ECOA__uint32 compInstID, ECOA__uint32 modInstID, ECOA__uint32 operationID, ECOA__uint32 seqNum, void* timerArgs, ECOA__return_status *error);" + LF + LF;
		} else {
			// Generate the Setup Timer function.
			stText += SEP_PATTERN_361 + timerManName + "__Setup_Timer(ECOA__duration time, Timer_Type type, ECOA__uint32 compInstID, ECOA__uint32 modInstID, ECOA__uint32 operationID, ECOA__uint32 seqNum, void* timerArgs, ECOA__return_status *error)" + LF + "{" + LF + LF + "   Time_Type aposTime;" + LF + "   aposTime.Seconds = time.seconds;" + LF + "   aposTime.Nanoseconds = time.nanoseconds;" + LF + "   int timerID;" + LF + "   int i = 0;" + LF + LF +

					"   Wait_For_Semaphore_Status_Type Wait_For_Semaphore_Status;" + LF + "   Post_Semaphore_Status_Type Post_Semaphore_Status;" + LF + "   Time_Type Timeout = {MAX_SECONDS, MAX_NANOSECONDS};" + LF + LF +

					"   Create_Timer_Status_Type ctStatus;" + LF + "   Start_Timer_Status_Type stStatus;" + LF + LF +

					"   Wait_For_Semaphore(Timer_Event_Manager_Access_Semaphore," + LF + "      &Timeout," + LF + "      &Wait_For_Semaphore_Status);" + LF + LF +

					// This must be inside the semaphore protection too, as it
					// allocates a timerID...
					"   Create_Timer(aposTime, &timerID, &ctStatus);" + LF +

					"   // Store timer id, type, seqnum and id mapping" + LF + "   for (i = 0; i < " + timerManName + "_MAX_TIMER_SIZE; i++)" + LF + "   {" + LF + "      if (" + timerManName + "_Timer_Lookup_List[i].timerID == -1)" + LF + "      {" + LF + "         /* Store in the lookup list */" + LF + "         " + timerManName + "_Timer_Lookup_List[i].timerID = timerID;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].timerType = type;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].compInstID = compInstID;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].modInstID = modInstID;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].operationID = operationID;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].seqNum = seqNum;" + LF + "         " + timerManName + "_Timer_Lookup_List[i].timerArgs = timerArgs;" + LF + "         break;" + LF + "      }" + LF + "   }" + LF + LF +

					"   Post_Semaphore(Timer_Event_Manager_Access_Semaphore," + LF + "      &Post_Semaphore_Status);" + LF + LF +

					"   Start_Timer(timerID, &stStatus);" + LF + "}" + LF + LF;
		}

		// Replace the #SETUP_TIMER# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SETUP_TIMER#", stText);
	}

	public void writeTypeDefs() {
		String typeDefsText = "";

		if (isHeader) {
			typeDefsText += "typedef enum" + LF + "{" + LF + "   REQUEST_TIMEOUT," + LF + "   REQUEST_QOS_TIMEOUT," + LF + "   DYNTRIG_TIMER" + LF + "} Timer_Type;" + LF + LF +

					"typedef enum" + LF + "{" + LF + "   Timer_Manager_Error_Type_OK," + LF + "   Timer_Manager_Error_Type_FAILED_TO_FIND_TIMER" + LF + "} Timer_Manager_Error_Type;" + LF + LF;
		} else {
			typeDefsText += "typedef struct" + LF + "{" + LF + "   int timerID;" + LF + "   Timer_Type timerType;" + LF + "   ECOA__uint32 compInstID;" + LF + "   ECOA__uint32 modInstID;" + LF + "   ECOA__uint32 operationID;" + LF + "   ECOA__uint32 serviceOpUID;" + LF + "   ECOA__uint32 seqNum;" + LF + "   void* timerArgs;" + LF + "} timerLookupType;" + LF + LF +

					"#define " + timerManName + "_MAX_TIMER_SIZE 100" + LF + "typedef timerLookupType " + timerManName + "_Timer_Lookup_List_Type[" + timerManName + "_MAX_TIMER_SIZE];" + LF + LF +

					"static " + timerManName + "_Timer_Lookup_List_Type " + timerManName + "_Timer_Lookup_List = {{-1,(Timer_Type)0,0,0,0,0,0}};" + LF + LF +

					"static int Timer_Event_Manager_Access_Semaphore;" + LF + LF;
		}

		// Replace the #TYPE_DEFS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#TYPE_DEFS#", typeDefsText);
	}

}
