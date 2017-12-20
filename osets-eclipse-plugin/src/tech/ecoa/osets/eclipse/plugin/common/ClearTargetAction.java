/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.common;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

public class ClearTargetAction extends SelectionAction {

	private String containerName;
	private String[] names;
	private IWorkspaceRoot wsRoot;
	private IWorkbenchPart part;
	private IResource wsRootRes;
	private IProject prj;
	private IFolder steps;

	public ClearTargetAction(IWorkbenchPart part, IPath iPath) {
		super(part);
		setText("Clear Generated Code");
		this.part = part;
		this.containerName = iPath.toString();
	}

	@Override
	public void run() {
		if (GenerationUtils.validate(containerName)) {
			Shell shell = Display.getDefault().getActiveShell();
			boolean confirm = MessageDialog.openQuestion(shell, "Confirm Create", "Existing Files will be cleared. Do you wish to continue?");
			if (confirm) {
				wsRoot = ResourcesPlugin.getWorkspace().getRoot();
				names = StringUtils.split(containerName, "/");
				wsRootRes = wsRoot.findMember(new Path("/" + names[0]));
				prj = wsRootRes.getProject();
				steps = prj.getFolder("target/Steps");
				File root = new File(steps.getLocation().toOSString());
				if (root.exists()) {
					GenerationUtils.clearCreatedFolders(root);
				}
				for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
					try {
						project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
					} catch (CoreException e) {
					}
				}
			}
		}
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	@Override
	public String getId() {
		return "OSETS_CLEAR_COMMANDS";
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public IWorkbenchPart getPart() {
		return part;
	}

	public void setPart(IWorkbenchPart part) {
		this.part = part;
	}

}
