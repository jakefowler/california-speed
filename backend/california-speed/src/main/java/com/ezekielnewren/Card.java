package com.ezekielnewren;

public class Card {

    enum Suit {
        SPADE,
        HEART,
        DIAMOND,
        CLUB
    }

    private Suit suit;
    private int rank;

    public Card(char suit, int rank) {
        if (suit == 'S') {
            this.suit = Suit.SPADE;
        }
        else if (suit == 'H') {
            this.suit = Suit.HEART;
        }
        else if (suit == 'D') {
            this.suit = Suit.DIAMOND;
        }
        else if (suit == 'C') {
            this.suit = Suit.CLUB;
        }
        this.rank = rank;
    }

    public Card(String suit, int rank) {
        suit = suit.toLowerCase();
        if (suit.equals("spade")) {
            this.suit = Suit.SPADE;
        }
        else if (suit.equals("heart")) {
            this.suit = Suit.HEART;
        }
        else if (suit.equals("diamond")) {
            this.suit = Suit.DIAMOND;
        }
        else if (suit.equals("club")) {
            this.suit = Suit.CLUB;
        }
        this.rank = rank;
    }

    public String getSuit() {
        return this.suit.toString();
    }

    public int getRank() {
        return this.rank;
    }

    public boolean compareCards(Card card) {
        if (!this.suit.toString().equals(card.getSuit())) {
            return false;
        }
        if (this.rank != card.getRank()) {
            return false;
        }
        return true;
    }

}
