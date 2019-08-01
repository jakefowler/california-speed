import React from 'react'
import Pile from './Pile'
import Deck from './Deck'
import {Board as BoardModel} from '../models/Board'
import { CSSTransition } from "react-transition-group";
import penaltyImage from '../img/Family_Feud_Strike_Indicator.png'

class GameBoard extends React.Component {

    constructor(props) {
        super(props);

        props.websocket.addEventListener('message', (message) => {
            let data = JSON.parse(message.data)
            console.log(data);
            if (!!data.push) {
                if (!!data.push.board) {
                    this.updateBoardFromServer(data.push.board.pile);
                    if (data.push.board.players.find(player => player.name === this.props.playerName).penalty) {
                        this.setState({penalty: true});
                        setTimeout(() => this.setState({penalty: false}), 1000);
                    }
                } else if (!!data.push.gameOver) {
                    data.push.winner.name === this.props.playerName ? this.props.gameWon() : this.props.gameLost();
                }
            }
        });
        // let ws = new WebSocket('ws://localhost:8080');
        //let ws = new WebSocket('wss://www.ezekielnewren.com:8080');
        
        // ws.onopen = () => {
        //     ws.send(JSON.stringify({request: {player: {name: this.props.playerName}}}));
        // };

        // ws.onmessage = (message) => {
        //     console.log(message.data);
        //     let data = JSON.parse(message.data)

        //     if (!!data.push) {
        //         if (!!data.push.board) {
        //             this.updateBoardFromServer(data.push.board.pile);
        //         } else if (!!data.push.gameOver) {
        //             //this.gameOver(data.winner);
        //             data.push.winner.name === this.props.playerName ? this.props.gameWon() : this.props.gameLost();
        //         } else if (!!data.push.gameStart) {
        //             let gameBoard = new BoardModel();
        //             gameBoard.onGameOver(() => this.gameOver());
        //             gameBoard.onNoPlayablePiles(() => this.setState({playablePile : false}));

        //             this.setState({board: gameBoard, playablePile: true, topPlayerName: data.push.players[0].name, bottomPlayerName: data.push.players[1].name});
        //         }
        //     } else if (!!data.response) {
        //         // server is sending us our id
        //         if (this.state.playerId === '' && !!data.response.player) {
        //             this.setState({playerId: data.response.player.id});
        //         }
        //     } else {
        //         // unknown message type throw error
        //     }

        // };

        // ws.onclose = (ev) => {
        //     // TODO tell the player that they have been disconnected
        //     // websocket close event codes and meanings https://developer.mozilla.org/en-US/docs/Web/API/CloseEvent
        //     console.log("websocket closed code: "+ev.code+" reason: "+ev.reason+" readystate: "+ws.readyState);
        //     if (ev.code == 1000) {
        //         // normal closure
        //     } else if (ev.code == 1001) {
        //         // going away e.g. user closes the tab
        //     } else if (ev.code == 1006) {
        //         // abnormal closure e.g. websocket failed to connect to the server
        //     } else if (ev.code == 1015) {
        //         // TLS Handshake problem i.e. a secure connection cannot be established

        //     } else {
        //         // some other problem has occurred
        //     }
        //     //console.log("you have been disconnected")
        // };

        let gameBoard = new BoardModel();
        gameBoard.onGameOver(() => this.gameOver());
        gameBoard.onNoPlayablePiles(() => this.setState({playablePile : false}));

        this.state = {board: gameBoard, playablePile: true, topPlayerName: props.players[0].name, bottomPlayerName: props.players[1].name, websocket : props.websocket, penalty: false};
    }

    handleCardClick(e, pile) {
        let {board, websocket} = this.state;
        console.log(board);

        if (websocket.readyState === websocket.OPEN) {
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
        let {board, playablePile, topPlayerName, bottomPlayerName, penalty} = this.state;

        return <div id="gameBoard">
            <div className='deck-row'>
                {!board.opponentDeck.isEmpty() && <Deck />}
                <div className='name right'>
                    <h1>{topPlayerName}</h1>
                </div>
            </div>
            <div className='pile-row'>
                {board.piles.slice(0, 4).map((pile, i) => <Pile key={i} pile={pile} selected={pile === this.state.pileSelected} onclick={this.handleCardClick.bind(this)} cardStyleSelected={this.props.cardStyleSelected} />)}
            </div>
            <div className='pile-row'>
                {board.piles.slice(4, 8).map((pile, i) => <Pile key={i} pile={pile} selected={pile === this.state.pileSelected} onclick={this.handleCardClick.bind(this)} cardStyleSelected={this.props.cardStyleSelected} />)}
            </div>
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
            <CSSTransition classNames="overlayFadeOut" in={penalty} enter={false} timeout={200} unmountOnExit>
                <div className='overlay no-dim'>
                    <div className='overlay-content'>
                        {/* <h1 style={{fontSize: '100px'}}>❌</h1> */}
                        <img src={penaltyImage}></img>
                    </div>
                </div>
            </CSSTransition>
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
