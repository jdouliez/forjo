package thales.spring.angular.demo.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode
/**
 * Class representing serialization concept of an user for the application
 */
public class UserSerializer {
    private @NonNull Long idClient;
    private @NonNull UserApp user;
    
    @Override
	public String toString() {
	    try {
	        return new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
	    } 
		catch (com.fasterxml.jackson.core.JsonProcessingException e) {
	        e.printStackTrace();
			return "";
	    }
	}
}
