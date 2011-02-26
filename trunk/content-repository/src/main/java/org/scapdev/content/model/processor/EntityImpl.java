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
package org.scapdev.content.model.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.scapdev.content.model.AbstractEntity;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.IndirectRelationship;
import org.scapdev.content.model.MutableKeyedRelationship;
import org.scapdev.content.model.Relationship;

class EntityImpl extends AbstractEntity implements MutableEntity {
	private List<Relationship> relationships;
	private List<MutableKeyedRelationship> keyedRelationships;
	private List<IndirectRelationship> indirectRelationships;

	public EntityImpl(EntityInfo entityInfo, JAXBElement<Object> object) {
		super(entityInfo.getKeyInfo().getKey(object.getValue()), entityInfo, object);
		relationships = Collections.emptyList();
		keyedRelationships = Collections.emptyList();
		indirectRelationships = Collections.emptyList();
	}

	/**
	 * @return the relationships
	 */
	public List<Relationship> getRelationships() {
		synchronized (this) {
			return relationships;
		}
	}

	@Override
	public List<IndirectRelationship> getIndirectRelationships() {
		synchronized (this) {
			return indirectRelationships;
		}
	}

	@Override
	public List<MutableKeyedRelationship> getKeyedRelationships() {
		synchronized (this) {
			return keyedRelationships;
		}
	}

	/**
	 * @param relationships the relationships to set
	 */
	public void setRelationships(List<MutableKeyedRelationship> keyedRelationships, List<IndirectRelationship> indirectRelationships) {
		synchronized (this) {
			this.keyedRelationships = Collections.unmodifiableList(keyedRelationships);
			this.indirectRelationships = Collections.unmodifiableList(indirectRelationships);

			List<Relationship> localRelationships = new ArrayList<Relationship>(keyedRelationships.size()+indirectRelationships.size());
			localRelationships.addAll(keyedRelationships);
			localRelationships.addAll(indirectRelationships);
			this.relationships = Collections.unmodifiableList(localRelationships);
		}
	}
}
