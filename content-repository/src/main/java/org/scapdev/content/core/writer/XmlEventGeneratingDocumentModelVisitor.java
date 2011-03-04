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

import java.util.Arrays;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.scapdev.content.annotation.EntityContainer;
import org.scapdev.content.annotation.Generated;
import org.scapdev.content.annotation.GeneratorType;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.GeneratedPropertyRefInfo;
import org.scapdev.content.model.ModelInstanceException;
import org.scapdev.jaxb.reflection.model.JAXBClass;
import org.scapdev.jaxb.reflection.model.JAXBModelException;
import org.scapdev.jaxb.reflection.model.JAXBProperty;
import org.scapdev.jaxb.reflection.model.visitor.CyclePreventingModelVisitor;

public class XmlEventGeneratingDocumentModelVisitor extends CyclePreventingModelVisitor {
//	private static final Logger log = Logger.getLogger(XmlEventGeneratingDocumentModelVisitor.class);

	private final GeneratedDocumentData documentData;
	private final XMLStreamWriter writer;
	private final Marshaller marshaller;

	public XmlEventGeneratingDocumentModelVisitor(
			GeneratedDocumentData documentData,
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
//			log.trace("writeStartElement: "+qname.toString());
			writer.writeStartElement(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
			visit(documentData.getDocumentInfo().getType());
			writer.writeEndElement();
//			log.trace("writeEndElement: "+qname.toString());
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
//					log.info("    writeStartElement: "+entity.getKey().toString());
					marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
					marshaller.marshal(entity.getObject(), writer);
//					log.info("    writeEndElement: "+entity.getKey().toString());
				} catch (JAXBException e) {
					throw new ModelInstanceException(e);
				}
			}
		} else {
			Generated generated = property.getAnnotation(Generated.class);
			if (generated != null) {
				String generatedId = generated.id();

				// Write the element or attribute
				QName qname = property.getQName();
				try {
//					log.info("  writeStartElement: "+qname.toString());
					if (property.isElement()) {
						writer.writeStartElement(qname.getPrefix(), qname.getLocalPart(), qname.getNamespaceURI());
						String value = getValue(generatedId, property);
						if (value != null) {
							writer.writeCharacters(value);
						} else {
							processChild = true;
						}
					} else if (property.isAttribute()) {
						writer.writeAttribute(qname.getPrefix(), qname.getNamespaceURI(), qname.getLocalPart(), getValue(generatedId, property));
					} else {
						throw new UnsupportedOperationException();
					}
				} catch (XMLStreamException e) {
					throw new ModelInstanceException(e);
				}
			}
		}
		return processChild;
	}

	private String getValue(String generatedId, JAXBProperty property) {
		GeneratedPropertyRefInfo generatedPropertyRefInfo = documentData.getDocumentInfo().getDocumentModel().getGeneratedPropertyRefInfo(generatedId);
		if (generatedPropertyRefInfo == null) {
			throw new ModelInstanceException("Unable to locate generated property info '"+generatedId+"' for property: "+property.toString());
		}
		GeneratedPropertyRefInfo.Value value = generatedPropertyRefInfo.getValue();

		String result = null;
		if (value != null) {
			GeneratorType type = value.getType();
			switch (type) {
			case STATIC:
				result = value.getValue();
				break;
			default:
				boolean match = false;
				ContextBean contextBean = ContextBeanFactory.getContextBean(ContextBeanFactory.Context.valueOf(type.toString()));
				if (contextBean != null) {
					PropertyHandler handler = contextBean.getPropertyHandler(value.getValue());
					if (handler != null) {
						result = handler.getValue(value, documentData);
						match = true;
					}
				}
				if (!match) {
					throw new UnsupportedOperationException(value.toString());
				}
			}
		}
		return result;
	}

	@Override
	public void afterJAXBProperty(JAXBProperty property) {
		EntityContainer container = property.getAnnotation(EntityContainer.class);
		if (container == null) {
			Generated generated = property.getAnnotation(Generated.class);
			if (generated != null) {
				if (property.isElement()) {
					try {
						writer.writeEndElement();
					} catch (XMLStreamException e) {
						throw new ModelInstanceException(e);
					}
				}
			}
		}
	}
}
