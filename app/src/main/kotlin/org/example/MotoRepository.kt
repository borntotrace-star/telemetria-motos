package org.example

import com.datastax.oss.driver.api.core.CqlSession

class MotoRepository(
    private val session: CqlSession
) {

    fun guardar(moto: Moto) {
        session.execute(
            """
            INSERT INTO motos (
                id,
                matricula,
                modelo,
                bateria,
                velocidad,
                latitud,
                longitud,
                estado,
                timestamp
            )
            VALUES (
                '${moto.id}',
                '${moto.matricula}',
                '${moto.modelo}',
                ${moto.bateria},
                ${moto.velocidad},
                ${moto.latitud},
                ${moto.longitud},
                '${moto.estado}',
                toTimestamp(now())
            )
            """.trimIndent()
        )
    }

    fun guardarTelemetria(moto: Moto) {
        session.execute(
            """
            INSERT INTO telemetria_motos (
                id_moto,
                timestamp,
                matricula,
                modelo,
                bateria,
                velocidad,
                latitud,
                longitud,
                estado
            )
            VALUES (
                '${moto.id}',
                toTimestamp(now()),
                '${moto.matricula}',
                '${moto.modelo}',
                ${moto.bateria},
                ${moto.velocidad},
                ${moto.latitud},
                ${moto.longitud},
                '${moto.estado}'
            )
            """.trimIndent()
        )
    }

    fun obtenerMoto(idMoto: String): Moto? {
        val result = session.execute(
            """
            SELECT *
            FROM motos
            WHERE id = '$idMoto'
            """.trimIndent()
        )

        val row = result.one() ?: return null

        return Moto(
            id = row.getString("id")!!,
            matricula = row.getString("matricula")!!,
            modelo = row.getString("modelo")!!,
            bateria = row.getInt("bateria"),
            velocidad = row.getDouble("velocidad"),
            latitud = row.getDouble("latitud"),
            longitud = row.getDouble("longitud"),
            estado = row.getString("estado")!!
        )
    }

    fun obtenerHistorial(idMoto: String): List<Moto> {
        val result = session.execute(
            """
            SELECT *
            FROM telemetria_motos
            WHERE id_moto = '$idMoto'
            """.trimIndent()
        )

        return result.map { row ->
            Moto(
                id = row.getString("id_moto")!!,
                matricula = row.getString("matricula")!!,
                modelo = row.getString("modelo")!!,
                bateria = row.getInt("bateria"),
                velocidad = row.getDouble("velocidad"),
                latitud = row.getDouble("latitud"),
                longitud = row.getDouble("longitud"),
                estado = row.getString("estado")!!
            )
        }.toList()
    }

    fun obtenerTodas(): List<Moto> {
        val result = session.execute("SELECT * FROM motos")

        return result.map { row ->
            Moto(
                id = row.getString("id")!!,
                matricula = row.getString("matricula")!!,
                modelo = row.getString("modelo")!!,
                bateria = row.getInt("bateria"),
                velocidad = row.getDouble("velocidad"),
                latitud = row.getDouble("latitud"),
                longitud = row.getDouble("longitud"),
                estado = row.getString("estado")!!
            )
        }.toList()
    }
}