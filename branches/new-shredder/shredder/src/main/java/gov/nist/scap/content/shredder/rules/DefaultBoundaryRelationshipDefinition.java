package gov.nist.scap.content.shredder.rules;

import javax.xml.namespace.QName;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.IMutableEntity;
import gov.nist.scap.content.shredder.parser.ContentHandler;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;

public class DefaultBoundaryRelationshipDefinition extends AbstractRelationshipDefinition implements IBoundaryRelationshipDefinition {
	private static final Logger log = Logger.getLogger(DefaultBoundaryRelationshipDefinition.class);

	/**
	 * The content definition mapping associated with this boundary
	 */
	private final ContentMapping contentMapping;

//	/**
//	 * The content definition containing this boundary
//	 */
//	private final IContentDefinition parent;

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
//
//		if (parent == null) {
//			throw new NullPointerException("parent");
//		}

		this.contentMapping = contentMapping;
//		this.parent = parent;
	}
//
//	public IContentDefinition getParent() {
//		return parent;
//	}

	@Override
	protected void handleRelationship(XmlCursor cursor,
			ContentHandler handler, IMutableEntity<?> containingEntity)
			throws ProcessingException, ContentException {
		QName qname = cursor.getName();
		// retrieve the content definition to use to process the child node
		IEntityDefinition contentDefinition = contentMapping.getContentDefinitionForQName(qname);
		if (contentDefinition == null) {
			log.warn("Unrecognized QName '"+qname.toString()+"' at boundary: "+getId());
		} else {
			// process the child node
			contentDefinition.processCursor(cursor, this, handler, containingEntity);
		}
	}
}
