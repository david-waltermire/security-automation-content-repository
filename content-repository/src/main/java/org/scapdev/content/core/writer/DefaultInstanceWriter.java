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

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.scapdev.content.core.query.QueryResult;
import org.scapdev.content.model.DocumentInfo;
import org.scapdev.content.model.Entity;

public class DefaultInstanceWriter implements InstanceWriter {
	private static final Logger log = Logger.getLogger(DefaultInstanceWriter.class);

	@Override
	public void write(QueryResult queryResult) throws IOException {
		Map<DocumentInfo, List<Entity>> documentToEntityMap = new HashMap<DocumentInfo, List<Entity>>();

		for (Entity entity : queryResult.getEntities().values()) {
			Collection<DocumentInfo> documentInfos = entity.getEntityInfo().getSchemaInfo().getDocumentInfos();
			if (documentInfos.size() == 1) {
				DocumentInfo documentInfo = documentInfos.iterator().next();
				List<Entity> entities;
				if (documentToEntityMap.containsKey(documentInfo)) {
					entities = documentToEntityMap.get(documentInfo);
				} else {
					entities = new LinkedList<Entity>();
					documentToEntityMap.put(documentInfo, entities);
				}
				entities.add(entity);
			} else {
				throw new UnsupportedOperationException();
			}
		}

		for (Map.Entry<DocumentInfo, List<Entity>> entry : documentToEntityMap.entrySet()) {
			log.info("writing document: "+entry.getKey().getId());
			for (Entity entity : entry.getValue()) {
				log.info("writing entity: "+entity.getKey());
			}
		}
	}

}
