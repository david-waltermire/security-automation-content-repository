package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IVersionDefinition;
import gov.nist.scap.content.model.definitions.IVersioningMethodDefinition;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DefaultVersion implements IVersion {
	private final IVersionDefinition definition;
	private final String value;
	private final List<IVersioningMethodDefinition> versioningMethodDefinitions = new LinkedList<IVersioningMethodDefinition>();

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

	public List<IVersioningMethodDefinition> getVersioningMethodDefinitions() {
		return Collections.unmodifiableList(versioningMethodDefinitions);
	}

	public void addVersioningMethodDefinition(IVersioningMethodDefinition definition) {
		versioningMethodDefinitions.add(definition);
	}
}
