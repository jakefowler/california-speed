const WebSocket = require('ws');
const BoardModel = require('./models/Board');

const wss = new WebSocket.Server({ port: 8080 });

var board = null
var players = [];

wss.on('connection', function connection(ws) {
    ws.on('message', function incoming(message) {
        let data = JSON.parse(message);

        if (!!data.request) {
            if (!!data.request.action) {
                console.log(board.players.find(player => player.name === data.request.action.player.name));
                console.log('recieved: %s', message);
                console.log('piles: %s', JSON.stringify(board.piles));
                console.log('valid move: %s', board.tryPlayOnPile(board.piles[data.request.action.claim.pile], board.players.find(player => player.name === data.request.action.player.name)));

                if (!!board) {
                    sendGameState();
                }
            }
        } else if (!!data.push) {
            if (!!data.push.player) {    
                players.push(data.push.player);
                console.log(players);

                if (!board && players.length >= 2) {
                    board = new BoardModel(players);
                    board.onGameOver(() => {
                        sendMessageToAll(JSON.stringify({push: { gameOver: true, winner: board.gameWon(board.players[0]) ? board.players[0] : board.players[1] }}));
                        
                        sendGameState();
            
                        board = null;
                        players = [];
                    });
                    board.onNoPlayablePiles(() => {
                        if (!board.gameOver()) {
                            while (!board.playableMoves()) {
                                board.redrawPiles();
                            }
            
                            sendGameState();
                        }
                    });

                    sendMessageToAll(JSON.stringify({push: {gameStart: true, players: players}}));

                    sendGameState();
                }
            }
        }
        
    });

    if (!!board) {
        sendGameState();
    }
});

function sendGameState() {
    sendMessageToAll(JSON.stringify({push: { board: { pile: board.piles } }}));
}

function sendMessageToAll(message) {
    wss.clients.forEach(function each(client) {
        if (client.readyState === WebSocket.OPEN) {
            client.send(message);
        }
    });
}