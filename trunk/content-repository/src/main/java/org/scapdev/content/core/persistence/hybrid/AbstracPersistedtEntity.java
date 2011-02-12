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

import java.util.List;

import org.scapdev.content.model.Entity;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.Relationship;

public class AbstracPersistedtEntity<DATA> implements Entity<DATA> {
	private final Key key;
	private final EntityInfo entityInfo;
	private final List<Relationship<DATA, ?>> relationships;
	private final ContentRetriever<DATA> retriever;

	// TODO: enable the object to be retrieved using lazy fetch
	public AbstracPersistedtEntity(EntityDescriptor<DATA> desc, ContentRetriever<DATA> retriever) {
		this.key = desc.getKey();
		this.relationships = desc.getRelationships();
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
	public List<Relationship<DATA, ?>> getRelationships() {
		return relationships;
	}

	@Override
	public DATA getObject() {
		return retriever.getContent();
	}

}
