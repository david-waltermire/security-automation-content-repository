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

import gov.nist.scap.content.definitions.IEntityDefinition;
import gov.nist.scap.content.model.IContentHandle;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IIndirectRelationship;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedRelationship;
import gov.nist.scap.content.model.IRelationship;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An entity that was rebuilt from within the metadata repository workflow
 *
 */
class RebuiltEntity<DEFINITION extends IEntityDefinition> implements IEntity<DEFINITION> {
	private IKey key;
	private DEFINITION entityInfo;
	private Collection<IKeyedRelationship> keyedRelationships = new LinkedList<IKeyedRelationship>();
	private Collection<IIndirectRelationship> indirectRelationships = new LinkedList<IIndirectRelationship>();
	private IContentHandle contentHandle;

	RebuiltEntity() {
	}

	public IEntity<?> getParentContext() {
		throw new UnsupportedOperationException("implement");
	}

	public IKey getKey() {
		return key;
	}
	
	void setKey(IKey key) {
		this.key = key;
	}

	public DEFINITION getDefinition() {
		return entityInfo;
	}
	
	void setEntityInfo(DEFINITION entityInfo) {
		this.entityInfo = entityInfo;
	}

	public Collection<? extends IRelationship<?>> getRelationships() {
		List<IRelationship<?>> rels = new LinkedList<IRelationship<?>>();
		rels.addAll(indirectRelationships);
		rels.addAll(keyedRelationships);
		return Collections.unmodifiableCollection(rels);
	}

	public Collection<IIndirectRelationship> getIndirectRelationships() {
		return Collections.unmodifiableCollection(indirectRelationships);
	}
	
	void addIndirectRelationship(IIndirectRelationship rel){
		indirectRelationships.add(rel);
	}

	public Collection<? extends IKeyedRelationship> getKeyedRelationships() {
		return Collections.unmodifiableCollection(keyedRelationships);
	}
	
	void addKeyedRelationship(IKeyedRelationship rel){
		keyedRelationships.add(rel);
	}

	public IContentHandle getContentHandle() {
		return contentHandle;
	}

	void setContentHandle(IContentHandle handle) {
		this.contentHandle = handle;
	}
}
