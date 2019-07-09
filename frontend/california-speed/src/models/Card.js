export let cardRanks = [2, 3, 4, 5, 6, 7, 8, 9, 10, 'J', 'Q', 'K', 'A'];
export let cardSuits = ['H', 'D', 'C', 'S'];

export class Card {

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

export default Card;