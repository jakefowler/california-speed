import React from 'react'

class Deck extends React.Component {
    render() {
        let baseURL = `${process.env.PUBLIC_URL}/card_images/card_back_15.svg`;

        return <div className={`deck ${this.props.isTop ? 'top' : ''}`}>
            <img src={baseURL} alt=""/>
        </div>
    }
}

export default Deck