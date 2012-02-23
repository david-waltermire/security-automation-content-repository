package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IEntityDefinition;

import java.util.LinkedList;
import java.util.List;

import org.apache.xmlbeans.XmlCursor;


public abstract class AbstractEntity<DEFINITION extends IEntityDefinition> implements IMutableEntity<DEFINITION> {
	private final DEFINITION contentDefinition;
	private final IContainer<?> parentContext;
	private final Bookmark bookmark;
	private final List<IRelationship<?>> relationships = new LinkedList<IRelationship<?>>();

	public AbstractEntity(XmlCursor cursor,
			DEFINITION contentDefinition,
			IContainer<?> parentContext) throws ContentException {
		this.contentDefinition = contentDefinition;
		this.parentContext = parentContext;
		this.bookmark = new Bookmark();
		cursor.setBookmark(bookmark);
	}

	public DEFINITION getDefinition() {
		return contentDefinition;
	}

	public IContainer<?> getParentContext() {
		return parentContext;
	}

	public Bookmark getBookmark() {
		return bookmark;
	}

	public XmlCursor getCursor() {
		return (bookmark != null ? bookmark.createCursor() : null);
	}

	public List<IRelationship<?>> getRelationships() {
		return relationships;
	}

	public IKey getKey(String keyId) {
		return (getParentContext() == null ? null : getParentContext().getKey(keyId));
	}

	public void appendRelationship(IRelationship<?> relationship) {
		relationships.add(relationship);
	}
}
