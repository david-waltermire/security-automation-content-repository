package gov.nist.scap.content.server;

import gov.nist.scap.content.model.IContentHandle;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.IRelationship;
import gov.nist.scap.content.model.IVersion;
import gov.nist.scap.schema.content.entity.x01.EntityContentType;
import gov.nist.scap.schema.content.entity.x01.EntityDocument;
import gov.nist.scap.schema.content.entity.x01.EntityKeyFieldType;
import gov.nist.scap.schema.content.entity.x01.EntityKeyType;
import gov.nist.scap.schema.content.entity.x01.EntityPropertyType;
import gov.nist.scap.schema.content.entity.x01.EntityType;
import gov.nist.scap.schema.content.entity.x01.EntityVersionType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;

public class EntityCollectionStreamingOutput implements StreamingOutput {
	private static final Logger log = Logger.getLogger(EntityCollectionStreamingOutput.class);
	private final Collection<? extends IEntity<?>> entities;

	public EntityCollectionStreamingOutput(Collection<? extends IEntity<?>> entities) {
		this.entities = entities;
	}

	@Override
	public void write(OutputStream output) throws IOException,
			WebApplicationException {
		try {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = factory.createXMLStreamWriter(output);
			writer.setDefaultNamespace("http://scap.nist.gov/schema/content/entity/0.1");

			writer.writeStartDocument();
			writer.writeStartElement("http://scap.nist.gov/schema/content/entity/0.1", "entity-collection");
			writer.writeDefaultNamespace("http://scap.nist.gov/schema/content/entity/0.1");

			XmlOptions xmlOptions = new XmlOptions();
			xmlOptions.setSaveNoXmlDecl();
			xmlOptions.setSaveOuter();
			xmlOptions.setSaveUseOpenFrag();
			for (IEntity<?> entity : entities) {
				EntityDocument entityDoc = createEntity(entity);
				EntityType entityType = entityDoc.getEntity();
				XmlReaderToWriter.writeAll(entityType.newXMLStreamReader(xmlOptions), writer);
			}

			writer.writeEndElement();
			writer.writeEndDocument();
		} catch (XMLStreamException e) {
			log.error("unable to stream output", e);
			throw new WebApplicationException(e);
		}
	}

	private EntityDocument createEntity(IEntity<?> entity) {
		EntityDocument retval = EntityDocument.Factory.newInstance();
		EntityType data = retval.addNewEntity();

		data.setIdentifier(entity.getId());
		data.setType(entity.getDefinition().getId());

		// if it's a keyed type, then populate the key info
		if( entity instanceof IKeyedEntity ) {
			IKey key = ((IKeyedEntity<?>)entity).getKey();
			EntityKeyType ekt = data.addNewKey();
			ekt.setId(key.getId());
			for( Map.Entry<String, String> field : key.getFieldNameToValueMap().entrySet() ) {
				EntityKeyFieldType ekft = ekt.addNewField();
				ekft.setId(field.getKey());
				ekft.setValue(field.getValue());
			}
		}
		
		IVersion version = entity.getVersion();
		// if the entity is versioned, set the version info
		if (version != null) {
			EntityVersionType versionData = data.addNewVersion();
			versionData.setStringValue(version.getValue());
		}

		// if the entity has properties, add each property
		for (Map.Entry<String, ? extends Set<String>> entry : entity.getProperties().entrySet()) {
			EntityPropertyType propertyData = data.addNewProperty();
			propertyData.setName(entry.getKey());

			for (String value : entry.getValue()) {
				propertyData.addValue(value);
			}
		}

		// if the entity has relationships, add them
		for (IRelationship<?> relationship : entity.getRelationships()) {
			relationship.accept(new XmlBeansEntityRelationshipVisitor(data));
		}

		// add the content associated with the entity
		EntityContentType contentData = data.addNewContent();

		IContentHandle handle = entity.getContentHandle();
		XmlCursor srcCursor = handle.getCursor();
		//srcCursor.toFirstChild();

		XmlCursor destCursor = contentData.newCursor();
		destCursor.toEndToken();
		srcCursor.copyXml(destCursor);
		return retval;
	}
}
