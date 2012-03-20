package gov.nist.scap.content.model.definitions;


public class DefaultKeyedField extends AbstractPatternExtractingValueProcessor implements IKeyedField {
	private final String name;

	public DefaultKeyedField(String name, IValueRetriever retriever) {
		super(retriever);
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
