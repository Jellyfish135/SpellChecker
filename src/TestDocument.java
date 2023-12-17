import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.Test;

class TestDocument {

	private Document document = new Document();
	
	@Before
	void setUp(Document document) {
		this.document = document;
	}
	
	@Test
	void testReadFromString() {
		String content = "This is a test string.";
		document.readFromString(content);
		
		assertEquals(content.length(), document.DocCharacterCountsWithSpace());
		assertEquals(1, document.DocLineCounts());
		assertEquals(5, document.DocWordCounts());
		assertEquals(List.of("This", "is", "a", "test", "string"), document.DocPrintWord());
	}
	
	@Test
	void testDOCopenFile() {
		try {
			String filePath = "testDocument.txt";
			document.DOCopenFile(filePath);
			File currentFile = new File(filePath);
		
			assertTrue(currentFile.exists());
			assertEquals(3, document.DocWordCounts());
			assertEquals(1, document.DocLineCounts());
		}
		catch (IOException e) {
			fail("IOException occurred: " + e.getMessage());
		}
	}
	
	@Test
	void testDOCopenNonExistentFile() {
		assertThrows(FileNotFoundException.class, () -> document.DOCopenFile("nonExistentFile.txt"));
	}
	
	@Test
	void testReadAndProcessFile() {
		try {
			String filePath = "testDocument.txt";
			
			document.DOCopenFile(filePath);
			assertEquals(21, document.DocCharacterCountsWithSpace());
			assertEquals(19, document.DocCharacterCounts());
			assertEquals(3, document.DocWordCounts());
			assertEquals(1, document.DocLineCounts());
			
		}
		catch (IOException e) {
			fail("IOException occurred: " + e.getMessage());
		}
	}
	
	@Test
	void testSplitIntoWords() {
		String content = "This!is a test*string.";
		List<String> words = document.splitIntoWords(content, false);
		
		assertEquals(List.of("This", "is", "a", "test", "string"), words);
	}
	
	@Test
	void testSaveFile() {
		 try {
	            document.DOCopenFile("testDocument.txt");
	            String outputPath = "outputFile.txt";
	            document.saveFile(outputPath);

	            // Check that the file was saved to the expected location
	            assertTrue(Files.exists(Path.of(outputPath)));

	            // Read the saved file and compare its content
	            List<String> savedLines = Files.readAllLines(Path.of(outputPath));
	            List<String> expectedLines = Files.readAllLines(Path.of("testDocument.txt"));
	            assertEquals(expectedLines, savedLines);
        } 
		catch (IOException e) {
			fail("IOException occurred: " + e.getMessage());
        }
	}
	
	@Test
	void testGetArrayList() {
		try {
            // Read from a test file
            String testFilePath = "testDocument.txt";
           
            // Open the test file
            document.DOCopenFile(testFilePath);

            // Get the array list and check its content
            String[] arrayList = document.getArrayList();

            assertEquals(3, arrayList.length);
            assertEquals("test", arrayList[0]);
            assertEquals("Document", arrayList[1]);
            assertEquals("content", arrayList[2]);
        } 
		catch (IOException e) {
            fail("IOException occurred: " + e.getMessage());
        }
	}
	
	@Test
	void testWordList() {
		fail("not written yet");
	}
	
	@Test
	void testTrimToSize() {
		fail("not written yet");
	}
	
	@Test
	void testSetNewCapacity() {
		fail("not written yet");
	}

}
