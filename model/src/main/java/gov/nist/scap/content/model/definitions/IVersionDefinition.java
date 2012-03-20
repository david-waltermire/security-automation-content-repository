package gov.nist.scap.content.model.definitions;

public interface IVersionDefinition {
	IVersioningMethodDefinition getMethod();
	XPathRetriever getXpath();
	boolean isUseParentVersionWhenUndefined();
}
