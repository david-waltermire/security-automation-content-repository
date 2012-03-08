package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.ContentException;
import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.KeyBuilder;
import gov.nist.scap.content.model.KeyException;

import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;

public class DefaultKeyDefinition extends AbstractDefinition implements IKeyDefinition {
	private final KeyedSupport keyedSupport;

	public DefaultKeyDefinition(ISchema schema, String id, List<? extends IKeyedField> keyFields) throws XmlException {
		super(schema, id);
		this.keyedSupport = new KeyedSupport(keyFields);
	}

	public List<? extends IKeyedField> getFields() {
		return keyedSupport.getFields();
	}

	public IKey getKey(IContainer<?> parentContext, XmlCursor cursor) throws KeyException, ContentException {
		Map<String, String> fieldIdToValueMap = keyedSupport.getFieldValues(parentContext, cursor);
		KeyBuilder builder = new KeyBuilder(getFields());
		builder.setId(getId());
		builder.addFields(fieldIdToValueMap);
		return builder.toKey();
	}
}
