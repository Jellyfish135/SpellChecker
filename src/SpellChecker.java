import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * @author Robert Biernacki
 * This class represents the spell-checking process of program
 * Creates a list of words from document and attaches error codes
 * Used by GUI to indicate what to underline as an error
 */
public class SpellChecker {
    private String[] document; // source of words to be corrected
    private ArrayList<correctionData> data;
    MainDictionary mainDictionary = new MainDictionary();
    UserDictionary userDictionary = new UserDictionary();
    private int misSpellingCounts;
    private int misCapitalizationCounts;
    private int doubleWordErrorCounts = 0;
    private static int acceptedSuggestionCounts = 0;
    private static int manualCorrectionsCounts = 0;
    private static int wordDeletionCounts = 0;
    private int numbOfErrors;
    private List<String> errorWords = new ArrayList<>();

    /**
     * constructor
     * @param doc an array of words that will be checked for spelling
     */
    public SpellChecker(String[] doc) {
        document = doc;
        data = new ArrayList<>();
        this.misCapitalizationCounts = 0;
        this.numbOfErrors = 0;

    }

    /**
     * get the list of error words
     * @param words list of words to compare
     * @return words that has an error
     */
    public List<String> getErrorWords(String[] words) {
        for (String word : words) {
        	String processedWord = word.replaceAll("^\\W+|\\W+$", "");
            if (!mainDictionary.contains(processedWord)) {
                errorWords.add(processedWord);
                misSpellingCounts++;
                numbOfErrors++;
            }
        }
        return errorWords;
    }
    
    

    /** @return counted misSpellingCounts */
    public int getMisSpellingCounts() {
        return misSpellingCounts;
    }
    
    /** @return counted misSpellingCounts */
    public int getNumbofErrors() {
        return numbOfErrors;
    }
    /**
     * Accessor method to get miscaplitalization counts
     * @return number of miscaplization
     */
    public int getMisCapitalizationCounts() {
        return misCapitalizationCounts;
    }
    
    /**
     * Adds a word to the error words list
     * @param word The word to add
     */
    private void addErrordWord(String word) {
        if (!errorWords.contains(word)) { // Avoid duplicates
            errorWords.add(word);
        
        }
    }

