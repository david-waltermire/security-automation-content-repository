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

import javax.xml.bind.JAXBElement;

import org.scapdev.content.annotation.Indirect;
import org.scapdev.content.annotation.KeyRef;
import org.scapdev.jaxb.reflection.model.JAXBClass;
import org.scapdev.jaxb.reflection.model.JAXBProperty;
import org.scapdev.jaxb.reflection.model.instance.DefaultInstanceVisitor;

public class JAXBRelationshipIdentifyingImportVisitor extends DefaultInstanceVisitor {
	private final MutableEntity owningEntity;
	private final MetadataModel metadataModel;

	public JAXBRelationshipIdentifyingImportVisitor(MutableEntity owningEntity, JAXBElement<Object> node, MetadataModel metadataModel) {
		super(node, metadataModel.getModel());
		this.owningEntity = owningEntity;
		this.metadataModel = metadataModel;
	}
	
	@Override
	public boolean beforeNode(JAXBElement<?> instance, JAXBClass jaxbClass, JAXBProperty parentProperty) {
		return beforeNode(instance.getValue(), jaxbClass, parentProperty);
	}

	@Override
	public boolean beforeNode(Object instance, JAXBClass typeInfo, JAXBProperty parentProperty) {
		boolean processContent = true;

		if (instance != null) {
			KeyRef annotation = typeInfo.getAnnotation(KeyRef.class, true);
			if (annotation != null) {
				KeyedRelationshipInfo relationshipInfo = (KeyedRelationshipInfo) metadataModel.getRelationshipInfoByKeyRefId(annotation.id());
				try {
					Collection<? extends KeyedRelationship> relationships = relationshipInfo.newRelationships(instance, owningEntity);
					for (KeyedRelationship relationship : relationships) {
						owningEntity.addRelationship(relationship);
					}
				} catch (NullFieldValueException e) {
					// This indicates that the relationship is not properly formed
					// do nothing, ignoring the relationship
				}
				processContent = false;
			}

			Indirect indirect = typeInfo.getAnnotation(Indirect.class, true);
			if (processIndirectRelationship(instance, indirect, null)) {
				processContent = false;
			}
		}
		return processContent;
	}

	@Override
	public void beforeJAXBProperty(Object instance, JAXBProperty property) {
		Indirect indirect = property.getAnnotation(Indirect.class);
		processIndirectRelationship(instance, indirect, property);
	}

	private boolean processIndirectRelationship(Object instance, Indirect indirect, JAXBProperty property) {
		boolean result = false;
		if (indirect != null) {
			IndirectRelationshipInfo relationshipInfo = (IndirectRelationshipInfo) metadataModel.getRelationshipInfoById(indirect.id());
			try {
				Collection<IndirectRelationship> relationships = relationshipInfo.newRelationships(instance, owningEntity);
				for (IndirectRelationship relationship : relationships) {
					owningEntity.addRelationship(relationship);
				}
			} catch (NullFieldValueException e) {
				// This indicates that the relationship is not properly formed
				// do nothing, ignoring the relationship
			}
			result = true;
		}
		return result;
	}
}
