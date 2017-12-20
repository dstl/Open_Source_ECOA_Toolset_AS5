/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;

public class ECOA_UID_Gen {
	private static final Logger LOGGER = LogManager.getLogger(ECOA_UID_Gen.class);
	private static final String SEP_PATTERN_01 = "value";

	private ECOA_System_Model systemModel;
	private Path projectLocation;
	private ArrayList<Integer> UIDList = new ArrayList<Integer>();

	public ECOA_UID_Gen(Path projectLocation, boolean apiOnly) {
		// Create an object to manage processing of the XML
		systemModel = new ECOA_System_Model(projectLocation, apiOnly);
		this.projectLocation = projectLocation;
	}

	public void generateUID() {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();

			// Create the root element
			Element rootElement = doc.createElement("ID_map");
			rootElement.setAttribute("xmlns", "http://www.ecoa.technology/uid-1.0");
			doc.appendChild(rootElement);

			// Add introductory text
			rootElement.appendChild(doc.createComment("This file was created by the MAI ECOA_UID_Plugin"));
			rootElement.appendChild(doc.createComment("This file contains a map allocating a numeric ID for the composite, provided service and service operation in each wire"));
			rootElement.appendChild(doc.createComment("Every ID is a positive 32-bit integer"));

			// Set the Composite ID
			writeCompositeID(doc, rootElement);

			// Set the Provided Service IDs
			writeServiceIDs(doc, rootElement);

			// Set the Service Operation IDs
			writeServiceOpIDs(doc, rootElement);

			// Write the content
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(projectLocation + "/5-Integration/maiIDs.xml"));

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);

			LOGGER.info("UID file successfully created!");
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	private void writeServiceOpIDs(Document doc, Element rootElement) {
		rootElement.appendChild(doc.createComment("Service Operation IDs are computed from a key string built as follows:"));
		rootElement.appendChild(doc.createComment("'sourceComponentInstanceName/sourceReferenceName:targetComponentInstanceName/targetServiceName:operationName'"));

		for (SM_Wire wire : systemModel.getSystemModel().getFinalAssembly().getWires()) {
			String serviceUIDString = wire.getSource().getName() + "/" + wire.getSourceOp().getName() + ":" + wire.getTarget().getName() + "/" + wire.getTargetOp().getName() + ":";

			for (SM_Object op : wire.getTargetOp().getServiceInterface().getOps()) {
				String serviceOpUIDString = serviceUIDString + op.getName();
				Element serviceOpUIDElement = doc.createElement("ID");
				serviceOpUIDElement.setAttribute("key", serviceOpUIDString);
				Integer hashCode = Math.abs(serviceOpUIDString.hashCode());
				hashCode = checkAndAddToUIDList(hashCode);
				serviceOpUIDElement.setAttribute(SEP_PATTERN_01, hashCode.toString());
				rootElement.appendChild(serviceOpUIDElement);
			}

		}
	}

	private void writeServiceIDs(Document doc, Element rootElement) {
		rootElement.appendChild(doc.createComment("Provided Service IDs are computed from a key string built as follows:"));
		rootElement.appendChild(doc.createComment("'providerComponentInstanceName/providedServiceName'"));

		for (SM_ComponentInstance compInst : systemModel.getSystemModel().getFinalAssembly().getComponentInstances()) {
			for (SM_ServiceInstance serviceInst : compInst.getCompType().getServiceInstancesList()) {
				String serviceUIDString = compInst.getName() + "/" + serviceInst.getName();

				Element serviceUIDElement = doc.createElement("ID");
				serviceUIDElement.setAttribute("key", serviceUIDString);
				Integer hashCode = Math.abs(serviceUIDString.hashCode());
				hashCode = checkAndAddToUIDList(hashCode);
				serviceUIDElement.setAttribute(SEP_PATTERN_01, hashCode.toString());
				rootElement.appendChild(serviceUIDElement);
			}
		}
	}

	private void writeCompositeID(Document doc, Element rootElement) {
		rootElement.appendChild(doc.createComment("Composite ID is compute from a key string built as follows:"));
		rootElement.appendChild(doc.createComment("'compositeName'"));
		Element compositeElement = doc.createElement("ID");
		compositeElement.setAttribute("key", systemModel.getSystemModel().getFinalAssembly().getName());
		Integer hashCode = Math.abs(systemModel.getSystemModel().getFinalAssembly().getName().hashCode());
		hashCode = checkAndAddToUIDList(hashCode);
		compositeElement.setAttribute(SEP_PATTERN_01, hashCode.toString());
		rootElement.appendChild(compositeElement);
	}

	private Integer checkAndAddToUIDList(Integer uid) {
		boolean added = false;

		while (!added) {
			if (UIDList.contains(uid)) {
				LOGGER.info("Warning - Duplicate UID has been found - adding 1 to hash value");
				uid++;
			} else {
				UIDList.add(uid);
				added = true;
			}
		}

		return uid;
	}

	/**
	 * This is the main function of the application.
	 * 
	 * @param args
	 *            Steps directory of ECOA XML.
	 */
	public static void main(String[] args) {
		// The argument should be the root Steps/ directory
		if (args.length == 1) {
			Path projectLocation = Paths.get(args[0]);
			boolean apiOnly = false;

			ECOA_UID_Gen ecoaUIDGen = new ECOA_UID_Gen(projectLocation, apiOnly);
			ecoaUIDGen.generateUID();
		} else {
			LOGGER.info("ERROR - call should be \"CodeGenerator <project root directory>\"");

		}
	}

}
