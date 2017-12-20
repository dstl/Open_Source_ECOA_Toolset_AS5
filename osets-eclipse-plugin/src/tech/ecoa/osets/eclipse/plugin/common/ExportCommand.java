/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.common;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.commands.Command;

import com.sun.xml.internal.bind.v2.runtime.MarshallerImpl;

import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.CompositeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ComponentImplementationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeploymentNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.Node;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.PlatformConfigurationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalSystemNode;
import tech.ecoa.osets.eclipse.plugin.util.EclipseUtil;
import tech.ecoa.osets.eclipse.plugin.util.ExportUtil;
import tech.ecoa.osets.eclipse.plugin.util.ParseUtil;
import tech.ecoa.osets.model.cdef.ComponentType;
import tech.ecoa.osets.model.cdef.Composite;
import tech.ecoa.osets.model.cimp.ComponentImplementation;
import tech.ecoa.osets.model.deploy.Deployment;
import tech.ecoa.osets.model.intf.ServiceDefinition;
import tech.ecoa.osets.model.lsys.LogicalSystem;
import tech.ecoa.osets.model.types.Library;
import tech.ecoa.osets.model.upbind.ObjectFactory;
import tech.ecoa.osets.model.upbind.Platform;
import tech.ecoa.osets.model.upbind.UDPBinding;

@SuppressWarnings("deprecation")
public class ExportCommand extends Command {
	private String containerName;
	private String fileName;
	private String fileType;
	private IWorkspaceRoot wsRoot;
	private String[] names;
	private IResource wsRootRes;
	private IProject prj;
	private IFolder target;
	private IFolder steps;
	private IFolder types;
	private IFolder srvcs;
	private IFolder cdef;
	private IFolder iassm;
	private IFolder cimpl;
	private IFolder intgr;
	private String content;

	public ExportCommand(String containerName, String fileName, String fileType, String content) {
		super();
		this.containerName = containerName;
		this.fileName = fileName;
		this.fileType = fileType;
		this.content = content;
		wsRoot = ResourcesPlugin.getWorkspace().getRoot();
		names = StringUtils.split(containerName, "/");
		wsRootRes = wsRoot.findMember(new Path("/" + names[0]));
		prj = wsRootRes.getProject();
		target = prj.getFolder("target");
		steps = prj.getFolder("target/Steps");
		types = prj.getFolder("target/Steps/0-Types");
		srvcs = prj.getFolder("target/Steps/1-Services");
		cdef = prj.getFolder("target/Steps/2-ComponentDefinitions");
		iassm = prj.getFolder("target/Steps/3-InitialAssembly");
		cimpl = prj.getFolder("target/Steps/4-ComponentImplementations");
		intgr = prj.getFolder("target/Steps/5-Integration");
	}

