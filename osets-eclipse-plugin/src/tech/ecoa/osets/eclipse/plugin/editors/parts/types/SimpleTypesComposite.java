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
import tech.ecoa.osets.model.types.Simple;

public class SimpleTypesComposite {

	private Text nameText;
	private Text commentText;
	private Combo typeCombo;
	private Text preSpnr;
	private Text minRangeSpnr;
	private Text maxRangeSpnr;
	private String containerName;
	private Text unitText;
	private Simple simple;
	private Composite ret;
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
		nameText.setText((simple.getName() != null) ? simple.getName() : "");
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		nameText.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Type:");
		typeCombo = new Combo(ret, SWT.NULL);
		refreshTypes();
		typeCombo.setText((simple.getType() != null) ? simple.getType() : "");
		typeCombo.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Comment:");
		commentText = new Text(ret, SWT.BORDER | SWT.SINGLE);
		commentText.setText((simple.getComment() != null) ? simple.getComment() : "");
		commentText.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("M&in Range:");
		minRangeSpnr = new Text(ret, SWT.BORDER | SWT.SINGLE);
		minRangeSpnr.setText((simple.getMinRange() != null) ? simple.getMinRange() : "");
		minRangeSpnr.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("M&ax Range:");
		maxRangeSpnr = new Text(ret, SWT.BORDER | SWT.SINGLE);
		maxRangeSpnr.setText((simple.getMaxRange() != null) ? simple.getMaxRange() : "");
		maxRangeSpnr.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Unit:");
		unitText = new Text(ret, SWT.BORDER | SWT.SINGLE);
		unitText.setText((simple.getUnit() != null) ? simple.getUnit() : "");
		unitText.setLayoutData(gd);
		label = new Label(ret, SWT.NULL);
		label.setText("&Precision:");
		preSpnr = new Text(ret, SWT.BORDER | SWT.SINGLE);
		preSpnr.setText((simple.getPrecision() != null) ? simple.getPrecision() : "");
		preSpnr.setLayoutData(gd);
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

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public Simple getSimple() {
		return simple;
	}

	public Simple getProcessedSimple() {
		Simple ret = new Simple();
		ret.setComment((commentText.getText().length() == 0) ? null : commentText.getText());
		ret.setName(nameText.getText());
		ret.setType(typeCombo.getText());
		ret.setMaxRange(maxRangeSpnr.getText());
		ret.setMinRange(minRangeSpnr.getText());
		ret.setUnit(unitText.getText());
		ret.setPrecision(preSpnr.getText());
		return ret;
	}

	public void setSimple(Simple simple) {
		this.simple = simple;
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
