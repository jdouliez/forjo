package thales.spring.angular.demo.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name="ADVISOR")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
/**
 * Advisor Entity related to ADVISOR DATATABLE
 *
 */
public class Advisor {
    @Id
    @Column(name = "id")
    private Long idAdvisor;
  
    @JsonProperty("firstnameAdvisor")
    @Column(name = "FIRSTNAME")
    private @NonNull String firstName;
  
    @JsonProperty("lastnameAdvisor")
    @Column(name = "LASTNAME")
    private @NonNull String lastName;
  
    private @NonNull String office;
  
    @JsonProperty("SocialSecurityNumbers")
    @Column(name = "SOCIALSECURITYNUMBERS")
    private @NonNull String SocialSecurityNumbers;

    @JsonManagedReference
    @OneToMany(mappedBy = "advisor", cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE}, fetch = FetchType.EAGER)
    /**
     * an Advisor has 0-N Clients
     */
    private Set<Client> clients = new HashSet<Client>();

    @Override
	public String toString() {
	    try {
	        return new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
	    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
	        e.printStackTrace();
            return "";
	    }
	}
}
