package thales.spring.angular.demo.remediation.dto;

import org.apache.http.HttpResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class CheckResult {

    private Boolean success;
    private HttpResponse response;
    private String reponseBody;
}
