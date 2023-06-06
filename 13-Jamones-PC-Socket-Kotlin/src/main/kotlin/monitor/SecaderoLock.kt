package monitor

import models.Jamon
import mu.KotlinLogging
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private val logger = KotlinLogging.logger {}

class SecaderoLock(private val maxJamones: Int = 10) : MonitorProducerConsumer<Jamon> {

    // Buffer de memoria limitada a maxJamones
    private val secadero: MutableList<Jamon> = mutableListOf()

    // Cerrojo con condiciones
    private val lock: ReentrantLock = ReentrantLock()
    private val secaderoConsumeCondition: Condition = lock.newCondition() // Hay jamones para consumir
    private val secaderoProduceCondition: Condition = lock.newCondition() // Necesita más jamones para producir

    override fun get(): Jamon {
        lock.withLock {
            while (secadero.size == 0) {
                try {
                    // Si no hay hay sacar, esperamos a que se pueda vacia, pues lo esta,
                    // y se espera a que se llene
                    secaderoConsumeCondition.await()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            val jamon = secadero.removeFirst() // Saco el primero
            logger.debug { "El secadero tiene ${secadero.size} jamones" }
            // Activamos avisando que hay espacio para que produzcan
            secaderoProduceCondition.signalAll()
            logger.debug { "Consumiendo $jamon" }
            return jamon // Devolvemos el jamon
        }
    }

    override fun put(item: Jamon) {
        lock.withLock {
            while (secadero.size == maxJamones) {    // Condición de memoria limitada
                try {
                    // Si no hay que producir esperamos, pues estamos llenos
                    secaderoProduceCondition.await()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            logger.debug { "Produciendo $item" }
            secadero.add(item) //Metemos al final
            logger.debug { "El secadero tiene ${secadero.size} jamones" }
            // Ya hay cantidad a consumir, activamos
            secaderoConsumeCondition.signalAll()
        }
    }
}
