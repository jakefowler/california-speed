const WebSocket = require('ws');
const BoardModel = require('./models/Board');

const wss = new WebSocket.Server({ port: 8080 });

var board = null

wss.on('connection', function connection(ws) {
    if (!board) {
        board = new BoardModel();
        board.onGameOver(() => {
            sendMessageToAll(JSON.stringify({ gameOver: true }));
            
            sendGameState();

            board = null
        });
        board.onNoPlayablePiles(() => {
            if (!board.gameOver()) {
                while (!board.playableMoves()) {
                    board.redrawPiles();
                }

                sendGameState();
            }
        });
    }

    ws.on('message', function incoming(message) {
        let data = JSON.parse(message);

        if (!!data.claim) {
            console.log('recieved: %s', message);
            console.log('piles: %s', JSON.stringify(board.piles));
            console.log('valid move: %s', board.tryPlayOnPile(board.piles[data.claim.pile]));

            if (!!board) {
                sendGameState();
            }
        }
        
    });

    sendGameState();
});

function sendGameState() {
    sendMessageToAll(JSON.stringify({ board: { piles: board.piles } }));
}

function sendMessageToAll(message) {
    wss.clients.forEach(function each(client) {
        if (client.readyState === WebSocket.OPEN) {
            client.send(message);
        }
    });
}