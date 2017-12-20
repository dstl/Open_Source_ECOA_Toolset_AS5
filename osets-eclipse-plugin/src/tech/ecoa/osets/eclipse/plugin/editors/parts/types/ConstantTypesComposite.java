/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.types;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tech.ecoa.osets.eclipse.plugin.util.TypesUtil;
import tech.ecoa.osets.model.types.Constant;

public class ConstantTypesComposite {

	private Text nameText;
	private Text valueText;
	private Text commentText;
	private Combo typeCombo;
	private String containerName;
	private Constant cnst;
	private Composite ret;
	private TypesUtil util;

	/**
	 * @see IDialogPage#createPartControl(Composite)
	 */
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
		nameText.setText((cnst.getName() != null) ? cnst.getName() : "");
		nameText.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Value:");
		valueText = new Text(ret, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		valueText.setText((cnst.getValue() != null) ? cnst.getValue() : "");
		valueText.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Type:");
		typeCombo = new Combo(ret, SWT.NULL);
		refreshTypes();
		typeCombo.setText((cnst.getType() != null) ? cnst.getType() : "");
		typeCombo.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Comment:");
		commentText = new Text(ret, SWT.BORDER | SWT.SINGLE);
		commentText.setText((cnst.getComment() != null) ? cnst.getComment() : "");
		commentText.setLayoutData(gd);
		ret.setVisible(true);
	}

	public void refreshTypes() {
		typeCombo.removeAll();
		ArrayList<String> types = util.getAllBasicTypes();
		for (String type : types)
			typeCombo.add(type);
	}

	public Constant getProcessedConstant() {
		Constant ret = new Constant();
		ret.setComment((commentText.getText().length() == 0) ? null : commentText.getText());
		ret.setName(nameText.getText());
		ret.setType(typeCombo.getText());
		ret.setValue(valueText.getText());
		return ret;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public Composite getRet() {
		return ret;
	}

	public void setRet(Composite ret) {
		this.ret = ret;
	}

	public Constant getCnst() {
		return cnst;
	}

	public void setCnst(Constant cnst) {
		this.cnst = cnst;
	}

	public TypesUtil getUtil() {
		return util;
	}

	public void setUtil(TypesUtil util) {
		this.util = util;
	}
}
