package gov.nist.scap.content.model.definitions;

public interface IVersionDefinition {
	public enum Method {
		TEXT,
		DECIMAL,
		SERIAL;

		public String getId() {
			return "http://change.me/#"+this.name();
		}
	}

	Method getMethod();
	XPathRetriever getXpath();
	boolean isUseParentVersionWhenUndefined();
}
