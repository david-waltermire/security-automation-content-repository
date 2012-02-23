/**
 * 
 */
package org.scapdev.content.model.processor;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.bind.JAXBElement;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.KeyedRelationship;

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
		Map<Key, Entity> importedEntities = new HashMap<Key, Entity>();
		Collection<KeyedRelationship> relationships = new LinkedList<KeyedRelationship>();
		for (Future<Entity> future : futures) {
			try {
				Entity entity = future.get();
				importedEntities.put(entity.getKey(), entity);
				relationships.addAll(entity.getKeyedRelationships());
			} catch (InterruptedException e) {
				throw new ImportException(e);
			} catch (ExecutionException e) {
				throw new ImportException(e);
			}
		}

		for (KeyedRelationship relationship : relationships) {
			Key keyRef = relationship.getKey();
			// TODO: implement resolver to check for keys that reference entities that are already imported
			Entity entity = importedEntities.get(keyRef);
			if (entity == null) {
				throw new ImportException("Invalid key reference '"+keyRef+"' on relationship: "+relationship);
			}
		}
	}

	public List<? extends Entity> getEntities() {
		return entities;
	}

}