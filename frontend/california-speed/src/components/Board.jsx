import React from 'react'
import Pile from './Pile'
import Deck from './Deck'
import {Card as CardModel} from '../models/Card'
import {Board as BoardModel} from '../models/Board'

class GameBoard extends React.Component {

    constructor(props) {
        super(props);
        this.state = {board: new BoardModel()};
    }

    handleCardClick(e, pile) {
        let {pileSelected, board} = this.state;
        console.log(e);
        if (pileSelected) {
            board.tryMatch(pile, pileSelected);
            this.setState({pileSelected : null});
            console.log('selected');
        } else {
            this.setState({pileSelected : pile});
            console.log('not selected');
        }
    }

    render() {
        let board = this.state.board;

        return (
            <div id="gameBoard">
                <Deck />
                {board.piles.map((pile, i) => <Pile key={i} pile={pile} selected={pile === this.state.pileSelected} onclick={this.handleCardClick.bind(this)} />)}
                <Deck />
            </div>
        );
    }
}

export default GameBoard;