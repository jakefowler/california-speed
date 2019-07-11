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
            addCard(new Card('s', i));
            addCard(new Card('h', i));
            addCard(new Card('d', i));
            addCard(new Card('c', i));
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

    /**
     * Replaces card at location with passed in card and places the old card at the back of the deck.
     * Used for the placed down deck of four visible cards that both players will have.
     *
     * @param index int 0 to 3 for the location the card is to be placed
     * @param card Card that should be put in the location
     * @return boolean determining whether card was successfully placed
     */
    public boolean placeCard(int index, Card card) {
        if (index > 3 || index < 0) {
            return false;
        }
        this.deck.add(this.deck.get(index));
        this.deck.add(index, card);
        return true;
    }

}
