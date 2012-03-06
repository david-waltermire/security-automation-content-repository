package gov.nist.scap.content.model;


public class SimpleKeyEntry implements IKeyEntry {
	private final String value;

	public SimpleKeyEntry(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
