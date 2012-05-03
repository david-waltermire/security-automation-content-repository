package org.scapdev.content.core.query;


public interface IContext<CONTEXT extends IContext<CONTEXT>> {
	public IConstruct<? extends CONTEXT> getConstruct();
}
