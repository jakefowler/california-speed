import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap';
import './App.css';
import GameBoard from './components/Board'

let cardStyles = [
  {
    name: 'Traditional',
    code: 'traditional'
  },
  {
    name: 'Ghost',
    code: 'poker-ghost-qr'
  },
  {
    name: 'Inverted Ghost',
    code: 'poker-ghost-qr-inverse'
  },
  {
    name: 'Olde',
    code: 'poker-old-noflip-noindex-qr-Goodall-Goodall'
  },
  {
    name: 'Four Color',
    code: 'poker-qr-4colour'
  },
  {
    name: 'Plain',
    code: 'poker-plain-qr'
  }
];

class App extends React.Component {

  constructor(props) {
    super(props);

    this.state = {
      gameStarted : false, 
      gameOver: false, 
      gameWon: false, 
      playerName: '',
      cardStyleSelected: 'traditional'
    };
  }

  startGame() {
    //let ws = new WebSocket('ws://localhost:8080');
    let ws = new WebSocket('wss://www.ezekielnewren.com:8080');
    
    ws.onopen = () => {
      ws.send(JSON.stringify({request: {player: {name: this.state.playerName}}}));
    };

    ws.onmessage = (message) => {
      console.log(message.data);
      let data = JSON.parse(message.data)

      if (!!data.push) {
        if (!!data.push.gameStart) {
          this.setState({players: data.push.players, gameStarted: true});
        }
      } else if (!!data.response) {
        // server is sending us our id
        if (this.state.playerId === '' && !!data.response.player) {
          this.setState({playerId: data.response.player.id});
        }
      } else {
        // unknown message type throw error
      }

    };

    ws.onclose = (ev) => {
      // TODO tell the player that they have been disconnected
      // websocket close event codes and meanings https://developer.mozilla.org/en-US/docs/Web/API/CloseEvent
      console.log("websocket closed code: "+ev.code+" reason: "+ev.reason+" readystate: "+ws.readyState);
      // other intesting but unnecessary codes: 1001 going away e.g. user closes the tab, 1006 abnormal closure e.g. websocket failed to connect to the server 1015 TLS Handshake problem i.e. a secure connection cannot be established
      if (ev.code != 1000) {
          // a problem has occurred with the websocket notify the player
      }
      // call some clean up function
      //console.log("you have been disconnected")
    };

    this.setState({inLobby: true, websocket: ws});
  }

  gameWon() {
    this.setState({gameOver: true, gameWon: true});
  }

  gameLost() {
    this.setState({gameOver: true, gameWon: false});
  }

  resetGame() {
    this.setState({gameStarted : false, gameOver: false, gameWon: false, inLobby: false});
  }

  render() {
    let {cardStyleSelected, gameStarted, inLobby, gameOver, gameWon, players, websocket, playerName} = this.state;

    return (
      <div>
        <div className="container">
          {gameStarted && 
            <GameBoard 
              cardStyleSelected={cardStyleSelected} 
              players={players}
              playerName={playerName}
              websocket={websocket}
              gameLost={this.gameLost.bind(this)} 
              gameWon={this.gameWon.bind(this)} 
            />
          }
          <div className="dropdown" id="cardSkinSelector">
            <button className="btn btn-outline-light" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
              &#9776;
            </button>
            <div className="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenuButton">
              <h6 className="dropdown-header">Select Card Style</h6>
              {cardStyles.map((style, i) => {
                return <button key={i} className={`dropdown-item ${style.code === cardStyleSelected ? 'active' : ''}`} onClick={() => this.setState({cardStyleSelected: style.code})} type="button">{style.name}</button>
              })}
              <div className="dropdown-divider"></div>
              <p className="text-muted px-4"><small>Card styles from <a href="https://www.me.uk/cards/" target="_blank">me.uk/cards/</a></small></p>
            </div>
          </div>
          </div>
          {!gameStarted && !inLobby && <div className='overlay'>
            <div className='overlay-content'>
              <h1>Welcome to <em>California Speed</em></h1>
              <div className="input-group mb-3">
                <input type="text" className="form-control" placeholder='Enter Name' onChange={(e) => this.setState({playerName: e.target.value})} value={playerName} aria-label="Player name" aria-describedby="start-game-button" />
                <div className="input-group-append">
                  <button className="btn btn-dark" type="button" id="start-game-button" onClick={() => this.startGame()}>Start Game</button>
                </div>
              </div>
            </div>
          </div>}
          {!gameStarted && inLobby && <div className='overlay'>
            <div className='overlay-content'>
              <h1>Waiting For Game Start</h1>
            </div>
          </div>}
          {gameOver && <div className='overlay'>
            <div className='overlay-content'>
              <h1>You {gameWon ? 'Win! 😊' : 'Lose 😞'}</h1>
              <button className="btn btn-dark" onClick={() => this.resetGame()}>Main Menu</button>
            </div>
          </div>}
        </div>
    );
  }
}

export default App;
