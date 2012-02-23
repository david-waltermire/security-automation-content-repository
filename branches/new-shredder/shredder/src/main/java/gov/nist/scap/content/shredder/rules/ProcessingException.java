package gov.nist.scap.content.shredder.rules;

public class ProcessingException extends Exception {

	/** the serial version UID */
	private static final long serialVersionUID = 1L;

	public ProcessingException() {
	}

	public ProcessingException(String message) {
		super(message);
	}

	public ProcessingException(Throwable cause) {
		super(cause);
	}

	public ProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
//
//	public ProcessingException(String message, Throwable cause,
//			boolean enableSuppression, boolean writableStackTrace) {
//		super(message, cause, enableSuppression, writableStackTrace);
//	}
}
