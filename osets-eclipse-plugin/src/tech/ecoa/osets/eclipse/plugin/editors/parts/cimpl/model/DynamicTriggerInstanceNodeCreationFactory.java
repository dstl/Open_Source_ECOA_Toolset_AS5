package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class DynamicTriggerInstanceNodeCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		DynamicTriggerInstanceNode node = new DynamicTriggerInstanceNode();
		node.setId(UUID.randomUUID().toString());
		DynamicTriggerInstanceTerminalNode inNode = new DynamicTriggerInstanceTerminalNode();
		inNode.setName("IN");
		inNode.setType(Enums.DynamicTriggerInstanceTerminalTypes.IN.name());
		inNode.setId(UUID.randomUUID().toString());
		inNode.setParent(node);
		node.getChild().add(inNode);
		DynamicTriggerInstanceTerminalNode outNode = new DynamicTriggerInstanceTerminalNode();
		outNode.setName("OUT");
		outNode.setType(Enums.DynamicTriggerInstanceTerminalTypes.OUT.name());
		outNode.setId(UUID.randomUUID().toString());
		outNode.setParent(node);
		node.getChild().add(outNode);
		DynamicTriggerInstanceTerminalNode resNode = new DynamicTriggerInstanceTerminalNode();
		resNode.setName("RESET");
		resNode.setType(Enums.DynamicTriggerInstanceTerminalTypes.RESET.name());
		resNode.setId(UUID.randomUUID().toString());
		resNode.setParent(node);
		node.getChild().add(resNode);
		return node;
	}

	@Override
	public Object getObjectType() {
		return DynamicTriggerInstanceNode.class;
	}

}
