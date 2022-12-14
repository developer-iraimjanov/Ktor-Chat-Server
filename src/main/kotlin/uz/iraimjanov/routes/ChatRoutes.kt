package uz.iraimjanov.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import uz.iraimjanov.data.model.SocketModel
import uz.iraimjanov.room.MemberAlreadyExistsException
import uz.iraimjanov.room.RoomController
import uz.iraimjanov.session.ChatSession

fun Route.chatSocket(roomController: RoomController) {
    webSocket("/chat-socket") {
        val session = call.sessions.get<ChatSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "no session."))
            return@webSocket
        }
        try {
            roomController.onJoin(
                username = session.username,
                sessionId = session.sessionId,
                socket = this
            )
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    val socketModel = Json.decodeFromString<SocketModel>(frame.readText())

                    when (socketModel.mode) {
                        "add" -> {
                            roomController.sendMessage(
                                senderUsername = session.username,
                                message = socketModel.messageText!!
                            )
                        }

                        "edit" -> {
                            roomController.updateMessage(socketModel.message!!)
                        }

                        "delete" -> {
                            roomController.deleteMessage(socketModel.id!!)
                        }
                    }
                }
            }
        } catch (e: MemberAlreadyExistsException) {
            call.respond(HttpStatusCode.Conflict)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            roomController.tryDisconnect(session.username)
        }

    }
}

fun Route.getAllMessage(roomController: RoomController) {
    get("/message") {
        call.respond(HttpStatusCode.OK, roomController.getAllMessages())
    }
}