package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model;

import java.util.ArrayList;

import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

@SuppressWarnings("deprecation")
public class DeployedTriggerInstanceNode extends Node {
	public static final int DEF_HEIGHT = 50;
	public static final int DEF_WIDTH = 50;
	public static final Color DEF_COLOR = ColorConstants.white;
	public static final Color FONT_COLOR = ColorConstants.black;
	private String compName;
	private String triggerName;
	private String priority;

	public String getCompName() {
		return compName;
	}

	public void setCompName(String compName) {
		this.compName = compName;
	}

	public String getTriggerName() {
		return triggerName;
	}

	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
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
		if (triggerName == null || triggerName.equalsIgnoreCase("null") || triggerName.trim().length() == 0)
			ret.add("Trigger Instance Name Cannot be empty");
		if (priority == null || priority.equalsIgnoreCase("null") || priority.trim().length() == 0 || (!NumberUtils.isNumber(priority)))
			ret.add("Priority Cannot be empty or a Non-numeric value");
		return ret;
	}
}
