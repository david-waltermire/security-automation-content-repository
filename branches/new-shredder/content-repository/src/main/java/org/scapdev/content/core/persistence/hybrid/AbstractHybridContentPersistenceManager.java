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

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.shredder.metamodel.IMetadataModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scapdev.content.core.query.EntityStatistic;

public abstract class AbstractHybridContentPersistenceManager implements HybridContentPersistenceManager, ContentRetrieverFactory {

	private final MetadataStore metadataStore;
	private final ContentStore contentStore;

	public AbstractHybridContentPersistenceManager(MetadataStore metadataStore, ContentStore contentStore) {
		this.metadataStore = metadataStore;
		this.contentStore = contentStore;
	}

	public IEntity<?> getEntityByKey(IKey key, IMetadataModel model) {
		return generateEntity(key, model);
	}

	public void storeEntities(List<? extends IEntity<?>> entities, IMetadataModel model) {
		Map<String, IEntity<?>> contentIdToEntityMap = contentStore.persist(entities, model);
		metadataStore.persist(contentIdToEntityMap, model);
	}

	public ContentRetriever newContentRetriever(String contentId, IMetadataModel model) {
		return contentStore.getContentRetriever(contentId, model);
	}

	private IEntity<?> generateEntity(IKey key, IMetadataModel model) {
		return metadataStore.getEntity(key, this, model);
	}


	public Set<IKey> getKeysForIndirectIds(String indirectType, Collection<String> indirectIds, Set<String> entityType) {
		return metadataStore.getKeysForIndirectIds(indirectType, indirectIds, entityType);
	}

	@Override
	public Map<String, ? extends EntityStatistic> getEntityStatistics(
			Set<String> entityInfoIds, IMetadataModel model) {
		return metadataStore.getEntityStatistics(entityInfoIds, model);
	}

	@Override
	public void shutdown() {
		metadataStore.shutdown();
		contentStore.shutdown();
	}

}