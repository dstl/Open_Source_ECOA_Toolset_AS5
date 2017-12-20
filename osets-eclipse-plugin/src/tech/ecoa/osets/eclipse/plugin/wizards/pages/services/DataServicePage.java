/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.wizards.pages.services;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tech.ecoa.osets.eclipse.plugin.util.TypesUtil;
import tech.ecoa.osets.model.intf.Data;

public class DataServicePage extends WizardPage {
	private Text nameText;
	private Text commentText;
	private Composite root;
	private Combo typeCombo;
	private String containerName;

	public DataServicePage() {
		super("Data Service");
		setTitle("New ECOA Data Service");
		setDescription("This wizard creates a new Data Service XML Definition");
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
		label.setText("&Type:");
		typeCombo = new Combo(root, SWT.NULL);
		ArrayList<String> types;
		try {
			types = TypesUtil.getInstance(containerName).getAllTypesForSimpleWizard();
		} catch (IOException | JAXBException e) {
			types = new ArrayList<String>();
		}
		for (String type : types)
			typeCombo.add(type);
		typeCombo.setLayoutData(gd);
		label = new Label(root, SWT.NULL);
		label.setText("&Comment:");
		commentText = new Text(root, SWT.BORDER | SWT.SINGLE);
		commentText.setLayoutData(gd);
		setControl(root);
	}

	public Data getData() {
		Data data = new Data();
		data.setName(nameText.getText());
		data.setComment(commentText.getText());
		data.setType(typeCombo.getText());
		return data;
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
}
