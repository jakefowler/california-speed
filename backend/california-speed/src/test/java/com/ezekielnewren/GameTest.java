package com.ezekielnewren;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GameTest {
    Game game;

    @Before
    public void setUp() throws Exception {
        Player zero = new Player();
        Player one = new Player();
        zero.name = "zero";
        one.name = "one";
        this.game = new Game(zero, one);
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

    @Test
    public void Should_DecreaseMainDeckSizeByFour_When_getNewPlacedCardsIsCalled() {
        int sizeBefore = this.game.players[0].mainDeck.getSize();
        ArrayList<Card> placed = this.game.players[0].mainDeck.getNewPlacedCards();
        int expected = sizeBefore - 4;
        int actual = this.game.players[0].mainDeck.getSize();
        assertEquals(expected, actual);
    }

    @Test
    public void Should_BeSizeFour_When_getNewPlacedDeckIsCalled() {
        ArrayList<Card> placed = this.game.players[0].mainDeck.getNewPlacedCards();
        int expected = 4;
        int actual = placed.size();
        assertEquals(expected, actual);
    }

    @Test
    public void Should_PlaceCard_When_OnClaimIsPassedValidMove() {
        for (int i = 0; i < this.game.placedCards.size(); i++) {
            if (this.game.hasMatch(i)) {
                Card expected = this.game.players[0].mainDeck.getCard(0);
                this.game.onClaim(this.game.players[0], i);
                Card actual = this.game.placedCards.get(i);
                assertEquals(expected, actual);
            }
        }
    }

}