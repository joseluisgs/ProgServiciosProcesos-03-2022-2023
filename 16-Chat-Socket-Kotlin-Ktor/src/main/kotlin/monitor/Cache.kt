package monitor

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.Serializable
import models.Mensaje
import mu.KotlinLogging
import serializers.LocalDateTimeSerializer
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

private val logger = KotlinLogging.logger {}
private const val MAX_MENSAJES = 10

/**
 * Al usar flujos todo es thread safing, por lo que no necesito Mutex, como si lo hiciese con arrays
 * para controlar el acceso concurrente
 * Sahred me permite compartir el flujo entre varios corutinas y almacenar los mensajes que quiera
 * Con state tengo un estado global que emito... en este caso el último mensaje
 *
 * De esta manera me ahoro un array y un mutex para hacer el historial
 * Y me ahorro un observador y una lista de observadores para el estado del último mensaje :)
 */


@Serializable
object Cache {
    @Serializable(with = LocalDateTimeSerializer::class)
    private var _lastModified: LocalDateTime = LocalDateTime.now()
    val lastModified
        get() = _lastModified.toString()

    // La cache es un flujo de mensajes, que se actualiza cada vez que se añade un nuevo mensaje
    // Se elimina los antiguos
    private val _data: MutableSharedFlow<Mensaje> = MutableSharedFlow(
        replay = MAX_MENSAJES, // Número de mensajes que se almacenan
        onBufferOverflow = BufferOverflow.DROP_OLDEST // Si se llena, elimina los más antiguos
    )
    val data: SharedFlow<Mensaje>
        get() = _data.asSharedFlow()


    // Podría usar un patrón observer para que se notifique a los clientes que se suscriban...
    // Pero para eso se inventó el stateFlow
    /*private val _mensaje = MutableStateFlow<Mensaje?>(null)
    val mensaje: StateFlow<Mensaje?>
        get() = _mensaje.asStateFlow()*/

    // voy a hacerlo con un sharedFlow para que se notifique a todos los clientes tambien y me ahorro un while
    // Miralo y elijes tu
    private val _mensaje = MutableSharedFlow<Mensaje>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val mensaje: SharedFlow<Mensaje>
        get() = _mensaje.asSharedFlow()

    var _recibidos = AtomicInteger(0)
    val size
        get() = if (_recibidos.get() > MAX_MENSAJES) MAX_MENSAJES else _recibidos.get()


    suspend fun add(mensaje: Mensaje) {
        logger.debug { "Añadiendo mensaje ${mensaje.id} a cache" }
        _data.emit(mensaje)
        _recibidos.incrementAndGet()

        _lastModified = LocalDateTime.now()
        // Notificamos a los observadores
        // _mensaje.value = mensaje // tambien podría ser _mensaje.emit(mensaje)
        _mensaje.emit(mensaje)
        // _mensaje.value = data.last()
        logger.debug { "Cache actualizada. Tiene: $size mensajes" }
    }
}
