package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model;

import java.util.ArrayList;

import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

@SuppressWarnings("deprecation")
public class DeployedModuleInstanceNode extends Node {
	public static final int DEF_HEIGHT = 50;
	public static final int DEF_WIDTH = 50;
	public static final Color DEF_COLOR = ColorConstants.white;
	public static final Color FONT_COLOR = ColorConstants.black;
	private String compName;
	private String moduleName;
	private String priority;

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
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
		if (compName == null || compName.equalsIgnoreCase("null") || compName.trim().length() == 0)
			ret.add("Computing Platform Name Cannot be empty");
		if (moduleName == null || moduleName.equalsIgnoreCase("null") || moduleName.trim().length() == 0)
			ret.add("Module Instance Name Cannot be empty");
		if (priority == null || priority.equalsIgnoreCase("null") || priority.trim().length() == 0 || (!NumberUtils.isNumber(priority)))
			ret.add("Priority Cannot be empty or a Non-numeric value");
		return ret;
	}
}
