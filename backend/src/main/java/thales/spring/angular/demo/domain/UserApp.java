package thales.spring.angular.demo.domain;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import thales.spring.angular.demo.util.CheckPassword;

@Entity
@Table(name="USER")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode
/**
 * User Entity related to USER DATATABLE
 *
 */
public class UserApp {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idUser;
	
    @NonNull
	private String email;
	
    @NonNull
	@JsonIgnore
	private String password;
	
    @NonNull
	@JsonIgnore
	private String token;

    @JsonIgnore
	private boolean strongPassword = false;

	private String secretQuestion = "Quel est le nom de jeune fille de votre mère ?";

	@NonNull
	private String secretAnswer;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_client")
    @JsonIgnore
    /**
     * a User (of the application) is also a Client (of the bank)
     */
    private Client client;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @JsonIgnore
    /**
     * un utilisateur a 0-N roles
     */
    private Collection<Role> roles;

    /**
     * @param value : password
     */
	public void setPassword(String value) {
		this.strongPassword = CheckPassword.getInstance().check(value);
		this.password = value;
	}

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

    /**
     * Returns client's informations (client is also a user)
     * @return String client's informations in json format
     */
    public String clientInformations() {
        return this.client.toString();
    }

    /**
     * The user is a admin ?
     * @return true or false
     */
    public Boolean admin() {
        for (Role role : this.roles) {
            if (role.getRole() == RoleEnum.ADMIN) {
                return true;
            }
        }
        return false;
    }
}
