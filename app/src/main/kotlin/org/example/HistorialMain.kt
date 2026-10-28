package org.example

fun main(args: Array<String>) {

    val idMoto = if (args.isNotEmpty()) {
        args[0]
    } else {
        "moto-101"
    }

    val session = CassandraConnection.open()

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
