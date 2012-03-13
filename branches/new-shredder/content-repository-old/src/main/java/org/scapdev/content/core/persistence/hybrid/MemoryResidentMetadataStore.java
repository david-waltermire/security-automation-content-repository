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
import gov.nist.scap.content.model.IBoundaryIdentifierRelationship;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IRelationship;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.IRelationshipDefinition;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scapdev.content.core.query.EntityStatistic;
import org.scapdev.content.core.query.RelationshipStatistic;

public class MemoryResidentMetadataStore implements MetadataStore {
//	private static final Logger log = Logger.getLogger(MemoryResidentMetadataStore.class);
	private final Map<IKey, IEntity<?>> descriptorMap;
	private final Map<String, Map<String, List<IEntity<?>>>> externalIdentifierToValueMap;

	public MemoryResidentMetadataStore() {
		this.descriptorMap = new HashMap<IKey, IEntity<?>>();
		this.externalIdentifierToValueMap = new HashMap<String, Map<String, List<IEntity<?>>>>();
	}

	@Override
	public IEntity<?> getEntity(IKey key, ContentRetrieverFactory contentRetrieverFactory, IMetadataModel model) {
		return descriptorMap.get(key);
	}


	@Override
	public Set<IKey> getKeysForIndirectIds(String indirectType, Collection<String> indirectIds, Set<String> entityTypes) {
		Set<IKey> result = Collections.emptySet();
		Map<String, List<IEntity<?>>> indirectValueToDescriptorsMap = externalIdentifierToValueMap.get(indirectType);
		if (indirectValueToDescriptorsMap != null) {
			result = new HashSet<IKey>();
			for (String indirectId : indirectIds) {
				List<IEntity<?>> entities = indirectValueToDescriptorsMap.get(indirectId);
				if (entities != null) {
					for (IEntity<?> entity : entities) {
						if (entityTypes.isEmpty() || entityTypes.contains(entity.getDefinition().getId())) {
							result.add(entity.getKey());
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public void persist(Map<String, IEntity<?>> contentIdToEntityMap, IMetadataModel model) {
		for (Map.Entry<String, IEntity<?>> entry : contentIdToEntityMap.entrySet()) {
			persist(entry.getValue(), entry.getKey());
		}
	}

	public void persist(IEntity<?> entity, String contentId) {
		descriptorMap.put(entity.getKey(), entity);
		
		for (IBoundaryIdentifierRelationship relationship : entity.getIndirectRelationships()) {
			IExternalIdentifier externalIdentifier = relationship.getExternalIdentifier();
			Map<String, List<IEntity<?>>> externalIdentifierValueToEntityMap = externalIdentifierToValueMap.get(externalIdentifier.getId());
			if (externalIdentifierValueToEntityMap == null) {
				externalIdentifierValueToEntityMap = new HashMap<String, List<IEntity<?>>>();
				externalIdentifierToValueMap.put(externalIdentifier.getId(), externalIdentifierValueToEntityMap);
			}
			List<IEntity<?>> descriptorList = externalIdentifierValueToEntityMap.get(relationship.getValue());
			if (descriptorList == null) {
				descriptorList = new LinkedList<IEntity<?>>();
				externalIdentifierValueToEntityMap.put(relationship.getValue(), descriptorList);
			}
			descriptorList.add(entity);
//			if (descriptorList.size() >= 2) {
//				log.trace("Found '"+descriptorList.size()+"' instances of : "+externalIdentifier.getId()+" "+externalIdentifier.getValue());
//			}
		}
	}

	@Override
	public Map<String, ? extends EntityStatistic> getEntityStatistics(
			Set<String> entityInfoIds, IMetadataModel model) {

		Map<String, InternalEntityStatistic> results = new HashMap<String, InternalEntityStatistic>();
		for (IEntity<?> entity : descriptorMap.values()) {
			String entityInfoId = entity.getDefinition().getId();
			if (entityInfoIds.contains(entityInfoId)) {
				InternalEntityStatistic stat = results.get(entityInfoId);
				if (stat == null) {
					stat = new InternalEntityStatistic(entity.getDefinition());
					results.put(entityInfoId, stat);
				}
				stat.incrementCount();
				stat.handleRelationships(entity.getRelationships());
			}
		}
		return results;
	}

	private class InternalEntityStatistic implements EntityStatistic {
		private final IEntityDefinition entityInfo;
		private int count = 0;
		private final Map<String, InternalRelationshipStatistic> relationshipStats = new HashMap<String, InternalRelationshipStatistic>();

		public InternalEntityStatistic(IEntityDefinition entityInfo) {
			this.entityInfo = entityInfo;
		}

		public void handleRelationships(Collection<? extends IRelationship<?>> relationships) {
			for (IRelationship<?> relationship : relationships) {
				IRelationshipDefinition relationshipInfo = relationship.getDefinition();
				String relationshipId = relationshipInfo.getId();
				InternalRelationshipStatistic stat = relationshipStats.get(relationshipId);
				if (stat == null) {
					stat = new InternalRelationshipStatistic(relationshipInfo);
					relationshipStats.put(relationshipId, stat);
				}
				stat.incrementCount();
			}
		}

		public void incrementCount() {
			++count;
		}

		@Override
		public int getCount() {
			return count;
		}

		@Override
		public IEntityDefinition getEntityInfo() {
			return entityInfo;
		}

		@Override
		public Map<String, ? extends RelationshipStatistic> getRelationshipInfoStatistics() {
			return Collections.unmodifiableMap(relationshipStats);
		}
		
	}

	private class InternalRelationshipStatistic implements RelationshipStatistic {
		private final IRelationshipDefinition relationshipInfo;
		private int count = 0;

		public InternalRelationshipStatistic(IRelationshipDefinition relationshipInfo) {
			this.relationshipInfo = relationshipInfo;
		}

		public void incrementCount() {
			++count;
		}

		@Override
		public int getCount() {
			return count;
		}

		@Override
		public IRelationshipDefinition getRelationshipInfo() {
			return relationshipInfo;
		}
	}

	@Override
	public void shutdown() {
	}
}
