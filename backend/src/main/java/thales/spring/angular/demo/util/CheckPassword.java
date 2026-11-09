package thales.spring.angular.demo.util;

/**
 * 
 * Checks if the password is complex
 *
 */
public class CheckPassword {
	
    private CheckPassword() {}
    
    private static class SingletonHolder {    
        public static final CheckPassword instance = new CheckPassword();
    }    
 
    public static CheckPassword getInstance() {    
        return SingletonHolder.instance;    
    }    

    /**
     * @param password : the password in clear text
     * returns true if password complex or false
     */
    public boolean check(String password) {
    	int cptLowerCase = 0;
		int cptUpperCase = 0;
		int cptDigit = 0;
		int length = password.length();
		
		for (int i = 0; i < length; i++) {
			char ch = password.charAt(i);			
			if (Character.isLowerCase(ch)) {				                   
				cptLowerCase++;				
			}
			else if (Character.isUpperCase(ch)) {
				cptUpperCase++;				
			}
			else if (Character.isDigit(ch)) {
				cptDigit++;					
			}
		}
		int cptOthers = length - cptDigit - cptUpperCase - cptLowerCase;
		return (length >= 1 && cptLowerCase >= 0 && cptUpperCase >= 0 && cptDigit > 0 && cptOthers >= 0);
    }  
}
