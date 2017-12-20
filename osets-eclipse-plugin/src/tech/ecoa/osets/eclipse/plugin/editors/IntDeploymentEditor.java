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
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.action.RefreshAction;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.edpart.DeploymentEditPart;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.edpart.EditPartFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.ComputingNodeConfigurationCreationFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeployedModuleInstanceCreationFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeployedTriggerInstanceCreationFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeploymentNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.PlatformConfigurationCreationFactory;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.ProtectionDomainCreationFactory;
import tech.ecoa.osets.eclipse.plugin.util.ParseUtil;

@SuppressWarnings("deprecation")
public class IntDeploymentEditor extends GraphicalEditorWithFlyoutPalette implements IResourceChangeListener {
	private DeploymentEditPart root;

	public IntDeploymentEditor() {
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
			DeploymentNode node = parse(text);
			root = new DeploymentEditPart(node, inp.getFile().getProject().getFullPath().toOSString());
			getGraphicalViewer().setContents(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public GraphicalViewer getViewer() {
		return getGraphicalViewer();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		try {
			DeploymentNode node = (DeploymentNode) root.getModel();
			FileEditorInput inp = (FileEditorInput) getEditorInput();
			inp.getFile().setContents(new StringBufferInputStream(ParseUtil.getIntDeploymentEditorContent(node)), IFile.FORCE, monitor);
			getCommandStack().markSaveLocation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private DeploymentNode parse(String text) {
		return ParseUtil.getDeploymentNodeFromText(text);
	}

	@Override
	protected PaletteRoot getPaletteRoot() {
		PaletteRoot root = new PaletteRoot();
		PaletteDrawer manipGroup = new PaletteDrawer("Definitions");
		manipGroup.add(new CreationToolEntry("Add Platform Configuration", "Add Platform Configuration", new PlatformConfigurationCreationFactory(), null, null));
		manipGroup.add(new CreationToolEntry("Add Computing Node Configuration", "Add Computing Node Configuration", new ComputingNodeConfigurationCreationFactory(), null, null));
		manipGroup.add(new CreationToolEntry("Add Protection Domain", "Add Protection Domain", new ProtectionDomainCreationFactory(), null, null));
		manipGroup.add(new CreationToolEntry("Add Module Instance", "Add Module Instance", new DeployedModuleInstanceCreationFactory(), null, null));
		manipGroup.add(new CreationToolEntry("Add Trigger Instance", "Add Trigger Instance", new DeployedTriggerInstanceCreationFactory(), null, null));
		root.add(manipGroup);
		return root;
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
}
