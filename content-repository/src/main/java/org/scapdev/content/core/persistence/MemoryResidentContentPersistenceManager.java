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
package org.scapdev.content.core.persistence;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.scapdev.content.core.ContentException;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.MetadataModel;

public class MemoryResidentContentPersistenceManager extends AbstractContentPersistenceManager {
	private static Logger log = Logger.getLogger(MemoryResidentContentPersistenceManager.class);

	private Map<String, Map<Key, Entity>> fragmentIndex;

	public MemoryResidentContentPersistenceManager(MetadataModel model) {
		super(model);
		log.info("Initializing database");
		fragmentIndex = new HashMap<String, Map<Key, Entity>>();
		log.info("Database initialized");
	}

	@Override
	public Entity getEntityByKey(Key key) {
		Map<Key, Entity> idMap = fragmentIndex.get(key.getId());
		if (idMap != null) {
			return idMap.get(key);
		}
		return null;
	}

	@Override
	public void storeEntity(Entity entity) throws ContentException {
		Key key = entity.getKey();
		String keyTypeId = key.getId();
		log.log(Level.TRACE, "Storing entity: "+key);
		Map<Key, Entity> idMap = fragmentIndex.get(keyTypeId);
		if (idMap == null) {
			idMap = new HashMap<Key, Entity>();
			fragmentIndex.put(keyTypeId, idMap);
		} else if (idMap.containsKey(key)) {
			throw new ContentException("Record already exists in the database identified by key: "+key);
		}
		idMap.put(key, entity);
	}

	@Override
	public Set<Key> getKeysForIndirectIds(String indirectType,
			Collection<String> indirectIds, Set<String> entityType) {
		throw new UnsupportedOperationException();
	}
}
