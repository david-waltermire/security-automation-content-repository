package org.scapdev.content.core.query.sparql;

import gov.nist.sparql.builder.AbstractSparqlBuilder;
import gov.nist.sparql.builder.IPrefix;

public class QueryInfo {
//	private static final Map<Class<IConstruct<?>>, IConstructProcessor> constructProcessorMap;
//
//	static {
//		constructProcessorMap.put(IConditional.class, new DefaultConstructProcessor());
//	}
	private final AbstractSparqlBuilder sparqlBuilder;
	private final IPrefix modelPrefix;
	private final IPrefix rdfPrefix;

	public QueryInfo(AbstractSparqlBuilder sparqlBuilder, IPrefix modelPrefix, IPrefix rdfPrefix) {
		this.sparqlBuilder = sparqlBuilder;
		this.modelPrefix = modelPrefix;
		this.rdfPrefix = rdfPrefix;
	}

	public AbstractSparqlBuilder getSparqlBuilder() {
		return sparqlBuilder;
	}

	public IPrefix getModelPrefix() {
		return modelPrefix;
	}

	public IPrefix getRdfPrefix() {
		return rdfPrefix;
	}
	
}
