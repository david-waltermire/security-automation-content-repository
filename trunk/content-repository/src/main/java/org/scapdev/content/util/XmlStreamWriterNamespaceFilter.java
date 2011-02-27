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
package org.scapdev.content.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XmlStreamWriterNamespaceFilter implements XMLStreamWriter {
	private final XMLStreamWriter delegate;
//	private LinkedList<String> currentNamespaceStack;

	public XmlStreamWriterNamespaceFilter(XMLStreamWriter delegate) {
		this.delegate = delegate;
//		this.currentNamespaceStack = new LinkedList<String>();
	}

	@Override
	public void close() throws XMLStreamException {
		delegate.close();
	}

	@Override
	public void flush() throws XMLStreamException {
		delegate.flush();
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		return delegate.getNamespaceContext();
	}

	@Override
	public String getPrefix(String uri) throws XMLStreamException {
		return delegate.getPrefix(uri);
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return delegate.getProperty(name);
	}

	@Override
	public void setDefaultNamespace(String uri) throws XMLStreamException {
		setDefaultNamespace(uri);
	}

	@Override
	public void setNamespaceContext(NamespaceContext context)
			throws XMLStreamException {
		delegate.setNamespaceContext(context);
	}

	@Override
	public void setPrefix(String prefix, String uri) throws XMLStreamException {
		delegate.setPrefix(prefix, uri);
	}

	@Override
	public void writeAttribute(String localName, String value)
			throws XMLStreamException {
		delegate.writeAttribute(localName, value);
	}

	@Override
	public void writeAttribute(String namespaceURI, String localName,
			String value) throws XMLStreamException {
		throw new UnsupportedOperationException();
//		delegate.writeAttribute(namespaceURI, localName, value);
	}

	@Override
	public void writeAttribute(String prefix, String namespaceURI,
			String localName, String value) throws XMLStreamException {
		throw new UnsupportedOperationException();
//		delegate.writeAttribute(prefix, namespaceURI, localName, value);
	}

	@Override
	public void writeCData(String data) throws XMLStreamException {
		delegate.writeCData(data);
	}

	@Override
	public void writeCharacters(String text) throws XMLStreamException {
		delegate.writeCharacters(text);
	}

	@Override
	public void writeCharacters(char[] text, int start, int len)
			throws XMLStreamException {
		delegate.writeCharacters(text, start, len);
	}

	@Override
	public void writeComment(String data) throws XMLStreamException {
		delegate.writeComment(data);
	}

	@Override
	public void writeDTD(String dtd) throws XMLStreamException {
		delegate.writeDTD(dtd);
	}

	@Override
	public void writeDefaultNamespace(String namespaceURI)
			throws XMLStreamException {
		delegate.writeDefaultNamespace(namespaceURI);
	}

	@Override
	public void writeEmptyElement(String localName) throws XMLStreamException {
		delegate.writeEmptyElement(localName);
	}

	@Override
	public void writeEmptyElement(String namespaceURI, String localName)
			throws XMLStreamException {
		delegate.writeEmptyElement(namespaceURI, localName);
	}

	@Override
	public void writeEmptyElement(String prefix, String localName,
			String namespaceURI) throws XMLStreamException {
		delegate.writeEmptyElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeEndDocument() throws XMLStreamException {
		delegate.writeEndDocument();
	}

	@Override
	public void writeEndElement() throws XMLStreamException {
		delegate.writeEndElement();
	}

	@Override
	public void writeEntityRef(String name) throws XMLStreamException {
		delegate.writeEntityRef(name);
	}

	@Override
	public void writeNamespace(String prefix, String namespaceURI)
			throws XMLStreamException {
//		delegate.writeNamespace(prefix, namespaceURI);
	}

	@Override
	public void writeProcessingInstruction(String target)
			throws XMLStreamException {
		delegate.writeProcessingInstruction(target);
	}

	@Override
	public void writeProcessingInstruction(String target, String data)
			throws XMLStreamException {
		delegate.writeProcessingInstruction(target, data);
	}

	@Override
	public void writeStartDocument() throws XMLStreamException {
		delegate.writeStartDocument();
	}

	@Override
	public void writeStartDocument(String version) throws XMLStreamException {
		delegate.writeStartDocument(version);
	}

	@Override
	public void writeStartDocument(String encoding, String version)
			throws XMLStreamException {
		delegate.writeStartDocument(encoding, version);
	}

	@Override
	public void writeStartElement(String localName) throws XMLStreamException {
		delegate.writeStartElement(localName);
	}

	@Override
	public void writeStartElement(String namespaceURI, String localName)
			throws XMLStreamException {
		delegate.writeStartElement(namespaceURI, localName);
	}

	@Override
	public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
		delegate.writeStartElement(prefix, localName, namespaceURI);
	}
}
