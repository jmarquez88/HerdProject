public class User
{
    private String name, username , password;
    private int balance , currentWins , currentPlayed;
    
    //empty constructor
    User() {
    
        name = "";
        username = "";
        password = "";
        balance = 0;
        currentWins = 0;
        currentPlayed = 0;
    }

  
    
    User( String n , String u , String p) {
    
        name = n;
        username = u;
        password = p;
        balance = 0;
        currentWins = 0;
        currentPlayed = 0;
    }
    
      public String getUsername()
    {
        return username;
    }

    public void setCurrentWins(int currentWins)
    {
        this.currentWins = currentWins;
    }

    public void setCurrentPlayed(int currentPlayed)
    {
        this.currentPlayed = currentPlayed;
    }

    public int getCurrentWins()
    {
        return currentWins;
    }

    public int getCurrentPlayed()
    {
        return currentPlayed;
    }
    
    public void setName(String n) {
    
        name = n;
    }
    
    public String getName() {
    
        return name;
    }
    
    public void setUsername(String u) {
    
        username = u;
    }
    
    public String getUserName() {
    
        return username;
    }
    
    public void setPassword(String p) {
    
        password = p;
    }
    
    public String getPassword() {
    
        return password;
    }
    
    public String checkPassword(String p , User u) {
    
        return "l";
    }
    
    public int getBalance() {
    
        return balance;
    }
    
    public void setBalance(int i) {
    
        balance = i;
    }
}
