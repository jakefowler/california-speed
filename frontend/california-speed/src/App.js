import React from 'react';
import './App.css';
import GameBoard from './components/Board'

class App extends React.Component {

  constructor(props) {
    super(props);

    this.state = {gameStarted : false, gameOver: false, gameWon: false};
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
        {this.state.gameStarted && <GameBoard gameLost={this.gameLost.bind(this)} gameWon={this.gameWon.bind(this)} />}
        {!this.state.gameStarted && <div className='overlay'>
          <div className='overlay-content'>
            <h1>Welcome to <em>California Speed</em></h1>
            <button onClick={() => this.setState({gameStarted : true})}>Start Game</button>
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
