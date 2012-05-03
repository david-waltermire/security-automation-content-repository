package org.scapdev.content.core.query.relationship;

import org.scapdev.content.core.query.IQueryVisitor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ToBoundaryIdentifier implements IRelationshipConstruct {

	public static ToBoundaryIdentifier toBoundaryIdentifier(String type, String id) {
		return new ToBoundaryIdentifier(type, id);
	}

	private final String type;
	private final String id;

	@JsonCreator
	public ToBoundaryIdentifier(@JsonProperty("type") String type, @JsonProperty("id") String id) {
		this.type = type;
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public <RESULT> RESULT visit(IQueryVisitor<RESULT, RelationshipContext> visitor) {
		return visitor.visit(this);
	}

}
