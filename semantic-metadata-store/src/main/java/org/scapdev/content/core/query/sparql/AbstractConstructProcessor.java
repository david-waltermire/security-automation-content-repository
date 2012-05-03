package org.scapdev.content.core.query.sparql;

import gov.nist.sparql.builder.GroupGraph;
import gov.nist.sparql.builder.Union;

import java.util.List;

import org.scapdev.content.core.query.IConditional;
import org.scapdev.content.core.query.IConstruct;
import org.scapdev.content.core.query.IContext;

public abstract class AbstractConstructProcessor<CONTEXT extends IContext<CONTEXT>> implements IConstructProcessor<CONTEXT> {
	private final QueryInfo queryInfo;
	private final GroupGraph groupGraph;

	public AbstractConstructProcessor(QueryInfo queryInfo,
			GroupGraph groupGraph) {
		this.queryInfo = queryInfo;
		this.groupGraph = groupGraph;
	}

	protected abstract AbstractConstructProcessor<CONTEXT> newChildConstructProcessor(
			GroupGraph groupGraph);

	public QueryInfo getQueryInfo() {
		return queryInfo;
	}

	public GroupGraph getGroupGraph() {
		return groupGraph;
	}

	@Override
	public IConstructProcessor<CONTEXT> visit(IConditional<CONTEXT> conditional) {
		switch (conditional.getForm()) {
		case CONJUNCTIVE:
			processAnd(conditional.getConstructs());
			break;
		case DISJUNCTIVE:
			processOr(conditional.getConstructs());
			break;
		}
		return this;
	}

	private void processOr(List<IConstruct<CONTEXT>> constructs) {
		Union union = new Union();
		for (IConstruct<CONTEXT> construct : constructs) {
			GroupGraph unionGroupGraph = new GroupGraph();
			union.addGroupGraph(unionGroupGraph);

			AbstractConstructProcessor<CONTEXT> childProcessor = newChildConstructProcessor(unionGroupGraph);
			construct.visit(childProcessor);
		}
		groupGraph.addStatement(union);
	}

	private void processAnd(List<IConstruct<CONTEXT>> constructs) {
		
		for (IConstruct<CONTEXT> construct : constructs) {
			construct.visit(this);
		}
	}
}
