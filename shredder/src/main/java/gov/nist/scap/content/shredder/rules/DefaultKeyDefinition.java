package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.IContainer;
import gov.nist.scap.content.shredder.model.IKey;
import gov.nist.scap.content.shredder.model.KeyBuilder;
import gov.nist.scap.content.shredder.model.KeyException;

import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;

public class DefaultKeyDefinition extends AbstractKeyedDefinition implements IKeyDefinition {
	public DefaultKeyDefinition(ISchema schema, String id, List<? extends IKeyedField> keyFields) throws XmlException {
		super(schema, id, keyFields);
	}

	public IKey getKey(IContainer<?> parentContext, XmlCursor cursor) throws KeyException, ContentException {
		Map<String, String> fieldIdToValueMap = getFieldValues(parentContext, cursor);
		KeyBuilder builder = new KeyBuilder(getFields());
		builder.setId(getId());
		builder.addFields(fieldIdToValueMap);
		return builder.toKey();
	}
}
