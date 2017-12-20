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
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
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
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.action.RefreshAction;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.edpart.ComponentImplementationEditPart;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.edpart.EditPartFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ComponentImplementationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.DynamicTriggerInstanceNodeCreationFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.LinkCreationFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleImplementationCreationFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleInstanceCreationFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleInstancePropertyCreationFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleOperationCreationFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleOperationParameterCreationFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleTypeCreationFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.TriggerInstanceNodeCreationFactory;
import tech.ecoa.osets.eclipse.plugin.util.ParseUtil;

@SuppressWarnings("deprecation")
public class CompImplEditor extends GraphicalEditorWithFlyoutPalette implements IResourceChangeListener {

	private ComponentImplementationEditPart root;

	public CompImplEditor() {
		super();
		setEditDomain(new DefaultEditDomain(this));
	}

	@Override
	public void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		getGraphicalViewer().setRootEditPart(new ScalableFreeformRootEditPart());
		FileEditorInput inp = (FileEditorInput) getEditorInput();
		setPartName(inp.getFile().getName());
		try {
			String path = inp.getFile().getLocation().toOSString();
			String text = FileUtils.readFileToString(new File(path));
			ComponentImplementationNode node = parse(text);
			root = new ComponentImplementationEditPart(node, inp.getFile().getProject().getFullPath().toOSString());
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
		getActionRegistry().registerAction(new RefreshAction(this, inp.getFile().getProject().getFullPath(), inp.getFile()));
		getGraphicalViewer().setContextMenu(new ContextMenuProvider(getGraphicalViewer(), getActionRegistry()));
	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		PaletteRoot root = new PaletteRoot();
		PaletteDrawer manipGroup = new PaletteDrawer("Definitions");
		manipGroup.add(new CreationToolEntry("Add Module Type", "Add Module Type", new ModuleTypeCreationFactory(), null, null));
		manipGroup.add(new CreationToolEntry("Add Module Implementation", "Add Module Implementation", new ModuleImplementationCreationFactory(), null, null));
		manipGroup.add(new CreationToolEntry("Add Module Instance", "Add Module Instance", new ModuleInstanceCreationFactory(), null, null));
		manipGroup.add(new CreationToolEntry("Add Module Instance Property", "Add Module Instance Property", new ModuleInstancePropertyCreationFactory(), null, null));
		manipGroup.add(new CreationToolEntry("Add Module Operation", "Add Module Operation", new ModuleOperationCreationFactory(), null, null));
		manipGroup.add(new CreationToolEntry("Add Module Operation Parameter", "Add Module Operation Parameter", new ModuleOperationParameterCreationFactory(), null, null));
		manipGroup.add(new CreationToolEntry("Add Trigger Instance", "Add Trigger Instance", new TriggerInstanceNodeCreationFactory(), null, null));
		manipGroup.add(new CreationToolEntry("Add Dynamic Trigger Instance", "Add Dynamic Trigger Instance", new DynamicTriggerInstanceNodeCreationFactory(), null, null));
		manipGroup.add(new ConnectionCreationToolEntry("Add Wire", "Add Wire", new LinkCreationFactory(), null, null));
		root.add(manipGroup);
		return root;
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			ComponentImplementationNode node = (ComponentImplementationNode) root.getModel();
			FileEditorInput inp = (FileEditorInput) getEditorInput();
			inp.getFile().setContents(new StringBufferInputStream(ParseUtil.getCompImplEditorContent(node)), IFile.FORCE, monitor);
			getCommandStack().markSaveLocation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public GraphicalViewer getViewer() {
		return getGraphicalViewer();
	}

	public ComponentImplementationNode parse(String text) {
		return ParseUtil.getComponentImplementationNodeFromText(text);
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

}
