package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.DefaultIndirectRelationship;
import gov.nist.scap.content.shredder.model.IMutableEntity;
import gov.nist.scap.content.shredder.parser.ContentHandler;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;

public class DefaultIndirectRelationshipDefinition extends
		AbstractRelationshipDefinition implements
		IIndirectRelationshipDefinition {
	private static final Logger log = Logger.getLogger(DefaultIndirectRelationshipDefinition.class);
	private final XPathRetriever valueRetriever;
	private final IExternalIdentifierMapping qualifierMapping;

	public DefaultIndirectRelationshipDefinition(ISchema schema, String id,
			String xpath, String valueXpath, IExternalIdentifierMapping qualifierMapping) throws XmlException {
		super(schema, id, xpath);

		if (valueXpath == null) {
			throw new NullPointerException("valueXpath");
		}

		if (qualifierMapping == null) {
			throw new NullPointerException("qualifierMapping");
		}
		this.valueRetriever = new XPathRetriever(XmlBeans.compilePath(valueXpath));
		this.qualifierMapping = qualifierMapping;
	}

	@Override
	protected void handleRelationship(XmlCursor cursor, ContentHandler handler,
			IMutableEntity<?> containingEntity) throws ProcessingException,
			ContentException {
		String value = valueRetriever.getValue(cursor);
		IExternalIdentifier externalIdentifier = qualifierMapping.resolveExternalIdentifier(cursor);
		if (externalIdentifier != null) {
			containingEntity.appendRelationship(new DefaultIndirectRelationship(this, containingEntity, externalIdentifier, value));
		} else {
			log.warn("Unable to extract indirect relationship for value: "+value);
		}
	}

}
