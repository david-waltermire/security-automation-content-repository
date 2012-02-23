package gov.nist.scap.content.shredder.rules;

import java.net.URI;

public class DefaultSchema implements ISchema {
	private final String id;
	private final URI namespace;

	public DefaultSchema(String id, URI namespace) {
		this.id = id;
		this.namespace = namespace;
	}

	public String getId() {
		return id;
	}

	/**
	 * @return the namespace
	 */
	public URI getNamespace() {
		return namespace;
	}
}
