package gov.nist.scap.content.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;


public interface IKey extends Comparable<IKey> {
	String getId();
	Set<String> getFieldIds();
	String getValue(String fieldId);
	Collection<String> getValues();
	/**
	 * @return the mapping between key field identifiers and values
	 */
	LinkedHashMap<String, String> getFieldIdToValueMap();
}
