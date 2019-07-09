import React from 'react'

class Deck extends React.Component {
    render() {
        return (<div className='deck'> 
            <img src={`${process.env.PUBLIC_URL}/card_images/red_back.png`} alt="deck"></img>
        </div>);
    }
}

export default Deck