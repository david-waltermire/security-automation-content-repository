package gov.nist.scap.content.server;

import static org.scapdev.content.core.query.Conditional.allOf;
import static org.scapdev.content.core.query.entity.EntityQuery.selectEntitiesWith;
import static org.scapdev.content.core.query.entity.Key.field;
import static org.scapdev.content.core.query.entity.Key.key;
import gov.nist.scap.content.model.IContentNode;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IEntityVisitor;
import gov.nist.scap.content.model.IGeneratedDocument;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedDocument;
import gov.nist.scap.content.model.IVersion;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.server.dto.SubmitResponseDto.SubmitEntityResponseDto;
import gov.nist.scap.content.shredder.parser.IEntityComparator;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.scapdev.content.core.persistence.ContentPersistenceManager;
import org.scapdev.content.core.query.entity.EntityQuery;
import org.scapdev.content.core.query.entity.Key.Field;
import org.scapdev.content.core.query.entity.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmitEntityVisitor  implements IEntityVisitor {

	private final ContentPersistenceManager contentRepo;
	private IEntityComparator entityComparator;
	private List<SubmitEntityResponseDto> responseList;
	
	private Logger log = LoggerFactory.getLogger(SubmitEntityVisitor.class);
	
	public SubmitEntityVisitor(ContentPersistenceManager contentRepo, IEntityComparator entityComparator, List<SubmitEntityResponseDto> responseList) {
		this.contentRepo = contentRepo;
		this.entityComparator = entityComparator;
		this.responseList = responseList;
	}
	
	
	@Override
	public void visit(IContentNode entity) {
		// disregard
	}
	
	@Override
	public void visit(IGeneratedDocument entity) {
		// disregard
	}
	
	@Override
	public void visit(IKeyedDocument entity) {
		IKey key = entity.getKey();
		IVersion version = entity.getVersion();

		Field[] f = new Field[key.getFieldNames().size()];
		Iterator<String> iter = key.getFieldNames().iterator();
		String fieldName;
		for (int j = 0, size = key.getFieldNames().size(); j < size; j++) {
			fieldName = iter.next();
			f[j] = field(fieldName, key.getValue(fieldName));
		}

		EntityQuery query;
		if (version != null) {
			query = selectEntitiesWith(allOf(key(key.getId(), f),
					Version.version(version.getValue())));
		} else {
			query = selectEntitiesWith(allOf(key(key.getId(), f)));
		}

		@SuppressWarnings("rawtypes")
		Collection<? extends IEntity> retVal;
		try {
			retVal = contentRepo
					.getEntities(query, false);
		} catch (ProcessingException e) {
			log.error("Processing error",e);
			throw new RuntimeException(e);
		}

		if (retVal.size() == 1) {

			IEntityComparator.STATUS status = entityComparator
					.compareEntities(retVal.iterator().next(), entity);
			if (status == null)
				throw new RuntimeException(
						"Unspecified error while comparing entities.");

			if (status == IEntityComparator.STATUS.IDENTICAL) {
				SubmitEntityResponseDto dto = new SubmitEntityResponseDto();
				dto.setKeyUri(key.getId());
				dto.setStatus(SubmitEntityResponseDto.STATUS.IDENTICAL);
				dto.setVersion(version != null ? version.getValue()
						: null);
				List<SubmitEntityResponseDto.Field> fields = new LinkedList<SubmitEntityResponseDto.Field>();
				for (Map.Entry<String, String> entry : key
						.getFieldNameToValueMap().entrySet()) {
					SubmitEntityResponseDto.Field field = new SubmitEntityResponseDto.Field();
					field.setName(entry.getKey());
					field.setValue(entry.getValue());
					fields.add(field);
				}
				responseList.add(dto);
				dto.setFields(fields);
				// TODO do something else!
			} else if (status == IEntityComparator.STATUS.SIMILAR) {
				SubmitEntityResponseDto dto = new SubmitEntityResponseDto();
				dto.setKeyUri(key.getId());
				dto.setStatus(SubmitEntityResponseDto.STATUS.SIMILAR);
				dto.setVersion(version != null ? version.getValue()
						: null);
				List<SubmitEntityResponseDto.Field> fields = new LinkedList<SubmitEntityResponseDto.Field>();
				for (Map.Entry<String, String> entry : key
						.getFieldNameToValueMap().entrySet()) {
					SubmitEntityResponseDto.Field field = new SubmitEntityResponseDto.Field();
					field.setName(entry.getKey());
					field.setValue(entry.getValue());
					fields.add(field);
				}
				dto.setFields(fields);
				responseList.add(dto);
				// TODO do something else!
			} else {
				SubmitEntityResponseDto dto = new SubmitEntityResponseDto();
				dto.setKeyUri(key.getId());
				dto.setStatus(SubmitEntityResponseDto.STATUS.DIFFERENT);
				dto.setVersion(version != null ? version.getValue()
						: null);
				List<SubmitEntityResponseDto.Field> fields = new LinkedList<SubmitEntityResponseDto.Field>();
				for (Map.Entry<String, String> entry : key
						.getFieldNameToValueMap().entrySet()) {
					SubmitEntityResponseDto.Field field = new SubmitEntityResponseDto.Field();
					field.setName(entry.getKey());
					field.setValue(entry.getValue());
					fields.add(field);
				}
				dto.setFields(fields);
				responseList.add(dto);
			}

		} else if (retVal.size() > 1) {
			// This exception should never happen
			SubmitEntityResponseDto dto = new SubmitEntityResponseDto();
			dto.setKeyUri(key.getId());
			dto.setVersion(version != null ? version.getValue()
					: null);
			List<SubmitEntityResponseDto.Field> fields = new LinkedList<SubmitEntityResponseDto.Field>();
			for (Map.Entry<String, String> entry : key
					.getFieldNameToValueMap().entrySet()) {
				SubmitEntityResponseDto.Field field = new SubmitEntityResponseDto.Field();
				field.setName(entry.getKey());
				field.setValue(entry.getValue());
				fields.add(field);
			}
			dto.setFields(fields);
		}
	}
}
