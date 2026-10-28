package org.example

fun main() {
    val session = CassandraConnection.open()

    val repository = MotoRepository(session)
    val simulador = SimuladorMotos(repository)

    simulador.iniciar()

    session.close()
}
