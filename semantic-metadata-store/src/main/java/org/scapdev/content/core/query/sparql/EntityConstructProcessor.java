package org.scapdev.content.core.query.sparql;

import gov.nist.sparql.builder.AbstractSparqlBuilder;
import gov.nist.sparql.builder.GroupGraph;
import gov.nist.sparql.builder.IPrefix;
import gov.nist.sparql.builder.IVar;
import gov.nist.sparql.builder.Property;
import gov.nist.sparql.builder.Tripple;
import gov.nist.sparql.builder.TripplesBlock;

import org.scapdev.content.core.query.Type;
import org.scapdev.content.core.query.entity.ContentId;
import org.scapdev.content.core.query.entity.EntityContext;
import org.scapdev.content.core.query.entity.Key;
import org.scapdev.content.core.query.entity.Version;
import org.scapdev.content.core.query.entity.Key.Field;
import org.scapdev.content.core.query.relationship.Relationship;
import org.scapdev.content.core.query.relationship.To;
import org.scapdev.content.core.query.relationship.ToBoundaryIdentifier;

public class EntityConstructProcessor extends AbstractConstructProcessor<EntityContext> {
	private final IVar entityUriVar;

	public EntityConstructProcessor(QueryInfo queryInfo, GroupGraph groupGraph, IVar entityUriVar) {
		super(queryInfo, groupGraph);
		this.entityUriVar = entityUriVar;
	}

	
	public IVar getEntityUriVar() {
		return entityUriVar;
	}

	@Override
	protected EntityConstructProcessor newChildConstructProcessor(
			GroupGraph groupGraph) {
		return new EntityConstructProcessor(getQueryInfo(), groupGraph, getEntityUriVar());
	}


	@Override
	public EntityConstructProcessor visit(Type<?> type) {
		IPrefix modelPrefix = getQueryInfo().getModelPrefix();
		AbstractSparqlBuilder builder = getQueryInfo().getSparqlBuilder();

		Property property = new Property(modelPrefix.resource("hasEntityType"), builder.createURI(type.getType()));
		Tripple tripple = new Tripple(getEntityUriVar(), property);
		TripplesBlock block = new TripplesBlock(tripple);
		getGroupGraph().addStatement(block);
		return this;
	}

	/*
	[_:node174j1hg4cx3619] http://scap.nist.gov/resource/content/individuals#5f902c18.xml http://scap.nist.gov/resource/content/model#hasVersion node174j1hg4cx3620
	[_:node174j1hg4cx3619] node174j1hg4cx3620 http://scap.nist.gov/resource/content/model#hasVersionType http://scap.nist.gov/resource/content/cms#versioning-method-serial
	[_:node174j1hg4cx3619] node174j1hg4cx3620 http://scap.nist.gov/resource/content/model#hasVersionValue 2
	*/

	@Override
	public IConstructProcessor<EntityContext> visit(Version version) {
		IPrefix modelPrefix = getQueryInfo().getModelPrefix();
		AbstractSparqlBuilder builder = getQueryInfo().getSparqlBuilder();
		IVar entityVar = getEntityUriVar();
		IVar versionVar = builder.newVarUsingBaseName("version");

		// Map entity to key
		Tripple entityTripple = new Tripple(entityVar, new Property(modelPrefix.resource("hasVersion"), versionVar));

		// Find the version
		Tripple versionTripple = new Tripple(versionVar, new Property(modelPrefix.resource("hasVersionValue"), builder.createLiteral(version.getVersion())));

		TripplesBlock block = new TripplesBlock(entityTripple, versionTripple);

		getGroupGraph().addStatement(block);
		return this;
	}
	
	@Override
	public EntityConstructProcessor visit(ContentId contentId) {
		IPrefix modelPrefix = getQueryInfo().getModelPrefix();
		AbstractSparqlBuilder builder = getQueryInfo().getSparqlBuilder();

		Property property = new Property(modelPrefix.resource("hasContentId"), builder.createLiteral(contentId.getContentId()));
		Tripple tripple = new Tripple(getEntityUriVar(), property);
		TripplesBlock block = new TripplesBlock(tripple);
		getGroupGraph().addStatement(block);
		return this;
	}

	@Override
	public EntityConstructProcessor visit(Key key) {
/*
[_:node16s4ssogux102] http://scap.nist.gov/resource/content/individuals#306ab70.xml http://scap.nist.gov/resource/content/model#hasKey node16s4ssogux104 
[_:node16s4ssogux102] node16s4ssogux104 http://scap.nist.gov/resource/content/model#hasKeyType http://oval.mitre.org/resource/content/definition/5#key-test 
[_:node16s4ssogux102] node16s4ssogux104 http://scap.nist.gov/resource/content/model#hasFieldData node16s4ssogux105 
[_:node16s4ssogux102] node16s4ssogux105 http://scap.nist.gov/resource/content/model#hasFieldName test-id 
[_:node16s4ssogux102] node16s4ssogux105 http://scap.nist.gov/resource/content/model#hasFieldValue oval:org.mitre.oval:tst:10792 
*/
		IPrefix modelPrefix = getQueryInfo().getModelPrefix();
		AbstractSparqlBuilder builder = getQueryInfo().getSparqlBuilder();
		IVar entityVar = getEntityUriVar();
		IVar keyVar = builder.newVarUsingBaseName("key");

		// Map entity to key
		Tripple entityTripple = new Tripple(entityVar, new Property(modelPrefix.resource("hasKey"), keyVar));

		// Find they key
		Tripple keyTripple = new Tripple(keyVar, new Property(modelPrefix.resource("hasKeyType"), builder.createURI(key.getType())));

		TripplesBlock block = new TripplesBlock(entityTripple, keyTripple);

		for (Field field : key.getFields()) {
			IVar fieldVar = builder.newVarUsingBaseName("field");
			keyTripple.addProperty(new Property(modelPrefix.resource("hasFieldData"), fieldVar));

			Tripple fieldTripple = new Tripple(fieldVar,
					new Property(modelPrefix.resource("hasFieldName"), builder.createLiteral(field.getName())),
					new Property(modelPrefix.resource("hasFieldValue"), builder.createLiteral(field.getValue())));
			block.addTripple(fieldTripple);
		}
		getGroupGraph().addStatement(block);
		return this;
	}

	@Override
	public EntityConstructProcessor visit(Relationship relationship) {
		AbstractSparqlBuilder builder = getQueryInfo().getSparqlBuilder();
		IVar entityVar = getEntityUriVar();

		// Generate a variable for the relationship
		IVar relationshipVar = builder.newVarUsingBaseName("relationship");
		IVar relationshipEntityVar = builder.newVarUsingBaseName("entity");

		// Match all relationships assuming that the nested criteria will select
		// the specific one
		// TODO: once we have inference and the rules in the tripple store, constrain this to relationships only
		Tripple relationshipTripple = new Tripple(entityVar, new Property(relationshipVar, relationshipEntityVar));

		TripplesBlock block = new TripplesBlock(relationshipTripple);
		getGroupGraph().addStatement(block);

		// Process the relationship construct
		RelationshipConstructProcessor processor = new RelationshipConstructProcessor(getQueryInfo(), getGroupGraph(), entityVar, relationshipVar, relationshipEntityVar);
		relationship.getConstruct().visit(processor);
		return this;
	}

	@Override
	public EntityConstructProcessor visit(To key) {
		throw new UnsupportedOperationException("RelationshipContext is required.");
	}


	@Override
	public IConstructProcessor<EntityContext> visit(
			ToBoundaryIdentifier toBoundaryIdentifier) {
		throw new UnsupportedOperationException("RelationshipContext is required.");
	}
}
