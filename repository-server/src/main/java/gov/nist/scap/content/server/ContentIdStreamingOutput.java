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

public class ContentIdStreamingOutput implements StreamingOutput {
	private static final Logger log = Logger
			.getLogger(ContentIdStreamingOutput.class);
	private final Iterator<String> contentIds;
	private String ns = "http://scap.nist.gov/schema/content/content-id-list/0.1";

	public ContentIdStreamingOutput(Iterator<String> iter) {
		this.contentIds = iter;
	}

	@Override
	public void write(OutputStream output) throws IOException,
			WebApplicationException {
		try {
			XMLOutputFactory factory = XMLOutputFactory.newInstance();
			XMLStreamWriter writer = factory.createXMLStreamWriter(output);
			writer.setDefaultNamespace(ns);

			writer.writeStartDocument();
			writer.writeStartElement(ns, "content-ids");
			writer.writeDefaultNamespace(ns);

			while (contentIds.hasNext()) {
				writer.writeStartElement(ns, "content-id");
				writer.writeCharacters(contentIds.next());
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
