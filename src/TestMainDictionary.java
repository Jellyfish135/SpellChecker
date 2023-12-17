import static org.junit.jupiter.api.Assertions.*;

import org.junit.Before;
import org.junit.jupiter.api.Test;

class TestMainDictionary {
	
	private MainDictionary mainDictionary = new MainDictionary();
	
	@Before
	void setUp(MainDictionary mainDictionary) {
		this.mainDictionary = mainDictionary;
	}
	
	@Test
	void testConstructor() {
		assertNotNull(mainDictionary);
	}
	
	@Test
	void testContainsValidWord() {
		assertTrue(mainDictionary.contains("hello"));
		assertTrue(mainDictionary.contains("world"));
	}
	
	@Test
	void testContainsInvalidWord() {
		assertFalse(mainDictionary.contains("hlowrld"));
		assertFalse(mainDictionary.contains("invalidword"));
	}

	@Test
	void testContainsWithMixedCase() {
		assertTrue(mainDictionary.contains("HeLLo"));
		assertTrue(mainDictionary.contains("WorLD"));
	}
	
	@Test
	void testGetAlphabet() {
		char[] alphabet = mainDictionary.getAlphabet();
        assertNotNull(alphabet);
        assertEquals(26, alphabet.length);
        assertEquals('a', alphabet[0]);
        assertEquals('z', alphabet[25]);
	}
}
