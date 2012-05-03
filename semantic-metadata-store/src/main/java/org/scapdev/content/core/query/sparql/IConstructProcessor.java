package org.scapdev.content.core.query.sparql;

import org.scapdev.content.core.query.IContext;
import org.scapdev.content.core.query.IQueryVisitor;

public interface IConstructProcessor<CONTEXT extends IContext<CONTEXT>> extends IQueryVisitor<IConstructProcessor<CONTEXT>, CONTEXT> {

}
