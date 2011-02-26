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

import javax.xml.bind.JAXBElement;

import org.scapdev.content.model.Entity;
import org.scapdev.content.model.MetadataModel;

public class MemoryResidentContentStore implements ContentStore {
	private int index = 0;
	private Map<Integer, JAXBElement<Object>> contentMap;

	public MemoryResidentContentStore() {
		contentMap = new HashMap<Integer, JAXBElement<Object>>();
	}

	@Override
	public JAXBElement<Object> getContent(String contentId, MetadataModel model) {
		return contentMap.get(Integer.valueOf(contentId));
	}

	@Override
	public Map<String, Entity> persist(List<? extends Entity> entities, MetadataModel model) {
		Map<String, Entity> result = new HashMap<String, Entity>();
		for (Entity entity : entities) {
			String contentId = persist(entity, model);
			result.put(contentId, entity);
		}
		return result;
	}

	// TODO: remove this method and merge into persist above
	public String persist(Entity entity, MetadataModel model) {
		Integer key = Integer.valueOf(++index);
		contentMap.put(key, entity.getObject());
		return key.toString();
	}

	@Override
	public InternalContentRetriever getContentRetriever(String contentId, MetadataModel model) {
		return new InternalContentRetriever(contentId, model);
	}

	private class InternalContentRetriever extends AbstractContentRetriever<Object> {

		public InternalContentRetriever(String contentId, MetadataModel model) {
			super(contentId, model);
		}

		@Override
		protected JAXBElement<Object> getContentInternal(String contentId, MetadataModel model) {
			return MemoryResidentContentStore.this.getContent(contentId, model);
		}
	}
}
