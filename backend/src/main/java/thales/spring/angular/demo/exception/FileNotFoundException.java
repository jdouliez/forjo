package thales.spring.angular.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
/**
 * Exception for downloads and uploads: File not found
 * If launched by an EntryPoint, the status of the response will be 404 NOT FOUND
 *
 */
public class FileNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -9117135858548856264L;

	public FileNotFoundException(String message) {
		super(message);
	}

	public FileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
