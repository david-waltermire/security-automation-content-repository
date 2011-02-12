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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scapdev.content.model.Entity;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.Relationship;

public class MemoryResidentMetadataStore implements MetadataStore<Object> {
	private final Map<Key, EntityDescriptor<Object>> descriptorMap;

	public MemoryResidentMetadataStore() {
		this.descriptorMap = new HashMap<Key, EntityDescriptor<Object>>();
	}

	@Override
	public EntityDescriptor<Object> getEntityDescriptor(Key key) {
		return descriptorMap.get(key);
	}

	@Override
	public void persist(Entity<Object> entity, String contentId) {
		EntityDescriptor<Object> desc = new InternalEntityDescriptor(entity, contentId);
		descriptorMap.put(entity.getKey(), desc);
	}

	private static class InternalEntityDescriptor implements EntityDescriptor<Object> {
		private final Entity<Object> entity;
		private final String contentId;
		
		InternalEntityDescriptor(Entity<Object> entity, String contentId) {
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
		public List<Relationship<Object, ?>> getRelationships() {
			return entity.getRelationships();
		}
		
	}
}
