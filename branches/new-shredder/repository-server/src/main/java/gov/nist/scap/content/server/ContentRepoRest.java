package gov.nist.scap.content.server;

import static org.scapdev.content.core.query.Conditional.allOf;
import static org.scapdev.content.core.query.entity.EntityContext.entityType;
import static org.scapdev.content.core.query.entity.EntityQuery.selectEntitiesWith;
import static org.scapdev.content.core.query.entity.Key.field;
import static org.scapdev.content.core.query.entity.Key.key;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.KeyBuilder;
import gov.nist.scap.content.model.KeyException;
import gov.nist.scap.content.model.definitions.IKeyDefinition;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.semantic.TripleStoreFacadeMetaDataManager;
import gov.nist.scap.content.semantic.entity.EntityProxy;
import gov.nist.scap.content.semantic.entity.KeyedEntityProxy;
import gov.nist.scap.content.server.dto.ContentIdentiferDto;
import gov.nist.scap.content.server.dto.RetrieveRequestDto;
import gov.nist.scap.content.shredder.parser.ContentHandler;
import gov.nist.scap.content.shredder.parser.ContentHandlerFactory;
import gov.nist.scap.content.shredder.parser.ContentShredder;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.apache.xmlbeans.XmlException;
import org.openrdf.repository.RepositoryException;
import org.scapdev.content.core.ContentException;
import org.scapdev.content.core.persistence.ContentPersistenceManager;
import org.scapdev.content.core.query.entity.EntityQuery;
import org.scapdev.content.core.query.entity.Key.Field;
import org.springframework.beans.factory.annotation.Autowired;

@Path("/")
public class ContentRepoRest {

	public static String CONTENT_STORE_CLASS = "";
	public static String METADATA_STORE_CLASS = "";

	@Autowired
	private ContentPersistenceManager contentRepo;

	@Autowired
	private ContentShredder shredder;

	@Autowired
	private TripleStoreFacadeMetaDataManager tsfmdm;

	@Autowired
	private ContentHandlerFactory contentHandlerFactory;

	private static final Pattern httpCharsetPattern = Pattern
			.compile("charset=(\\S+)");
	private static final Pattern xmlCharsetPattern = Pattern
			.compile("^\\s*\\<\\?xml[\\s\\S]+encoding\\s*=[\"'](\\S+)[\"'][\\s\\S]*\\?\\>");

	private static final String HOSTNAME = "usgcb.nist.gov";
	private static final Integer PORT = 8080;

