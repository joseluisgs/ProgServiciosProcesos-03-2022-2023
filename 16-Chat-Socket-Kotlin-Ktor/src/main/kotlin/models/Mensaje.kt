package models

import kotlinx.serialization.Serializable
import serializers.LocalDateTimeSerializer
import serializers.UUIDSerializer
import java.time.LocalDateTime
import java.util.*

@Serializable
data class Mensaje(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    val user: String,
    val content: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
