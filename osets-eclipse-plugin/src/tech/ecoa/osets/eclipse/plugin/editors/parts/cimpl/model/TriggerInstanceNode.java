package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.util.ArrayList;

import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

@SuppressWarnings("deprecation")
public class TriggerInstanceNode extends Node {
	public static final int DEF_HEIGHT = 150;
	public static final int DEF_WIDTH = 150;
	public static final Color DEF_COLOR = ColorConstants.lightBlue;
	public static final Color FONT_COLOR = ColorConstants.black;
	private String name;
	private String priority;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	@Override
	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		if (name == null || name.equalsIgnoreCase("null") || name.trim().length() == 0)
			ret.add("Trigger Instance Name Cannot be empty");
		if (priority == null || priority.equalsIgnoreCase("null") || priority.trim().length() == 0 || (!NumberUtils.isNumber(priority)))
			ret.add("Trigger Instance Priority Cannot be empty or a non numeric value");
		return ret;
	}
}
