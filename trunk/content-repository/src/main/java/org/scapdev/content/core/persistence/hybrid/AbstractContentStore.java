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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.scapdev.content.core.persistence.ContentPersistenceException;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.MetadataModel;

public abstract class AbstractContentStore implements ContentStore {
//	private final JAXBMarshallerPool marshallerPool;
//	private final JAXBUnmarshallerPool unmarshallerPool;
	private Queue<Marshaller> marshallerPool;
	private Queue<Unmarshaller> unmarshallerPool;

	public AbstractContentStore() {
		marshallerPool = new LinkedList<Marshaller>();
		unmarshallerPool = new LinkedList<Unmarshaller>();
	}

	private Unmarshaller retrieveUnmarshaller(JAXBContext context) throws JAXBException {
		Unmarshaller unmarshaller;
		synchronized (this) {
			unmarshaller = unmarshallerPool.poll();
		}
		if (unmarshaller == null) {
			unmarshaller = context.createUnmarshaller();
		}
		return unmarshaller;
	}

	private void storeUnmarshaller(Unmarshaller unmarshaller) {
		synchronized (this) {
			unmarshallerPool.offer(unmarshaller); // always returns true
		}
	}

	@Override
	public JAXBElement<Object> getContent(String contentId, MetadataModel model) {
		try {
			Unmarshaller unmarshaller = retrieveUnmarshaller(model.getJAXBContext());
			JAXBElement<Object> result = getContentInternal(contentId, model, unmarshaller);
			storeUnmarshaller(unmarshaller);
			return result;
		} catch (JAXBException e) {
			throw new ContentPersistenceException(e);
		}
	}
	
	protected abstract JAXBElement<Object> getContentInternal(String contentId, MetadataModel model, Unmarshaller unmarshaller);
	
	@Override
	public ContentRetriever getContentRetriever(String contentId, MetadataModel model) {
		return new DefaultContentRetriever(contentId, model, this);
	}

	private Marshaller retrieveMarshaller(JAXBContext context) throws JAXBException {
		Marshaller marshaller;
		synchronized (this) {
			marshaller = marshallerPool.poll();
		}
		if (marshaller == null) {
			marshaller = context.createMarshaller();
		}
		return marshaller;
	}

	private void storeMarshaller(Marshaller marshaller) {
		synchronized (this) {
			marshallerPool.offer(marshaller); // always returns true
		}
	}

	@Override
	public Map<String, Entity> persist(List<? extends Entity> entities,
			MetadataModel model) {
		try {
			Marshaller marshaller = retrieveMarshaller(model.getJAXBContext());
			Map<String, Entity> result = persistInternal(entities, model, marshaller);
			storeMarshaller(marshaller);
			return result;
		} catch (JAXBException e) {
			throw new ContentPersistenceException(e);
		}
	}
	
	protected abstract Map<String, Entity> persistInternal(List<? extends Entity> entities, MetadataModel model, Marshaller marshaller);

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}
}
