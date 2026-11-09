package thales.spring.angular.demo.exception;

/**
 * Exception: the Client does not exist. Not found
 *
 */
public final class ClientNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -3533703346229194048L;

	public ClientNotFoundException() {
        super();
    }

    public ClientNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ClientNotFoundException(final String message) {
        super(message);
    }

    public ClientNotFoundException(final Throwable cause) {
        super(cause);
    }
}
