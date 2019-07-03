import Card from './Card'
import Deck from './Deck'

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
    }

    playablePile(pile) {
        let otherPiles = this.piles.filter(p => p === pile);

        otherPiles.forEach(p => {
            if (p.peek().equalRank(pile.peek())) {
                return true;
            }
        });

        return false;
    }
}

export default Board;
