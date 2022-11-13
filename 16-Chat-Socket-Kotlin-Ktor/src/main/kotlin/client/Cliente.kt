package client

import com.github.ajalt.mordant.rendering.BorderType
import com.github.ajalt.mordant.rendering.TextColors.*
import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import models.Mensaje
import models.Request
import mu.KotlinLogging
import java.util.*
import kotlin.system.exitProcess


private val logger = KotlinLogging.logger {}
private val json = Json
private val t = Terminal()

private const val HOST = "localhost"
private const val PORT = 6969

fun main(args: Array<String>): Unit = runBlocking {

    t.println(bold(blue("✳ DAM Chat Client ✳")))

    val username by lazy { getUsername() }

    // Indicamos el Dispatcher para el Cliente
    val selectorManager = SelectorManager(Dispatchers.IO)

    // Creamos el socket TCP y si todo va bien, nos conectamos al servidor en el puerto 6969
    val socket = getConexion(selectorManager)

    println("✅ $username Conectado a: ${socket.remoteAddress}")

    // Creamos un canal de entrada y salida
    val entrada = socket.openReadChannel()
    val salida = socket.openWriteChannel(autoFlush = true)

    // Obtenemos los mensajes en caché del servidor y los mostramos
    receiveMensajesEnCached(entrada)

    // Lanzamos corrutinas para leer de teclado y mandar mensajes
    // y otra para leer mensajes del servidor
    val recepcionMensajesJob = launch { recepcionMensajes(entrada, username) }

    val envioMensajesJob = launch { envioMensajes(salida, username) }

    // Esperamos a que se complete (escriba salir), haya error en la entrada
    envioMensajesJob.join()
    // Cancelamos la corutina de entrada de mensajes porque ya no la necesitamos
    recepcionMensajesJob.cancelAndJoin()

    // Cerramos todo
    withContext(Dispatchers.IO) {
        salida.close()
        socket.close()
    }

    t.println(bold(blue("👋 Hasta luego $username")))

}

/**
 * Obtiene la conexión con el servidor
 * @param selectorManager SelectorManager
 * @return Socket
 */
suspend fun getConexion(selectorManager: SelectorManager): Socket {
    logger.debug { "Obteniendo conexión con el servidor" }
    try {
        return aSocket(selectorManager).tcp().connect(HOST, PORT)
    } catch (e: Exception) {
        // logger.error(e) { "Error al conectar con el servidor" }
        println("❌" + red("Error al conectar con el servidor: $HOST:$PORT. Servidor no disponible"))
        exitProcess(1)
    }
}

/**
 * Obtiene los mensajes en caché del servidor y los muestra
 * @param entrada Canal de entrada
 */
private suspend fun receiveMensajesEnCached(entrada: ByteReadChannel) {
    logger.debug { "Obteniendo mensajes en caché del servidor" }
    // Espero recibir la lista de mensajes de mensajes
    val response = entrada.readUTF8Line()
    logger.debug("Cliente: Recibido mensajes en cache: $response")
    val mensajes = json.decodeFromString<List<Mensaje>>(response!!)

    // Si la lista no está vacía, la mostramos
    if (mensajes.isNotEmpty()) {
        t.println(bold("📥 Mensajes en caché del servidor ${mensajes.size}:"))
        // Eliminamos el ultimo, porque lo voy a recibir de reactivamente
        t.println(
            table {
                borderType = BorderType.ROUNDED
                header {
                    row("", "Usuario", "Mensaje", "Fecha")
                }
                body {
                    mensajes.dropLast(1).forEach {
                        row("\uD83D\uDCE9", blue(it.user), it.content, gray(it.createdAt.toString()))
                    }
                }
            }
        )
    } else {
        t.println(bold(gray("\uD83D\uDCE5 No hay mensajes en caché del servidor")))
    }
}

/**
 * Obtiene el nombre de usuario por la consola
 * @return Nombre de usuario
 */
private fun getUsername(): String {
    var username = ""
    do {
        username = t.prompt("Introduce tu nombre de usuario").orEmpty().trim()
    } while (username.isBlank())

    // De esta manera no habrá dos usuarios con el mismo nombre
    username += ("-" + UUID.randomUUID().toString().substring(0, 4))
    return username
}

/**
 * Corrutina que se encarga de tomar los mensajes del usuario y enviarlos al servidor
 * @param salida Canal de salida
 * @param username Nombre de usuario
 */
private suspend fun envioMensajes(salida: ByteWriteChannel, username: String) = withContext(Dispatchers.IO) {
    logger.debug { "Lanzando corrutina de envío de mensajes" }
    println("Escribe un mensaje y pulsa enter para enviarlo. Escribe 'salir' para salir")
    while (!salida.isClosedForWrite) {
        // print("$username -> ") (( Otra cosa es poner readln()
        val content =
            t.prompt(
                gray("$username >"),
            ).orEmpty().trim() // es bloqueante, por lo que por eso lo he metido en otro Dispacher al programa principal

        if (content.isBlank()) {
            continue
        }
        if (content == "salir") {
            val request = Request<Mensaje>(
                content = Mensaje(user = username, content = content),
                type = Request.Type.LOGOUT // Para avisar al servidor que el usuario se va
            )
            salida.writeStringUtf8(json.encodeToString(request) + "\n")
            logger.debug("Saliendo...")
            return@withContext // Salimos de la función
        } else {
            val request = Request<Mensaje>(
                content = Mensaje(user = username, content = content),
                type = Request.Type.POST
            )
            salida.writeStringUtf8(json.encodeToString(request) + "\n")
        }
        logger.debug("Mensaje Enviado: $content")

        // Como estamos en consola para no saturar la CPU y bloquearla
        delay(500)
    }

}

/**
 * Corrutina que se encarga de leer los mensajes del servidor y mostrarlos por pantalla (Reactivamente)
 * @param entrada Canal de entrada
 */
private suspend fun recepcionMensajes(entrada: ByteReadChannel, username: String) = withContext(Dispatchers.IO) {
    logger.debug { "Lanzando corrutina de recepción de mensajes" }

    mensajeRecibidos(entrada).filter { username != it.user }.collect {
        t.println("\uD83D\uDCE9 ${blue(it.user)} ${it.content} ${gray(it.createdAt.toString())}")
    }

    /*while (!entrada.isClosedForRead) {
        val response = entrada.readUTF8Line()
        response?.let {
            logger.debug("Cliente: Recibido: $response")
            val mensaje = json.decodeFromString<Mensaje>(response)
            // solo lo si no es nuestro!!!
            if (mensaje.user != username) {
                println("\uD83D\uDCE9 ${mensaje.user}: ${mensaje.content} | ${mensaje.createdAt}")
            }
        }
        delay(1000) // Para darle vidilla al programa :)
    }*/

}

/**
 * Lee los mensajes del servidor y los devuelve como un Flow (Flujo)
 */
private suspend fun mensajeRecibidos(entrada: ByteReadChannel): Flow<Mensaje> = flow {
    logger.debug { "Lanzando corrutina de recepción de mensajes" }
    while (!entrada.isClosedForRead) {
        val response = entrada.readUTF8Line()
        response?.let {
            logger.debug("Cliente: Recibido: $response")
            val mensaje = json.decodeFromString<Mensaje>(response)
            emit(mensaje)
        }
        delay(500) // Para darle vidilla al programa :)
    }
}.flowOn(Dispatchers.IO)

