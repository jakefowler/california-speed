package com.ezekielnewren;

import java.util.ArrayList;

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
        this.playerOneDeck.getDeck().forEach((card) -> card.setHasMatch(false));
        this.playerTwoDeck.getDeck().forEach((card) -> card.setHasMatch(false));
        this.playerOnePlacedDeck = this.playerOneDeck.getNewPlacedDeck();
        this.playerTwoPlacedDeck = this.playerTwoDeck.getNewPlacedDeck();
        updateMatches();
    }

    public void updateMatches() {
        ArrayList<Card> pOne = playerOnePlacedDeck.getDeck();
        ArrayList<Card> pTwo = playerTwoPlacedDeck.getDeck();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (i != j && j > i) {
                    if (pOne.get(i).compareRank(pOne.get(j))) {
                        playerOnePlacedDeck.getDeck().get(i).setHasMatch(true);
                        playerOnePlacedDeck.getDeck().get(j).setHasMatch(true);
                    }
                    if (pTwo.get(i).compareRank(pTwo.get(j))) {
                        playerTwoPlacedDeck.getDeck().get(i).setHasMatch(true);
                        playerTwoPlacedDeck.getDeck().get(j).setHasMatch(true);
                    }
                }
                if (pOne.get(i).compareRank(pTwo.get(j))) {
                    playerOnePlacedDeck.getDeck().get(i).setHasMatch(true);
                    playerTwoPlacedDeck.getDeck().get(j).setHasMatch(true);
                }
            }
        }
    }

}
