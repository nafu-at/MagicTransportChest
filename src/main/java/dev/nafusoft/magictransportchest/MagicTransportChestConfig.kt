/*
 * Copyright 2024 Nafu Satsuki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.nafusoft.magictransportchest

import com.zaxxer.hikari.HikariConfig
import java.util.*

data class MagicTransportChestConfig(
    val database: DatabaseConfig,
    val redis: RedisConfig,
    val storage: StorageConfig,
    val serverUniqueId: String
) {
    class ConfigLoader(private val instance: MagicTransportChest) {
        private val config = instance.config

        fun loadConfig(): MagicTransportChestConfig {
            instance.saveDefaultConfig() // Save default config
            instance.reloadConfig() // Reload config

            val databaseConfig = config.getConfigurationSection("database")
            val redisConfig = config.getConfigurationSection("redis")
            val storageConfig = config.getConfigurationSection("storage")

            var serverUniqueId = config.getString("serverUniqueId")
            if (serverUniqueId.isNullOrBlank()) { // Generate server unique id
                serverUniqueId = UUID.randomUUID().toString()
                MagicTransportChest.instance!!.config.set("serverUniqueId", serverUniqueId)
                instance.saveConfig()
            }

            return MagicTransportChestConfig(
                DatabaseConfig(
                    databaseConfig!!.getString("jdbcUrl")!!,
                    databaseConfig.getString("username"),
                    databaseConfig.getString("password"),
                    databaseConfig.getString("tablePrefix")
                ),
                RedisConfig(
                    redisConfig!!.getString("host")!!,
                    redisConfig.getInt("port"),
                    redisConfig.getInt("database")
                ),
                StorageConfig(
                    storageConfig!!.getBoolean("createDefault"),
                ),
                serverUniqueId
            )
        }
    }
}

data class DatabaseConfig(
    val address: String,
    val username: String?,
    val password: String?,
    val prefix: String?
) {
    fun toHikariConfig(): HikariConfig {
        val hconfig = HikariConfig()

        if (address.contains("mysql")) hconfig.driverClassName = "com.mysql.cj.jdbc.Driver"
        else if (address.contains("maria")) hconfig.driverClassName = "org.mariadb.jdbc.Driver"
        else throw IllegalArgumentException("Unsupported database type")

        hconfig.jdbcUrl = address
        hconfig.addDataSourceProperty("user", username)
        hconfig.addDataSourceProperty("password", password)

        return hconfig
    }
}

data class RedisConfig(
    val host: String,
    val port: Int,
    val database: Int,
)

data class StorageConfig(
    val createDefault: Boolean,
)
