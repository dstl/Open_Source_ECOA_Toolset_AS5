/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.common;

import javax.xml.namespace.QName;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;

public class Constants {
	public static final Font DEF_FONT = new Font(null, "Consolas", 10, SWT.NORMAL);
	public static final String ROW_SEP = "#";
	public static final String DEF_SEP = "^";
	public static final String FLD_SEP = "&";
	public static final String VAL_SEP = ",";
	public static final String LNK_SEP = ":";
	public static final String BP_SEP = "$";
	public final static QName TYPE_QNAME = new QName("http://www.ecoa.technology/sca", "type");
	public final static QName XS_QNAME = new QName("http://www.w3.org/2001/XMLSchema", "string");
}
