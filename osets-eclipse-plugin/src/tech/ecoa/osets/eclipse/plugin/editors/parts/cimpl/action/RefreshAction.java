/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.action;

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
import tech.ecoa.osets.eclipse.plugin.editors.CompImplEditor;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ComponentImplementationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Link;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceOperationNode;
import tech.ecoa.osets.eclipse.plugin.util.EclipseUtil;
import tech.ecoa.osets.eclipse.plugin.util.ParseUtil;

@SuppressWarnings("deprecation")
public class RefreshAction extends SelectionAction {

	private String containerName;
	private IWorkbenchPart part;
	private IFile file;

	public RefreshAction(IWorkbenchPart part, IPath iPath, IFile file) {
		super(part);
		setText("Refresh Implementation");
		this.part = part;
		this.containerName = iPath.toString();
		this.file = file;
	}

	@Override
	public void run() {
		try {
			Shell shell = Display.getDefault().getActiveShell();
			boolean confirm = MessageDialog.openQuestion(shell, "Confirm Create", "Existing Service and Reference Nodes will be reset. Do you wish to continue?");
			if (confirm) {
				if (part instanceof CompImplEditor) {
					String path = file.getLocation().toOSString();
					String text = FileUtils.readFileToString(new File(path));
					ComponentImplementationNode node = ParseUtil.getComponentImplementationNodeFromText(text);
					ComponentImplementationNode proc = ParseUtil.getComponentImplementationNodeFromText(GenerationUtils.getCompImplFromDef(node.getName(), containerName));
					for (Node child : node.getChild()) {
						if (!(child instanceof ServiceNode))
							proc.getChild().add(child);
					}
					for (Link link : node.getLinks()) {
						if (!((link.getSource() instanceof ServiceOperationNode) || (link.getTarget() instanceof ServiceOperationNode)))
							proc.getLinks().add(link);
					}
					final ComponentImplementationNode immProc = proc;
					IRunnableWithProgress op = new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor) throws InvocationTargetException {
							try {
								doFinish(containerName, file, monitor, immProc);
							} catch (CoreException e) {
								throw new InvocationTargetException(e);
							} finally {
								monitor.done();
							}
						}

						private void doFinish(String containerName, IFile file, IProgressMonitor monitor, ComponentImplementationNode immProc) throws CoreException {
							final IFile copy = file;
							try {
								InputStream stream = openContentStream(immProc);
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

						private InputStream openContentStream(ComponentImplementationNode immProc) {
							try {
								String content = ParseUtil.getCompImplEditorContent(immProc);
								return new ByteArrayInputStream(content.getBytes());
							} catch (Exception e) {
								MessageDialog.openError(shell, "Refresh Error", "Cannot refresh the editor");
								return new ByteArrayInputStream(text.getBytes());
							}
						}

					};
					part.getSite().getWorkbenchWindow().run(true, false, op);
					((CompImplEditor) part).initializeGraphicalViewer();
				}
			}
		} catch (Exception e) {
			EclipseUtil.writeStactTraceToConsole(e);
		}
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	@Override
	public String getId() {
		return "OSETS_CREFRESH_COMMANDS";
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
