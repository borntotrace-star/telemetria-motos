package org.example

import kotlin.random.Random

class SimuladorMotos(
    private val repository: MotoRepository
) {

    private val motosIniciales = listOf(
        Moto(
            id = "moto-101",
            matricula = "SIM-101",
            modelo = "Moto Simulada A",
            bateria = 100,
            velocidad = 0.0,
            latitud = 41.3874,
            longitud = 2.1686,
            estado = "EN_MARCHA"
        ),
        Moto(
            id = "moto-102",
            matricula = "SIM-102",
            modelo = "Moto Simulada B",
            bateria = 100,
            velocidad = 0.0,
            latitud = 41.3880,
            longitud = 2.1700,
            estado = "EN_MARCHA"
        ),
        Moto(
            id = "moto-103",
            matricula = "SIM-103",
            modelo = "Moto Simulada C",
            bateria = 100,
            velocidad = 0.0,
            latitud = 41.3890,
            longitud = 2.1710,
            estado = "EN_MARCHA"
        )
    )

    private val motos = motosIniciales.map { motoInicial ->
        repository.obtenerMoto(motoInicial.id) ?: motoInicial
    }.toMutableList()

    fun iniciar() {

        println("Simulador iniciado")
        println("Estados recuperados desde Cassandra")

        while (true) {

            for (i in motos.indices) {

                val motoActual = motos[i]

                val nuevaMoto = when {

                    // MOTO SIN BATERÍA
                    motoActual.bateria <= 0 -> {

                        motoActual.copy(
                            bateria = 5,
                            velocidad = 0.0,
                            estado = "RECARGANDO"
                        )
                    }

                    // MOTO RECARGANDO
                    motoActual.estado == "RECARGANDO" -> {

                        val nuevaBateria =
                            (motoActual.bateria + 5).coerceAtMost(100)

                        if (nuevaBateria >= 100) {

                            motoActual.copy(
                                bateria = 100,
                                velocidad = Random.nextDouble(20.0, 60.0),
                                latitud = motoActual.latitud +
                                    Random.nextDouble(-0.0005, 0.0005),
                                longitud = motoActual.longitud +
                                    Random.nextDouble(-0.0005, 0.0005),
                                estado = "EN_MARCHA"
                            )

                        } else {

                            motoActual.copy(
                                bateria = nuevaBateria,
                                velocidad = 0.0,
                                estado = "RECARGANDO"
                            )
                        }
                    }

                    // MOTO EN MARCHA
                    else -> {

                        val consumo = Random.nextInt(1, 4)

                        val nuevaBateria =
                            (motoActual.bateria - consumo).coerceAtLeast(0)

                        val nuevoEstado = when {
                            nuevaBateria == 0 -> "DETENIDA"
                            nuevaBateria <= 10 -> "BATERIA_BAJA"
                            else -> "EN_MARCHA"
                        }

                        motoActual.copy(
                            bateria = nuevaBateria,

                            velocidad = if (nuevoEstado == "DETENIDA") {
                                0.0
                            } else {
                                Random.nextDouble(20.0, 60.0)
                            },

                            latitud = if (nuevoEstado == "DETENIDA") {
                                motoActual.latitud
                            } else {
                                motoActual.latitud +
                                    Random.nextDouble(-0.0005, 0.0005)
                            },

                            longitud = if (nuevoEstado == "DETENIDA") {
                                motoActual.longitud
                            } else {
                                motoActual.longitud +
                                    Random.nextDouble(-0.0005, 0.0005)
                            },

                            estado = nuevoEstado
                        )
                    }
                }

                motos[i] = nuevaMoto

                // Guardar estado actual
                repository.guardar(nuevaMoto)

                // Guardar histórico
                repository.guardarTelemetria(nuevaMoto)

                println(
                    "${nuevaMoto.id} | " +
                    "Velocidad: ${"%.1f".format(nuevaMoto.velocidad)} km/h | " +
                    "Batería: ${nuevaMoto.bateria}% | " +
                    "Estado: ${nuevaMoto.estado} | " +
                    "Ubicación: ${"%.5f".format(nuevaMoto.latitud)}, " +
                    "${"%.5f".format(nuevaMoto.longitud)}"
                )
            }

            println("----------")

            Thread.sleep(2000)
        }
    }
}