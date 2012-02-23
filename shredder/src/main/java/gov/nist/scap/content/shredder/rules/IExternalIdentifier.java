package gov.nist.scap.content.shredder.rules;

import java.util.regex.Pattern;

public interface IExternalIdentifier {
	String getId();
	Pattern getPattern();
}
