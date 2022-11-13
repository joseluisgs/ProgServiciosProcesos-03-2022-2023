package monitor

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import models.Jamon
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

private const val MAX_JAMONES = 10

object Secadero {
    // Mi canal es el secadero de memoria (buffer) limitada
    private val channel = Channel<Jamon>(MAX_JAMONES)

    // Aquí no es necesario, pero lo ponemos para que se entienda mejor
    val puertaEntrada
        get() = channel as SendChannel<Jamon>
    val puertaSalida
        get() = channel as ReceiveChannel<Jamon>
}