/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.common.underlyingplatform;

import java.util.ArrayList;

public class C_Posix extends Generic_Platform {
	private static final String SEP_PATTERN_131 = "string";
	private static final String SEP_PATTERN_A = "posix_apos_binding";
	private static final String SEP_PATTERN_B = "      if (";
	private static final String SEP_PATTERN_C = "      }";
	private static final String SEP_PATTERN_D = "stdio";
	private static final String SEP_PATTERN_E = "ecoaLog";
	private static final String SEP_PATTERN_F = "      {";
	private static final String SEP_PATTERN_G = "Private_Context";
	private static final String SEP_PATTERN_H = "stdlib";
	private static final String SEP_PATTERN_I = "   if (status != 0)";
	private static final String SEP_PATTERN_J = "ECOA_time_utils";
	private static final String SEP_PATTERN_K = "Component_Instance_ID";
	private static final String SEP_PATTERN_L = "         ";

	@Override
	public ArrayList<String> addIncludesContainerBody() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_D);
		includes.add(SEP_PATTERN_H);
		includes.add(SEP_PATTERN_131);
		includes.add(SEP_PATTERN_G);
		includes.add("time");
		includes.add("ECOA");
		includes.add(SEP_PATTERN_J);
		includes.add(SEP_PATTERN_E);
		includes.add(SEP_PATTERN_K);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesPDControllerBody() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add("pthread");
		includes.add("prctl");
		includes.add(SEP_PATTERN_A);
		includes.add(SEP_PATTERN_D);
		includes.add("time");
		includes.add(SEP_PATTERN_H);

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
		includes.add(SEP_PATTERN_E);
		includes.add(SEP_PATTERN_131);
		includes.add(SEP_PATTERN_H);
		includes.add(SEP_PATTERN_K);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesELISupport() {
		ArrayList<String> includes = new ArrayList<String>();

		includes.add("ELI_Message");
		includes.add(SEP_PATTERN_E);
		includes.add("fragment");
		includes.add("reassemble");
		includes.add(SEP_PATTERN_A);
		includes.add(SEP_PATTERN_D);
		includes.add(SEP_PATTERN_H);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesInternalRouterBody() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_D);
		includes.add(SEP_PATTERN_H);
		includes.add("ECOA");
		includes.add(SEP_PATTERN_J);
		includes.add(SEP_PATTERN_K);
		includes.add("ILI_Message");
		includes.add(SEP_PATTERN_A);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesInternalRouterHeader() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_G);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesModInstCont() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_D);
		includes.add(SEP_PATTERN_H);
		includes.add(SEP_PATTERN_E);
		includes.add(SEP_PATTERN_G);
		includes.add("message_queue");
		includes.add(SEP_PATTERN_K);
		includes.add(SEP_PATTERN_A);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesServiceAPIBody() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_H);
		includes.add(SEP_PATTERN_131);
		includes.add(SEP_PATTERN_D);
		includes.add("time");
		includes.add("ECOA");
		includes.add(SEP_PATTERN_J);
		includes.add(SEP_PATTERN_K);
		includes.add("ILI_Message");

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesServiceAPIHeader() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_G);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesTimerEventHandlerBody() {
		ArrayList<String> includes = new ArrayList<String>();

		includes.add(SEP_PATTERN_J);
		includes.add(SEP_PATTERN_A);
		includes.add(SEP_PATTERN_D);

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesTimerEventManagerBody() {
		ArrayList<String> includes = new ArrayList<String>();

		includes.add(SEP_PATTERN_A);

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
		includes.add(SEP_PATTERN_D);
		includes.add(SEP_PATTERN_K);
		includes.add("time");
		includes.add(SEP_PATTERN_E);
		includes.add("message_queue");

		return includes;
	}

	@Override
	public ArrayList<String> addIncludesVDRepo() {
		ArrayList<String> includes = new ArrayList<String>();
		includes.add(SEP_PATTERN_D);
		includes.add(SEP_PATTERN_A);
		includes.add("ECOA");
		includes.add(SEP_PATTERN_K);

		return includes;
	}

	@Override
	public String checkCreateSemphoreStatus(String semaphoreID) {
		// TODO - for now generate the same calls as MAI_APOS...
		C_MAI_APOS maiAPOS = new C_MAI_APOS();
		return maiAPOS.checkCreateSemphoreStatus(semaphoreID);
	}

	@Override
	public String checkGALTStatusCALL_ONLY() {
		return "   {" + LF;
	}

	@Override
	public String checkPostSemaphoreStatus(String semaphoreID) {
		// TODO - for now generate the same calls as MAI_APOS...
		C_MAI_APOS maiAPOS = new C_MAI_APOS();
		return maiAPOS.checkPostSemaphoreStatus(semaphoreID);
	}

	@Override
	public String checkTriggerInFuture(String delayVarName, String nextTimeVarName, String timeNowVarName) {
		return SEP_PATTERN_B + nextTimeVarName + ".tv_sec >= " + timeNowVarName + ".tv_sec + carry)" + LF + SEP_PATTERN_F + LF + SEP_PATTERN_L + delayVarName + ".tv_sec = " + nextTimeVarName + ".tv_sec - carry - " + timeNowVarName + ".tv_sec;" + LF;
	}

	@Override
	public String checkWaitForSemaphoreStatusCALL_ONLY() {
		// TODO - for now generate the same calls as MAI_APOS...
		C_MAI_APOS maiAPOS = new C_MAI_APOS();
		return maiAPOS.checkWaitForSemaphoreStatusCALL_ONLY();
	}

	@Override
	public String determineDelayTimeForTrigger(String delayVarName, String nextTimeVarName, String timeNowVarName) {
		return SEP_PATTERN_B + nextTimeVarName + ".tv_nsec >= " + timeNowVarName + ".tv_nsec)" + LF + SEP_PATTERN_F + LF + SEP_PATTERN_L + delayVarName + ".tv_nsec = " + nextTimeVarName + ".tv_nsec - " + timeNowVarName + ".tv_nsec;" + LF + "         carry = 0;" + LF + SEP_PATTERN_C + LF + "      else" + LF + SEP_PATTERN_F + LF + SEP_PATTERN_L + delayVarName + ".tv_nsec = (" + nextTimeVarName + ".tv_nsec + 1000000000) - " + timeNowVarName + ".tv_nsec;" + LF + "         carry = 1;" + LF + SEP_PATTERN_C + LF;
	}

	@Override
	public String determineNextTimeToTrigger(String nextTimeVarName) {
		return "      " + nextTimeVarName + ".tv_sec += timeout.tv_sec;" + LF + "      " + nextTimeVarName + ".tv_nsec = " + nextTimeVarName + ".tv_nsec + timeout.tv_nsec;" + LF + SEP_PATTERN_B + nextTimeVarName + ".tv_nsec >= 1000000000)" + LF + SEP_PATTERN_F + LF + SEP_PATTERN_L + nextTimeVarName + ".tv_nsec = " + nextTimeVarName + ".tv_nsec - 1000000000;" + LF + SEP_PATTERN_L + nextTimeVarName + ".tv_sec++;" + LF + SEP_PATTERN_C + LF;
	}

	@Override
	public String generateCreateSemaphoreAttributes() {
		// TODO - for now generate the same calls as MAI_APOS...
		C_MAI_APOS maiAPOS = new C_MAI_APOS();
		return maiAPOS.generateCreateSemaphoreAttributes();
	}

	@Override
	public String generateCreateSemphore(int initVal, int maxVal, String semaphoreID) {
		// TODO - for now generate the same calls as MAI_APOS...
		C_MAI_APOS maiAPOS = new C_MAI_APOS();
		return maiAPOS.generateCreateSemphore(initVal, maxVal, semaphoreID);
	}

	@Override
	public String generateGALT() {
		return "   clock_gettime(CLOCK_REALTIME, &time);" + LF;
	}

	@Override
	public String generateGALT(String timeVarName) {
		return "   clock_gettime(CLOCK_REALTIME, &" + timeVarName + ");" + LF;
	}

	@Override
	public String generateGALTAttributes() {
		return "   struct timespec time;" + LF;
	}

	@Override
	public String generateGALTStatusAttribute() {
		// NOT REQUIRED FOR POSIX.
		return "";
	}

	@Override
	public String generateGALTTimeAttribute(String timeVarName) {
		return "   struct timespec " + timeVarName + ";" + LF;
	}

	@Override
	public String generatePostSemaphore(String semaphoreID) {
		// TODO - for now generate the same calls as MAI_APOS...
		C_MAI_APOS maiAPOS = new C_MAI_APOS();
		return maiAPOS.generatePostSemaphore(semaphoreID);
	}

	@Override
	public String generatePostSemaphoreAttributes() {
		// TODO - for now generate the same calls as MAI_APOS...
		C_MAI_APOS maiAPOS = new C_MAI_APOS();
		return maiAPOS.generatePostSemaphoreAttributes();
	}

	@Override
	public String generateReceiveMessageCall() {
		// TODO - for now generate the same calls as MAI_APOS... (will use an
		// APOS shim)
		C_MAI_APOS maiAPOS = new C_MAI_APOS();
		return maiAPOS.generateReceiveMessageCall();
	}

	@Override
	public String generateReceiveMessageVars(String id) {
		// TODO - for now generate the same calls as MAI_APOS... (will use an
		// APOS shim)
		C_MAI_APOS maiAPOS = new C_MAI_APOS();
		return maiAPOS.generateReceiveMessageVars(id);
	}

	@Override
	public String generateRMCheckStatusOK() {
		// TODO - for now generate the same calls as MAI_APOS... (will use an
		// APOS shim)
		C_MAI_APOS maiAPOS = new C_MAI_APOS();
		return maiAPOS.generateRMCheckStatusOK();
	}

	@Override
	public String generateSendMessageNonBlockCall(String paddingString) {
		// TODO - for now generate the same calls as MAI_APOS... (will use an
		// APOS shim)
		C_MAI_APOS maiAPOS = new C_MAI_APOS();
		return maiAPOS.generateSendMessageNonBlockCall(paddingString);
	}

	@Override
	public String generateSendMessageNonBlockVars() {
		// TODO - for now generate the same calls as MAI_APOS... (will use an
		// APOS shim)
		C_MAI_APOS maiAPOS = new C_MAI_APOS();
		return maiAPOS.generateSendMessageNonBlockVars();
	}

	@Override
	public String generateSetOwnPriority(String padding, String priority) {
		String ownThread = padding + "   int status;" + LF + padding + "   pthread_t thread;" + LF + padding + "   struct sched_param param;" + LF + LF +

				padding + "   param.sched_priority = Scale_ThreadPriority(" + priority + ", SCHED_FIFO);" + LF + padding + "   thread = pthread_self();" + LF + padding + "   status = pthread_setschedparam(thread, SCHED_FIFO, &param);" + LF + padding + SEP_PATTERN_I + LF + padding + "      printf(\"ERROR - (SELF) pthread_setschedparam\\n\");" + LF;

		return ownThread;
	}

	@Override
	public String generateSleepAttributes(String secs, String nano) {
		return "   timeout.tv_sec = " + secs + ";" + LF + "   timeout.tv_nsec = " + nano + ";" + LF;
	}

	@Override
	public String generateSleepCall() {
		return "   nanosleep(&timeout, &rem);" + LF;
	}

	@Override
	public String generateSleepCall(String sleepVarName) {
		return "   nanosleep(&" + sleepVarName + ", &rem);" + LF;
	}

	@Override
	public String generateSleepTimeAttribute(String sleepVarName) {
		return "   struct timespec " + sleepVarName + ";" + LF;
	}

	@Override
	public String generateSleepVariableDecls() {
		return "   struct timespec timeout, rem;" + LF;
	}

	@Override
	public String generateThreadAttributes(String padding, String priority) {
		String threadAttr = padding + "   status = pthread_attr_init(&attr);" + LF + padding + SEP_PATTERN_I + LF + padding + "      printf(\"ERROR - pthread_attr_init\\n\");" + LF + LF +

				padding + "   status = pthread_attr_setstacksize(&attr, (size_t)2000000);" + LF + padding + SEP_PATTERN_I + LF + padding + "      printf(\"ERROR - pthread_attr_setstacksize\\n\");" + LF + LF +

				padding + "   status = pthread_attr_setschedpolicy(&attr, SCHED_FIFO);" + LF + padding + SEP_PATTERN_I + LF + padding + "      printf(\"ERROR - pthread_attr_setschedpolicy\\n\");" + LF + LF +

				padding + "   status = pthread_attr_setinheritsched(&attr, PTHREAD_EXPLICIT_SCHED);" + LF + padding + SEP_PATTERN_I + LF + padding + "      printf(\"ERROR - pthread_attr_setinheritsched\\n\");" + LF + LF +

				padding + "   param.sched_priority = Scale_ThreadPriority(" + priority + ", SCHED_FIFO);" + LF + padding + "   status = pthread_attr_setschedparam(&attr, &param);" + LF + padding + SEP_PATTERN_I + LF + padding + "      printf(\"ERROR - pthread_attr_setschedparam\\n\");" + LF;

		return threadAttr;
	}

	@Override
	public String generateThreadCreate(String padding, String entryPoint) {
		String createThread = padding + "   status = pthread_create(&thread, &attr, (void *(*)(void *))" + entryPoint + ", arg);" + LF + padding + SEP_PATTERN_I + LF + padding + "      printf(\"ERROR - pthread_create\\n\");" + LF + LF +

				"#if defined(__linux__)" + LF + padding + "   prctl(PR_SET_NAME, threadName, 0, 0, 0);" + LF + "#endif" + LF + LF +

				padding + "   status = pthread_attr_destroy(&attr);" + LF + padding + SEP_PATTERN_I + LF + padding + "      printf(\"ERROR - pthread_attr_destroy\\n\");" + LF;

		return createThread;
	}

	@Override
	public String generateThreadVariableDecls(String padding) {
		return padding + "   int status, policy;" + LF + padding + "   pthread_t thread;" + LF + padding + "   pthread_attr_t attr;" + LF + padding + "   struct sched_param param;" + LF;
	}

	@Override
	public String generateTimeoutVars() {
		// TODO - for now generate the same calls as MAI_APOS... (will use an
		// APOS shim)
		C_MAI_APOS maiAPOS = new C_MAI_APOS();
		return maiAPOS.generateTimeoutVars();
	}

	@Override
	public String generateWaitForSemaphore(String semaphoreID) {
		// TODO - for now generate the same calls as MAI_APOS...
		C_MAI_APOS maiAPOS = new C_MAI_APOS();
		return maiAPOS.generateWaitForSemaphore(semaphoreID);
	}

	@Override
	public String generateWaitForSemaphoreAttributes() {
		// TODO - for now generate the same calls as MAI_APOS...
		C_MAI_APOS maiAPOS = new C_MAI_APOS();
		return maiAPOS.generateWaitForSemaphoreAttributes();
	}

	@Override
	public String getELIReceiverPriority() {
		return "50";
	}

	@Override
	public String getTimeNanoVar() {
		return "time.tv_nsec";
	}

	@Override
	public String getTimeSecondsVar() {
		return "time.tv_sec";
	}

	@Override
	public String getECOALogTemplate(boolean isHeader) {
		if (isHeader) {
			return "ecoaLog.h";
		} else {
			return "ecoaLog_posix.c";
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
			return "message_queue_posix.h";
		} else {
			return "message_queue_posix.c";
		}
	}

	@Override
	public String getTimeUtilsTemplate(boolean isHeader) {
		if (isHeader) {
			return "ECOA_time_utils.h";
		} else {
			return "ECOA_time_utils_posix.c";
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
		includes.add("time");
		includes.add(SEP_PATTERN_H);
		includes.add("pthread");
		includes.add("prctl");
		includes.add(SEP_PATTERN_D);
		includes.add(SEP_PATTERN_A);

		return includes;
	}

}
