package gov.nist.sparql.builder;

import org.openrdf.model.ValueFactory;

public class StringLiteral implements ILiteral {
	private final String value;

	public StringLiteral(String value) {
		this.value = value;
	}

	@Override
	public String getValue(ValueFactory vf) {
		return new StringBuilder().append('"').append(vf.createLiteral(value).stringValue()).append('"').toString();
	}

}
