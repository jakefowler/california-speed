import React from "react";

class Card extends React.Component {

    getCardURL() {
        let card = this.props.card;
        return `${process.env.PUBLIC_URL}/card_images/${card.rank}${card.suit}.png`;
    }

    render() {
        return <img src={this.getCardURL()}></img>
    }
}

export default Card;