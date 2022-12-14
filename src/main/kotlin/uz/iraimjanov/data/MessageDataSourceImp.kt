package uz.iraimjanov.data

import org.litote.kmongo.coroutine.CoroutineDatabase
import uz.iraimjanov.data.model.Message

class MessageDataSourceImp(
    private val db: CoroutineDatabase
) : MessageDataSource {

    private val messages = db.getCollection<Message>()

    override suspend fun getAllMessages(): List<Message> {
        return messages.find().descendingSort(Message::timestamp).toList()
    }

    override suspend fun insertMessage(message: Message) {
        messages.insertOne(message)
    }

    override suspend fun updateMessage(message: Message) {
        messages.updateOneById(message.id, message)
    }

    override suspend fun deleteMessage(id: String) {
        messages.deleteOneById(id)
    }

}