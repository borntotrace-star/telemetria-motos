package org.example

import com.datastax.oss.driver.api.core.CqlSession
import java.net.InetSocketAddress

object CassandraConnection {
    fun open(): CqlSession {
        val host = System.getenv("CASSANDRA_HOST") ?: "127.0.0.1"
        val port = System.getenv("CASSANDRA_PORT")?.let {
            it.toIntOrNull() ?: throw IllegalArgumentException("CASSANDRA_PORT debe ser un número")
        } ?: 9042
        val datacenter = System.getenv("CASSANDRA_DATACENTER") ?: "datacenter1"
        val keyspace = System.getenv("CASSANDRA_KEYSPACE") ?: "telemetria"

        require(port in 1..65535) { "CASSANDRA_PORT debe estar entre 1 y 65535" }

        return CqlSession.builder()
            .addContactPoint(InetSocketAddress(host, port))
            .withLocalDatacenter(datacenter)
            .withKeyspace(keyspace)
            .build()
            .also { println("Conectado a Cassandra en $host:$port ($datacenter/$keyspace)") }
    }
}
