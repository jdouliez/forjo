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

@Getter
@Setter
public class OwaspPayload {
    
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("vulnerabilities")
    private List<VulnerabilitiesDto> vulnerabilities;
}
