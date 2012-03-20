package gov.nist.scap.content.model.definitions;


public class DefaultPropertyRef extends AbstractPatternExtractingValueProcessor implements IPropertyRef {
	private final IPropertyDefinition propertyDefinition;

	public DefaultPropertyRef(
			IPropertyDefinition propertyDefinition,
			IValueRetriever retriever) {
		super(retriever);
		
		this.propertyDefinition = propertyDefinition;
	}

	public IPropertyDefinition getPropertyDefinition() {
		return propertyDefinition;
	}
}
