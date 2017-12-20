/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.wizards.pages.types;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import tech.ecoa.osets.eclipse.plugin.util.TypesUtil;
import tech.ecoa.osets.model.types.Enum;
import tech.ecoa.osets.model.types.EnumValue;

public class EnumTypesPage extends WizardPage {
	private Text nameText;
	private Combo typeCombo;
	private Text commentText;
	private TableViewer tabValues;
	private ArrayList<EnumValue> enumArr;
	private String containerName;
	private Composite root;
	private TypesUtil util;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param string
	 * 
	 * @param pageName
	 */
	public EnumTypesPage() {
		super("Enum Type");
		setTitle("New ECOA Enum Type");
		setDescription("This wizard creates a new Enum Type XML Definition");
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		root = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		root.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		Label label = new Label(root, SWT.NULL);
		label.setText("&Name:");
		nameText = new Text(root, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		nameText.setLayoutData(gd);
		label = new Label(root, SWT.NULL);
		label.setText("&Type:");
		typeCombo = new Combo(root, SWT.NULL);
		typeCombo.setLayoutData(gd);
		label = new Label(root, SWT.NULL);
		label.setText("&Comment:");
		commentText = new Text(root, SWT.BORDER | SWT.SINGLE);
		commentText.setLayoutData(gd);
		label = new Label(root, SWT.NULL);
		label.setText("&Values:");
		tabValues = new TableViewer(root, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tabValues.setContentProvider(ArrayContentProvider.getInstance());
		tabValues.setInput(getEnumArr());
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		tabValues.getControl().setLayoutData(gridData);
		TableViewerColumn nameValCol = new TableViewerColumn(tabValues, SWT.BORDER);
		TableColumn nameTabCol = nameValCol.getColumn();
		nameTabCol.setWidth(200);
		nameTabCol.setText("Name");
		nameValCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((EnumValue) element).getName();
			}
		});
		nameValCol.setEditingSupport(new CellEditingSupport(tabValues, "getName"));
		TableViewerColumn valNumValCol = new TableViewerColumn(tabValues, SWT.BORDER);
		TableColumn valNumTabCol = valNumValCol.getColumn();
		valNumTabCol.setWidth(200);
		valNumTabCol.setText("Val Num");
		valNumValCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((EnumValue) element).getValnum();
			}
		});
		valNumValCol.setEditingSupport(new CellEditingSupport(tabValues, "getValnum"));
		TableViewerColumn comValCol = new TableViewerColumn(tabValues, SWT.BORDER);
		TableColumn comTabCol = comValCol.getColumn();
		comTabCol.setWidth(200);
		comTabCol.setText("Comment");
		comValCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((EnumValue) element).getComment();
			}
		});
		comValCol.setEditingSupport(new CellEditingSupport(tabValues, "getComment"));
		final Table table = tabValues.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		label = new Label(root, SWT.NULL);
		label.setText("");
		Composite valComp = new Composite(root, SWT.NULL);
		GridLayout valLayout = new GridLayout();
		valComp.setLayout(valLayout);
		valLayout.numColumns = 3;
		valLayout.verticalSpacing = 9;
		valLayout.horizontalSpacing = 9;
		GridData valGd = new GridData();
		valGd.verticalAlignment = GridData.FILL;
		valGd.grabExcessHorizontalSpace = true;
		valGd.grabExcessVerticalSpace = true;
		valGd.horizontalAlignment = GridData.FILL;
		Button button = new Button(valComp, SWT.PUSH);
		button.setText("Add");
		button.addMouseListener(new EnumTypeMouseListener());
		button.setLayoutData(valGd);
		button = new Button(valComp, SWT.PUSH);
		button.setText("Remove");
		button.addMouseListener(new EnumTypeMouseListener());
		button.setLayoutData(valGd);
		button = new Button(valComp, SWT.PUSH);
		button.setText("Clear");
		button.addMouseListener(new EnumTypeMouseListener());
		button.setLayoutData(valGd);
		setControl(root);
	}

	public void refreshTypes() {
		typeCombo.removeAll();
		ArrayList<String> types = util.getAllBasicTypes();
		for (String type : types)
			typeCombo.add(type);
	}

	private class EnumTypeMouseListener implements MouseListener {

		@Override
		public void mouseDoubleClick(MouseEvent e) {
		}

		@Override
		public void mouseDown(MouseEvent e) {
		}

		@Override
		public void mouseUp(MouseEvent e) {
			if (((Button) e.getSource()).getText().equalsIgnoreCase("Add")) {
				EnumValue val = new EnumValue();
				val.setName("");
				val.setComment("");
				val.setValnum("");
				getEnumArr().add(val);
				tabValues.refresh();
			} else if (((Button) e.getSource()).getText().equalsIgnoreCase("Remove")) {
				int[] items = tabValues.getTable().getSelectionIndices();
				ArrayList<EnumValue> rem = new ArrayList<EnumValue>();
				for (int item : items) {
					rem.add(getEnumArr().get(item));
				}
				getEnumArr().removeAll(rem);
				tabValues.refresh();
			} else if (((Button) e.getSource()).getText().equalsIgnoreCase("Clear")) {
				getEnumArr().clear();
				tabValues.refresh();
			}
		}

	}

	private class CellEditingSupport extends EditingSupport {
		private final TableViewer viewer;
		private final CellEditor editor;
		private final String fName;

		public CellEditingSupport(TableViewer viewer, String fName) {
			super(viewer);
			this.viewer = viewer;
			this.fName = fName;
			this.editor = new TextCellEditor(viewer.getTable());
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			EnumValue val = (EnumValue) element;
			try {
				Method getter = val.getClass().getMethod(fName, null);
				Object data = getter.invoke(val, null);
				return data;
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
				return "";
			}
		}

		@Override
		protected void setValue(Object element, Object value) {
			EnumValue val = (EnumValue) element;
			try {
				Method setter = val.getClass().getMethod(StringUtils.replaceFirst(fName, "get", "set"), String.class);
				setter.invoke(element, value);
			} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
			}
			viewer.update(element, null);
		}

	}

	public String getTypeName() {
		return nameText.getText();
	}

	public String getTypeComment() {
		return commentText.getText();
	}

	public String getTypeType() {
		return typeCombo.getText();
	}

	public ArrayList<EnumValue> getEnumArr() {
		if (enumArr == null) {
			enumArr = new ArrayList<EnumValue>();
		}
		return enumArr;
	}

	public void setEnumArr(ArrayList<EnumValue> enumArr) {
		this.enumArr = enumArr;
	}

	public Enum getEnum() {
		Enum ret = new Enum();
		ret.setComment((commentText.getText().length() == 0) ? null : commentText.getText());
		ret.setName(nameText.getText());
		ret.setType(typeCombo.getText());
		ArrayList<EnumValue> vals = new ArrayList<EnumValue>();
		for (EnumValue val : getEnumArr()) {
			vals.add(val);
		}
		ret.getValue().addAll(vals);
		return ret;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public Composite getRoot() {
		return root;
	}

	public void setRoot(Composite root) {
		this.root = root;
	}

	public TypesUtil getUtil() {
		return util;
	}

	public void setUtil(TypesUtil util) {
		this.util = util;
	}
}
