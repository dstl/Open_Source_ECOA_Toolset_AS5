/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ComponentImplementationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalSystemNode;

@SuppressWarnings("deprecation")
public class PluginUtil {

	public ArrayList<String> getResourcesWithExtension(String ext, String containerName) {
		ArrayList<String> ret = new ArrayList<String>();
		if (containerName != null) {
			String[] names = StringUtils.split(containerName, "/");
			IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
			IResource resource = wsRoot.findMember(new Path("/" + names[0]));
			IPath loc = resource.getLocation();
			File prjLoc = new File(loc.toString());
			Collection<File> res = FileUtils.listFiles(prjLoc, FileFilterUtils.suffixFileFilter(ext, IOCase.INSENSITIVE), TrueFileFilter.INSTANCE);
			for (File file : res)
				ret.add(file.getAbsolutePath());
		}
		return ret;
	}

	public CompositeNode getFinalAssemblyDefinition(String name, String containerName) {
		CompositeNode ret = null;
		if (containerName != null) {
			String[] names = StringUtils.split(containerName, "/");
			IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
			IResource resource = wsRoot.findMember(new Path("/" + names[0]));
			IPath loc = resource.getLocation();
			File file = new File(loc.toOSString() + File.separator + name + ".fassmbl");
			try {
				ret = ParseUtil.getFinalAssemblyNodeFromText(FileUtils.readFileToString(file));
			} catch (IOException e) {
				EclipseUtil.writeStactTraceToConsole(e);
			}
		}
		return ret;
	}

	public LogicalSystemNode getLogicalSystemDefinition(String name, String containerName) {
		LogicalSystemNode ret = null;
		if (containerName != null) {
			String[] names = StringUtils.split(containerName, "/");
			IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
			IResource resource = wsRoot.findMember(new Path("/" + names[0]));
			IPath loc = resource.getLocation();
			File file = new File(loc.toOSString() + File.separator + name + ".lsys");
			try {
				ret = ParseUtil.getLogicalSystemNodeFromText(FileUtils.readFileToString(file));
			} catch (IOException e) {
				EclipseUtil.writeStactTraceToConsole(e);
			}
		}
		return ret;
	}

	public ComponentImplementationNode getComponentImplementationDefinition(String name, String containerName) {
		ComponentImplementationNode ret = null;
		if (containerName != null) {
			String[] names = StringUtils.split(containerName, "/");
			IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
			IResource resource = wsRoot.findMember(new Path("/" + names[0]));
			IPath loc = resource.getLocation();
			File file = new File(loc.toOSString() + File.separator + name + ".cimpl");
			try {
				ret = ParseUtil.getComponentImplementationNodeFromText(FileUtils.readFileToString(file));
			} catch (IOException e) {
				EclipseUtil.writeStactTraceToConsole(e);
			}
		}
		return ret;
	}
}
