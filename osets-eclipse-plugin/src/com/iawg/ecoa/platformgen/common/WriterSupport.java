/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.common;

public final class WriterSupport {

	public static void replaceText(StringBuilder sb, String replaceWhat, String replaceWith) {
		int startIndex = sb.indexOf(replaceWhat);
		int endIndex = startIndex + replaceWhat.length();

		if (startIndex >= 0 && endIndex >= 1) {
			sb.replace(startIndex, endIndex, replaceWith);
		}
	}

	// Suppress default constructor for noninstantiability
	private WriterSupport() {
		throw new AssertionError();
	}

}
