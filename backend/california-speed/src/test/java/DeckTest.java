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

    @org.junit.Test
    public void Should_DecreaseMainDeckSizeByOne_When_drawCardIsCalled() {
        this.testDeck.fillDeck();
        int sizeBefore = this.testDeck.getSize();
        this.testDeck.drawCard();
        int expected = sizeBefore - 1;
        int actual = this.testDeck.getSize();
        assertEquals(expected, actual);
    }

    @org.junit.Test
    public void Should_ReplaceCard_When_placeCardIsCalled() {
        this.testDeck.fillDeck();
        Deck placedDeck = this.testDeck.getNewPlacedDeck();
        Card testCard = new Card('H', 1);
        placedDeck.placeCard(3, testCard);
        String expectedSuit = "HEART";
        String actualSuit = placedDeck.getDeck().get(3).getSuit();
        assertEquals(expectedSuit, actualSuit);
        int expectedRank = 1;
        int actualRank = placedDeck.getDeck().get(3).getRank();
        assertEquals(expectedRank, actualRank);
    }

    @org.junit.Test
    public void Should_MoveOldCardToBack_When_placeCardIsCalled() {
        this.testDeck.fillDeck();
        Deck placedDeck = this.testDeck.getNewPlacedDeck();
        Card testCard = new Card('H', 1);
        Card cardThatIsMoved = placedDeck.getDeck().get(3);
        String expectedSuit = cardThatIsMoved.getSuit();
        int expectedRank = cardThatIsMoved.getRank();
        placedDeck.placeCard(3, testCard);
        String actualSuit = placedDeck.getDeck().get(placedDeck.getSize() - 1).getSuit();
        int actualRank = placedDeck.getDeck().get(placedDeck.getSize() - 1).getRank();
        assertEquals(expectedSuit, actualSuit);
        assertEquals(expectedRank, actualRank);
    }
}