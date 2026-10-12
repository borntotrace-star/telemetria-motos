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

    val moto = Moto(
        id = "moto-003",
        matricula = "TEST-003",
        modelo = "Moto Kotlin 2",
        bateria = 91,
        velocidad = 35.0,
        latitud = 41.3890,
        longitud = 2.1710,
        estado = "EN_MARCHA"
    )

    repository.guardar(moto)

    println("Moto guardada: ${moto.id}")

    val motos = repository.obtenerTodas()

    println("Motos almacenadas en Cassandra:")

    for (motoActual in motos) {
        println(
            "${motoActual.id} | " +
            "${motoActual.matricula} | " +
            "${motoActual.modelo} | " +
            "${motoActual.bateria}% | " +
            "${motoActual.estado}"
        )
    }

    session.close()
}