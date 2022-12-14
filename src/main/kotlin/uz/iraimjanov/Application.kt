package uz.iraimjanov

import io.ktor.server.application.*
import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import uz.iraimjanov.di.mainModule
import uz.iraimjanov.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    install(Koin) {
        modules(mainModule)
    }
    configureSerialization()
    configureSockets()
    configureMonitoring()
    configureSecurity()
    configureRouting()
}
