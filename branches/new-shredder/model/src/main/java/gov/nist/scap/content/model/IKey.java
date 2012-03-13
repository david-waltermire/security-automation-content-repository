package gov.nist.scap.content.model;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * This interface represents an ordered mapping of fields and values that can be
 * used to identify some concept.
 * 
 * The contract of this interface requires that all fields must have values
 * associated with them. Definition of a field with a <code>null</code> value
 * must result in a {@link java.lang.NullPointerException}. This will typically
 * be thrown from the implementing class constructor.
 */
public interface IKey extends Comparable<IKey> {
	/**
	 * Retrieves the unique identifier for this key
	 * 
	 * @return the key's unique identifier
	 */
	String getId();

	/**
	 * Retrieves a set containing the names of each field participating in the
	 * key. Even though this method returns a set, it is expected that the
	 * ordering of the values in the key will be preserved by any set operations
	 * that iterate over the contents.
	 * 
	 * @return an ordered set of field names
	 */
	Set<String> getFieldNames();

	/**
	 * Retrieves the value associated with a given field.
	 * 
	 * @param fieldName the name of the field possessing the value to be retrieved
	 * @return the requested field value
	 */
	String getValue(String fieldName);

	/**
	 * Retrieves a set containing the values of each field participating in the
	 * key. Even though this method returns a collection, it is expected that
	 * the ordering of the values in the key will be preserved by any collection
	 * operations that iterate over the contents.
	 * 
	 * @return an ordered collection of field values
	 */
	Collection<String> getValues();

	/**
	 * @return the mapping between key field names and values
	 */
	LinkedHashMap<String, String> getFieldNameToValueMap();
}
