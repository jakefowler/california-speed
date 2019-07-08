import React from 'react'
import Pile from './Pile'
import Deck from './Deck'
import {Card as CardModel} from '../models/Card'
import {Board as BoardModel} from '../models/Board'

class GameBoard extends React.Component {

    constructor(props) {
        super(props);

        let gameBoard = new BoardModel();
        this.state = {board: gameBoard, playablePile : gameBoard.playableMoves()};
    }

    handleCardClick(e, pile) {
        let {pileSelected, board} = this.state;
        console.log(board);

        if (pileSelected) {
            board.tryMatch(pile, pileSelected);

            if (board.gameOver()) {
                board.gameWon() ? this.props.gameWon() : this.props.gameLost();
            }

            this.setState({pileSelected : null, playablePile : board.playableMoves()});
        } else {
            this.setState({pileSelected : pile});
        }
    }

    render() {
        let {board, playablePile} = this.state;

        return (
            <div id="gameBoard">
                {!board.opponentDeck.isEmpty() && <Deck />}
                {board.piles.map((pile, i) => <Pile key={i} pile={pile} selected={pile === this.state.pileSelected} onclick={this.handleCardClick.bind(this)} />)}
                {!board.playerDeck.isEmpty() && <Deck />}
                {!playablePile && <div className='overlay'>
                    <div className='overlay-content'>
                        <h1>There are no playable piles</h1>
                        <button onClick={() => {
                            board.redrawPiles();
                            this.setState({playablePile : board.playableMoves()})
                            }}>Draw</button>
                    </div>
                </div>}
            </div>
        );
    }
}

export default GameBoard;