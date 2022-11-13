package server

import io.ktor.server.application.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

// Indicamos que estamos con Ktor y el engine que vamos a usar como servidor
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)


@Suppress("unused")
// Función que se ejecuta al iniciar el servidor, son funciones de extensión
fun Application.module() {
    logger.debug { "Configurando plugins" }
    // Configuramos todas las opciones de nuestro servidor
    configureRouting()
    configureSockets()
}
