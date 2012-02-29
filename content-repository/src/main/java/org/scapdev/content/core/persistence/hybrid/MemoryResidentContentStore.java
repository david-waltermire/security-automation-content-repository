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

import gov.nist.scap.content.shredder.metamodel.IMetadataModel;
import gov.nist.scap.content.shredder.model.IEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;

public class MemoryResidentContentStore implements ContentStore {
	private int index = 0;
	private Map<Integer, XmlObject> contentMap;

	public MemoryResidentContentStore() {
		contentMap = new HashMap<Integer, XmlObject>();
	}

	@Override
	public XmlObject getContent(String contentId, IMetadataModel model) {
		return contentMap.get(Integer.valueOf(contentId));
	}

	@Override
	public Map<String, IEntity<?>> persist(List<? extends IEntity<?>> entities, IMetadataModel model) {
		Map<String, IEntity<?>> result = new HashMap<String, IEntity<?>>();
		for (IEntity<?> entity : entities) {
			String contentId = persist(entity, model);
			result.put(contentId, entity);
		}
		return result;
	}

	// TODO: remove this method and merge into persist above
	public String persist(IEntity<?> entity, IMetadataModel model) {
		Integer key = Integer.valueOf(++index);
		contentMap.put(key, entity.getContentHandle().getCursor().getObject());
		return key.toString();
	}

	@Override
	public InternalContentRetriever getContentRetriever(String contentId, IMetadataModel model) {
		return new InternalContentRetriever(contentId, model);
	}

	private class InternalContentRetriever extends AbstractContentRetriever {

		public InternalContentRetriever(String contentId, IMetadataModel model) {
			super(contentId, model);
		}

		@Override
		protected XmlObject getContentInternal(String contentId, IMetadataModel model) {
			return MemoryResidentContentStore.this.getContent(contentId, model);
		}

		@Override
		public XmlCursor getCursor() {
			return MemoryResidentContentStore.this.getContent(contentId, model).newCursor();
		}
	}

	@Override
	public void shutdown() {
	}
}
