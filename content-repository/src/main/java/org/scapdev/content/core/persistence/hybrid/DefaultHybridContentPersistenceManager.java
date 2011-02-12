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

import org.scapdev.content.core.ContentException;
import org.scapdev.content.core.persistence.AbstractContentPersistenceManager;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.Relationship;

public class DefaultHybridContentPersistenceManager extends AbstractContentPersistenceManager implements HybridContentPersistenceManager {
	final MetadataStore<Object> metadataStore;
	private final ContentStore contentStore;

	public DefaultHybridContentPersistenceManager() {
		this.metadataStore = new MemoryResidentMetadataStore();
		this.contentStore = new MemoryResidentContentStore();
	}

	protected DefaultHybridContentPersistenceManager(MetadataStore<Object> metadataStore, ContentStore contentStore) {
		this.metadataStore = metadataStore;
		this.contentStore = contentStore;
	}

	@Override
	public Entity<Object> getEntityByKey(Key key) {
		return generateEntity(key);
	}

	@Override
	public void storeEntity(Entity<Object> entity) throws ContentException {
		String contentId = contentStore.persist(entity.getObject());
		metadataStore.persist(entity, contentId);
	}

	public EntityDescriptor<Object> getEntityDescriptorByKey(Key key) {
		return metadataStore.getEntityDescriptor(key);
	}
	
	public List<Relationship<Object, ?>> getRelationshipsByKey(Key key) {
		return getEntityDescriptorByKey(key).getRelationships();
	}

	private Entity<Object> generateEntity(Key key) {
		EntityDescriptor<Object> desc = metadataStore.getEntityDescriptor(key);
		return newEntity(desc, contentStore.getContentRetriever(desc.getContentId()));
	}

	protected Entity<Object> newEntity(EntityDescriptor<Object> desc, ContentRetriever<Object> retriever) {
		return new AbstracPersistedtEntity<Object>(desc, retriever) {};
	}
}
