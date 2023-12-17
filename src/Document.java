import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.*;
/**
 * The Document class represents a text document and provides functionality to process
 * and manipulate its content. It includes methods for reading from a file, counting words
 * and characters, and saving changes back to a file.
 */

public class Document {
    private int characterCounts;
    private int characterCountsWithSpace;
    private int lineCounts;
    private int wordCounts;
    private File currentFile;
    private List<String> words;
    private String[] arrayList;
    public List<String> allElements;
    public List<String> original;
    
    /**
     * Constructs a new Document with initial values.
     */
    public Document() {
        this.characterCounts = 0;
        this.characterCountsWithSpace = 0;
        this.lineCounts = 0;
        this.wordCounts = 0;
        this.words = new ArrayList<>();
        this.arrayList = new String[5];
        this.allElements = new ArrayList<>();
        this.original = new ArrayList<>();
    }

    
    /**
     * Reads and processes the document content from a given string.
     * @param content The string content to be processed.
     */
    public void readFromString(String content) {
        // Assume the entire string is one line for simplicity.
        this.lineCounts = 1;

        words.clear(); // Clear any existing words
        words.addAll(splitIntoWords(content, false)); // Collect words from the string

        // Count characters excluding spaces
        characterCounts = (int) content.chars().filter(c -> !Character.isWhitespace(c)).count();

        // Count characters including spaces
        characterCountsWithSpace = content.length();

        wordCounts = words.size(); // Update the word count based on the collected words
    }

    // use to test
    /**
     * Returns the list of words in the document.
     * @return A list of words in the document.
     */
    public List<String> DocPrintWord() {
        return words;
    }
    
    /**
     * Returns the count of characters in the document, excluding spaces.
     * @return The character count excluding spaces.
     */
    public int DocCharacterCounts() {
        return characterCounts;
    }
    /**
     * Returns the count of words in the document
     * @return The word count
     */
    public int DocWordCounts() {
        return wordCounts;
    }
    
    /**
     * Returns the count of characters in the document, with spaces.
     * @return The character count with spaces.
     */
    public int DocCharacterCountsWithSpace() {
        return characterCountsWithSpace;
    }
    /**
     * Returns the count of lines in the document.
     * @return number of lines in document
     */
    public int DocLineCounts() {
        return lineCounts;
    }
    /**
     * Opens txtfile 
     * @param filePath textfilePath that user select 
     * @throws IOException
     */
    public void DOCopenFile(String filePath) throws IOException {
        // open another file,
        // Warn the user if there are unsaved changes
        /////////////////////////////////////////////////// need to change later.


        currentFile = new File(filePath);
        if (!currentFile.exists()) {
            throw new FileNotFoundException("The file at the specified path does not exist: " + filePath);
        }

        // Read the file and update the counts
        readAndProcessFile();

    }

    /**
     * method to parse txt file and get some counts
     * @throws IOException
     */
    private void readAndProcessFile() throws IOException {
        List<String> lines = Files.readAllLines(currentFile.toPath(), StandardCharsets.UTF_8);
        lineCounts = lines.size();

        StringBuilder fileContent = new StringBuilder();
        words.clear(); // Clear previous words
        allElements.clear(); // Clear previous allElements

        characterCountsWithSpace = 0; // Reset the character count with spaces
        characterCounts = 0; // Reset the character count without spaces
        int arrayListIndex = 0;
        
        for (String line : lines) { // loop through each line in the document
            fileContent.append(line).append(System.lineSeparator());
            words.addAll(splitIntoWords(line, false)); // Collect words from each line
            
//            if (arrayList.length < words.size()) {
//                setNewCapacity(words.size(), arrayList.length);
//            }
            
            Matcher matcher = Pattern.compile("(\\b\\w+\\b|[\\s\\p{Punct}]+)").matcher(line);
            while (matcher.find()) {
                String element = matcher.group();
                allElements.add(element);  // Add both words and separators to allElements
//                if (element.trim().length() > 0) {  // Only add words to arrayList
//                    //arrayList[arrayListIndex++] = element;
//                    if (arrayListIndex == arrayList.length) {
//                        setNewCapacity(arrayListIndex * 2, arrayListIndex);
//                    }
//                }
            }

            // this loop creates a list for spell checker so words are formated properly
            List<String> temps = new ArrayList<>();
            temps.addAll(splitIntoWords(line, true));
            for (int i = 0; i < temps.size(); i++) {
                if (arrayListIndex == arrayList.length) {
                    setNewCapacity(arrayList.length * 2, arrayListIndex);
                }
                arrayList[arrayListIndex++] = temps.get(i);
            }
            temps.clear();
            // Add newline character at the end of each line
            allElements.add(System.lineSeparator());

            // Update character counts with and without spaces
            characterCountsWithSpace += line.length() + System.lineSeparator().length();
            characterCounts += line.chars().filter(c -> !Character.isWhitespace(c)).count();

            List<String> getLineAllSplitUp = splitIntoWords1(line);
            original.addAll(getLineAllSplitUp);

        }

        // Subtract the extra line separator count from the end
        characterCountsWithSpace -= System.lineSeparator().length();

        wordCounts = words.size(); // Update the word count based on the collected words
    }

