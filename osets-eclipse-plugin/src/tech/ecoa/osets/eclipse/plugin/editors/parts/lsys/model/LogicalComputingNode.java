package tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model;

import java.util.ArrayList;

import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

@SuppressWarnings("deprecation")
public class LogicalComputingNode extends Node {
	public static final int DEF_HEIGHT = 150;
	public static final int DEF_WIDTH = 300;
	public static final Color DEF_COLOR = ColorConstants.white;
	public static final Color FONT_COLOR = ColorConstants.black;
	private String name;
	private String endianess;
	private String osName;
	private String osVer;
	private String availMem;
	private String mst;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEndianess() {
		return endianess;
	}

	public void setEndianess(String endianess) {
		this.endianess = endianess;
	}

	public String getOsName() {
		return osName;
	}

	public void setOsName(String osName) {
		this.osName = osName;
	}

	public String getOsVer() {
		return osVer;
	}

	public void setOsVer(String osVer) {
		this.osVer = osVer;
	}

	public String getAvailMem() {
		return availMem;
	}

	public void setAvailMem(String availMem) {
		this.availMem = availMem;
	}

	public String getMst() {
		return mst;
	}

	public void setMst(String mst) {
		this.mst = mst;
	}

	@Override
	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		if (name == null || name.equalsIgnoreCase("null") || name.trim().length() == 0)
			ret.add("Node Name Cannot be empty");
		if (endianess == null || endianess.equalsIgnoreCase("null") || endianess.trim().length() == 0)
			ret.add("Endianess Cannot be empty");
		if (osName == null || osName.equalsIgnoreCase("null") || osName.trim().length() == 0)
			ret.add("OS Name Cannot be empty");
		if (osVer == null || osVer.equalsIgnoreCase("null") || osVer.trim().length() == 0)
			ret.add("OS Version Cannot be empty");
		if (availMem == null || availMem.equalsIgnoreCase("null") || availMem.trim().length() == 0 || (!NumberUtils.isNumber(availMem)))
			ret.add("Available Memory Cannot be empty or a Non-numeric value");
		if (mst == null || mst.equalsIgnoreCase("null") || mst.trim().length() == 0 || (!NumberUtils.isNumber(mst)))
			ret.add("Module Switch Time Cannot be empty or a Non-numeric value");
		return ret;
	}
}
