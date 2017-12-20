/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.action;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPart;

import tech.ecoa.osets.eclipse.plugin.common.GenerationUtils;
import tech.ecoa.osets.eclipse.plugin.editors.IntFinalAssemblyEditor;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositeNode;
import tech.ecoa.osets.eclipse.plugin.util.EclipseUtil;
import tech.ecoa.osets.eclipse.plugin.util.ParseUtil;

@SuppressWarnings("deprecation")
public class RefreshAction extends SelectionAction {

	private String containerName;
	private IWorkbenchPart part;
	private IFile file;

	public RefreshAction(IWorkbenchPart part, IPath iPath, IFile file) {
		super(part);
		setText("Refresh Final Assembly");
		this.part = part;
		this.containerName = iPath.toString();
		this.file = file;
	}

	@Override
	public void run() {
		try {
			Shell shell = Display.getDefault().getActiveShell();
			boolean confirm = MessageDialog.openQuestion(shell, "Confirm Refresh", "Existing changes will be reset. Do you wish to continue?");
			if (confirm) {
				if (part instanceof IntFinalAssemblyEditor) {
					String path = file.getLocation().toOSString();
					String text = FileUtils.readFileToString(new File(path));
					CompositeNode node = ParseUtil.getFinalAssemblyNodeFromText(text);
					final String content = GenerationUtils.getFinalAssemblyfromInitialAssembly(node.getDef(), containerName, file.getName());
					IRunnableWithProgress op = new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) throws InvocationTargetException {
							try {
								doFinish(containerName, file, monitor, content);
							} catch (CoreException e) {
								throw new InvocationTargetException(e);
							} finally {
								monitor.done();
							}
						}

						private void doFinish(String containerName, IFile file, IProgressMonitor monitor, String content) throws CoreException {
							final IFile copy = file;
							try {
								InputStream stream = openContentStream(node.getDef(), containerName, file.getName());
								if (copy.exists()) {
									copy.setContents(stream, true, true, monitor);
								} else {
									copy.create(stream, true, monitor);
								}
								stream.close();
							} catch (IOException e) {
							}
							monitor.worked(1);
						}

						private InputStream openContentStream(String name, String containerName, String fileName) {
							try {
								String content = GenerationUtils.getFinalAssemblyfromInitialAssembly(name, containerName, fileName);
								return new ByteArrayInputStream(content.getBytes());
							} catch (IOException e) {
								MessageDialog.openError(shell, "Refresh Error", "Cannot refresh the editor");
								return new ByteArrayInputStream(text.getBytes());
							}
						}

					};
					part.getSite().getWorkbenchWindow().run(true, false, op);
					((IntFinalAssemblyEditor) part).initializeGraphicalViewer();
				}
			}
		} catch (IOException | InvocationTargetException | InterruptedException e) {
			EclipseUtil.writeStactTraceToConsole(e);
		}
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	@Override
	public String getId() {
		return "OSETS_FREFRESH_COMMANDS";
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
