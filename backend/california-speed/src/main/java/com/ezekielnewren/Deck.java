package com.ezekielnewren;
import java.util.*;

public class Deck {

    private ArrayList<Card> deck;

    public Deck() {
        this.deck = new ArrayList<Card>();
    }

    /**
     * Adds all 52 cards of a standard deck
     */
    public void fillDeck() {
        for (int i = 1; i < 14; i++) {
            addCard(new Card('S', i));
            addCard(new Card('H', i));
            addCard(new Card('D', i));
            addCard(new Card('C', i));
        }
    }

    public void shuffle() {
        Collections.shuffle(this.deck);
    }

    /**
     * Splits the deck in half and returns the firstHalf. On odd deck size values the main deck gets the extra card.
     *
     * @return Deck containing half of the cards
     */
    public Deck splitDeck() {
        int size = this.deck.size();
        Deck deckToReturn = new Deck();
        deckToReturn.addDeck(new ArrayList<Card>(this.deck.subList(0, size/2)));
        this.deck.subList(0, size/2).clear();
        return deckToReturn;
    }

    public int getSize() {
        return this.deck.size();
    }

    public void addCard(Card card) {
        this.deck.add(card);
    }

    public void addDeck(ArrayList<Card> deckToAdd) {
        this.deck.addAll(deckToAdd);
    }

    public ArrayList<Card> getDeck() {
        return this.deck;
    }

    /**
     * This method is for retrieving four cards and returning them as a new Deck to be placed in front of the player.
     * The placed cards are kept as a deck so more cards can be added and they can be retrieved when no cards can be
     * place. They are then able to be added back into the main deck with the addDeck() method.
     *
     * @return Deck filled with four cards from main deck.
     */
    public Deck getNewPlacedDeck() {
        Deck placedDeck = new Deck();
        placedDeck.addDeck(new ArrayList<Card>(this.deck.subList(0, 4)));
        this.deck.subList(0, 4).clear();
        return placedDeck;
    }

    public Card drawCard() {
        Card drawnCard = this.deck.get(0);
        this.deck.remove(0);
        return drawnCard;
    }

}
