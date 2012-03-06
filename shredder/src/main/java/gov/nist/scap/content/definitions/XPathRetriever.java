package gov.nist.scap.content.definitions;

import org.apache.xmlbeans.XmlCursor;

public class XPathRetriever {
	private final String xpath;

	public XPathRetriever(String xpath) {
		this.xpath = xpath;
	}

	protected String getXpath() {
		return xpath;
	}

	public String getValue(XmlCursor cursor)  {

		cursor.push();

		String value = null;
		try {
			cursor.selectPath(getXpath());
	
			if (cursor.hasNextSelection()) {
				cursor.toNextSelection();
				value = cursor.getTextValue();
			}
		} finally {
			cursor.pop();
		}
		return value;
	}
}
