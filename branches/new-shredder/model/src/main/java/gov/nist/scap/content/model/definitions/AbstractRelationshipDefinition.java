package gov.nist.scap.content.model.definitions;

import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;

public abstract class AbstractRelationshipDefinition extends AbstractSchemaRelatedDefinition implements IRelationshipDefinition {
	/**
	 * The xpath used to navigate to the XML element associated with this
	 * relationship. This may be <code>null</code> if the relationship is at the
	 * same location as the containing content node
	 */
	private final String xpath;

	public AbstractRelationshipDefinition(ISchemaDefinition schema, String id, String xpath) throws XmlException {
		super(schema, id);

		if (xpath != null && xpath.isEmpty()) {
			throw new IllegalArgumentException("xpath must not be empty");
		}

		this.xpath = (xpath == null ? null : XmlBeans.compilePath(xpath));
	}

	public String getXpath() {
		return xpath;
	}
}
