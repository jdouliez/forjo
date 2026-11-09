package thales.spring.angular.demo.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import thales.spring.angular.demo.domain.FilePDFReport;
import thales.spring.angular.demo.services.SearchPDFServiceImpl;

@RestController
@RequestMapping("/api/pdf/search")
@CrossOrigin(origins = "*")
/**
 * API Controller : /api/pdf/search
 */
public class SearchPDFController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchPDFController.class);

    @Autowired
    /**
     * @see SearchPDFServiceImpl
     */
    private SearchPDFServiceImpl searchService;

    @GetMapping(value = "/{regex}")
    /**
     * GET /api/pdf/search/{regex}
     *
     * @param regex : string type regex
     * @return the list of found files
     * @see FilePDFReport
     */
    public ResponseEntity<List<FilePDFReport>> getFiles(@PathVariable String regex) {
        LOGGER.debug("SEARCH PDF -> " + regex);
      
        List<FilePDFReport> reports = searchService.getPDFsLike(regex);
        
        if (reports.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(reports, HttpStatus.OK);
    }
}
