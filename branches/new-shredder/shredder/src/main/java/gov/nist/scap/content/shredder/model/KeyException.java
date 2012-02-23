package gov.nist.scap.content.shredder.model;


public class KeyException extends ContentException {

	/** the serial version UID */
	private static final long serialVersionUID = 1L;

	public KeyException() {
	}

	public KeyException(String message) {
		super(message);
	}

	public KeyException(Throwable cause) {
		super(cause);
	}

	public KeyException(String message, Throwable cause) {
		super(message, cause);
	}
//
//	public KeyException(String message, Throwable cause,
//			boolean enableSuppression, boolean writableStackTrace) {
//		super(message, cause, enableSuppression, writableStackTrace);
//	}
}
