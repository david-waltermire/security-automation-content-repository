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

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.scapdev.content.annotation.Entity;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.JAXBMetadataModel;
import org.scapdev.content.model.MetadataModel;
import org.scapdev.jaxb.reflection.model.JAXBClass;
import org.scapdev.jaxb.reflection.model.JAXBProperty;
import org.scapdev.jaxb.reflection.model.instance.DefaultInstanceVisitor;

class ImportVisitor extends DefaultInstanceVisitor {
	private final JAXBEntityProcessor processor;
	private final MetadataModel metadataModel;
	private ImportData state;

	public ImportVisitor(Object document, JAXBEntityProcessor processor, MetadataModel metadataModel) {
		super(document, metadataModel.getModel());
		this.processor = processor;
		this.metadataModel = metadataModel;
	}

	public ImportVisitor(JAXBElement<Object> document, JAXBEntityProcessor processor, JAXBMetadataModel metadataModel) {
		super(document, metadataModel.getModel());
		this.processor = processor;
		this.metadataModel = metadataModel;
	}

	public ImportData importContent() {
		state = new ImportData();
		visit();
		return state;
	}

	@Override
	public boolean beforeNode(JAXBElement<?> instance, JAXBClass jaxbClass, JAXBProperty parentProperty) {
		boolean processContent = true;
		
		Entity entity = jaxbClass.getAnnotation(Entity.class, true);
		if (entity != null) {
			EntityInfo entityInfo = metadataModel.getEntityById(entity.id());

			@SuppressWarnings("unchecked")
			JAXBElement<Object> element = (JAXBElement<Object>)instance;
			state.newEntity(entityInfo, element, processor);
			processContent = false;
		}
		return processContent;
	}

	@Override
	public boolean beforeNode(Object instance, JAXBClass jaxbClass, JAXBProperty parentProperty) {
		boolean processContent = true;
		
		Entity entity = jaxbClass.getAnnotation(Entity.class, true);
		if (entity != null) {
			EntityInfo entityInfo = metadataModel.getEntityById(entity.id());
			QName qname = entityInfo.getQName();
			if (qname == null) {
				parentProperty.getQName();
			}
			if (qname == null) {
				// TODO: extract ObjectFactory information for use with parentProperty.getQName();
				throw new UnsupportedOperationException("Class is not a global element: "+instance.getClass().getName());
			}
			@SuppressWarnings("unchecked")
			JAXBElement<Object> element = new JAXBElement<Object>(
					qname,
					(Class<Object>) jaxbClass.getType(),
					null,
					instance);
			state.newEntity(entityInfo, element, processor);
			processContent = false;
		}
		return processContent;
	}

}
