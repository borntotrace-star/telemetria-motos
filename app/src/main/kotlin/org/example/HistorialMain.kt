package org.example

import com.datastax.oss.driver.api.core.CqlSession
import java.net.InetSocketAddress

fun main(args: Array<String>) {

    val idMoto = if (args.isNotEmpty()) {
        args[0]
    } else {
        "moto-101"
    }

    val cassandraHost = System.getenv("CASSANDRA_HOST") ?: "127.0.0.1"

    val session = CqlSession.builder()
        .addContactPoint(InetSocketAddress(cassandraHost, 9042))
        .withLocalDatacenter("datacenter1")
        .withKeyspace("telemetria")
        .build()

    println("Conexión con Cassandra establecida")
    println("Cassandra: $cassandraHost:9042")
    println("Consultando historial de: $idMoto")
    println("--------------------------------")

    val repository = MotoRepository(session)

    val historial = repository.obtenerHistorial(idMoto)

    if (historial.isEmpty()) {
        println("No se encontraron registros para $idMoto")
    } else {
        for (moto in historial) {
            println(
                "${moto.id} | " +
                "${moto.matricula} | " +
                "${moto.bateria}% | " +
                "${"%.1f".format(moto.velocidad)} km/h | " +
                "${"%.5f".format(moto.latitud)}, " +
                "${"%.5f".format(moto.longitud)} | " +
                "${moto.estado}"
            )
        }

        println("--------------------------------")
        println("Registros encontrados: ${historial.size}")
    }

    session.close()
}