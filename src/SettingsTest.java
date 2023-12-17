import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class SettingsTest {
    private Settings settings;
    private final String testConfigFilePath = "test_config.txt";

    @BeforeEach
    public void setUp() throws IOException {
        // Create a temporary test configuration file
        System.setProperty("config.file", testConfigFilePath);
        new File(testConfigFilePath).createNewFile();

        settings = new Settings(testConfigFilePath);
    }

    @AfterEach
    public void tearDown() {
        new File(testConfigFilePath).delete();
    }

    @Test
    public void testSaveSettings() {
        String testPath = "/test/path";
        settings.defaultSaveLocationTextField.setText(testPath);
        settings.tagFilterBox.setSelected(true);

        settings.saveSettings();

        try (BufferedReader reader = new BufferedReader(new FileReader(testConfigFilePath))) {
            String saveLocationLine = reader.readLine();
            assertNotNull(saveLocationLine);
            assertTrue(saveLocationLine.contains(testPath));

            String tagFilterLine = reader.readLine();
            assertNotNull(tagFilterLine);
            assertTrue(tagFilterLine.contains("true"));
        } catch (IOException e) {
            fail("Exception occurred while reading the settings file.");
        }
    }


    @Test
    public void testLoadSettings() {
        try (PrintWriter writer = new PrintWriter(new File(testConfigFilePath))) {
            writer.println(Settings.DEFAULT_SAVE_LOCATION_KEY + ": /example/path");
            writer.println(Settings.TAG_FILTER_KEY + ": false");
        } catch (FileNotFoundException e) {
            fail("Failed to create the test configuration file.");
        }

        settings.loadSettings();

        assertEquals("/example/path", settings.defaultSaveLocationTextField.getText());
        assertFalse(settings.tagFilterBox.isSelected());
    }

    

    
    @Test
    public void testDisplaySettings() throws NoSuchFieldException, IllegalAccessException {
        Settings settings = new Settings();

        // Using reflection to access the private field
        Field settingsField = Settings.class.getDeclaredField("settings");
        settingsField.setAccessible(true);

        List<String> testData = new ArrayList<>();
        testData.add(Settings.DEFAULT_SAVE_LOCATION_KEY + ": /example/path");
        testData.add(Settings.TAG_FILTER_KEY + ": true");
        settingsField.set(settings, testData); // Set the field using reflection

        settings.displaySettings();

        assertEquals("/example/path", settings.defaultSaveLocationTextField.getText());
        assertTrue(settings.tagFilterBox.isSelected());
    }


    @Test
    public void testRetrieveSettings() {
        assertNotNull(settings.retrieveSettings());
        assertTrue(settings.retrieveSettings() instanceof java.util.List);
    }

    @Test
    public void testGetDefaultSaveLocation() {
        String expectedPath = "/test/path";
        settings.defaultSaveLocationTextField.setText(expectedPath);
        assertEquals(expectedPath, settings.getDefaultSaveLocation());
    }
}