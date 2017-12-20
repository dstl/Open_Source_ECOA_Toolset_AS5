/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.xmlprocessor;

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;

public class ECOALSInput implements LSInput {

	private InputStream byteStream;

	public ECOALSInput(InputStream byteStream) {
		super();
		this.byteStream = byteStream;
	}

	@Override
	public String getBaseURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream getByteStream() {
		return byteStream;
	}

	@Override
	public boolean getCertifiedText() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Reader getCharacterStream() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEncoding() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPublicId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStringData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSystemId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBaseURI(String baseURI) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setByteStream(InputStream byteStream) {
		this.byteStream = byteStream;
	}

	@Override
	public void setCertifiedText(boolean certifiedText) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCharacterStream(Reader characterStream) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEncoding(String encoding) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPublicId(String publicId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setStringData(String stringData) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSystemId(String systemId) {
		// TODO Auto-generated method stub

	}

}
