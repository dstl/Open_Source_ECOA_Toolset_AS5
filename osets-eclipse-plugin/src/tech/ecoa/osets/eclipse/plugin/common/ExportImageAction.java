/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.common;

import org.eclipse.core.runtime.IPath;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.ui.IWorkbenchPart;

import tech.ecoa.osets.eclipse.plugin.editors.CompImplEditor;
import tech.ecoa.osets.eclipse.plugin.editors.InitAssemblyEditor;
import tech.ecoa.osets.eclipse.plugin.editors.IntDeploymentEditor;
import tech.ecoa.osets.eclipse.plugin.editors.IntFinalAssemblyEditor;
import tech.ecoa.osets.eclipse.plugin.editors.IntLogicalSysEditor;

public class ExportImageAction extends SelectionAction {

	private String containerName;
	private IWorkbenchPart part;

	public ExportImageAction(IWorkbenchPart part, IPath iPath) {
		super(part);
		setText("Export As Image");
		this.part = part;
		this.containerName = iPath.toString();
	}

	@Override
	public void run() {
		if (part instanceof GraphicalEditorWithFlyoutPalette) {
			GraphicalEditorWithFlyoutPalette edPart = (GraphicalEditorWithFlyoutPalette) part;
			ImageSaveUtil.save(edPart, getViewer(edPart));
		}
	}

	private GraphicalViewer getViewer(GraphicalEditorWithFlyoutPalette edPart) {
		if (edPart instanceof InitAssemblyEditor) {
			return ((InitAssemblyEditor) edPart).getViewer();
		} else if (edPart instanceof IntFinalAssemblyEditor) {
			return ((IntFinalAssemblyEditor) edPart).getViewer();
		} else if (edPart instanceof IntLogicalSysEditor) {
			return ((IntLogicalSysEditor) edPart).getViewer();
		} else if (edPart instanceof IntDeploymentEditor) {
			return ((IntDeploymentEditor) edPart).getViewer();
		} else if (edPart instanceof CompImplEditor) {
			return ((CompImplEditor) edPart).getViewer();
		}
		return null;
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	@Override
	public String getId() {
		return "OSETS_EXPORT_COMMANDS";
	}

	public IWorkbenchPart getPart() {
		return part;
	}

	public void setPart(IWorkbenchPart part) {
		this.part = part;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

}
