package gov.nist.sparql.builder;

import java.net.URI;

class Prefix implements IPrefix {

	private final URI uri;
	private final String label;

	public Prefix(URI uri, String label) {
		this.uri = uri;
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public URI getUri() {
		return uri;
	}

	@Override
	public IResource resource(String name) {
		return new PrefixedResource(this, name);
	}

}
