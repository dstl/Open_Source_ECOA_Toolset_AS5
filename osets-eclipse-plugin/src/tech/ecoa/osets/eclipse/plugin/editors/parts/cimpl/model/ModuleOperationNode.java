package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.util.ArrayList;

import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

@SuppressWarnings("deprecation")
public class ModuleOperationNode extends Node {
	public static final int DEF_HEIGHT = 100;
	public static final int DEF_WIDTH = 150;
	public static final Color DEF_COLOR = ColorConstants.white;
	public static final Color FONT_COLOR = ColorConstants.black;
	private String type;
	private String name;
	private boolean sync;
	private String timeout;
	private String dType;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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
			ret.add("Operation Name Cannot be empty");
		if (type == null || type.equalsIgnoreCase("null") || type.trim().length() == 0)
			ret.add("Operation Type Cannot be empty");
		if (getType().equalsIgnoreCase(Enums.ModuleOperationTypes.REQUEST_SENT.name())) {
			if (timeout == null || timeout.equalsIgnoreCase("null") || timeout.trim().length() == 0 || !NumberUtils.isNumber(timeout))
				ret.add("Timeout Cannot be empty");
		}
		if (getType().equalsIgnoreCase(Enums.ModuleOperationTypes.DATA_READ.name()) || getType().equalsIgnoreCase(Enums.ModuleOperationTypes.DATA_WRITE.name())) {
			if (dType == null || dType.equalsIgnoreCase("null") || dType.trim().length() == 0)
				ret.add("Data Type Cannot be empty");
		}
		return ret;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public boolean isSync() {
		return sync;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
	}

	public String getdType() {
		return dType;
	}

	public void setdType(String dType) {
		this.dType = dType;
	}
}
