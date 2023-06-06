package models

import kotlinx.serialization.Serializable
import serializers.LocalDateTimeSerializer
import serializers.UUIDSerializer
import java.time.LocalDateTime
import java.util.*

@Serializable
data class Jamon(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    val idGranja: String,
    val peso: Int = (6..9).random(),
    @Serializable(with = LocalDateTimeSerializer::class)
    val fechaProduccion: LocalDateTime = LocalDateTime.now(),
)
