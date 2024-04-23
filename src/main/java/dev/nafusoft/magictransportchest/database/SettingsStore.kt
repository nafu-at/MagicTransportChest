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

package dev.nafusoft.magictransportchest.database

import dev.nafusoft.magictransportchest.MagicTransportChest
import java.sql.SQLException

class SettingsStore(connector: DatabaseConnector) : DatabaseTable("settings", connector) {

    init {
        try {
            super.createTable("setting_key VARCHAR(36) NOT NULL, setting_value LONGTEXT NOT NULL, PRIMARY KEY (setting_key)")
        } catch (e: Exception) {
            MagicTransportChest.instance!!.logger.severe("Failed to create table $tablename")
        }
    }

    @Throws(SQLException::class)
    fun getSetting(settingKey: String): String? {
        connector.connection.use { connection ->
            connection.prepareStatement(
                "SELECT * FROM $tablename WHERE setting_key = ?"
            ).use { ps ->
                ps.setString(1, settingKey)
                ps.executeQuery().use { rs ->
                    return if (rs.next()) {
                        rs.getString("setting_value")
                    } else {
                        null
                    }
                }
            }
        }
    }

    @Throws(SQLException::class)
    fun getSettings(): Map<String, String> {
        val settings = mutableMapOf<String, String>()
        connector.connection.use { connection ->
            connection.prepareStatement(
                "SELECT * FROM $tablename"
            ).use { ps ->
                ps.executeQuery().use { rs ->
                    while (rs.next()) {
                        settings[rs.getString("setting_key")] = rs.getString("setting_value")
                    }
                }
            }
        }
        return settings
    }

    @Throws(SQLException::class)
    fun setSetting(settingKey: String, settingValue: String) {
        connector.connection.use { connection ->
            connection.prepareStatement(
                "INSERT INTO $tablename (setting_key, setting_value) VALUES (?, ?) ON DUPLICATE KEY UPDATE setting_value = ?"
            ).use { ps ->
                ps.setString(1, settingKey)
                ps.setString(2, settingValue)
                ps.setString(3, settingValue)
                ps.executeUpdate()
            }
        }
    }

    @Throws(SQLException::class)
    fun removeSetting(settingKey: String) {
        connector.connection.use { connection ->
            connection.prepareStatement(
                "DELETE FROM $tablename WHERE setting_key = ?"
            ).use { ps ->
                ps.setString(1, settingKey)
                ps.executeUpdate()
            }
        }
    }
}
