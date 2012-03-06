package gov.nist.scap.content.model.definitions;


import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

public class RuleDefinitions {
	private final Map<QName, IDocumentDefinition> documentDefinitions;

	public RuleDefinitions() {
		documentDefinitions = new HashMap<QName, IDocumentDefinition>();
	}

	public IDocumentDefinition getDocumentDefinition(QName name) {
		return documentDefinitions.get(name);
	}

	public void add(IDocumentDefinition document) {
		if (documentDefinitions.containsKey(document.getName())) {
			throw new RuntimeException("duplicate document definition for qname: "+document.getName());
		}
		documentDefinitions.put(document.getName(), document);
	}
}
