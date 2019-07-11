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
        suit = Character.toLowerCase(suit);
        if (suit == 's') {
            this.suit = Suit.SPADE;
        }
        else if (suit == 'h') {
            this.suit = Suit.HEART;
        }
        else if (suit == 'd') {
            this.suit = Suit.DIAMOND;
        }
        else if (suit == 'c') {
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

    public boolean compareRank(Card card) {
        if (this.rank != card.getRank()) {
            return false;
        }
        return true;
    }

}
