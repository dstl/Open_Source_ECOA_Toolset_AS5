package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class ModuleOperationParameterCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		ModuleOperationParameterNode node = new ModuleOperationParameterNode();
		node.setId(UUID.randomUUID().toString());
		return node;
	}

	@Override
	public Object getObjectType() {
		return ModuleOperationParameterNode.class;
	}

}
