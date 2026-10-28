package org.example

import com.datastax.oss.driver.api.core.CqlSession
class MotoRepository(
    private val session: CqlSession
) {

    private val guardarStatement = session.prepare(
        """
        INSERT INTO motos (
            id, matricula, modelo, bateria, velocidad, latitud, longitud, estado, timestamp
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
    )
    private val guardarEnFlotaStatement = session.prepare(
        """
        INSERT INTO motos_por_flota (
            flota, id, matricula, modelo, bateria, velocidad, latitud, longitud, estado, timestamp
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
    )
    private val guardarTelemetriaStatement = session.prepare(
        """
        INSERT INTO telemetria_motos (
            id_moto, timestamp, matricula, modelo, bateria, velocidad, latitud, longitud, estado
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
    )
    private val obtenerMotoStatement = session.prepare("SELECT * FROM motos WHERE id = ?")
    private val obtenerFlotaStatement = session.prepare(
        "SELECT * FROM motos_por_flota WHERE flota = ?"
    )
    private val obtenerHistorialStatement = session.prepare(
        "SELECT * FROM telemetria_motos WHERE id_moto = ?"
    )

    fun guardar(moto: Moto) {
        val timestamp = moto.timestamp
        session.execute(
            guardarStatement.bind(
                moto.id, moto.matricula, moto.modelo, moto.bateria, moto.velocidad,
                moto.latitud, moto.longitud, moto.estado, timestamp
            )
        )
        session.execute(
            guardarEnFlotaStatement.bind(
                FLOTA_BARCELONA, moto.id, moto.matricula, moto.modelo, moto.bateria,
                moto.velocidad, moto.latitud, moto.longitud, moto.estado, timestamp
            )
        )
    }

    fun guardarTelemetria(moto: Moto) {
        session.execute(
            guardarTelemetriaStatement.bind(
                moto.id, moto.timestamp, moto.matricula, moto.modelo, moto.bateria,
                moto.velocidad, moto.latitud, moto.longitud, moto.estado
            )
        )
    }

    fun obtenerMoto(idMoto: String): Moto? {
        val result = session.execute(obtenerMotoStatement.bind(idMoto))

        val row = result.one() ?: return null

        return row.toMoto("id")
    }

    fun obtenerHistorial(idMoto: String): List<Moto> {
        val result = session.execute(obtenerHistorialStatement.bind(idMoto))

        return result.map { it.toMoto("id_moto") }.toList()
    }

    fun obtenerTodas(): List<Moto> {
        val result = session.execute(obtenerFlotaStatement.bind(FLOTA_BARCELONA))
        return result.map { it.toMoto("id") }.toList()
    }

    fun comprobarConexion(): Boolean = try {
        session.execute("SELECT key FROM system.local").one() != null
    } catch (_: Exception) {
        false
    }

    private fun com.datastax.oss.driver.api.core.cql.Row.toMoto(idColumn: String) = Moto(
        id = getString(idColumn)!!,
        matricula = getString("matricula")!!,
        modelo = getString("modelo")!!,
        bateria = getInt("bateria"),
        velocidad = getDouble("velocidad"),
        latitud = getDouble("latitud"),
        longitud = getDouble("longitud"),
        estado = getString("estado")!!,
        timestamp = getInstant("timestamp")!!
    )

    private companion object {
        const val FLOTA_BARCELONA = "barcelona"
    }
}
