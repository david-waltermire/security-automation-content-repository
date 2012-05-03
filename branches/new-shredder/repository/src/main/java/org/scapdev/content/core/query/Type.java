package org.scapdev.content.core.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;



public class Type<CONTEXT extends IContext<CONTEXT>> implements IConstruct<CONTEXT> {
	private final String type;

	@JsonCreator
	public Type(@JsonProperty("type") String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	@Override
	public <RESULT> RESULT visit(IQueryVisitor<RESULT, CONTEXT> visitor) {
		return visitor.visit(this);
	}
}
