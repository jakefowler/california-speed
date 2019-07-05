import React from 'react';
import './App.css';
import GameBoard from './components/Board'

class App extends React.Component {

  constructor(props) {
    super(props);

    this.state = {gameOver: false, gameWon: false};
  }

  gameWon() {
    this.setState({gameOver: true, gameWon: true});
  }

  gameLost() {
    this.setState({gameOver: true, gameWon: false});
  }

  render() {
    return (
      <div className="container">
        <GameBoard gameLost={this.gameLost.bind(this)} gameWon={this.gameWon.bind(this)} />
        {this.state.gameOver && <div className='overlay'>
          <div className='overlay-content'>
            <h1>You {this.state.gameWon ? 'Win!' : 'Lose'}...</h1>
          </div>
        </div>}
      </div>
    );
  }
}

export default App;
