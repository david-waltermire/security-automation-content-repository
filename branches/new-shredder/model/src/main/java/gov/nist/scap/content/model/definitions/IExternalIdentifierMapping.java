package gov.nist.scap.content.model.definitions;

import org.apache.xmlbeans.XmlCursor;

public interface IExternalIdentifierMapping {
	IExternalIdentifier resolveExternalIdentifier(XmlCursor cursor);
}
