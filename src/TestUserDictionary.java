import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.jupiter.api.Test;

class TestUserDictionary {

	private UserDictionary userDictionary = new UserDictionary();
	
	@Before
	void setUp(UserDictionary userDictionary) {
		this.userDictionary = userDictionary;
	}
	
	@Test
	void testContains() {
		assertFalse(userDictionary.contains("orange"));
		
		userDictionary.addWord("orange");
		assertTrue(userDictionary.contains("orange"));
	}
	
	@Test
	void testAddWord() {
		userDictionary.addWord("apple");
		assertTrue(userDictionary.contains("apple"));
	}

	@Test
	void testRemoveWord() {
		userDictionary.addWord("banana");
		assertTrue(userDictionary.contains("banana"));
		
		userDictionary.removeWord("banana");
		assertFalse(userDictionary.contains("banana"));
	}
	
	@Test
	void testReset() {
		userDictionary.addWord("kiwi");
		assertFalse(userDictionary.getUserDictionary().isEmpty());
		
		userDictionary.reset();
		assertTrue(userDictionary.getUserDictionary().isEmpty());
	}
	
	@Test
	void testGetUserDictionary() {
		userDictionary.reset();
		assertNotNull(userDictionary.getUserDictionary());
		
		userDictionary.addWord("apple");
		assertNotNull(userDictionary.getUserDictionary());
		
		TreeSet<String> expectedSet = new TreeSet<>();
        expectedSet.add("apple");
        assertEquals(expectedSet, userDictionary.getUserDictionary());
		
	}
	
	@Test
	void testSaveUserDicToDisk() throws IOException {
		File tempFile = File.createTempFile("user_dictionary", ".txt");
		userDictionary.saveUserDicToDisk();
		
		assertTrue(tempFile.exists());
		
		tempFile.delete();
	}
	
}
