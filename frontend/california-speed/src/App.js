import React from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';
import GameBoard from './components/Board'

class App extends React.Component {

  constructor(props) {
    super(props);

    this.state = {gameStarted : false, gameOver: false, gameWon: false, playerName: ''};
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
    return (
      <div className="container">
        {this.state.gameStarted && <GameBoard playerName={this.state.playerName} gameLost={this.gameLost.bind(this)} gameWon={this.gameWon.bind(this)} />}
        {!this.state.gameStarted && <div className='overlay'>
          <div className='overlay-content'>
            <h1>Welcome to <em>California Speed</em></h1>
            <div class="input-group mb-3">
              <input type="text" class="form-control" placeholder='Enter Name' onChange={(e) => this.setState({playerName: e.target.value})} value={this.state.playerName} aria-label="Player name" aria-describedby="start-game-button" />
              <div class="input-group-append">
                <button class="btn btn-dark" type="button" id="start-game-button" onClick={() => this.setState({gameStarted : true})}>Start Game</button>
              </div>
            </div>
            {/* <input type='text' placeholder='Enter Name' onChange={(e) => this.setState({playerName: e.target.value})} value={this.state.playerName}></input> */}
            {/* <button onClick={() => this.setState({gameStarted : true})}>Start Game</button> */}
          </div>
        </div>}
        {this.state.gameOver && <div className='overlay'>
          <div className='overlay-content'>
            <h1>You {this.state.gameWon ? 'Win!' : 'Lose'}</h1>
            <button onClick={() => this.resetGame()}>Main Menu</button>
          </div>
        </div>}
      </div>
    );
  }
}

export default App;
