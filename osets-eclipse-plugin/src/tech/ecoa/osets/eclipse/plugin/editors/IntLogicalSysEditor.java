/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors;

import java.io.File;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.util.EventObject;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.part.FileEditorInput;

import tech.ecoa.osets.eclipse.plugin.common.ClearTargetAction;
import tech.ecoa.osets.eclipse.plugin.common.ContextMenuProvider;
import tech.ecoa.osets.eclipse.plugin.common.ExportAction;
import tech.ecoa.osets.eclipse.plugin.common.ExportImageAction;
import tech.ecoa.osets.eclipse.plugin.common.GenerateAPIAction;
import tech.ecoa.osets.eclipse.plugin.common.GenerateINTAction;
import tech.ecoa.osets.eclipse.plugin.common.GenerateUIDAction;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.edpart.EditPartFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.edpart.LogicalSystemEditPart;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalComputingCreationFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalComputingPlatformCreationFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalProcessorsCreationFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalSystemNode;
import tech.ecoa.osets.eclipse.plugin.util.ParseUtil;

@SuppressWarnings("deprecation")
public class IntLogicalSysEditor extends GraphicalEditorWithFlyoutPalette implements IResourceChangeListener {
	LogicalSystemEditPart root;

	public IntLogicalSysEditor() {
		super();
		setEditDomain(new DefaultEditDomain(this));
	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		PaletteRoot root = new PaletteRoot();
		PaletteDrawer manipGroup = new PaletteDrawer("Definitions");
		manipGroup.add(new CreationToolEntry("Add Computing Platform", "Add Computing Platform Definition", new LogicalComputingPlatformCreationFactory(), null, null));
		manipGroup.add(new CreationToolEntry("Add Computing Node", "Add Computing Node Definition", new LogicalComputingCreationFactory(), null, null));
		manipGroup.add(new CreationToolEntry("Add Logical Processor", "Add Logical Processor Definition", new LogicalProcessorsCreationFactory(), null, null));
		root.add(manipGroup);
		return root;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			LogicalSystemNode node = (LogicalSystemNode) root.getModel();
			FileEditorInput inp = (FileEditorInput) getEditorInput();
			inp.getFile().setContents(new StringBufferInputStream(ParseUtil.getLogicalSysEditorContent(node)), IFile.FORCE, monitor);
			getCommandStack().markSaveLocation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private LogicalSystemNode parse(String text) {
		return ParseUtil.getLogicalSystemNodeFromText(text);
	}

	@Override
	protected void createActions() {
		super.createActions();
	}

	@Override
	protected void setInput(IEditorInput input) {
		super.setInput(input);
	}

	@Override
	public void commandStackChanged(EventObject event) {
		firePropertyChange(PROP_DIRTY);
		super.commandStackChanged(event);
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i < pages.length; i++) {
						if (((FileEditorInput) getEditorInput()).getFile().getProject().equals(event.getResource())) {
							IEditorPart editorPart = pages[i].findEditor(getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}

	public GraphicalViewer getViewer() {
		return getGraphicalViewer();
	}

	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		getGraphicalViewer().setRootEditPart(new ScalableFreeformRootEditPart());
		FileEditorInput inp = (FileEditorInput) getEditorInput();
		setPartName(inp.getFile().getName());
		try {
			String path = inp.getFile().getLocation().toOSString();
			String text = FileUtils.readFileToString(new File(path));
			LogicalSystemNode node = parse(text);
			root = new LogicalSystemEditPart(node, inp.getFile().getProject().getFullPath().toOSString());
			getGraphicalViewer().setContents(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void configureGraphicalViewer() {
		super.configureGraphicalViewer();
		EditPartFactory factory = new EditPartFactory();
		FileEditorInput inp = (FileEditorInput) getEditorInput();
		factory.setContainerName(inp.getFile().getProject().getFullPath().toOSString());
		getGraphicalViewer().setEditPartFactory(factory);
		((FigureCanvas) getGraphicalControl()).setScrollBarVisibility(FigureCanvas.ALWAYS);
		getActionRegistry().registerAction(new ExportImageAction(this, inp.getFile().getProject().getFullPath()));
		getActionRegistry().registerAction(new ExportAction(this, inp.getFile().getProject().getFullPath()));
		getActionRegistry().registerAction(new GenerateAPIAction(this, inp.getFile().getProject().getFullPath()));
		getActionRegistry().registerAction(new GenerateINTAction(this, inp.getFile().getProject().getFullPath()));
		getActionRegistry().registerAction(new GenerateUIDAction(this, inp.getFile().getProject().getFullPath()));
		getActionRegistry().registerAction(new ClearTargetAction(this, inp.getFile().getProject().getFullPath()));
		getGraphicalViewer().setContextMenu(new ContextMenuProvider(getGraphicalViewer(), getActionRegistry()));
	}
}