    /**
     * Main function of spell-checker that goes through the input string array of
     * Words and creates an array list that holds error codes, original words, and
     * Tree set of suggested correction
     */
    public void checkWord() {
        raw_string prev = null;
        String str1, str2; // used for testing double words
        boolean shouldBeCaps = false; // used for making correction with first letter being caps
        // main loop that goes through entire string of words from document
        for (int i = 0, results = 0; i < document.length; i++) {
            raw_string str = new raw_string(document[i]); // process each string
            correctionData tag = new correctionData();   // create an error tag

           boolean pierodInWord = otherCase(new String(str.charArray), tag);

            boolean titles = specialCases(new String(str.charArray), tag);
            char firstLetter = str.getFirstChar();
            if (!titles && !pierodInWord) { // if false and false
                if (prev != null) { // checks to see if this is the first word in document
                    boolean isCaps = (firstLetter >= 'A' && firstLetter <= 'Z');
                    char punctuation = prev.getLastChar();
                    if (punctuation == '.' || punctuation == '?' || punctuation == '!') { // check to see if the previous word ended in punctuation
                        //char firstLetter = str.getFirstChar();
                        if (!isCaps) { // if first letter not in caps
                            results += 1; // error found
                            addErrordWord(document[i]);
                            numbOfErrors++;
                            misCapitalizationCounts++;
                        }
                        shouldBeCaps = true;
                    } else if (isCaps) { // if first letter is in caps and previous word did not end in punctuation
                        results += 4;
                        numbOfErrors++;
                        misCapitalizationCounts++;
                    }
                } else { // first word of document must start with caps
                    if (!(firstLetter >= 'A' && firstLetter <= 'Z')) {
                        results += 1; // error found
                        numbOfErrors++;
                        misCapitalizationCounts++;
                    }
//                   shouldBeCaps = (punctuation == '.' || punctuation == '?' || punctuation == '!'); 
                    shouldBeCaps = true;
                }
            } else {
                results = 100;
            }

            // dupe words
            // they. They would not an error
            if (prev != null && (str2 = new String(str.charArray)).equals(str1 = new String(prev.charArray))) {
                prev = str;
                results = 7;
            } else if (!titles && !pierodInWord) {
                raw_string format = null;
                prev = new raw_string(new String(str.charArray));;
                // format the string
                char punctuation = str.getLastChar();
                if ((punctuation == '.' || punctuation == '?' || punctuation == '!')) {
                    format = new raw_string(new String(str.charArray));
                    format.removeLastChar();
                    format.trimToSize();
                } else {
                    format = str;
                }
                boolean mixNumbersFlag = numbersInWordsCorrection(format, tag, shouldBeCaps);
                if (mixNumbersFlag) {
                    results += 8;
                } else {
                    if (!userDictionary.contains(new String(format.charArray))) {
                        boolean spellingFlagMixCaps = miscapitalizationCorrection(format, tag, shouldBeCaps);
                        if (spellingFlagMixCaps) {
                            if (results == 4) {
                                results = 10;
                            } else {
                                results += 10;
                            }
                        }
                    }
                    String s = new String(format.charArray);
                    if (!(mainDictionary.contains(s.toLowerCase()) || userDictionary.contains(s.toLowerCase()) || userDictionary.contains(s))) {
                        boolean missSpellFlag = misspellingCorrection(s, tag, shouldBeCaps);
                        if (missSpellFlag) {
                            results += 15;
                        }
                    }
                }
            }

            /** be sure that i is corrected to I */
            tag.setOrginalWord(document[i]);
            String string;
            switch (results) {
                case 1: // caps error
                    if (tag.getCorrections() == null) {
                        str.set(0, Character.toUpperCase(firstLetter));
                        tag.addCorrection(string = new String(str.charArray));
                    }
                    tag.setError(1);
                    break;
                case 2: // caps and num in word error

                    break;
                case 3: // caps and spell error
                    break;
                case 4:
                    if (str.charCount > 1 && !Character.isUpperCase(str.charArray[1])) {
                        str.set(0, Character.toLowerCase(firstLetter));
                        tag.addCorrection(string = new String(str.charArray));
                        tag.setError(4);
                    }
                    break;
                case 7:
                    tag.addCorrection("");
                    tag.setError(7);
                    break;
                case 8:
                    tag.setError(8);
                    break;
                case 9:
                    tag.setError(9);
                    break;
                case 10:
                    tag.setError(10);
                    break;
                case 11:
                    tag.setError(11);
                    break;
                case 15:
                    tag.setError(15);
                    break;
                case 16:
                    tag.setError(16);
                    break;
                case 25:
                    tag.setError(25);
                    break;
                default:
                    break;
            }
            shouldBeCaps = false;
            data.add(tag);
            results = 0;
        }
    }

    /**
     * For testing purposes to display all words from input array
     * in command line and show its error code and suggested correction
     */
    public void displayErrors() {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
        for (int i = 0; i < data.size(); i++) {
            System.out.println(data.get(i).toStirng());
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
        }
    }

    /**
     * method for returning the array listed created in spellcheck() method
     * @return ArrayList<correctionData>
     */
    public ArrayList<correctionData> suggestCorrection() {
        return data;
    }

