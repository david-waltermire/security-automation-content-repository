package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IVersionDefinition;

public class DefaultVersion implements IVersion {
	private final IVersionDefinition definition;
	private final String value;

	public DefaultVersion(IVersionDefinition definition, String value) {
		if (definition == null) {
			throw new NullPointerException("definition");
		}
		if (value == null) {
			throw new NullPointerException("value");
		}
		this.definition = definition;
		this.value = value;
	}

	public IVersionDefinition getDefinition() {
		return definition;
	}

	public String getValue() {
		return value;
	}

	public double getValueAsDouble() {
		return Double.valueOf(getValue());
	}

	public int getValueAsInt() {
		return Integer.valueOf(getValue());
	}
}