	@Override
	public void execute() {
		try {
			if (containerName != null) {
				verifyFolderStructure();
				IPath loc = wsRootRes.getLocation();
				File prjLoc = new File(loc.toString());
				File[] res = prjLoc.listFiles();
				HashMap<String, ArrayList<File>> fileGroups = new HashMap<String, ArrayList<File>>();
				HashMap<String, Integer> groupCnt = new HashMap<String, Integer>();
				for (File file : res) {
					String extension = FilenameUtils.getExtension(file.getName());
					ArrayList<File> list = fileGroups.get(extension);
					int cnt = (groupCnt.get(extension) != null) ? groupCnt.get(extension).intValue() + 1 : 1;
					if (list == null)
						list = new ArrayList<File>();
					list.add(file);
					groupCnt.put(extension, new Integer(cnt++));
					fileGroups.put(extension, list);
				}
				Iterator<String> iter = fileGroups.keySet().iterator();
				JAXBContext compCtx = JAXBContext.newInstance(Composite.class);
				JAXBContext lsysCtx = JAXBContext.newInstance(LogicalSystem.class);
				JAXBContext deployCtx = JAXBContext.newInstance(Deployment.class);
				JAXBContext cImpCtx = JAXBContext.newInstance(ComponentImplementation.class);
				JAXBContext udpBindCtx = JAXBContext.newInstance(UDPBinding.class);
				MarshallerImpl compMar = (MarshallerImpl) compCtx.createMarshaller();
				compMar.setProperty(MarshallerImpl.JAXB_FORMATTED_OUTPUT, true);
				compMar.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper());
				MarshallerImpl lSysMar = (MarshallerImpl) lsysCtx.createMarshaller();
				lSysMar.setProperty(MarshallerImpl.JAXB_FORMATTED_OUTPUT, true);
				lSysMar.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper());
				MarshallerImpl deployMar = (MarshallerImpl) deployCtx.createMarshaller();
				deployMar.setProperty(MarshallerImpl.JAXB_FORMATTED_OUTPUT, true);
				deployMar.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper());
				MarshallerImpl cImpMar = (MarshallerImpl) cImpCtx.createMarshaller();
				cImpMar.setProperty(MarshallerImpl.JAXB_FORMATTED_OUTPUT, true);
				cImpMar.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper());
				MarshallerImpl udpBindMar = (MarshallerImpl) udpBindCtx.createMarshaller();
				udpBindMar.setProperty(MarshallerImpl.JAXB_FORMATTED_OUTPUT, true);
				udpBindMar.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper());
				while (iter.hasNext()) {
					String ext = iter.next();
					ArrayList<File> files = fileGroups.get(ext);
					for (File file : files) {
						switch (ext) {
						case "types":
							IFile tTarget = types.getFile(StringUtils.replace(file.getName(), ".types", ".types.xml"));
							String tContent = FileUtils.readFileToString(file);
							tContent = ParseUtil.removeEmptyTags(tContent, Library.class);
							FileUtils.write(createFile(tTarget.getLocation().toOSString(), true), tContent);
							break;
						case "srvc":
							IFile sTarget = srvcs.getFile(StringUtils.replace(file.getName(), ".srvc", ".interface.xml"));
							String sContent = FileUtils.readFileToString(file);
							sContent = ParseUtil.removeEmptyTags(sContent, ServiceDefinition.class);
							FileUtils.write(createFile(sTarget.getLocation().toOSString(), true), sContent);
							break;
						case "cdef":
							String folder = cdef.getLocation().toOSString() + "/" + StringUtils.replace(file.getName(), ".cdef", "");
							createDirectory(folder, true);
							String cTarget = folder + "/" + StringUtils.replace(file.getName(), ".cdef", ".componentType");
							String cContent = FileUtils.readFileToString(file);
							cContent = ParseUtil.removeEmptyTags(cContent, ComponentType.class);
							FileUtils.write(createFile(cTarget, true), cContent);
							break;
						}
					}
				}
				ArrayList<String> validations;
				switch (fileType) {
				case "assmbl":
					CompositeNode cNode = ParseUtil.getInitialAssemblyNodeFromText(content);
					validations = cNode.validate();
					if (validations.size() > 0) {
						EclipseUtil.displayValidations(validations);
					} else {
						Composite comp = ExportUtil.getInitialAssemblyJAXBFromNode(cNode);
						IFile target = iassm.getFile(comp.getName() + ".composite");
						StringWriter writer = new StringWriter();
						compMar.marshal(comp, writer);
						FileUtils.write(createFile(target.getLocation().toOSString(), true), ParseUtil.removeEmptyTags(writer.toString(), Composite.class));
					}
					break;
				case "cimpl":
					ComponentImplementationNode ciNode = ParseUtil.getComponentImplementationNodeFromText(content);
					validations = ciNode.validate();
					if (validations.size() > 0) {
						EclipseUtil.displayValidations(validations);
					} else {
						ComponentImplementation cImp = ExportUtil.getComponentImplementationJAXBFromNode(ciNode);
						String file = FilenameUtils.getBaseName(fileName) + ".cimpl";
						String folder = cimpl.getLocation().toOSString() + "/" + StringUtils.replace(file, ".cimpl", "");
						createDirectory(folder, true);
						String cTarget = folder + "/" + StringUtils.replace(file, ".cimpl", ".impl.xml");
						StringWriter cWriter = new StringWriter();
						cImpMar.marshal(cImp, cWriter);
						FileUtils.write(createFile(cTarget, true), ParseUtil.removeEmptyTags(cWriter.toString(), ComponentImplementation.class));
					}
					break;
				case "lsys":
					LogicalSystemNode lNode = ParseUtil.getLogicalSystemNodeFromText(content);
					validations = lNode.validate();
					if (validations.size() > 0) {
						EclipseUtil.displayValidations(validations);
					} else {
						LogicalSystem lsys = ExportUtil.getLogicalSystemJAXBFromNode(lNode);
						IFile lTarget = intgr.getFile(lsys.getId() + ".logical_system.xml");
						StringWriter lWriter = new StringWriter();
						lSysMar.marshal(lsys, lWriter);
						FileUtils.write(createFile(lTarget.getLocation().toOSString(), true), ParseUtil.removeEmptyTags(lWriter.toString(), LogicalSystem.class));
					}
					break;
				case "fassmbl":
					tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositeNode fcNode = ParseUtil.getFinalAssemblyNodeFromText(content);
					validations = fcNode.validate();
					if (validations.size() > 0) {
						EclipseUtil.displayValidations(validations);
					} else {
						Composite fComp = ExportUtil.getFinalAssemblyJAXBFromNode(fcNode);
						IFile fTarget = intgr.getFile(FilenameUtils.getBaseName(fileName) + ".impl.composite");
						StringWriter fWriter = new StringWriter();
						compMar.marshal(fComp, fWriter);
						FileUtils.write(createFile(fTarget.getLocation().toOSString(), true), ParseUtil.removeEmptyTags(fWriter.toString(), Composite.class));
					}
					break;
				case "deploy":
					DeploymentNode dNode = ParseUtil.getDeploymentNodeFromText(content);
					validations = dNode.validate();
					if (validations.size() > 0) {
						EclipseUtil.displayValidations(validations);
					} else {
						Deployment deploy = ExportUtil.getDeploymentJAXBFromNode(dNode);
						IFile dTarget = intgr.getFile("deployment.xml");
						StringWriter dWriter = new StringWriter();
						deployMar.marshal(deploy, dWriter);
						FileUtils.write(createFile(dTarget.getLocation().toOSString(), true), ParseUtil.removeEmptyTags(dWriter.toString(), Deployment.class));
						ObjectFactory factory = new ObjectFactory();
						UDPBinding bind = factory.createUDPBinding();
						for (Node cNd : dNode.getChild()) {
							if (cNd instanceof PlatformConfigurationNode) {
								PlatformConfigurationNode conf = (PlatformConfigurationNode) cNd;
								Platform plat = factory.createPlatform();
								plat.setName(conf.getCompPlatform());
								plat.setPlatformId(Long.valueOf(conf.getPlatNum()));
								plat.setReceivingMulticastAddress(conf.getMcastAddr());
								plat.setReceivingPort(new BigInteger(conf.getPort()));
								bind.getPlatform().add(plat);
							}
						}
						IFile uTarget = intgr.getFile("udpbinding.xml");
						StringWriter uWriter = new StringWriter();
						udpBindMar.marshal(bind, uWriter);
						FileUtils.write(createFile(uTarget.getLocation().toOSString(), true), ParseUtil.removeEmptyTags(uWriter.toString(), UDPBinding.class));
					}
					break;
				}
			}
			for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
				try {
					project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
				} catch (CoreException e) {
				}
			}
		} catch (Exception e) {
			EclipseUtil.writeStactTraceToConsole(e);
		}
	}

	private void verifyFolderStructure() {
		try {
			createDirectory(target.getLocation().toOSString(), false);
			createDirectory(steps.getLocation().toOSString(), false);
			createDirectory(types.getLocation().toOSString(), false);
			createDirectory(srvcs.getLocation().toOSString(), false);
			createDirectory(cdef.getLocation().toOSString(), false);
			createDirectory(iassm.getLocation().toOSString(), false);
			createDirectory(cimpl.getLocation().toOSString(), false);
			createDirectory(intgr.getLocation().toOSString(), false);
		} catch (Exception e) {
			EclipseUtil.writeStactTraceToConsole(e);
		}
	}

	private void createDirectory(String loc, boolean delete) throws IOException {
		File file = new File(loc);
		if (delete && file.exists())
			FileUtils.forceDelete(file);
		if (!file.exists())
			file.mkdirs();
	}

	private File createFile(String loc, boolean delete) throws IOException {
		File file = new File(loc);
		if (delete && file.exists())
			FileUtils.forceDelete(file);
		file.getParentFile().mkdirs();
		file.createNewFile();
		return file;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
