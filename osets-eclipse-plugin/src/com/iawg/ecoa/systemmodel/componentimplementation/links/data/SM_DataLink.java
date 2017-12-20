/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.data;

import java.util.ArrayList;
import java.util.List;

import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_VDRepository;

public class SM_DataLink {

	private List<SM_ReaderInterface> readers = new ArrayList<SM_ReaderInterface>();
	private SM_WriterInterface writer;
	private SM_ComponentImplementation componentImpl;
	private SM_VDRepository vdRepo;

	public SM_DataLink(SM_ComponentImplementation compImpl) {
		componentImpl = compImpl;
		// Link this object to the component impl object
		compImpl.addDataLink(this);
	}

	public List<SM_ReaderInterface> getReaders() {
		return readers;
	}

	public SM_WriterInterface getWriter() {
		return writer;
	}

	public void addWriter(SM_WriterInterface smWriter) {
		this.writer = smWriter;
	}

	public void addVDRepo(SM_VDRepository vdRepo) {
		this.vdRepo = vdRepo;
	}

	public SM_VDRepository getVDRepo() {
		return vdRepo;
	}

	public void addReader(SM_ReaderInterface reader) {
		this.readers.add(reader);
	}

	public List<SM_ReaderModuleInstance> getLocalReaders() {
		List<SM_ReaderModuleInstance> localReaders = new ArrayList<SM_ReaderModuleInstance>();

		for (SM_ReaderInterface reader : readers) {
			if (reader instanceof SM_ReaderModuleInstance) {
				localReaders.add((SM_ReaderModuleInstance) reader);
			}
		}

		return localReaders;
	}

	public List<SM_ReaderModuleInstance> getLocalReaders(SM_ModuleImpl readerModuleImpl) {
		List<SM_ReaderModuleInstance> localReaders = new ArrayList<SM_ReaderModuleInstance>();

		for (SM_ReaderInterface reader : readers) {
			if (reader instanceof SM_ReaderModuleInstance) {
				// Only add if the same implementation type.
				if (((SM_ModuleInstance) reader.getReaderInst()).getImplementation() == readerModuleImpl) {
					localReaders.add((SM_ReaderModuleInstance) reader);
				}
			}
		}

		return localReaders;
	}

	public SM_ComponentImplementation getComponentImpl() {
		return componentImpl;
	}

	public SM_ReaderModuleInstance getReader(SM_ModuleInstance moduleInstance) {
		for (SM_ReaderInterface reader : readers) {
			if (reader.getReaderInst() == moduleInstance) {
				return (SM_ReaderModuleInstance) reader;
			}
		}

		return null;
	}

}
