/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class AdaLanguageSupport {
	private static String LF = System.lineSeparator();

	public static String generateIncludes(ArrayList<String> includeList) {
		String includeText = "";

		// Add to hashset to remove duplicate entries.
		HashSet<String> hs = new HashSet<String>();
		hs.addAll(includeList);
		includeList.clear();
		includeList.addAll(hs);

		// Sort alphabetically for neatness (as hashmap screws any ordering up).
		Collections.sort(includeList);

		for (String includeName : includeList) {
			includeText += "with " + includeName + ";" + LF;
		}

		return includeText;
	}
}
