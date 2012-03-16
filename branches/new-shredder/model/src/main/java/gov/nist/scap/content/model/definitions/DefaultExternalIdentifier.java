package gov.nist.scap.content.model.definitions;

import java.util.regex.Pattern;

public class DefaultExternalIdentifier implements IExternalIdentifier {
	private final String id;
	private final String namespace;
	private final Pattern pattern;

	public DefaultExternalIdentifier(String id, String namespace, Pattern pattern) {
		this.id = id;
		this.namespace = namespace;
		this.pattern = pattern;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	public String getNamespace() {
		return namespace;
	}

	/**
	 * @return the pattern
	 */
	public Pattern getPattern() {
		return pattern;
	}
}
