package gov.nist.sparql.builder;

import org.openrdf.model.ValueFactory;

public class Var implements IVar {

	public static Var var(String name) {
		return new Var(name);
	}

	private final String name;

	public Var(String name) {
		this.name = name;
	}

	@Override
	public String getValue(ValueFactory vf) {
		return "?"+name;
	}

}
