package models

import kotlinx.serialization.Serializable
import serializers.LocalDateTimeSerializer
import serializers.UUIDSerializer
import java.time.LocalDateTime
import java.util.*

@Serializable
data class Lote(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID = UUID.randomUUID(),
    val idMensajero: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val fechaProduccion: LocalDateTime = LocalDateTime.now(),
    val jamones: List<Jamon> = listOf(),
)