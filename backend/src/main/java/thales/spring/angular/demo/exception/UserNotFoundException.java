package thales.spring.angular.demo.exception;

/**
 * Exception : The user does not exist. Not Found
 *
 */
public final class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -3533703346229194048L;

	public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(final String message) {
        super(message);
    }

    public UserNotFoundException(final Throwable cause) {
        super(cause);
    }
}
