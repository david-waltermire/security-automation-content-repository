package gov.nist.scap.content.shredder.rules;

import org.apache.xmlbeans.XmlCursor;

public interface IExternalIdentifierMapping {
	IExternalIdentifier resolveExternalIdentifier(XmlCursor cursor);
}
