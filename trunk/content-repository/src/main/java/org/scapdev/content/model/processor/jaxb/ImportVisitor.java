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
package org.scapdev.content.model.processor.jaxb;

import org.scapdev.content.annotation.Entity;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.jaxb.JAXBMetadataModel;
import org.scapdev.jaxb.reflection.instance.DefaultInstanceVisitor;
import org.scapdev.jaxb.reflection.model.DefaultTypeInfo;
import org.scapdev.jaxb.reflection.model.jaxb.DefaultModel;
import org.scapdev.jaxb.reflection.model.jaxb.DefaultPropertyInfo;

class ImportVisitor extends DefaultInstanceVisitor<DefaultModel, DefaultTypeInfo, DefaultPropertyInfo> {
	private final JAXBEntityProcessor processor;
	private final JAXBMetadataModel metadataModel;
	private ImportData state;

	public ImportVisitor(Object document, JAXBEntityProcessor processor, JAXBMetadataModel metadataModel) {
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
	public boolean beforeNode(Object instance, DefaultTypeInfo typeInfo) {
		boolean processContent = true;
		
		Entity entity = typeInfo.getAnnotation(Entity.class, true);
		if (entity != null) {
			EntityInfo entityInfo = metadataModel.getEntityById(entity.id());
			state.newEntity(entityInfo, instance, processor);
			processContent = false;
		}
		return processContent;
	}

}
