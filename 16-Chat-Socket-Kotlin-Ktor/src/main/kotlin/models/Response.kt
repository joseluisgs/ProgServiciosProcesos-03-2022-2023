package models

import kotlinx.serialization.Serializable
import serializers.LocalDateTimeSerializer
import java.time.LocalDateTime

@Serializable
data class Response<T>(
    val content: T?, // Contenido de la respuesta
    val type: Type, // Tipo de respuesta
    @Serializable(with = LocalDateTimeSerializer::class)
    val createAt: LocalDateTime = LocalDateTime.now()
) {
    enum class Type {
        OK, ERROR
    }
}