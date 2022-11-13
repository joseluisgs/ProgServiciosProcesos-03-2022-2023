package es.joseluisgs.dam.clienteservidor.model

import java.io.Serializable


class Maquina : Serializable {
    private val listaRefrescos: ArrayList<Refresco> = ArrayList<Refresco>()

    // Lo mejor es hacerlo con candados Lock de hilos!!
    @Synchronized
    fun add(r: Refresco) {
        listaRefrescos.add(r)
    }

    @Synchronized
    operator fun get(i: Int): Refresco {
        return listaRefrescos[i]
    }

    @Synchronized
    fun remove(i: Int): Refresco {
        return listaRefrescos.removeAt(i)
    }

    @Synchronized
    fun removeAll(): Maquina {
        val lista = Maquina()
        while (listaRefrescos.size > 0) {
            lista.add(listaRefrescos.removeAt(0))
        }
        return lista
    }

    @Synchronized
    fun size(): Int {
        return listaRefrescos.size
    }

}
