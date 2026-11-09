package thales.spring.angular.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import thales.spring.angular.demo.domain.Role;
import thales.spring.angular.demo.domain.RoleEnum;

@RepositoryRestResource
/**
 * 
 * Interface for REPOSITORY ROLE extending:
 * - the JPA REPOSITORY ROLE interface 
 *
 */
public interface RoleRepository extends JpaRepository<Role, Long> {
	/**
	 * 
	 * @param role: one of the possible values of the ROLE field/column: @see RoleEnum
	 * @return the role according to the field/column ROLE of the ENTITY/TABLE ROLE
	 */
	Role findByRole(RoleEnum role);
}
