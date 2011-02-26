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
package org.scapdev.content.core.persistence.hybrid;

import java.util.Collection;
import java.util.Collections;

import javax.xml.bind.JAXBElement;

import org.scapdev.content.model.Entity;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.IndirectRelationship;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.KeyedRelationship;
import org.scapdev.content.model.Relationship;

public abstract class AbstractPersistedEntity implements Entity {
	private final Key key;
	private final EntityInfo entityInfo;
	private final Collection<Relationship> relationships;
	private final Collection<KeyedRelationship> keyedRelationships;
	private final Collection<IndirectRelationship> indirectRelationships;
	private final ContentRetriever retriever;

	public AbstractPersistedEntity(EntityDescriptor desc, ContentRetriever retriever) {
		this.key = desc.getKey();
		this.relationships = Collections.unmodifiableCollection(desc.getRelationships());
		this.keyedRelationships = Collections.unmodifiableCollection(desc.getKeyedRelationships());
		this.indirectRelationships = Collections.unmodifiableCollection(desc.getIndirectRelationships());
		this.entityInfo = desc.getEntityInfo();
		this.retriever = retriever;
	}

	@Override
	public Key getKey() {
		return key;
	}

	/**
	 * @return the entityInfo
	 */
	public EntityInfo getEntityInfo() {
		return entityInfo;
	}

	@Override
	public Collection<Relationship> getRelationships() {
		return relationships;
	}

	@Override
	public Collection<IndirectRelationship> getIndirectRelationships() {
		return indirectRelationships;
	}

	@Override
	public Collection<KeyedRelationship> getKeyedRelationships() {
		return keyedRelationships;
	}

	@Override
	public JAXBElement<Object> getObject() {
		return retriever.getContent();
	}

}
