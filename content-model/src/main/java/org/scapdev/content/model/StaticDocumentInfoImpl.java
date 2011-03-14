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

import javax.xml.bind.JAXBElement;

import org.scapdev.content.model.jaxb.DocumentType;
import org.scapdev.content.model.jaxb.IdentifierMappingType;
import org.scapdev.content.model.jaxb.StaticDocumentModelType;

class StaticDocumentInfoImpl extends AbstractDocumentInfo<StaticDocumentModel> implements StaticDocumentInfo {
	private final StaticDocumentModel documentModel;

	private final KeyInfo keyInfo;
	private final EntityIdentifierMapping identifierMapping;

	protected StaticDocumentInfoImpl(DocumentType documentType, SchemaInfoImpl schema,
			JAXBMetadataModel model, InitializingJAXBClassVisitor init) {
		super(documentType, schema, model, init);

		@SuppressWarnings("unchecked")
		JAXBElement<StaticDocumentModelType> modelType = (JAXBElement<StaticDocumentModelType>) documentType.getDocumentModel();
		StaticDocumentModelType documentModelType = modelType.getValue();
		this.documentModel = new StaticDocumentModelImpl(modelType, this);

		keyInfo = new KeyInfoImpl(documentModelType.getKey(), this, model, init);
		IdentifierMappingType mapping = documentModelType.getIdentifierMapping();
		if (mapping != null) {
			identifierMapping = new EntityIdentifierMappingImpl(mapping, this);
		} else {
			identifierMapping = null;
		}
	}

	@Override
	public StaticDocumentModel getDocumentModel() {
		return documentModel;
	}

	@Override
	public EntityIdentifierMapping getEntityIdentifierMapping() {
		return identifierMapping;
	}

	@Override
	public KeyInfo getKeyInfo() {
		return keyInfo;
	}
}
