package thales.spring.angular.demo.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.services.FileStorageService;

@RestController
/**
 * API Controller /api/downloadFile
 *
 */
public class FileDownloadController extends GenericController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileDownloadController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/api/downloadFile")
    /**
     * POST /api/downloadFile Downloading a resource
     *
     * @param fileName : the name of the resource to be downloaded
     * @param request : HTTP request including the Authorization header. This
     * header allows to identify the user by token.
     * @return 200 OK null if the resource is not found 200 OK the resource to
     * download
     */
    public ResponseEntity<Resource> postDownloadFile(@RequestParam("filename") String fileName, HttpServletRequest request) {
       
        UserApp user = getUserByToken(request);
       
        if (user != null) {
            return download(fileName, request);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    /**
     * Download the requested resource
     *
     * @param fileName : the name of the resource to download
     * @param request: the HTTP request 200 OK the resource to download HTTP
     * error
     */
    private ResponseEntity<Resource> download(String fileName, HttpServletRequest request) {
        LOGGER.debug("Download file : {}", fileName);
        // Load file as Resource
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            LOGGER.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
