package thales.spring.angular.demo.domain;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="ROLE")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode
/**
 * Role Entity related to ROLE DATATABLE
 *
 */
public class Role {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
	@Enumerated(EnumType.STRING)
    private RoleEnum role = RoleEnum.USER;

	@JsonBackReference
    @ManyToMany(mappedBy = "roles")
	/**
	 * a Role is associated to 0-N Users
	 */
    private Collection<UserApp> users;
    
    public Role(RoleEnum typeRole) {
    	this.role = typeRole;
    }

	public RoleEnum getRole(){
		return this.role;
	}
    
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