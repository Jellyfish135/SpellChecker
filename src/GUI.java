import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;

import java.awt.event.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.NumberFormat.Style;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

// for sound effect.
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This class represents the graphical user interface for a spell checker application.
 * It includes functionalities like loading a text file, displaying its content,
 * highlighting misspelled words, and allowing the user to add words to a custom dictionary.
 * The interface provides options to save changes, generate reports, and access application settings.
 */
public class GUI 
{
	private static JTextArea RightDisplayArea = new JTextArea();  // the area under Button Rec../ User's ...
	private static JTextArea COUNTdisplayArea = new JTextArea();
	private static JScrollPane countScrollPane;
    private static JTextArea TXTdisplayArea = new JTextArea();    // the area to Show contents of .txt file.
  	private static JScrollPane TXTscrollPane = null;
	private static ImageIcon icon = new ImageIcon("icon/xx.png");
	private static File currentFile = null;
	private static JLabel fileIconLabel = new JLabel(icon);
	private static JLabel insertTxtLabel = new JLabel("Click to insert valid txt file");
	private static JLabel welcomeLabel = new JLabel("Welcome to spell checker!");
	private static JLabel welcomeLabel1 = new JLabel("Insert a valid txt file to start.");
	private static UserDictionary myDictionary;
	private static Document document;
	public static Metrics metrics = new Metrics();
	private static File selectedFile; // selectedFile.getAbsolutePath().
	private static boolean HighLightAllowedRightArea = false;
	private static boolean ShowUserDic = false;
	private static boolean ShowRecommendedWord = false;
	static Housekeeping housekeeping = new Housekeeping(false,false,false);
	private static JTextPane textPane; // JTextPane for displaying and highlighting text
    private static SpellChecker spellChecker; 
    private static String currentSelectedWord = null;
    private static JButton recommendedWordBtn;
    
	
    /**
     * Saves the contents of the provided JTextPane to a file chosen by the user.
     * Displays a file chooser dialog to select the save location and file name.
     * If an error occurs during saving, an error message is displayed.
     *
     * @param textPane The JTextPane containing text to be saved.
     * @param frame    The JFrame from which the file chooser dialog is displayed.
     */
    public static void saveToFile(JTextPane textPane, JFrame frame) 
	{	
		if (!housekeeping.isFileCompatible())
		{
			frame.remove(welcomeLabel);
	        frame.remove(welcomeLabel1);
	        playSound("soundEffect/error.wav"); // play error sound.
	        COUNTdisplayArea.setFont(new Font("Arial", Font.PLAIN, 25)); // font
	        COUNTdisplayArea.setText("Error! Please insert a valid .txt file.");
	        frame.add(COUNTdisplayArea);
	        frame.revalidate();  // Refresh the container hierarchy
	        frame.repaint();     // Repaint the frame to show updated components
	        return;
		}
		
		 JFileChooser fileChooser = new JFileChooser();
		    
		    // Retrieve the default save location from the Settings class
		    Settings settings = new Settings();
		    String defaultSaveLocation = settings.getDefaultSaveLocation();
		    if (!defaultSaveLocation.isEmpty()) {
		        fileChooser.setCurrentDirectory(new File(defaultSaveLocation));
		    }

		    fileChooser.setDialogTitle("Specify a file to save");
		    
		    // Set up a filter for .txt files
		    FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
		    fileChooser.setFileFilter(filter);
		    
		    int userSelection = fileChooser.showSaveDialog(frame);
		    
		    if (userSelection == JFileChooser.APPROVE_OPTION) {
		        File fileToSave = fileChooser.getSelectedFile();
		        
		        // Ensure the file has a .txt extension
		        if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
		            fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
		        }
		        
		        try {
		        	String text = textPane.getText();

		            Files.write(fileToSave.toPath(), text.getBytes(StandardCharsets.UTF_8));
		            housekeeping.setSavedBeforeExiting(true);
		        } catch (IOException ex) {
		            ex.printStackTrace();
		            JOptionPane.showMessageDialog(frame, "Error saving file!", "Error", JOptionPane.ERROR_MESSAGE);
		        }
		    }
	}

    /**
     * Saves the current content of the JTextPane to the currently opened file.
     * If no file is currently open, it invokes the saveToFile method to save to a new file.
     * Displays error messages if an invalid file is detected or if an error occurs during saving.
     *
     * @param textPane The JTextPane containing text to be saved.
     * @param frame    The JFrame for displaying error messages or file chooser dialog.
     */
    public static void saveToCurrentFile(JTextPane textPane, JFrame frame) 
	{
		if (!housekeeping.isFileCompatible())
		{
			frame.remove(welcomeLabel);
	        frame.remove(welcomeLabel1);
	        playSound("soundEffect/error.wav"); // play error sound.
	        COUNTdisplayArea.setFont(new Font("Arial", Font.PLAIN, 25)); // font
	        COUNTdisplayArea.setText("Error! Please insert a valid .txt file.");
	        frame.add(COUNTdisplayArea);
	        frame.revalidate();  // Refresh the container hierarchy
	        frame.repaint();     // Repaint the frame to show updated components
	        return;
		}
		
	    if (currentFile != null) 
	    {
	        try 
	        {
	        	String text = textPane.getText();

	        	 Files.write(currentFile.toPath(), text.getBytes(StandardCharsets.UTF_8));
	             housekeeping.setSavedBeforeExiting(true);
	        } catch (IOException ex) 
	        {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(frame, "Error saving file!", "Error", JOptionPane.ERROR_MESSAGE);
	        }
	    } else 
	    {
	        // If no file has been opened/saved yet, then open save dialog
	        saveToFile(textPane, frame);
	    }
	}
	 
    /**
     * Plays a sound effect from the specified file.
     * The method handles the opening and playing of the sound file.
     * If the sound file is incompatible or an error occurs, it prints the stack trace.
     *
     * @param soundFileName The file path of the sound effect to be played.
     */
	public static void playSound(String soundFileName) 
	{
	    try {
	        // Open an audio input stream.
	    	AudioInputStream audioIn = AudioSystem.getAudioInputStream(new File(soundFileName));
	        // Get a sound clip resource.
	        Clip clip = AudioSystem.getClip();
	        // Open audio clip and load samples from the audio input stream.
	        clip.open(audioIn);
	        
	        // Get the gain control from clip
	        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
	        // Reduce volume to approximately half
	        float volumeReduction = -26.0f;
	        gainControl.setValue(volumeReduction);

	        
	        clip.start();
	    } catch (UnsupportedAudioFileException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (LineUnavailableException e) {
	        e.printStackTrace();
	    }
	}

	/**
	 * Removes specific labels from the given JFrame.
	 * This method is typically used to clear the interface in preparation for displaying new content.
	 *
	 * @param frame The JFrame from which the labels will be removed.
	 */
    private static void removeLabels(JFrame frame) 
    {
        frame.remove(fileIconLabel);
        frame.remove(insertTxtLabel);
        frame.remove(welcomeLabel);
        frame.remove(welcomeLabel1);
        frame.revalidate();
        frame.repaint();
    }
    

    /**
     * Highlights misspelled words in the JTextPane.
     * This method processes the text in the JTextPane and applies a specific style to misspelled words.
     * The style includes underlining and coloring of misspelled words.
     */
    private static void highlightMisspellings(SpellChecker spellChecker) {
        // Get the text from textPane
        String text = textPane.getText();

        boolean filterTags = Settings.isTagFilterActive();

        String processedText;
        if (filterTags) {
            processedText = text.replaceAll("<[^>]*>", " "); // Remove HTML tags
        } else {
            processedText = text; // Use the original text
        }

        // Updated regex to split text into words, handling cases like 'UI' better
        String[] words = processedText.split("\\W+"); // Split the text on non-word characters

        System.out.println("Words extracted: "); // Logging the extracted words

        // Log each word for debugging
        for (String word : words) {
            System.out.println(word);
        }

        List<String> errorWords = spellChecker.getErrorWords(words);
        System.out.println("Error Words: ");
        for (String errorWord : errorWords) {
            System.out.println(errorWord);
        }
        // Get the StyledDocument of the JTextPane
        StyledDocument doc = textPane.getStyledDocument();

        // Create a style for underlining and coloring
        javax.swing.text.Style style = textPane.addStyle("Underline", null);
        StyleConstants.setUnderline(style, true);
        StyleConstants.setForeground(style, Color.RED);

        // Loop through each misspelled word and apply the style
        for (String word : errorWords) {
            try {
                // Use regex to find the start index of each word, ignoring punctuation
                Pattern pattern = Pattern.compile("\\b" + Pattern.quote(word) + "\\b");
                Matcher matcher = pattern.matcher(text);
                while (matcher.find()) {
                    int start = matcher.start();
                    int length = word.length();
                    doc.setCharacterAttributes(start, length, style, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get recommend words for user's selectedWord
     * 
     * @param spellChecker 
     * @param selectedWord user's selected word using their mouse
     * @return String that contains all the recommended words
     */
    public static String recommendWords(SpellChecker spellChecker, String selectedWord) {
        StringBuilder recommendations = new StringBuilder();
        ArrayList<correctionData> tester = spellChecker.suggestCorrection();

        for (correctionData data : tester) {
            if (data.getOrginalWord().equals(selectedWord)) {
                TreeSet<String> display = data.getCorrections();
                if (display != null && !display.isEmpty()) {
                    recommendations.append("Corrections for '").append(selectedWord).append("':\n");

                    for (String correction : display) {
                        recommendations.append(correction).append("\n"); // Each word on a new line
                    }
                    break;
                }
            }
        }
        return recommendations.toString();
    }
    
    /**
     * Displays the content of a selected text file in the JTextArea.
     * This method allows the user to choose a file, and then it reads and displays its content.
     * It also handles various checks like file emptiness and compatibility.
     *
     * @param TXTdisplayArea   The JTextArea where the file's content is to be displayed.
     * @param COUNTdisplayArea The JTextArea where file statistics are displayed.
     * @param frame            The JFrame used for displaying dialogs and messages.
     */
    public static void displaySelectedFileContent(JTextArea TXTdisplayArea,JTextArea COUNTdisplayArea, JFrame frame) 
    {
    	
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) 
        {
        	currentFile = fileChooser.getSelectedFile();
            selectedFile = fileChooser.getSelectedFile();
            System.out.println("Selected file: " + selectedFile.getAbsolutePath());

            // Read the contents of the file and set to displayArea
            try {
            	if(selectedFile.length() == 0) {
            		 JOptionPane.showMessageDialog(frame, "The selected file is empty.", "Empty File", JOptionPane.INFORMATION_MESSAGE);
            	}
                frame.add(COUNTdisplayArea);
                
                // not .txt file
        	   if (!selectedFile.getName().toLowerCase().endsWith(".txt")) 
        	   {
        	        frame.remove(welcomeLabel);
        	        frame.remove(welcomeLabel1);
        	        playSound("soundEffect/error.wav"); // play error sound.
        	        COUNTdisplayArea.setFont(new Font("Arial", Font.PLAIN, 25)); // font
        	        COUNTdisplayArea.setText("Error! Please select a valid .txt file.");
        	        frame.revalidate();  // Refresh the container hierarchy
        	        frame.repaint();     // Repaint the frame to show updated components
        	        return;
        	    }

        	   ////////////
        	   //Document//
        	   ////////////
        	   	document = new Document();
        	    document.DOCopenFile(selectedFile.getAbsolutePath());

        	    int DocChar = document.DocCharacterCounts();
        	    int DocCharWithSpace = document.DocCharacterCountsWithSpace();
        	    int DocLine = document.DocLineCounts();
        	    int DocWord = document.DocWordCounts();
        	    

        	    ///////////////////////////
        	    /// Load User Dictionary
        	    ///////////////////////////
        	    myDictionary = new UserDictionary();
        	    String[] doc = document.getArrayList();
        	    spellChecker = new SpellChecker(doc);
        	    
            
        	   	// here, we can read the .txt file.
        	    housekeeping.setCompatible(true);
        	    removeLabels(frame);
                String content = new String(Files.readAllBytes(Paths.get(selectedFile.getAbsolutePath())), StandardCharsets.UTF_8);
              	TXTdisplayArea.setText(content);
              	TXTdisplayArea.setCaretPosition(0); // reset scroll position to the top

              	if (textPane == null) {
                    textPane = new JTextPane();
                    JScrollPane scrollPane = new JScrollPane(textPane);
                    scrollPane.setBounds(10, 10, 675, 360); // Adjust as needed
                    frame.add(scrollPane);
                }
                textPane.setText(content);
                textPane.setCaretPosition(0); // Scroll to top
                textPane.setEditable(false); // User cannot edit
                // Highlight misspellings
                spellChecker.checkWord();
                highlightMisspellings(spellChecker);
                
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem recommendWord = new JMenuItem("Show recommend words");
                JMenuItem addToDictionary = new JMenuItem("Add to User Dictionary");
                JMenuItem manually = new JMenuItem("Manually correct");
                recommendWord.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) 
                    {
                    	 String selectedWord = textPane.getSelectedText();
                         if (selectedWord != null && !selectedWord.trim().isEmpty()) {
                             currentSelectedWord = selectedWord;
                             displayRecommendedWords();
                        }
                    }
                });
                addToDictionary.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        // add word to User dictionary
                        String selectedWord = textPane.getSelectedText();
                        myDictionary.addWord(selectedWord);
                        myDictionary.saveUserDicToDisk();
                        System.out.println(selectedWord + "is added to User dictionary");
                        TXTdisplayArea.setCaretPosition(TXTdisplayArea.getSelectionEnd());
                        highlightMisspellings(spellChecker);
         
                        //refresh user's dictionary contents here.
                        if (ShowUserDic == true)
                        {
                        	String displayUserDictionary = String.join("\n", myDictionary.getUserDictionary());
                    		RightDisplayArea.setText(displayUserDictionary);
                    		frame.revalidate();  
                            frame.repaint();
                        }
 
                    }
                });
                manually.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String selectedWord = textPane.getSelectedText();
                        if (selectedWord != null && !selectedWord.trim().isEmpty()) {
                            JDialog dialog = new JDialog();
                            dialog.setTitle("Manually correct");
                            dialog.setLayout(null); // Using null layout for absolute positioning
                            dialog.setSize(300, 200); // Set the size of the dialog

                            JTextField textfield = new JTextField();
                            textfield.setBounds(50, 20, 200, 30); // Positioned right below the button
                            dialog.add(textfield);

                            JButton submit = new JButton("Submit");
                            submit.setBounds(50, 60, 200, 30); // Positioned right below the textfield
                            dialog.add(submit);

                            // Submit Button ActionListener
                            submit.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    String manualCorrection = textfield.getText();
                                    try {
                                        StyledDocument doc = textPane.getStyledDocument();
                                        int selectionStart = textPane.getSelectionStart();
                                        int selectionEnd = textPane.getSelectionEnd();

                                        // Replace the selected word with manual correction
                                        doc.remove(selectionStart, selectionEnd - selectionStart);
                                        doc.insertString(selectionStart, manualCorrection, null);

                                        highlightMisspellings(spellChecker);
                                        Metrics.incrementManualCorrection();
                                        dialog.dispose(); // Close the dialog
                                    } catch (BadLocationException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });

                            dialog.setLocationRelativeTo(null);
                            dialog.setModal(true); // Make the dialog modal
                            dialog.setVisible(true); // Show the dialog
                        }
                    }
                });

                popupMenu.add(recommendWord);
                popupMenu.add(addToDictionary);
                popupMenu.add(manually);
                
                
                textPane.addMouseListener(new MouseAdapter() {
                    public void mouseReleased(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            selectWordAt(e);
                            popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }

                    public void mousePressed(MouseEvent e) {
                        if (e.isPopupTrigger()) {
                            selectWordAt(e);
                            popupMenu.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }

                    private static void selectWordAt(MouseEvent e) {
                        JTextPane textPane = (JTextPane) e.getSource();
                        int clickPosition = textPane.viewToModel2D(e.getPoint());

                        try {
                            javax.swing.text.Document swingDoc = textPane.getDocument();
                            String text = swingDoc.getText(0, swingDoc.getLength());

                            // Define a pattern that considers words separated by non-alphanumeric characters
                            Pattern pattern = Pattern.compile("\\b\\w+\\b");
                            Matcher matcher = pattern.matcher(text);

                            while (matcher.find()) {
                                if (matcher.start() <= clickPosition && matcher.end() >= clickPosition) {
                                    textPane.select(matcher.start(), matcher.end());
                                    break;
                                }
                            }
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                    }
                });
                
                ////////////
                //set Metrics here
                ////////////
                metrics.setCharacterCounts(DocChar);
                metrics.setCharacterCountsWithSpace(DocCharWithSpace);
                metrics.setWordCount(DocWord);
                metrics.setLineCount(DocLine);
                metrics.setMisSpellingCounts(spellChecker.getMisSpellingCounts());
                metrics.setMisCapitalizationCounts(spellChecker.getMisCapitalizationCounts());

                int totalErrors = spellChecker.getNumbofErrors();
              	COUNTdisplayArea.setFont(new Font("Arial", Font.PLAIN, 29)); // font
              	COUNTdisplayArea.setText("# of characters (no space) " + DocChar + 
                        "\n# of characters (with space) " + DocCharWithSpace +
                        "\n# of words: " + DocWord +
                        "\n# of lines: " + DocLine);              	RightDisplayArea.setText("Welcome!");
                frame.revalidate();  // Refresh the container hierarchy
                frame.repaint();     // Repaint the frame to show updated components
                housekeeping.setFileOpened(true);
            } catch (IOException ex) 
            {
                ex.printStackTrace();
                TXTdisplayArea.setText("Error reading file.");
                frame.revalidate();  // Refresh the container hierarchy
                frame.repaint();     // Repaint the frame to show updated components
            }
        }
    }
    
    /**
     * Displays recommended words based on the currently selected word in the JTextPane.
     * This method updates the JTextArea with recommendations or a message if no recommendations are found.
     */
    private static void displayRecommendedWords() {
        String selectedWord = textPane.getSelectedText();
        if (selectedWord != null && !selectedWord.trim().isEmpty()) {
            currentSelectedWord = selectedWord.trim();
            String recommendationText = recommendWords(spellChecker, currentSelectedWord);
            if (!recommendationText.isEmpty()) {
                RightDisplayArea.setText(recommendationText);
                
                // Simulate clicking the "Recommended Word" button
                recommendedWordBtn.doClick();
            } else {
                RightDisplayArea.setText("No recommendations found for '" + currentSelectedWord + "'.");
            }
        } else {
            RightDisplayArea.setText("Please select a word first.");
        }
    }


	
    /**
     * Handles the window closing event to check if changes are unsaved.
     * Prompts the user to save changes before exiting if unsaved changes are detected.
     *
     * @param textArea The JTextArea containing the document to check for unsaved changes.
     * @param frame    The JFrame that is being closed.
     */
    private static void handleWindowClosing(JTextArea textArea, JFrame frame) 
    {
    	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        if (!housekeeping.isSavedBeforeExiting() && housekeeping.isFileOpened()) 
        {
        	playSound("soundEffect/error.wav"); // play error sound.
        	int choice = JOptionPane.showConfirmDialog(
                frame, 
                "The file has been modified. Would you like to save it?", 
                "Confirmation", 
                JOptionPane.YES_NO_CANCEL_OPTION, 
                JOptionPane.QUESTION_MESSAGE
            );

            switch (choice) 
            {
                case JOptionPane.YES_OPTION:
                    // Call your saveToFile method here or any method you've implemented for saving
                	saveToCurrentFile(textPane, frame);
                    frame.dispose();
                    System.exit(0);
                    break;
                    
                case JOptionPane.NO_OPTION:
                    // Exit without saving
                    frame.dispose();
                    System.exit(0);
                    break;
                    
                case JOptionPane.CANCEL_OPTION:
                    // Just return to prevent the window from closing
                    return;
            }
        } else {
            frame.dispose();
            System.exit(0);
        }
    }
    
    /**
     * Initializes and displays the main graphical user interface for the spell checker application.
     * This method sets up the layout, initializes UI components like buttons, labels, text areas, 
     * and configures their properties and event listeners. It also handles the overall appearance 
     * of the application window including its size, background color, and visibility.
     */
    public static void mainGUI() 
    {	
    	int manuallyCount = 0;
        int WordDeletionCount = 0;
        int AcceptedCount = 0;
        JFrame frame = new JFrame("CS2212 Group 12 Spell checker");
        
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setResizable(false); // To disable the maximize button

        // Background color to match your design
        frame.getContentPane().setBackground(Color.white);
        
        
        //////////////////////////////
        // Black bar dividers |
        // setBounds(int x, int y, int width, int height)
        //////////////////////////////
        JPanel blackBar1 = new JPanel();
        blackBar1.setBackground(Color.black);
        blackBar1.setBounds(699, 0, 1, 600);  // Adjust position and size as needed
        frame.add(blackBar1);
        
        //////////////////////////////
        // Horizontal bar divider -
        //////////////////////////////
        JPanel blackBarHorizontal = new JPanel();
        blackBarHorizontal.setBackground(Color.black);
        blackBarHorizontal.setBounds(0, 380, 699, 1);  // Spanning the frame's width
        frame.add(blackBarHorizontal);
        
        //////////////////////////////
        // File icon and label
        /////////////////////////////
        fileIconLabel.setBounds(250, 80, 200, 200);
        fileIconLabel.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(fileIconLabel);
        
        // Insert txt label below the icon
        insertTxtLabel.setFont(new Font("Arial", Font.PLAIN, 23));
        insertTxtLabel.setBounds(220, 280, 460, 40);
        frame.add(insertTxtLabel);

        // Welcome label
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 33));
        welcomeLabel.setBounds(20, 400, 460, 40);
        frame.add(welcomeLabel);
        
        welcomeLabel1.setFont(new Font("Arial", Font.BOLD, 33));
        welcomeLabel1.setBounds(20, 435, 460, 40);
        frame.add(welcomeLabel1);
        TXTdisplayArea.setBounds(10, 10, 675, 360);
        
        ///////////////////
        //House keeping
        ////////////////
        TXTdisplayArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                housekeeping.setSavedBeforeExiting(false);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            	housekeeping.setSavedBeforeExiting(false);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // This usually won't be triggered for plain text areas, but it's a good practice to have it.
            	housekeeping.setSavedBeforeExiting(false);
            }
        });
        

        //////////////////////////////////////////////////////
        // Area under button to Show contents of # line ... 
        //////////////////////////////////////////////////////
        COUNTdisplayArea.setBounds(10, 390, 675, 170);
        COUNTdisplayArea.setEditable(false);
      	
        /////////////////////////////////////////
        // Area under Button Rec../ User's ... 
      	/////////////////////////////////////////
        RightDisplayArea.setBounds(705, 51, 290, 428);
        RightDisplayArea.setEditable(false);
        frame.add(RightDisplayArea);
        RightDisplayArea.setText("Welcome!\n");
        // add a scroll pane
        JScrollPane NewScrollPane = new JScrollPane(RightDisplayArea);
        NewScrollPane.setBounds(705, 51, 290, 428);  // Adjust this to where you want the JTextArea to appear
      	frame.add(NewScrollPane);
      	RightDisplayArea.setLineWrap(true);
      	RightDisplayArea.setWrapStyleWord(true);
      	RightDisplayArea.setCaretPosition(0); 
      	
        // for window closing "unsaved warning"
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing(TXTdisplayArea, frame);
            }
        });
        
        //////////////////////////////////
        /// Button of "Recommended word"  
        //////////////////////////////////
        recommendedWordBtn = new JButton("Recommended word");
        recommendedWordBtn.setBounds(700, 1, 150, 50);
       
        recommendedWordBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            
                if (currentSelectedWord != null && !currentSelectedWord.trim().isEmpty()) {
                    String recommendationText = recommendWords(spellChecker, currentSelectedWord);
                    if (!recommendationText.isEmpty()) {
                        RightDisplayArea.setText(recommendationText);
                        RightDisplayArea.addMouseMotionListener(new MouseMotionAdapter() {
                            @Override
                            public void mouseMoved(MouseEvent e) {
                                try {
                                    int offset = RightDisplayArea.viewToModel(e.getPoint());
                                    int wordStart = Utilities.getWordStart(RightDisplayArea, offset);
                                    int wordEnd = Utilities.getWordEnd(RightDisplayArea, offset);

                                    if (wordStart != -1 && wordEnd != -1) {
                                    	RightDisplayArea.select(wordStart, wordEnd);
                                    }
                                } catch (BadLocationException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });

                        // MouseAdapter for the right-click context menu and to request focus
                     // MouseAdapter for the right-click context menu and to request focus
                        RightDisplayArea.addMouseListener(new MouseAdapter() {

                            @Override
                            public void mouseEntered(MouseEvent e) {
                                RightDisplayArea.requestFocusInWindow();
                            }

                            private JPopupMenu createPopupMenu() {
                                JPopupMenu rightPopupMenu = new JPopupMenu();
                                
                                JMenuItem switchWord = new JMenuItem("Switch");
                                switchWord.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent event) {
                                        String switchWord = RightDisplayArea.getSelectedText();
                                        if (switchWord != null && !switchWord.isEmpty()) {
                                            StyledDocument doc = textPane.getStyledDocument();
                                            try {
                                                // Get the start and end positions of the selected text
                                                int selectionStart = textPane.getSelectionStart();
                                                int selectionEnd = textPane.getSelectionEnd();
                                                
                                                // Replace the selected text with the new word
                                                doc.remove(selectionStart, selectionEnd - selectionStart);
                                                doc.insertString(selectionStart, switchWord, null);
                                                
                                                highlightMisspellings(spellChecker); // Recheck the spellings

                                            } catch (BadLocationException e) {
                                                e.printStackTrace();
                                            }
                                            Metrics.incrementAcceptedSuggestion();
                                            frame.revalidate();
                                            frame.repaint();
                                        }
                                    }
                                });

                                rightPopupMenu.add(switchWord);
                                return rightPopupMenu;
                            }

                            public void mouseReleased(MouseEvent e) {
                                if (e.isPopupTrigger()) {
                                    JPopupMenu rightPopupMenu = createPopupMenu();
                                    rightPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                                }
                            }

                            public void mousePressed(MouseEvent e) {
                                if (e.isPopupTrigger()) {
                                    JPopupMenu rightPopupMenu = createPopupMenu();
                                    rightPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                                }
                            }
                        });

                    } else {
                        RightDisplayArea.setText("No recommendations found for '" + currentSelectedWord + "'.");
                    }
                } else {
                    RightDisplayArea.setText("Please select a word first.");
                }
                
            }
            
        });
        frame.add(recommendedWordBtn);
        
        
        /////////////////////////////////////
        /// Button of "User's dictionary" 
        ////////////////////////////////////
        JButton userDictionaryBtn = new JButton("User's dictionary");
        userDictionaryBtn.setBounds(850, 1, 150, 50);
        userDictionaryBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) 
            {
            	if (!housekeeping.isFileCompatible())
            	{
            		RightDisplayArea.setText("Contents of User's dictionary...\nPlease insert a valid txt file.");
            	}
            	else // note: after file opened, it need to show "user's dictionary" content
            	{
        			ShowUserDic = true;
        			ShowRecommendedWord = false;
        			
            		if (myDictionary.getUserDictionary().isEmpty())
            		{
            			//HighLightAllowedRightArea = false;
            			RightDisplayArea.setText("The user's dictionary is currently empty.\n");
            		}else {
            			//HighLightAllowedRightArea = true;
	            		String displayUserDictionary = String.join("\n", myDictionary.getUserDictionary());
	            		RightDisplayArea.setText(displayUserDictionary);
	            		RightDisplayArea.setCaretPosition(0);
	            		frame.revalidate();  // Refresh the container hierarchy
	                    frame.repaint();
	                    
	                    // allow to delete from the user dictionary.
	                    if (ShowUserDic)
	                    {
	                    	//allow right click to delete word from RightDisplayArea
	                        // Make RightDisplayArea focusable
	                        RightDisplayArea.setFocusable(true);
	                        RightDisplayArea.requestFocusInWindow();

	                        // MouseMotionListener for selecting the word under the mouse cursor
	                        RightDisplayArea.addMouseMotionListener(new MouseMotionAdapter() {
	                            @Override
	                            public void mouseMoved(MouseEvent e) {
	                            	if (!ShowUserDic) {
	                                    return; // Skip the rest of the method if ShowUserDic is false
	                                }
	                                try {
	                                    int offset = RightDisplayArea.viewToModel(e.getPoint());
	                                    int wordStart = Utilities.getWordStart(RightDisplayArea, offset);
	                                    int wordEnd = Utilities.getWordEnd(RightDisplayArea, offset);

	                                    if (wordStart != -1 && wordEnd != -1) {
	                                        RightDisplayArea.select(wordStart, wordEnd);
	                                    }
	                                } catch (BadLocationException ex) {
	                                    ex.printStackTrace();
	                                }
	                            }
	                        });

	                        // MouseAdapter for the right-click context menu and to request focus
	                        RightDisplayArea.addMouseListener(new MouseAdapter() {

	                            @Override
	                            public void mouseEntered(MouseEvent e) {
	                                RightDisplayArea.requestFocusInWindow();
	                            }

	                            private JPopupMenu createPopupMenu() {
	                                JPopupMenu rightPopupMenu = new JPopupMenu();
	                                JMenuItem deleteItem = new JMenuItem("Delete from User's Dictionary");
	                                deleteItem.addActionListener(new ActionListener() {
	                                    @Override
	                                    public void actionPerformed(ActionEvent event) {
	                                        String selectedWord = RightDisplayArea.getSelectedText();
	                                        if (selectedWord != null && !selectedWord.isEmpty()) {
	                                            myDictionary.removeWord(selectedWord);
	                                            myDictionary.saveUserDicToDisk();
	                                            String displayUserDictionary = String.join("\n", myDictionary.getUserDictionary());
	                                            RightDisplayArea.setText(displayUserDictionary);
	                                            frame.revalidate();  
	                                            frame.repaint();
	                                        }
	                                    }
	                                });
	                                
	                                JMenuItem resetItem = new JMenuItem("Reset User's Dictionary");
	                                resetItem.addActionListener(new ActionListener() {
	                                	public void actionPerformed(ActionEvent event) {
	                                		myDictionary.reset();
	                                		String displayUserDictionary = String.join("\n", myDictionary.getUserDictionary());
                                            RightDisplayArea.setText(displayUserDictionary);
                                            frame.revalidate();  
                                            frame.repaint();
	                                	}
	                                });
	                                
	                                rightPopupMenu.add(deleteItem);
	                                rightPopupMenu.add(resetItem);
	                                return rightPopupMenu;
	                            }

	                            public void mouseReleased(MouseEvent e) {
	                                if (e.isPopupTrigger()) {
	                                    JPopupMenu rightPopupMenu = createPopupMenu();
	                                    rightPopupMenu.show(e.getComponent(), e.getX(), e.getY());
	                                }
	                            }

	                            public void mousePressed(MouseEvent e) {
	                                if (e.isPopupTrigger()) {
	                                    JPopupMenu rightPopupMenu = createPopupMenu();
	                                    rightPopupMenu.show(e.getComponent(), e.getX(), e.getY());
	                                }
	                            }
	                        });
	                    }
            		}
            	}
            }
        });
        frame.add(userDictionaryBtn);
        
        ///////////////////////////
        // save a new file button
        ///////////////////////////
        JButton saveButton = new JButton("Save as a new file");
        saveButton.setBounds(700, 523, 130, 50); // set appropriate bounds as per your layout
        saveButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                saveToFile(textPane, frame);
            }
        });
        frame.add(saveButton);
        
        //////////////////
        // save button 
        //////////////////
        JButton NewFileSaveButton = new JButton("Save");
        NewFileSaveButton.setBounds(830, 523, 85, 50); // set appropriate bounds as per your layout
        NewFileSaveButton.addActionListener(new ActionListener() 
        { 
	        public void actionPerformed(ActionEvent e) 
	        {
	            saveToCurrentFile(textPane, frame); // This will attempt to save the file
	            if (housekeeping.isFileCompatible())
	            {
	            	// Regardless of the result, show the "File saved!" message
		            JOptionPane pane = new JOptionPane("File saved!\nClose after 1 second.", JOptionPane.INFORMATION_MESSAGE);
		            JDialog dialog = pane.createDialog(frame, "Message");
		
		            // Set timer to 1 second (1000 milliseconds), then close the dialog
		            Timer timer = new Timer(1000, new ActionListener() {
		                public void actionPerformed(ActionEvent e) {
		                    dialog.dispose();
		                }
		            });
		            timer.setRepeats(false); // The timer should only go off once
		            timer.start();
		
		            dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		            dialog.setVisible(true); // Make the dialog visible
	            }
	        }
        });
        frame.add(NewFileSaveButton);
        
        /////////////////////
        // Load file button
        /////////////////////
        JButton loadButton = new JButton("Loading a new file");
        loadButton.setBounds(700, 478, 130, 50); // set appropriate bounds as per your layout
        loadButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) ////////////////////// need action here.
            {
            	if (housekeeping.isFileCompatible())
            	{
                    if (!housekeeping.isSavedBeforeExiting()) 
                    {
                    	playSound("soundEffect/error.wav"); // play error sound.
                    	int choice = JOptionPane.showConfirmDialog(
                            frame, 
                            "The file has been modified. Would you like to save it before opening a new file?", 
                            "Confirmation", 
                            JOptionPane.YES_NO_CANCEL_OPTION, 
                            JOptionPane.QUESTION_MESSAGE
                        );

                        switch (choice) 
                        {
                            case JOptionPane.YES_OPTION:
                                // Call your saveToFile method here or any method you've implemented for saving
                            	saveToCurrentFile(textPane, frame);
                            	displaySelectedFileContent(TXTdisplayArea, COUNTdisplayArea, frame);
                                break;
                                
                            case JOptionPane.NO_OPTION:
                            	displaySelectedFileContent(TXTdisplayArea, COUNTdisplayArea, frame);
                                break;
                                
                            case JOptionPane.CANCEL_OPTION:
                                return;
                        }
                    }
                    else
                    {
                    	displaySelectedFileContent(TXTdisplayArea, COUNTdisplayArea, frame);
                    }
                    
            	} else
            	{
            		displaySelectedFileContent(TXTdisplayArea, COUNTdisplayArea, frame);
            	}
            }
        });
        
        frame.add(loadButton);
        
        ////////////////////
        // setting button 
        ////////////////////
        JButton settingButton = new JButton("Setting");
        settingButton.setBounds(915, 523, 85, 50);

        settingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Settings();
            }
        });

        frame.add(settingButton);
        
        ///////////////////////////
        // metrics/report button 
        ///////////////////////////
        JButton metricsButton = new JButton("Report");
        metricsButton.setBounds(830, 478, 85, 50); // set appropriate bounds as per your layout
        metricsButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) ////////////////////// need action here.
            {
            	if (!housekeeping.isFileCompatible())
            	{
        			frame.remove(welcomeLabel);
        	        frame.remove(welcomeLabel1);
        	        playSound("soundEffect/error.wav"); // play error sound.
        	        COUNTdisplayArea.setFont(new Font("Arial", Font.PLAIN, 25)); // font
        	        COUNTdisplayArea.setText("Error! Please insert a valid .txt file.");
        	        frame.add(COUNTdisplayArea);
        	        frame.revalidate();  // Refresh the container hierarchy
        	        frame.repaint();     // Repaint the frame to show updated components
            	}
            	else // note: after file opened //////////////////////
            	{

                    JLabel messageLabel = new JLabel(metrics.generateReport());
                    messageLabel.setFont(new Font("Arial", Font.PLAIN, 14)); // Set the font here

                    Object[] options = {"OK"};
                    int n = JOptionPane.showOptionDialog(
                        frame,                  // parent component
                        messageLabel,           // message, using JLabel for customization
                        "Report",  // title of the dialog
                        JOptionPane.YES_OPTION, // option type
                        JOptionPane.PLAIN_MESSAGE,        // message type
                        null,                   // custom icon (null for no icon)
                        options,                // custom button texts
                        options[0]              // default button
                    );


                }
            }
        });
        frame.add(metricsButton);
        
        ////////////////////////////
        // houseKeeping/help button 
        ////////////////////////////
        List<String> adviceList = housekeeping.help();
        String htmlAdvice = "<html>" + String.join("<br>", adviceList) + "</html>";
        System.out.print(htmlAdvice);
        JButton houseKeepingButton = new JButton("Help");
        houseKeepingButton.setBounds(915, 478, 85, 50);
        houseKeepingButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Create a new JDialog. Set 'true' for modal, or 'false' for non-modal
                JDialog helpDialog = new JDialog();
                helpDialog.setTitle("How to use our spellchecker");
                
                // Add content to the dialog
                helpDialog.add(new JLabel(htmlAdvice));

                // Set dialog size
                helpDialog.setSize(1000, 500);

                // Set location relative to the button or frame
                helpDialog.setLocationRelativeTo(houseKeepingButton);
                
                // Display the dialog
                helpDialog.setVisible(true);
            }
        });
        frame.add(houseKeepingButton);

        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        /////////////////////////////////////////////
        // click the file icon to insert .txt file.
        /////////////////////////////////////////////
        fileIconLabel.addMouseListener(new MouseAdapter() 
        {
        	@Override
            public void mouseClicked(MouseEvent e) 
        	{
                displaySelectedFileContent(TXTdisplayArea, COUNTdisplayArea, frame);
            }
        });
        frame.setVisible(true);
    }
    
    /**
     * main function
     */
    public static void main(String[] args) 
    {
    	mainGUI();
    }
}
