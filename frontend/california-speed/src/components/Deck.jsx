import React from 'react'

class Deck extends React.Component {
    render() {
        let baseURL = `${process.env.PUBLIC_URL}/card_images/card-image-parts.svg`;

        return <div className='deck'>
            <svg xmlns="http://www.w3.org/2000/svg" xmlnsXlink="http://www.w3.org/1999/xlink" viewBox="0 0 169.075 244.64" >
                <use xlinkHref={`${baseURL}#back`} x="0" y="0" fill="#ff6961"/>
            </svg>
        </div>
    }
}

export default Deck