package thales.spring.angular.demo.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode()
/**
 * Class representing the report of a downloaded file
 *
 */
public class DownloadFileResponse {
	private String fileName;
	private String fileDownloadUri;
	private String fileType;
	private long size;
}
