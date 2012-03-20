package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.IContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.xmlbeans.XmlCursor;

public class AbstractPatternExtractingValueProcessor {
	private final IValueRetriever retriever;
	private Pattern pattern;

	public AbstractPatternExtractingValueProcessor(IValueRetriever retriever) {
		this.retriever = retriever;
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

	public String getValue(IContainer<?> parentContext, XmlCursor cursor) throws ProcessingException {
		String value;
		try {
			value = retriever.getValue(parentContext, cursor);
		} catch (Exception e) {
			throw new ProcessingException("unable to retrieve value", e);
		}

		if (value != null && pattern != null) {
			Matcher matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				throw new ProcessingException("the specified pattern '"+pattern.pattern()+"' did not match the retrieved value: "+value);
			}
			value = matcher.group(1);
		}
		return value;
	}


	public List<String> getValues(IContainer<?> parentContext, XmlCursor cursor) throws ProcessingException {
		List<String> values;
		try {
			values = retriever.getValues(parentContext, cursor);
		} catch (Exception e) {
			throw new ProcessingException("unable to retrieve value", e);
		}

		List<String> retval = values;
		if (values != null && pattern != null) {
			if (values.isEmpty()) {
				retval = Collections.emptyList();
			} else {
				retval = new ArrayList<String>(values.size());
				for (String value : values) {
					Matcher matcher = pattern.matcher(value);
					if (!matcher.matches()) {
						throw new ProcessingException("the specified pattern '"+pattern.pattern()+"' did not match the retrieved value: "+values);
					}
					retval.add(matcher.group(1));
				}
			}
		}
		return retval;
	}
}
