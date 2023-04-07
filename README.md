# Chat Server (Kotlin)

Follows the DGG chat protocol, WIP

## Usage

```
./gradlew run # Build and run


websocat ws://localhost:8080/chat
```

Some examples of websocket frames to send:

```
AUTH { "nick": "foobar" }

MSG { "nick": "foobar", "timestamp": 12345, "data": "hello there!" }

QUIT { "nick": "foobar", "timestamp": 12345 }
```

JOIN, MSG, QUIT, OK, and BAD_REQUEST frames will all be sent back to the client(s).

Try joining with multiple clients and authenticating with different usernames!
