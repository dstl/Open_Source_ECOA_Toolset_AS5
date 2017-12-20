/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.common.underlyingplatform;

import java.util.ArrayList;

public class C_MAI_APOS extends Generic_Platform {
	private static final String SEP_PATTERN_131 = "string";
	private static final String SEP_PATTERN_A = "      }";
	private static final String SEP_PATTERN_B = "stdio";
	private static final String SEP_PATTERN_C = "ecoaLog";
	private static final String SEP_PATTERN_D = "      {";
	private static final String SEP_PATTERN_E = "Private_Context";
	private static final String SEP_PATTERN_F = ".Nanoseconds = ";
	private static final String SEP_PATTERN_G = "stdlib";
	private static final String SEP_PATTERN_H = "osl004_apos_types";
	private static final String SEP_PATTERN_I = "ECOA_time_utils";
	private static final String SEP_PATTERN_J = "Component_Instance_ID";
	private static final String SEP_PATTERN_K = "         ";
	private static final String SEP_PATTERN_L = "      ";
	private static final String SEP_PATTERN_M = "osl004_apos";

	@Override
	public ArrayList<String> addIncludesContainerBody() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_B);
		includes.add(SEP_PATTERN_G);
		includes.add(SEP_PATTERN_131);
		includes.add(SEP_PATTERN_E);
		includes.add(SEP_PATTERN_M);
		includes.add(SEP_PATTERN_H);
		includes.add("ECOA");
		includes.add(SEP_PATTERN_I);
		includes.add(SEP_PATTERN_C);
		includes.add(SEP_PATTERN_J);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesPDControllerBody() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_M);
		includes.add(SEP_PATTERN_H);
		includes.add(SEP_PATTERN_B);
		includes.add(SEP_PATTERN_G);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesPDControllerHeader() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add("ECOA");

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesELIIn() {
		ArrayList<String> includes = new ArrayList<String>();

		includes.add("ECOA");
		includes.add("ecoaByteswap");
		includes.add("ELI_Message");
		includes.add(SEP_PATTERN_C);
		includes.add(SEP_PATTERN_131);
		includes.add(SEP_PATTERN_G);
		includes.add(SEP_PATTERN_J);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesELISupport() {
		ArrayList<String> includes = new ArrayList<String>();

		includes.add("ELI_Message");
		includes.add(SEP_PATTERN_C);
		includes.add("fragment");
		includes.add("reassemble");
		includes.add(SEP_PATTERN_B);
		includes.add(SEP_PATTERN_G);

		includes.add(SEP_PATTERN_H);
		includes.add(SEP_PATTERN_M);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesInternalRouterBody() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_B);
		includes.add(SEP_PATTERN_G);
		includes.add("ECOA");
		includes.add(SEP_PATTERN_I);
		includes.add(SEP_PATTERN_J);
		includes.add("ILI_Message");
		includes.add(SEP_PATTERN_M);
		includes.add(SEP_PATTERN_H);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesInternalRouterHeader() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_E);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesModInstCont() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_B);
		includes.add(SEP_PATTERN_G);
		includes.add(SEP_PATTERN_C);
		includes.add(SEP_PATTERN_E);
		includes.add("message_queue");
		includes.add(SEP_PATTERN_J);
		includes.add(SEP_PATTERN_M);
		includes.add(SEP_PATTERN_H);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesServiceAPIBody() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_G);
		includes.add(SEP_PATTERN_131);
		includes.add(SEP_PATTERN_B);
		includes.add(SEP_PATTERN_M);
		includes.add(SEP_PATTERN_H);
		includes.add("ECOA");
		includes.add(SEP_PATTERN_I);
		includes.add(SEP_PATTERN_J);
		includes.add("ILI_Message");

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesServiceAPIHeader() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_E);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesTimerEventHandlerBody() {
		ArrayList<String> includes = new ArrayList<String>();

		includes.add(SEP_PATTERN_I);
		includes.add(SEP_PATTERN_H);
		includes.add(SEP_PATTERN_M);
		includes.add(SEP_PATTERN_B);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesTimerEventManagerBody() {
		ArrayList<String> includes = new ArrayList<String>();

		includes.add(SEP_PATTERN_H);
		includes.add(SEP_PATTERN_M);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesTimerEventManagerHeader() {
		ArrayList<String> includes = new ArrayList<String>();

		includes.add("ECOA");

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesTrigInstCont() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_B);
		includes.add(SEP_PATTERN_J);
		includes.add(SEP_PATTERN_M);
		includes.add(SEP_PATTERN_H);
		includes.add(SEP_PATTERN_C);
		includes.add("message_queue");

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesVDRepo() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_B);
		includes.add(SEP_PATTERN_M);
		includes.add(SEP_PATTERN_H);
		includes.add("ECOA");
		includes.add(SEP_PATTERN_J);

		return includes;
	}

	@Override
	public String checkCreateSemphoreStatus(String semaphoreID) {
		return "   if (Create_Semaphore_Status != Create_Semaphore_OK)" + LF + "   {" + LF + "      printf(\"ERROR - creating " + semaphoreID + "\\n\");" + LF + "   }" + LF + LF;
	}

	@Override
	public String checkGALTStatusCALL_ONLY() {
		return "   if (timeStatus == Time_OK)" + LF + "   {" + LF;
	}

	@Override
	public String checkPostSemaphoreStatus(String semaphoreID) {
		return "   if (Post_Semaphore_Status != Post_Semaphore_OK)" + LF + SEP_PATTERN_D + LF + "         printf(\"ERROR - giving " + semaphoreID + "\\n\");" + LF + SEP_PATTERN_A + LF;
	}

	@Override
	public String checkTriggerInFuture(String delayVarName, String nextTimeVarName, String timeNowVarName) {
		return "      if ((" + nextTimeVarName + ".Seconds - carry) >= " + timeNowVarName + ".Seconds)" + LF + SEP_PATTERN_D + LF + SEP_PATTERN_K + delayVarName + ".Seconds = " + nextTimeVarName + ".Seconds - carry - " + timeNowVarName + ".Seconds;" + LF;
	}

	@Override
	public String checkWaitForSemaphoreStatusCALL_ONLY() {
		return "   if(Wait_For_Semaphore_Status == Wait_For_Semaphore_OK)" + LF;
	}

	@Override
	public String determineDelayTimeForTrigger(String delayVarName, String nextTimeVarName, String timeNowVarName) {
		return "      if (" + nextTimeVarName + ".Nanoseconds >= " + timeNowVarName + ".Nanoseconds)" + LF + SEP_PATTERN_D + LF + SEP_PATTERN_K + delayVarName + SEP_PATTERN_F + nextTimeVarName + ".Nanoseconds - " + timeNowVarName + ".Nanoseconds;" + LF + "         carry = 0;" + LF + SEP_PATTERN_A + LF + "      else" + LF + SEP_PATTERN_D + LF + SEP_PATTERN_K + delayVarName + ".Nanoseconds = (" + nextTimeVarName + ".Nanoseconds + 1000000000) - " + timeNowVarName + ".Nanoseconds;" + LF + "         carry = 1;" + LF + SEP_PATTERN_A + LF;
	}

	@Override
	public String determineNextTimeToTrigger(String nextTimeVarName) {
		return SEP_PATTERN_L + nextTimeVarName + ".Seconds += S_Timeout.Seconds;" + LF + SEP_PATTERN_L + nextTimeVarName + SEP_PATTERN_F + nextTimeVarName + ".Nanoseconds + S_Timeout.Nanoseconds;" + LF + "      if (" + nextTimeVarName + ".Nanoseconds >= 1000000000)" + LF + SEP_PATTERN_D + LF + SEP_PATTERN_K + nextTimeVarName + SEP_PATTERN_F + nextTimeVarName + ".Nanoseconds - 1000000000;" + LF + SEP_PATTERN_K + nextTimeVarName + ".Seconds++;" + LF + SEP_PATTERN_A + LF;
	}

	@Override
	public String generateCreateSemaphoreAttributes() {
		return "   Create_Semaphore_Status_Type Create_Semaphore_Status;" + LF + LF;
	}

	@Override
	public String generateCreateSemphore(int initVal, int maxVal, String semaphoreID) {
		return "   Create_Semaphore(" + initVal + "," + LF + SEP_PATTERN_L + maxVal + "," + LF + "      Queuing_Discipline_FIFO," + LF + "      &" + semaphoreID + "," + LF + "      &Create_Semaphore_Status);" + LF + LF;
	}

	@Override
	public String generateGALT() {
		return "   Get_Absolute_Local_Time(&aposTime, &timeStatus);" + LF + LF;
	}

	@Override
	public String generateGALT(String timeVarName) {
		return "   Get_Absolute_Local_Time(&" + timeVarName + ", &timeStatus);" + LF + LF;
	}

	@Override
	public String generateGALTAttributes() {
		return "   Time_Status_Type timeStatus;" + LF + "   Time_Type aposTime;" + LF + LF;
	}

	@Override
	public String generateGALTStatusAttribute() {
		return "   Time_Status_Type timeStatus;" + LF;
	}

	@Override
	public String generateGALTTimeAttribute(String timeVarName) {
		return "   Time_Type " + timeVarName + ";" + LF;
	}

	@Override
	public String generatePostSemaphore(String semaphoreID) {
		return "   Post_Semaphore(" + semaphoreID + "," + LF + "      &Post_Semaphore_Status);" + LF + LF;
	}

	@Override
	public String generatePostSemaphoreAttributes() {
		return "   Post_Semaphore_Status_Type Post_Semaphore_Status;" + LF + LF;
	}

	@Override
	public String generateReceiveMessageCall() {
		return "      Receive_Message(VC_ID," + LF + "                      &Rx_Message," + LF + "                      65507," + LF + "                      Timeout," + LF + "                      &Msg_Len_Avail," + LF + "                      &RM_Status);" + LF;
	}

	@Override
	public String generateReceiveMessageVars(String id) {
		return "   int VC_ID = " + id + ";" + LF + "   RM_Status_Type RM_Status;" + LF;
	}

	@Override
	public String generateRMCheckStatusOK() {
		return "      if (RM_Status == Blocking_Message_Received_OK)" + LF;
	}

	@Override
	public String generateSendMessageNonBlockCall(String paddingString) {
		return paddingString + "Send_Message_Non_Blocking(VC_ID," + LF + paddingString + "   Fragments[i].fragment," + LF + paddingString + "   Fragments[i].sizeOfFragment," + LF + paddingString + "   &SMNB_Status);" + LF + LF;
	}

	@Override
	public String generateSendMessageNonBlockVars() {
		return "   int VC_ID;" + LF + "   SMNB_Status_Type SMNB_Status;" + LF;
	}

	@Override
	public String generateSetOwnPriority(String padding, String priority) {
		// Not required for IMS
		String ownThread = "";

		return ownThread;
	}

	@Override
	public String generateSleepAttributes(String secs, String nano) {
		return "   S_Timeout.Seconds = " + secs + ";" + LF + "   S_Timeout.Nanoseconds = " + nano + ";" + LF + LF;
	}

	@Override
	public String generateSleepCall() {
		return "Sleep(&S_Timeout, &S_Status);" + LF + LF +

				"   if (S_Status != Sleep_Call_OK)" + LF + "   {" + LF + "      printf(\"Sleep failed - %d\\n\", S_Status);" + LF + "   }" + LF + LF;

	}

	@Override
	public String generateSleepCall(String sleepVarName) {
		return "Sleep(&" + sleepVarName + ", &S_Status);" + LF + LF +

				"   if (S_Status != Sleep_Call_OK)" + LF + "   {" + LF + "      printf(\"Sleep failed - %d\\n\", S_Status);" + LF + "   }" + LF + LF;
	}

	@Override
	public String generateSleepTimeAttribute(String sleepVarName) {
		return "   Timeout_Type " + sleepVarName + ";" + LF;
	}

	@Override
	public String generateSleepVariableDecls() {
		return "   Timeout_Type S_Timeout;" + LF + "   Sleep_Status_Type S_Status;" + LF + LF;
	}

	@Override
	public String generateThreadAttributes(String padding, String priority) {
		return padding + "threadAttrs.Sched_Policy = Sched_FIFO;" + LF + padding + "threadAttrs.Sched_Priority = " + priority + ";" + LF + padding + "threadAttrs.Stack_Size = 1000000;" + LF;
	}

	@Override
	public String generateThreadCreate(String padding, String entryPoint) {
		return padding + "Create_Thread(&threadAttrs," + LF + padding + entryPoint + "," + LF + padding + "arg," + LF + padding + "&Thread_ID," + LF + padding + "&CT_Status);" + LF + LF +

				padding + "if (CT_Status != Create_Thread_Call_OK)" + LF + padding + "{" + LF + padding + "   printf(\"ERROR - create thread failed - status = %d\\n\", CT_Status);" + LF + padding + "}" + LF;
	}

	@Override
	public String generateThreadVariableDecls(String padding) {
		return padding + "Create_Thread_Status_Type CT_Status;" + LF + padding + "int Thread_ID;" + LF + padding + "Thread_Attr_Type threadAttrs;" + LF;
	}

	@Override
	public String generateTimeoutVars() {
		return "   Time_Type Timeout;" + LF + "   Timeout.Seconds = MAX_SECONDS;" + LF + "   Timeout.Nanoseconds = MAX_NANOSECONDS;" + LF;
	}

	@Override
	public String generateWaitForSemaphore(String semaphoreID) {
		return "   Wait_For_Semaphore(" + semaphoreID + "," + LF + "      &WFS_Timeout," + LF + "      &Wait_For_Semaphore_Status);" + LF + LF;
	}

	@Override
	public String generateWaitForSemaphoreAttributes() {
		return "   Wait_For_Semaphore_Status_Type Wait_For_Semaphore_Status;" + LF + "   Time_Type WFS_Timeout = {MAX_SECONDS, MAX_NANOSECONDS};" + LF + LF;
	}

	@Override
	public String getELIReceiverPriority() {
		return "255";
	}

	@Override
	public String getTimeNanoVar() {
		return "aposTime.Nanoseconds";
	}

	@Override
	public String getTimeSecondsVar() {
		return "aposTime.Seconds";
	}

	@Override
	public String getECOALogTemplate(boolean isHeader) {
		if (isHeader) {
			return "ecoaLog.h";
		} else {
			return "ecoaLog_mai_apos.c";
		}
	}

	@Override
	public String getFileHandlerTemplate(boolean isHeader) {
		if (isHeader) {
			return "ECOA_file_handler.h";
		} else {
			return "ECOA_file_handler.c";
		}
	}

	@Override
	public String getMessageQueueTemplate(boolean isHeader) {
		if (isHeader) {
			return "message_queue_mai_apos.h";
		} else {
			return "message_queue_mai_apos.c";
		}
	}

	@Override
	public String getTimeUtilsTemplate(boolean isHeader) {
		if (isHeader) {
			return "ECOA_time_utils.h";
		} else {
			return "ECOA_time_utils_mai_apos.c";
		}
	}

	@Override
	public ArrayList<String> addIncludesPFControllerHeader() {
		ArrayList<String> includes = new ArrayList<String>();

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesPFControllerBody() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_M);
		includes.add(SEP_PATTERN_H);
		includes.add(SEP_PATTERN_B);
		includes.add(SEP_PATTERN_G);

		return includes;
	}

}
