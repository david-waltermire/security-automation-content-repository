package gov.nist.sparql.builder;

import org.openrdf.model.ValueFactory;

public interface IGraphPatternNotTripples {

	void buildQueryString(ValueFactory vf,
			StringBuilder builder);

}
