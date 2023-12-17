import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

/**
 * 
 * @author Chaewon Eom 
 * This MainDictionary class represents a dictionary object.
 * This class reads a file ("words_alpha.txt") that contains English words
 * and stores the words into a HashSet.
 * This class is only to be created once in the mainUI.
 * 
 */
public class MainDictionary {
	
	/**
	 * Container for English words stored in memory
	 */
    public final static HashSet<String> dictionary = new HashSet<>();
    
    /**
     * File path of known correct English words
     */
    private final String correctEnglishWords = "words_alpha.txt";     
    
    /**
     * Array of alphabets needed for spell checker class
     */
    private final char alphabet[] = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'}; 

    /** 
     * Constructor for MainDictionary class
     */
    public MainDictionary() {
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(correctEnglishWords));
            String word;

            while((word = buffer.readLine()) != null) {
                // remove leading and trailing whitespace
                word = word.trim();
                // word is a key with a null value
                dictionary.add(word);
            }
            // close the reader
            buffer.close();

        } catch (IOException e) {
            System.err.println("Error loading dictionary: " + e.getMessage());
        }
    }

    /**
     * This method checks if the dictionary contains a given word.
     * @param word The word that needs to be checked
     * @return boolean returns true if the word is in the dictionary,
     * 						   false if the word is not in the dictionary.
     */
    public boolean contains(String word) {
        return dictionary.contains(word.toLowerCase());
    }

    /**
     * Getter method for alphabet array.
     * @return alphabet Array of alphabets
     */
    public char[] getAlphabet() {
        return alphabet;
    }
}

