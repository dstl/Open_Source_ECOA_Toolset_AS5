/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cdef;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tech.ecoa.osets.eclipse.plugin.util.ServicesUtil;
import tech.ecoa.osets.model.cdef.ComponentType;
import tech.ecoa.osets.model.cdef.ComponentTypeReference;
import tech.ecoa.osets.model.cdef.EcoaInterface;
import tech.ecoa.osets.model.cdef.EcoaInterfaceElement;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (cdef).
 */

public class CompDefReferenceComposite {
	private Text nameText;
	private Composite ret;
	private String containerName;
	private Combo svcCombo;
	private ComponentTypeReference def;

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
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
		nameText.setText((def != null && def.getName() != null) ? def.getName() : "");
		nameText.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Type:");
		svcCombo = new Combo(ret, SWT.NULL);
		ArrayList<String> svc;
		try {
			svc = ServicesUtil.getInstance(containerName).getAllServiceDefNames();
		} catch (IOException e) {
			svc = new ArrayList<String>();
		}
		for (String type : svc)
			svcCombo.add(type);
		svcCombo.setText((def != null && def.getInterface() != null && def.getInterface().getValue() != null && def.getInterface().getValue().getSyntax() != null) ? def.getInterface().getValue().getSyntax() : "");
		svcCombo.setLayoutData(gd);
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

	public ComponentType getProcessedComponent() {
		ComponentType type = new ComponentType();
		ComponentTypeReference ref = new ComponentTypeReference();
		ref.setName(nameText.getText());
		EcoaInterface intf = new EcoaInterface();
		intf.setSyntax(svcCombo.getText());
		ref.setInterface(new EcoaInterfaceElement(intf));
		type.getServiceOrReferenceOrProperty().add(ref);
		return type;
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

	public ComponentTypeReference getDef() {
		return def;
	}

	public void setDef(ComponentTypeReference def) {
		this.def = def;
	}
}