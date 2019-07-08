import com.ezekielnewren.Card;
import org.junit.Test;

import static org.junit.Assert.*;

public class CardTest {

    @Test
    public void getSuit() {
        Card cardH = new Card('H', 1);
        String actual = cardH.getSuit();
        String expected = "HEART";
        assertEquals(expected, actual);
    }

    @Test
    public void getRank() {
        Card cardOne = new Card('H', 1);
        int actual = cardOne.getRank();
        int expected = 1;
        assertEquals(expected, actual);
    }
}