package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class ModuleOperationCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		ModuleOperationNode node = new ModuleOperationNode();
		node.setId(UUID.randomUUID().toString());
		return node;
	}

	@Override
	public Object getObjectType() {
		return ModuleOperationNode.class;
	}

}
