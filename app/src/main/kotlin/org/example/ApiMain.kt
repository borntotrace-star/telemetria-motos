package org.example

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

@Serializable
data class MotoResponse(
    val id: String,
    val matricula: String,
    val modelo: String,
    val bateria: Int,
    val velocidad: Double,
    val latitud: Double,
    val longitud: Double,
    val estado: String,
    val timestamp: String
)

fun main() {

    val session = CassandraConnection.open()

    val repository = MotoRepository(session)

    println("Conexión con Cassandra establecida")
    println("Iniciando API en puerto 8080...")

    val server = embeddedServer(
        Netty,
        host = "0.0.0.0",
        port = 8080
    ) {

        install(ContentNegotiation) {
            json()
        }

        routing {

            get("/health/live") {
                call.respond(mapOf("estado" to "activo"))
            }

            get("/health/ready") {
                if (repository.comprobarConexion()) {
                    call.respond(mapOf("estado" to "listo"))
                } else {
                    call.respond(
                        HttpStatusCode.ServiceUnavailable,
                        mapOf("estado" to "cassandra_no_disponible")
                    )
                }
            }

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
                        estado = moto.estado,
                        timestamp = moto.timestamp.toString()
                    )
                }

                call.respond(respuesta)
            }

            get("/motos/{id}") {

                val id = call.parameters["id"]

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest,
                        mapOf("error" to "ID de moto obligatorio")
                    )
                    return@get
                }

                val moto = repository.obtenerMoto(id)

                if (moto == null) {
                    call.respond(HttpStatusCode.NotFound,
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
                        estado = moto.estado,
                        timestamp = moto.timestamp.toString()
                    )
                )
            }

            get("/motos/{id}/historial") {

                val id = call.parameters["id"]

                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest,
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
                        estado = moto.estado,
                        timestamp = moto.timestamp.toString()
                    )
                }

                call.respond(respuesta)
            }
        }

    }

    try {
        server.start(wait = true)
    } finally {
        server.stop()
        session.close()
    }
}
