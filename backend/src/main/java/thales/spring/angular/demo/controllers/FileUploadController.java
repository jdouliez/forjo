package thales.spring.angular.demo.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import thales.spring.angular.demo.domain.DownloadFileResponse;
import thales.spring.angular.demo.services.FileStorageService;

@RestController
/**
 * API Controller /uploadFile
 *
 */
public class FileUploadController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadController.class);

	@Autowired
	private FileStorageService fileStorageService;

	@PostMapping("/uploadFile")
	/**
	 * POST /uploadFile
	 * Upload a resource to the /downloadFile directory
	 * @param file : the resource to upload
	 * @return a download report
	 * @see thales.spring.angular.demo.fileuploaddownload.payload.Response 
	 */
	public DownloadFileResponse uploadFile(@RequestParam("file") MultipartFile file) {		
		LOGGER.debug("Upload file");
	
		String fileName = fileStorageService.storeFile(file);

		String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
				.path(fileName).toUriString();

		return new DownloadFileResponse(fileName, fileDownloadUri, file.getContentType(), file.getSize());
	}

	@PostMapping("/uploadMultipleFiles")
	/**
	 * POST /uploadMultipleFiles
	 * Upload of several resources in the /downloadFile directory
	 * @param files : the resources to upload
	 * @return a download report list for each uploaded file
	 * @see thales.spring.angular.demo.fileuploaddownload.payload.Response 
	 */
	public List<DownloadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
		return Arrays.asList(files).stream().map(file -> uploadFile(file)).collect(Collectors.toList());
	}
}
