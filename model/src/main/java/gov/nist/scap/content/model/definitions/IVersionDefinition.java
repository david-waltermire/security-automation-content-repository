package gov.nist.scap.content.model.definitions;

public interface IVersionDefinition {
	public enum Method {
		TEXT,
		DECIMAL,
		SERIAL;
	}

	Method getMethod();
	XPathRetriever getXpath();
	boolean isUseParentVersionWhenUndefined();
}
