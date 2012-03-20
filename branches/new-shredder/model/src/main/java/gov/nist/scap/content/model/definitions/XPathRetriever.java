package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.IContainer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.xmlbeans.XmlCursor;

public class XPathRetriever implements IValueRetriever {
	private final String xpath;

	public XPathRetriever(String xpath) {
		if (xpath == null) {
			throw new NullPointerException("xpath");
		}
		this.xpath = xpath;
	}

	protected String getXpath() {
		return xpath;
	}

	public String getValue(IContainer<?> entity, XmlCursor cursor) {
		return getValue(cursor);
	}
	public List<String> getValues(IContainer<?> entity, XmlCursor cursor) {
		return getValues(cursor);
	}

	public String getValue(XmlCursor cursor) {
		cursor.push();

		String value = null;
		try {
			cursor.selectPath(getXpath());
			if (cursor.getSelectionCount() > 1) {
				throw new IllegalStateException("xpath resulted in multiple values");
			}
	
			if (cursor.hasNextSelection()) {
				cursor.toNextSelection();
				value = cursor.getTextValue();
			}
		} finally {
			cursor.pop();
		}
		return value;
	}

	public List<String> getValues(XmlCursor cursor) {
		cursor.push();

		List<String> values = new LinkedList<String>();
		try {
			cursor.selectPath(getXpath());
			while (cursor.hasNextSelection()) {
				cursor.toNextSelection();
				String value = cursor.getTextValue();
				values.add(value);
			}
		} finally {
			cursor.pop();
		}
		return Collections.unmodifiableList(values);
	}
}
