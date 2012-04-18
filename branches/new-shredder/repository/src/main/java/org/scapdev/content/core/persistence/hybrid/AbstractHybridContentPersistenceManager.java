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
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.IVersion;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.ProcessingException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scapdev.content.core.ContentException;
import org.scapdev.content.core.persistence.IEntityFilter;

//import org.scapdev.content.core.query.EntityStatistic;

public abstract class AbstractHybridContentPersistenceManager implements HybridContentPersistenceManager, ContentRetrieverFactory {

	private final MetadataStore metadataStore;
	private final ContentStore contentStore;

	public AbstractHybridContentPersistenceManager(MetadataStore metadataStore, ContentStore contentStore) {
		this.metadataStore = metadataStore;
		this.contentStore = contentStore;
	}

	private static <T extends IEntity<?>> Collection<? extends T> filter(
			Collection<? extends T> entities, IEntityFilter<T> filter) {

		Collection<? extends T> retval;
		if (filter != null) {
			retval = filter.filter(entities);
		} else {
			retval = entities;
		}
		return retval;
	}

	public Collection<? extends IKeyedEntity<?>> getEntities(IKey key,
			IVersion version, IEntityFilter<IKeyedEntity<?>> filter) throws ProcessingException {
		// TODO: handle session?
		Collection<? extends IKeyedEntity<?>> result = metadataStore.getEntities(key, version);
		return filter(result, filter);
	}

	public Collection<? extends IEntity<?>> getEntities(
			IExternalIdentifier externalIdentifier,
			Set<String> boundaryObjectIds,
			Set<? extends IEntityDefinition> entityTypes,
			IEntityFilter<IEntity<?>> filter) throws ProcessingException {
		// TODO: handle session?
		Map<String, Set<? extends IKey>> result = getKeysForBoundaryIdentifier(
				externalIdentifier,
				boundaryObjectIds,
				entityTypes);

		Set<IKey> uniqueKeys = new HashSet<IKey>();
		for (Set<? extends IKey> keys : result.values()) {
			uniqueKeys.addAll(keys);
		}
		Collection<IEntity<?>> retval = new LinkedList<IEntity<?>>();
		for (IKey key : uniqueKeys) {
			retval.addAll(getEntities(key, null, null));
		}
		return filter(Collections.unmodifiableCollection(retval), filter);
	}

	@Deprecated
	public Map<String, Set<? extends IKey>> getKeysForBoundaryIdentifier(IExternalIdentifier externalIdentifier, Collection<String> boundaryObjectIds, Set<? extends IEntityDefinition> entityTypes) {
		// TODO: handle session?
		return metadataStore.getKeysForBoundaryIdentifier(externalIdentifier, boundaryObjectIds, entityTypes);
	}

	public void storeEntities(List<? extends IEntity<?>> entities) throws ContentException {
	    Object session = new Object();

		Map<String, IEntity<?>> contentIdToEntityMap = null;
        try {
            contentIdToEntityMap = contentStore.persist(entities, session);
        } catch (ContentException e) {
            contentStore.rollback(session);
            throw e;
        }

        try {
            metadataStore.persist(contentIdToEntityMap);
        } catch ( ContentException e ) {
            metadataStore.rollback(session);
            contentStore.rollback(session);
        }
        contentStore.commit(session);
        metadataStore.commit(session);
	}

	public ContentRetriever newContentRetriever(String contentId) {
		return contentStore.getContentRetriever(contentId);
	}

    public MetadataStore getMetadataStore() {
       return metadataStore;
    }

    public ContentStore getContentStore() {
        return contentStore;
    }

//	@Override
//	public Map<String, ? extends EntityStatistic> getEntityStatistics(
//			Set<String> entityInfoIds, IMetadataModel model) {
//		return metadataStore.getEntityStatistics(entityInfoIds, model);
//	}

	@Override
	public void shutdown() {
		metadataStore.shutdown();
		contentStore.shutdown();
	}

}