/**
 * 
 */
package org.scapdev.content.model.processor.jaxb;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBElement;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.Relationship;
import org.scapdev.content.model.processor.ImportException;

public class ImportData {
	private static final Logger log = Logger.getLogger(ImportData.class);

	private final List<Future<Entity>> futures;
	private final List<Entity> entities;

	ImportData() {
		this.entities = new LinkedList<Entity>();
		this.futures = new LinkedList<Future<Entity>>();
	}

	void registerRelationshipData(Future<Entity> future) {
		futures.add(future);
	}

	void newEntity(EntityInfo entityInfo, JAXBElement<Object> obj, JAXBEntityProcessor processor) {
		log.log(Level.TRACE,"Creating entity type: "+entityInfo.getId());
		EntityImpl entity = new EntityImpl(entityInfo, obj);
		Future<Entity> future = processor.processEntity(entity, obj);
		registerRelationshipData(future);
		entities.add(entity);
	}

	void validateRelationships() {
		for (Future<Entity> future : futures) {
			try {
				Entity entity = future.get();
				for (Relationship relationship : entity.getRelationships()) {
					validateRelationship(relationship);
				}
			} catch (InterruptedException e) {
				throw new ImportException(e);
			} catch (ExecutionException e) {
				throw new ImportException(e);
			}
		}
	}

	private void validateRelationship(Relationship relationship) {
		// TODO: implement
	}

	public List<Entity> getEntities() {
		return entities;
	}

}