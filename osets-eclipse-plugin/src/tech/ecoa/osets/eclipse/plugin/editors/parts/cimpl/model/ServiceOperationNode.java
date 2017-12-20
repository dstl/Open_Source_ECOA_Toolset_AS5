package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

public class ServiceOperationNode extends Node {
	public static final int DEF_HEIGHT = 50;
	public static final int DEF_WIDTH = 100;
	public static final Color DEF_COLOR = ColorConstants.white;
	public static final Color FONT_COLOR = ColorConstants.black;
	private String name;
	private String type;
	private String dir;

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

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}
}
