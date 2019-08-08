package com.ezekielnewren;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class Game {
    private static final Logger log = Log.getLogger(Game.class);
    Controller ctrl;
    public ArrayList<Card> placedCards;
    Player[] players;
    double penaltyTime = 1.0;
    boolean isDraw;
    boolean gameOver = false;

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
        if (!(this.prevMoves.isEmpty()) && this.prevMoves.contains(cardToMatch)) {
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

    public boolean drawExists() {
        for (int i = 0; i < this.placedCards.size(); i++) {
            if (hasMatch(i)) {
                return false;
            }
        }
        this.isDraw = true;
        return true;
    }

    public void clearUnmatchedPrevMoves() {
        this.prevMoves = (HashSet) this.prevMoves
                .stream()
                .filter(card -> this.placedCards.contains(card))
                .collect(Collectors.toCollection(HashSet::new));
    }

    /**
     * Checks if the card the player clicked on has a match, places card, checks for draw, updates board,
     * and returns zero for penalty time. If the move doesn't have a match then the penalty time is returned
     *
     * @param p Player who is making the move
     * @param pile int of index for the pile the player clicked on
     * @return double for penalty time. 0 if valid click and penalty time if invalid move
     */
    public double onClaim(Player p, int pile) {
        if (hasMatch(pile)) {
            placeCard(p.mainDeck.drawCard(), pile);
            clearUnmatchedPrevMoves();
            if (p.mainDeck.getSize() == 0) {
                gameOver(p);
            }
            if (drawExists()) {
                noMatch();
            }
            updateGameboard();
            return 0;
        }
        if (drawExists()) {
            noMatch();
            updateGameboard();
            return 0;
        }
        return this.penaltyTime;
    }

    public void updateGameboard() {
        ArrayList<Card> state = this.placedCards;
        ctrl.updateBoard(this, state, this.isDraw);
        this.isDraw = false;
    }

    public void gameOver(Player winner) {
        if (winner == null) throw new NullPointerException();
        ctrl.gameOver(this, winner);
        gameOver = true;
    }

    public void sendBoth(JSONObject json) {
        Arrays.asList(players).forEach((e)->e.send(json));
    }

    public Player opponent(Player p) {
        if (p.equals(players[0])) {
            return players[1];
        } else if (p.equals(players[1])) {
            return players[0];
        } else {
            return null;
        }
    }

    /**
     * Checks if a card was covered but the match was never finished. Would be called after a certain time of
     * no one making a guess. This would be used to prevent clicking random cards when there aren't any matches on the
     * board but one of the cards in the match is already covered.
     * @return ArrayList<Card> full of cards in placedCards that don't have matches on board but have match that was covered
     */
    public ArrayList<Card> checkForNeededHint() {
        ArrayList<Card> hintCards = this.placedCards
                .stream()
                .filter(card -> {
                    if (
                        this.placedCards
                                .stream()
                                .filter(card2 -> card2.equals(card))
                                .count() == 1) {
                        return true;
                    }
                    else {
                        return false;
                    }
                })
                .filter(card -> {
                    for (int i = 0; i < this.players.length; i++) {
                        if (players[i].coveredCards.contains(card)) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toCollection(ArrayList::new));
        return hintCards;
    }

}
