package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.util.ArrayList;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

public class ModuleImplementationNode extends Node {
	public static final int DEF_HEIGHT = 50;
	public static final int DEF_WIDTH = 50;
	public static final Color DEF_COLOR = ColorConstants.white;
	public static final Color FONT_COLOR = ColorConstants.black;
	private String name;
	private String type;
	private String lang;

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

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		if (name == null || name.equalsIgnoreCase("null") || name.trim().length() == 0)
			ret.add("Implementation Name Cannot be empty");
		if (type == null || type.equalsIgnoreCase("null") || type.trim().length() == 0)
			ret.add("Module Type Cannot be empty");
		if (lang == null || lang.equalsIgnoreCase("null") || lang.trim().length() == 0)
			ret.add("Language Cannot be empty");
		return ret;
	}
}
