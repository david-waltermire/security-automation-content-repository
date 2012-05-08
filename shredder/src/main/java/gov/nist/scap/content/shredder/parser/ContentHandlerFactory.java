package gov.nist.scap.content.shredder.parser;

public class ContentHandlerFactory {

	private Class<? extends ContentHandler> clazz;
	
	public ContentHandlerFactory(Class<? extends ContentHandler> clazz) {
		this.clazz = clazz;
	}
	
	public ContentHandler newContentHandler() {
		try {
			@SuppressWarnings("cast")
			ContentHandler ch = (ContentHandler)clazz.newInstance();
			return ch;
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
