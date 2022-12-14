package uz.iraimjanov.plugins

import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import org.koin.ktor.ext.inject
import uz.iraimjanov.room.RoomController
import uz.iraimjanov.routes.chatSocket
import uz.iraimjanov.routes.getAllMessage

fun Application.configureRouting() {
    val roomController by inject<RoomController>()
    install(Routing) {
        chatSocket(roomController)
        getAllMessage(roomController)
    }
}
