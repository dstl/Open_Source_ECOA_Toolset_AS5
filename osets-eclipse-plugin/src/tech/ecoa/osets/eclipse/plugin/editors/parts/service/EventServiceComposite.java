/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.service;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
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
import tech.ecoa.osets.model.intf.EEventDirection;
import tech.ecoa.osets.model.intf.Event;
import tech.ecoa.osets.model.intf.Parameter;

public class EventServiceComposite {
	private Text nameText;
	private Text commentText;
	private Composite ret;
	private Combo dirCombo;
	private TableViewer tabValues;
	private ArrayList<Parameter> paramArr;
	private String containerName;
	private Event event;
	private TypesUtil util;

	public void createPartControl(Composite parent) {
		ret = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		ret.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		Label label = new Label(ret, SWT.NULL);
		label.setText("&Name:");
		nameText = new Text(ret, SWT.BORDER | SWT.SINGLE);
		nameText.setText((event.getName() == null) ? "" : event.getName());
		nameText.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Direction:");
		dirCombo = new Combo(ret, SWT.NULL);
		dirCombo.add(EEventDirection.RECEIVED_BY_PROVIDER.name());
		dirCombo.add(EEventDirection.SENT_BY_PROVIDER.name());
		dirCombo.setText((event.getDirection() == null) ? "" : event.getDirection().name());
		dirCombo.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Comment:");
		commentText = new Text(ret, SWT.BORDER | SWT.SINGLE);
		commentText.setText((event.getComment() == null) ? "" : event.getComment());
		commentText.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Input:");
		label = new Label(ret, SWT.NULL);
		tabValues = new TableViewer(ret, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tabValues.setContentProvider(ArrayContentProvider.getInstance());
		tabValues.setInput(getParamArr());
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
				return ((Parameter) element).getName();
			}
		});
		nameValCol.setEditingSupport(new TextCellEditingSupport(tabValues, "getName"));
		TableViewerColumn valNumValCol = new TableViewerColumn(tabValues, SWT.BORDER);
		TableColumn valNumTabCol = valNumValCol.getColumn();
		valNumTabCol.setWidth(200);
		valNumTabCol.setText("Type");
		valNumValCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Parameter) element).getType();
			}
		});
		valNumValCol.setEditingSupport(new ComboCellEditingSupport(tabValues));
		final Table table = tabValues.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		label = new Label(ret, SWT.NULL);
		label.setText("");
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
		button.addMouseListener(new EventMouseListener());
		button.setLayoutData(valGd);
		button = new Button(valComp, SWT.PUSH);
		button.setText("Remove");
		button.addMouseListener(new EventMouseListener());
		button.setLayoutData(valGd);
		button = new Button(valComp, SWT.PUSH);
		button.setText("Clear");
		button.addMouseListener(new EventMouseListener());
		button.setLayoutData(valGd);
	}

	private class EventMouseListener implements MouseListener {

		@Override
		public void mouseDoubleClick(MouseEvent e) {
		}

		@Override
		public void mouseDown(MouseEvent e) {
		}

		@Override
		public void mouseUp(MouseEvent e) {
			if (((Button) e.getSource()).getText().equalsIgnoreCase("Add")) {
				Parameter val = new Parameter();
				val.setName("");
				val.setType("");
				getParamArr().add(val);
				tabValues.refresh();
			} else if (((Button) e.getSource()).getText().equalsIgnoreCase("Remove")) {
				int[] items = tabValues.getTable().getSelectionIndices();
				ArrayList<Parameter> rem = new ArrayList<Parameter>();
				for (int item : items) {
					rem.add(getParamArr().get(item));
				}
				getParamArr().removeAll(rem);
				tabValues.refresh();
			} else if (((Button) e.getSource()).getText().equalsIgnoreCase("Clear")) {
				getParamArr().clear();
				tabValues.refresh();
			}
		}

	}

	private class TextCellEditingSupport extends EditingSupport {
		private final TableViewer viewer;
		private final CellEditor editor;
		private final String fName;

		public TextCellEditingSupport(TableViewer viewer, String fName) {
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
			Parameter val = (Parameter) element;
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
			Parameter val = (Parameter) element;
			try {
				Method setter = val.getClass().getMethod(StringUtils.replaceFirst(fName, "get", "set"), String.class);
				setter.invoke(element, value);
			} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
			}
			viewer.update(element, null);
		}

	}

	private class ComboCellEditingSupport extends EditingSupport {
		private final TableViewer viewer;
		private ArrayList<String> types;

		public ComboCellEditingSupport(TableViewer viewer) {
			super(viewer);
			this.viewer = viewer;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			try {
				util.setContainerName(containerName);
				util.setFileName("");
				types = util.getAllTypes();
				String[] opts = new String[types.size()];
				int i = 0;
				for (String type : types) {
					opts[i] = type;
					i++;
				}
				return new ComboBoxCellEditor(viewer.getTable(), opts);
			} catch (IOException | JAXBException e) {
				return new TextCellEditor(viewer.getTable());
			}
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected Object getValue(Object element) {
			Parameter val = (Parameter) element;
			int i = 0;
			for (String type : types) {
				if (type.equalsIgnoreCase(val.getType()))
					return i;
				i++;
			}
			return 0;
		}

		@Override
		protected void setValue(Object element, Object value) {
			Parameter val = (Parameter) element;
			val.setType(types.get((Integer) value));
			viewer.update(element, null);
		}
	}

	public ArrayList<Parameter> getParamArr() {
		if (paramArr == null) {
			paramArr = new ArrayList<Parameter>();
			paramArr.addAll(event.getInput());
		}
		return paramArr;
	}

	public void setParamArr(ArrayList<Parameter> paramArr) {
		this.paramArr = paramArr;
	}

	public Event getProcessedEvent() {
		Event event = new Event();
		event.setDirection(EEventDirection.fromValue(dirCombo.getText()));
		event.getInput().addAll(getParamArr());
		event.setName(nameText.getText());
		event.setComment(commentText.getText());
		return event;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Event getEvent() {
		return event;
	}

	public Composite getRet() {
		return ret;
	}

	public void setRet(Composite ret) {
		this.ret = ret;
	}

	public TypesUtil getUtil() {
		return util;
	}

	public void setUtil(TypesUtil util) {
		this.util = util;
	}

}
