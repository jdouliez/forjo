package thales.spring.angular.demo.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import thales.spring.angular.demo.domain.FilePDFReport;

@Service
@ConfigurationProperties(prefix = "file")
/**
 * Implementation of SearchPDFService
 */
public class SearchPDFServiceImpl implements SearchPDFService {

	@Value("${file.upload-dir}")
	private String directoryPath;

	private static final Logger LOGGER = LoggerFactory.getLogger(SearchPDFServiceImpl.class);

	@Override
	/**
	 * @see SearchPDFService.getPDFsLike(String regex) 
	 */
	public List<FilePDFReport> getPDFsLike(String regex) {
		List<FilePDFReport> reports = new ArrayList<FilePDFReport>();

		int result = -1;

		try {
			Runtime rt = Runtime.getRuntime();
			Process proc;
			String cmd = "cd " + directoryPath + ";ls -1 *" + regex + "*.pdf";
			LOGGER.info(cmd);
			proc = rt.exec(new String[] { "sh", "-c", cmd });
			result = proc.waitFor();
			try (InputStream in = (result == 0) ? proc.getInputStream() : proc.getErrorStream();
					BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));) {
				String s;
				long l = 0L;
				while ((s = br.readLine()) != null) {
					reports.add(new FilePDFReport(++l, s));
				}
			}
		} catch (IOException e) {
			LOGGER.info(e.getMessage());
		} catch (InterruptedException e) {
			LOGGER.info(e.getMessage());
		}

		return reports;
	}
}