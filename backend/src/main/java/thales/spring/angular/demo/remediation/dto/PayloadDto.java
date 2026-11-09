/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thales.spring.angular.demo.remediation.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.http.HttpMethod;

import lombok.Getter;
import lombok.Setter;

/**
 * Class which represent a payload to test is vulnerability is active or not
 */
@Getter 
@Setter
public class PayloadDto {
    
    @JsonProperty("payload")
    private String payload;
    
    @JsonProperty("regex")
    private String regex;
    
    @JsonProperty("exceptions")
    private List<ExceptionDto> exceptions;
    
    @JsonProperty("method")
    private HttpMethod method;
    
    @JsonProperty("isHtml")
    private Boolean isHtml;
}
