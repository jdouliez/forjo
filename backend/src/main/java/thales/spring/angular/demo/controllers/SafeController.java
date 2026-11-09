package thales.spring.angular.demo.controllers;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/safe")
@CrossOrigin(origins = "*")
/**
 * API Controller: /api/safe
 */
public class SafeController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SafeController.class);
    
    @GetMapping("/secretcode")
    @ResponseBody
    public ResponseEntity<String> getSecretcode(HttpServletRequest request) {
        
        try {
            InetAddress clientIp = InetAddress.getByName(request.getRemoteAddr());
            LOGGER.info("Safe \"secretcode\" endpoint has been requested from IP : " + clientIp);
        
            if (isLocalAddress(clientIp)) {
                return new ResponseEntity<>("{\"status\":\"OK\", \"message\":\" internal request -- accepted\", \"secretcode\" : \"" + getRandomNumber(1000000, 9999999) + "\"}", HttpStatus.OK);            
            }
            else {
                return new ResponseEntity<>("{\"status\":\"KO\", \"message\":\"external request -- refused\"}", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (UnknownHostException e) {
            LOGGER.error("Weird client IP address", e);
            return new ResponseEntity<>("{\"status\":\"KO\", \"message\":\"external request -- refused\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static boolean isLocalAddress(InetAddress addr) {
        // Check if the address is a valid special local or loop back
        if (addr.isAnyLocalAddress() || addr.isLoopbackAddress())
            return true;
    
        // Check if the address is defined on any interface
        try {
            return NetworkInterface.getByInetAddress(addr) != null;
        } catch (SocketException e) {
            return false;
        }
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
