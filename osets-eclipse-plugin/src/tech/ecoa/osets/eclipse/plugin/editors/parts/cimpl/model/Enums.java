package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

public class Enums {
	public enum ServiceTypes {
		PROVIDED, REQUIRED
	}

	public enum ServiceOperationTypes {
		DATA, EVENT, REQ_RES
	}

	public enum LinkTypes {
		DATA, EVENT, REQUEST
	}

	public enum ModuleOperationTypes {
		DATA_READ, DATA_WRITE, EVENT_SENT, EVENT_RECEIVED, REQUEST_SENT, REQUEST_RECEIVED
	}

	public enum ParameterTypes {
		INPUT, OUTPUT
	}

	public enum DynamicTriggerInstanceTerminalTypes {
		IN, OUT, RESET
	}

	public static ServiceTypes getServiceType(String type) {
		return ServiceTypes.valueOf(type);
	}

	public static ServiceOperationTypes getServiceOperationType(String type) {
		return ServiceOperationTypes.valueOf(type);
	}

	public static LinkTypes getLinkType(String type) {
		return LinkTypes.valueOf(type);
	}

	public static ModuleOperationTypes getModuleOperationType(String type) {
		return ModuleOperationTypes.valueOf(type);
	}

	public static ParameterTypes getParameterType(String type) {
		return ParameterTypes.valueOf(type);
	}

	public static DynamicTriggerInstanceTerminalTypes getDynamicTriggerInstanceTerminalType(String type) {
		return DynamicTriggerInstanceTerminalTypes.valueOf(type);
	}

	public static String[] getServiceTypes() {
		String ret[] = new String[ServiceTypes.values().length + 1];
		ret[0] = "";
		int i = 1;
		for (ServiceTypes type : ServiceTypes.values()) {
			ret[i] = type.name();
			i++;
		}
		return ret;
	}

	public static String[] getServiceOperationTypes() {
		String ret[] = new String[ServiceOperationTypes.values().length + 1];
		ret[0] = "";
		int i = 1;
		for (ServiceOperationTypes type : ServiceOperationTypes.values()) {
			ret[i] = type.name();
			i++;
		}
		return ret;
	}

	public static String[] getModuleOperationTypes() {
		String ret[] = new String[ModuleOperationTypes.values().length + 1];
		ret[0] = "";
		int i = 1;
		for (ModuleOperationTypes type : ModuleOperationTypes.values()) {
			ret[i] = type.name();
			i++;
		}
		return ret;
	}

	public static String[] getLinkTypes() {
		String ret[] = new String[LinkTypes.values().length + 1];
		ret[0] = "";
		int i = 1;
		for (LinkTypes type : LinkTypes.values()) {
			ret[i] = type.name();
			i++;
		}
		return ret;
	}

	public static String[] getParameterTypes() {
		String ret[] = new String[ParameterTypes.values().length + 1];
		ret[0] = "";
		int i = 1;
		for (ParameterTypes type : ParameterTypes.values()) {
			ret[i] = type.name();
			i++;
		}
		return ret;
	}

	public static String[] getDynamicTriggerInstanceTerminalTypes() {
		String ret[] = new String[DynamicTriggerInstanceTerminalTypes.values().length + 1];
		ret[0] = "";
		int i = 1;
		for (DynamicTriggerInstanceTerminalTypes type : DynamicTriggerInstanceTerminalTypes.values()) {
			ret[i] = type.name();
			i++;
		}
		return ret;
	}
}
