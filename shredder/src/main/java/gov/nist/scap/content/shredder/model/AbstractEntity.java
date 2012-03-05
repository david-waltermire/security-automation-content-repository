package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IEntityDefinition;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.XmlBookmark;


public abstract class AbstractEntity<DEFINITION extends IEntityDefinition> implements IMutableEntity<DEFINITION> {
	private final DEFINITION contentDefinition;
	private final IContainer<?> parentContext;
	private final Bookmark bookmark;
	private final List<IRelationship<?>> relationships = new LinkedList<IRelationship<?>>();
	private final List<IKeyedRelationship> keyedRelationships = new LinkedList<IKeyedRelationship>();
	private final List<IIndirectRelationship> indirectRelationships = new LinkedList<IIndirectRelationship>();

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
		return Collections.unmodifiableList(relationships);
	}

	public List<IKeyedRelationship> getKeyedRelationships() {
		return Collections.unmodifiableList(keyedRelationships);
	}

	public List<IIndirectRelationship> getIndirectRelationships() {
		return Collections.unmodifiableList(indirectRelationships);
	}

	public IKey getKey(String keyId) {
		return (getParentContext() == null ? null : getParentContext().getKey(keyId));
	}

	public void appendRelationship(IKeyedRelationship relationship) {
		keyedRelationships.add(relationship);
		appendRelationship((IRelationship<?>)relationship);
	}

	public void appendRelationship(IIndirectRelationship relationship) {
		indirectRelationships.add(relationship);
		appendRelationship((IRelationship<?>)relationship);
	}

	public void appendRelationship(IRelationship<?> relationship) {
		relationships.add(relationship);
	}

	@Override
	public IContentHandle getContentHandle() {
		return new ContentHandle();
	}

	private class ContentHandle implements IContentHandle {
		public XmlCursor getCursor() {
			return bookmark.createCursor();
		}

		public XmlBookmark getBookmark() {
			return bookmark;
		}
		
	}
}
