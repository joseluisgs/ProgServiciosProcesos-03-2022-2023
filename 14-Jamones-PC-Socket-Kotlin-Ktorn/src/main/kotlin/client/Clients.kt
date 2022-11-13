import client.Granja
import client.Mensajero
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

private const val MAX_JAMONES = 30
private const val INTER_GRANJA = 1000
private const val INTER_MENSA = 3000
private const val TAM_LOTE = 3

fun main(args: Array<String>): Unit = runBlocking {
    val granjas = listOf(
        Granja("Granja-1", MAX_JAMONES, INTER_GRANJA),
        Granja("Granja-2", MAX_JAMONES, INTER_GRANJA),
    )

    val mensajero = Mensajero("Mensajero-1", TAM_LOTE, 2 * MAX_JAMONES / TAM_LOTE, INTER_MENSA)

    // Lanazamos las granjas
    granjas.forEach {
        launch { it.run() }
    }

    // Lanzamos el mensajero
    val mensajeroJob = launch { mensajero.run() }

    // Esperamos que acabe el mensajero
    mensajeroJob.join()
}

