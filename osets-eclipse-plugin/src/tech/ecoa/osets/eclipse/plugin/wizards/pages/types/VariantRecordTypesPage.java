/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.wizards.pages.types;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogPage;
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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import tech.ecoa.osets.eclipse.plugin.util.TypesUtil;
import tech.ecoa.osets.model.types.Field;
import tech.ecoa.osets.model.types.Union;
import tech.ecoa.osets.model.types.VariantRecord;

public class VariantRecordTypesPage extends WizardPage {
	private Text nameText;
	private Combo typeCombo;
	private Text selNameText;
	private Text commentText;
	private TableViewer fTabValues;
	private TableViewer uTabValues;
	private ArrayList<Field> fArr;
	private ArrayList<Union> uArr;
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
	public VariantRecordTypesPage() {
		super("Variant Record Type");
		setTitle("New ECOA Variant Record Type");
		setDescription("This wizard creates a new Variant Record Type XML Definition");
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
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		GridLayout valLayout = new GridLayout();
		valLayout.numColumns = 3;
		valLayout.verticalSpacing = 9;
		valLayout.horizontalSpacing = 9;
		GridData valGd = new GridData();
		valGd.verticalAlignment = GridData.FILL;
		valGd.grabExcessHorizontalSpace = true;
		valGd.grabExcessVerticalSpace = true;
		valGd.horizontalAlignment = GridData.FILL;
		Label label = new Label(root, SWT.NULL);
		label.setText("&Name:");
		nameText = new Text(root, SWT.BORDER | SWT.SINGLE);
		nameText.setLayoutData(gd);
		label = new Label(root, SWT.NULL);
		label.setText("&Select Name:");
		selNameText = new Text(root, SWT.BORDER | SWT.SINGLE);
		selNameText.setLayoutData(gd);
		label = new Label(root, SWT.NULL);
		label.setText("Select T&ype:");
		typeCombo = new Combo(root, SWT.NULL);
		typeCombo.setLayoutData(gd);
		label = new Label(root, SWT.NULL);
		label.setText("&Comment:");
		commentText = new Text(root, SWT.BORDER | SWT.SINGLE);
		commentText.setLayoutData(gd);
		label = new Label(root, SWT.NULL);
		label.setText("&Values:");

		fTabValues = new TableViewer(root, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		fTabValues.setContentProvider(ArrayContentProvider.getInstance());
		fTabValues.setInput(getfArr());
		fTabValues.getControl().setLayoutData(gridData);
		TableViewerColumn fNameValCol = new TableViewerColumn(fTabValues, SWT.BORDER);
		TableColumn nameTabCol = fNameValCol.getColumn();
		nameTabCol.setWidth(200);
		nameTabCol.setText("Name");
		fNameValCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Field) element).getName();
			}
		});
		fNameValCol.setEditingSupport(new TextCellEditingSupport(fTabValues, "getName", 0));
		TableViewerColumn fTypeValCol = new TableViewerColumn(fTabValues, SWT.BORDER);
		TableColumn fTypeTabCol = fTypeValCol.getColumn();
		fTypeTabCol.setWidth(200);
		fTypeTabCol.setText("Type");
		fTypeValCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Field) element).getType();
			}
		});
		fTypeValCol.setEditingSupport(new ComboCellEditingSupport(fTabValues, 0));
		TableViewerColumn fComValCol = new TableViewerColumn(fTabValues, SWT.BORDER);
		TableColumn fComTabCol = fComValCol.getColumn();
		fComTabCol.setWidth(200);
		fComTabCol.setText("Comment");
		fComValCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Field) element).getComment();
			}
		});
		fComValCol.setEditingSupport(new TextCellEditingSupport(fTabValues, "getComment", 0));
		final Table fTable = fTabValues.getTable();
		fTable.setHeaderVisible(true);
		fTable.setLinesVisible(true);

		label = new Label(root, SWT.NULL);
		label.setText("");
		Composite iValComp = new Composite(root, SWT.NULL);
		iValComp.setLayout(valLayout);
		Button button = new Button(iValComp, SWT.PUSH);
		button.setText("Add");
		button.addMouseListener(new VarRecTypeMouseListener(1));
		button.setLayoutData(valGd);
		button = new Button(iValComp, SWT.PUSH);
		button.setText("Remove");
		button.addMouseListener(new VarRecTypeMouseListener(1));
		button.setLayoutData(valGd);
		button = new Button(iValComp, SWT.PUSH);
		button.setText("Clear");
		button.addMouseListener(new VarRecTypeMouseListener(1));
		button.setLayoutData(valGd);

		uTabValues = new TableViewer(root, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		uTabValues.setContentProvider(ArrayContentProvider.getInstance());
		uTabValues.setInput(getuArr());
		uTabValues.getControl().setLayoutData(gridData);
		TableViewerColumn uNameValCol = new TableViewerColumn(uTabValues, SWT.BORDER);
		TableColumn uNameTabCol = uNameValCol.getColumn();
		uNameTabCol.setWidth(150);
		uNameTabCol.setText("Name");
		uNameValCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Union) element).getName();
			}
		});
		uNameValCol.setEditingSupport(new TextCellEditingSupport(uTabValues, "getName", 1));
		TableViewerColumn uTypeValCol = new TableViewerColumn(uTabValues, SWT.BORDER);
		TableColumn uTypeTabCol = uTypeValCol.getColumn();
		uTypeTabCol.setWidth(150);
		uTypeTabCol.setText("Type");
		uTypeValCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Union) element).getType();
			}
		});
		uTypeValCol.setEditingSupport(new ComboCellEditingSupport(uTabValues, 1));
		TableViewerColumn uWhenValCol = new TableViewerColumn(uTabValues, SWT.BORDER);
		TableColumn uWhenTabCol = uWhenValCol.getColumn();
		uWhenTabCol.setWidth(150);
		uWhenTabCol.setText("When");
		uWhenValCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Union) element).getWhen();
			}
		});
		uWhenValCol.setEditingSupport(new TextCellEditingSupport(uTabValues, "getWhen", 1));
		TableViewerColumn uComValCol = new TableViewerColumn(uTabValues, SWT.BORDER);
		TableColumn uComTabCol = uComValCol.getColumn();
		uComTabCol.setWidth(150);
		uComTabCol.setText("Comment");
		uComValCol.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Union) element).getComment();
			}
		});
		uComValCol.setEditingSupport(new TextCellEditingSupport(uTabValues, "getComment", 1));
		final Table uTable = uTabValues.getTable();
		uTable.setHeaderVisible(true);
		uTable.setLinesVisible(true);

		label = new Label(root, SWT.NULL);
		label.setText("");
		Composite uValComp = new Composite(root, SWT.NULL);
		valLayout.numColumns = 3;
		valLayout.verticalSpacing = 9;
		valLayout.horizontalSpacing = 9;
		uValComp.setLayout(valLayout);
		Button uButton = new Button(uValComp, SWT.PUSH);
		uButton.setText("Add");
		uButton.addMouseListener(new VarRecTypeMouseListener(2));
		uButton.setLayoutData(valGd);
		uButton = new Button(uValComp, SWT.PUSH);
		uButton.setText("Remove");
		uButton.addMouseListener(new VarRecTypeMouseListener(2));
		uButton.setLayoutData(valGd);
		uButton = new Button(uValComp, SWT.PUSH);
		uButton.setText("Clear");
		uButton.addMouseListener(new VarRecTypeMouseListener(2));
		uButton.setLayoutData(valGd);

		setControl(root);
	}

	public void refreshTypes() {
		try {
			typeCombo.removeAll();
			ArrayList<String> types = util.getAllTypesForSimpleWizard();
			for (String type : types)
				typeCombo.add(type);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private class VarRecTypeMouseListener implements MouseListener {
		private int type;

		public VarRecTypeMouseListener(int type) {
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
				switch (type) {
				case 1:
					Field fVal = new Field();
					fVal.setName("");
					fVal.setComment("");
					fVal.setType("");
					getfArr().add(fVal);
					break;
				case 2:
					Union uVal = new Union();
					uVal.setName("");
					uVal.setComment("");
					uVal.setType("");
					uVal.setWhen("");
					getuArr().add(uVal);
					break;
				}
			} else if (((Button) e.getSource()).getText().equalsIgnoreCase("Remove")) {
				int[] items = fTabValues.getTable().getSelectionIndices();
				switch (type) {
				case 1:
					ArrayList<Field> fRem = new ArrayList<Field>();
					for (int item : items) {
						fRem.add(getfArr().get(item));
					}
					getfArr().removeAll(fRem);
					break;
				case 2:
					ArrayList<Union> uRem = new ArrayList<Union>();
					for (int item : items) {
						uRem.add(getuArr().get(item));
					}
					getuArr().removeAll(uRem);
					break;
				}
			} else if (((Button) e.getSource()).getText().equalsIgnoreCase("Clear")) {
				switch (type) {
				case 1:
					getfArr().clear();
					break;
				case 2:
					getuArr().clear();
					break;
				}
			}
			fTabValues.refresh();
			uTabValues.refresh();
		}

	}

	private class TextCellEditingSupport extends EditingSupport {
		private final TableViewer viewer;
		private final CellEditor editor;
		private final String fName;
		private final int type;

		public TextCellEditingSupport(TableViewer viewer, String fName, int type) {
			super(viewer);
			this.viewer = viewer;
			this.fName = fName;
			this.type = type;
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
			switch (type) {
			case 0:
				Field fVal = (Field) element;
				try {
					Method getter = fVal.getClass().getMethod(fName, null);
					Object data = getter.invoke(fVal, null);
					return data;
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
					return "";
				}
			case 1:
				Union uVal = (Union) element;
				try {
					Method getter = uVal.getClass().getMethod(fName, null);
					Object data = getter.invoke(uVal, null);
					return data;
				} catch (IllegalArgumentException | IllegalAccessException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
					return "";
				}
			}
			return "";
		}

		@Override
		protected void setValue(Object element, Object value) {
			switch (type) {
			case 0:
				Field fVal = (Field) element;
				try {
					Method setter = fVal.getClass().getMethod(StringUtils.replaceFirst(fName, "get", "set"), String.class);
					setter.invoke(element, value);
				} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
				}
				break;
			case 1:
				Union uVal = (Union) element;
				try {
					Method setter = uVal.getClass().getMethod(StringUtils.replaceFirst(fName, "get", "set"), String.class);
					setter.invoke(element, value);
				} catch (IllegalArgumentException | IllegalAccessException | SecurityException | NoSuchMethodException | InvocationTargetException e) {
				}
				break;
			}
			viewer.update(element, null);
		}

	}

	private class ComboCellEditingSupport extends EditingSupport {
		private final TableViewer viewer;
		private ArrayList<String> types;
		private int type;

		public ComboCellEditingSupport(TableViewer viewer, int type) {
			super(viewer);
			this.viewer = viewer;
			this.type = type;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			try {
				switch (type) {
				case 0:
					types = util.getAllTypes();
					break;
				case 1:
					types = util.getAllTypes();
					break;
				}
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
			switch (type) {
			case 0:
				Field fVal = (Field) element;
				int i = 0;
				for (String type : types) {
					if (type.equalsIgnoreCase(fVal.getType()))
						return i;
					i++;
				}
				break;
			case 1:
				Union uVal = (Union) element;
				i = 0;
				for (String type : types) {
					if (type.equalsIgnoreCase(uVal.getType()))
						return i;
					i++;
				}
				break;
			}
			return 0;
		}

		@Override
		protected void setValue(Object element, Object value) {
			switch (type) {
			case 0:
				Field fVal = (Field) element;
				fVal.setType(types.get((Integer) value));
				break;
			case 1:
				Union uVal = (Union) element;
				uVal.setType(types.get((Integer) value));
				break;
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

	public ArrayList<Field> getfArr() {
		if (fArr == null) {
			fArr = new ArrayList<Field>();
		}
		return fArr;
	}

	public void setfArr(ArrayList<Field> fArr) {
		this.fArr = fArr;
	}

	public ArrayList<Union> getuArr() {
		if (uArr == null) {
			uArr = new ArrayList<Union>();
		}
		return uArr;
	}

	public void setuArr(ArrayList<Union> uArr) {
		this.uArr = uArr;
	}

	public VariantRecord getVarRec() {
		VariantRecord ret = new VariantRecord();
		ret.setComment((commentText.getText().length() == 0) ? null : commentText.getText());
		ret.setName(nameText.getText());
		ret.setSelectName(selNameText.getText());
		ret.setSelectType(typeCombo.getText());
		ret.getField().addAll(getfArr());
		ret.getUnion().addAll(getuArr());
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
