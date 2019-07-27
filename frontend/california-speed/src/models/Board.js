import Deck from './Deck'
import Card from './Card';

export class Board {
    constructor() {
        let mainDeck = new Deck(true);

        this.piles = Array.apply(null, Array(8));
        this.piles = this.piles.map(() => new Deck(false));

        console.log(this.piles);
        this.piles.forEach(pile => pile.addToStart(mainDeck.drawOne()));

        this.playerDeck = new Deck(false);
        this.opponentDeck = new Deck(false);

        mainDeck.dealInto(this.playerDeck, this.opponentDeck);

        while (!this.playableMoves()) {
            this.redrawPiles();
        }
    }

    playablePile(pile) {
        let otherPiles = this.piles.filter(p => p !== pile);

        for (const p of otherPiles) {
            if (p.peek().equalRank(pile.peek())) {
                return true;
            }
        }

        return false;
    }

    playableMoves() {
        for (const pile of this.piles) {
            if (this.playablePile(pile)) {
                return true;
            }
        }

        return false;
    }

    tryPlayOnPile(pile) {
        if (this.playablePile(pile)) {
            this.prevPlayedCard = pile.peek();
            
            this.playOnPile(pile);

            return true;
        } else if (!!this.prevPlayedCard && this.prevPlayedCard.equalRank(pile.peek())) {
            this.playOnPile(pile);

            if (!this.playableMoves() && !!this.noPlayablePilesCallback) {
                this.noPlayablePilesCallback();
            }

            return true;
        } else if (!this.prevPlayedCard) {
            if (!this.playableMoves() && !!this.noPlayablePilesCallback) {
                this.noPlayablePilesCallback();
            }

            return false;
        } else {
            return false;
        }
    }

    playOnPile(pile) {
        pile.addToStart(this.playerDeck.drawOne());

        if (this.gameWon() && !!this.gameOverCallback) {
            this.gameOverCallback();
        }
    }

    tryMatch(firstPile, secondPile) {
        if (firstPile !== secondPile && firstPile.peek().equalRank(secondPile.peek())) {
            firstPile.addToStart(this.playerDeck.drawOne());

            //Check if the player has already won by playing the first card before playing the second
            if (!this.gameWon()) {
                secondPile.addToStart(this.playerDeck.drawOne());
            }

            if (this.gameWon() && !!this.gameOverCallback) {
                this.gameOverCallback();
            }

            if (!this.playableMoves() && !!this.noPlayablePilesCallback) {
                this.noPlayablePilesCallback();
            }
        }
    }

    gameWon() {
        return this.playerDeck.isEmpty();
    }

    gameOver() {
        return this.gameWon() || this.opponentDeck.isEmpty();
    }

    onGameOver(callback) {
        this.gameOverCallback = callback;
    }

    onNoPlayablePiles(callback) {
        this.noPlayablePilesCallback = callback;
    }

    redrawPiles() {
        let opponentPiles = this.piles.slice(0, 4);
        let playerPiles = this.piles.slice(4);

        opponentPiles.forEach(pile => {
            pile.dealInto(this.opponentDeck);
            pile.addToStart(this.opponentDeck.drawOne());
        });

        playerPiles.forEach(pile => {
            pile.dealInto(this.playerDeck);
            pile.addToStart(this.playerDeck.drawOne());
        });
    }

    updatePiles(piles) {
        piles.forEach((pile, i) => {
            this.piles[i] = deckFromArray(pile.deck);
        });
    }
}

function deckFromArray(array) {
    let deck = new Deck(false);

    array.forEach((card) => deck.addToEnd(new Card(card.suit, card.rank)));

    return deck;
}

export default Board;
