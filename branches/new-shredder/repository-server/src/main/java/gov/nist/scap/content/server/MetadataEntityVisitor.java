package gov.nist.scap.content.server;

import gov.nist.scap.content.model.IBoundaryIdentifierRelationship;
import gov.nist.scap.content.model.ICompositeRelationship;
import gov.nist.scap.content.model.IContentNode;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IEntityVisitor;
import gov.nist.scap.content.model.IGeneratedDocument;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedDocument;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.IKeyedRelationship;
import gov.nist.scap.schema.content.entity.x01.EntityCollectionDocument;
import gov.nist.scap.schema.content.entity.x01.EntityCollectionType;
import gov.nist.scap.schema.content.entity.x01.EntityContentType;
import gov.nist.scap.schema.content.entity.x01.EntityKeyFieldType;
import gov.nist.scap.schema.content.entity.x01.EntityKeyType;
import gov.nist.scap.schema.content.entity.x01.EntityPropertyType;
import gov.nist.scap.schema.content.entity.x01.EntityRelationshipType;
import gov.nist.scap.schema.content.entity.x01.EntityType;
import gov.nist.scap.schema.content.entity.x01.EntityVersionType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlbeans.XmlOptions;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class MetadataEntityVisitor implements IEntityVisitor {

	private EntityCollectionDocument doc;
	private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	
	static {
		dbf.setNamespaceAware(true);
	}

	public EntityCollectionDocument getDoc() {
		return doc;
	}

	private void visit(IEntity<?> entity) {
		doc = EntityCollectionDocument.Factory
				.newInstance();
		EntityCollectionType collectionType = doc.addNewEntityCollection();

		EntityType et = collectionType.addNewEntity();

		EntityContentType contentType = et.addNewContent();
		contentType.set(entity.getContentHandle().getCursor().getObject());

		if (entity instanceof IKeyedEntity) {
			EntityKeyType ekt = et.addNewKey();
			IKeyedEntity<?> e = (IKeyedEntity<?>) entity;
			IKey key = e.getKey();
			ekt.setId(key.getId());
			for (Map.Entry<String, String> entry : key.getFieldNameToValueMap()
					.entrySet()) {
				EntityKeyFieldType ekft = ekt.addNewField();
				ekft.setId(entry.getKey());
				ekft.setValue(entry.getValue());
			}
		}

		if (entity.getProperties() != null) {
			for (Map.Entry<String, ? extends Set<String>> entry : entity
					.getProperties().entrySet()) {
				EntityPropertyType ept = et.addNewProperty();
				ept.setName(entry.getKey());
				for (String s : entry.getValue()) {
					ept.addValue(s);
				}
			}
		}

		if (entity.getBoundaryIdentifierRelationships() != null) {
			for (IBoundaryIdentifierRelationship entry : entity
					.getBoundaryIdentifierRelationships()) {
				EntityRelationshipType ert = et.addNewRelationship();
				ert.setPredicate(entry.getDefinition().getId());
				ert.setObject(entry.getValue());
			}
		}

		if (entity.getCompositeRelationships() != null) {
			for (ICompositeRelationship entry : entity
					.getCompositeRelationships()) {
				EntityRelationshipType ert = et.addNewRelationship();
				ert.setPredicate(entry.getDefinition().getId());
				ert.setObject(entry.getReferencedEntity().getId());
			}
		}

		if (entity.getKeyedRelationships() != null) {
			for (IKeyedRelationship entry : entity.getKeyedRelationships()) {
				EntityRelationshipType ert = et.addNewRelationship();
				ert.setPredicate(entry.getDefinition().getId());
				ert.setObject(entry.getReferencedEntity().getId());
			}
		}

		if (entity.getVersion() != null) {
			EntityVersionType evt = et.addNewVersion();
			evt.setStringValue(entity.getVersion().getValue());
		}

	}

	@Override
	public void visit(IContentNode entity) {
		visit((IEntity<?>) entity);
	}

	@Override
	public void visit(IGeneratedDocument entity) {
		visit((IEntity<?>) entity);
	}

	@Override
	public void visit(IKeyedDocument entity) {
		visit((IEntity<?>) entity);
	}
}
