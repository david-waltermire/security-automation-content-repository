package gov.nist.scap.content.model.definitions;

import java.util.Collections;
import java.util.List;

public class KeyedSupport {
	private final List<? extends IKeyedField> fields;

	public KeyedSupport(List<? extends IKeyedField> fields) {
		this.fields = Collections.unmodifiableList(fields);
	}

	public List<? extends IKeyedField> getFields() {
		return fields;
	}
}
