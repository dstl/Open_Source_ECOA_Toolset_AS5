/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.wizards.pages.cdef;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tech.ecoa.osets.eclipse.plugin.util.ServicesUtil;
import tech.ecoa.osets.model.cdef.ComponentType;
import tech.ecoa.osets.model.cdef.EcoaInterface;
import tech.ecoa.osets.model.cdef.EcoaInterfaceElement;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (cdef).
 */

public class CompDefServicePage extends WizardPage {
	private Text nameText;
	private Composite root;
	private String containerName;
	private Combo svcCombo;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public CompDefServicePage() {
		super("Server Component");
		setTitle("New ECOA Server Component");
		setDescription("This wizard creates a new Server Component XML Definition");
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
		Label label = new Label(root, SWT.NULL);
		label.setText("&Name:");
		nameText = new Text(root, SWT.BORDER | SWT.SINGLE);
		nameText.setLayoutData(gd);
		label = new Label(root, SWT.NULL);
		label.setText("&Type:");
		svcCombo = new Combo(root, SWT.NULL);
		ArrayList<String> svc;
		try {
			svc = ServicesUtil.getInstance(containerName).getAllServiceDefNames();
		} catch (IOException e) {
			svc = new ArrayList<String>();
		}
		for (String type : svc)
			svcCombo.add(type);
		svcCombo.setLayoutData(gd);
		setControl(root);
	}

	public void refreshSvc() {
		ArrayList<String> svc;
		svcCombo.removeAll();
		try {
			svc = ServicesUtil.getInstance(containerName).getAllServiceDefNames();
		} catch (IOException e) {
			svc = new ArrayList<String>();
		}
		for (String type : svc)
			svcCombo.add(type);
	}

	public ComponentType getComponent() {
		ComponentType type = new ComponentType();
		ComponentType.Service ref = new ComponentType.Service();
		ref.setName(nameText.getText());
		EcoaInterface intf = new EcoaInterface();
		intf.setSyntax(svcCombo.getText());
		ref.setInterface(new EcoaInterfaceElement(intf));
		type.getServiceOrReferenceOrProperty().add(ref);
		return type;
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