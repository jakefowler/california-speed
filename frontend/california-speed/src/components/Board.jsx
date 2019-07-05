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
        console.log(board);

        if (pileSelected) {
            board.tryMatch(pile, pileSelected);

            if (board.gameOver()) {
                board.gameWon() ? this.props.gameWon() : this.props.gameLost();
            }

            this.setState({pileSelected : null});
        } else {
            this.setState({pileSelected : pile});
        }
    }

    render() {
        let board = this.state.board;

        return (
            <div id="gameBoard">
                {!board.opponentDeck.isEmpty() && <Deck />}
                {board.piles.map((pile, i) => <Pile key={i} pile={pile} selected={pile === this.state.pileSelected} onclick={this.handleCardClick.bind(this)} />)}
                {!board.playerDeck.isEmpty() && <Deck />}
            </div>
        );
    }
}

export default GameBoard;