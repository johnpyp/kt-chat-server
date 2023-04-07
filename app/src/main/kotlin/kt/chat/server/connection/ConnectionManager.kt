package kt.chat.server.connection

import kt.chat.server.ChatEvent
import java.util.Collections
import java.util.UUID

class ConnectionManager {
  private val connections = Collections.synchronizedMap<UUID, Connection>(HashMap())

  fun addConnection(conn: Connection) {
    connections[conn.sessionId] = conn
  }

  fun disconnect(conn: Connection) {
    connections.remove(conn.sessionId)
  }

  suspend fun emitToAll(chatEvent: ChatEvent) {
    connections.forEach { (sessionId, conn) ->
      conn.sendEvent(chatEvent)
    }
  }

  suspend fun emitToAllUsers(chatEvent: ChatEvent) {
    connections.forEach { (sessionId, conn) ->
      if (conn.user != null) {
        conn.sendEvent(chatEvent)
      }
    }
  }
}