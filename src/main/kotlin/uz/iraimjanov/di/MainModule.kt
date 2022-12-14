package uz.iraimjanov.di

import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import uz.iraimjanov.data.MessageDataSource
import uz.iraimjanov.data.MessageDataSourceImp
import uz.iraimjanov.room.RoomController

val mainModule = module {
    single {
        KMongo.createClient().coroutine.getDatabase("message_db")
    }
    single<MessageDataSource> {
        MessageDataSourceImp(get())
    }
    single {
        RoomController(get())
    }
}