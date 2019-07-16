import React from 'react'
import Pile from './Pile'
import Deck from './Deck'
import {Board as BoardModel} from '../models/Board'

class GameBoard extends React.Component {

    constructor(props) {
        super(props);

        let ws = new WebSocket('ws://localhost:8080');

        ws.onopen = () => {
            ws.send(JSON.stringify({push: {player: {name: this.props.playerName}}}));
        };

        ws.onmessage = (message) => {
            console.log(message.data);
            let data = JSON.parse(message.data)

            if (!!data.board) {
                this.updateBoardFromServer(data.board.piles);
            } else if (!!data.gameOver) {
                //this.gameOver(data.winner);
                data.winner.name === this.props.playerName ? this.props.gameWon() : this.props.gameLost();
            } else if (!!data.gameStart) {
                let gameBoard = new BoardModel();
                gameBoard.onGameOver(() => this.gameOver());
                gameBoard.onNoPlayablePiles(() => this.setState({playablePile : false}));

                this.setState({board: gameBoard, playablePile: true, topPlayerName: data.players[0].name, bottomPlayerName: data.players[1].name});
            }
        };

        this.state = {board: null, websocket : ws};
    }

    handleCardClick(e, pile) {
        let {board, websocket} = this.state;
        console.log(board);

        if (websocket.readyState == websocket.OPEN) {
            websocket.send(JSON.stringify({claim: {player: this.props.playerName, pile: board.piles.indexOf(pile)}}));
        }

        // if (board.tryPlayOnPile(pile)) {
        //     this.forceUpdate();
        // }
    }

    updateBoardFromServer(piles) {
        this.state.board.updatePiles(piles);
        this.forceUpdate();
    }

    gameOver() {
        this.props.gameWon()
        // let {board} = this.state;

        // board.gameWon() ? this.props.gameWon() : this.props.gameLost();
    }

    renderBoard() {
        let {board, playablePile, topPlayerName, bottomPlayerName} = this.state;

        return <div id="gameBoard">
            <div className='deck-row'>
                {!board.opponentDeck.isEmpty() && <Deck />}
                <div className='name right'>
                    <h1>{topPlayerName}</h1>
                </div>
            </div>
            {board.piles.map((pile, i) => <Pile key={i} pile={pile} selected={pile === this.state.pileSelected} onclick={this.handleCardClick.bind(this)} />)}
            <div className='deck-row'>
                {!board.playerDeck.isEmpty() && <Deck />}
                <div className='name'>
                    <h1>{bottomPlayerName}</h1>
                </div>
            </div>
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
    }

    render() {
        let {board} = this.state;

        return !!board ? this.renderBoard()
                : <div className='overlay'>
                    <div className='overlay-content'>
                        <h1>Waiting For Game Start</h1>
                    </div>
                </div>
    }
}

export default GameBoard;