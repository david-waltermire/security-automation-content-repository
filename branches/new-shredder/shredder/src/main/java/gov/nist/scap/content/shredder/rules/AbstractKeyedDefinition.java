package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.IContainer;
import gov.nist.scap.content.shredder.model.KeyException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;

public class AbstractKeyedDefinition extends AbstractDefinition {
	private final List<? extends IKeyedField> fields;

	public AbstractKeyedDefinition(ISchema schema, String id, List<? extends IKeyedField> fields) throws XmlException {
		super(schema, id);
		this.fields = Collections.unmodifiableList(fields);
	}

	public List<? extends IKeyedField> getFields() {
		return fields;
	}

	protected LinkedHashMap<String, String> getFieldValues(IContainer<?> parentContext, XmlCursor cursor) throws KeyException, ContentException {
		LinkedHashMap<String, String> fieldIdToValueMap = new LinkedHashMap<String, String>();

		for (IKeyedField field : fields) {
			String id = field.getName();
			String value = field.getValue(parentContext, cursor);

			if (value == null) {
				throw new RuntimeException("null field '" + id + "' for key '" + getId() + "'");
			}
			fieldIdToValueMap.put(id, value);
		}
		return fieldIdToValueMap;
	}
}
