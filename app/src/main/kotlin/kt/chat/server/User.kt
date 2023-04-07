package kt.chat.server

import io.ktor.websocket.DefaultWebSocketSession
import java.util.UUID


class User(val username: String) {
  val id: String = UUID.randomUUID().toString()
}