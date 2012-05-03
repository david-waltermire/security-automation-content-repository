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
import org.scapdev.content.core.query.entity.Key;
import org.scapdev.content.core.query.relationship.Relationship;
import org.scapdev.content.core.query.relationship.RelationshipContext;
import org.scapdev.content.core.query.relationship.To;
import org.scapdev.content.core.query.relationship.ToBoundaryIdentifier;

public class RelationshipConstructProcessor extends
		AbstractConstructProcessor<RelationshipContext> {
	private final IVar entityVar;
	private final IVar relationshipVar;
	private final IVar relationshipEntityVar;

	public RelationshipConstructProcessor(QueryInfo queryInfo,
			GroupGraph groupGraph, IVar entityVar, IVar relationshipVar, IVar relationshipEntityVar) {
		super(queryInfo, groupGraph);
		this.entityVar = entityVar;
		this.relationshipVar = relationshipVar;
		this.relationshipEntityVar = relationshipEntityVar;
	}

	public IVar getEntityVar() {
		return entityVar;
	}

	public IVar getRelationshipVar() {
		return relationshipVar;
	}

	public IVar getRelationshipEntityVar() {
		return relationshipEntityVar;
	}

	@Override
	protected RelationshipConstructProcessor newChildConstructProcessor(
			GroupGraph groupGraph) {
		return new RelationshipConstructProcessor(getQueryInfo(), groupGraph, getEntityVar(), getRelationshipVar(), getRelationshipEntityVar());
	}

	@Override
	public RelationshipConstructProcessor visit(Type<?> type) {
		Tripple tripple = new Tripple(getEntityVar(), new Property(getQueryInfo().getSparqlBuilder().createURI(type.getType()), getRelationshipEntityVar()));
		TripplesBlock block = new TripplesBlock(tripple);
		getGroupGraph().addStatement(block);
		return this;
	}

	@Override
	public RelationshipConstructProcessor visit(ContentId contentId) {
		throw new UnsupportedOperationException("EntityContext is required.");
	}

	@Override
	public RelationshipConstructProcessor visit(Key key) {
		throw new UnsupportedOperationException("EntityContext is required.");
	}

	@Override
	public RelationshipConstructProcessor visit(Relationship key) {
		throw new UnsupportedOperationException("EntityContext is required.");
	}

	@Override
	public RelationshipConstructProcessor visit(To to) {

		// Process the entity construct
		EntityConstructProcessor processor = new EntityConstructProcessor(getQueryInfo(), getGroupGraph(), relationshipEntityVar);
		to.getConstruct().visit(processor);
		return this;
	}

	@Override
	public IConstructProcessor<RelationshipContext> visit(
			ToBoundaryIdentifier toBoundaryIdentifier) {
/*
[_:node16s5s0sdcx50] http://cce.mitre.org/5#CCE-13091-4 http://scap.nist.gov/resource/content/model#hasBoundaryObjectType http://cce.mitre.org/resource/content/cce#external-identifier-cce-5 
[_:node16s5s0sdcx50] http://cce.mitre.org/5#CCE-13091-4 http://scap.nist.gov/resource/content/model#hasBoundaryObjectValue CCE-13091-4 
[_:node16s5s0sdcx50] http://scap.nist.gov/resource/content/individuals#eea5e8a2.xml http://scap.nist.gov/resource/content/xccdf/1.2#relationship-selectable-item-ident http://cce.mitre.org/5#CCE-13091-4 
*/
		
		IPrefix modelPrefix = getQueryInfo().getModelPrefix();
		AbstractSparqlBuilder builder = getQueryInfo().getSparqlBuilder();

		// Map relationship to boundary identifier
		Tripple tripple = new Tripple(getRelationshipEntityVar(),
				new Property(modelPrefix.resource("hasBoundaryObjectType"), builder.createURI(toBoundaryIdentifier.getType())),
				new Property(modelPrefix.resource("hasBoundaryObjectValue"), builder.createLiteral(toBoundaryIdentifier.getId())));
		TripplesBlock block = new TripplesBlock(tripple);
		getGroupGraph().addStatement(block);
		return this;
	}

}
