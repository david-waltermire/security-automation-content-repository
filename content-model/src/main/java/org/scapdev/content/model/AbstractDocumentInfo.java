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
package org.scapdev.content.model;

import java.util.Set;

import org.scapdev.content.model.jaxb.DocumentType;
import org.scapdev.jaxb.reflection.model.JAXBClass;

public abstract class AbstractDocumentInfo<MODEL extends DocumentModel> extends AbstractSchemaComponent implements DocumentInfo {
	private final BindingInfo<org.scapdev.content.annotation.SchemaDocument> binding;

	protected AbstractDocumentInfo(DocumentType entity, SchemaInfoImpl schema, JAXBMetadataModel model, InitializingJAXBClassVisitor init) {
		super(entity.getId(), schema, entity.getSchemaNode().getNode());
		binding = init.getDocumentBindingInfo(entity.getId());
	}

	BindingInfo<org.scapdev.content.annotation.SchemaDocument> getBinding() {
		return binding;
	}

	@Override
	public JAXBClass getType() {
		return binding.getJaxbClass();
	}

	@Override
	public Set<EntityInfo> getSupportedEntityInfos() {
		return getDocumentModel().getSupportedEntityInfos(getSchemaInfo().getMetadataModel());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AbstractDocumentInfo<?>))
			return false;
		AbstractDocumentInfo<?> other = (AbstractDocumentInfo<?>) obj;
		if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = 13;
		result = 37 * result +  getId().hashCode();
		return result;
	}
}
