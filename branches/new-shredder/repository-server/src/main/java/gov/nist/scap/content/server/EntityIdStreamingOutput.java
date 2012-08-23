package gov.nist.scap.content.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.log4j.Logger;

public class EntityIdStreamingOutput implements StreamingOutput {
	private static final Logger log = Logger
			.getLogger(EntityIdStreamingOutput.class);
	private final Iterator<String> entityIds;
	private String ns = "http://scap.nist.gov/schema/content/entity-id-list/0.1";

	public EntityIdStreamingOutput(Iterator<String> iter) {
		this.entityIds = iter;
	}

	@Override
	public void write(OutputStream output) throws IOException,
			WebApplicationException {
		try {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = factory.createXMLStreamWriter(output);
			writer.setDefaultNamespace(ns);

			writer.writeStartDocument();
			writer.writeStartElement(ns, "entity-ids");
			writer.writeDefaultNamespace(ns);

			while (entityIds.hasNext()) {
				writer.writeStartElement(ns, "entity-id");
				writer.writeCharacters(entityIds.next());
				writer.writeEndElement();
			}
			writer.writeEndElement();
			writer.writeEndDocument();
		} catch (XMLStreamException e) {
			log.error("unable to stream output", e);
			throw new WebApplicationException(e);
		}
	}
}
