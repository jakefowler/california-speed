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

    @Test
    public void Should_ReturnTrue_When_ComparingCardToItself() {
        Card cardOne = new Card('H', 1);
        boolean actual = cardOne.compareCards(cardOne);
        assertEquals(true, actual);
    }

    @Test
    public void Should_ReturnFalse_When_ComparingDifferentCards() {
        Card cardOne = new Card('H', 1);
        Card cardTwo = new Card('S', 5);
        boolean actual = cardOne.compareCards(cardTwo);
        assertNotEquals(true, actual);
    }
}