package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

public class AlphabetTest {

    @Test
    public void test() {
        Alphabet A = new Alphabet();
        assertEquals(26, A.size());
        assertEquals(true, A.contains('I'));
        assertEquals('O', A.toChar(A.toInt('O')));
    }

}
