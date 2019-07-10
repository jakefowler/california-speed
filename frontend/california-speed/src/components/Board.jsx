import React from 'react'
import Pile from './Pile'
import Deck from './Deck'
import {Board as BoardModel} from '../models/Board'

class GameBoard extends React.Component {

    constructor(props) {
        super(props);

        let gameBoard = new BoardModel();
        gameBoard.onGameOver(() => this.gameOver());
        gameBoard.onNoPlayablePiles(() => this.setState({playablePile : false}));
        this.state = {board: gameBoard, playablePile : gameBoard.playableMoves()};
    }

    handleCardClick(e, pile) {
        let {prevCardSelected, board} = this.state;
        console.log(board);

        board.tryPlayOnPile(pile);
        this.setState({prevCardSelected : null});
        // if (prevCardSelected) {
        //     board.tryMatch(pile, pileSelected);

        //     this.setState({prevCardSelected : null});
        // } else {
        //     if (board.playOnPile(pile)) {
        //         this.setState({prevCardSelected : pile.peek()});
        //     }
        // }
    }

    gameOver() {
        let {board} = this.state;

        board.gameWon() ? this.props.gameWon() : this.props.gameLost();
    }

    render() {
        let {board, playablePile} = this.state;

        return (
            <div id="gameBoard">
                {!board.opponentDeck.isEmpty() && <Deck />}
                {board.piles.map((pile, i) => <Pile key={i} pile={pile} selected={pile === this.state.pileSelected} onclick={this.handleCardClick.bind(this)} />)}
                {!board.playerDeck.isEmpty() && <Deck />}
                {!playablePile && !board.gameWon() && <div className='overlay'>
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