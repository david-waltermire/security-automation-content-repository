package gov.nist.scap.content.definitions;

import java.util.regex.Pattern;

public interface IExternalIdentifier {
	String getId();
	Pattern getPattern();
}
