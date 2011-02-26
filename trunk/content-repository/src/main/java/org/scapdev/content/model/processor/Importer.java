/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 David Waltermire
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package org.scapdev.content.model.processor;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public interface Importer {
	ImportData read(File file) throws ImportException;
	ImportData read(InputStream is) throws ImportException;
	ImportData read(Reader reader) throws ImportException;
	ImportData read(URL url) throws ImportException;
	ImportData read(InputSource is) throws ImportException;
	ImportData read(Node node) throws ImportException;
	<T> ImportData read(Node node, Class<T> clazz) throws ImportException;
	ImportData read(Source source) throws ImportException;
	<T> ImportData read(Source source, Class<T> clazz) throws ImportException;
	ImportData read(XMLStreamReader reader) throws ImportException;
	<T> ImportData read(XMLStreamReader reader, Class<T> clazz) throws ImportException;
	ImportData read(XMLEventReader reader) throws ImportException;
	<T> ImportData read(XMLEventReader reader, Class<T> clazz) throws ImportException;
//	
//	public abstract void marshal(java.lang.Object arg0, javax.xml.transform.Result arg1) throws javax.xml.bind.JAXBException;
//	  public abstract void marshal(java.lang.Object arg0, java.io.OutputStream arg1) throws javax.xml.bind.JAXBException;
//	  public abstract void marshal(java.lang.Object arg0, java.io.File arg1) throws javax.xml.bind.JAXBException;
//	  public abstract void marshal(java.lang.Object arg0, java.io.Writer arg1) throws javax.xml.bind.JAXBException;
//	  public abstract void marshal(java.lang.Object arg0, org.xml.sax.ContentHandler arg1) throws javax.xml.bind.JAXBException;
//	  public abstract void marshal(java.lang.Object arg0, org.w3c.dom.Node arg1) throws javax.xml.bind.JAXBException;
//	  public abstract void marshal(java.lang.Object arg0, javax.xml.stream.XMLStreamWriter arg1) throws javax.xml.bind.JAXBException;
//	  public abstract void marshal(java.lang.Object arg0, javax.xml.stream.XMLEventWriter arg1) throws javax.xml.bind.JAXBException;
	 
}
