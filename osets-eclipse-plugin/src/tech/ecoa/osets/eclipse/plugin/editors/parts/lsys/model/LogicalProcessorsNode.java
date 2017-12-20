package tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model;

import java.util.ArrayList;

import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

@SuppressWarnings("deprecation")
public class LogicalProcessorsNode extends Node {
	public static final int DEF_HEIGHT = 50;
	public static final int DEF_WIDTH = 50;
	public static final Color DEF_COLOR = ColorConstants.white;
	public static final Color FONT_COLOR = ColorConstants.black;
	private String num;
	private String type;
	private String stepDur;

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStepDur() {
		return stepDur;
	}

	public void setStepDur(String stepDur) {
		this.stepDur = stepDur;
	}

	@Override
	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		if (type == null || type.equalsIgnoreCase("null") || type.trim().length() == 0)
			ret.add("Processor Type Cannot be empty");
		if (num == null || num.equalsIgnoreCase("null") || num.trim().length() == 0 || (!NumberUtils.isNumber(num)))
			ret.add("Processor Number Cannot be empty or a Non-numeric value");
		if (stepDur == null || stepDur.equalsIgnoreCase("null") || stepDur.trim().length() == 0 || (!NumberUtils.isNumber(stepDur)))
			ret.add("Step Duration Cannot be empty or a Non-numeric value");
		return ret;
	}
}
