import java.util.Collections;
import java.util.List;

/**
 * The Housekeeping class handles various states and functionalities related to file operations and user help in a spell checker application.
 */
public class Housekeeping {
    private boolean savedBeforeExiting;
    private boolean compatible;
    private boolean isFileOpened;

    /**
     * Constructs a Housekeeping object with specified initial states.
     *
     * @param savedBeforeExiting Indicates whether changes have been saved before exiting.
     * @param compatible Indicates whether the file is compatible with the spell checker.
     * @param isFileOpened Indicates whether a file is currently opened in the spell checker.
     */
    public Housekeeping(boolean savedBeforeExiting, boolean compatible, boolean isFileOpened) {
        this.savedBeforeExiting = savedBeforeExiting;
        this.compatible = compatible;
        this.isFileOpened = isFileOpened;
    }

    /**
     * Checks if the current file is compatible with the spell checker.
     *
     * @return true if the file is compatible, false otherwise.
     */
    public boolean isFileCompatible() {
        return compatible;
    }

    /**
     * Sets the compatibility status of the current file.
     *
     * @param compatible The new compatibility status.
     */
    public void setCompatible(boolean compatible) {
        this.compatible = compatible;
    }

    /**
     * Checks if a file is currently opened in the spell checker.
     *
     * @return true if a file is opened, false otherwise.
     */
    public boolean isFileOpened() {
        return isFileOpened;
    }

    /**
     * Sets the file opened status.
     *
     * @param isFileOpened The new file opened status.
     */
    public void setFileOpened(boolean isFileOpened) {
        this.isFileOpened = isFileOpened;
    }

    /**
     * Checks if changes have been saved before exiting.
     *
     * @return true if changes have been saved, false otherwise.
     */
    public boolean isSavedBeforeExiting() {
        return savedBeforeExiting;
    }

    /**
     * Sets the saved before exiting status.
     *
     * @param savedBeforeExiting The new saved before exiting status.
     */
    public void setSavedBeforeExiting(boolean savedBeforeExiting) {
        this.savedBeforeExiting = savedBeforeExiting;
    }

    /**
     * Provides a list of help instructions and advice for users of the spell checker application.
     *
     * @return A list of strings, each representing a help instruction or advice.
     */
    public List<String> help() {
        String rawText = 
            "1. Upload a .txt file by clicking 'Loading a new file' button.\n\n" +
            "If the file is not compatible, then the file will not open and the error message will be shown\n\n" +
            "2. Once the file is opened, the spellchecker will detect errors (misspellings, miscapitalizations, and double words) and show them with red underline\n\n" +
            "3. You can right-click the highlighted errors and select from the correction options:\n\n" +
            "\n- Click 'Show Recommended words', and then click 'Recommended word' to see the list of recommended words. Right-click the word you want to switch and then click 'Switch'. \n\n" +
            "\n- Click 'Add to user dictionary'. The word is now added and displayed on User's Dictionary. You can remove the word from the User's Dictionary by right-clicking the word and select 'Delete from User's Dictionary'. You can also reset the User's Dictionary by right-clicking any word and select 'Reset User's Dictionary'.\n\n" +
            "\n- Click 'Manually correct' and type in a word to update.\n\n" +
            "4. Click 'Report' button to see all the metrics including word count, error (misspelling, miscapitalization, double words) counts, etc.\n\n" +
            "5. Save the file before exiting if necessary. You can overwrite the edited file by clicking 'Save' button, or save a copy in a new location by clicking 'Save as a new file' button.\n\n" +
            "6. 'Settings' button allows you to adjust settings:\n\n" +
            "\n- Click 'Browse' to choose default save location and click 'Apply' button. It will now set as your default saving location.\n\n" +
            "\n- Check 'Filter or skip HTML/XML tags' and click 'Apply' button. The spellchecker will now filter HTML tags.";

        // Replace each occurrence of "\n" followed by a digit with "<br>" followed by that digit
        String formattedText = rawText.replaceAll("\n(\\d)", "<br>$1");

        return Collections.singletonList(formattedText);
    }
}
