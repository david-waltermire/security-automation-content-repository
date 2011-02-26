/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 davidwal
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

import java.util.Arrays;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.scapdev.content.annotation.EntityContainer;
import org.scapdev.content.annotation.Generated;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.ModelInstanceException;
import org.scapdev.jaxb.reflection.model.JAXBClass;
import org.scapdev.jaxb.reflection.model.JAXBModelException;
import org.scapdev.jaxb.reflection.model.JAXBProperty;
import org.scapdev.jaxb.reflection.model.visitor.CyclePreventingModelVisitor;

public class XmlEventGeneratingDocumentModelVisitor extends CyclePreventingModelVisitor {
	private static final Logger log = Logger.getLogger(XmlEventGeneratingDocumentModelVisitor.class);

	private final DocumentData documentData;
	private final XMLStreamWriter writer;
	private final Marshaller marshaller;

	public XmlEventGeneratingDocumentModelVisitor(
			DocumentData documentData,
			XMLStreamWriter writer,
			Marshaller marshaller) {
		super(documentData.getDocumentInfo().getType().getJAXBPackage().getJAXBModel());
		this.documentData = documentData;
		this.writer = writer;
		this.marshaller = marshaller;
	}

	public void visit() throws JAXBModelException {
		QName qname = getDocumentQName();
		try {
			log.info("writeStartElement: "+qname.toString());
			writer.writeStartElement(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
			visit(documentData.getDocumentInfo().getType());
			writer.writeEndElement();
			log.info("writeEndElement: "+qname.toString());
		} catch (XMLStreamException e) {
			throw new JAXBModelException(e);
		}
	}

	private QName getDocumentQName() {
		JAXBClass jaxbClass = documentData.getDocumentInfo().getType();
		return jaxbClass.getQName();
	}

	@Override
	public boolean beforeNode(JAXBClass jaxbClass) {
		return true;
	}

	@Override
	public void afterNode(JAXBClass jaxbClass) {
	}

	@Override
	public boolean beforeJAXBClass(JAXBClass typeInfo) {
		return true;
	}

	@Override
	public void afterJAXBClass(JAXBClass typeInfo) {
	}

	@Override
	public boolean beforeJAXBProperty(JAXBProperty property) {
		boolean processChild = false;

		EntityContainer container = property.getAnnotation(EntityContainer.class);
		if (container != null) {
			// Write out matching entity
			for (Entity entity : documentData.getEntities(container.id(), Arrays.asList(container.entityIds()))) {
				try {
					log.info("    writeStartElement: "+entity.getKey().toString());
					marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
					marshaller.marshal(entity.getObject(), writer);
					log.info("    writeEndElement: "+entity.getKey().toString());
				} catch (JAXBException e) {
					throw new ModelInstanceException(e);
				}
			}
		} else {
			Generated generated = property.getAnnotation(Generated.class);
			if (generated != null) {
				switch (generated.type()) {
				case INSTANCE:
					processChild = true;
					QName qname = property.getQName();
					try {
						log.info("  writeStartElement: "+qname.toString());
						writer.writeStartElement(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
					} catch (XMLStreamException e) {
						throw new ModelInstanceException(e);
					}
					break;
				case STATIC:
				case VALUE:
				default:
					throw new UnsupportedOperationException();
				}
			}
		}
		return processChild;
	}

	@Override
	public void afterJAXBProperty(JAXBProperty property) {
		EntityContainer container = property.getAnnotation(EntityContainer.class);
		if (container != null) {
			// Write out matching entity
		} else {
			Generated generated = property.getAnnotation(Generated.class);
			if (generated != null) {
				switch (generated.type()) {
				case INSTANCE:
					QName qname = property.getQName();
					try {
						log.info("  writeEndElement: "+qname.toString());
						writer.writeEndElement();
					} catch (XMLStreamException e) {
						throw new ModelInstanceException(e);
					}
					break;
				case STATIC:
				case VALUE:
				default:
					throw new UnsupportedOperationException();
				}
			}
		}
	}
}
