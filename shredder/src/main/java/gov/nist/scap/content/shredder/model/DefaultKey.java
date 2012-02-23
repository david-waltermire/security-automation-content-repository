package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IKeyDefinition;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * A key is a collection of identification fields that form a compound index. In
 * most simple cases this index may consist of a single field. Keys are used to
 * identify an {@link IElement} within the meta model.
 * 
 * @see IKeyDefinition
 * @see IElement
 */
class DefaultKey implements IKey {
	private static final Logger log = Logger.getLogger(DefaultKey.class);
	private final String id;
	private final LinkedHashMap<String, String> idToValueMap;

	private Integer hashCache;

	DefaultKey(String id, List<String> fieldIds, List<String> values) throws KeyException {
		this(id, fieldIds.toArray(new String[fieldIds.size()]), values.toArray(new String[values.size()]));
	}

	DefaultKey(String id, String[] fieldIds, String[] values) throws KeyException, KeyException {
		this.id = id;
		idToValueMap = new LinkedHashMap<String, String>();
		for (int i = 0; i < fieldIds.length; i++) {
			if (fieldIds[i] == null) {
				KeyException e = new KeyException("null field id for key '" + id + "'");
				log.error("null key field", e);
				throw e;
			} else if (values[i] == null) {
				KeyException e = new KeyException("null value for key '" + id + "' field: " + values[i]);
				log.error("illegal null field value", e);
				throw e;
			}
			idToValueMap.put(fieldIds[i], values[i]);
		}
	}

	DefaultKey(String id, LinkedHashMap<String, String> idToValueMap) throws KeyException, KeyException {
		this.id = id;
		this.idToValueMap = idToValueMap;
		for (Map.Entry<String, String> entry : idToValueMap.entrySet()) {
			if (entry.getKey() == null) {
				KeyException e = new KeyException("null field id for key '" + id + "'");
				log.error("null key field", e);
				throw e;
			} else if (entry.getValue() == null) {
				KeyException e = new KeyException("null value for key '" + id + "' field: " + entry.getKey());
				log.error("illegal null field value", e);
				throw e;
			}
		}
	}

	public String getId() {
		return id;
	}

	public Set<String> getFieldIds() {
		return Collections.unmodifiableSet(getFieldIdToValueMap().keySet());
	}

	public String getValue(String fieldId) {
		return getFieldIdToValueMap().get(fieldId);
	}

	public Collection<String> getValues() {
		return Collections.unmodifiableCollection(getFieldIdToValueMap().values());
	}

	/**
	 * @return {@inheritDoc}
	 */
	public LinkedHashMap<String, String> getFieldIdToValueMap() {
		return idToValueMap;
	}

	public String toString() {
		return new StringBuilder().append(id).append("=").append(
				getFieldIdToValueMap().toString()).toString();
	}

	/**
	 * This method overrides the default {@link java.lang.Object#equals(Object)}
	 * implementation based on an expectation that subclasses will not implement
	 * additional properties that factor into equality. If a subclass does add
	 * additional properties it is expected that that class will override both
	 * the {@link #equals(Object)} and {@link #canEqual(Object)} methods to
	 * insure that the equals contract is preserved.
	 * 
	 * @param other the object reference to compare to
	 * @return <code>true</code> if this object is the same as the other
     * 		argument or <code>false</code> otherwise.
	 * @see java.lang.Object#equals(Object)
	 */
    @Override
    public boolean equals(Object other) {
        boolean result = false;
        if (this == other) {
        	result = true;
        } else if (other instanceof DefaultKey) {
        	DefaultKey that = (DefaultKey) other;
            result = (that.canEqual(this)
            		&& this.getId().equals(that.getId())
            		&& this.getFieldIdToValueMap().equals(that.getFieldIdToValueMap())
            		);
        }
        return result;
    }

	/**
	 * This method is used to determine if the instance provided by the
	 * <code>other</code> argument is compatible for equals comparison. It is
	 * expected that subclasses will override this method if additional
	 * properties are added that will affect equality.
	 * 
	 * @param other the object reference to compare to
	 * @return <code>true</code> if this object is compatible for equals
	 *         comparison or <code>false</code> otherwise.
	 */
    protected boolean canEqual(Object other) {
        return (other instanceof DefaultKey);
    }

	/**
	 * Retrieves the current value in the hash cache for this instance.
	 * 
	 * @return the hashCache if the {@link #hashCode} method has been called or
	 *         <code>null</code> otherwise
	 */
	protected final Integer getHashCache() {
		return hashCache;
	}

	/**
	 * Returns the hash code for this instance. It is expected that subclasses
	 * will override this method if additional properties are added that will
	 * affect equality. This method implements a hash caching behavior for
	 * performance.
	 * 
	 * @return a hash code value for this object
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
    	if (hashCache == null) {
			int hash = 15;
			hash = hash * 31 + getId().hashCode();
			hash = hash * 31 + getFieldIdToValueMap().hashCode();
			hashCache = hash;
    	}
		return hashCache;
	}

	public int compareTo(IKey that) {
		if (this == that)
			return 0;

		int result = this.getId().compareTo(that.getId());
		if (result == 0) {
			Map<String, String> thatValueMap = that.getFieldIdToValueMap();
			for (Map.Entry<String, String> entry : getFieldIdToValueMap().entrySet()) {
				result = entry.getValue().compareTo(thatValueMap.get(entry.getKey()));
				if (result != 0)
					break;
			}
		}
		return result;
	}
}
