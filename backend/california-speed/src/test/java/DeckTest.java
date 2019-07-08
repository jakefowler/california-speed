import com.ezekielnewren.Deck;
import com.ezekielnewren.Card;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DeckTest {

    Deck testDeck;

    @Before
    public void setUp() throws Exception {
        this.testDeck = new Deck();
    }

    @org.junit.Test
    public void Should_BeSizeFiftyTwo_When_FillDeckIsCalled() {
        this.testDeck.fillDeck();
        int actual = this.testDeck.getSize();
        int expected = 52;
        assertEquals(expected, actual);

    }

    @org.junit.Test
    public void Should_BeEqualDecks_When_splitDeckIsCalled() {
        Deck toSplit = new Deck();
        toSplit.fillDeck();
        int beforeSize = toSplit.getSize();
        Deck firstHalf = toSplit.splitDeck();
        int actualFirstHalfSize = firstHalf.getSize();
        int actualSecondHalfSize = toSplit.getSize();
        int expectedSize = 26;
        assertEquals(expectedSize, actualFirstHalfSize);
        assertEquals(expectedSize, actualSecondHalfSize);
    }

    @org.junit.Test
    public void Should_ReturnCorrectSize_When_getSizeIsCalled() {
        this.testDeck.fillDeck();
        int expected = 52;
        int actual = this.testDeck.getSize();
        assertEquals(expected, actual);
        this.testDeck.addCard(new Card('H', 9));
        expected = 53;
        actual = this.testDeck.getSize();
        assertEquals(expected, actual);
    }

    @org.junit.Test
    public void Should_IncreaseSize_When_addCardIsPassedACard() {
        Card cardOne = new Card('H', 1);
        Card cardTwo = new Card('D', 4);
        Card cardThree = new Card('S', 10);
        this.testDeck.addCard(cardOne);
        this.testDeck.addCard(cardTwo);
        this.testDeck.addCard(cardThree);
        int expected = 3;
        int actual = this.testDeck.getSize();
        assertEquals(expected, actual);
    }

    @org.junit.Test
    public void Should_DoubleSize_When_DeckIsAddedToItself() {
        this.testDeck.fillDeck();
        int initialSize = this.testDeck.getSize();
        this.testDeck.addDeck(this.testDeck.getDeck());
        int expected = initialSize * 2;
        int actual = this.testDeck.getSize();
        assertEquals(expected, actual);
    }

    @org.junit.Test
    public void Should_BeSizeFour_When_getNewPlacedDeckIsCalled() {
        this.testDeck.fillDeck();
        Deck placed = this.testDeck.getNewPlacedDeck();
        int expected = 4;
        int actual = placed.getSize();
        assertEquals(expected, actual);
    }

    @org.junit.Test
    public void Should_DecreaseMainDeckSizeByFour_When_getNewPlacedDeckIsCalled() {
        this.testDeck.fillDeck();
        int sizeBefore = this.testDeck.getSize();
        Deck placed = this.testDeck.getNewPlacedDeck();
        int expected = sizeBefore - 4;
        int actual = this.testDeck.getSize();
        assertEquals(expected, actual);
    }
}