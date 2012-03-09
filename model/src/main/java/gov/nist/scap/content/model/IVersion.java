package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IVersionDefinition;

public interface IVersion {
	IVersionDefinition getDefinition();
	String getValue();
	double getValueAsDouble();
	int getValueAsInt();
}
