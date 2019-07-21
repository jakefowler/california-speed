import React from 'react'
import Pile from './Pile'
import Deck from './Deck'
import {Board as BoardModel} from '../models/Board'

class GameBoard extends React.Component {

    constructor(props) {
        super(props);

        let ws = new WebSocket('wss://www.ezekielnewren.com:8080');

        ws.onopen = () => {
            ws.send(JSON.stringify({request: {player: {name: this.props.playerName}}}));
        };

        ws.onmessage = (message) => {
            console.log(message.data);
            let data = JSON.parse(message.data)

            if (typeof data.push !== 'undefined') {
                if (!!data.push.board) {
                    this.updateBoardFromServer(data.push.board.pile);
                } else if (!!data.push.gameOver) {
                    //this.gameOver(data.winner);
                    data.push.winner.name === this.props.playerName ? this.props.gameWon() : this.props.gameLost();
                } else if (!!data.push.gameStart) {
                    let gameBoard = new BoardModel();
                    gameBoard.onGameOver(() => this.gameOver());
                    gameBoard.onNoPlayablePiles(() => this.setState({playablePile : false}));

                    this.setState({board: gameBoard, playablePile: true, topPlayerName: data.push.players[0].name, bottomPlayerName: data.push.players[1].name});
                }
            } else if (typeof data.response !== 'undefined') {
                // server is sending us our id
                if (this.state.playerId === '' && !!data.response.player) {
                    this.state.playerId = data.response.player.id;
                }
            } else {
                // unknown message type throw error
            }

        };

        ws.onclose = (ev) => {
            // TODO tell the player that they have been disconnected
            // websocket close event codes and meanings https://developer.mozilla.org/en-US/docs/Web/API/CloseEvent
            console.log("websocket closed code: "+ev.code+" reason: "+ev.reason+" readystate: "+ws.readyState);
            if (ev.code == 1000) {
                // normal closure
            } else if (ev.code == 1001) {
                // going away e.g. user closes the tab
            } else if (ev.code == 1006) {
                // abnormal closure e.g. websocket failed to connect to the server
            } else if (ev.code == 1015) {
                // TLS Handshake problem i.e. a secure connection cannot be established

            } else {
                // some other problem has occurred
            }
            //console.log("you have been disconnected")
        };

        this.state = {board: null, websocket : ws};
    }

    handleCardClick(e, pile) {
        let {board, websocket} = this.state;
        console.log(board);

        if (websocket.readyState == websocket.OPEN) {
            websocket.send(JSON.stringify(
                {
                    request: { 
                        action: { 
                            player: {
                                name: this.props.playerName
                            }, 
                            claim: { 
                                pile: board.piles.indexOf(pile)
                            }
                        }
                    }
                })
            );
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
