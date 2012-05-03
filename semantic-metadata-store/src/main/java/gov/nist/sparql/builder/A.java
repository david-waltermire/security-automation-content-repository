package gov.nist.sparql.builder;

import org.openrdf.model.ValueFactory;

public class A implements IVerb {
	public static final A A = new A();

	public static A a() {
		return A;
	}

	@Override
	public String getValue(ValueFactory vf) {
		return "a";
	}
}
