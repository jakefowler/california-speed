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

  gameWon() {
    this.setState({gameOver: true, gameWon: true});
  }

  gameLost() {
    this.setState({gameOver: true, gameWon: false});
  }

  resetGame() {
    this.setState({gameStarted : false, gameOver: false, gameWon: false});
  }

  render() {
    let {cardStyleSelected} = this.state;

    return (
      <div>
        <div className="container">
          {this.state.gameStarted && <GameBoard cardStyleSelected={cardStyleSelected} playerName={this.state.playerName} gameLost={this.gameLost.bind(this)} gameWon={this.gameWon.bind(this)} />}
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
          {!this.state.gameStarted && <div className='overlay'>
            <div className='overlay-content'>
              <h1>Welcome to <em>California Speed</em></h1>
              <div className="input-group mb-3">
                <input type="text" className="form-control" placeholder='Enter Name' onChange={(e) => this.setState({playerName: e.target.value})} value={this.state.playerName} aria-label="Player name" aria-describedby="start-game-button" />
                <div className="input-group-append">
                  <button className="btn btn-dark" type="button" id="start-game-button" onClick={() => this.setState({gameStarted : true})}>Start Game</button>
                </div>
              </div>
            </div>
          </div>}
          {this.state.gameOver && <div className='overlay'>
            <div className='overlay-content'>
              <h1>You {this.state.gameWon ? 'Win! 😊' : 'Lose 😞'}</h1>
              <button className="btn btn-dark" onClick={() => this.resetGame()}>Main Menu</button>
            </div>
          </div>}
        </div>
    );
  }
}

export default App;
