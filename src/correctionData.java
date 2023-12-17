import java.util.TreeSet;

/**
 * @author Robert Biernacki
 * This class represents data on an invidual word, which includes
 * the word its self, the possible corrections, and the error tpye
 */
public class correctionData {
    private TreeSet<String> possibleCorrection;
    private String orginalWord;
    private errorType error;
    private int index;

    /**
     * enum object to represent the types error type
     */
    public enum errorType {
        capsFirstLetter,
        mixedCaps,
        mixedCaps_capFirstLetter,
        misspelling,
        misspelling_capFirstLetter,
        numbersInWord,
        numbersInWord_capsFirstLetter,
        doubleWords,
        noError;
    }

    /**
     * constructor
     */
    public correctionData() {
        possibleCorrection = null;
        orginalWord = null;
        error = errorType.noError;
    }

    /**
     * setter for the original word
     * @param word to be set as the original
     */
    public void setOrginalWord(String word) {
        orginalWord = word;
    }

    /**
     * getter method to get the original word
     * @return String of the original word
     */
    public String getOrginalWord() {
        return orginalWord;
    }

    /**
     * method that adds strings of possible corrections to the Tree set
     * @param word string that is to be added to Tree set
     */
    public void addCorrection(String word) {
        if (possibleCorrection == null) {
            possibleCorrection = new TreeSet<>();
            possibleCorrection.add(word);
        } else {
            possibleCorrection.add(word);
        }
    }

    /**
     * getter method that returns the Tree set
     * @return TreeSet of corrections
     */
    public TreeSet<String> getCorrections() {
        return possibleCorrection;
    }

    /**
     * setter method that sets the possible corrections to input tree
     * @param e of TreeSet with possible corrections for words
     */
    public void ErrorList(TreeSet<String> e) {
        possibleCorrection = e;
    }

    /**
     * method that checks if the word has an error
     * @return boolean true if the error type is not of noError type
     */
    public boolean isError() {
        return (error == errorType.noError) ? false : true;
    }

    /**
     * getter method for error type
     * @return errorType the error type
     */
    public errorType getError() {
		return error;
    }

    /**
     * sets the error type for the object based on a int number
     * @param err the int of the error code for this method to read
     */
    public void setError(int err) {
        switch (err) {
            case 1:
                error = errorType.capsFirstLetter;
                break;
            case 4:
                error = errorType.capsFirstLetter;
                break;
            case 7:
                error = errorType.doubleWords;
                break;
            case 8:
                error = errorType.numbersInWord;
                break;
            case 9:
                error = errorType.numbersInWord_capsFirstLetter;
                break;
            case 10:
                error = errorType.mixedCaps;
                break;
            case 11:
                error = errorType.mixedCaps_capFirstLetter;
                break;
            case 15:
                error = errorType.misspelling;
                break;
            case 16:
                error = errorType.misspelling_capFirstLetter;
                break;
            case 25:
                error = errorType.misspelling_capFirstLetter;
                break;
            default:
                error = errorType.noError;
                break;
        }
    }

    /**
     * A method to display the correctionData object as a string
     * @return String representation of the correctionData object
     */
    public String toStirng() {
        if (possibleCorrection == null) {
            return "original word: [" + orginalWord + "] Error type: " + error.toString();
        }
        return "Original Word: [" + orginalWord + "] Error Type: [" + error.toString() + "] Corrections: " + possibleCorrection.toString();
    }
}
