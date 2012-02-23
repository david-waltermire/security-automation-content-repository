package gov.nist.scap.content.shredder.rules;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.IEntity;
import gov.nist.scap.content.shredder.model.KeyException;

import org.apache.xmlbeans.XmlCursor;

public abstract class AbstractKeyedField implements IKeyedField {
	private final String name;
	private Pattern pattern;

	public AbstractKeyedField(String name) {
		this.name = name;
	}

	protected abstract String retrieveValue(IEntity<?> parentContext, XmlCursor cursor) throws KeyException;

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

	public String getValue(IEntity<?> parentContext, XmlCursor cursor)
			throws ContentException {
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