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
package org.scapdev.content.core.writer;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.scapdev.content.model.DocumentInfo;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.Key;

class DocumentData {
	private final DocumentInfo documentInfo;
	private final Map<String, Map<Key, Entity>> entityTypeToEntityMap;

	public DocumentData(DocumentInfo info) {
		this.documentInfo = info;
		this.entityTypeToEntityMap = new HashMap<String, Map<Key, Entity>>();
	}

	public void addEntity(Entity entity) {
		String entityTypeId = entity.getEntityInfo().getId();
		Map<Key, Entity> entityMap = entityTypeToEntityMap.get(entityTypeId);
		if (entityMap == null) {
			entityMap = new LinkedHashMap<Key, Entity>();
			entityTypeToEntityMap.put(entityTypeId, entityMap);
		}
		entityMap.put(entity.getKey(), entity);
	}

	/**
	 * @return the info
	 */
	public DocumentInfo getDocumentInfo() {
		return documentInfo;
	}

	public Collection<Entity> getEntities(String documentContainerId, List<String> entityTypeIds) {
		Collection<Entity> result = new LinkedList<Entity>();
		for (String entityTypeId : entityTypeIds) {
			Map<Key, Entity> entityMap = entityTypeToEntityMap.get(entityTypeId);
			if (entityMap != null) {
				result.addAll(entityMap.values());
			}
		}
		return result;
	}
}
