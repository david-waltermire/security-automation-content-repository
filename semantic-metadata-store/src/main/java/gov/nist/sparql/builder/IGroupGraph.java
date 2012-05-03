package gov.nist.sparql.builder;

import org.openrdf.model.ValueFactory;

public interface IGroupGraph {
	IGroupGraph addStatement(TripplesBlock...blocks);
	IGroupGraph addStatement(IGraphPatternNotTripples pattern);
	IGroupGraph addStatement(IGraphPatternNotTripples pattern, TripplesBlock...blocks);
	IGroupGraph addStatement(TripplesBlock block, IGraphPatternNotTripples pattern, TripplesBlock...blocks);
	void buildQueryString(ValueFactory vf, StringBuilder builder);
}
