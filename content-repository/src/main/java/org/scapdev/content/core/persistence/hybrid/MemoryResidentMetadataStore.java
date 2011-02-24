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

import org.apache.log4j.Logger;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.ExternalIdentifier;
import org.scapdev.content.model.ExternalIdentifierInfo;
import org.scapdev.content.model.IndirectRelationship;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.KeyedRelationship;
import org.scapdev.content.model.Relationship;

public class MemoryResidentMetadataStore implements MetadataStore {
	private static final Logger log = Logger.getLogger(MemoryResidentMetadataStore.class);
	private final Map<Key, EntityDescriptor> descriptorMap;
	private final Map<String, Map<String, List<EntityDescriptor>>> externalIdentifierToValueMap;

	public MemoryResidentMetadataStore() {
		this.descriptorMap = new HashMap<Key, EntityDescriptor>();
		this.externalIdentifierToValueMap = new HashMap<String, Map<String, List<EntityDescriptor>>>();
	}

	@Override
	public EntityDescriptor getEntityDescriptor(Key key) {
		return descriptorMap.get(key);
	}

	@Override
	public List<EntityDescriptor> getEntityDescriptor(ExternalIdentifierInfo externalIdentifierInfo, String value) {
		Map<String, List<EntityDescriptor>> externalIdentifierValueToEntityDescriptorMap = externalIdentifierToValueMap.get(externalIdentifierInfo.getId());
		List<EntityDescriptor> result;
		if (externalIdentifierValueToEntityDescriptorMap.containsKey(value)) {
			result = Collections.unmodifiableList(externalIdentifierValueToEntityDescriptorMap.get(value));
		} else {
			result = Collections.emptyList();
		}
		return result;
	}

	@Override
	public Set<Key> getKeysForIndirectIds(String indirectType, Collection<String> indirectIds, Set<String> entityType) {
		Set<Key> result = Collections.emptySet();
		Map<String, List<EntityDescriptor>> indirectValueToDescriptorsMap = externalIdentifierToValueMap.get(indirectType);
		if (indirectValueToDescriptorsMap != null) {
			result = new HashSet<Key>();
			for (String indirectId : indirectIds) {
				List<EntityDescriptor> descs = indirectValueToDescriptorsMap.get(indirectId);
				if (descs != null) {
					for (EntityDescriptor desc : descs) {
						if (entityType.contains(desc.getEntityInfo().getId())) {
							result.add(desc.getKey());
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public void persist(Entity entity, String contentId) {
		EntityDescriptor desc = new InternalEntityDescriptor(entity, contentId);
		descriptorMap.put(entity.getKey(), desc);

		for (IndirectRelationship relationship : entity.getIndirectRelationships()) {
			ExternalIdentifier externalIdentifier = relationship.getExternalIdentifier();
			Map<String, List<EntityDescriptor>> externalIdentifierValueToEntityDescriptorMap = externalIdentifierToValueMap.get(externalIdentifier.getId());
			if (externalIdentifierValueToEntityDescriptorMap == null) {
				externalIdentifierValueToEntityDescriptorMap = new HashMap<String, List<EntityDescriptor>>();
				externalIdentifierToValueMap.put(externalIdentifier.getId(), externalIdentifierValueToEntityDescriptorMap);
			}
			List<EntityDescriptor> descriptorList = externalIdentifierValueToEntityDescriptorMap.get(externalIdentifier.getValue());
			if (descriptorList == null) {
				descriptorList = new LinkedList<EntityDescriptor>();
				externalIdentifierValueToEntityDescriptorMap.put(externalIdentifier.getValue(), descriptorList);
			}
			descriptorList.add(desc);
			if (descriptorList.size() >= 2) {
				log.info("Found '"+descriptorList.size()+"' instances of : "+externalIdentifier.getId()+" "+externalIdentifier.getValue());
			}
		}
	}

	private static class InternalEntityDescriptor implements EntityDescriptor {
		private final Entity entity;
		private final String contentId;
		
		InternalEntityDescriptor(Entity entity, String contentId) {
			this.entity = entity;
			this.contentId = contentId;
		}

		@Override
		public String getContentId() {
			return contentId;
		}

		@Override
		public EntityInfo getEntityInfo() {
			return entity.getEntityInfo();
		}

		@Override
		public Key getKey() {
			return entity.getKey();
		}

		@Override
		public Collection<Relationship> getRelationships() {
			return entity.getRelationships();
		}

		@Override
		public Collection<IndirectRelationship> getIndirectRelationships() {
			return entity.getIndirectRelationships();
		}

		@Override
		public Collection<KeyedRelationship> getKeyedRelationships() {
			return entity.getKeyedRelationships();
		}
	}
}
