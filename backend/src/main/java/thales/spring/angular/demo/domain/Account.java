package thales.spring.angular.demo.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="ACCOUNT")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
@EqualsAndHashCode(exclude = "client")
/**
 * 
 * Account Entity related to ACCOUNT DATATABLE
 *
 */
public class Account {
	
	@Id
	@Column(name = "id")
	private Long idAccount;

	private String type;

	private double balance;

	private double rate;

	@Column(columnDefinition="text")
	private String document;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_client")		
	/**
	 * a Client is associated to the Account
	 */
	private Client client;
	
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
