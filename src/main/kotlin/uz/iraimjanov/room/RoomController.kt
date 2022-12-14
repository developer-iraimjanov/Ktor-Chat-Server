package uz.iraimjanov.room

import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import uz.iraimjanov.data.MessageDataSource
import uz.iraimjanov.data.model.Message
import uz.iraimjanov.data.model.SocketModel
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {
    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(
        username: String,
        sessionId: String,
        socket: WebSocketSession,
    ) {
        if (members.containsKey(username)) {
            throw MemberAlreadyExistsException()
        }
        members[username] = Member(username = username, sessionId = sessionId, socket = socket)
    }

    suspend fun sendMessage(senderUsername: String, message: String) {
        val messageEntity = Message(
            text = message,
            username = senderUsername,
            timestamp = System.currentTimeMillis()
        )

        messageDataSource.insertMessage(messageEntity)

        members.values.forEach { member ->
            val socketModel = SocketModel("add", message = messageEntity)
            val parsedMessage = Json.encodeToString(socketModel)
            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun updateMessage(message: Message) {
        messageDataSource.updateMessage(message)

        members.values.forEach { member ->
            val socketModel = SocketModel("edit", message = message)
            val parsedMessage = Json.encodeToString(socketModel)
            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun deleteMessage(id: String) {
        messageDataSource.deleteMessage(id)

        members.values.forEach { member ->
            val socketModel = SocketModel("delete", id = id)
            val parsedMessage = Json.encodeToString(socketModel)
            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessages(): List<Message> {
        return messageDataSource.getAllMessages()
    }

    suspend fun tryDisconnect(username: String) {
        members[username]?.socket?.close()
        if (members.containsKey(username)) {
            members.remove(username)
        }
    }
}