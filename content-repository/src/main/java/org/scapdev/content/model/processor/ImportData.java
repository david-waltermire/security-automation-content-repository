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
import org.scapdev.content.model.MutableKeyedRelationship;

public class ImportData {
	private static final Logger log = Logger.getLogger(ImportData.class);

	private final List<Future<MutableEntity>> futures;
	private final List<MutableEntity> entities;

	ImportData() {
		this.entities = new LinkedList<MutableEntity>();
		this.futures = new LinkedList<Future<MutableEntity>>();
	}

	void registerRelationshipData(Future<MutableEntity> future) {
		futures.add(future);
	}

	void newEntity(EntityInfo entityInfo, JAXBElement<Object> obj, JAXBEntityProcessor processor) {
		log.log(Level.TRACE,"Creating entity type: "+entityInfo.getId());
		EntityImpl entity = new EntityImpl(entityInfo, obj);
		Future<MutableEntity> future = processor.processEntity(entity, obj);
		registerRelationshipData(future);
		entities.add(entity);
	}

	void validateRelationships() {
		Map<Key, MutableEntity> importedEntities = new HashMap<Key, MutableEntity>();
		Collection<MutableKeyedRelationship> relationships = new LinkedList<MutableKeyedRelationship>();
		for (Future<MutableEntity> future : futures) {
			try {
				MutableEntity entity = future.get();
				importedEntities.put(entity.getKey(), entity);
				relationships.addAll(entity.getKeyedRelationships());
			} catch (InterruptedException e) {
				throw new ImportException(e);
			} catch (ExecutionException e) {
				throw new ImportException(e);
			}
		}

		for (MutableKeyedRelationship relationship : relationships) {
			Key keyRef = relationship.getKey();
			// TODO: implement resolver to check for keys that reference entities that are already imported
			Entity entity = importedEntities.get(keyRef);
			if (entity == null) {
				throw new ImportException("Invalid key reference '"+keyRef+"' on relationship: "+relationship);
			}
			relationship.setRelatedEntity(entity);
		}
	}

	public List<? extends Entity> getEntities() {
		return entities;
	}

}