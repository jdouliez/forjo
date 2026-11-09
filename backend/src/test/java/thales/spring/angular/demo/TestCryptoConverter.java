package thales.spring.angular.demo;

import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import org.apache.commons.codec.binary.Base64;

/**
 * Exemple d'implémentation de chiffrement automatique d'un mot de passe en base de données
 * POUR SUPPORT DE COURS
 */
public class TestCryptoConverter implements AttributeConverter<String, String> {
	
    private TestCryptoConverter() {}
    
    private static class SingletonHolder {    
        public static final TestCryptoConverter instance = new TestCryptoConverter();
    }    
 
    public static TestCryptoConverter getInstance() {    
        return SingletonHolder.instance;    
    }    

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final byte[] KEY = "MySuperSecretKey".getBytes();

    @Override
    public String convertToDatabaseColumn(String ccData) {
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
    public String convertToEntityAttribute(String dbData) {
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