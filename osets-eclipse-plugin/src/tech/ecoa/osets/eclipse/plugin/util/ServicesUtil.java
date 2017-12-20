/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.util;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.sun.xml.internal.bind.v2.runtime.MarshallerImpl;

import tech.ecoa.osets.eclipse.plugin.common.NamespacePrefixMapper;
import tech.ecoa.osets.eclipse.plugin.editors.parts.service.DataServiceComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.service.EventServiceComposite;
import tech.ecoa.osets.eclipse.plugin.editors.parts.service.ReqResServiceComposite;
import tech.ecoa.osets.model.intf.Data;
import tech.ecoa.osets.model.intf.Event;
import tech.ecoa.osets.model.intf.Operation;
import tech.ecoa.osets.model.intf.Operations;
import tech.ecoa.osets.model.intf.Parameter;
import tech.ecoa.osets.model.intf.RequestResponse;
import tech.ecoa.osets.model.intf.ServiceDefinition;
import tech.ecoa.osets.model.intf.Use;

@SuppressWarnings("deprecation")
public class ServicesUtil {
	private final PluginUtil util = new PluginUtil();
	private String containerName;
	private String editorText;

	public static ServicesUtil getInstance() {
		ServicesUtil util = new ServicesUtil();
		return util;
	}

	public static ServicesUtil getInstance(String containerName) {
		ServicesUtil util = new ServicesUtil();
		util.setContainerName(containerName);
		return util;
	}

