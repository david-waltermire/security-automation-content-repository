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
package org.scapdev.content.model.jaxb;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.scapdev.content.annotation.KeyRef;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.NullFieldValueException;
import org.scapdev.content.model.Relationship;
import org.scapdev.content.model.RelationshipInfo;
import org.scapdev.jaxb.reflection.instance.DefaultInstanceVisitor;
import org.scapdev.jaxb.reflection.model.JAXBClass;

public class JAXBRelationshipIdentifyingImportVisitor extends DefaultInstanceVisitor {
	private final Entity owningEntity;
	private final JAXBMetadataModel metadataModel;
	private final List<Relationship> relationships;

	public JAXBRelationshipIdentifyingImportVisitor(Entity owningEntity, JAXBElement<Object> node, JAXBMetadataModel metadataModel) {
		super(node, metadataModel.getModel());
		this.owningEntity = owningEntity;
		this.metadataModel = metadataModel;
		relationships = new LinkedList<Relationship>();
	}

	public List<Relationship> getRelationships() {
		List<Relationship> retval;
		if (relationships.isEmpty()) {
			retval = Collections.emptyList();
		} else {
			retval = Collections.unmodifiableList(relationships);
		}
		return retval;
	}

	@Override
	public boolean beforeNode(JAXBElement<?> instance, JAXBClass jaxbClass) {
		return beforeNode(instance.getValue(), jaxbClass);
	}

	@Override
	public boolean beforeNode(Object instance, JAXBClass typeInfo) {
		boolean processContent = true;

		if (instance != null) {
			KeyRef annotation = typeInfo.getAnnotation(KeyRef.class, true);
			if (annotation != null) {
				RelationshipInfo relationshipInfo = metadataModel.getRelationshipByKeyRefId(annotation.id());
				try {
					Relationship relationship = relationshipInfo.newRelationship(instance, owningEntity);
					relationships.add(relationship);
				} catch (NullFieldValueException e) {
					// This indicates that the relationship is not properly formed
					// do nothing, ignoring the relationship
				}
				processContent = false;
			}
		}
		return processContent;
	}

}
