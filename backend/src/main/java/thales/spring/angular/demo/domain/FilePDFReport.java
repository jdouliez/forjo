package thales.spring.angular.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter @Setter
/**
 * FilePDFReport Class
 * @see SearchPDFController
 *
 */
public class FilePDFReport {
	 private long id;
	 private String content;
}
