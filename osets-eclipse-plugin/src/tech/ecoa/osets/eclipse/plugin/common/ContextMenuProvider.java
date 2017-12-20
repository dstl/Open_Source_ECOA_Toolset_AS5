/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.common;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;

public class ContextMenuProvider extends org.eclipse.gef.ContextMenuProvider {

	private ActionRegistry actionRegistry;

	public ContextMenuProvider(EditPartViewer viewer, ActionRegistry actionRegistry) {
		super(viewer);
		setActionRegistry(actionRegistry);
	}

	@Override
	public void buildContextMenu(IMenuManager menu) {
		GEFActionConstants.addStandardActionGroups(menu);
		IAction rAction;
		rAction = getActionRegistry().getAction("OSETS_FREFRESH_COMMANDS");
		if (rAction != null) {
			menu.appendToGroup(GEFActionConstants.MB_ADDITIONS, rAction);
		}
		rAction = getActionRegistry().getAction("OSETS_DREFRESH_COMMANDS");
		if (rAction != null) {
			menu.appendToGroup(GEFActionConstants.MB_ADDITIONS, rAction);
		}
		rAction = getActionRegistry().getAction("OSETS_CREFRESH_COMMANDS");
		if (rAction != null) {
			menu.appendToGroup(GEFActionConstants.MB_ADDITIONS, rAction);
		}
		IAction eAction;
		eAction = getActionRegistry().getAction("OSETS_EXPORT_COMMANDS");
		menu.appendToGroup(GEFActionConstants.MB_ADDITIONS, eAction);
		IAction action;
		action = getActionRegistry().getAction("OSETS_COMMANDS");
		menu.appendToGroup(GEFActionConstants.MB_ADDITIONS, action);
		IAction cAction;
		cAction = getActionRegistry().getAction("OSETS_CLEAR_COMMANDS");
		menu.appendToGroup(GEFActionConstants.MB_ADDITIONS, cAction);
		IAction uAction;
		uAction = getActionRegistry().getAction("OSETS_UID_COMMANDS");
		menu.appendToGroup(GEFActionConstants.MB_ADDITIONS, uAction);
		IAction aAction;
		aAction = getActionRegistry().getAction("OSETS_API_COMMANDS");
		menu.appendToGroup(GEFActionConstants.MB_ADDITIONS, aAction);
		IAction iAction;
		iAction = getActionRegistry().getAction("OSETS_INT_COMMANDS");
		menu.appendToGroup(GEFActionConstants.MB_ADDITIONS, iAction);
	}

	public ActionRegistry getActionRegistry() {
		return actionRegistry;
	}

	public void setActionRegistry(ActionRegistry actionRegistry) {
		this.actionRegistry = actionRegistry;
	}

}
