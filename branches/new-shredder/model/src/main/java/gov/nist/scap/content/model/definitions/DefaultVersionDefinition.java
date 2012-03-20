package gov.nist.scap.content.model.definitions;

import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlException;


public class DefaultVersionDefinition implements IVersionDefinition {

	private final IVersioningMethodDefinition method;
	private final XPathRetriever xpath;
	private final boolean useParentVersionWhenUndefined;

	public DefaultVersionDefinition(IVersioningMethodDefinition method, String xpath, boolean useParentVersionWhenUndefined) throws XmlException {
		this.method = method;
		this.xpath = new XPathRetriever(XmlBeans.compilePath(xpath));
		this.useParentVersionWhenUndefined = useParentVersionWhenUndefined;
	}

	public IVersioningMethodDefinition getMethod() {
		return method;
	}

	public XPathRetriever getXpath() {
		return xpath;
	}

	public boolean isUseParentVersionWhenUndefined() {
		return useParentVersionWhenUndefined;
	}
}
