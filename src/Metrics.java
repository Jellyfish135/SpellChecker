import java.io.IOException;

/**
 * @author Sungmin Lee
 * Class that will store every counts by getting updates from spell check and MainUI class.
 */
public class Metrics {
	private static int characterCounts = 0;
	private static int lineCounts = 0;
	private static int wordCounts = 0;
	private static int misSpellingCounts = 0;
	private static int misCapitalizationCounts = 0;
	private static int acceptedSuggestionCounts = 0;
	private static int manualCorrectionsCounts = 0;
	private static int characterCountsWithSpace = 0;
	static Document doc;
	
	
	/**
	 * Constructor initialize every counts
	 * @param  the location of the txt.file that the user tries to open
	 * @throws IOException 
	 */
	public Metrics() {
		characterCounts = getCharacterCounts();
        lineCounts = getLineCount();
        wordCounts = getWordCount();
        misSpellingCounts = 0;
        misCapitalizationCounts = 0;
        acceptedSuggestionCounts = 0;
        manualCorrectionsCounts = 0;
        characterCountsWithSpace = getCharacterCountsWithSpace();
    }
	
	/**
	 * Sets misSpellingCounts count
	 * @param count the number of characters that will stored in misSpellingCounts
	 */
	public void setMisSpellingCounts(int count) {
		misSpellingCounts = count;
	}
	
	/**
	 * Sets misCapitalizationCounts count
	 * @param count the number of characters that will stored in misCapitalizationCounts
	 */
	public void setMisCapitalizationCounts(int count) {
		misCapitalizationCounts = count;
	}
	
	/**
	 * Sets character count
	 * @param count the number of characters that will stored in characterCounts
	 */
	public void setCharacterCounts(int count) {
		characterCounts = count;
	}
	
	/**
	 * Sets line count
	 * @param count the number of lines that will stored in lineCounts
	 */
	public void setLineCount(int count) {
		lineCounts = count;
	}
	/**
	 * Sets word count
	 * @param count the number of word that will stored in wordCounts
	 */
	public void setWordCount(int count) {
		wordCounts = count;
	}
	/**
	 * Sets character count with space
	 * @param count the number of character that will stored in characterCountsWithSpace
	 */
	public void setCharacterCountsWithSpace(int count) {
		characterCountsWithSpace = count;
	}
	/**
	 * Accessor method to get the number of characters
	 * @return number of characters without the space
	 */
	public int getCharacterCounts() {
		return characterCounts;
	}
	/**
	 * Accessor method to get the number of lines
	 * @return number of lines
	 */
	public int getLineCount() {
		return lineCounts;
	}
	/**
	 * Accessor method to get the number of words
	 * @return number of words
	 */
	public int getWordCount() {
		return wordCounts;
	}
	/**
	 * Accessor method to get the number of characters including space
	 * @return number of characters with space
	 */
	public int getCharacterCountsWithSpace() {
		return characterCountsWithSpace;
	}
	/**
	 * increase misspelling count
	 */
	public static void incrementMisSpelling() {
		misSpellingCounts++;
	}
	/**
	 * decrease misspelling count
	 */
	public static void decreaseMisSpelling() {
		misSpellingCounts--;
	}
	/**
	 * increase miscapitalization count
	 */
	public static void incrementMisCapitalization() {
		misCapitalizationCounts++;
	}
	/**
	 * decrease miscapitalization couht
	 */
	public static void decreaseMisCapitalization() {
		misCapitalizationCounts--;
	}
	/**
	 * increase manual correction count
	 */
	public static void incrementManualCorrection() {
		manualCorrectionsCounts++;
	}
	/**
	 * increase accepted suggestion count
	 */
	public static void incrementAcceptedSuggestion() {
		acceptedSuggestionCounts++;
	}
	
	
	/**
	 * Report all the counts
	 * @return String contains all the information collected from MainUI and spellchecker class
	 */
	public String generateReport() {
	    StringBuilder report = new StringBuilder("<html>");
	    report.append("Character Count: ").append(characterCounts).append("<br>");
	    report.append("Character Count With Space: ").append(characterCountsWithSpace).append("<br>");
	    report.append("Word Count: ").append(wordCounts).append("<br>");
	    report.append("Line Count: ").append(lineCounts).append("<br>");
	    report.append("MisSpelling Count: ").append(misSpellingCounts).append("<br>");
	    report.append("MisCapitalization Count: ").append(misCapitalizationCounts).append("<br>");
	    report.append("AcceptedSuggestion Count: ").append(acceptedSuggestionCounts).append("<br>");
	    report.append("ManualCorrections Count: ").append(manualCorrectionsCounts).append("<br>");
	    report.append("</html>");
	    return report.toString();
	}


	
}
