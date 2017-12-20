/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cdef;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import tech.ecoa.osets.eclipse.plugin.common.Constants;
import tech.ecoa.osets.eclipse.plugin.util.TypesUtil;
import tech.ecoa.osets.model.cdef.ComponentType;
import tech.ecoa.osets.model.cdef.Property;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (cdef).
 */

public class CompDefPropertyComposite {
	private Text nameText;
	private Composite ret;
	private String containerName;
	private Property def;
	private Combo typeCombo;
	private TypesUtil util;

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
		typeCombo = new Combo(ret, SWT.NULL);
		refreshTypes();
		typeCombo.setLayoutData(gd);
		typeCombo.setText(getType(def));
		label = new Label(ret, SWT.NULL);
	}

	private String getType(Property val) {
		Map<QName, String> attrib = val.getOtherAttributes();
		if (attrib == null)
			return "";
		else {
			if (attrib.get(Constants.TYPE_QNAME) == null)
				return "";
			else
				return attrib.get(Constants.TYPE_QNAME);
		}
	}

	public void refreshTypes() {
		typeCombo.removeAll();
		for (String type : getAllTypes())
			typeCombo.add(type);
	}

	protected String[] getAllTypes() {
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("");
		try {
			TypesUtil util = TypesUtil.getInstance(containerName);
			util.setFileName("");
			ret.addAll(util.getAllTypesForSimpleWizard());
		} catch (IOException | JAXBException e) {
			e.printStackTrace();
		}
		return ret.toArray(new String[0]);
	}

	public ComponentType getProcessedComponent() {
		ComponentType type = new ComponentType();
		Property ref = new Property();
		ref.setName(nameText.getText());
		ref.setType(Constants.XS_QNAME);
		ref.getOtherAttributes().put(Constants.TYPE_QNAME, typeCombo.getText());
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

	public Property getDef() {
		return def;
	}

	public void setDef(Property def) {
		this.def = def;
	}

	public TypesUtil getUtil() {
		if (util == null) {
			util = TypesUtil.getInstance(containerName);
			util.setFileName("");
		}
		return util;
	}

	public void setUtil(TypesUtil util) {
		this.util = util;
	}
}