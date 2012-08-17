package org.scapdev.content.core.query.entity;

import org.scapdev.content.core.query.IQueryVisitor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Version implements IEntityConstruct {
	public static Version version(String version) {
		return new Version(version);
	}

	private final String version;

	@JsonCreator
	public Version(@JsonProperty("version") String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	@Override
	public <RESULT> RESULT visit(IQueryVisitor<RESULT, EntityContext> visitor) {
		return visitor.visit(this);
	}
}
