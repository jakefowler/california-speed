package com.ezekielnewren;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameControllerTest {
    GameController game;

    @Before
    public void setUp() throws Exception {
        this.game = new GameController();
    }

    @Test
    public void Should_ContainCard_When_CardIsCopiedFromDeck() {
        Card card = this.game.placedCards.get(0);
        boolean actual = this.game.placedCards.contains(card);
        assertEquals(true, actual);
    }

    @Test
    public void Should_ContainCard_When_SuitAndRankAreCopiedFromCardInDeck() {
        Card card = this.game.placedCards.get(0);
        String suit = card.getSuit();
        int rank = card.getRank();
        Card testCard = new Card(suit, rank);
        boolean actual = this.game.placedCards.contains(testCard);
        assertEquals(true, actual);
    }

    @Test
    public void Should_ContainCardInPrevMoves_When_CardIsCovered() {
        Card testCard = this.game.placedCards.get(0);
        this.game.placeCard(new Card('h', 1), 0);
        boolean actual = this.game.prevMoves.contains(testCard);
        assertEquals(true, actual);
    }

    @Test
    public void Should_HaveMatch_When_CardsOfTheSameRankAreInserted() {
        Card copyCard = this.game.placedCards.get(0);
        int rank = copyCard.getRank();
        Card testCard = new Card('h', rank);
        this.game.placedCards.set(1, testCard);
        boolean actual = this.game.hasMatch(0);
        assertEquals(true, actual);
    }

    @Test
    public void Should_HaveMatch_When_prevMovesHasSameRankOfCard() {
        Card copyCard = this.game.placedCards.get(0);
        int rank = copyCard.getRank();
        Card testCard = new Card('h', rank);
        this.game.prevMoves.add(testCard);
        boolean actual = this.game.hasMatch(0);
        assertEquals(true, actual);
    }

    @Test
    public void Should_ClearPrevMove_WhenNoMatch() {
        Card testCard = new Card('h', 200);
        this.game.prevMoves.add(testCard);
        assertEquals(true, this.game.prevMoves.contains(testCard));
        this.game.clearUnmatchedPrevMoves();
        assertEquals(false, this.game.prevMoves.contains(testCard));
    }
}