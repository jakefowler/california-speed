import React from "react";
const pathToCards = require.context('../../public/card-images/', true);

class Card extends React.Component {

    async getCardURL() {
        let card = this.props.card;

        let uri = pathToCards(`card-images/${card.rank}${card.suit}.png`, true);
        return uri;
    }

    render() {
        return <img src={this.getCardURL()}></img>
    }
}

export default Card;