	@POST
	@Path("retrieve")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_XML)
	public Object retrieve(RetrieveRequestDto dto) throws KeyException,
			MalformedURLException, UnsupportedEncodingException,
			ProcessingException {

		if( dto.getIdentifier() == null ) {
			throw new IllegalArgumentException(
					"An identifier is required");
		}

		if( dto.getIdentifier().getHost() == null ) {
			throw new IllegalArgumentException(
					"Host is required");
		}

		if( dto.getIdentifier().getKeyUri() == null ) {
			throw new IllegalArgumentException(
					"Key URI is required");
		}

		if( dto.getIdentifier().getKeyValues() == null || dto.getIdentifier().getKeyValues().size() == 0) {
			throw new IllegalArgumentException(
					"1 or more key values is required");
		}

		Boolean metadata = dto.getMetadata();

		if (metadata != null && metadata.equals(true)) {
			throw new IllegalArgumentException(
					"The metadata parameter is not yet supported");
		}

		Integer depth = dto.getDepth();

		if (depth != null && !depth.equals(-1)) {
			throw new IllegalArgumentException(
					"The depth parameter is not yet supported");
		}

		ContentIdentiferDto cid = dto.getIdentifier();

		if (!HOSTNAME.equals(cid.getHost())) {
			throw new UnsupportedOperationException(
					"Remote content is not yet implemented...");
		}

		if (PORT.intValue() != cid.getPort().intValue() ) {
			throw new UnsupportedOperationException(
					"Remote content is not yet implemented...");
		}

		IKeyDefinition keyDef = tsfmdm.getOntology()
				.getKeyById(cid.getKeyUri());

		if (keyDef == null) {
			throw new IllegalArgumentException("Could not find key: "
					+ cid.getKeyUri());
		}

		KeyBuilder builder = new KeyBuilder(keyDef.getFields());
		builder.setId(keyDef.getId());
		if (keyDef.getFields().size() != cid.getKeyValues().size()) {
			throw new IllegalArgumentException(
					"Number of field values does not match key fields. Expecting "
							+ keyDef.getFields().size() + " fields.");
		}
		
		for (int i=0,size = keyDef.getFields().size(); i<size; i++) {
			builder.addField(keyDef.getFields().get(i).getName(), cid.getKeyValues().get(i));
		}
		IKey key = builder.toKey();

		Field[] f = new Field[key.getFieldNames().size()];
		Iterator<String> iter = key.getFieldNames().iterator();
		String fieldName;
		for (int j = 0, size = key.getFieldNames().size(); j < size; j++) {
			fieldName = iter.next();
			f[j] = field(fieldName, key.getValue(fieldName));
		}

		EntityQuery query = selectEntitiesWith(allOf(key(key.getId(), f)));

		@SuppressWarnings("rawtypes")
		Collection<? extends IKeyedEntity> retVal = tsfmdm.getEntities(query,
				KeyedEntityProxy.class);

		if (retVal.size() == 0) {
			throw new WebApplicationException(404);
		}

		if (retVal.size() > 1) {
			// TODO the body should include all of the specific
			// resources...see HTTP spec
			throw new WebApplicationException(300);
		}

		return retVal.iterator().next().getContentHandle().getCursor()
				.getObject().newInputStream();

	}

	@POST
	@Path("submit")
	@Consumes("application/xml")
	@Produces("text/xml")
	public Object submit(@HeaderParam("Content-Type") String contentType,
			InputStream is) {

		try {
			String encoding = null;
			if (contentType != null) {
				Matcher m = httpCharsetPattern.matcher(contentType);
				if (m.find()) {
					encoding = m.group(1);
				}
			}

			if (encoding == null) {

				byte[] bArray = new byte[1024]; // the XML header should
												// complete within the first
												// kilobyte
				int readLength = is.read(bArray);
				Matcher m = xmlCharsetPattern.matcher(new String(bArray));
				if (m.find()) {
					encoding = m.group(1);
				}
				is = new PassThroughInputStream(is, bArray, readLength);
			}

			if (encoding == null) {
				encoding = "UTF-8"; // the default encoding for XML
			}
			ContentHandler handler = contentHandlerFactory.newContentHandler();
			shredder.shred(is, encoding, handler);
			List<String> storedEntities = contentRepo.storeEntities(handler
					.getEntities());

			return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<content-id>"
					+ storedEntities.get(storedEntities.size() - 1)
					+ "</content-id>";
		} catch (XmlException e) {
			e.printStackTrace();
			throw new WebApplicationException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WebApplicationException(e);
		} catch (ProcessingException e) {
			e.printStackTrace();
			throw new WebApplicationException(e);
		} catch (ContentException e) {
			e.printStackTrace();
			throw new WebApplicationException(e);
		}
	}

	@POST
	@Path("sparql-query")
	@Produces("text/xml")
	public Object sparqlQuery(String sparqlQuery) {
		// TODO delete this once testing is complete
		try {
			List<SortedMap<String, String>> r;
			r = tsfmdm.runSPARQL(sparqlQuery);

			StringBuilder returnString = new StringBuilder();
			returnString.append("<results>\n");

			for (SortedMap<String, String> m : r) {
				returnString.append("  <result>\n");
				for (String key : m.keySet()) {
					returnString.append("    <key>" + key + "</key><value>"
							+ m.get(key) + "</value>\n");
				}
				returnString.append("  </result>\n");
			}

			returnString.append("</result>\n");
			return returnString.toString();
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new WebApplicationException(e);
		}

	}

	@GET
	@Path("test")
	@Produces("text/xml")
	public StreamingOutput test() {
		// TODO delete this once testing is complete

		// // Generic query
		// EntityQuery query = selectEntitiesWith(allOf(
		// // anyOf(
		// //
		// entityType("http://oval.mitre.org/resource/content/definition/5#content-node-test"),
		// //
		// entityType("http://oval.mitre.org/resource/content/definition/5#content-node-definition")
		// // ),
		// relationship(
		// // anyOf(
		// //
		// relationshipType("http://scap.nist.gov/resource/content/model#hasParentRelationshipTo"),
		// toBoundaryIdentifier(
		// "http://cce.mitre.org/resource/content/cce#external-identifier-cce-5",
		// "CCE-13091-4")
		// // )
		// )));

		EntityQuery query = selectEntitiesWith(
		// entityType("http://oval.mitre.org/resource/content/definition/5#content-node-definition")
		entityType("http://scap.nist.gov/resource/content/source/1.2#document-datastream-collection"));

		try {
			@SuppressWarnings("unchecked")
			Collection<? extends IEntity<?>> results = (Collection<? extends IEntity<?>>) tsfmdm
					.getEntities(query, EntityProxy.class);
			// StringBuilder returnString = new StringBuilder();
			// returnString.append("<results>\n");
			//
			// for (IEntity<?> entityObj : results) {
			// returnString.append("  <class>");
			// returnString.append(entityObj.getDefinition().getId());
			// returnString.append("</class>\n");
			// // returnString.append("  <uri>");
			// // returnString.append(entityObj.getUri());
			// // returnString.append("</uri>\n");
			// }
			//
			// returnString.append("</result>\n");

			return new EntityCollectionStreamingOutput(results);
		} catch (ProcessingException e) {
			e.printStackTrace();
			throw new WebApplicationException(e);
		}

	}

}
