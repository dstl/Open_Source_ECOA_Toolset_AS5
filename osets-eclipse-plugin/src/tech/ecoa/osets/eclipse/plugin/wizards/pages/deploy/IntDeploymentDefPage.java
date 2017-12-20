/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.wizards.pages.deploy;

import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import tech.ecoa.osets.eclipse.plugin.util.PluginUtil;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (deploy).
 */

public class IntDeploymentDefPage extends WizardPage {
	private Combo nameText;
	private Combo assmblText;
	private String containerName;
	private Composite root;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public IntDeploymentDefPage() {
		super("Select Logical System and Final Assembly");
		setTitle("Select Logical System and Final Assembly");
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
		label.setText("&Logical System:");
		nameText = new Combo(root, SWT.BORDER | SWT.SINGLE);
		nameText.setLayoutData(gd);
		label = new Label(root, SWT.NULL);
		label.setText("&Final Assembly:");
		assmblText = new Combo(root, SWT.BORDER | SWT.SINGLE);
		assmblText.setLayoutData(gd);
		setControl(root);
	}

	public void refreshLsys() {
		nameText.removeAll();
		ArrayList<String> cDef = new PluginUtil().getResourcesWithExtension("lsys", containerName);
		for (String str : cDef) {
			nameText.add(FilenameUtils.getBaseName(str));
		}
	}

	public void refreshAssmbl() {
		assmblText.removeAll();
		ArrayList<String> cDef = new PluginUtil().getResourcesWithExtension("fassmbl", containerName);
		for (String str : cDef) {
			assmblText.add(FilenameUtils.getBaseName(str));
		}
	}

	public String getName() {
		return nameText.getText();
	}

	public String getAssmbl() {
		return assmblText.getText();
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