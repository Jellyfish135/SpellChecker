import static org.junit.jupiter.api.Assertions.*;

import java.util.TreeSet;

import org.junit.Before;
import org.junit.jupiter.api.Test;

class TestCorrectionData {

	private correctionData data = new correctionData();
	
	@Before
	void setUp(correctionData data) {
		this.data = data;
	}
	
	@Test
	void testSetGetOriginalWord() {
		data.setOrginalWord("testWord");
		assertEquals("testWord", data.getOrginalWord());
	}
	
	@Test
	void testAddGetCorrections() {
		data.addCorrection("correction1");
		data.addCorrection("correction2");
		
		TreeSet<String> expectedCorrections = new TreeSet<>();
		expectedCorrections.add("correction1");
		expectedCorrections.add("correction2");
		
		assertEquals(expectedCorrections, data.getCorrections());
	}
	
	@Test
    public void testErrorList() {
        TreeSet<String> errorList = new TreeSet<>();
        errorList.add("error1");
        errorList.add("error2");

        data.ErrorList(errorList);

        assertEquals(errorList, data.getCorrections());
    }
	
	@Test
	void testSetError() {
		data.setError(1);
		assertEquals(correctionData.errorType.capsFirstLetter, data.getError());
	
		data.setError(7);
		assertEquals(correctionData.errorType.doubleWords, data.getError());
	}

	@Test
	void testIsError() {
		data.setError(1);
		assertTrue(data.isError());
	}
}
