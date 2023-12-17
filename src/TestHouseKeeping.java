import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.Before;
import org.junit.jupiter.api.Test;

class TestHouseKeeping {
	
	Housekeeping housekeeping = new Housekeeping(true, true, true);
	
	@Before
	void setUp(Housekeeping housekeeping) {
		this.housekeeping = housekeeping;
	}
	
	@Test
	void testIsFileCompatible() {
		assertTrue(housekeeping.isFileCompatible());
	}
	
	@Test
	void testSetCompatible() {
		housekeeping.setCompatible(false);
		
		assertFalse(housekeeping.isFileCompatible());
	}
	
	@Test
	void testIsFileOpened() {
		assertTrue(housekeeping.isFileOpened());
	}
	
	@Test
	void testSetFileOpened() {
		housekeeping.setFileOpened(false);
		
		assertFalse(housekeeping.isFileOpened());
	}

	@Test
	void testIsSavedBeforeExiting() {
		assertTrue(housekeeping.isSavedBeforeExiting());
	}
	
	@Test
	void testSetSavedBeforeExiting() {
		housekeeping.setSavedBeforeExiting(false);
		
		assertFalse(housekeeping.isSavedBeforeExiting());
	}
	
	@Test
	void testHelp() {
		List<String> helpList = housekeeping.help();
		
		assertFalse(helpList.isEmpty());
		assertTrue(helpList.contains("1. Upload a .txt file by clicking \'Loading a new file\' button."));
	}

}
