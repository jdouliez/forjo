package thales.spring.angular.demo.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.persistence.AttributeConverter;

/**
 * 
 * convertit une donnée en la chiffrant selon l'algorythme MD5
 *
 */
public class CryptoConverter2 implements AttributeConverter<String, String> {
	
    private CryptoConverter2() {}
    
    private static class SingletonHolder {    
        public static final CryptoConverter2 instance = new CryptoConverter2();
    }    
 
    public static CryptoConverter2 getInstance() {    
        return SingletonHolder.instance;    
    }    

    private static final String ALGORITHM = "MD5";

    @Override
    /**
     * @param ccData : la donnée à chiffrer
     * retourne la donnée chiffrée
     */
    public String convertToDatabaseColumn(String ccData) {
      String generatedPassword = null;
      try {
          // Create MessageDigest instance for MD5
          MessageDigest md = MessageDigest.getInstance(ALGORITHM);
          //Add password bytes to digest
          md.update(ccData.getBytes());
          //Get the hash's bytes
          byte[] bytes = md.digest();
          //This bytes[] has bytes in decimal format;
          //Convert it to hexadecimal format
          StringBuilder sb = new StringBuilder();
          for(int i=0; i< bytes.length ;i++)
          {
              sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
          }
          //Get complete hashed password in hex format
          generatedPassword = sb.toString();
      }
      catch (NoSuchAlgorithmException e)
      {
          e.printStackTrace();
      }
      return generatedPassword;

    }

    @Override
    /**
     * 
     * @param dbData : la donnée chiffrée
     * retourne la donnée à déchiffrer: retourne TOUJOURS "SECRET"
     */
    public String convertToEntityAttribute(String dbData) {
      // do some decryption
      return "SECRET";
    }
}
