package org.scapdev.content.core.query.sparql;

import gov.nist.sparql.builder.IPrefix;
import gov.nist.sparql.builder.IVar;
import gov.nist.sparql.builder.SelectQueryBuilder;

import java.net.URI;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.scapdev.content.core.query.entity.EntityQuery;

public class EntityQueryParser {
	public static final String ENTITY_URI_VARIABLE_NAME = "entityURI";

	private EntityQueryParser() {
		// disable construction
	}

	public static TupleQuery parse(EntityQuery query, RepositoryConnection conn) throws RepositoryException, MalformedQueryException {
		SelectQueryBuilder builder = new SelectQueryBuilder();
		// Make results distinct
		builder.setOption(SelectQueryBuilder.Option.DISTINCT);
		
		// Define prefixes
		IPrefix model = builder.addPrefix(URI.create("http://scap.nist.gov/resource/content/model#"), "model");

		// Define variables
		IVar entityURI = builder.newVar(ENTITY_URI_VARIABLE_NAME);

		// Assign the select statement
		builder.setSelectVars(entityURI);

		QueryInfo info = new QueryInfo(builder, model);

		EntityConstructProcessor processor = new EntityConstructProcessor(info, builder.where(), entityURI);
		query.getConstruct().visit(processor);

		// Generate the query
		return builder.build(conn);
	}
}
