/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thales.spring.angular.demo.remediation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author galadac
 */
@Getter @Setter
public class VulnerabilitiesDto {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("endPoint")
    private String endPoint;
    
    @JsonProperty("payloads")
    private List<PayloadDto> payloads;
}
