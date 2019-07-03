import React from 'react'
import Card from './Card'
import {Card as CardModel} from '../models/Card'
import {Board as BoardModel} from '../models/Board'

class GameBoard extends React.Component {

    constructor(props) {
        super(props);
        this.state = {board: new BoardModel()};
    }

    render() {
        let board = this.state.board;

        return (
            <div id="gameBoard">
                {board.piles.map((pile, i) => <Card key={i} card={pile.peek()} />)}
            </div>
        );
    }
}

export default GameBoard;