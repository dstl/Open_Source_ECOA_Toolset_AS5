package tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model;

import java.util.ArrayList;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

public class LogicalComputingPlatformNode extends Node {
	public static final int DEF_HEIGHT = 300;
	public static final int DEF_WIDTH = 500;
	public static final Color DEF_COLOR = ColorConstants.white;
	public static final Color FONT_COLOR = ColorConstants.black;
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		if (name == null || name.equalsIgnoreCase("null") || name.trim().length() == 0)
			ret.add("Node Name Cannot be empty");
		return ret;
	}

}
