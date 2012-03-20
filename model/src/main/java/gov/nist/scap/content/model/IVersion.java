package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IVersionDefinition;
import gov.nist.scap.content.model.definitions.IVersioningMethodDefinition;

import java.util.List;

public interface IVersion {
	IVersionDefinition getDefinition();
	List<? extends IVersioningMethodDefinition> getVersioningMethodDefinitions();
	String getValue();
	double getValueAsDouble();
	int getValueAsInt();
}
