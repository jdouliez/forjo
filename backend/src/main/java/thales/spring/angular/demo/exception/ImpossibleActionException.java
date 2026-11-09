package thales.spring.angular.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import thales.spring.angular.demo.util.I18n;

@ResponseStatus(HttpStatus.NOT_FOUND)
/**
 * Exception: impossible, unplanned action
 * If launched by an EntryPoint, the status of the response will be 404 NOT FOUND
 *
 */
public class ImpossibleActionException extends RuntimeException {
	private static final long serialVersionUID = 6167091272950188556L;

	public ImpossibleActionException() {
		super(I18n.getMessage("exception.impossibleAction"));
	}
	public ImpossibleActionException(String message) {
		super(message);
	}

	public ImpossibleActionException(String message, Throwable cause) {
		super(message, cause);
	}
}
