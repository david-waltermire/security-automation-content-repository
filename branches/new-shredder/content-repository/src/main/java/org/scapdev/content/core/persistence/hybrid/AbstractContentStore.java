/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 'David Waltermire'
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

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;

import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlObject;

public abstract class AbstractContentStore implements ContentStore {

	public AbstractContentStore() {
	}

	public XmlObject getContent(String contentId, IMetadataModel model) {
		return getContentInternal(contentId, model);
	}
	
	protected abstract XmlObject getContentInternal(String contentId, IMetadataModel model);
	
	public ContentRetriever getContentRetriever(String contentId, IMetadataModel model) {
		return new DefaultContentRetriever(contentId, model, this);
	}

	public Map<String, IEntity<?>> persist(List<? extends IEntity<?>> entities,
			IMetadataModel model) {
		return persistInternal(entities, model);
	}
	
	protected abstract Map<String, IEntity<?>> persistInternal(List<? extends IEntity<?>> entities, IMetadataModel model);

	public void shutdown() {
	}
}
