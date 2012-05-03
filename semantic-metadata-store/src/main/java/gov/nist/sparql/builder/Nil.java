package gov.nist.sparql.builder;

import org.openrdf.model.ValueFactory;

public class Nil implements IGraphTerm {
	public static Nil nil() {
		return new Nil();
	}

	@Override
	public String getValue(ValueFactory vf) {
		return "NIL";
	}
}
