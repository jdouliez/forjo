package thales.spring.angular.demo.services;

import java.util.List;

import thales.spring.angular.demo.domain.FilePDFReport;

/**
 * 
 * Interface managing the PDF resources search service
 */
public interface SearchPDFService {

	/**
	 * 
	 * @param regex : string of type regex
	 * @return the list of PDFs whose name matches with the regex
	 */
	List<FilePDFReport> getPDFsLike(String regex);
}
