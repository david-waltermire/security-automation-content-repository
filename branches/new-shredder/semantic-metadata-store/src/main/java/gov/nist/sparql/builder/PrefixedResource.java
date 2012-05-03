package gov.nist.sparql.builder;

import org.openrdf.model.ValueFactory;



class PrefixedResource implements IResource {
	private final IPrefix prefix;
	private final String name;

	public PrefixedResource(IPrefix prefix, String name) {
		this.prefix = prefix;
		this.name = name;
	}

	@Override
	public String getValue(ValueFactory vf) {
		return new StringBuilder().append(prefix.getLabel()).append(":").append(name).toString();
	}
}
