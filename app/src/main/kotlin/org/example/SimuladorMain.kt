package org.example

import com.datastax.oss.driver.api.core.CqlSession
import java.net.InetSocketAddress

fun main() {

    val cassandraHost = System.getenv("CASSANDRA_HOST") ?: "127.0.0.1"

    val session = CqlSession.builder()
        .addContactPoint(InetSocketAddress(cassandraHost, 9042))
        .withLocalDatacenter("datacenter1")
        .withKeyspace("telemetria")
        .build()

    println("Conexión con Cassandra establecida")
    println("Cassandra: $cassandraHost:9042")

    val repository = MotoRepository(session)
    val simulador = SimuladorMotos(repository)

    simulador.iniciar()

    session.close()
}