import java.io.*;
import java.util.TreeSet;

/**
 * 
 * @author Chaewon Eom
 * This UserDictionary class represents a user dictionary object for storing user-defined words.
 * The dictionary is loaded from and saved to a text file ("user_dictionary.txt").
 * Words in the user dictionary are stored in a sorted order using a TreeSet.
 * The class provides methods for adding, removing, checking, and resetting words in the user dictionary.
 * Additionally, it allows users to save the user dictionary to disk.
 *
 */

public class UserDictionary {
    private TreeSet<String> userDictionary;            // Tree
    private final String file = "user_dictionary.txt"; //file path

    /** 
     * Constructor for UserDictionary class
     * Load words from the "user_dictionary.txt" file.
     * If the file does not exist, it attempts to create a new empty file.
     */
    public UserDictionary() {
        userDictionary = new TreeSet<>();
        FileReader dictTxt = null;
        try {
            dictTxt = new FileReader(file);
            BufferedReader buffer = new BufferedReader(dictTxt);
            String word;

            while((word = buffer.readLine()) != null) {
                // remove leading and trailing whitespace
                word = word.trim();
                // word is a key with a null value
                userDictionary.add(word);
            }
            // close the reader
            buffer.close();

        } catch (IOException e) {
            boolean secondFailFlag = false;
            // file does not exist and will try to make a new empty file of the name "user_dictionary.txt"
            try {
                if (dictTxt == null) {
                    FileWriter newFile = new FileWriter(file);
                    newFile.close();
                }
            } catch (IOException c) {
                secondFailFlag = true;
                //System.err.println("Error loading dictionary: " + c.getMessage());
            }
            if (secondFailFlag) {
                System.err.println("Error loading dictionary: " + e.getMessage());
            }
        }
    }

    /**
     * Saves the current user dictionary to the "user_dictionary.txt" file.
     * If the dictionary is empty, the file is deleted.
     */
    public void saveUserDicToDisk() {
        try {
            File dictTxt = new File(file);
            dictTxt.delete();
            if (userDictionary.isEmpty()) {
                return;
            } else {
                FileWriter newFile = new FileWriter(file);
                BufferedWriter buffer = new BufferedWriter(newFile);
                for (String word : userDictionary) { // Write the sorted words
                    buffer.write(word);
                    buffer.newLine();
                }
                buffer.close();
            }
        } catch (IOException e) {
            System.err.println("Error saving dictionary to file: " + e.getMessage());
        }
    }

    /**
     * Adds a word to the user dictionary after trimming leading and trailing whitespaces.
     * @param word The word to be added to the user dictionary.
     */
    public void addWord(String word) {
        word = word.trim();
        userDictionary.add(word);
    }
    
    /**
     * Removes a word from the user dictionar after trimming leading and trailing whitespaces.
     * @param word The word to be removed from the user dictionary.
     */
    public void removeWord(String word) {
        word = word.trim();
        userDictionary.remove(word);
    }

    /**
     * Checks if the user dictionary contains a specific word.
     * @param word	The word to be checked in the user dictionary.
     * @return boolean returns true if the word is in the user dictionary,
     * 						   false if the word is not in the user dictionary.
     */
    public boolean contains(String word) {
        return userDictionary.contains(word);
    }

    /**
     * Resets the user dictionary by clearing all sorted words.
     */
    public void reset() {
        userDictionary.clear();
    }

    /**
     * Gets TreeSet containing the user dictionary words.
     * @return userDictionary The TreeSet containing the user dictionary words.
     */
    public TreeSet<String> getUserDictionary() {
        return userDictionary;
    }

}
