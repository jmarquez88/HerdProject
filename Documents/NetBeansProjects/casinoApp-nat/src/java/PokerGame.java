import java.util.*;
public class PokerGame
{
    //game id to identify specific game
    private int gameId;
    //array will g=hold list of players
    private ArrayList<User> players;
    //level of play 
    private int level;
    //deck to be used during play
    private Deck deck;
    
    //empty contrusctor
    PokerGame() {
    
        gameId = 0;
        players = new ArrayList<User>();
        level = 0;
        deck = new Deck();
    }
    
    //contructor with parameters
    PokerGame(int g , ArrayList<User> u) {
    
        gameId = g;
        players = u;
        level = 0; 
        deck = new Deck();
        int i;
    }
}
