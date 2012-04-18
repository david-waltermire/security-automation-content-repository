package org.scapdev.content.core.query.entity;

public class ContentId implements IEntityConstruct {
	public static ContentId contentId(String id) {
		return new ContentId(id);
	}

	private final String contentId;

	public ContentId(String id) {
		this.contentId = id;
	}

	public String getContentId() {
		return contentId;
	}
}