    /**
     * This method tries to find possible corrections of the word. This method is
     * called upon if word was not found in either of the user or main dictionary.
     * @param word the String to check for spelling
     * @param e the container where we store the possible corrections
     * @param shouldBeCaps a flag that indicates possible corrections must start with caps letter
     * @return boolean true to indicate if possible corrections were found else false
     */
    private boolean misspellingCorrection(String word, correctionData e, boolean shouldBeCaps) {
        int numFoundErrors = (e.getCorrections() != null) ? e.getCorrections().size() : 0;
        char alphabet[] = mainDictionary.getAlphabet();
        raw_string str = new raw_string(word.toLowerCase()); // better version of StringBuffer and StringBuilder
        String string;

        // This function tries to omit (in turn, one by one) a single character in the misspelled word
        if (str.charCount != 1) {
            for (int i = 0; i < str.charCount; i++) {
                char deletedChar = str.removeAtIndex(i);
                if (mainDictionary.contains(string = new String(str.getCharArray(), 0, str.charCount - 1)) || userDictionary.contains(string = new String(str.getCharArray(), 0, str.charCount - 1))) {
                    if (shouldBeCaps) {
                        String s = string.substring(0, 1).toUpperCase() + string.substring(1);
                        e.addCorrection(s);
                    } else {
                        e.addCorrection(string);
                    }
                }
                str.addAtIndex(i, deletedChar);
            }
        }

        // This function tries to insert a letter in the misspelled word
        for (int i = 0; i < str.getCharCount() + 1; i++) {
            for (int j = 0; j < alphabet.length; j++) {
                str.addAtIndex(i, alphabet[j]);
                if (mainDictionary.contains(string = new String(str.getCharArray())) || userDictionary.contains(string = new String(str.getCharArray()))) {
                    if (shouldBeCaps) {
                        String s = string.substring(0, 1).toUpperCase() + string.substring(1);
                        e.addCorrection(s);
                    } else {
                        e.addCorrection(string);
                    }
                }
                str.removeAtIndex(i);
            }
        }

        str.trimToSize(); // last function broke the string, and it ends with an invalid null >:(

        //This function tries swapping every pair of adjacent characters
        for (int i = 0; i < str.charCount - 1; ++i) {
            str.swap(i, i + 1);
            if (mainDictionary.contains(string = new String(str.getCharArray())) || userDictionary.contains(string = new String(str.getCharArray()))) {
                if (shouldBeCaps) {
                    String s = string.substring(0, 1).toUpperCase() + string.substring(1);
                    e.addCorrection(s);
                } else {
                    e.addCorrection(string);
                }
            }
            str.swap(i, i + 1);
        }

        // Inserting a space or hyphen
        if (str.charCount > 1) {
            for (int i = 1; i < str.charCount; i++) {
                str.addAtIndex(i, ' ');
                String string1 = new String(str.getCharArray(), 0, i);
                String string2 = new String(str.rightSplit(i + 1));
                boolean flag1 = (mainDictionary.contains(string1) || userDictionary.contains(string1));
                boolean flag2 = (mainDictionary.contains(string2) || userDictionary.contains(string2));
                if (flag1 && flag2) {
                    String space = string1 + " " + string2;
                    if (shouldBeCaps) {
                        String s = space.substring(0, 1).toUpperCase() + space.substring(1);
                        e.addCorrection(s);
                    } else {
                        e.addCorrection(space);
                    }
                    String hyphen = string1 + "-" + string2;
                    if (shouldBeCaps) {
                        String s = hyphen.substring(0, 1).toUpperCase() + hyphen.substring(1);
                        e.addCorrection(s);
                    } else {
                        e.addCorrection(hyphen);
                    }
                } else if (flag1) {
                    if (shouldBeCaps) {
                        String s = string1.substring(0, 1).toUpperCase() + string1.substring(1);
                        e.addCorrection(s);
                    } else {
                        e.addCorrection(string1);
                    }
                } else if (flag2) {
                    if (shouldBeCaps) {
                        String s = string2.substring(0, 1).toUpperCase() + string2.substring(1);
                        e.addCorrection(s);
                    } else {
                        e.addCorrection(string2);
                    }
                }
                str.removeAtIndex(i);
                str.trimToSize();
            }
        }
        // This function takes in a string word and tries to correct the spelling by substituting letters and
        //check if the resulting new word is in the dictionary.
        for (int i = 0; i < str.charCount; i++) {
            char orignalChar = str.getCharAtIndex(i);
            // loop through each letter in the alphabet to be substituted at index i
            for (int j = 0; j < alphabet.length; j++) {
                str.set(i, alphabet[j]);
                if (mainDictionary.contains(string = new String(str.getCharArray())) || userDictionary.contains(string = new String(str.getCharArray()))) {
                    if (shouldBeCaps) {
                        String s = string.substring(0, 1).toUpperCase() + string.substring(1);
                        e.addCorrection(s);
                    } else {
                        e.addCorrection(string);
                    }
                }
            }
            str.set(i, orignalChar);
        }

        return (numFoundErrors < ((e.getCorrections() != null) ? e.getCorrections().size() : 0)); // will returns true of we find some new correction
    }

