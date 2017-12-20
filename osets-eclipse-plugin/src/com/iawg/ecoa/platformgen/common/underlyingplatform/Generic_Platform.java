/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.common.underlyingplatform;

import java.util.ArrayList;

public abstract class Generic_Platform {
	protected String LF = System.lineSeparator();

	public abstract ArrayList<String> addIncludesContainerBody();

	public abstract ArrayList<String> addIncludesPDControllerBody();

	public abstract ArrayList<String> addIncludesPDControllerHeader();

	public abstract ArrayList<String> addIncludesELIIn();

	public abstract ArrayList<String> addIncludesELISupport();

	public abstract ArrayList<String> addIncludesInternalRouterBody();

	public abstract ArrayList<String> addIncludesInternalRouterHeader();

	public abstract ArrayList<String> addIncludesModInstCont();

	public abstract ArrayList<String> addIncludesServiceAPIBody();

	public abstract ArrayList<String> addIncludesServiceAPIHeader();

	public abstract ArrayList<String> addIncludesTimerEventHandlerBody();

	public abstract ArrayList<String> addIncludesTimerEventManagerBody();

	public abstract ArrayList<String> addIncludesTimerEventManagerHeader();

	public abstract ArrayList<String> addIncludesTrigInstCont();

	public abstract ArrayList<String> addIncludesVDRepo();

	public abstract ArrayList<String> addIncludesPFControllerHeader();

	public abstract ArrayList<String> addIncludesPFControllerBody();

	public abstract String checkCreateSemphoreStatus(String semaphoreID);

	public abstract String checkGALTStatusCALL_ONLY();

	public abstract String checkPostSemaphoreStatus(String semaphoreID);

	public abstract String checkTriggerInFuture(String delayVarName, String nextTimeVarName, String timeNowVarName);

	public abstract String checkWaitForSemaphoreStatusCALL_ONLY();

	public abstract String determineDelayTimeForTrigger(String delayVarName, String nextTimeVarName, String timeNowVarName);

	public abstract String determineNextTimeToTrigger(String nextTimeVarName);

	public abstract String generateCreateSemaphoreAttributes();

	public abstract String generateCreateSemphore(int initVal, int maxVal, String semaphoreID);

	public abstract String generateGALT();

	public abstract String generateGALT(String timeVarName);

	public abstract String generateGALTAttributes();

	public abstract String generateGALTStatusAttribute();

	public abstract String generateGALTTimeAttribute(String timeVarName);

	public abstract String generatePostSemaphore(String semaphoreID);

	public abstract String generatePostSemaphoreAttributes();

	public abstract String generateReceiveMessageCall();

	public abstract String generateReceiveMessageVars(String id);

	public abstract String generateRMCheckStatusOK();

	public abstract String generateSendMessageNonBlockCall(String paddingString);

	public abstract String generateSendMessageNonBlockVars();

	public abstract String generateSetOwnPriority(String padding, String priority);

	public abstract String generateSleepAttributes(String secs, String nano);

	public abstract String generateSleepCall();

	public abstract String generateSleepCall(String sleepVarName);

	public abstract String generateSleepTimeAttribute(String sleepVarName);

	public abstract String generateSleepVariableDecls();

	public abstract String generateThreadAttributes(String padding, String priority);

	public abstract String generateThreadCreate(String padding, String entryPoint);

	public abstract String generateThreadVariableDecls(String padding);

	public abstract String generateTimeoutVars();

	public abstract String generateWaitForSemaphore(String semaphoreID);

	public abstract String generateWaitForSemaphoreAttributes();

	public abstract String getELIReceiverPriority();

	public abstract String getTimeNanoVar();

	public abstract String getTimeSecondsVar();

	public abstract String getECOALogTemplate(boolean isHeader);

	public abstract String getFileHandlerTemplate(boolean isHeader);

	public abstract String getMessageQueueTemplate(boolean isHeader);

	public abstract String getTimeUtilsTemplate(boolean isHeader);

}
