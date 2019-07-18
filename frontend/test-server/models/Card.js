let cardRanks = [2, 3, 4, 5, 6, 7, 8, 9, 10, 'jack', 'queen', 'king', 'ace'];
let cardSuits = ['heart', 'diamond', 'club', 'spade'];

class Card {

    constructor(suit, rank) {
        if (!cardRanks.includes(rank) || !cardSuits.includes(suit)) {
            throw new RangeError('Invalid suit or card rank');
        }
        this.suit = suit;
        this.rank = rank;
    }

    equalRank(otherCard) {
        return this.rank === otherCard.rank;
    }

    equalSuit(otherCard) {
        return this.suit === otherCard.suit;
    }

    equals(otherCard) {
        return this.equalRank(otherCard) && this.equalSuit(otherCard);
    }
}

module.exports = {Card, cardRanks, cardSuits};