    /**
     * method checks words for mix caps errors
     * @param word the String to check for spelling that is of type raw_string
     * @param e the container where we store the possible corrections
     * @param shouldBeCaps a flag that indicates possible corrections must start with caps letter
     * @return boolean true to indicate if possible corrections were found else false
     */
    private boolean miscapitalizationCorrection(raw_string word, correctionData e, boolean shouldBeCaps) {
        int numFoundErrors = (e.getCorrections() != null) ? e.getCorrections().size() : 0;
        raw_string str = word;
        String string;

        //Mixed capitalization within a word. does not check first char EX: "Dog" the letter D will not be looked at
        // will ignore all caps words such as Ex: "ASAP"
        char[] charArray = str.getCharArray();
        for (int allUpper = 0, i = 1; i < str.charCount; i++) {
            // this only checks English characters
            if ((charArray[i] >= 'a') && (charArray[i] <= 'z')) {
                if (allUpper >= 1 && allUpper != i) {
                    string = new String(str.getCharArray()).toLowerCase();
                    if (shouldBeCaps) {
                        String s = string.substring(0, 1).toUpperCase() + string.substring(1);
                        e.addCorrection(s);
                    } else {
                        e.addCorrection(string);
                    }
                }
            } else {
                allUpper++;
                if ((allUpper != i) || !(charArray[0] >= 'A' && charArray[0] <= 'Z')) { //!Character.isUpperCase(charArray[0])) {
                    string = new String(str.getCharArray()).toLowerCase();
                    if (shouldBeCaps) {
                        String s = string.substring(0, 1).toUpperCase() + string.substring(1);
                        e.addCorrection(s);
                    } else {
                        e.addCorrection(string);
                    }
                }
            }
        }

        return (numFoundErrors < ((e.getCorrections() != null) ? e.getCorrections().size() : 0)); // will returns true of we find some new correction
    }

    /**
     * method to check spelling for special cases
     * @param word the String to check for spelling
     * @param e the container where we store the possible corrections
     * @return boolean true to indicate if possible corrections were found else false
     */
    public boolean specialCases(String word, correctionData e) {
        String check = word.toLowerCase();
        boolean flag = false;
        switch (check) {
            case "mr": specialCasesHelper(word, e); flag = true; break;
            case "mrs": specialCasesHelper(word, e); flag = true; break;
            case "miss": specialCasesHelper(word, e); flag = true; break;
            case "ms": specialCasesHelper(word, e); flag = true; break;
            case "dr": specialCasesHelper(word, e); flag = true; break;
            case "prof": specialCasesHelper(word, e); flag = true; break;
            case "mr.": specialCasesHelper(word, e); flag = true; break;
            case "mrs.": specialCasesHelper(word, e); flag = true; break;
            case "miss.": specialCasesHelper(word, e); flag = true; break;
            case "ms.": specialCasesHelper(word, e); flag = true; break;
            case "dr.": specialCasesHelper(word, e); flag = true; break;
            case "prof.": specialCasesHelper(word, e); flag = true; break;
            default: break;
        }
        return flag;
    }

