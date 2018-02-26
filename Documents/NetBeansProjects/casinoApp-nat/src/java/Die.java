import java.security.SecureRandom;

public class Die {
    private int value;
    private SecureRandom randomNumbers = new SecureRandom();
    
    public Die(){
        
    }
    
    public void roll(){
        value = 1 + randomNumbers.nextInt(6);
    }
    
    public int getValue(){
        return value;
    }
}
