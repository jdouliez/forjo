package thales.spring.angular.demo.remediation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Class which represent an exception if the payload is not a success (in case of a remediation solution)
 */
@Getter 
@Setter
public class ExceptionDto {
    @JsonProperty("name")
    private String name;
}
