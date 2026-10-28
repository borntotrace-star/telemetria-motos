package org.example

import java.time.Instant

data class Moto(
    val id: String,
    val matricula: String,
    val modelo: String,
    val bateria: Int,
    val velocidad: Double,
    val latitud: Double,
    val longitud: Double,
    val estado: String,
    val timestamp: Instant = Instant.now()
)

fun main() {
    val moto = Moto(
        id = "moto-001",
        matricula = "TEST-001",
        modelo = "Moto Demo",
        bateria = 87,
        velocidad = 42.5,
        latitud = 41.3874,
        longitud = 2.1686,
        estado = "EN_MARCHA"
    )

    println("ID: ${moto.id}")
    println("Matrícula: ${moto.matricula}")
    println("Modelo: ${moto.modelo}")
    println("Batería: ${moto.bateria}%")
    println("Velocidad: ${moto.velocidad} km/h")
    println("Ubicación: ${moto.latitud}, ${moto.longitud}")
    println("Estado: ${moto.estado}")
}
