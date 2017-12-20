package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model;

import java.util.ArrayList;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

public class ProtectionDomainNode extends Node {
	public static final int DEF_HEIGHT = 150;
	public static final int DEF_WIDTH = 300;
	public static final Color DEF_COLOR = ColorConstants.white;
	public static final Color FONT_COLOR = ColorConstants.black;
	private String name;
	private String eoCompNode;
	private String eoCompPlatform;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEoCompNode() {
		return eoCompNode;
	}

	public void setEoCompNode(String eoCompNode) {
		this.eoCompNode = eoCompNode;
	}

	public String getEoCompPlatform() {
		return eoCompPlatform;
	}

	public void setEoCompPlatform(String eoCompPlatform) {
		this.eoCompPlatform = eoCompPlatform;
	}

	@Override
	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		if (name == null || name.equalsIgnoreCase("null") || name.trim().length() == 0)
			ret.add("Name Cannot be empty");
		if (eoCompNode == null || eoCompNode.equalsIgnoreCase("null") || eoCompNode.trim().length() == 0)
			ret.add("Execute On Platform Node Cannot be empty");
		if (eoCompPlatform == null || eoCompPlatform.equalsIgnoreCase("null") || eoCompPlatform.trim().length() == 0)
			ret.add("Execute On Platform Name Cannot be empty");
		return ret;
	}
}
