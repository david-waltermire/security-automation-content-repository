package org.scapdev.content.model;

public class NullFieldValueException extends KeyException {

	/** the serial version UID */
	private static final long serialVersionUID = 1L;

	protected NullFieldValueException() {
	}

	protected NullFieldValueException(String message, Throwable cause) {
		super(message, cause);
	}

	protected NullFieldValueException(String message) {
		super(message);
	}

	protected NullFieldValueException(Throwable cause) {
		super(cause);
	}
}
