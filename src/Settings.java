import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

	/**
	 * 
	 * @author JUYOUNG LEE
	 * This Settings class represents a GUI window for managing application settings.
	 * This class allows users to set and modify different configuration options for the application.
	 * such as the default save location and filtering options for HTML/XML tags.
	 * The class extends JFrame and create a simple user interface using Swing components.
	 * 
	 */
public class Settings extends JFrame {
    JTextField defaultSaveLocationTextField;
    JCheckBox tagFilterBox;
    JButton browseButton;

    static File CONFIG_FILE = new File("config.txt");
    static final String DEFAULT_SAVE_LOCATION_KEY = "Default Save Location";
    static final String TAG_FILTER_KEY = "Filter or skip HTML/XML tags";
    private List<String> settings;
    
    /**
     *  Testing settings
     * @param configFilePath
     */
    public Settings(String configFilePath) {
        this();
        CONFIG_FILE = new File(configFilePath);
        loadSettings();
    }
    
    
    /**
     * Constructor for the Settings class.
     */
    public Settings() {
        super("Settings");
        setLayout(new BorderLayout());

        JPanel settingsPanel = new JPanel(new GridLayout(3, 2)); 

        JLabel defaultSaveLocationLabel = new JLabel(DEFAULT_SAVE_LOCATION_KEY + ":");
        defaultSaveLocationTextField = new JTextField();
        browseButton = new JButton("Browse");
        JScrollPane scrollPane = new JScrollPane(defaultSaveLocationTextField);

        JLabel tagFilterLabel = new JLabel(TAG_FILTER_KEY + ":");
        tagFilterBox = new JCheckBox();

        settingsPanel.add(defaultSaveLocationLabel);
        settingsPanel.add(scrollPane);
        settingsPanel.add(browseButton);
        settingsPanel.add(new JLabel());
        settingsPanel.add(tagFilterLabel);
        settingsPanel.add(tagFilterBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton applyButton = new JButton("Apply");
        JButton closeSettingsButton = new JButton("Close");

        buttonPanel.add(applyButton);
        buttonPanel.add(closeSettingsButton);

        applyButton.addActionListener(e -> saveSettings());
        closeSettingsButton.addActionListener(e -> dispose());

        browseButton.addActionListener(e -> browseDefaultSaveLocation());

        add(settingsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);
        loadSettings();
        setVisible(true);
    }

    /**
     * Saves the current settings to the configuration file.
     * 
     */
    void saveSettings() {
        try (PrintWriter writer = new PrintWriter(CONFIG_FILE)) {
            writer.println(DEFAULT_SAVE_LOCATION_KEY + ": " + defaultSaveLocationTextField.getText());
            writer.println(TAG_FILTER_KEY + ": " + tagFilterBox.isSelected());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the settings from the configuration file.
     */
    void loadSettings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(CONFIG_FILE))) {
            settings = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                settings.add(line);
            }
            displaySettings(); // Ensure settings are displayed after loading
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Displays the settings in the user interface.
     */
    void displaySettings() {
        for (String setting : settings) {
            String[] parts = setting.split(": ");
            if (parts.length == 2) {
                String key = parts[0];
                String value = parts[1];
                switch (key) {
                    case DEFAULT_SAVE_LOCATION_KEY:
                        defaultSaveLocationTextField.setText(value);
                        break;
                    case TAG_FILTER_KEY:
                        tagFilterBox.setSelected(Boolean.parseBoolean(value));
                        break;
                }
            }
        }
    }
    
    
    /**
     * Opens a dialog for the user to browse and select a default location.
     */
    void browseDefaultSaveLocation() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = fileChooser.showDialog(this, "Select Default Save Location");
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            defaultSaveLocationTextField.setText(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Retrieves the current settings as a list of strings. 
     * @return a List of strings representing the current settings.
     */
    public List<String> retrieveSettings() {
        return settings;
    }
    
    /**
     *Gets the default save location set by the user 
     * @return
     */
    public String getDefaultSaveLocation() {
        return defaultSaveLocationTextField.getText();
    }
    
    /**
     * Reads the configuration file to determine whether the tag filtering
     * @return true if the tag filter is active
     */
    public static boolean isTagFilterActive() {
        File configFile = new File("config.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(TAG_FILTER_KEY)) {
                    return Boolean.parseBoolean(line.split(": ")[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false; // Default return value if setting is not found
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Settings());
    }
}
