package kt.chat.server.connection

import io.ktor.websocket.CloseReason
import io.ktor.websocket.DefaultWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import java.util.UUID
import kt.chat.server.ChatAuthEvent
import kt.chat.server.ChatBadRequestEvent
import kt.chat.server.ChatEvent
import kt.chat.server.ChatEventHandler
import kt.chat.server.ChatJoinEvent
import kt.chat.server.ChatMessageEvent
import kt.chat.server.ChatOkEvent
import kt.chat.server.ChatQuitEvent
import kt.chat.server.User

class Connection(
    private val connectionManager: ConnectionManager,
    private val session: DefaultWebSocketSession
) {
  val sessionId: UUID = UUID.randomUUID()
  var user: User? = null

  suspend fun processEvent(evt: ChatEvent) {
    when (evt) {
      is ChatAuthEvent -> return handleAuth(evt)
      is ChatMessageEvent -> return handleChatMessage(evt)
      is ChatQuitEvent -> return handleQuit(evt)
      else -> {}
    }
  }

  private suspend fun handleAuth(evt: ChatAuthEvent) {
    val newUser = User(evt.username)
    user = newUser

    connectionManager.emitToAll(ChatJoinEvent(newUser.username, System.currentTimeMillis()))
    sendEvent(ChatOkEvent("Successfully authenticated"))
  }

  private suspend fun handleChatMessage(evt: ChatMessageEvent) {
    val u = user ?: return sendEvent(ChatBadRequestEvent("Not logged in"))

    if (evt.nick != u.username)
        return sendEvent(ChatBadRequestEvent("Incorrect username"))

    connectionManager.emitToAll(evt)
    sendEvent(ChatOkEvent("Successfully sent message"))
  }

  private suspend fun handleQuit(evt: ChatQuitEvent) {
    val u = user ?: return sendEvent(ChatBadRequestEvent("Not logged in"))
    if (evt.nick != u.username)
      return sendEvent(ChatBadRequestEvent("Incorrect username"))

    connectionManager.emitToAll(ChatQuitEvent(evt.nick, System.currentTimeMillis()))
    this.closeConnection()
  }

  private suspend fun closeConnection() {
    this.session.close(CloseReason(CloseReason.Codes.NORMAL, "Closed"))
  }

  suspend fun sendEvent(chatEvent: ChatEvent) {
    val payload = ChatEventHandler.serializeChatEvent(chatEvent)
    session.send(Frame.Text("--> $payload"))
  }
}
