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
import java.util.List;
import java.util.Set;

import org.scapdev.content.core.ContentException;
import org.scapdev.content.core.persistence.AbstractContentPersistenceManager;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.MetadataModel;
import org.scapdev.content.model.Relationship;

public class DefaultHybridContentPersistenceManager extends AbstractContentPersistenceManager implements HybridContentPersistenceManager {
	final MetadataStore metadataStore;
	private final ContentStore contentStore;

	public DefaultHybridContentPersistenceManager(MetadataModel model) {
		super(model);
		this.metadataStore = new TripleStoreFacadeMetaDataManager();
		this.contentStore = new MemoryResidentContentStore();
	}

	protected DefaultHybridContentPersistenceManager(MetadataStore metadataStore, ContentStore contentStore, MetadataModel model) {
		super(model);
		this.metadataStore = metadataStore;
		this.contentStore = contentStore;
	}

	@Override
	public Entity getEntityByKey(Key key) {
		return generateEntity(key);
	}

	@Override
	public void storeEntity(Entity entity) throws ContentException {
		String contentId = contentStore.persist(entity, getMetadataModel());
		metadataStore.persist(entity, contentId);
	}

	public EntityDescriptor getEntityDescriptorByKey(Key key) {
		return metadataStore.getEntityDescriptor(key);
	}
	
	public List<Relationship> getRelationshipsByKey(Key key) {
		return getEntityDescriptorByKey(key).getRelationships();
	}

	private Entity generateEntity(Key key) {
		EntityDescriptor desc = metadataStore.getEntityDescriptor(key);
		return newEntity(desc, contentStore.getContentRetriever(desc.getContentId(), getMetadataModel()));
	}

	protected Entity newEntity(EntityDescriptor desc, ContentRetriever retriever) {
		return new AbstracPersistedtEntity(desc, retriever) {};
	}

	@Override
	public Set<Key> getKeysForIndirectIds(String indirectType, Collection<String> indirectIds, Set<String> entityType) {
		return metadataStore.getKeysForIndirectIds(indirectType, indirectIds, entityType);
	}
	
}
