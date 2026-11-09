package thales.spring.angular.demo;

import java.beans.XMLDecoder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import thales.spring.angular.demo.domain.Account;
import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.exception.AccountAlreadyExistException;
import thales.spring.angular.demo.exception.ClientNotFoundException;
import thales.spring.angular.demo.services.AccountService;
import thales.spring.angular.demo.services.ClientService;

/**
 * Exemple de faille XXE
 * POUR SUPPORT DE COURS
 */
public class ExempleXXE {

	private static Account deserializeFromXML(String xmlAccount) throws IOException {
		Account decoded = null;
		
		try (XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlAccount.getBytes()))) {		 
		    decoded = (Account) decoder.readObject();	    	
	    }

		return decoded;
	}
	
	private static List<Account>  deserializeFromDOM(String xmlAccount) throws ParserConfigurationException, SAXException, IOException {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(xmlAccount.getBytes()));
		
		doc.getDocumentElement ().normalize ();

        NodeList listOfAccounts = doc.getElementsByTagName("account");
        int totalAccounts = listOfAccounts.getLength();
        
        List<Account> accounts = new ArrayList<>(totalAccounts);

		for (int s = 0; s < totalAccounts; s++) {
			//BOUCLE SUR LES NOEUDS DE L'ELEMENT account
		}
		return accounts;
	}
	
	@Autowired
	private AccountService service;
	@Autowired
	private ClientService serviceClient;
	
	@PostMapping(value="ok/{idClient}", produces={"application/json","application/xml"},consumes={"application/json", "application/xml"})
	public ResponseEntity<?> createAccount(@PathVariable Long idClient, @RequestBody Account account)  throws ClientNotFoundException, AccountAlreadyExistException, JsonParseException, JsonMappingException, IOException {
		Client client = verifClient(idClient);
		
		if (service.isExist(account.getIdAccount())){
			throw new AccountAlreadyExistException();
		}
		
		account.setClient(client);
		return new ResponseEntity<>(service.update(account), HttpStatus.CREATED);
	}
	
	private Client verifClient(Long idClient) {
		Optional<Client> oClient = serviceClient.findById(idClient);
		
		if (!oClient.isPresent()) {
			throw new ClientNotFoundException();
		}
		
		return oClient.get();
	}
}
