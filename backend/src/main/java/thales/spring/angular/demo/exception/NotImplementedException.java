package thales.spring.angular.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import thales.spring.angular.demo.util.I18n;

@ResponseStatus(HttpStatus.NOT_FOUND)
/**
 * Exception: function not implemented
 * If launched by an EntryPoint, the status of the response will be 404 NOT FOUND
 *
 */
public class NotImplementedException extends RuntimeException {

	private static final long serialVersionUID = 6167091272950188556L;

	public NotImplementedException() {
		super(I18n.getMessage("exception.notImplemented"));
	}
	public NotImplementedException(String message) {
		super(message);
	}

	public NotImplementedException(String message, Throwable cause) {
		super(message, cause);
	}
}
