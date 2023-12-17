import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.jupiter.api.Test;

class SpellCheckerTest {
    String input[] = {"helsy", "tessst", "l0ot", "mIx"};
    private SpellChecker spellChecker = new SpellChecker(input);

    @Before
    void setUp(SpellChecker spellChecker) {
        this.spellChecker = spellChecker;
    }

    @Test
    void missSpellingTest() {
        correctionData tmp = new correctionData();
        assertTrue(spellChecker.misspellingCorrection("yhe",tmp, false));
        assertTrue(spellChecker.misspellingCorrection("todayHey",tmp, false));
        assertTrue(spellChecker.misspellingCorrection("oples",tmp, true));
    }

    @Test
    void miscapitalizationCorrectionTest() {
        SpellChecker.raw_string tmp1 = new SpellChecker.raw_string("mIx");
        correctionData tmp = new correctionData();
        assertTrue(spellChecker.miscapitalizationCorrection(tmp1,tmp, false));
    }

    @Test
    void numbersInWordsCorrectionTest() {
        SpellChecker.raw_string tmp2 = new SpellChecker.raw_string("l0ot");
        correctionData tmp = new correctionData();
        assertTrue(spellChecker.numbersInWordsCorrection(tmp2,tmp, false));
    }

    @Test
    void  specialCasesTest() {
        correctionData tmp = new correctionData();
        assertTrue(spellChecker.specialCases("dr",tmp));
    }

    @Test
    void  otherCaseTest() {
        correctionData tmp = new correctionData();
        assertTrue(spellChecker.otherCase("google.dhfh",tmp));
    }

    @Test
    void  testMainFunction() {
        spellChecker.checkWord();
        ArrayList<correctionData> test = spellChecker.suggestCorrection();
        for (int i = 0; i < test.size(); i++) {
            assertTrue(test.get(i).isError()); // they all show be wrong
        }
    }

    @Test
    void  getErrorWordsTest() {
        List<String> test = spellChecker.getErrorWords(input);
        for (int i = 0; i < test.size(); i++) {
           assertTrue(test.get(i).equals(input[i]));
        }
    }

}