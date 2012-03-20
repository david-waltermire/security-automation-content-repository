package gov.nist.scap.content.model.definitions;

import java.util.HashMap;
import java.util.Map;

import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;

public class QualifiedExternalIdentifierMapping implements IExternalIdentifierMapping {
	private final Map<String, IExternalIdentifier> qualifierToExternalIdentifierMap = new HashMap<String, IExternalIdentifier>();
	private final XPathRetriever retriever;

	public QualifiedExternalIdentifierMapping(String xpath) throws XmlException {
		this.retriever = new XPathRetriever(XmlBeans.compilePath(xpath));
	}

	public void addQualifier(String qualifier, IExternalIdentifier externalIdentifier) {
		qualifierToExternalIdentifierMap.put(qualifier, externalIdentifier);
	}

	public IExternalIdentifier resolveExternalIdentifier(XmlCursor cursor) {
		String qualifier = retriever.getValue(null, cursor);
		return qualifierToExternalIdentifierMap.get(qualifier);
	}

}
