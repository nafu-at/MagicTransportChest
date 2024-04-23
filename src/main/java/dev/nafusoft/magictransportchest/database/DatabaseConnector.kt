package dev.nafusoft.magictransportchest.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection
import java.sql.SQLException

class DatabaseConnector(config: HikariConfig) {
    private val dataSource: HikariDataSource = HikariDataSource(config)

    @get:Throws(SQLException::class)
    val connection: Connection
        get() = dataSource.connection

    fun close() {
        dataSource.close()
    }
}
