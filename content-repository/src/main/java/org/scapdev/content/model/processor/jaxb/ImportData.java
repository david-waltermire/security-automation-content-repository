/**
 * 
 */
package org.scapdev.content.model.processor.jaxb;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.Relationship;
import org.scapdev.content.model.processor.ImportException;

public class ImportData {
	private static final Logger log = Logger.getLogger(ImportData.class);

	private final List<Future<List<Relationship<Object, ?>>>> futures;
	private final List<Entity<Object>> entities;

	ImportData() {
		this.entities = new LinkedList<Entity<Object>>();
		this.futures = new LinkedList<Future<List<Relationship<Object, ?>>>>();
	}

	void registerRelationshipData(Future<List<Relationship<Object, ?>>> future) {
		futures.add(future);
	}

	void newEntity(EntityInfo entityInfo, Object obj, JAXBEntityProcessor processor) {
		log.log(Level.TRACE,"Creating entity type: "+entityInfo.getId());
		EntityImpl entity = new EntityImpl(entityInfo, obj);
		Future<List<Relationship<Object, ?>>> future = processor.processEntity(entity, obj);
		registerRelationshipData(future);
		entities.add(entity);
	}

	void validateRelationships() {
		for (Future<List<Relationship<Object, ?>>> future : futures) {
			try {
				for (Relationship<Object, ?> relationship : future.get()) {
					validateRelationship(relationship);
				}
			} catch (InterruptedException e) {
				throw new ImportException(e);
			} catch (ExecutionException e) {
				throw new ImportException(e);
			}
		}
	}

	private void validateRelationship(Relationship<Object, ?> relationship) {
		// TODO: implement
	}

	public List<Entity<Object>> getEntities() {
		return entities;
	}

}