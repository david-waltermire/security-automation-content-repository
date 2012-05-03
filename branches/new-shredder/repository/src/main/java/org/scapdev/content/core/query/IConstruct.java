package org.scapdev.content.core.query;

public interface IConstruct<CONTEXT extends IContext<CONTEXT>> {
	<RESULT> RESULT visit(IQueryVisitor<RESULT, CONTEXT> visitor);
}
