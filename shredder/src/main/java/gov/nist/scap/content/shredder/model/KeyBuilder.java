package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IKeyedField;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KeyBuilder {
	private String id;
	private final List<? extends IKeyedField> fields;
	private LinkedHashMap<String, String> fieldIdToValueMap = new LinkedHashMap<String, String>();
	
	public KeyBuilder(List<? extends IKeyedField> fields) {
		this.fields = fields;
	}
	
	public KeyBuilder setId(String id) {
		this.id = id;
		return this;
	}

	public KeyBuilder addField(String fieldId, String fieldValue){
		fieldIdToValueMap.put(fieldId, fieldValue);
		return this;
	}

	public KeyBuilder addFields(Map<String, String> fieldIdToValueMap) {
		this.fieldIdToValueMap.putAll(fieldIdToValueMap);
		return this;
	}
	
	/**
	 * Builds a key out of all data added to builder.
	 * @return
	 */
	public IKey toKey() throws KeyException {
		if (id == null || id.isEmpty() || fieldIdToValueMap.isEmpty()){
			throw new KeyException("Not all values are populated");
		}

		// Insure the correct ordering of fields and that the key is complete
		if (fields.size() != fieldIdToValueMap.size()) {
			throw new KeyException("incomplete key fields");
		}

		LinkedHashMap<String, String> fieldValueMap = new LinkedHashMap<String, String>();
		for (IKeyedField field : fields) {
			String fieldName = field.getName();
			String value = fieldIdToValueMap.get(fieldName);
			if (value == null) {
				throw new KeyException("key '"+id+"' has null value for field: "+fieldName);
			}
			fieldValueMap.put(fieldName, value);
		}
		IKey key = new DefaultKey(id, fieldValueMap);
		return key;
	}
}
