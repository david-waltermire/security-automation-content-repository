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
package org.scapdev.content.model.processor.jaxb;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBElement;

import org.scapdev.content.core.persistence.ContentPersistenceManager;
import org.scapdev.content.model.JAXBMetadataModel;
import org.scapdev.content.model.JAXBRelationshipIdentifyingImportVisitor;
import org.scapdev.content.model.Relationship;
import org.scapdev.content.model.processor.EntityProcessor;
import org.scapdev.content.model.processor.Importer;

public class JAXBEntityProcessor implements EntityProcessor {
	private final JAXBMetadataModel model;
	private final ExecutorService service;
	private final ContentPersistenceManager persistenceManager;

	public JAXBEntityProcessor(JAXBMetadataModel model, ContentPersistenceManager persistenceManager) {
		this.model = model;
		this.persistenceManager = persistenceManager;
		this.service = Executors.newFixedThreadPool(2);
	}

	public void shutdown() {
		service.shutdown();
	}

	public Importer newImporter() {
		return new JAXBImporter(this);
	}

	/**
	 * @return the persistenceManager
	 */
	public ContentPersistenceManager getPersistenceManager() {
		return persistenceManager;
	}

	/**
	 * @return the model
	 */
	public JAXBMetadataModel getMetadataModel() {
		return model;
	}


	private static class RelationshipExtractingTask implements Callable<List<Relationship>> {
		private final EntityImpl entity;
		private final JAXBRelationshipIdentifyingImportVisitor visitor;

		RelationshipExtractingTask(EntityImpl entity, JAXBElement<Object> node, JAXBMetadataModel model) {
			this.entity = entity;
			this.visitor = new JAXBRelationshipIdentifyingImportVisitor(entity, node, model);
		}

		@Override
		public List<Relationship> call() throws Exception {
			visitor.visit();
			List<Relationship> relationships = visitor.getRelationships();
			entity.setRelationships(relationships);
			return relationships;
		}
		
	}


	public Future<List<Relationship>> processEntity(EntityImpl entity, JAXBElement<Object> obj) {
		RelationshipExtractingTask task = new RelationshipExtractingTask(entity, obj, model);
		Future<List<Relationship>> future = service.submit(task);
		return future;
	}
}
