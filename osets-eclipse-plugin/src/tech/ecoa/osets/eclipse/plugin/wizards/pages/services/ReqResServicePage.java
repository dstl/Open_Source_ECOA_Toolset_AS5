/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.wizards.pages.services;

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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import tech.ecoa.osets.eclipse.plugin.util.TypesUtil;
import tech.ecoa.osets.model.intf.Parameter;
import tech.ecoa.osets.model.intf.RequestResponse;

public class ReqResServicePage extends WizardPage {
	private Text nameText;
	private Text commentText;
	private Composite root;
	private TableViewer iTabValues;
	private TableViewer uTabValues;
	private ArrayList<Parameter> iParamArr;
	private ArrayList<Parameter> uParamArr;
	private String containerName;

	public ReqResServicePage() {
		super("Request Response Service");
		setTitle("New ECOA Request Response Service");
		setDescription("This wizard creates a new Request Response Service XML Definition");
	}

	@Override
	public void createControl(Composite parent) {
		root = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		root.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		Label label = new Label(root, SWT.NULL);
		label.setText("&Name:");
		nameText = new Text(root, SWT.BORDER | SWT.SINGLE);
		nameText.setLayoutData(gd);
		label = new Label(root, SWT.NULL);
		label.setText("&Comment:");
		commentText = new Text(root, SWT.BORDER | SWT.SINGLE);
		commentText.setLayoutData(gd);
		GridLayout valLayout = new GridLayout();
		GridData valGd = new GridData();
		valGd.verticalAlignment = GridData.FILL;
		valGd.grabExcessHorizontalSpace = true;
		valGd.grabExcessVerticalSpace = true;
		valGd.horizontalAlignment = GridData.FILL;
		label = new Label(root, SWT.NULL);
		label.setText("&Input:");
		label = new Label(root, SWT.NULL);
		iTabValues = new TableViewer(root, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		iTabValues.setContentProvider(ArrayContentProvider.getInstance());
		iTabValues.setInput(getiParamArr());
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		iTabValues.getControl().setLayoutData(gridData);
		TableViewerColumn nameValCol = new TableViewerColumn(iTabValues, SWT.BORDER);
		TableColumn nameTabCol = nameValCol.getColumn();
		nameTabCol.setWidth(200);
		nameTabCol.setText("Name");
		nameValCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Parameter) element).getName();
			}
		});
		nameValCol.setEditingSupport(new TextCellEditingSupport(iTabValues, "getName"));
		TableViewerColumn valNumValCol = new TableViewerColumn(iTabValues, SWT.BORDER);
		TableColumn valNumTabCol = valNumValCol.getColumn();
		valNumTabCol.setWidth(200);
		valNumTabCol.setText("Type");
		valNumValCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Parameter) element).getType();
			}
		});
		valNumValCol.setEditingSupport(new ComboCellEditingSupport(iTabValues));
		final Table iTable = iTabValues.getTable();
		iTable.setHeaderVisible(true);
		iTable.setLinesVisible(true);
		label = new Label(root, SWT.NULL);
		label.setText("");
		Composite iValComp = new Composite(root, SWT.NULL);
		iValComp.setLayout(valLayout);
		Button iButton = new Button(iValComp, SWT.PUSH);
		iButton.setText("Add");
		iButton.addMouseListener(new EventMouseListener(1));
		iButton.setLayoutData(valGd);
		iButton = new Button(iValComp, SWT.PUSH);
		iButton.setText("Remove");
		iButton.addMouseListener(new EventMouseListener(1));
		iButton.setLayoutData(valGd);
		iButton = new Button(iValComp, SWT.PUSH);
		iButton.setText("Clear");
		iButton.addMouseListener(new EventMouseListener(1));
		iButton.setLayoutData(valGd);
		label = new Label(root, SWT.NULL);
		label.setText("&Output:");
		label = new Label(root, SWT.NULL);
		uTabValues = new TableViewer(root, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		uTabValues.setContentProvider(ArrayContentProvider.getInstance());
		uTabValues.setInput(getuParamArr());
		uTabValues.getControl().setLayoutData(gridData);
		nameValCol = new TableViewerColumn(uTabValues, SWT.BORDER);
		nameTabCol = nameValCol.getColumn();
		nameTabCol.setWidth(200);
		nameTabCol.setText("Name");
		nameValCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Parameter) element).getName();
			}
		});
		nameValCol.setEditingSupport(new TextCellEditingSupport(uTabValues, "getName"));
		valNumValCol = new TableViewerColumn(uTabValues, SWT.BORDER);
		valNumTabCol = valNumValCol.getColumn();
		valNumTabCol.setWidth(200);
		valNumTabCol.setText("Type");
		valNumValCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Parameter) element).getType();
			}
		});
		valNumValCol.setEditingSupport(new ComboCellEditingSupport(uTabValues));
		final Table uTable = uTabValues.getTable();
		uTable.setHeaderVisible(true);
		uTable.setLinesVisible(true);
		label = new Label(root, SWT.NULL);
		label.setText("");
		Composite uValComp = new Composite(root, SWT.NULL);
		uValComp.setLayout(valLayout);
		valLayout.numColumns = 3;
		valLayout.verticalSpacing = 9;
		valLayout.horizontalSpacing = 9;
		Button uButton = new Button(uValComp, SWT.PUSH);
		uButton.setText("Add");
		uButton.addMouseListener(new EventMouseListener(2));
		uButton.setLayoutData(valGd);
		uButton = new Button(uValComp, SWT.PUSH);
		uButton.setText("Remove");
		uButton.addMouseListener(new EventMouseListener(2));
		uButton.setLayoutData(valGd);
		uButton = new Button(uValComp, SWT.PUSH);
		uButton.setText("Clear");
		uButton.addMouseListener(new EventMouseListener(2));
		uButton.setLayoutData(valGd);
		setControl(root);
	}

	private class EventMouseListener implements MouseListener {
		private int type;

		public EventMouseListener(int type) {
			super();
			this.type = type;
		}

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
				switch (type) {
				case 1:
					getiParamArr().add(val);
					iTabValues.refresh();
					break;
				case 2:
					getuParamArr().add(val);
					uTabValues.refresh();
					break;
				}
			} else if (((Button) e.getSource()).getText().equalsIgnoreCase("Remove")) {
				int[] items;
				ArrayList<Parameter> rem = new ArrayList<Parameter>();
				switch (type) {
				case 1:
					items = iTabValues.getTable().getSelectionIndices();
					for (int item : items) {
						rem.add(getiParamArr().get(item));
					}
					getiParamArr().removeAll(rem);
					iTabValues.refresh();
					break;
				case 2:
					items = uTabValues.getTable().getSelectionIndices();
					for (int item : items) {
						rem.add(getuParamArr().get(item));
					}
					getuParamArr().removeAll(rem);
					uTabValues.refresh();
					break;
				}
			} else if (((Button) e.getSource()).getText().equalsIgnoreCase("Clear")) {
				switch (type) {
				case 1:
					getiParamArr().clear();
					iTabValues.refresh();
					break;
				case 2:
					getuParamArr().clear();
					uTabValues.refresh();
					break;
				}
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
				types = TypesUtil.getInstance(containerName).getAllTypes();
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

	public RequestResponse getReqRes() {
		RequestResponse reqRes = new RequestResponse();
		reqRes.getInput().addAll(getiParamArr());
		reqRes.getOutput().addAll(getuParamArr());
		reqRes.setName(nameText.getText());
		reqRes.setComment(commentText.getText());
		return reqRes;
	}

	public Composite getRoot() {
		return root;
	}

	public void setRoot(Composite root) {
		this.root = root;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public ArrayList<Parameter> getiParamArr() {
		if (iParamArr == null)
			iParamArr = new ArrayList<Parameter>();
		return iParamArr;
	}

	public void setiParamArr(ArrayList<Parameter> iParamArr) {
		this.iParamArr = iParamArr;
	}

	public ArrayList<Parameter> getuParamArr() {
		if (uParamArr == null)
			uParamArr = new ArrayList<Parameter>();
		return uParamArr;
	}

	public void setuParamArr(ArrayList<Parameter> uParamArr) {
		this.uParamArr = uParamArr;
	}
}
