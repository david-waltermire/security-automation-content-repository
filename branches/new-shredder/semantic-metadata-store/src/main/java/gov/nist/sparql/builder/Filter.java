package gov.nist.sparql.builder;

import java.net.URI;

import org.openrdf.model.ValueFactory;

public class Filter implements IGraphPatternNotTripples {

	public static Filter filter(IVar var, URI uri) {
		return new Filter(var, uri);
	}

	private IVar var;
	private URI uri;
	
	public Filter(IVar var, URI uri) {
		this.var = var;
		this.uri = uri;
	}

	@Override
	public void buildQueryString(ValueFactory vf, StringBuilder builder) {

		builder.append(" FILTER (" + var.getValue(vf) + " = <" +uri.toString() + ">) ");
	}

}
