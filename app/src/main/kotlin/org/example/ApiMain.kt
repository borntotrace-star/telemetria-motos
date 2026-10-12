package org.example

import com.datastax.oss.driver.api.core.CqlSession
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.net.InetSocketAddress

@Serializable
data class MotoResponse(
    val id: String,
    val matricula: String,
    val modelo: String,
    val bateria: Int,
    val velocidad: Double,
    val latitud: Double,
    val longitud: Double,
    val estado: String
)

fun main() {

    val cassandraHost =
        System.getenv("CASSANDRA_HOST") ?: "127.0.0.1"

    val session = CqlSession.builder()
        .addContactPoint(
            InetSocketAddress(cassandraHost, 9042)
        )
        .withLocalDatacenter("datacenter1")
        .withKeyspace("telemetria")
        .build()

    val repository = MotoRepository(session)

    println("Conexión con Cassandra establecida")
    println("Cassandra: $cassandraHost:9042")
    println("Iniciando API en puerto 8080...")

    embeddedServer(
        Netty,
        host = "0.0.0.0",
        port = 8080
    ) {

        install(ContentNegotiation) {
            json()
        }

        routing {

            get("/") {
                call.respond(
                    mapOf(
                        "mensaje" to "API de telemetría de motos funcionando"
                    )
                )
            }

            get("/motos") {

                val motos = repository.obtenerTodas()

                val respuesta = motos.map { moto ->
                    MotoResponse(
                        id = moto.id,
                        matricula = moto.matricula,
                        modelo = moto.modelo,
                        bateria = moto.bateria,
                        velocidad = moto.velocidad,
                        latitud = moto.latitud,
                        longitud = moto.longitud,
                        estado = moto.estado
                    )
                }

                call.respond(respuesta)
            }

            get("/motos/{id}") {

                val id = call.parameters["id"]

                if (id == null) {
                    call.respond(
                        mapOf("error" to "ID de moto obligatorio")
                    )
                    return@get
                }

                val moto = repository.obtenerMoto(id)

                if (moto == null) {
                    call.respond(
                        mapOf("error" to "Moto no encontrada")
                    )
                    return@get
                }

                call.respond(
                    MotoResponse(
                        id = moto.id,
                        matricula = moto.matricula,
                        modelo = moto.modelo,
                        bateria = moto.bateria,
                        velocidad = moto.velocidad,
                        latitud = moto.latitud,
                        longitud = moto.longitud,
                        estado = moto.estado
                    )
                )
            }

            get("/motos/{id}/historial") {

                val id = call.parameters["id"]

                if (id == null) {
                    call.respond(
                        mapOf("error" to "ID de moto obligatorio")
                    )
                    return@get
                }

                val historial = repository.obtenerHistorial(id)

                val respuesta = historial.map { moto ->
                    MotoResponse(
                        id = moto.id,
                        matricula = moto.matricula,
                        modelo = moto.modelo,
                        bateria = moto.bateria,
                        velocidad = moto.velocidad,
                        latitud = moto.latitud,
                        longitud = moto.longitud,
                        estado = moto.estado
                    )
                }

                call.respond(respuesta)
            }
        }

    }.start(wait = true)
}