    /**
     * helper method of special cases
     * @param word the String to check for spelling
     * @param e the container where we store the possible corrections
     * @return boolean true if correction were added
     */
    private boolean specialCasesHelper(String word, correctionData e) {
        boolean flag = false;
        raw_string cor = new raw_string(word);
        if (!(cor.charArray[0] >= 'A' && cor.charArray[0] <= 'Z')) {
            cor.charArray[0] = Character.toUpperCase(cor.charArray[0]);
            flag = true;
        }
        if (cor.getLastChar() != '.') {
            cor.addAtIndex(cor.charCount, '.');
            flag = true;
        }	
        if (flag) {
            e.addCorrection(new String(cor.charArray));
        }
        return flag;
    }

    /**
     * method to check spelling of words with periods in the middle of it.
     * Ex: google.com would be split by the period and check "google" and "com"
     * @param word the String to check for spelling
     * @param tag the container where we store the possible corrections
     * @return boolean true to indicate if possible corrections were found else false
     */
    public boolean otherCase(String word, correctionData tag) {
        raw_string str = new raw_string(word);
        boolean wasItOtherCase = false;
        int num = str.firstIndexOf('.');
        if (num != 0 && num != word.length() - 1 && num != -1) {
            wasItOtherCase = true;
            String[] temp = str.split('.');
            for (int i = 0; i < temp.length; i++) { //loop each sub word;
                boolean flag1, flag2, flag3;
                correctionData tempTag = new correctionData();
                if (mainDictionary.contains(temp[i].toLowerCase()) || userDictionary.contains(temp[i].toLowerCase())) {
                    flag1 = false;
                } else {
                    flag1 = misspellingCorrection(temp[i], tempTag, false);
                }
                flag2 = numbersInWordsCorrection(new raw_string(temp[i]), tempTag, false);
                flag3 = miscapitalizationCorrection(new raw_string(temp[i]), tempTag, false);
                if (flag1 || flag2 || flag3) {
                    TreeSet<String> posCorr = tempTag.getCorrections();
                    if (posCorr != null) {
                        // loop through list of corrections
                        Object[] tmp = posCorr.toArray();

                        for (int j = 0; j < posCorr.size(); j++) {
                            String corr = "";
                            for (int r = 0; r < temp.length; r++) {
                                if (i == r) {
                                    corr += tmp[j];
                                } else {
                                    corr += temp[r];
                                }
                                if (r != temp.length - 1) {
                                    corr += ".";
                                }
                            }
                            tag.addCorrection(corr);
                        }


                    }
                }
            }
        }
        return  wasItOtherCase;
    }
    /**
     * This method tries to find possible corrections of the word when there are numbers in the string.
     * @param word the String to check for spelling that is of type raw_string
     * @param e the container where we store the possible corrections
     * @param shouldBeCaps a flag that indicates possible corrections must start with caps letter
     * @return boolean indicate if possible corrections were found
     */
    public boolean numbersInWordsCorrection(raw_string word, correctionData e, boolean shouldBeCaps) {
        int numFoundErrors = (e.getCorrections() != null) ? e.getCorrections().size() : 0;
        raw_string str = new raw_string(new String(word.charArray));
        str.toLowerCase();
        String string;
        char alphabet[] = mainDictionary.getAlphabet();

        // words with embedded numbers. Ex: "l00t"
        // find all positions (indexes) of digits in the string and store these positions in the arrayList
        ArrayList<Integer> posOfDigitsInStr = new ArrayList<>();
        for (int i = 0; i < str.charCount; i++) {
            char ch = str.getCharAtIndex(i);
            if (ch >= '0' && ch <= '9') {
                posOfDigitsInStr.add(i);
            }
        }
        if (posOfDigitsInStr.isEmpty()) {
            return false;
        }
        // check if the word consists of all digits
        if (str.charCount == posOfDigitsInStr.size()) {
            return false; // no correction needed
        }
        // by default set all digits to char 'a'
        for (int i = 0; i < posOfDigitsInStr.size(); i++) {
            str.set(posOfDigitsInStr.get(i), 'a');
        }
        if (posOfDigitsInStr.size() != str.getCharCount()) {
            String[] wild_card = getAll(alphabet, posOfDigitsInStr.size());
            for (int i = 0; i < wild_card.length; i++) {
                raw_string temp = new raw_string(wild_card[i]);
                for (int j = 0; j < posOfDigitsInStr.size(); j++) {
                    str.set(posOfDigitsInStr.get(j), temp.charArray[j]);
                }
                if (mainDictionary.contains(string = new String(str.getCharArray())) || userDictionary.contains(string = new String(str.getCharArray()))) {
                    if (shouldBeCaps) {
                        String s = string.substring(0, 1).toUpperCase() + string.substring(1);
                        e.addCorrection(s);
                    } else {
                        e.addCorrection(string);
                    }
                }
            }
        }
        return (numFoundErrors < ((e.getCorrections() != null) ? e.getCorrections().size() : 0));
    }