	public Tree buildTreeFromContent(Composite parent) throws JAXBException {
		Tree tree = new Tree(parent, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		Unmarshaller unmarshaller = JAXBContext.newInstance(ServiceDefinition.class).createUnmarshaller();
		ServiceDefinition def = (ServiceDefinition) unmarshaller.unmarshal(new StringReader(editorText));
		TreeItem lA1Item = new TreeItem(tree, 0);
		lA1Item.setText("Uses");
		lA1Item.setExpanded(true);
		for (Use use : def.getUse()) {
			TreeItem lB1Item = new TreeItem(lA1Item, 0);
			lB1Item.setText(use.getLibrary());
			lB1Item.setExpanded(true);
		}
		TreeItem lA2Item = new TreeItem(tree, 0);
		lA2Item.setText("Operations");
		lA2Item.setExpanded(true);
		TreeItem lB1Item = new TreeItem(lA2Item, 0);
		lB1Item.setText("Data");
		lB1Item.setExpanded(true);
		TreeItem lB2Item = new TreeItem(lA2Item, 0);
		lB2Item.setText("Event");
		lB2Item.setExpanded(true);
		TreeItem lB3Item = new TreeItem(lA2Item, 0);
		lB3Item.setText("Request Response");
		lB3Item.setExpanded(true);
		for (Object obj : def.getOperations().getDataOrEventOrRequestresponse()) {
			if (obj instanceof Data) {
				TreeItem lCItem = new TreeItem(lB1Item, 0);
				Data val = (Data) obj;
				lCItem.setText(val.getName());
				lCItem.setExpanded(true);
			} else if (obj instanceof Event) {
				TreeItem lCItem = new TreeItem(lB2Item, 0);
				Event val = (Event) obj;
				lCItem.setText(val.getName());
				lCItem.setExpanded(true);
			} else if (obj instanceof RequestResponse) {
				TreeItem lCItem = new TreeItem(lB3Item, 0);
				RequestResponse val = (RequestResponse) obj;
				lCItem.setText(val.getName());
				lCItem.setExpanded(true);
			}
		}
		return tree;
	}

	public ServiceDefinition buildFromData(Data data) {
		ServiceDefinition ret = new ServiceDefinition();
		ret.setOperations(new Operations());
		if (StringUtils.containsIgnoreCase(data.getType(), ":")) {
			String[] vals = StringUtils.split(data.getType(), ":");
			Use use = new Use();
			use.setLibrary(LibraryUtil.getLibraryUseNameFromName(vals[0]));
			ret.getUse().add(use);
			data.setType(data.getType());
		}
		ret.getOperations().getDataOrEventOrRequestresponse().add(data);
		return ret;
	}

	public ServiceDefinition buildFromEvent(Event data) {
		ServiceDefinition ret = new ServiceDefinition();
		ret.setOperations(new Operations());
		ArrayList<Parameter> param = new ArrayList<Parameter>();
		ArrayList<Use> uses = new ArrayList<Use>();
		for (Parameter para : data.getInput()) {
			if (StringUtils.containsIgnoreCase(para.getType(), ":")) {
				String[] vals = StringUtils.split(para.getType(), ":");
				Use use = new Use();
				use.setLibrary(LibraryUtil.getLibraryUseNameFromName(vals[0]));
				if (!existing(use, uses))
					uses.add(use);
				para.setType(para.getType());
			}
			param.add(para);
		}
		data.getInput().clear();
		data.getInput().addAll(param);
		ret.getUse().clear();
		ret.getUse().addAll(uses);
		ret.getOperations().getDataOrEventOrRequestresponse().add(data);
		return ret;
	}

	public ServiceDefinition buildFromReqRes(RequestResponse data) {
		ServiceDefinition ret = new ServiceDefinition();
		ret.setOperations(new Operations());
		ArrayList<Parameter> param = new ArrayList<Parameter>();
		ArrayList<Use> uses = new ArrayList<Use>();
		for (Parameter para : data.getInput()) {
			if (StringUtils.containsIgnoreCase(para.getType(), ":")) {
				String[] vals = StringUtils.split(para.getType(), ":");
				Use use = new Use();
				use.setLibrary(LibraryUtil.getLibraryUseNameFromName(vals[0]));
				if (!existing(use, uses))
					uses.add(use);
				para.setType(para.getType());
			}
			param.add(para);
		}
		data.getInput().clear();
		data.getInput().addAll(param);
		param.clear();
		for (Parameter para : data.getOutput()) {
			if (StringUtils.containsIgnoreCase(para.getType(), ":")) {
				String[] vals = StringUtils.split(para.getType(), ":");
				Use use = new Use();
				use.setLibrary(LibraryUtil.getLibraryUseNameFromName(vals[0]));
				if (!existing(use, uses))
					uses.add(use);
				para.setType(para.getType());
			}
			param.add(para);
		}
		data.getOutput().clear();
		data.getOutput().addAll(param);
		ret.getUse().clear();
		ret.getUse().addAll(uses);
		ret.getOperations().getDataOrEventOrRequestresponse().add(data);
		return ret;
	}

	public Data getSelectedDataOp(String text, Class<Data> clazz) throws JAXBException {
		Data data = new Data();
		Unmarshaller unmarshaller = JAXBContext.newInstance(ServiceDefinition.class).createUnmarshaller();
		ServiceDefinition sDef = (ServiceDefinition) unmarshaller.unmarshal(new StringReader(editorText));
		for (Object obj : sDef.getOperations().getDataOrEventOrRequestresponse()) {
			if (obj instanceof Data) {
				Data temp = (Data) obj;
				if (temp.getName().equalsIgnoreCase(text))
					data = temp;
			}
		}
		return data;
	}

	public Event getSelectedEventOp(String text, Class<Event> clazz) throws JAXBException {
		Event event = new Event();
		Unmarshaller unmarshaller = JAXBContext.newInstance(ServiceDefinition.class).createUnmarshaller();
		ServiceDefinition sDef = (ServiceDefinition) unmarshaller.unmarshal(new StringReader(editorText));
		for (Object obj : sDef.getOperations().getDataOrEventOrRequestresponse()) {
			if (obj instanceof Event) {
				Event temp = (Event) obj;
				if (temp.getName().equalsIgnoreCase(text))
					event = temp;
			}
		}
		return event;
	}

	public RequestResponse getSelectedReqResOp(String text, Class<RequestResponse> clazz) throws JAXBException {
		RequestResponse reqRes = new RequestResponse();
		Unmarshaller unmarshaller = JAXBContext.newInstance(ServiceDefinition.class).createUnmarshaller();
		ServiceDefinition sDef = (ServiceDefinition) unmarshaller.unmarshal(new StringReader(editorText));
		for (Object obj : sDef.getOperations().getDataOrEventOrRequestresponse()) {
			if (obj instanceof RequestResponse) {
				RequestResponse temp = (RequestResponse) obj;
				if (temp.getName().equalsIgnoreCase(text))
					reqRes = temp;
			}
		}
		return reqRes;
	}

	public String removeItem(TreeItem selItem, String parent, String content) throws JAXBException {
		Unmarshaller unmarshaller = JAXBContext.newInstance(ServiceDefinition.class).createUnmarshaller();
		MarshallerImpl marshaller = (MarshallerImpl) JAXBContext.newInstance(ServiceDefinition.class).createMarshaller();
		marshaller.setProperty(MarshallerImpl.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper());
		ServiceDefinition sDef = (ServiceDefinition) unmarshaller.unmarshal(new StringReader(editorText));
		ArrayList<Operation> ops = new ArrayList<Operation>();
		ops.addAll(sDef.getOperations().getDataOrEventOrRequestresponse());
		int i = 0;
		for (Object obj : sDef.getOperations().getDataOrEventOrRequestresponse()) {
			if (obj instanceof Data && parent.equalsIgnoreCase("Data")) {
				if (((Data) obj).getName().equalsIgnoreCase(selItem.getText()))
					ops.remove(i);
			} else if (obj instanceof Event && parent.equalsIgnoreCase("Event")) {
				if (((Event) obj).getName().equalsIgnoreCase(selItem.getText()))
					ops.remove(i);
			} else if (obj instanceof RequestResponse && parent.equalsIgnoreCase("Request Response")) {
				if (((RequestResponse) obj).getName().equalsIgnoreCase(selItem.getText()))
					ops.remove(i);
			}
			i++;
		}
		sDef.getOperations().getDataOrEventOrRequestresponse().clear();
		sDef.getOperations().getDataOrEventOrRequestresponse().addAll(ops);
		StringWriter writer = new StringWriter();
		marshaller.marshal(sDef, writer);
		return ParseUtil.removeEmptyTags(writer.toString(), ServiceDefinition.class);
	}

	public String processAdd(boolean isEdit, String editName, Object obj, String type, String string) throws JAXBException {
		Unmarshaller unmarshaller = JAXBContext.newInstance(ServiceDefinition.class).createUnmarshaller();
		MarshallerImpl marshaller = (MarshallerImpl) JAXBContext.newInstance(ServiceDefinition.class).createMarshaller();
		marshaller.setProperty(MarshallerImpl.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper());
		ServiceDefinition sDef = (ServiceDefinition) unmarshaller.unmarshal(new StringReader(editorText));
		ServiceDefinition tempA = null;
		if (type != null && type.equalsIgnoreCase("Data")) {
			DataServiceComposite comp = (DataServiceComposite) obj;
			Data proc = comp.getProcessedData();
			tempA = buildFromData(proc);
			if (isEdit)
				processAndRemoveOperation(sDef, editName, type);
			sDef.getOperations().getDataOrEventOrRequestresponse().addAll(tempA.getOperations().getDataOrEventOrRequestresponse());
		} else if (type != null && type.equalsIgnoreCase("Event")) {
			EventServiceComposite comp = (EventServiceComposite) obj;
			Event proc = comp.getProcessedEvent();
			tempA = buildFromEvent(proc);
			if (isEdit)
				processAndRemoveOperation(sDef, editName, type);
			sDef.getOperations().getDataOrEventOrRequestresponse().addAll(tempA.getOperations().getDataOrEventOrRequestresponse());
		} else if (type != null && type.equalsIgnoreCase("Request Response")) {
			ReqResServiceComposite comp = (ReqResServiceComposite) obj;
			RequestResponse proc = comp.getProcessedReqRes();
			tempA = buildFromReqRes(proc);
			if (isEdit)
				processAndRemoveOperation(sDef, editName, type);
			sDef.getOperations().getDataOrEventOrRequestresponse().addAll(tempA.getOperations().getDataOrEventOrRequestresponse());
		}
		ArrayList<Use> uses = new ArrayList<Use>();
		for (Use use : sDef.getUse())
			if (!existing(use, uses))
				uses.add(use);
		for (Use use : tempA.getUse())
			if (!existing(use, uses))
				uses.add(use);
		sDef.getUse().clear();
		for (Use use : uses) {
			if (!existing(use, sDef))
				sDef.getUse().add(use);
		}
		StringWriter writer = new StringWriter();
		marshaller.marshal(sDef, writer);
		return ParseUtil.removeEmptyTags(writer.toString(), ServiceDefinition.class);
	}

	private void processAndRemoveOperation(ServiceDefinition sDef, String editName, String parent) {
		ArrayList<Operation> ops = new ArrayList<Operation>();
		ops.addAll(sDef.getOperations().getDataOrEventOrRequestresponse());
		int i = 0;
		for (Object obj : sDef.getOperations().getDataOrEventOrRequestresponse()) {
			if (obj instanceof Data && parent.equalsIgnoreCase("Data")) {
				if (((Data) obj).getName().equalsIgnoreCase(editName))
					ops.remove(i);
			} else if (obj instanceof Event && parent.equalsIgnoreCase("Event")) {
				if (((Event) obj).getName().equalsIgnoreCase(editName))
					ops.remove(i);
			} else if (obj instanceof RequestResponse && parent.equalsIgnoreCase("Request Response")) {
				if (((RequestResponse) obj).getName().equalsIgnoreCase(editName))
					ops.remove(i);
			}
			i++;
		}
		sDef.getOperations().getDataOrEventOrRequestresponse().clear();
		sDef.getOperations().getDataOrEventOrRequestresponse().addAll(ops);
	}

	public ArrayList<String> getAllServiceNames() throws IOException, JAXBException {
		ArrayList<String> ret = new ArrayList<String>();
		ArrayList<String> types = util.getResourcesWithExtension("srvc", containerName);
		for (String type : types) {
			File file = new File(type);
			String content = FileUtils.readFileToString(file);
			JAXBContext ctx = JAXBContext.newInstance(ServiceDefinition.class);
			ServiceDefinition sDef = (ServiceDefinition) ctx.createUnmarshaller().unmarshal(new StringReader(content));
			for (Operation op : sDef.getOperations().getDataOrEventOrRequestresponse()) {
				ret.add(op.getName());
			}
		}
		return ret;
	}

	public ArrayList<String> getAllServiceDefNames() throws IOException {
		ArrayList<String> ret = new ArrayList<String>();
		ArrayList<String> types = util.getResourcesWithExtension("srvc", containerName);
		for (String type : types) {
			ret.add(FilenameUtils.getBaseName(type));
		}
		return ret;
	}

	public ServiceDefinition getByName(String name) throws JAXBException, IOException {
		ServiceDefinition data = new ServiceDefinition();
		ArrayList<String> sDefs = util.getResourcesWithExtension("srvc", containerName);
		Unmarshaller unmarshaller = JAXBContext.newInstance(ServiceDefinition.class).createUnmarshaller();
		for (String sDef : sDefs) {
			File file = new File(sDef);
			if (file.exists() && StringUtils.contains(file.getName(), name)) {
				String content = FileUtils.readFileToString(file);
				data = (ServiceDefinition) unmarshaller.unmarshal(new StringReader(content));
				break;
			}
		}
		return data;
	}

	private boolean existing(Use use, ArrayList<Use> uses) {
		boolean ret = false;
		for (Use u : uses)
			ret = ret || (u.getLibrary().equalsIgnoreCase(use.getLibrary()));
		return ret;
	}

	private boolean existing(Use use, ServiceDefinition sDef) {
		boolean ret = false;
		for (Use u : sDef.getUse())
			ret = ret || (u.getLibrary().equalsIgnoreCase(use.getLibrary()));
		return ret;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getEditorText() {
		return editorText;
	}

	public void setEditorText(String editorText) {
		this.editorText = editorText;
	}

}
