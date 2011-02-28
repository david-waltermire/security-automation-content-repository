/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 paul
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
package org.scapdev.content.core.persistence.semantic.translation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.scapdev.content.core.persistence.hybrid.ContentRetriever;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.IndirectRelationship;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.KeyedRelationship;
import org.scapdev.content.model.Relationship;

/**
 * An entity that was rebuilt from within the metadata repository workflow
 *
 */
class RebuiltEntity implements Entity {
	private Key key;
	private EntityInfo entityInfo;
	private Collection<KeyedRelationship> keyedRelationships = new LinkedList<KeyedRelationship>();
	private Collection<IndirectRelationship> indirectRelationships = new LinkedList<IndirectRelationship>();
	private ContentRetriever retriever;

	RebuiltEntity() {
	}

	@Override
	public Key getKey() {
		return key;
	}
	
	void setKey(Key key) {
		this.key = key;
	}

	/**
	 * @return the entityInfo
	 */
	public EntityInfo getEntityInfo() {
		return entityInfo;
	}
	
	void setEntityInfo(EntityInfo entityInfo) {
		this.entityInfo = entityInfo;
	}

	@Override
	public Collection<Relationship> getRelationships() {
		List<Relationship> rels = new LinkedList<Relationship>();
		rels.addAll(indirectRelationships);
		rels.addAll(keyedRelationships);
		return rels;
	}

	@Override
	public Collection<IndirectRelationship> getIndirectRelationships() {
		return Collections.unmodifiableCollection(indirectRelationships);
	}
	
	void addIndirectRelationship(IndirectRelationship rel){
		indirectRelationships.add(rel);
	}

	@Override
	public Collection<KeyedRelationship> getKeyedRelationships() {
		return Collections.unmodifiableCollection(keyedRelationships);
	}

	@Override
	public JAXBElement<Object> getObject() {
		return retriever.getContent();
	}
	
	void setRetriever(ContentRetriever retriever) {
		this.retriever = retriever;
	}


}
