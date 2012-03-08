package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.ContentException;
import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.KeyException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.xmlbeans.XmlCursor;

public abstract class AbstractKeyedField implements IKeyedField {
	private final String name;
	private Pattern pattern;

	public AbstractKeyedField(String name) {
		this.name = name;
	}

	protected abstract String retrieveValue(IContainer<?> parentContext, XmlCursor cursor) throws KeyException;

	public String getName() {
		return name;
	}

	public final String getPattern() {
		return (pattern == null ? null : pattern.pattern());
	}

	/**
	 * 
	 * @param regex
	 * @throws PatternSyntaxException if the regex argument is invalid
	 */
	public final void setPattern(String regex) {
		this.pattern = (regex == null ? null : Pattern.compile(regex));
	}

	public String getValue(IContainer<?> parentContext, XmlCursor cursor)
			throws KeyException, ContentException {
		String value = retrieveValue(parentContext, cursor);
		if (pattern != null) {
			Matcher matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				throw new ContentException("the specified pattern '"+pattern.pattern()+"' did not match the retrieved value: "+value);
			}
			value = matcher.group(1);
		}
		return value;
	}

}