    /**
     * helper method of numbersInWordsCorrection()
     * @param elements takes in a char array of the alphabet
     * @param lengthOfList an int of the number of letters to check their permutations
     * @return array string of all permutations of the input string
     */
    private String[] getAll(char[] elements, int lengthOfList) {
        String[] allLists = new String[(int)Math.pow(elements.length, lengthOfList)];

        if(lengthOfList == 1) {
            String[] result = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "w", "x", "y", "z" };
            return result;
        } else {
            String[] allSublists = getAll(elements, lengthOfList - 1);
            int arrayIndex = 0;
            for(int i = 0; i < elements.length; i++) {
                for(int j = 0; j < allSublists.length; j++) {
                    if (arrayIndex == allLists.length) {
                        allLists = Arrays.copyOf(allLists, arrayIndex * 2);
                    }
                    allLists[arrayIndex] = elements[i] + allSublists[j];
                    arrayIndex++;
                }
            }
            return allLists;
        }
    }

    /** ************************************
     * C like way of dealing with Strings  *
     * class used to edit and check words  *
     * for corrections                     *
     * *************************************/
    private class raw_string {
        private final int DEFAULT_CAPACITY = 16;
        private int charCount;
        private char[] charArray;

        /** constructor */
        public raw_string(String word) {
            if(word == null) {
                return;
            }
            charCount = word.length();
            charArray = word.toCharArray();
        }

        /** convert all char in array to lowercase */
        public void toLowerCase() {
            for (int i = 0; i < charCount; i++) {
                charArray[i] = Character.toLowerCase(charArray[i]);
            }
        }

        /** inserts a char at the given index */
        public void addAtIndex(int index, char e) {
            checkChar(e);
            checkRange(index);
            if (capacity() == charCount) {
                setNewCapacity(capacity() + 1);
            }
            for (int i = charCount - 1; i >= index; i--) {
                charArray[i + 1] = charArray[i];
            }
            charArray[index] = e;
            charCount = charCount + 1;
        }

        /**
         * deletes a char at the given index
         * @return char was removed from array
         * */
        public char removeAtIndex(int index) {
            checkRange(index);
            char oldValue = charArray[index];
            for (; index <  charCount - 1; index++) {
                charArray[index] = charArray[index + 1];
            }
            charArray[index] = '\0';
            charCount = charCount - 1;
            return oldValue;
        }

        /**
         * remove the last char in the array
         * @return char that was deleted from the end of the array
         * */
        public char removeLastChar() {
            if (!isEmpty()) {
                charCount = charCount - 1;
                char result = charArray[charCount];
                charArray[charCount] = '\0';
                return result;
            }
            return '\0';
        }

        /**
         * returns the char at the given index
         * @param int of index you want to get from array
         * @return char at the input index
         * */
        public char getCharAtIndex(int index) {
            checkRange(index);
            return charArray[index];
        }

        /**
         * returns the first char in the array
         * @return the first char in the array
         * */
        public char getFirstChar() {
            return (isEmpty()) ? null : charArray[0];
        }

        /**
         * returns the last char in the array
         * @return the last char in the array
         * */
        public char getLastChar() {
            return (isEmpty()) ? null : charArray[charCount - 1];
        }

        /**
         * set a new char at the index of old char
         * @param index of where to perform the update of the char
         * @param e new char to set in place of the old char
         * @return the old value that was updated
         * */
        public char set(int index, char e) {
            checkChar(e);
            checkRange(index);
            char oldValue = charArray[index];
            charArray[index] = e;
            return oldValue;
        }

        /**
         * return the index of the first occurrence of char e
         * @param e the target char to find its first occurrence
         * @return int of the index of the first occurrence of char the input char
         * */
        public int firstIndexOf(char e) {
            for (int i = 0; i < charCount; i++) {
                if (e == (charArray[i])) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * return the number of type char in the array
         * @return the number of type char in the array
         * */
        public int getCharCount() {
            return charCount;
        }

        /**
         * return the char array
         * @return the char array
         * */
        public char[] getCharArray() {
            return charArray;
        }

        /**
         * return array length
         * @return int of length of array
         * */
        public int capacity() {
            return charArray.length;
        }

        /**
         *  return ture if char array is empty
         * @return true if empty
         * */
        public boolean isEmpty() {
            return charCount == 0;
        }

        /** cut off unused slots of the char array */
        public void trimToSize() {
            if (charCount != capacity()) {
                setNewCapacity(charCount);
            } else if (isEmpty()) {
                charArray = null;
            }
        }

        /**
         *  perform a right split of the array char and return the results
         * @param i index of where to start the split to the right of the array
         * @return char array of splitting the original array
         * */
        public char[] rightSplit(int i) {
            char[] temp = new char[charCount - i];
            for (int j = 0;i < charCount; ++i, j++) {
                temp[j] = charArray[i];
            }
            return temp;
        }

        /** swap char in place with another char */
        public void swap(int i, int j) {
            checkRange(i);
            checkRange(j);
            uncheckedSwap(i, j);
        }

        /**
         * allows to split a strings from a target char
         * @param target the char you want to split at
         * @return String array of the strings split at target
         */
        public String[] split(char target) {
            int count = 0;
            for (int i = 0; i < charCount; i++) {
                if (charArray[i] == target) {
                    count++;
                }
            }
            String[] temp = new String[count + 1];
            for (int i = 0, j = 0, offset = 0, strIdx = 0; i < charArray.length; i++) {
                if (charArray[i] == target) {
                    temp[j++] = new String(charArray, strIdx, offset);
                    offset = -1;
                    strIdx = i + 1;
                } else if (i == charArray.length - 1) {
                    temp[j++] = new String(charArray, strIdx, (charArray.length) - strIdx);
                }
                offset++;
            }
            return temp;
        }

        /** swap char with another char in a char array */
        private void uncheckedSwap(int i1, int i2) {
            char temp = charArray[i1];
            charArray[i1] = charArray[i2];
            charArray[i2] = temp;
        }

        /** check to ensure char is valid */
        private void checkChar(char letter) {
            if (letter == '\0') {
                throw new IllegalArgumentException("Argument cannot be null.");
            }
        }

        /** ensure index is within char array */
        private void checkRange(int i) {
            if (i > charCount || i < 0) {
                throw new IndexOutOfBoundsException("Index: " + i + ", Size: " + charCount);
            }
        }

        /** increase char array capacity */
        private void setNewCapacity(int newCapacity) {
            char[] temp = (charArray.length != 0)
                    ? new char[newCapacity]
                    : new char[DEFAULT_CAPACITY];
            for (int i = 0; i < this.charCount; i++) {
                temp[i] = charArray[i];
            }
            charArray = temp;
        }

    }// end of raw_string class

}// end of spellchecker class
