package thales.spring.angular.demo.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.GenerationType;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import lombok.Setter;

@Entity
@Table(name="CLIENT")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"advisor"})
/**
 * Client Entity related to CLIENT DATATABLE
 *
 */
public class Client {
	
    @Id 
    @Column(name = "id")
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType. IDENTITY)
    private Long idClient;

    @JsonProperty("firstname")
    @Column(name = "firstname")
    private @NonNull String firstName;

    @JsonProperty("lastname")
    @Column(name = "lastname")
    private @NonNull String lastName;

    @JsonProperty("address")
    private String address;

    @JsonProperty("picture_base64")
    @Column(name = "PICTURE_BASE64", length =  512000) // 512000 = 500*1024 = 500kB
    private String pictureBase64Data;

    @JsonProperty("picture_url")
    @Column(name = "picture_url")
    private String pictureUrl;
    

    /**
     * a Client has 0-N Accounts
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "client", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Set<Account> accounts = new HashSet<Account>();
    

    /**
     * a Client has 0-1 Advisor
     * 
     */
    @JsonBackReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_advisor")
    private Advisor advisor;
    
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
