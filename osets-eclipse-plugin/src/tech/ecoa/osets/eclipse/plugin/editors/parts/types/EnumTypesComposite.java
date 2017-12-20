/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.types;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
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

public class EnumTypesComposite {

	private Text nameText;
	private Combo typeCombo;
	private Text commentText;
	private TableViewer tabValues;
	private ArrayList<EnumValue> enumArr;
	private String containerName;
	private Enum enm;
	private Composite ret;
	private TypesUtil util;

	public void createPartControl(Composite parent) {
		ret = new Composite(parent, SWT.BORDER_SOLID);
		GridLayout layout = new GridLayout();
		ret.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		Label label = new Label(ret, SWT.NULL);
		label.setText("&Name:");
		nameText = new Text(ret, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		nameText.setLayoutData(gd);
		nameText.setText((enm.getName() != null) ? enm.getName() : "");
		label = new Label(ret, SWT.NULL);
		label.setText("&Type:");
		typeCombo = new Combo(ret, SWT.NULL);
		refreshTypes();
		typeCombo.setLayoutData(gd);
		typeCombo.setText((enm.getType() != null) ? enm.getType() : "");
		label = new Label(ret, SWT.NULL);
		label.setText("&Comment:");
		commentText = new Text(ret, SWT.BORDER | SWT.SINGLE);
		commentText.setLayoutData(gd);
		commentText.setText((enm.getComment() != null) ? enm.getComment() : "");
		label = new Label(ret, SWT.NULL);
		label.setText("&Values:");
		tabValues = new TableViewer(ret, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tabValues.setContentProvider(ArrayContentProvider.getInstance());
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
		label = new Label(ret, SWT.NULL);
		label.setText("");
		tabValues.setInput(getEnumArr());
		ret.layout();
		Composite valComp = new Composite(ret, SWT.NULL);
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
		ret.setVisible(true);
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
			enumArr.addAll(enm.getValue());
		}
		return enumArr;
	}

	public void setEnumArr(ArrayList<EnumValue> enumArr) {
		this.enumArr = enumArr;
	}

	public Composite getRet() {
		return ret;
	}

	public void setRet(Composite ret) {
		this.ret = ret;
	}

	public tech.ecoa.osets.model.types.Enum getEnm() {
		return enm;
	}

	public tech.ecoa.osets.model.types.Enum getProcessedEnum() {
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

	public void setEnm(tech.ecoa.osets.model.types.Enum enm) {
		this.enm = enm;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public TypesUtil getUtil() {
		return util;
	}

	public void setUtil(TypesUtil util) {
		this.util = util;
	}
}
