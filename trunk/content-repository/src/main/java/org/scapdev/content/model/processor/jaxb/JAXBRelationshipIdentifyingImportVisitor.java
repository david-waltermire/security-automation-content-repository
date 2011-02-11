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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.scapdev.content.annotation.KeyRef;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.Relationship;
import org.scapdev.content.model.RelationshipInfo;
import org.scapdev.content.model.jaxb.JAXBMetadataModel;
import org.scapdev.jaxb.reflection.instance.DefaultInstanceVisitor;
import org.scapdev.jaxb.reflection.model.DefaultTypeInfo;
import org.scapdev.jaxb.reflection.model.jaxb.DefaultModel;
import org.scapdev.jaxb.reflection.model.jaxb.DefaultPropertyInfo;

public class JAXBRelationshipIdentifyingImportVisitor extends DefaultInstanceVisitor<DefaultModel, DefaultTypeInfo, DefaultPropertyInfo> {
	private final Entity<Object> owningEntity;
	private final JAXBMetadataModel metadataModel;
	private final List<Relationship<Object, ?>> relationships;

	public JAXBRelationshipIdentifyingImportVisitor(Entity<Object> owningEntity, Object node, JAXBMetadataModel metadataModel) {
		super(node, metadataModel.getModel());
		this.owningEntity = owningEntity;
		this.metadataModel = metadataModel;
		relationships = new LinkedList<Relationship<Object, ?>>();
	}

	public List<Relationship<Object, ?>> getRelationships() {
		List<Relationship<Object, ?>> retval;
		if (relationships.isEmpty()) {
			retval = Collections.emptyList();
		} else {
			retval = Collections.unmodifiableList(relationships);
		}
		return retval;
	}

	@Override
	public boolean beforeNode(Object instance, DefaultTypeInfo typeInfo) {
		boolean processContent = true;

		KeyRef annotation = typeInfo.getAnnotation(KeyRef.class, true);
		if (annotation != null) {
			RelationshipInfo<Object> relationshipInfo = metadataModel.getRelationshipByKeyRefId(annotation.id());
			Relationship<Object, ?> relationship = relationshipInfo.newRelationship(instance, owningEntity);
			relationships.add(relationship);
			processContent = false;
		}
		return processContent;
	}

}
