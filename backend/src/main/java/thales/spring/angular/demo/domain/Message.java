package thales.spring.angular.demo.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table(name="MESSAGE")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode
@Builder
/**
 * Message Entity related to MESSAGE DATATABLE
 * Allow to save user's send messages to his advisor
 */
public class Message {
	@Id 
	@GeneratedValue
	@Column(name = "id")
	private Long idMessage;
	private String subject;

	@Lob
	private String content;

	@NonNull
	@Column(name = "id_client")
	private Long from;
	
	@NonNull
	@Column(name = "id_advisor")
	private Long to;
  
	@NonNull
    @Column(name = "read")
    private Boolean read;
  
	@Enumerated(EnumType.STRING)
    private MessageStatus status;
}
