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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scapdev.content.model.Entity;
import org.scapdev.content.model.ExternalIdentifier;
import org.scapdev.content.model.ExternalIdentifierInfo;
import org.scapdev.content.model.IndirectRelationship;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.MetadataModel;

public class MemoryResidentMetadataStore implements MetadataStore {
//	private static final Logger log = Logger.getLogger(MemoryResidentMetadataStore.class);
	private final Map<Key, Entity> descriptorMap;
	private final Map<String, Map<String, List<Entity>>> externalIdentifierToValueMap;

	public MemoryResidentMetadataStore() {
		this.descriptorMap = new HashMap<Key, Entity>();
		this.externalIdentifierToValueMap = new HashMap<String, Map<String, List<Entity>>>();
	}

	@Override
	public Entity getEntity(Key key, ContentRetrieverFactory contentRetrieverFactory, MetadataModel model) {
		return descriptorMap.get(key);
	}

	@Override
	public List<Entity> getEntity(ExternalIdentifierInfo externalIdentifierInfo, String value, ContentRetrieverFactory contentRetrieverFactory, MetadataModel model) {
		Map<String, List<Entity>> externalIdentifierValueToEntityMap = externalIdentifierToValueMap.get(externalIdentifierInfo.getId());
		List<Entity> result;
		if (externalIdentifierValueToEntityMap.containsKey(value)) {
			result = Collections.unmodifiableList(externalIdentifierValueToEntityMap.get(value));
		} else {
			result = Collections.emptyList();
		}
		return result;
	}

	@Override
	public Set<Key> getKeysForIndirectIds(String indirectType, Collection<String> indirectIds, Set<String> entityType) {
		Set<Key> result = Collections.emptySet();
		Map<String, List<Entity>> indirectValueToDescriptorsMap = externalIdentifierToValueMap.get(indirectType);
		if (indirectValueToDescriptorsMap != null) {
			result = new HashSet<Key>();
			for (String indirectId : indirectIds) {
				List<Entity> entities = indirectValueToDescriptorsMap.get(indirectId);
				if (entities != null) {
					for (Entity entity : entities) {
						if (entityType.contains(entity.getEntityInfo().getId())) {
							result.add(entity.getKey());
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public void persist(Map<String, Entity> contentIdToEntityMap, MetadataModel model) {
		for (Map.Entry<String, Entity> entry : contentIdToEntityMap.entrySet()) {
			persist(entry.getValue(), entry.getKey());
		}
	}

	public void persist(Entity entity, String contentId) {
		descriptorMap.put(entity.getKey(), entity);
		
		for (IndirectRelationship relationship : entity.getIndirectRelationships()) {
			ExternalIdentifier externalIdentifier = relationship.getExternalIdentifier();
			Map<String, List<Entity>> externalIdentifierValueToEntityMap = externalIdentifierToValueMap.get(externalIdentifier.getId());
			if (externalIdentifierValueToEntityMap == null) {
				externalIdentifierValueToEntityMap = new HashMap<String, List<Entity>>();
				externalIdentifierToValueMap.put(externalIdentifier.getId(), externalIdentifierValueToEntityMap);
			}
			List<Entity> descriptorList = externalIdentifierValueToEntityMap.get(externalIdentifier.getValue());
			if (descriptorList == null) {
				descriptorList = new LinkedList<Entity>();
				externalIdentifierValueToEntityMap.put(externalIdentifier.getValue(), descriptorList);
			}
			descriptorList.add(entity);
//			if (descriptorList.size() >= 2) {
//				log.trace("Found '"+descriptorList.size()+"' instances of : "+externalIdentifier.getId()+" "+externalIdentifier.getValue());
//			}
		}
	}
}
