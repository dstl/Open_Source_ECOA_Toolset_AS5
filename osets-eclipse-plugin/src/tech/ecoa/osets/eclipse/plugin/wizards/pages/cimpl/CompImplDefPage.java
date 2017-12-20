/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.wizards.pages.cimpl;

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
 * OR with the extension that matches the expected one (cimpl).
 */

public class CompImplDefPage extends WizardPage {
	private Combo nameText;
	private String containerName;
	private Composite root;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public CompImplDefPage() {
		super("Select Component Definition");
		setTitle("Select Component Definition");
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
		label.setText("&Select Definition:");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		nameText = new Combo(root, SWT.BORDER | SWT.SINGLE);
		nameText.setLayoutData(gd);
		setControl(root);
	}

	public void refreshDef() {
		nameText.removeAll();
		ArrayList<String> cDef = new PluginUtil().getResourcesWithExtension("cdef", containerName);
		for (String str : cDef) {
			nameText.add(FilenameUtils.getBaseName(str));
		}
	}

	public String getName() {
		return nameText.getText();
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
}