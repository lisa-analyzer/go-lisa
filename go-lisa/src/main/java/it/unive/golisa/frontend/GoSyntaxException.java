package it.unive.golisa.frontend;

/**
 * An exception thrown due to an inconsistency in an Go file.
 */
public class GoSyntaxException extends RuntimeException {

	private static final long serialVersionUID = 4950907533241427847L;

	/**
	 * Builds the exception.
	 */
	public GoSyntaxException() {
		super();
	}

	/**
	 * Builds the exception.
	 * 
	 * @param message the message of this exception
	 * @param cause   the inner cause of this exception
	 */
	public GoSyntaxException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Builds the exception.
	 * 
	 * @param message the message of this exception
	 */
	public GoSyntaxException(String message) {
		super(message);
	}

	/**
	 * Builds the exception.
	 * 
	 * @param cause the inner cause of this exception
	 */
	public GoSyntaxException(Throwable cause) {
		super(cause);
	}
}