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

import gov.nist.scap.content.model.IEntity;

import java.util.LinkedHashMap;
import java.util.List;

import org.apache.xmlbeans.XmlObject;
import org.scapdev.content.core.ContentException;

public interface ContentStore extends ContentRetrieverFactory {

	XmlObject getContent(String contentId);
	/**
	 * Persist a list of entities. 
	 * @param entities The list MUST be ordered such that each entity either contains no other entities, or only contains entities that appear before it in the list
	 * @return A map of content IDs to entities. The insertion of the entities MUST be in the same order as the passed in list
	 * @throws ContentException error with the repo
	 */
	LinkedHashMap<String, IEntity<?>> persist(List<? extends IEntity<?>> entities) throws ContentException;
    /**
     * Persist a list of entities. 
     * @param entities The list MUST be ordered such that each entity either contains no other entities, or only contains entities that appear before it in the list
     * @session A session for tracking purposes
     * @return A map of content IDs to entities. The insertion of the entities MUST be in the same order as the passed in list
     * @throws ContentException error with the repo
     */
	LinkedHashMap<String, IEntity<?>> persist(List<? extends IEntity<?>> entities, Object session) throws ContentException;
    boolean commit(Object session);
    boolean rollback(Object session);
	ContentRetriever getContentRetriever(String contentId);
	void shutdown();

}