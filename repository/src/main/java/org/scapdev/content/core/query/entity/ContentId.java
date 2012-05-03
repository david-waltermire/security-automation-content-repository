package org.scapdev.content.core.query.entity;

import org.scapdev.content.core.query.IQueryVisitor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ContentId implements IEntityConstruct {
	public static ContentId contentId(String id) {
		return new ContentId(id);
	}

	private final String contentId;

	@JsonCreator
	public ContentId(@JsonProperty("contentId") String id) {
		this.contentId = id;
	}

	public String getContentId() {
		return contentId;
	}

	@Override
	public <RESULT> RESULT visit(IQueryVisitor<RESULT, EntityContext> visitor) {
		return visitor.visit(this);
	}
}
