package gov.nist.scap.content.model.definitions;

import java.util.regex.Pattern;

public interface IExternalIdentifier {
	String getId();
	Pattern getPattern();
}
