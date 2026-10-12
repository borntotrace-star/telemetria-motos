package org.example

import kotlin.test.Test
import kotlin.test.assertEquals

class AppTest {

    @Test
    fun `moto is created correctly`() {
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

        assertEquals("moto-001", moto.id)
        assertEquals("TEST-001", moto.matricula)
        assertEquals(87, moto.bateria)
        assertEquals("EN_MARCHA", moto.estado)
    }
}
