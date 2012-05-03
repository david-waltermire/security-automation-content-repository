package gov.nist.sparql.builder;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;

public class StringURIResource implements IResource {
	private final String uri;

	public StringURIResource(String uri) {
		this.uri = uri;
	}

	@Override
	public String getValue(ValueFactory vf) {
		URI result = vf.createURI(uri);
		return new StringBuilder().append('<').append(result.stringValue()).append('>').toString();
	}

}
