package thales.spring.angular.demo.remediation.exception;

public class OwaspException extends RuntimeException {

	private static final long serialVersionUID = -1742791485194469435L;
	
	public OwaspException() {
        super();
    }

    public OwaspException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public OwaspException(final String message) {
        super(message);
    }

    public OwaspException(final Throwable cause) {
        super(cause);
    }

}
