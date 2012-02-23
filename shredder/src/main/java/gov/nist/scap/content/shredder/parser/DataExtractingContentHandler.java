package gov.nist.scap.content.shredder.parser;

import gov.nist.scap.content.shredder.model.IContainerVisitor;
import gov.nist.scap.content.shredder.model.IContentNode;
import gov.nist.scap.content.shredder.model.IEntity;
import gov.nist.scap.content.shredder.model.IGeneratedDocument;
import gov.nist.scap.content.shredder.model.IIndexedDocument;
import gov.nist.scap.content.shredder.model.IKey;
import gov.nist.scap.content.shredder.model.IMutableEntity;
import gov.nist.scap.content.shredder.rules.KeyedRelationshipInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;


// TODO: deal with synchronization issues 'synchronized'
public class DataExtractingContentHandler implements ContentHandler, IContainerVisitor {
	private static final Logger log = Logger.getLogger(DataExtractingContentHandler.class);

	private final Collection<KeyedRelationshipInfo> keyedRelationships = new LinkedList<KeyedRelationshipInfo>();
	private final Map<IKey, IMutableEntity<?>> keyedEntities = new HashMap<IKey, IMutableEntity<?>>();
	private final LinkedList<IMutableEntity<?>> entities = new LinkedList<IMutableEntity<?>>();

	public DataExtractingContentHandler() {
	}

	public Queue<? extends IEntity<?>> getEntities() {
		process();
		// TODO: make the queue unmodifiable
		return entities;
	}

	public void process() {
		Iterator<KeyedRelationshipInfo> i = keyedRelationships.iterator();
		while (i.hasNext()) {
			KeyedRelationshipInfo info = i.next();
			IKey key = info.getKey();
			IMutableEntity<?> referencedEntity = keyedEntities.get(key);
			if (referencedEntity != null) {
				info.applyRelationship(referencedEntity);
				i.remove();
			} else {
				log.warn("The key '"+key.toString()+"' does not match an entity");
			}
		}
	}

	public void handle(IMutableEntity<?> entity) {
		entity.accept((IContainerVisitor)this);
		IKey key = entity.getKey();
		if (key != null) {
			keyedEntities.put(key, entity);
		}
		entities.add(entity);
	}

	public void handle(KeyedRelationshipInfo info) {
		keyedRelationships.add(info);
	}

	public void visit(IContentNode entity) {
		XmlCursor cursor = entity.getBookmark().createCursor();
		StringBuilder builder = new StringBuilder();
		builder.append("handling content node: ");
		builder.append(entity.getDefinition().getId());
		builder.append(": qname=");
		builder.append(cursor.getName());
		builder.append(", key=");
		builder.append(entity.getKey());

		log.debug(builder.toString());
		cursor.dispose();
	}

	public void visit(IIndexedDocument document) {
		XmlCursor cursor = document.getBookmark().createCursor();
		StringBuilder builder = new StringBuilder();
		builder.append("Handling indexed document: ");
		builder.append(document.getDefinition().getId());
		builder.append(": qname=");
		builder.append(cursor.getName());

		builder.append(", key=");
		builder.append(document.getKey());

		log.debug(builder.toString());
		cursor.dispose();
	}

	public void visit(IGeneratedDocument document) {
		XmlCursor cursor = document.getBookmark().createCursor();
		StringBuilder builder = new StringBuilder();
		builder.append("Handling generated document: ");
		builder.append(document.getDefinition().getId());
		builder.append(": qname=");
		builder.append(cursor.getName());

		log.debug(builder.toString());
		cursor.dispose();
	}
}
