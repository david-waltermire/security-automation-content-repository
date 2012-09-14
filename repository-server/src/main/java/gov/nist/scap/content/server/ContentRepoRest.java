package gov.nist.scap.content.server;

import static org.scapdev.content.core.query.Conditional.allOf;
import static org.scapdev.content.core.query.entity.EntityQuery.selectEntitiesWith;
import static org.scapdev.content.core.query.entity.Key.field;
import static org.scapdev.content.core.query.entity.Key.key;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IEntityVisitor;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.KeyBuilder;
import gov.nist.scap.content.model.KeyException;
import gov.nist.scap.content.model.definitions.IKeyDefinition;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;
import gov.nist.scap.content.server.dto.ContentIdentiferDto;
import gov.nist.scap.content.server.dto.RetrieveRequestDto;
import gov.nist.scap.content.server.dto.SubmitResponseDto;
import gov.nist.scap.content.server.dto.SubmitResponseDto.SubmitEntityResponseDto;
import gov.nist.scap.content.shredder.parser.ContentHandler;
import gov.nist.scap.content.shredder.parser.ContentShredder;
import gov.nist.scap.content.shredder.parser.IEntityComparator;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.xmlbeans.XmlException;
import org.scapdev.content.core.ContentException;
import org.scapdev.content.core.persistence.ContentPersistenceManager;
import org.scapdev.content.core.query.entity.EntityId;
import org.scapdev.content.core.query.entity.EntityQuery;
import org.scapdev.content.core.query.entity.Key.Field;
import org.scapdev.content.core.query.entity.Version;
import org.springframework.beans.factory.ObjectFactory;
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
	private IMetadataModel metadataModel;

	@Autowired
	private ObjectFactory<ContentHandler> contentHandlerFactory;

	@Autowired
	private IEntityComparator entityComparator;

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
	public Response retrieve(RetrieveRequestDto dto) throws KeyException,
			MalformedURLException, UnsupportedEncodingException,
			ProcessingException {

		// Start parameter checking
		if (dto.getIdentifier() == null) {
			throw new IllegalArgumentException("An identifier is required");
		}

		if (dto.getIdentifier().getHost() == null) {
			throw new IllegalArgumentException("Host is required");
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

		if (PORT.intValue() != cid.getPort().intValue()) {
			throw new UnsupportedOperationException(
					"Remote content is not yet implemented...");
		}

		if (dto.getIdentifier().getKeyUri() != null) {
			if (dto.getIdentifier().getEntityId() != null) {
				throw new IllegalArgumentException(
						"Only one of key-uri OR content-id may be specified");
			}
			if (dto.getIdentifier().getKeyValues() == null
					|| dto.getIdentifier().getKeyValues().size() == 0) {
				throw new IllegalArgumentException(
						"1 or more key values is required");
			}

		} else if (dto.getIdentifier().getEntityId() != null) {
			if (dto.getIdentifier().getKeyUri() != null) {
				throw new IllegalArgumentException(
						"Only one of key-uri OR content-id may be specified");
			}
			if (dto.getIdentifier().getKeyValues() != null) {
				throw new IllegalArgumentException(
						"key-values may not be specified when content-id is specified");
			}
		} else {

			throw new IllegalArgumentException(
					"key-values or content Id must be specified");
		}
		// End parameter checking
		
		EntityQuery query = null;

		// if this is a request by key, then recreate the key query
		if (cid.getKeyUri() != null) {
			IKeyDefinition keyDef = metadataModel.getKeyById(cid.getKeyUri());

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

			for (int i = 0, size = keyDef.getFields().size(); i < size; i++) {
				builder.addField(keyDef.getFields().get(i).getName(), cid
						.getKeyValues().get(i));
			}
			IKey key = builder.toKey();

			Field[] f = new Field[key.getFieldNames().size()];
			Iterator<String> iter = key.getFieldNames().iterator();
			String fieldName;
			for (int j = 0, size = key.getFieldNames().size(); j < size; j++) {
				fieldName = iter.next();
				f[j] = field(fieldName, key.getValue(fieldName));
			}

			// if version user version query otherwise user previous query
			query = selectEntitiesWith(allOf(key(key.getId(), f),
					Version.version(dto.getIdentifier().getVersion())));

		} else if (cid.getEntityId() != null) {
			// if this is a request by entity id, then create the entity id query
			query = selectEntitiesWith(allOf(EntityId.entityId(dto
					.getIdentifier().getEntityId())));

		}

		// We can't guarantee that all entities are keyed if they are retrieved by entity id
		@SuppressWarnings("rawtypes")
		Collection<? extends IEntity> retVal = contentRepo.getEntities(query,
				false);

		RetrieveEntityVisitor visitor = new RetrieveEntityVisitor();
		for( IEntity<?> entity : retVal ) {
			entity.accept(visitor);
		}
		
		return visitor.getResponse();
	}

	@POST
	@Path("submit")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_JSON)
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

			ContentHandler handler = contentHandlerFactory.getObject();

			shredder.shred(is, encoding, handler);

			List<SubmitEntityResponseDto> responseList = new LinkedList<SubmitEntityResponseDto>();
			IEntityVisitor visitor = new SubmitEntityVisitor(contentRepo, entityComparator, responseList);
			for (IEntity<?> i : handler.getEntities()) {
				i.accept(visitor);
			}

			
			if( responseList.size() == 0 ) {
				List<String> storedEntities = contentRepo.storeEntities(handler
						.getEntities());
				SubmitResponseDto responseDto = new SubmitResponseDto();
				responseDto.setStatus(SubmitResponseDto.STATUS.SUCCESS);
				responseDto.setNewEntity(new URI(storedEntities.get(storedEntities.size() - 1)));
				
				return responseDto;
			} else {
				SubmitResponseDto responseDto = new SubmitResponseDto();
				responseDto.setStatus(SubmitResponseDto.STATUS.FAILED);
				responseDto.setEntityList(responseList);
				return responseDto;
			}
			
			

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
		} catch (URISyntaxException e) {
			// should never happen
			throw new WebApplicationException(e);
		}
		
	}

	@GET
	@Path("get-top-entities")
	@Produces(MediaType.APPLICATION_XML)
	public StreamingOutput getTopLevelEntities() {
		return new EntityIdStreamingOutput(contentRepo.getAllTopLevelEntities());

	}

}
