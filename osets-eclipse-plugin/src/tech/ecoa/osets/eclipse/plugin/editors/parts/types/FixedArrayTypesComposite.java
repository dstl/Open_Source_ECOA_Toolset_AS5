/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.types;

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
import tech.ecoa.osets.model.types.FixedArray;

public class FixedArrayTypesComposite {
	private Composite ret;
	private Text nameText;
	private Text commentText;
	private Combo typeCombo;
	private Text maxNumText;
	private String containerName;
	private FixedArray array;
	private TypesUtil util;

	public void createPartControl(Composite parent) {
		ret = new Composite(parent, SWT.BORDER_SOLID);
		GridLayout layout = new GridLayout();
		ret.setLayout(layout);
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		Label label = new Label(ret, SWT.NULL);
		label.setText("&Name:");
		nameText = new Text(ret, SWT.BORDER | SWT.SINGLE);
		nameText.setText((array.getName() != null) ? array.getName() : "");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		nameText.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Max Size:");
		maxNumText = new Text(ret, SWT.BORDER | SWT.SINGLE);
		maxNumText.setText((array.getMaxNumber() != null) ? array.getMaxNumber() : "");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		maxNumText.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Type:");
		typeCombo = new Combo(ret, SWT.NULL);
		refreshTypes();
		typeCombo.setText((array.getItemType() != null) ? array.getItemType() : "");
		typeCombo.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Comment:");
		commentText = new Text(ret, SWT.BORDER | SWT.SINGLE);
		commentText.setText((array.getComment() != null) ? array.getComment() : "");
		commentText.setLayoutData(gd);
		parent.setVisible(true);
		ret.setVisible(true);
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

	public FixedArray getArray() {
		return array;
	}

	public FixedArray getProcessedFixedArray() {
		FixedArray ret = new FixedArray();
		ret.setComment((commentText.getText().length() == 0) ? null : commentText.getText());
		ret.setItemType(typeCombo.getText());
		ret.setMaxNumber(maxNumText.getText());
		ret.setName(nameText.getText());
		return ret;
	}

	public void setArray(FixedArray array) {
		this.array = array;
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

	public TypesUtil getUtil() {
		return util;
	}

	public void setUtil(TypesUtil util) {
		this.util = util;
	}
}
