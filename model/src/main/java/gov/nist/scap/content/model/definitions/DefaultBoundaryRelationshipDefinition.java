package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.ContentException;

import org.apache.xmlbeans.XmlException;

public class DefaultBoundaryRelationshipDefinition extends AbstractRelationshipDefinition implements IBoundaryRelationshipDefinition {
	/**
	 * The content definition mapping associated with this boundary
	 */
	private final ContentMapping contentMapping;

	/**
	 * Creates a new boundary instance.
	 * @param schema
	 * @param id the boundary identifier
	 * @param xpath the xpath used to navigate to this boundary from the
	 * 		containing node or <code>null</code> if the boundary is at the same
	 * 		location as the containing node
	 * @param contentMapping the content mapping to use when processing children
	 * 		of this boundary
	 * @param parent the parent containing this boundary
	 * @throws XmlException if the specified XPath is invalid
	 * @throws NullPointerException if the id, contentMapping, or parent
	 * 		parameters are <code>null</code>
	 * @throws IllegalArgumentException if the id or xpath arguments are empty
	 */
	public DefaultBoundaryRelationshipDefinition(ISchema schema, String id, String xpath, ContentMapping contentMapping) throws XmlException {
		super(schema, id, xpath);

		if (xpath == null) {
			throw new NullPointerException("xpath");
		}

		if (contentMapping == null) {
			throw new NullPointerException("contentMapping");
		}

		this.contentMapping = contentMapping;
	}

	public ContentMapping getContentMapping() {
		return contentMapping;
	}

	public void accept(IRelationshipDefinitionVisitor visitor) throws ContentException, ProcessingException {
		visitor.visit(this);
	}
}
