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
     * This method is for retrieving four cards and returning them as an ArrayList to be placed in front of the player.
     *
     * @return ArrayList filled with four cards from main deck.
     */
    public ArrayList<Card> getNewPlacedCards() {
        ArrayList<Card> placedCards = new ArrayList<Card>(this.deck.subList(0, 4));
        this.deck.subList(0, 4).clear();
        return placedCards;
    }

    public Card drawCard() {
        Card drawnCard = this.deck.get(0);
        this.deck.remove(0);
        return drawnCard;
    }

    public Card getCard(int index) {
        return this.deck.get(index);
    }

    public void setCard(int index, Card card) {
        this.deck.set(index, card);
    }

}
