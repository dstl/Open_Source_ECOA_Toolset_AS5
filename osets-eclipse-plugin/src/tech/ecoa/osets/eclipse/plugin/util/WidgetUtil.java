/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.util;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

public class WidgetUtil {
	public boolean isAdded(Composite composite, Control ctrl, int level) {
		Control[] subs = composite.getChildren();
		for (Control sub : subs) {
			if (sub.getClass().getName().equalsIgnoreCase(Composite.class.getName())) {
				Control[] comps = ((Composite) sub).getChildren();
				for (Control comp : comps) {
					if (comp.getClass().getName().equalsIgnoreCase(Button.class.getName())) {
						Button btn = (Button) comp;
						if (StringUtils.contains(btn.getText(), "ype"))
							return true;
					}
				}
			}
		}
		return false;
	}

}
