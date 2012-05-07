package gov.nist.scap.content.shredder.parser;

import gov.nist.scap.content.model.AbstractEntity;
import gov.nist.scap.content.model.DefaultBoundaryIdentifierRelationship;
import gov.nist.scap.content.model.DefaultCompositeRelationship;
import gov.nist.scap.content.model.DefaultContentNode;
import gov.nist.scap.content.model.DefaultGeneratedDocument;
import gov.nist.scap.content.model.DefaultKeyedDocument;
import gov.nist.scap.content.model.DefaultVersion;
import gov.nist.scap.content.model.ICompositeRelationship;
import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.IContentHandle;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IVersion;
import gov.nist.scap.content.model.KeyBuilder;
import gov.nist.scap.content.model.KeyException;
import gov.nist.scap.content.model.definitions.ContentMapping;
import gov.nist.scap.content.model.definitions.IBoundaryIdentifierRelationshipDefinition;
import gov.nist.scap.content.model.definitions.ICompositeRelationshipDefinition;
import gov.nist.scap.content.model.definitions.IContentNodeDefinition;
import gov.nist.scap.content.model.definitions.IDocumentDefinition;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IEntityDefinitionVisitor;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.IExternalIdentifierMapping;
import gov.nist.scap.content.model.definitions.IGeneratedDocumentDefinition;
import gov.nist.scap.content.model.definitions.IKeyDefinition;
import gov.nist.scap.content.model.definitions.IKeyRef;
import gov.nist.scap.content.model.definitions.IKeyedDocumentDefinition;
import gov.nist.scap.content.model.definitions.IKeyedField;
import gov.nist.scap.content.model.definitions.IKeyedRelationshipDefinition;
import gov.nist.scap.content.model.definitions.IPropertyDefinition;
import gov.nist.scap.content.model.definitions.IPropertyRef;
import gov.nist.scap.content.model.definitions.IRelationshipDefinition;
import gov.nist.scap.content.model.definitions.IRelationshipDefinitionVisitor;
import gov.nist.scap.content.model.definitions.IVersionDefinition;
import gov.nist.scap.content.model.definitions.KeyedRelationshipInfo;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.model.definitions.XPathRetriever;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentProcessor {
	private static final Logger log = LoggerFactory.getLogger(ContentProcessor.class);

	private final ContentHandler contentHandler;

	public ContentProcessor(ContentHandler contentHandler) {
		this.contentHandler = contentHandler;
	}

	public void process(IDocumentDefinition definition, XmlCursor cursor) throws ProcessingException {
		definition.accept(new ContentProcessingEntityDefinitionVisitor(cursor, null));
	}

	private void processEntity(AbstractEntity<?> entity,
			XmlCursor cursor) throws ProcessingException {

		IVersionDefinition versionDef = entity.getDefinition().getVersionDefinition();
		if (versionDef != null) {
			IVersion version = processVersion(versionDef, cursor);
			if (version == null && versionDef.isUseParentVersionWhenUndefined()) {
				version = entity.getParent().getVersion();
			}
			if (version != null) {
				entity.setVersion(version);
			}
		}
		processProperties(entity, cursor);

		// Must processes everything before relationships, due to the recursion
		// caused by CompositeRelationship processing
		processRelationships(entity, cursor);

		contentHandler.handle(entity);
	}

	private static IContentHandle getContentHandle(XmlCursor cursor) {
		Bookmark bookmark = new Bookmark();
		cursor.setBookmark(bookmark);

		return new BookmarkContentHandle(bookmark);
	}

	private void processRelationships(AbstractEntity<?> entity,
			XmlCursor cursor) throws ProcessingException {
		Collection<? extends IRelationshipDefinition> relationshipDefinitions = entity.getDefinition().getRelationshipDefinitions();

		if (!relationshipDefinitions.isEmpty()) {
			ContentProcessingRelationshipDefinitionVisitor visitor = new ContentProcessingRelationshipDefinitionVisitor(entity);

			// Handle all relationships
			for (IRelationshipDefinition relationship : relationshipDefinitions) {
				String xpath = relationship.getXpath();
				if (xpath != null) {
					cursor.push();
					try {
						// select the children of this boundary
						cursor.selectPath(xpath);
						int count = cursor.getSelectionCount();
						// iterate over the selected nodes
						for (int i = 0; i < count; i++) {
							if (!cursor.toSelection(i)) {
								throw new RuntimeException("unable to navigate to next cursor position");
							}
							XmlCursor newCursor = cursor.newCursor();
							visitor.setXmlCursor(newCursor);
							relationship.accept(visitor);
						}
					} catch (XmlRuntimeException e) {
						throw new ProcessingException("Unable to select xpath for boundary: "+relationship.getId(), e);
					} finally {
						cursor.pop();
					}
				} else {
					visitor.setXmlCursor(cursor);
					relationship.accept(visitor);
				}
			}
		}
	}

	private static IKey getKey(IKeyDefinition keyDefinition, IContainer<?> parentContext, XmlCursor cursor) throws KeyException {
		List<? extends IKeyedField> fields = keyDefinition.getFields();

		Map<String, String> fieldIdToValueMap = getFieldValues(fields, parentContext, cursor);
		KeyBuilder builder = new KeyBuilder(fields);
		builder.setId(keyDefinition.getId());
		builder.addFields(fieldIdToValueMap);
		return builder.toKey();
	}

	public static IKey getKey(IKeyRef keyRefDefinition, IContainer<?> parentContext, XmlCursor cursor) throws KeyException {
		List<? extends IKeyedField> fields = keyRefDefinition.getFields();
		Map<String, String> referenceFieldIdToValueMap = getFieldValues(fields, parentContext, cursor);

		IKeyDefinition keyDefinition = keyRefDefinition.getKeyDefinition();
		KeyBuilder builder = new KeyBuilder(keyDefinition.getFields());
		builder.setId(keyDefinition.getId());
		builder.addFields(referenceFieldIdToValueMap);
		return builder.toKey();
	}

	private static LinkedHashMap<String, String> getFieldValues(List<? extends IKeyedField> fields, IContainer<?> parentContext, XmlCursor cursor) throws KeyException {
		LinkedHashMap<String, String> fieldIdToValueMap = new LinkedHashMap<String, String>();

		for (IKeyedField field : fields) {
			String id = field.getName();
			String value;
			try {
				value = field.getValue(parentContext, cursor);
			} catch (Exception e) {
				throw new KeyException("unable to retrieve field: "+id, e);
			}

			if (value == null) {
				throw new KeyException("null field '" + id + "'");
			}
			fieldIdToValueMap.put(id, value);
		}
		return fieldIdToValueMap;
	}

	private static IVersion processVersion(IVersionDefinition version, XmlCursor cursor) {
		XPathRetriever valueRetriever = version.getXpath();
		String value = valueRetriever.getValue(cursor);
		DefaultVersion retval = null;
		if (value != null) {
			retval = new  DefaultVersion(version, value);
			retval.addVersioningMethodDefinition(version.getMethod());
		}
		return retval;
	}

	private static void processProperties(AbstractEntity<?> entity, XmlCursor cursor) throws ProcessingException {
		for (IPropertyRef propertyRef : entity.getDefinition().getPropertyRefs()) {
			IPropertyDefinition definition = propertyRef.getPropertyDefinition();
			List<String> values = propertyRef.getValues(entity.getParent(), cursor);
			if (values != null) {
				entity.addProperty(definition.getId(), values);
			}
		}
	}

	private class ContentProcessingRelationshipDefinitionVisitor implements IRelationshipDefinitionVisitor {
		private final AbstractEntity<?> entity;
		private XmlCursor cursor;

		public ContentProcessingRelationshipDefinitionVisitor(AbstractEntity<?> entity) {
			this.entity = entity;
		}

		public void setXmlCursor(XmlCursor newCursor) {
			this.cursor = newCursor;
		}

		@Override
		public void visit(ICompositeRelationshipDefinition definition) throws ProcessingException {
			ContentMapping contentMapping = definition.getContentMapping();
			QName qname = cursor.getName();
			// retrieve the content definition to use to process the child node
			IEntityDefinition contentDefinition = contentMapping.getContentDefinitionForQName(qname);
			if (contentDefinition == null) {
				log.warn("Unrecognized QName '"+qname.toString()+"' at composite boundary: "+definition.getId());
			} else {
				// process the child node
				AbstractEntity<?> childEntity = contentDefinition.accept(new ContentProcessingEntityDefinitionVisitor(cursor, entity));

				ICompositeRelationship relationship = new DefaultCompositeRelationship(definition, childEntity);
				// Add to the parent entity.  The child has a pointer back to
				// the parent that can be used as well
				entity.addRelationship(relationship);
			}
		}

		public void visit(IBoundaryIdentifierRelationshipDefinition definition) {
			XPathRetriever valueRetriever = definition.getValueRetriever();
			IExternalIdentifierMapping qualifierMapping = definition.getQualifierMapping();

			String value = valueRetriever.getValue(cursor);
			IExternalIdentifier externalIdentifier = qualifierMapping.resolveExternalIdentifier(cursor);
			if (externalIdentifier != null) {
				entity.addRelationship(new DefaultBoundaryIdentifierRelationship(definition, externalIdentifier, value));
			} else {
				log.warn("Unable to extract boundary identifier relationship for value: "+value);
			}
		}

		public void visit(IKeyedRelationshipDefinition definition) throws ProcessingException {
			IKey key;
			try {
				key = getKey(definition.getKeyRefDefinition(), entity, cursor);
			} catch (KeyException e) {
				throw new ProcessingException("Unable to extract key for keyed relationship '"+definition.getId()+"' on entity: "+entity.getDefinition().getId(), e);
			}
			contentHandler.handle(new KeyedRelationshipInfo(definition, entity, key));
		}
		
	}

	private class ContentProcessingEntityDefinitionVisitor implements IEntityDefinitionVisitor<AbstractEntity<?>> {
		private final XmlCursor cursor;
		private final AbstractEntity<?> parent;

		public ContentProcessingEntityDefinitionVisitor(XmlCursor cursor,
				AbstractEntity<?> parent) {
			this.cursor = cursor;
			this.parent = parent;
		}

		public AbstractEntity<?> visit(IGeneratedDocumentDefinition definition) throws ProcessingException {
			DefaultGeneratedDocument document = new DefaultGeneratedDocument(definition, getContentHandle(cursor), parent);
			processEntity(document, cursor);
			return document;
		}

		public AbstractEntity<?> visit(IKeyedDocumentDefinition definition) throws ProcessingException {
			IKeyDefinition keyDefinition = definition.getKeyDefinition();
			IKey key;
			try {
				key = getKey(keyDefinition, parent, cursor);
			} catch (KeyException e) {
				throw new ProcessingException("Unable to extract key for entity: "+cursor.getName().toString(), e);
			}

			DefaultKeyedDocument document = new DefaultKeyedDocument(definition, key, getContentHandle(cursor), parent);

			IVersionDefinition versionDef = definition.getVersionDefinition();
			if (versionDef != null) {
				IVersion version = processVersion(versionDef, cursor);
				if (version != null) {
					document.setVersion(version);
				}
			}
			processEntity(document, cursor);
			return document;
		}

		public AbstractEntity<?> visit(IContentNodeDefinition definition) throws ProcessingException {
			IKeyDefinition keyDefinition = definition.getKeyDefinition();
			IKey key;
			try {
				key = getKey(keyDefinition, parent, cursor);
			} catch (KeyException e) {
				throw new ProcessingException("Unable to extract key for entity: "+cursor.getName().toString(), e);
			}

			DefaultContentNode node = new DefaultContentNode(definition, key, getContentHandle(cursor), parent);
			processEntity(node, cursor);
			return node;
		}
		
	}
}
