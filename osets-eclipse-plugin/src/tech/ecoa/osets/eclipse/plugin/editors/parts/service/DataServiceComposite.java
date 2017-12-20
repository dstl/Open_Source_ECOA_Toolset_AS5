/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.service;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tech.ecoa.osets.eclipse.plugin.util.TypesUtil;
import tech.ecoa.osets.model.intf.Data;

public class DataServiceComposite {
	private Text nameText;
	private Text commentText;
	private Composite ret;
	private Combo typeCombo;
	private String containerName;
	private Data data;

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
		nameText.setText((data.getName() == null) ? "" : data.getName());
		nameText.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Type:");
		typeCombo = new Combo(ret, SWT.NULL);
		ArrayList<String> types;
		try {
			TypesUtil util = TypesUtil.getInstance(containerName);
			util.setFileName("");
			types = util.getAllTypesForSimpleWizard();
		} catch (IOException | JAXBException e) {
			types = new ArrayList<String>();
		}
		for (String type : types)
			typeCombo.add(type);
		typeCombo.setText((data.getType() == null) ? "" : data.getType());
		typeCombo.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Comment:");
		commentText = new Text(ret, SWT.BORDER | SWT.SINGLE);
		commentText.setText((data.getComment() == null) ? "" : data.getComment());
		commentText.setLayoutData(gd);
	}

	public Data getProcessedData() {
		Data ret = new Data();
		ret.setName(nameText.getText());
		ret.setComment(commentText.getText());
		ret.setType(typeCombo.getText());
		return ret;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public Data getData() {
		return data;
	}

	public Composite getRet() {
		return ret;
	}

	public void setRet(Composite ret) {
		this.ret = ret;
	}
}
