package gov.nist.scap.content.shredder.parser;

import gov.nist.scap.content.model.IContentHandle;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.XmlBookmark;

class BookmarkContentHandle implements IContentHandle {
	private final XmlCursor.XmlBookmark bookmark;

	public BookmarkContentHandle(XmlCursor.XmlBookmark bookmark) {
		this.bookmark = bookmark;
	}

	public XmlCursor getCursor() {
		return bookmark.createCursor();
	}

	public XmlBookmark getBookmark() {
		return bookmark;
	}
}