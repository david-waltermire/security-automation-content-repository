package gov.nist.scap.content.definitions;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

public class ContentMapping {
	/**
	 * A mapping of QNames to content definitions. The <code>null</code> entry in
	 * this map designates the default content definition if it exists. 
	 */
	private final Map<QName, IEntityDefinition> qnameToContentDefinitionMap = new HashMap<QName, IEntityDefinition>();

	public ContentMapping() {
	}

	/**
	 * Sets the default content definition to use if no matching QName is found
	 * @param defaultDefinition the default content definition
	 */
	public void setDefaultContentDefinition(IEntityDefinition defaultDefinition) {
		qnameToContentDefinitionMap.put(null, defaultDefinition);
	}

	/**
	 * Add a QName to content definition mapping to the collection.
	 * @param qname a non-null qname
	 * @param definition a content node definition
	 * @return the old content definition mapped to the qname argument or
	 * 		<code>null</code> if no mapping existed
	 * @throws NullPointerException if either the qname or definition arguments
	 * 		are <code>null</code>
	 */
	public IEntityDefinition addContentDefinition(QName qname, IEntityDefinition definition) {
		if (qname == null) {
			throw new NullPointerException("qname");
		}
		if (definition == null) {
			throw new NullPointerException("definition");
		}
		return qnameToContentDefinitionMap.put(qname, definition);
	}

	/**
	 * Retrieves the content definition associated with the qname or the default
	 * content definition if no association exists.
	 * @param qname the qname to retrieve the content definition for
	 * @return an content definition if a qname mapping was found, the default
	 * 		content definition if one exists, or <code>null<code> otherwise
	 */
	public IEntityDefinition getContentDefinitionForQName(QName qname) {
		// retrieve the content definition associated with the qname argument
		IEntityDefinition retval = qnameToContentDefinitionMap.get(qname);
		if (retval == null) {
			// Use the default content definition if it exists
			retval = qnameToContentDefinitionMap.get(null);
		}
		return retval;
	}
}
