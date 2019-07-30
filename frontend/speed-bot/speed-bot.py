#!/usr/bin/env python

import asyncio
import websockets
import json

async def hello():
    uri = "wss://www.ezekielnewren.com:8080"
    async with websockets.connect(uri) as websocket:
        await websocket.send(json.dumps({'push': {'player': {'name': 'Speed Bot'}}}))
        while True:
            for pile in range(8):
                await websocket.send(json.dumps({
                    'request': { 
                        'action': { 
                            'player': {
                                'name': 'Speed Bot'
                            }, 
                            'claim': { 
                                'pile': pile
                            }
                        }
                    }
                }))
        
        await websocket.recv()

asyncio.get_event_loop().run_until_complete(hello())