    public List<String> getStrings(String str) {
            List<String> getLineAllSplitUp = splitIntoWords1(str);
            original.addAll(getLineAllSplitUp);
            return getLineAllSplitUp;
    }
    /**
     * Splits a given string into words based on specific delimiters.
     * @param content content The string to be split into words.
     * @param flag 
     * @return  A list of words obtained from splitting the content.
     */
    public List<String> splitIntoWords(String content, boolean flag) {
        String str = null;
        if (flag) {
            str = "[\\-\\\\'\\=+*&/<>;:_{}|,@\\)\\(\\];]";
        } else {
            str = "[\\-\\.\\!\\?=+*&/<>;:{}|,@\\)\\(\\];]";
        }

        List<String> matchedWords = new ArrayList<>();
        // Split content at spaces to get potential words or numbers
        String[] parts = content.split("\\s+");

        // Process each part
        for (String part : parts) {
            // Split part at hyphens and periods regardless of what characters are around them
            String[] subParts = part.split(str);

            // Add non-empty subparts to the list
            for (String subPart : subParts) {
                if (!subPart.isEmpty()) {
                    matchedWords.add(subPart);
                }
            }
        }
        return matchedWords;
    }

    /**
     * Splits a given string into words and special characters.
     * @param content The string to be split into words and special characters.
     * @return A list of words and special characters obtained from splitting the content.
     */
    public List<String> splitIntoWords1(String content) {
        List<String> matchedWords = new ArrayList<>();
        int spaceCount = 0;
        String string = "";
        String space = "";

        char[] array = content.toCharArray();
        for (int i = 0; i < array.length; i++) {
            char c = array[i];
            if (!Character.isLetter(c) && c != '.' && c != '!' && c != '?' && !Character.isDigit(c)) {
                spaceCount++;
                if (spaceCount > 0) {
                    matchedWords.add(string);
                    string = "";
                }
                space += c;
            } else {
                if (spaceCount > 0) {
                    matchedWords.add(space);
                    space = "";
                }
                string += c;
            }
            if (i == array.length - 1) {
                if (string != "") {
                    matchedWords.add(string);
                } else if (space != "") {
                    matchedWords.add(space);
                }
            }
        }

        matchedWords.add("\n");
        return matchedWords;
    }

    /**
     * Saves the document content to a specified file path or overwrites the original file if no path is provided.
     * @param outputPath The path where the file content is to be saved. If null, the original file is overwritten.
     * @throws IOException If an I/O error occurs while writing the file.
     */
    public void saveFile(String outputPath) throws IOException {
        if (currentFile == null) {
            throw new IllegalStateException("No file is currently loaded to save.");
        }

        // Convert the list of words back into the file content with proper newlines
        String contentToSave = String.join(" ", words);

        if (outputPath != null && !outputPath.isEmpty()) {
            // Save to a new location
            Files.writeString(Paths.get(outputPath), contentToSave, StandardCharsets.UTF_8);
        } else {
            // Overwrite the existing file
            Files.writeString(currentFile.toPath(), contentToSave, StandardCharsets.UTF_8);
        }
    }
    /**
     * Filters the words in the document based on the specified criteria.
     * @param filterTags If true, filters out HTML-like tags from the document words.
     * @return A list of filtered words from the document.
     */
    public List<String> wordList(boolean filterTags) {
        if (filterTags) {
            return words.stream()
                    .filter(word -> !word.matches("<.*?>")) // Exclude HTML-like tags
                    .collect(Collectors.toList());
        }
        return new ArrayList<>(words);
    }
    
    /**
     * Returns a copy of the internal array containing words of the document.
     * @return A copy of the internal array of words.
     */
    public String[] getArrayList() {
        int nonNullCount = 0;
        for (String element : arrayList) {
            if (element != null) {
                nonNullCount++;
            }
        }

        trimToSize(nonNullCount);
        return arrayList;
    }

    public void trimToSize(int newSize) {
        if (newSize != arrayList.length) {
            setNewCapacity(newSize, newSize);
        }
    }

    /**
     * Resizes the internal array to the specified new size.
     * @param newSize The new size of the internal array.
     */
    private void setNewCapacity(int newCapacity, int j) {
        String[] temp = (arrayList.length != 0)
                ? new String[newCapacity]
                : new String[5];
        for (int i = 0; i < j; i++) {
            temp[i] = arrayList[i];
        }
        arrayList = temp;
    }
    
    /**
     * prints arraylist
     */
    public void printArrayList() {
        System.out.println("Contents of arrayList:");
        for (String element : arrayList) {
            if (element != null) {
                System.out.print("["+element + "]");
            }
        }
        System.out.println(); // Adds a newline at the end for better formatting
    }
    
    
    
    public static void main(String[] args) {
        Document doc = new Document();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the file path to process:");
        String filePath = "/Users/ouakiraakira/Desktop/aaahello.txt";

        try {
            doc.DOCopenFile(filePath);

            // Output the results
            System.out.println("Number of characters (excluding spaces): " + doc.characterCounts);
            System.out.println("Number of lines: " + doc.lineCounts);
            System.out.println("Number of words: " + doc.wordCounts);
            System.out.println("Processed words: " + doc.words);
            System.out.print("////");
            doc.printArrayList();
            String[] stt = doc.getArrayList();
            for (int i =0; i < stt.length; i++)
            {
            	//System.out.print(stt[i] + "/");
            }
            
            System.out.print("////");
            // Print out the original file content using allElements
            System.out.println("Original file content:");
            for (String element : doc.allElements) {
                System.out.print(element);
            }


        } catch (IOException e) {
            System.out.println("An error occurred while opening the file: " + e.getMessage());
        } finally {
            scanner.close(); // Close the scanner
        }
    }



}
