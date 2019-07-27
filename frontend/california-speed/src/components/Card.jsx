import React from "react";

class Card extends React.Component {

    getCardURL() {
        let card = this.props.card;
        return `${process.env.PUBLIC_URL}/card_images/svg-cards_rename.svg#${card.rank}${card.suit}`;
    }

    render() {
        let {card, cardStyleSelected} = this.props;

        return cardStyleSelected === 'traditional' ? <svg xmlns="http://www.w3.org/2000/svg" xmlnsXlink="http://www.w3.org/1999/xlink" viewBox="0 0 169.075 244.64" >
            <use xlinkHref={this.getCardURL()} x="0" y="0"/>
        </svg> :
        <img src={`${process.env.PUBLIC_URL}/card_images/${cardStyleSelected}/${card.rank}${card.suit}.svg`} alt={`${card.rank}${card.suit}`}></img>
        
        // <img src={`${process.env.PUBLIC_URL}/card_images/poker-old-noflip-noindex-qr-Goodall-Goodall/${card.rank}${card.suit}.svg`}></img>

        // return <svg xmlns="http://www.w3.org/2000/svg" xmlnsXlink="http://www.w3.org/1999/xlink" viewBox="0 0 169.075 244.64" >
        //     <use xlinkHref={this.getCardURL()} x="0" y="0"/>
        // </svg>
    }
}

export default Card;