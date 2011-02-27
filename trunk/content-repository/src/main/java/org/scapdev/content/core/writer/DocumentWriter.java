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
package org.scapdev.content.core.writer;

import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class DocumentWriter {
	private final DocumentData documentData;
	private final XMLStreamWriter writer;
	private final Marshaller marshaller;

	protected DocumentWriter(DocumentData documentData, XMLStreamWriter writer, Marshaller marshaller) {
		this.documentData = documentData;
		this.writer = writer;
		this.marshaller = marshaller;
	}

	public void write() throws XMLStreamException {
		XMLEventFactory factory = XMLEventFactory.newInstance();
		writeDocument(factory);
	}

	public void writeDocument(XMLEventFactory factory) throws XMLStreamException {
		XmlEventGeneratingDocumentModelVisitor visitor = new XmlEventGeneratingDocumentModelVisitor(
				documentData,
				writer,
				marshaller);
		visitor.visit();
	}
}
