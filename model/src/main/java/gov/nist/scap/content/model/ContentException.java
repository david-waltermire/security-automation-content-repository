package gov.nist.scap.content.model;

public class ContentException extends Exception {

	/** the serial version UID */
	private static final long serialVersionUID = 1L;

	public ContentException() {
	}

	public ContentException(String message) {
		super(message);
	}

	public ContentException(Throwable cause) {
		super(cause);
	}

	public ContentException(String message, Throwable cause) {
		super(message, cause);
	}
//
//	public ContentException(String message, Throwable cause,
//			boolean enableSuppression, boolean writableStackTrace) {
//		super(message, cause, enableSuppression, writableStackTrace);
//	}
}
