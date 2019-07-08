import React from 'react'
import Card from './Card'

class Pile extends React.Component {
    render() {
        return <div className={`pile ${this.props.selected ? 'selected' : ''}`} onClick={(e) => this.props.onclick(e, this.props.pile)}><Card card={this.props.pile.peek()} /></div>
    }
}

export default Pile