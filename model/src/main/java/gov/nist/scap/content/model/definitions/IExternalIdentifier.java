package gov.nist.scap.content.model.definitions;

import java.util.regex.Pattern;

public interface IExternalIdentifier extends IDefinition {
	Pattern getPattern();
	String getNamespace();
}
