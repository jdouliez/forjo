package thales.spring.angular.demo.controllers;

import java.beans.ExceptionListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import thales.spring.angular.demo.domain.Account;
import thales.spring.angular.demo.domain.Client;
import thales.spring.angular.demo.domain.UserApp;
import thales.spring.angular.demo.exception.AccountAlreadyExistException;
import thales.spring.angular.demo.exception.ClientNotFoundException;
import thales.spring.angular.demo.services.AccountService;
import thales.spring.angular.demo.services.ClientService;
import thales.spring.angular.demo.util.GenericResponse;

@RestController
@RequestMapping("/api/account")
@CrossOrigin(origins = "*")
/**
 *
 * API Controller /api/account
 *
 */
public class AccountController extends GenericController{

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private AccountService service;

    @Autowired
    private ClientService serviceClient;

    @GetMapping
    /**
     * GET /api/account
     *
     * @return the list of all accounts No verification or authentication
     * control
     * @see Account
     */
    public ResponseEntity<Collection<Account>> getAccounts() {
        LOGGER.debug("GET ACCOUNTS");
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }


    @GetMapping(value = "/user")
    /**
     * GET /api/account/user
     *
     * @param request : HTTP request
     * @return user accounts (authenticated by token user)
     * @see Account
     */
    public ResponseEntity<Collection<Account>> getUserAccounts(HttpServletRequest request) {
        UserApp user = getUserByToken(request);
     
        if (user != null) {
            return new ResponseEntity<>(user.getClient().getAccounts(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @GetMapping(value = "/rib", produces = "text/plain; charset=UTF-8")
    /**
     * GET /api/account/rib
     *
     * @param request : HTTP request
     * @return user current account informations (authenticated by token). Body
     * http is a string.
     */
    public ResponseEntity<String> getCompteCourant(HttpServletRequest request) {
        
        UserApp user = getUserByToken(request);
       
        LOGGER.debug("RETRIEVE ACCOUNT");
       
        if (user != null) {
            for (Account account : user.getClient().getAccounts()) {
                if (account.getType().equals("Compte Courant")) {
                    return new ResponseEntity<String>(account.getDocument(), HttpStatus.OK);
                }
            }
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    /**
     * DELETE /api/account/{id} Delete the account identified by id No
     * verification or authentication control
     *
     * @param id : identifiant du compte
     * @return la réponse HTTP : OK 200 if deletion OK
     */
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAccounts(@PathVariable Long id) {
        LOGGER.debug("DELETE ACCOUNT");
        service.deleteById(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PutMapping
    /**
     * PUT /api/account/ Body request to fill out. No verification or
     * authentication control
     *
     * @param account : account to update. account bypassed in body request .
     * @return HTTP response : CREATED 201 if update is OK
     */
    public ResponseEntity<?> updateAccounts(@RequestBody Account account) {
        LOGGER.debug("UPDATE ACCOUNT");
        return new ResponseEntity<>(service.update(account), HttpStatus.CREATED);
    }

    @PostMapping(value = "client/{idClient}", produces = {"application/json", "application/xml"}, consumes = {
        "application/json", "application/xml"})
    /**
     * POST /api/account/client/{idClient} Body request to fill out. No
     * verification or authentication control except to : the client must exist
     *
     * @param idClient client id.
     * @param account : account to update. account bypassed in body request .
     * @return HTTP response : CREATED 201 if create is OK
     * @see account
     */
    public ResponseEntity<?> createAccount(@PathVariable Long idClient, @RequestBody Account account) {

        GenericResponse response = null;
        Client client = null;

        try {
            client = verifClient(idClient);
        } catch (ClientNotFoundException cnfe) {
            response = new GenericResponse(cnfe.getMessage(), messageSource.getMessage("msg.notFoundClient", null, LocaleContextHolder.getLocale()));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            if (service.isExist(account.getIdAccount())) {
                throw new AccountAlreadyExistException(String.format(messageSource.getMessage("msg.responseExisting", null, LocaleContextHolder.getLocale()), account.getIdAccount()));
            }
            // CALL TO HAVE THE CORRECT XML ACCOUNT
            serializeToXML(account);
            account.setClient(client);
        } catch (IOException e) {
            response = new GenericResponse(e.getMessage(), "IOException");
        } catch (AccountAlreadyExistException e) {
            response = new GenericResponse(e.getMessage(), messageSource.getMessage("msg.allReadyExistAccount", null, LocaleContextHolder.getLocale()));
        }

        if (response == null) {
            return new ResponseEntity<>(service.update(account), HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "xml")
    /**
     * POST /api/account/xml Create or update an account No verification or
     * authentication control. The account is not linked to a client.
     *
     * @param xmlAccount : xml representing an account. Must be present in body
     * request http.
     * @return response http body (string). HTTP : CREATED 201 if OK
     * @see Account
     */
    public ResponseEntity<?> serializeAccount(@RequestBody String xmlAccount) {

        Account account = deserializeFromXML(xmlAccount);
        return new ResponseEntity<>(service.update(account), HttpStatus.CREATED);
    }

    /**
     * POST /api/account create an account Controls : - client must exist.
     * idClient to set in @xmlAccount parameter - account must not exist. - User
     * authenticated by token user
     *
     * @param xmlAccount : xml representing an account. Must be present in body
     * request http.
     * @param request : HTTP request
     * @return response http body (string). HTTP : CREATED 201 if OK
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<?> buildAccount(@RequestBody String xmlAccount, HttpServletRequest request) {
       
        UserApp user = getUserByToken(request);
       
        if (user != null) {

            LOGGER.info("Received XML data to create new account for client : {}", user.getEmail());

            GenericResponse response = null;
            Client client = null;
            List<Account> accounts = null;

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder;
                Document doc;
                
                builder = factory.newDocumentBuilder();
                doc = builder.parse(new ByteArrayInputStream(xmlAccount.getBytes()));
                doc.getDocumentElement().normalize();

                NodeList listOfAccounts = doc.getElementsByTagName("account");
                int totalAccounts = listOfAccounts.getLength();

                accounts = new ArrayList<>(totalAccounts);

                for (int s = 0; s < totalAccounts; s++) {

                    Node firstAccountNode = listOfAccounts.item(s);
                    
                    if (firstAccountNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element firstAccountElement = (Element) firstAccountNode;

                        String idAccount = extractElementByName(firstAccountElement, "idAccount");
                       
                        if (service.isExist(Long.valueOf(idAccount))) {
                            throw new AccountAlreadyExistException(String.format(messageSource.getMessage("msg.responseExisting", null, LocaleContextHolder.getLocale()), idAccount));
                        }
                       
                        String type = extractElementByName(firstAccountElement, "type");
                        String balance = extractElementByName(firstAccountElement, "balance");
                        String bonus = extractElementByName(firstAccountElement, "rate");
                        String statement = extractElementByName(firstAccountElement, "document");

                        Account account = Account.builder()
                                            .idAccount(Long.valueOf(idAccount))
                                            .balance(Double.valueOf(balance))
                                            .rate(Double.valueOf(bonus))
                                            .type(type)
                                            .document(statement)
                                            .client(client)
                                            .build();

                        accounts.add(account);
                    }
                }
            } catch (ParserConfigurationException e) {
                response = new GenericResponse(e.getMessage(), "ParserConfigurationException");
            } catch (SAXException e) {
                response = new GenericResponse(e.getMessage(), "SAXException");
            } catch (IOException e) {
                response = new GenericResponse(e.getMessage(), "IOException");
            } catch (AccountAlreadyExistException e) {
                response = new GenericResponse(e.getMessage(), messageSource.getMessage("msg.allReadyExistAccount", null, LocaleContextHolder.getLocale()));
            }

            if (response == null && accounts != null) {
                return new ResponseEntity<>(service.updateAll(accounts), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>(new GenericResponse(messageSource.getMessage("msg.nopermitToAccessResource", null, LocaleContextHolder.getLocale()), messageSource.getMessage("msg.authenticationFail", null, LocaleContextHolder.getLocale())), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Check if a client exists
     *
     * @param idClient client id
     * @return the client if found throw ClientNotFoundException
     */
    private Client verifClient(Long idClient) {
        Optional<Client> oClient = serviceClient.findById(idClient);
        
        if (!oClient.isPresent()) {
            throw new ClientNotFoundException(String.format(messageSource.getMessage("msg.responseNotExisting", null, LocaleContextHolder.getLocale()), idClient));
        }
        
        return oClient.get();
    }

    /**
     *
     * @param element : XML element
     * @param name : XML element name
     * @return XML element value XML identified by name
     */
    private String extractElementByName(Element element, String name) {
        NodeList nameList = element.getElementsByTagName(name);
        Element nameElement = (Element) nameList.item(0);

        NodeList textFNList = nameElement.getChildNodes();
        String nameValue = ((Node) textFNList.item(0)).getNodeValue().trim();
        return nameValue;
    }

    /**
     * Create a XML file representing the account
     *
     * @param account : the account to print in the XML file
     * @throws IOException if file writing problem
     * @see Account
     */
    private static void serializeToXML(Account account) throws IOException {
        try (FileOutputStream fos = new FileOutputStream("/tmp/accounts.xml");
                XMLEncoder encoder = new XMLEncoder(fos)) {
            encoder.setExceptionListener(new ExceptionListener() {
                public void exceptionThrown(Exception e) {
                    LOGGER.warn("Exception! :" + e.toString());
                }
            });
            encoder.writeObject(account);
        }
    }

    /**
     * Load the account defined by XML string structure
     *
     * @param xmlAccount : XML string structure for an account
     * @return the account object
     * @see Account
     */
    private static Account deserializeFromXML(String xmlAccount) {
        Account decoded = null;
       
        try (XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlAccount.getBytes()))) {
            decoded = (Account) decoder.readObject();
        }
        return decoded;
    }


    @GetMapping(value = "/{id}")
    /**
     * GET /api/account/{id}
     * Control by token user
     * @param id : account id
     * @param request : HTTP request including the "Authorization" header
     * @return specific account
     * @see Advisor
     */
    public ResponseEntity<Collection<Account>> getAccountById(@PathVariable String id, HttpServletRequest request) {
        UserApp user = getUserByToken(request);
        List<Account> accounts = service.getAccountByClientIdAndAccountId((Long.toString(user.getIdUser())), id);
        if (accounts.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

}
