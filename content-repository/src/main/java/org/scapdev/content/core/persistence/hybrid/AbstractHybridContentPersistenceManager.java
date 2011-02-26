/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 davidwal
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
import java.util.Set;

import org.scapdev.content.core.ContentException;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.MetadataModel;
import org.scapdev.content.model.Relationship;

public abstract class AbstractHybridContentPersistenceManager implements HybridContentPersistenceManager, ContentRetrieverFactory {

	private final MetadataStore metadataStore;
	private final ContentStore contentStore;

	public AbstractHybridContentPersistenceManager(MetadataStore metadataStore, ContentStore contentStore) {
		this.metadataStore = metadataStore;
		this.contentStore = contentStore;
	}

	@Override
	public Entity getEntityByKey(Key key, MetadataModel model) {
		return generateEntity(key, model);
	}

	@Override
	public void storeEntity(Entity entity, MetadataModel model)
			throws ContentException {
				String contentId = contentStore.persist(entity, model);
				metadataStore.persist(entity, contentId);
			}

	public EntityDescriptor getEntityDescriptorByKey(Key key) {
		return metadataStore.getEntityDescriptor(key);
	}

	public Collection<Relationship> getRelationshipsByKey(Key key) {
		return getEntityDescriptorByKey(key).getRelationships();
	}

	@Override
	public ContentRetriever newContentRetriever(String contentId, MetadataModel model) {
		return contentStore.getContentRetriever(contentId, model);
	}

	private Entity generateEntity(Key key, MetadataModel model) {
		EntityDescriptor desc = metadataStore.getEntityDescriptor(key);
		return newEntity(desc, contentStore.getContentRetriever(desc.getContentId(), model));
	}

	protected Entity newEntity(EntityDescriptor desc, ContentRetriever retriever) {
		return new AbstractPersistedEntity(desc, retriever) {};
	}

	@Override
	public Set<Key> getKeysForIndirectIds(String indirectType, Collection<String> indirectIds, Set<String> entityType) {
		return metadataStore.getKeysForIndirectIds(indirectType, indirectIds, entityType);
	}

}