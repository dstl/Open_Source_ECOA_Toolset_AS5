package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model;

import java.util.ArrayList;

import org.apache.commons.lang3.math.NumberUtils;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

@SuppressWarnings("deprecation")
public class PlatformConfigurationNode extends Node {
	public static final int DEF_HEIGHT = 100;
	public static final int DEF_WIDTH = 100;
	public static final Color DEF_COLOR = ColorConstants.white;
	public static final Color FONT_COLOR = ColorConstants.black;
	private String compPlatform;
	private String notifMaxNumber;
	private String mcastAddr;
	private String port;
	private String platNum;

	public String getCompPlatform() {
		return compPlatform;
	}

	public void setCompPlatform(String compPlatform) {
		this.compPlatform = compPlatform;
	}

	public String getNotifMaxNumber() {
		return notifMaxNumber;
	}

	public void setNotifMaxNumber(String notifMaxNumber) {
		this.notifMaxNumber = notifMaxNumber;
	}

	public String getMcastAddr() {
		return mcastAddr;
	}

	public void setMcastAddr(String mcastAddr) {
		this.mcastAddr = mcastAddr;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getPlatNum() {
		return platNum;
	}

	public void setPlatNum(String platNum) {
		this.platNum = platNum;
	}

	@Override
	public ArrayList<String> validate() {
		ArrayList<String> ret = new ArrayList<>();
		if (compPlatform == null || compPlatform.equalsIgnoreCase("null") || compPlatform.trim().length() == 0)
			ret.add("Computing Platform Name Cannot be empty");
		if (mcastAddr == null || mcastAddr.equalsIgnoreCase("null") || mcastAddr.trim().length() == 0)
			ret.add("Multicast Address Cannot be empty");
		if (notifMaxNumber == null || notifMaxNumber.equalsIgnoreCase("null") || notifMaxNumber.trim().length() == 0 || (!NumberUtils.isNumber(notifMaxNumber)))
			ret.add("Notification Max Number Cannot be empty or a Non-numeric value");
		if (port == null || port.equalsIgnoreCase("null") || port.trim().length() == 0 || (!NumberUtils.isNumber(port)))
			ret.add("Port Number Cannot be empty or a Non-numeric value");
		if (platNum == null || platNum.equalsIgnoreCase("null") || platNum.trim().length() == 0 || (!NumberUtils.isNumber(platNum)))
			ret.add("Platform Number Cannot be empty or a Non-numeric value");
		return ret;
	}

}
