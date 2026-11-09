package thales.spring.angular.demo.exception;

/**
 * Exception : the Account already exists
 *
 */
public final class AccountAlreadyExistException extends RuntimeException {

	private static final long serialVersionUID = 490835231255414152L;

	public AccountAlreadyExistException() {
        super();
    }

    public AccountAlreadyExistException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AccountAlreadyExistException(final String message) {
        super(message);
    }

    public AccountAlreadyExistException(final Throwable cause) {
        super(cause);
    }

}
