/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FilenameUtils;
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

import tech.ecoa.osets.eclipse.plugin.editors.parts.types.ArrayTypesComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.types.ConstantTypesComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.types.EnumTypesComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.types.FixedArrayTypesComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.types.RecordTypesComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.types.SimpleTypesComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.types.VarRecTypesComposite;
import tech.ecoa.osets.eclipse.plugin.util.TypesUtil;
import tech.ecoa.osets.model.types.Array;
import tech.ecoa.osets.model.types.Constant;
import tech.ecoa.osets.model.types.FixedArray;
import tech.ecoa.osets.model.types.Record;
import tech.ecoa.osets.model.types.Simple;
import tech.ecoa.osets.model.types.VariantRecord;

/**
 * An example showing how to create a multi-page editor. This example has 3
 * pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class TypesEditor extends MultiPageEditorPart implements IResourceChangeListener {

	/** The text editor used in page 0. */
	private TextEditor editor;

	private Tree tree;

	private TypesUtil util = new TypesUtil();

	/**
	 * Creates a multi-page editor example.
	 */
	public TypesEditor() {
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
			util.setFileName(FilenameUtils.getBaseName(file.getName()));
			util.setContainerName(containerName);
			util.setEditorText(content);
			tree = util.buildTreeFromContent(composite);
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
							button.setText("Add Type");
							button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
							button.addMouseListener(new SelectMouseListener(sel, content, containerName));
							button = new Button(btnComp, SWT.PUSH);
							button.setText("Remove Type");
							button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));
							button.addMouseListener(new SelectMouseListener(sel, content, containerName));
						}
						composite.layout();
						if (parent != null && parent.getText().equalsIgnoreCase("Array")) {
							try {
								Array arr = util.getSelectedArrayType(sel.getText(), Array.class);
								ArrayTypesComposite comp = new ArrayTypesComposite();
								comp.setUtil(util);
								comp.setArray(arr);
								comp.setContainerName(containerName);
								comp.createPartControl(composite);
								Composite arrDet = comp.getRet();
								arrDet.setLayout(compLayout);
								arrDet.setLayoutData(gd);
								Label lbl = new Label(arrDet, SWT.BORDER_SOLID);
								lbl.setText("");
								Button button = new Button(arrDet, SWT.PUSH);
								button.setText("Save");
								button.addMouseListener(new SaveMouseListener(true, arr.getName(), comp, parent.getText()));
								arrDet.layout();
								composite.layout();
							} catch (JAXBException e) {
								ErrorDialog.openError(getSite().getShell(), "Error reading text editor", null, new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Error reading text editor", null));
							}
						} else if (parent != null && parent.getText().equalsIgnoreCase("Constant")) {
							try {
								Constant cnst = util.getSelectedConstantType(sel.getText(), Constant.class);
								ConstantTypesComposite comp = new ConstantTypesComposite();
								comp.setUtil(util);
								comp.setCnst(cnst);
								comp.createPartControl(composite);
								comp.setContainerName(containerName);
								Composite cnstDet = comp.getRet();
								cnstDet.setLayout(compLayout);
								cnstDet.setLayoutData(gd);
								Label lbl = new Label(cnstDet, SWT.BORDER_SOLID);
								lbl.setText("");
								Button button = new Button(cnstDet, SWT.PUSH);
								button.setText("Save");
								button.addMouseListener(new SaveMouseListener(true, cnst.getName(), comp, parent.getText()));
								cnstDet.layout();
								composite.layout();
							} catch (JAXBException e) {
								ErrorDialog.openError(getSite().getShell(), "Error reading text editor", null, new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Error reading text editor", null));
							}
						} else if (parent != null && parent.getText().equalsIgnoreCase("Enum")) {
							try {
								tech.ecoa.osets.model.types.Enum enm = util.getSelectedEnumType(sel.getText(), tech.ecoa.osets.model.types.Enum.class);
								EnumTypesComposite comp = new EnumTypesComposite();
								comp.setUtil(util);
								comp.setEnm(enm);
								comp.setContainerName(containerName);
								comp.createPartControl(composite);
								Composite enmDet = comp.getRet();
								enmDet.setLayout(compLayout);
								enmDet.setLayoutData(gd);
								Label lbl = new Label(enmDet, SWT.BORDER_SOLID);
								lbl.setText("");
								Button button = new Button(enmDet, SWT.PUSH);
								button.setText("Save");
								button.addMouseListener(new SaveMouseListener(true, enm.getName(), comp, parent.getText()));
								enmDet.layout();
								composite.layout();
							} catch (JAXBException e) {
								ErrorDialog.openError(getSite().getShell(), "Error reading text editor", null, new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Error reading text editor", null));
							}
						} else if (parent != null && parent.getText().equalsIgnoreCase("Simple")) {
							try {
								Simple simple = util.getSelectedSimpleType(sel.getText(), Simple.class);
								SimpleTypesComposite comp = new SimpleTypesComposite();
								comp.setUtil(util);
								comp.setSimple(simple);
								comp.setContainerName(containerName);
								comp.createPartControl(composite);
								Composite arrDet = comp.getRet();
								arrDet.setLayout(compLayout);
								arrDet.setLayoutData(gd);
								Label lbl = new Label(arrDet, SWT.BORDER_SOLID);
								lbl.setText("");
								Button button = new Button(arrDet, SWT.PUSH);
								button.setText("Save");
								button.addMouseListener(new SaveMouseListener(true, simple.getName(), comp, parent.getText()));
								arrDet.layout();
								composite.layout();
							} catch (JAXBException e) {
								ErrorDialog.openError(getSite().getShell(), "Error reading text editor", null, new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Error reading text editor", null));
							}
						} else if (parent != null && parent.getText().equalsIgnoreCase("Record")) {
							try {
								Record rec = util.getSelectedRecordType(sel.getText(), Record.class);
								RecordTypesComposite comp = new RecordTypesComposite();
								comp.setUtil(util);
								comp.setContainerName(containerName);
								comp.setRecord(rec);
								comp.createPartControl(composite);
								Composite arrDet = comp.getRet();
								arrDet.setLayout(compLayout);
								arrDet.setLayoutData(gd);
								Label lbl = new Label(arrDet, SWT.BORDER_SOLID);
								lbl.setText("");
								Button button = new Button(arrDet, SWT.PUSH);
								button.setText("Save");
								button.addMouseListener(new SaveMouseListener(true, rec.getName(), comp, parent.getText()));
								arrDet.layout();
								composite.layout();
							} catch (JAXBException e) {
								ErrorDialog.openError(getSite().getShell(), "Error reading text editor", null, new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Error reading text editor", null));
							}
						} else if (parent != null && parent.getText().equalsIgnoreCase("Fixed Array")) {
							try {
								FixedArray arr = util.getSelectedFixedArrayType(sel.getText(), FixedArray.class);
								FixedArrayTypesComposite comp = new FixedArrayTypesComposite();
								comp.setUtil(util);
								comp.setArray(arr);
								comp.setContainerName(containerName);
								comp.createPartControl(composite);
								Composite arrDet = comp.getRet();
								arrDet.setLayout(compLayout);
								arrDet.setLayoutData(gd);
								Label lbl = new Label(arrDet, SWT.BORDER_SOLID);
								lbl.setText("");
								Button button = new Button(arrDet, SWT.PUSH);
								button.setText("Save");
								button.addMouseListener(new SaveMouseListener(true, arr.getName(), comp, parent.getText()));
								arrDet.layout();
								composite.layout();
							} catch (JAXBException e) {
								ErrorDialog.openError(getSite().getShell(), "Error reading text editor", null, new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Error reading text editor", null));
							}
						} else if (parent != null && parent.getText().equalsIgnoreCase("Variant Record")) {
							try {
								VariantRecord vRec = util.getSelectedVarRecType(sel.getText(), VariantRecord.class);
								VarRecTypesComposite comp = new VarRecTypesComposite();
								comp.setUtil(util);
								comp.setContainerName(containerName);
								comp.setvRec(vRec);
								comp.createPartControl(composite);
								Composite arrDet = comp.getRet();
								arrDet.setLayout(compLayout);
								arrDet.setLayoutData(gd);
								composite.layout();
								Label lbl = new Label(arrDet, SWT.BORDER_SOLID);
								lbl.setText("");
								Button button = new Button(arrDet, SWT.PUSH);
								button.setText("Save");
								button.addMouseListener(new SaveMouseListener(true, vRec.getName(), comp, parent.getText()));
								arrDet.layout();
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
							if (parent != null && parent.getText().equalsIgnoreCase("Types")) {
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
		private String editorText;
		private String containerName;

		public SelectMouseListener(TreeItem selItem, String editorText, String containerName) {
			this.selItem = selItem;
			this.editorText = editorText;
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
				if (btn.getText().equalsIgnoreCase("Add Type")) {
					GridLayout compLayout = new GridLayout();
					compLayout.numColumns = 2;
					GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
					Composite child = null;
					Object com = null;
					if (!level2Item(selItem)) {
						ErrorDialog.openError(getSite().getShell(), "Please Select a level 2 Item for Add", null, new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, 0, "Please Select a level 2 Item for Add", null));
					} else {
						if (selItem != null && selItem.getText().equalsIgnoreCase("Array")) {
							Array arr = new Array();
							ArrayTypesComposite comp = new ArrayTypesComposite();
							comp.setUtil(util);
							comp.setArray(arr);
							comp.setContainerName(containerName);
							comp.createPartControl(composite);
							child = comp.getRet();
							com = comp;
						} else if (selItem != null && selItem.getText().equalsIgnoreCase("Constant")) {
							Constant cnst = new Constant();
							ConstantTypesComposite comp = new ConstantTypesComposite();
							comp.setUtil(util);
							comp.setCnst(cnst);
							comp.createPartControl(composite);
							comp.setContainerName(containerName);
							child = comp.getRet();
							com = comp;
						} else if (selItem != null && selItem.getText().equalsIgnoreCase("Enum")) {
							tech.ecoa.osets.model.types.Enum enm = new tech.ecoa.osets.model.types.Enum();
							EnumTypesComposite comp = new EnumTypesComposite();
							comp.setUtil(util);
							comp.setEnm(enm);
							comp.setContainerName(containerName);
							comp.createPartControl(composite);
							child = comp.getRet();
							com = comp;
						} else if (selItem != null && selItem.getText().equalsIgnoreCase("Simple")) {
							Simple simple = new Simple();
							SimpleTypesComposite comp = new SimpleTypesComposite();
							comp.setUtil(util);
							comp.setSimple(simple);
							comp.setContainerName(containerName);
							comp.createPartControl(composite);
							child = comp.getRet();
							com = comp;
						} else if (selItem != null && selItem.getText().equalsIgnoreCase("Record")) {
							Record rec = new Record();
							RecordTypesComposite comp = new RecordTypesComposite();
							comp.setUtil(util);
							comp.setContainerName(containerName);
							comp.setRecord(rec);
							comp.createPartControl(composite);
							child = comp.getRet();
							com = comp;
						} else if (selItem != null && selItem.getText().equalsIgnoreCase("Fixed Array")) {
							FixedArray arr = new FixedArray();
							FixedArrayTypesComposite comp = new FixedArrayTypesComposite();
							comp.setUtil(util);
							comp.setArray(arr);
							comp.setContainerName(containerName);
							comp.createPartControl(composite);
							child = comp.getRet();
							com = comp;
						} else if (selItem != null && selItem.getText().equalsIgnoreCase("Variant Record")) {
							VariantRecord vRec = new VariantRecord();
							VarRecTypesComposite comp = new VarRecTypesComposite();
							comp.setUtil(util);
							comp.setContainerName(containerName);
							comp.setvRec(vRec);
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
				} else if (btn.getText().equalsIgnoreCase("Remove Type")) {
					if (selItem == null || selItem.getParent() == null || selItem.getParent().getParent() == null) {
					} else {
						try {
							String tempText = util.removeItem(selItem, selItem.getParentItem().getText(), editorText);
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

		public TreeItem getSelItem() {
			return selItem;
		}

		public void setSelItem(TreeItem selItem) {
			this.selItem = selItem;
		}

		public String getEditorText() {
			return editorText;
		}

		public void setEditorText(String editorText) {
			this.editorText = editorText;
		}

		public String getContainerName() {
			return containerName;
		}

		public void setContainerName(String containerName) {
			this.containerName = containerName;
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
						String tempText = util.processAdd(isEdit, editName, comp, type, editor.getDocumentProvider().getDocument(getEditorInput()).get());
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
		createPage1();
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
