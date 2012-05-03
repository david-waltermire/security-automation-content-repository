package org.scapdev.content.core.query;

import java.util.List;

public interface IConditional<CONTEXT extends IContext<CONTEXT>> extends IConstruct<CONTEXT> {
	public enum Form {
		CONJUNCTIVE, // AND
		DISJUNCTIVE; // OR
	}
	Form getForm();
	List<IConstruct<CONTEXT>> getConstructs();
}
