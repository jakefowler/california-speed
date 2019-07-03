import { Card, cardRanks, cardSuits } from './Card';

class Deck {
    
    constructor(isFull) {
        this.deck = [];

        if (isFull) {
            for (const suit of cardSuits) {
                for (const rank of cardRanks) {
                    this.deck.push(new Card(suit, rank));
                }
            }
        }
    }

    drawOne() {
        return this.draw(1)[0];
    }

    drawAll() {
        return this.draw(this.deck.length);
    }

    draw(count) {
        return this.deck.splice(0, count);
    }

    peek() {
        return this.deck[0];
    }

    addToEnd(toAdd) {
        if (toAdd instanceof Deck) {
            this.deck = this.deck.concat(toAdd.drawAll());
        } else if (toAdd instanceof Card) {
            this.deck.push(toAdd);
        } else {
            this.deck = this.deck.concat(toAdd);
        }
    }

    addToStart(toAdd) {
        if (toAdd instanceof Deck) {
            this.deck = toAdd.drawAll().concat(this.deck);
        } else if (toAdd instanceof Card) {
            this.deck.unshift(toAdd);
        } else {
            this.deck = toAdd.concat(this.deck);
        }
    }

    isEmpty() {
        return this.deck.length == 0;
    }

    dealInto(...otherDecks) {
        while(!this.isEmpty()) {
            otherDecks.forEach(deck => {
                if (!this.isEmpty()) {
                    deck.addToEnd(this.drawOne());
                }
            });
        }
    }
}

export default Deck