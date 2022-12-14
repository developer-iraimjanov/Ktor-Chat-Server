package uz.iraimjanov.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SocketModel(
    val mode: String,
    val messageText: String? = null,
    val message: Message? = null,
    val id: String? = null,
)