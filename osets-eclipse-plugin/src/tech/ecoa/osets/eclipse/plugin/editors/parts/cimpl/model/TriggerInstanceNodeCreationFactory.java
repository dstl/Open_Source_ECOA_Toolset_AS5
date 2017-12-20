package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class TriggerInstanceNodeCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		TriggerInstanceNode node = new TriggerInstanceNode();
		TriggerInstanceTerminalNode cNode = new TriggerInstanceTerminalNode();
		node.setId(UUID.randomUUID().toString());
		cNode.setId(UUID.randomUUID().toString());
		cNode.setParent(node);
		node.getChild().add(cNode);
		return node;
	}

	@Override
	public Object getObjectType() {
		return TriggerInstanceNode.class;
	}

}
