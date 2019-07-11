package com.ezekielnewren;

public class GameController {

    private Deck playerOneDeck;
    private Deck playerTwoDeck;
    private Deck playerOnePlacedDeck;
    private Deck playerTwoPlacedDeck;

    public GameController() {
        this.playerOneDeck = new Deck();
        this.playerOneDeck.fillDeck();
        this.playerOneDeck.shuffle();
        this.playerTwoDeck = playerOneDeck.splitDeck();
        this.playerOnePlacedDeck = playerOneDeck.getNewPlacedDeck();
        this.playerTwoPlacedDeck = playerTwoDeck.getNewPlacedDeck();
    }

    public void noMatch() {
        this.playerOneDeck.addDeck(this.playerOnePlacedDeck.getDeck());
        this.playerTwoDeck.addDeck(this.playerTwoPlacedDeck.getDeck());
        this.playerOnePlacedDeck = this.playerOneDeck.getNewPlacedDeck();
        this.playerTwoPlacedDeck = this.playerTwoDeck.getNewPlacedDeck();
    }
}
