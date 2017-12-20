/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.common;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.FileEditorInput;

import tech.ecoa.osets.eclipse.plugin.editors.CompImplEditor;
import tech.ecoa.osets.eclipse.plugin.editors.InitAssemblyEditor;
import tech.ecoa.osets.eclipse.plugin.editors.IntDeploymentEditor;
import tech.ecoa.osets.eclipse.plugin.editors.IntFinalAssemblyEditor;
import tech.ecoa.osets.eclipse.plugin.editors.IntLogicalSysEditor;
import tech.ecoa.osets.eclipse.plugin.util.EclipseUtil;

@SuppressWarnings("deprecation")
public class ExportAction extends SelectionAction {

	private String containerName;
	private IWorkbenchPart part;

	public ExportAction(IWorkbenchPart part, IPath iPath) {
		super(part);
		setText("Generate XML");
		this.part = part;
		this.containerName = iPath.toString();
	}

	@Override
	public void run() {
		if (part instanceof GraphicalEditorWithFlyoutPalette) {
			GraphicalEditorWithFlyoutPalette edPart = (GraphicalEditorWithFlyoutPalette) part;
			ExportCommand cmd = new ExportCommand(containerName, getFileName(edPart), getFileType(edPart), getContent(edPart));
			execute(cmd);
		}
	}

	private String getContent(GraphicalEditorWithFlyoutPalette edPart) {
		try {
			FileEditorInput inp = (FileEditorInput) edPart.getEditorInput();
			String path = inp.getFile().getLocation().toOSString();
			String text = FileUtils.readFileToString(new File(path));
			return text;
		} catch (Exception e) {
			EclipseUtil.writeStactTraceToConsole(e);
		}
		return null;
	}

	private String getFileName(GraphicalEditorWithFlyoutPalette edPart) {
		FileEditorInput inp = (FileEditorInput) edPart.getEditorInput();
		return inp.getFile().getLocation().toOSString();
	}

	private String getFileType(GraphicalEditorWithFlyoutPalette edPart) {
		if (edPart instanceof InitAssemblyEditor) {
			return "assmbl";
		} else if (edPart instanceof IntFinalAssemblyEditor) {
			return "fassmbl";
		} else if (edPart instanceof IntLogicalSysEditor) {
			return "lsys";
		} else if (edPart instanceof IntDeploymentEditor) {
			return "deploy";
		} else if (edPart instanceof CompImplEditor) {
			return "cimpl";
		}
		return null;
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	@Override
	public String getId() {
		return "OSETS_COMMANDS";
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
