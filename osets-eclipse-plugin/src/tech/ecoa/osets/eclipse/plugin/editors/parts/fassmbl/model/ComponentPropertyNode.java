package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model;

import java.util.ArrayList;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

public class ComponentPropertyNode extends Node {
	public static final int DEF_HEIGHT = 50;
	public static final int DEF_WIDTH = 50;
	public static final Color DEF_COLOR = ColorConstants.yellow;
	public static final Color FONT_COLOR = ColorConstants.white;
	private String name;
	private String type;
	private String value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		if (name == null || name.equalsIgnoreCase("null") || name.trim().length() == 0)
			ret.add("Property Name Cannot be empty");
		if (value == null || value.equalsIgnoreCase("null") || value.trim().length() == 0)
			ret.add("Property Value Cannot be empty");
		if (type == null || type.equalsIgnoreCase("null") || type.trim().length() == 0)
			ret.add("Property Type Cannot be empty");
		return ret;
	}

}
