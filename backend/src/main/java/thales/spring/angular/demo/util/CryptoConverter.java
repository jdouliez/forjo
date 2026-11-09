package thales.spring.angular.demo.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * convertit une donnée en la chiffrant selon l'algorythme AES/ECB/PKCS5Padding
 *
 */
public class CryptoConverter implements AttributeConverter<String, String> {
	
    private CryptoConverter() {}
    
    private static class SingletonHolder {    
        public static final CryptoConverter instance = new CryptoConverter();
    }    
 
    public static CryptoConverter getInstance() {    
        return SingletonHolder.instance;    
    }    

	private static final Logger LOGGER = LoggerFactory.getLogger(CryptoConverter.class);
	
    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final byte[] KEY = "MySuperSecretKey".getBytes();

    @Override
    /**
     * @param ccData : la donnée à chiffrer
     * retourne la donnée chiffrée
     */
    public String convertToDatabaseColumn(String ccData) {
      LOGGER.info("CryptoConverter.convertToDatabaseColumn = " +ccData);
      // do some encryption
      Key key = new SecretKeySpec(KEY, "AES");
      try {
         Cipher c = Cipher.getInstance(ALGORITHM);
         c.init(Cipher.ENCRYPT_MODE, key);
         return new String(Base64.encodeBase64(c.doFinal(ccData.getBytes())));
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
    }

    @Override
    /**
     * 
     * @param dbData : la donnée chiffrée
     * retourne la donnée à déchiffrer
     */
    public String convertToEntityAttribute(String dbData) {
    	LOGGER.info("CryptoConverter.convertToEntityAttribute = " +dbData);
      // do some decryption
      Key key = new SecretKeySpec(KEY, "AES");
      try {
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        return new String(c.doFinal(Base64.decodeBase64(dbData)));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
}
