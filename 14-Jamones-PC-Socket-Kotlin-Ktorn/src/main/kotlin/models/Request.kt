package models

import kotlinx.serialization.Serializable
import serializers.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class Request<T>(
    val content: T?, // Contenido de la petición
    val type: Type, // Tipo de petición
    @Serializable(with = LocalDateTimeSerializer::class)
    val createAt: LocalDateTime = LocalDateTime.now()
) {
    enum class Type {
        GET, POST, PUT, DELETE, ERROR
    }
}
