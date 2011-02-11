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
package org.scapdev.content.core.database.memory;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.scapdev.content.core.ContentException;
import org.scapdev.content.core.database.AbstractContentDatabase;
import org.scapdev.content.core.model.context.instance.EntityHandle;
import org.scapdev.content.model.Key;

public class MemoryResidentContentDatabase extends AbstractContentDatabase {
	private static Logger log = Logger.getLogger(MemoryResidentContentDatabase.class);

	private Map<String, Map<Key, EntityHandle>> fragmentIndex;

	public MemoryResidentContentDatabase() {
		log.info("Initializing database");
		fragmentIndex = new HashMap<String, Map<Key, EntityHandle>>();
		log.info("Database initialized");
	}

	@Override
	public EntityHandle getFragmentHandleByKey(Key key) {
		Map<Key, EntityHandle> idMap = fragmentIndex.get(key.getId());
		if (idMap != null) {
			return idMap.get(key);
		}
		return null;
	}

	@Override
	public void storeFragmentHandle(EntityHandle handle) throws ContentException {
		Key key = handle.getKey();
		String keyTypeId = key.getId();
		log.info("Storing fragment: "+key);
		Map<Key, EntityHandle> idMap = fragmentIndex.get(keyTypeId);
		if (idMap == null) {
			idMap = new HashMap<Key, EntityHandle>();
			fragmentIndex.put(keyTypeId, idMap);
		} else if (idMap.containsKey(key)) {
			throw new ContentException("Record already exists in the database identified by key: "+key);
		}
		idMap.put(key, handle);
	}
}
