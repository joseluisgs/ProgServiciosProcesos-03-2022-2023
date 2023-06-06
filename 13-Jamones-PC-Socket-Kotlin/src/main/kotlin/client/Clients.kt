package client

import java.util.concurrent.Executors

private const val MAX_JAMONES = 30
private const val INTER_GRANJA = 1000
private const val INTER_MENSA = 3000
private const val TAM_LOTE = 3

fun main() {
    val pool = Executors.newFixedThreadPool(10)
    val granjas = listOf(
        Granja("Granja-1", MAX_JAMONES, INTER_GRANJA),
        Granja("Granja-2", MAX_JAMONES, INTER_GRANJA),
    )

    val mensajero = Mensajero("Mensajero-1", TAM_LOTE, 2 * MAX_JAMONES / TAM_LOTE, INTER_MENSA)

    granjas.forEach {
        pool.execute(it)
    }

    pool.execute(mensajero)

    pool.shutdown()

}