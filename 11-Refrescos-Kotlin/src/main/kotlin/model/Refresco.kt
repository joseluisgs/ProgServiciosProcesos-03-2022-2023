package es.joseluisgs.dam.clienteservidor.model

import java.io.Serializable
import kotlin.random.Random

data class Refresco(
    var nombre: String = "Agua",
    val precio: Int = (1..3).random()
) : Serializable {
    init {
        nombre = when ((Math.random() * 7).toInt()) {
            0 -> "Coca Cola"
            1 -> "Sprite"
            2 -> "Fanta"
            3 -> "Tónica"
            4 -> "Nestea"
            5 -> "Cerveza"
            6 -> "Zumo"
            else -> "Agua"
        }
    }
}
