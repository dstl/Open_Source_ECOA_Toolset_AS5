/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

import tech.ecoa.osets.eclipse.plugin.editors.parts.service.DataServiceComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.service.EventServiceComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.service.ReqResServiceComposite;
import tech.ecoa.osets.eclipse.plugin.util.ServicesUtil;
import tech.ecoa.osets.eclipse.plugin.util.TypesUtil;
import tech.ecoa.osets.model.intf.Data;
import tech.ecoa.osets.model.intf.Event;
import tech.ecoa.osets.model.intf.RequestResponse;

/**
 * An example showing how to create a multi-page editor. This example has 3
 * pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class ServicesEditor extends MultiPageEditorPart implements IResourceChangeListener {

	/** The text editor used in page 0. */
	private TextEditor editor;

	private Tree tree;

	private ServicesUtil sUtil = new ServicesUtil();

	private TypesUtil util = new TypesUtil();

	/**
	 * Creates a multi-page editor example.
	 */
	public ServicesEditor() {
		super();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
	}

	/**
	 * Creates page 0 of the multi-page editor, which contains a text editor.
	 */
	void createPage0() {
		try {
			editor = new TextEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, "XML");
			setPartName(((FileEditorInput) editor.getEditorInput()).getFile().getName());
		} catch (PartInitException e) {
			ErrorDialog.openError(getSite().getShell(), "Error creating nested text editor", null, e.getStatus());
		}
	}

	/**
	 * Creates page 1 of the multi-page editor, which allows you to change the
	 * font used in page 2.
	 */
	void createPage1() {

		Composite composite = new Composite(getContainer(), SWT.BORDER_SOLID);
		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 3;
		GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
		try {
			FileEditorInput inp = (FileEditorInput) editor.getEditorInput();
			String path = inp.getFile().getLocation().toOSString();
			File file = new File(path);
			String containerName = "/" + file.getParentFile().getName() + "/";
			String content = editor.getDocumentProvider().getDocument(getEditorInput()).get();
			sUtil.setEditorText(content);
			util.setContainerName(containerName);
			util.setFileName("");
			tree = sUtil.buildTreeFromContent(composite);
			tree.setLayoutData(gd);
			tree.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent evt) {
					if (evt.item.getClass().getName().equalsIgnoreCase(TreeItem.class.getName())) {
						GridLayout compLayout = new GridLayout();
						compLayout.numColumns = 2;
						TreeItem sel = (TreeItem) evt.item;
						TreeItem parent = sel.getParentItem();
						disposeRemaining(composite, parent);
						if (parent != null) {
							Composite btnComp = new Composite(composite, SWT.BORDER_SOLID);
							GridLayout gL = new GridLayout();
							gL.numColumns = 1;
							btnComp.setLayout(gL);
							Button button = new Button(btnComp, SWT.PUSH);
							button.setText("Add Op");
							button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
							button.addMouseListener(new SelectMouseListener(sel, content, containerName));
							button = new Button(btnComp, SWT.PUSH);
							button.setText("Remove Op");
							button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
							button.addMouseListener(new SelectMouseListener(sel, content, containerName));
						}
						composite.layout();
						if (parent != null && parent.getText().equalsIgnoreCase("Data")) {
							try {
								Data data = sUtil.getSelectedDataOp(sel.getText(), Data.class);
								DataServiceComposite comp = new DataServiceComposite();
								comp.setData(data);
								comp.setContainerName(containerName);
								comp.createPartControl(composite);
								Composite dataDet = comp.getRet();
								dataDet.setLayout(compLayout);
								dataDet.setLayoutData(gd);
								Label lbl = new Label(dataDet, SWT.BORDER_SOLID);
								lbl.setText("");
								Button button = new Button(dataDet, SWT.PUSH);
								button.setText("Save");
								button.addMouseListener(new SaveMouseListener(true, comp.getData().getName(), comp, parent.getText()));
								dataDet.layout();
								composite.layout();
							} catch (JAXBException e) {
								ErrorDialog.openError(getSite().getShell(), "Error reading text editor", null, new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Error reading text editor", null));
							}
						} else if (parent != null && parent.getText().equalsIgnoreCase("Event")) {
							try {
								Event event = sUtil.getSelectedEventOp(sel.getText(), Event.class);
								EventServiceComposite comp = new EventServiceComposite();
								comp.setUtil(util);
								comp.setEvent(event);
								comp.setContainerName(containerName);
								comp.createPartControl(composite);
								Composite evtDet = comp.getRet();
								evtDet.setLayout(compLayout);
								evtDet.setLayoutData(gd);
								Label lbl = new Label(evtDet, SWT.BORDER_SOLID);
								lbl.setText("");
								Button button = new Button(evtDet, SWT.PUSH);
								button.setText("Save");
								button.addMouseListener(new SaveMouseListener(true, comp.getEvent().getName(), comp, parent.getText()));
								evtDet.layout();
								composite.layout();
							} catch (JAXBException e) {
								ErrorDialog.openError(getSite().getShell(), "Error reading text editor", null, new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Error reading text editor", null));
							}
						} else if (parent != null && parent.getText().equalsIgnoreCase("Request Response")) {
							try {
								RequestResponse reqRes = sUtil.getSelectedReqResOp(sel.getText(), RequestResponse.class);
								ReqResServiceComposite comp = new ReqResServiceComposite();
								comp.setUtil(util);
								comp.setReqRes(reqRes);
								comp.setContainerName(containerName);
								comp.createPartControl(composite);
								Composite reqResDet = comp.getRet();
								reqResDet.setLayout(compLayout);
								reqResDet.setLayoutData(gd);
								Label lbl = new Label(reqResDet, SWT.BORDER_SOLID);
								lbl.setText("");
								Button button = new Button(reqResDet, SWT.PUSH);
								button.setText("Save");
								button.addMouseListener(new SaveMouseListener(true, comp.getReqRes().getName(), comp, parent.getText()));
								reqResDet.layout();
								composite.layout();
							} catch (JAXBException e) {
								ErrorDialog.openError(getSite().getShell(), "Error reading text editor", null, new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Error reading text editor", null));
							}
						}
					}
				}

				protected void disposeRemaining(Composite composite, TreeItem parent) {
					Control[] ctrls = composite.getChildren();
					for (Control ctrl : ctrls) {
						if (!(ctrl instanceof Tree)) {
							if (parent != null && parent.getText().equalsIgnoreCase("Operations")) {
								if (ctrl.getClass().getName().equalsIgnoreCase(Button.class.getName()))
									continue;
								else {
									ctrl.setVisible(false);
									ctrl.dispose();
								}
							} else {
								ctrl.setVisible(false);
								ctrl.dispose();
							}
						}
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});
			composite.layout(true, true);
		} catch (JAXBException e) {
			ErrorDialog.openError(getSite().getShell(), "Error reading text editor", null, new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Error reading text editor", null));
		}

		if (getPageCount() == 2)
			removePage(1);
		int index = addPage(composite);
		setPageText(index, "Tree");
	}

	public class SelectMouseListener implements MouseListener {
		private TreeItem selItem;
		private String content;
		private String containerName;

		public SelectMouseListener(TreeItem selItem, String content, String containerName) {
			super();
			this.selItem = selItem;
			this.content = content;
			this.containerName = containerName;
		}

		@Override
		public void mouseDoubleClick(MouseEvent e) {
		}

		@Override
		public void mouseDown(MouseEvent e) {
		}

		@Override
		public void mouseUp(MouseEvent e) {
			if (e.getSource() instanceof Button) {
				Button btn = (Button) e.getSource();
				Composite composite = btn.getParent().getParent();
				if (btn.getText().equalsIgnoreCase("Add Op")) {
					GridLayout compLayout = new GridLayout();
					compLayout.numColumns = 2;
					GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
					Composite child = null;
					Object com = null;
					if (!level2Item(selItem)) {
						ErrorDialog.openError(getSite().getShell(), "Please Select a level 2 Item for Add", null, new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Please Select a level 2 Item for Add", null));
					} else {
						if (selItem != null && selItem.getText().equalsIgnoreCase("Data")) {
							Data data = new Data();
							DataServiceComposite comp = new DataServiceComposite();
							comp.setData(data);
							comp.setContainerName(containerName);
							comp.createPartControl(composite);
							child = comp.getRet();
							com = comp;
						} else if (selItem != null && selItem.getText().equalsIgnoreCase("Event")) {
							Event event = new Event();
							EventServiceComposite comp = new EventServiceComposite();
							comp.setUtil(util);
							comp.setEvent(event);
							comp.setContainerName(containerName);
							comp.createPartControl(composite);
							child = comp.getRet();
							com = comp;
						} else if (selItem != null && selItem.getText().equalsIgnoreCase("Request Response")) {
							RequestResponse reqRes = new RequestResponse();
							ReqResServiceComposite comp = new ReqResServiceComposite();
							comp.setUtil(util);
							comp.setReqRes(reqRes);
							comp.setContainerName(containerName);
							comp.createPartControl(composite);
							child = comp.getRet();
							com = comp;
						}
						Label lbl = new Label(child, SWT.BORDER_SOLID);
						lbl.setText("");
						Button button = new Button(child, SWT.PUSH);
						button.setText("Save");
						button.addMouseListener(new SaveMouseListener(false, null, com, selItem.getText()));
						child.setLayout(compLayout);
						child.setLayoutData(gd);
						child.layout();
						composite.layout();
					}
				} else if (btn.getText().equalsIgnoreCase("Remove Op")) {
					if (selItem == null || selItem.getParent() == null || selItem.getParent().getParent() == null) {
					} else {
						try {
							String tempText = sUtil.removeItem(selItem, selItem.getParentItem().getText(), content);
							editor.getDocumentProvider().getDocument(getEditorInput()).set(tempText);
							createPage1();
							setActivePage(1);
						} catch (JAXBException ex) {
							ErrorDialog.openError(getSite().getShell(), "Error removing item", null, new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Error removing item", null));
						}
					}
				}
			}
		}

		private boolean level2Item(TreeItem check) {
			boolean ret = false;
			if (check.getParentItem() != null && check.getParentItem().getParentItem() == null)
				ret = true;
			return ret;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getContainerName() {
			return containerName;
		}

		public void setContainerName(String containerName) {
			this.containerName = containerName;
		}

		public TreeItem getSelItem() {
			return selItem;
		}

		public void setSelItem(TreeItem selItem) {
			this.selItem = selItem;
		}

	}

	public class SaveMouseListener implements MouseListener {
		private Object comp;
		private String type;
		private boolean isEdit;
		private String editName;

		public SaveMouseListener(boolean isEdit, String editName, Object comp, String type) {
			super();
			this.comp = comp;
			this.type = type;
			this.editName = editName;
			this.isEdit = isEdit;
		}

		@Override
		public void mouseDoubleClick(MouseEvent e) {
		}

		@Override
		public void mouseDown(MouseEvent e) {
		}

		@Override
		public void mouseUp(MouseEvent e) {
			if (e.getSource() instanceof Button) {
				Button sel = (Button) e.getSource();
				if (sel.getText().equalsIgnoreCase("Save")) {
					try {
						String tempText = sUtil.processAdd(isEdit, editName, comp, type, editor.getDocumentProvider().getDocument(getEditorInput()).get());
						editor.getDocumentProvider().getDocument(getEditorInput()).set(tempText);
						createPage1();
						setActivePage(1);
					} catch (JAXBException ex) {
						ErrorDialog.openError(getSite().getShell(), "Error removing item", null, new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Error removing item", null));
					}
				}
			}
		}

		public Object getComp() {
			return comp;
		}

		public void setComp(Object comp) {
			this.comp = comp;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public boolean isEdit() {
			return isEdit;
		}

		public void setEdit(boolean isEdit) {
			this.isEdit = isEdit;
		}

		public String getEditName() {
			return editName;
		}

		public void setEditName(String editName) {
			this.editName = editName;
		}

	}

	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPage0();
		createPage1();
	}

	/**
	 * The <code>MultiPageEditorPart</code> implementation of this
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}

	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}

	/**
	 * Saves the multi-page editor's document as another file. Also updates the
	 * text for page 0's tab, and updates this multi-page editor's input to
	 * correspond to the nested editor's.
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}

	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput) throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}

	/*
	 * (non-Javadoc) Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i < pages.length; i++) {
						if (((FileEditorInput) editor.getEditorInput()).getFile().getProject().equals(event.getResource())) {
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
							pages[i].closeEditor(editorPart, true);
						}
					}
				}
			});
		}
	}
}
