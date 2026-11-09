package thales.spring.angular.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file")
/**
 * 
 * Parameter related to the application.properties file (and its derivatives)
 * Settings to manage storage access (download/upload of resources)
 */
public class FileStorageProperties {

	@Value("${file.upload-dir}")
	private String uploadDir;

	public String getUploadDir() {
		return uploadDir;
	}

	public void setUploadDir(String uploadDir) {
		this.uploadDir = uploadDir;
	}
}