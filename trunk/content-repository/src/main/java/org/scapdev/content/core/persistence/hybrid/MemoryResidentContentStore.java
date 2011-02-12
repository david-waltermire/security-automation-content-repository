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
import java.util.Map;

public class MemoryResidentContentStore implements ContentStore {
	private int index = 0;
	private Map<Integer, Object> contentMap;

	public MemoryResidentContentStore() {
		contentMap = new HashMap<Integer, Object>();
	}

	@Override
	public Object getContent(String contentId) {
		return contentMap.get(Integer.valueOf(contentId));
	}

	@Override
	public String persist(Object content) {
		Integer key = Integer.valueOf(++index);
		contentMap.put(key, content);
		return key.toString();
	}

	@Override
	public ContentRetriever<Object> getContentRetriever(String contentId) {
		return new InternalContentRetriever(contentId);
	}

	private class InternalContentRetriever extends AbstractContentRetriever<Object> {

		public InternalContentRetriever(String contentId) {
			super(contentId);
		}

		@Override
		protected Object getContentInternal(String contentId) {
			return MemoryResidentContentStore.this.getContent(contentId);
		}
	}
}
