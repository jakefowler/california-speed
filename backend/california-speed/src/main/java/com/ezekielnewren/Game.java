package com.ezekielnewren;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Game {
    private static final Logger log = Log.getLogger(Game.class);

    Controller ctrl;

    public ArrayList<Card> placedCards;
    Player[] players;

    /**
     * prevMoves saves the cards that are clicked and have a match. This allows the two players to each click on a card
     * for a match and click on three cards that match. It also prevents losing matches when only one card is clicked
     * and the players move on to other cards.
     * This gets cleared of cards that don't have matches every time the board changes.
     */
    public HashSet prevMoves;

    public Game(Player zero, Player one) {
        ctrl = Controller.getInstance();
        this.players = new Player[2];
        this.players[0] = zero;
        this.players[1] = one;
        resetMainDecks();
        resetPlacedCards();
        resetCoveredCards();
        this.prevMoves = new HashSet();
    }

    public void resetMainDecks() {
        this.players[0].mainDeck = new Deck();
        this.players[0].mainDeck.fillDeck();
        this.players[0].mainDeck.shuffle();
        this.players[1].mainDeck = this.players[0].mainDeck.splitDeck();
    }

    public void retrieveCards() {
        for (Player p : this.players) {
            p.mainDeck.addDeck(p.coveredCards);
        }
        this.players[0].mainDeck.addDeck(new ArrayList<Card>(this.placedCards.subList(0, 4)));
        this.players[1].mainDeck.addDeck(new ArrayList<Card>(this.placedCards.subList(4, 8)));
    }

    public void resetPlacedCards() {
        this.placedCards = new ArrayList<Card>(this.players[0].mainDeck.getNewPlacedCards());
        this.placedCards.addAll(this.players[1].mainDeck.getNewPlacedCards());
    }

    public void resetCoveredCards() {
        for (Player p : this.players) {
            p.coveredCards = new ArrayList<Card>();
        }
    }

    public void noMatch() {
        retrieveCards();
        resetPlacedCards();
        resetCoveredCards();
    }

    public void placeCard(Card card, int index) {
        if (index < 4) {
            this.players[0].coveredCards.add(this.placedCards.get(index));
        }
        else {
            this.players[1].coveredCards.add(this.placedCards.get(index));
        }
        this.prevMoves.add(this.placedCards.get(index));
        this.placedCards.set(index, card);
    }

    public boolean hasMatch(int index) {
        Card cardToMatch = this.placedCards.get(index);
        if(!(this.prevMoves.isEmpty()) && this.prevMoves.contains(cardToMatch)) {
            return true;
        }
        long count = this.placedCards
                .stream()
                .filter(card -> card.equals(cardToMatch))
                .count();
        if (count > 1) {
            return true;
        }
        return false;
    }

    /**
     * This clears out any cards from prevMoves that don't have a match.
     */
    public void clearUnmatchedPrevMoves() {
        this.prevMoves = (HashSet) this.prevMoves
                .stream()
                .filter(card -> this.placedCards.contains(card))
                .collect(Collectors.toCollection(HashSet::new));
    }

    public boolean onClaim(Player p, int pile) {
        if (hasMatch(pile)) {
            placeCard(p.mainDeck.drawCard(), pile);
            clearUnmatchedPrevMoves();
            updateGameboard();
            return true;
        }
        return false;
    }

    public void updateGameboard() {
        ArrayList<Card> state = this.placedCards;
        ctrl.updateBoard(this, state);
    }

}
