package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model;

import java.util.ArrayList;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

public class ComputingNodeConfigurationNode extends Node {
	public static final int DEF_HEIGHT = 50;
	public static final int DEF_WIDTH = 50;
	public static final Color DEF_COLOR = ColorConstants.white;
	public static final Color FONT_COLOR = ColorConstants.black;
	private String name;
	private String schedInfo;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSchedInfo() {
		return schedInfo;
	}

	public void setSchedInfo(String schedInfo) {
		this.schedInfo = schedInfo;
	}

	@Override
	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		if (name == null || name.equalsIgnoreCase("null") || name.trim().length() == 0)
			ret.add("Computing Node Cannot be empty");
		if (schedInfo == null || schedInfo.equalsIgnoreCase("null") || schedInfo.trim().length() == 0)
			ret.add("Scheduling Info Cannot be empty");
		return ret;
	}
}
