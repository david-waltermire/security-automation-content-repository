package gov.nist.scap.content.model;


public class KeyException extends Exception {

	/** the serial version UID */
	private static final long serialVersionUID = 1L;

	public KeyException(String message) {
		super(message);
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