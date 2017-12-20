/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation;

import java.util.ArrayList;
import java.util.List;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_ReaderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_WriterInterface;
import com.iawg.ecoa.systemmodel.types.SM_Type;

public class SM_VDRepository extends SM_Object {
	private List<SM_WriterInterface> writers = new ArrayList<SM_WriterInterface>();
	private List<SM_ReaderInterface> readers = new ArrayList<SM_ReaderInterface>();
	private SM_Type dataType;

	public SM_VDRepository(Integer name) {

		super(name.toString());
	}

	public void addWriter(SM_WriterInterface writer) {
		this.writers.add(writer);
	}

	public void addReader(SM_ReaderInterface reader) {
		this.readers.add(reader);
	}

	public List<SM_WriterInterface> getWriters() {
		return writers;
	}

	public List<SM_ReaderInterface> getReaders() {
		return readers;
	}

	public void setDataType(SM_Type dataType) {
		this.dataType = dataType;
	}

	public SM_Type getDataType() {
		return dataType;
	}

	public String getDataTypeName() {
		return dataType.getNamespace().getName().replaceAll("\\.", "__") + "__" + dataType.getName();
	}

}
