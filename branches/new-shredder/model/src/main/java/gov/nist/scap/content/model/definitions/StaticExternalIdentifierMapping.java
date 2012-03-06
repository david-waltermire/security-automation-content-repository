package gov.nist.scap.content.model.definitions;

import org.apache.xmlbeans.XmlCursor;

public class StaticExternalIdentifierMapping implements IExternalIdentifierMapping {
	private final IExternalIdentifier externalIdentifier;

	public StaticExternalIdentifierMapping(IExternalIdentifier externalIdentifier) {
		this.externalIdentifier = externalIdentifier;
	}

	public IExternalIdentifier resolveExternalIdentifier(XmlCursor cursor) {
		return externalIdentifier;
	}

}
