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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.scapdev.content.annotation.Indirect;
import org.scapdev.content.annotation.KeyRef;
import org.scapdev.jaxb.reflection.model.JAXBClass;
import org.scapdev.jaxb.reflection.model.JAXBProperty;
import org.scapdev.jaxb.reflection.model.instance.DefaultInstanceVisitor;

public class JAXBRelationshipIdentifyingImportVisitor extends DefaultInstanceVisitor {
	private final Entity owningEntity;
	private final MetadataModel metadataModel;
	private final List<KeyedRelationship> keyedRelationships;
	private final List<IndirectRelationship> indirectRelationships;

	public JAXBRelationshipIdentifyingImportVisitor(Entity owningEntity, JAXBElement<Object> node, MetadataModel metadataModel) {
		super(node, metadataModel.getModel());
		this.owningEntity = owningEntity;
		this.metadataModel = metadataModel;
		keyedRelationships = new LinkedList<KeyedRelationship>();
		indirectRelationships = new LinkedList<IndirectRelationship>();
	}

	public List<KeyedRelationship> getKeyedRelationships() {
		List<KeyedRelationship> retval;
		if (keyedRelationships.isEmpty()) {
			retval = Collections.emptyList();
		} else {
			retval = Collections.unmodifiableList(keyedRelationships);
		}
		return retval;
	}

	public List<IndirectRelationship>  getIndirectRelationships() {
		List<IndirectRelationship> retval;
		if (indirectRelationships.isEmpty()) {
			retval = Collections.emptyList();
		} else {
			retval = Collections.unmodifiableList(indirectRelationships);
		}
		return retval;
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
				KeyedRelationshipInfo relationshipInfo = (KeyedRelationshipInfo) metadataModel.getRelationshipByKeyRefId(annotation.id());
				try {
					List<? extends KeyedRelationship> relationships = relationshipInfo.newRelationships(instance, owningEntity);
					keyedRelationships.addAll(relationships);
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
			IndirectRelationshipInfo relationshipInfo = (IndirectRelationshipInfo) metadataModel.getRelationshipById(indirect.id());
			try {
				List<IndirectRelationship> relationships = relationshipInfo.newRelationships(instance, owningEntity);
				indirectRelationships.addAll(relationships);
			} catch (NullFieldValueException e) {
				// This indicates that the relationship is not properly formed
				// do nothing, ignoring the relationship
			}
			result = true;
		}
		return result;
	}
}
