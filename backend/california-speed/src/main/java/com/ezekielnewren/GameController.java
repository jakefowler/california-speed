package com.ezekielnewren;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class GameController {
    private static final Logger log = Log.getLogger(GameController.class);


    Controller ctrl;

    public Deck playerOneDeck;
    public Deck playerTwoDeck;
    public ArrayList<Card> placedCards;
    public ArrayList<Card> playerOneCoveredCards;
    public ArrayList<Card> playerTwoCoveredCards;
    /**
     * prevMoves saves the cards that are clicked and have a match. This allows the two players to each click on a card
     * for a match and click on three cards that match. It also prevents losing matches when only one card is clicked
     * and the players move on to other cards.
     * This gets cleared of cards that don't have matches every time the board changes.
     */
    public HashSet prevMoves;

    public GameController() {
        ctrl = Controller.getInstance();
        this.playerOneDeck = new Deck();
        this.playerOneDeck.fillDeck();
        this.playerOneDeck.shuffle();
        this.playerTwoDeck = playerOneDeck.splitDeck();
        setupPlacedCards();
        this.prevMoves = new HashSet();
        this.playerOneCoveredCards = new ArrayList<Card>();
        this.playerTwoCoveredCards = new ArrayList<Card>();
    }

    public void retrieveCards() {
        this.playerOneDeck.addDeck(playerOneCoveredCards);
        this.playerTwoDeck.addDeck(playerTwoCoveredCards);
        this.playerOneDeck.addDeck(new ArrayList<Card>(this.placedCards.subList(0, 4)));
        this.playerOneDeck.addDeck(new ArrayList<Card>(this.placedCards.subList(4, 8)));
    }

    public void setupPlacedCards() {
        this.placedCards = new ArrayList<Card>(this.playerOneDeck.getNewPlacedCards());
        this.placedCards.addAll(this.playerTwoDeck.getNewPlacedCards());
    }

    public void noMatch() {
        retrieveCards();
        setupPlacedCards();
    }

    public void placeCard(Card card, int index) {
        if (index < 4) {
            this.playerOneCoveredCards.add(this.placedCards.get(index));
        }
        else {
            this.playerTwoCoveredCards.add(this.placedCards.get(index));
        }
        this.prevMoves.add(this.placedCards.get(index));
        this.placedCards.set(index, card);
    }

    public boolean hasMatch(int index) {
        Card cardToMatch = this.placedCards.get(index);
        if(!(this.prevMoves.isEmpty()) && this.prevMoves.contains(cardToMatch)) {
            return true;
        }
        long count = this.placedCards.stream().filter(card -> card.equals(cardToMatch)).count();
        if (count > 1) {
            return true;
        }
        return false;
    }

    /**
     * This clears out any cards from prevMoves that don't have a match.
     */
    public void clearUnmatchedPrevMoves() {
        this.prevMoves = (HashSet) this.prevMoves.stream().filter(card -> this.placedCards.contains(card)).collect(Collectors.toCollection(HashSet::new));
    }

    public boolean onClaim(Player p, int pile) {
        return true;
    }

    public void updateGameboard() {
        Card[] state = null;
        //ctrl.updateBoard(state);
    }

}
