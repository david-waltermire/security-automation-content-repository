package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.ContentException;
import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.KeyException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.xmlbeans.XmlCursor;

public class KeyedSupport {
	private final List<? extends IKeyedField> fields;

	public KeyedSupport(List<? extends IKeyedField> fields) {
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
				throw new KeyException("null field '" + id + "'");
			}
			fieldIdToValueMap.put(id, value);
		}
		return fieldIdToValueMap;
	}
}
