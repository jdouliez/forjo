package thales.spring.angular.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import thales.spring.angular.demo.util.I18n;

@Getter
@AllArgsConstructor
/**
 * 
 * available Account types.  Enumeration with informations :
 * Account name
 * Accoun rate
 *
 */
public enum AccountEnum {
	COURANT(I18n.getMessage("enum.compte.courant"),0.),
	LIVRETA(I18n.getMessage("enum.livretA"),0.75),
	PEL(I18n.getMessage("enum.PEL"),2.5),
	CEL(I18n.getMessage("enum.CEL"),1.25),
	BOOST(I18n.getMessage("enum.super.livret"),4.5);
	
	public String name;
	public Double rate;
}
