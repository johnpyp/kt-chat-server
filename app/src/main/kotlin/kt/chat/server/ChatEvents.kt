package kt.chat.server

import io.github.oshai.KotlinLogging
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

enum class ChatEventType {
  Auth,
  Message,
  Join,
  Quit,
  BadRequest,
  Ok
}

sealed class ChatEvent()

@Serializable
class ChatMessageEvent(val nick: String, val timestamp: Long, val data: String) : ChatEvent()

@Serializable class ChatAuthEvent(val username: String) : ChatEvent()

@Serializable class ChatBadRequestEvent(val text: String) : ChatEvent()

@Serializable class ChatOkEvent(val text: String) : ChatEvent()

@Serializable class ChatJoinEvent(val nick: String, val timestamp: Long) : ChatEvent()

@Serializable class ChatQuitEvent(val nick: String, val timestamp: Long) : ChatEvent()

object ChatEventHandler {
  private val logger = KotlinLogging.logger {}

  fun parseChatFrame(frame: String): ChatEvent? {
    val splitFrame = frame.split(" ", limit = 2)
    if (splitFrame.size == 2) {
      val type = splitFrame[0]
      val jsonBlob = splitFrame[1]
      return frameToEvent(type, jsonBlob)
    }
    return null
  }

  fun serializeChatEvent(evt: ChatEvent): String {
    val p =
        when (evt) {
          is ChatAuthEvent -> Pair("AUTH", Json.encodeToString(evt))
          is ChatMessageEvent -> Pair("MSG", Json.encodeToString(evt))
          is ChatBadRequestEvent -> Pair("BAD_REQUEST", Json.encodeToString(evt))
          is ChatOkEvent -> Pair("OK", Json.encodeToString(evt))
          is ChatJoinEvent -> Pair("JOIN", Json.encodeToString(evt))
          is ChatQuitEvent -> Pair("QUIT", Json.encodeToString(evt))
        }

    return p.toList().joinToString(" ")
  }

  private fun frameToEvent(type: String, jsonBlob: String): ChatEvent? {
    return try {
      when (parseChatEventType(type)) {
        ChatEventType.Auth -> return Json.decodeFromString<ChatAuthEvent>(jsonBlob)
        ChatEventType.Message -> Json.decodeFromString<ChatMessageEvent>(jsonBlob)
        ChatEventType.Join -> Json.decodeFromString<ChatJoinEvent>(jsonBlob)
        ChatEventType.Quit -> Json.decodeFromString<ChatQuitEvent>(jsonBlob)
        ChatEventType.BadRequest -> return Json.decodeFromString<ChatBadRequestEvent>(jsonBlob)
        else -> {
          logger.warn("Frame didn't have a valid type: $type")
          null
        }
      }
    } catch (e: Exception) {
      logger.warn("Error parsing chat frame: ${e.localizedMessage}")
      return null
    }
  }

  private fun parseChatEventType(type: String): ChatEventType? {
    return when (type) {
      "AUTH" -> ChatEventType.Auth
      "MSG" -> ChatEventType.Message
      "JOIN" -> ChatEventType.Message
      "QUIT" -> ChatEventType.Quit
      "BAD_REQUEST" -> ChatEventType.BadRequest
      "OK" -> ChatEventType.Ok
      else -> null
    }
  }
}
