package thales.spring.angular.demo.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shell")
/**
 * API Controller : /api/shell
 */
public class WebshellController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebshellController.class);
    
    @PostMapping
    @CrossOrigin(origins = "*")
    /**
     * POST /api/shell
     *
     * @param command : command to execute in shell
     * @param request : HTTP request including the Authorization header. This
     * header allows to identify the user by token.
     * @return the command result
     */
    public ResponseEntity<List<String>> shell(@RequestBody String command, HttpServletRequest request) {
        LOGGER.debug("Running command from remote webshell : {}", command);
        List<String> output = new ArrayList<String>();

        int result = -1;
        try {
            Runtime rt = Runtime.getRuntime();
            Process proc;
            proc = rt.exec(new String[]{"sh", "-c", command});
            result = proc.waitFor();
            try (InputStream in = (result == 0) ? proc.getInputStream() : proc.getErrorStream(); BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));) {
                String s;
                while ((s = br.readLine()) != null) {
                    output.add(s);
                }
            }
        } catch (IOException e) {
            LOGGER.info(e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.info(e.getMessage());
        }

        if (output.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(output, HttpStatus.OK);
    }
}
