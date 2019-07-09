import React from "react";

class Card extends React.Component {

    getCardURL() {
        let card = this.props.card;
        return `${process.env.PUBLIC_URL}/card_images/svg-cards.svg#${card.suit}_${card.rank}`;
    }

    render() {
        let baseURL = `${process.env.PUBLIC_URL}/card_images/svg-cards.svg`;
        let card = this.props.card;

        return <svg xmlns="http://www.w3.org/2000/svg" xmlnsXlink="http://www.w3.org/1999/xlink" viewBox="0 0 169.075 244.64" >
            <use xlinkHref={`${baseURL}#base`} x="0" y="236.52" width="100%" height="100%" />
            <use xlinkHref={`${baseURL}#${card.suit}`} x="0" y="0" transform="matrix(-6,0,0,-6,82.9934495,115.70272)" width="100%" height="100%" />
            <use xlinkHref={`${baseURL}#${typeof card.rank === 'string' ? card.rank : `n_${card.rank}`}`} x="5.25" y="37.403" transform={card.rank === 10 ? "scale(2.5,1)" : "scale(1.5,1)"} width="100%" height="100%" />
            <use xlinkHref={`${baseURL}#${typeof card.rank === 'string' ? card.rank : `n_${card.rank}`}`} x="164.8249" y="206.24" transform={card.rank === 10 ? "matrix(-2.5,0,0,-1,570.1875,413.35)" : "matrix(-1.5,0,0,-1,407.1875,413.35)" } width="100%" height="100%" />
            {/* <use xlinkHref={this.getCardURL()} x="0" y="0"/> */}
        </svg>
    }
}

export default Card;