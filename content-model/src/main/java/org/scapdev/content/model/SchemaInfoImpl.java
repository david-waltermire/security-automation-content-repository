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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.scapdev.content.model.jaxb.DocumentEntityType;
import org.scapdev.content.model.jaxb.EntityType;
import org.scapdev.content.model.jaxb.LocalRelationshipType;
import org.scapdev.content.model.jaxb.RelationshipType;
import org.scapdev.content.model.jaxb.SchemaType;
import org.scapdev.jaxb.reflection.model.JAXBClass;
import org.scapdev.jaxb.reflection.model.JAXBModelException;

class SchemaInfoImpl implements SchemaInfo {
	private String id;
	private String namespace;
	private String schemaLocation;
	private String prefix;
	private String schemaNode;
	private Map<JAXBClass, EntityInfoImpl> entityMap;
	private Map<JAXBClass, DocumentInfo> documentMap;
	private Map<JAXBClass, RelationshipInfo> relationshipMap;

	SchemaInfoImpl(SchemaType type, JAXBMetadataModel metadataModel, InitializingJAXBClassVisitor init) {
		this.id = type.getId();
		this.namespace = type.getNamespace();
		this.schemaLocation = type.getSchemaLocation();
		this.prefix = type.getPrefix();
		this.schemaNode = type.getSchemaNode().getNode();

		documentMap = new HashMap<JAXBClass, DocumentInfo>();
		for (DocumentEntityType node : type.getDocuments().getDocument()) {
			BindingInfo<org.scapdev.content.annotation.SchemaDocument> binding = init.getDocumentBindingInfo(node.getId());
			org.scapdev.content.annotation.SchemaDocument annotation = binding.getAnnotation();
			AbstractDocumentBase document;
			switch (annotation.type()) {
			case GENERATED:
				document = new GeneratedDocumentInfoImpl(node, this, metadataModel, init);
				break;
			default:
				throw new UnsupportedOperationException("Document Type: "+annotation.type().toString());
			}

			metadataModel.registerDocument(document);
			documentMap.put(binding.getJaxbClass(), document);
			
		}

		entityMap = new HashMap<JAXBClass, EntityInfoImpl>();
		for (EntityType node : type.getEntities().getEntity()) {
			EntityInfoImpl entity = new EntityInfoImpl(node, this, metadataModel, init);
			metadataModel.registerEntity(entity);
			entityMap.put(entity.getBinding().getJaxbClass(), entity);
		}

		relationshipMap = new HashMap<JAXBClass, RelationshipInfo>();
		for (JAXBElement<? extends RelationshipType> element : type.getRelationships().getRelationship()) {
			RelationshipInfo relationship;
			JAXBClass typeInfo;
			if (element.getDeclaredType().isAssignableFrom(LocalRelationshipType.class)) {
				LocalRelationshipType node = (LocalRelationshipType)element.getValue();
				LocalRelationshipInfoImpl relationshipInfo = new LocalRelationshipInfoImpl(node, this, metadataModel, init);
				typeInfo = relationshipInfo.getKeyRefInfo().getBinding().getJaxbClass();
				relationship = relationshipInfo;
			} else {
				UnsupportedOperationException e = new UnsupportedOperationException("Unsupported relationship: "+element.getDeclaredType().getName());
				throw new JAXBModelException(e);
			}
			metadataModel.registerRelationship(typeInfo, relationship);
			relationshipMap.put(typeInfo, relationship);
		}
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public String getSchemaNode() {
		return schemaNode;
	}

	@Override
	public String getSchemaLocation() {
		return schemaLocation;
	}

	@Override
	public SchemaInfo getSchemaInfo() {
		return this;
	}

	@Override
	public Collection<DocumentInfo> getDocumentInfos() {
		return documentMap.values();
	}
}
