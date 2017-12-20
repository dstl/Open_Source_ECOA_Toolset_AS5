package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.util.ArrayList;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

public class ModuleTypeNode extends Node {
	public static final int DEF_HEIGHT = 300;
	public static final int DEF_WIDTH = 600;
	public static final Color DEF_COLOR = ColorConstants.white;
	public static final Color FONT_COLOR = ColorConstants.black;
	private String name;
	private boolean sup;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isSup() {
		return sup;
	}

	public void setSup(boolean sup) {
		this.sup = sup;
	}

	@Override
	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		if (name == null || name.equalsIgnoreCase("null") || name.trim().length() == 0)
			ret.add("Type Name Cannot be empty");
		return ret;
	}
}
