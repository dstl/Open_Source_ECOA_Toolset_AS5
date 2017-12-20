/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.wizards.pages.types;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tech.ecoa.osets.eclipse.plugin.util.TypesUtil;
import tech.ecoa.osets.model.types.Array;

public class ArrayTypesPage extends WizardPage {
	private Text nameText;
	private Text commentText;
	private Combo typeCombo;
	private Text maxNumText;
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
	public ArrayTypesPage() {
		super("Array Type");
		setTitle("New ECOA Array Type");
		setDescription("This wizard creates a new Array Type XML Definition");
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
		label.setText("&Max Size:");
		maxNumText = new Text(root, SWT.BORDER | SWT.SINGLE);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		maxNumText.setLayoutData(gd);
		label = new Label(root, SWT.NULL);
		label.setText("&Type:");
		typeCombo = new Combo(root, SWT.NULL);
		typeCombo.setLayoutData(gd);
		label = new Label(root, SWT.NULL);
		label.setText("&Comment:");
		commentText = new Text(root, SWT.BORDER | SWT.SINGLE);
		commentText.setLayoutData(gd);

		setControl(root);
	}

	public void refreshTypes() {
		try {
			typeCombo.removeAll();
			ArrayList<String> types = util.getAllTypes();
			for (String type : types)
				typeCombo.add(type);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public Array getArray() {
		Array ret = new Array();
		ret.setComment((commentText.getText().length() == 0) ? null : commentText.getText());
		ret.setItemType(typeCombo.getText());
		ret.setMaxNumber(maxNumText.getText());
		ret.setName(nameText.getText());
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
