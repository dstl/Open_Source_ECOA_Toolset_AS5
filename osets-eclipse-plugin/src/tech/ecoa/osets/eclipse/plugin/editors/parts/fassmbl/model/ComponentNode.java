package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model;

import java.util.ArrayList;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

public class ComponentNode extends Node {
	public static final int DEF_HEIGHT = 150;
	public static final int DEF_WIDTH = 300;
	public static final Color DEF_COLOR = ColorConstants.cyan;
	public static final Color FONT_COLOR = ColorConstants.black;
	private String name;
	private String type;
	private String inst;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInst() {
		return inst;
	}

	public void setInst(String inst) {
		this.inst = inst;
	}

	@Override
	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		if (name == null || name.equalsIgnoreCase("null") || name.trim().length() == 0)
			ret.add("Component Name Cannot be empty");
		if (type == null || type.equalsIgnoreCase("null") || type.trim().length() == 0)
			ret.add("Component Type Cannot be empty");
		if (inst == null || inst.equalsIgnoreCase("null") || inst.trim().length() == 0)
			ret.add("Component Instance Cannot be empty");
		return ret;
	}
}
