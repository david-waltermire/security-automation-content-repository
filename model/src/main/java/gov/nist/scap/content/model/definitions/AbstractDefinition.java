package gov.nist.scap.content.model.definitions;

public abstract class AbstractDefinition implements IDefinition {
	private final String id;

	public AbstractDefinition(String id) {
		if (id == null) {
			throw new NullPointerException("id");
		} else if (id.isEmpty()) {
			throw new IllegalArgumentException("id must not be empty");
		}
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
}
