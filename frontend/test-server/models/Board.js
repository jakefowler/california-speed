const Deck = require('./Deck');

class Board {
    constructor(players) {
        let mainDeck = new Deck(true);

        this.players = players.map((player) => { return {name: player.name, deck: new Deck(false)} });
        console.log(this.players);

        this.piles = Array.apply(null, Array(8));
        this.piles = this.piles.map(() => new Deck(false));

        console.log(this.piles);
        this.piles.forEach(pile => pile.addToStart(mainDeck.drawOne()));

        // this.playerDeck = new Deck(false);
        // this.opponentDeck = new Deck(false);
        console.log(...this.players.map(player => player.deck));

        mainDeck.dealInto(...this.players.map(player => player.deck));

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

    tryPlayOnPile(pile, player) {
        if (this.playablePile(pile)) {
            this.prevPlayedCard = pile.peek();
            
            this.playOnPile(pile, player);

            return true;
        } else if (!!this.prevPlayedCard && this.prevPlayedCard.equalRank(pile.peek())) {
            this.playOnPile(pile, player);

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

    playOnPile(pile, player) {
        pile.addToStart(player.deck.drawOne());

        if (this.gameOver() && !!this.gameOverCallback) {
            this.gameOverCallback();
        }
    }

    tryMatch(firstPile, secondPile, player) {
        if (firstPile !== secondPile && firstPile.peek().equalRank(secondPile.peek())) {
            firstPile.addToStart(player.deck.drawOne());

            //Check if the player has already won by playing the first card before playing the second
            if (!this.gameOver()) {
                secondPile.addToStart(player.deck.drawOne());
            }

            if (this.gameOver() && !!this.gameOverCallback) {
                this.gameOverCallback();
            }

            if (!this.playableMoves() && !!this.noPlayablePilesCallback) {
                this.noPlayablePilesCallback();
            }
        }
    }

    gameWon(player) {
        return player.deck.isEmpty();
    }

    gameOver() {
        return this.players.some(player => player.deck.isEmpty());
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
            pile.dealInto(this.players[0].deck);
            pile.addToStart(this.players[0].deck.drawOne());
        });

        playerPiles.forEach(pile => {
            pile.dealInto(this.players[1].deck);
            pile.addToStart(this.players[1].deck.drawOne());
        });
    }

    updatePiles(piles) {
        piles.forEach((pile, i) => {
            this.piles[i] = deckFromArray(pile.deck);
        });
    }
}

module.exports = Board;
