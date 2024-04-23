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

class InventoryStore(connector: DatabaseConnector) : DatabaseTable("inventory", connector) {

    init {
        try {
            if (super.createTable("storage_id VARCHAR(36) NOT NULL, inventory LONGTEXT NOT NULL, PRIMARY KEY (storage_id)")) {
                createForeignKey()
            }
        } catch (e: Exception) {
            MagicTransportChest.instance!!.logger.severe("Failed to create table $tablename")
        }
    }

    @Throws(SQLException::class)
    private fun createForeignKey() {
        val storage = MagicTransportChest.instance!!.storageStore!!.tablename
        connector.connection.use { connection ->
            // Set forgen key for storage_id
            connection.prepareStatement(
                "ALTER TABLE $tablename ADD FOREIGN KEY (storage_id) REFERENCES $storage (storage_id) ON DELETE CASCADE"
            ).use { ps ->
                ps.executeUpdate()
            }
        }
    }

    @Throws(SQLException::class)
    fun getInventory(storageId: String): String? {
        connector.connection.use { connection ->
            connection.prepareStatement(
                "SELECT * FROM $tablename WHERE storage_id = ?"
            ).use { ps ->
                ps.setString(1, storageId)
                ps.executeQuery().use { rs ->
                    return if (rs.next()) {
                        rs.getString("inventory")
                    } else {
                        null
                    }
                }
            }
        }
    }

    @Throws(SQLException::class)
    fun setInventory(storageId: String, inventory: String) {
        connector.connection.use { connection ->
            connection.prepareStatement(
                "INSERT INTO $tablename (storage_id, inventory) VALUES (?, ?) ON DUPLICATE KEY UPDATE inventory = ?"
            ).use { ps ->
                ps.setString(1, storageId)
                ps.setString(2, inventory)
                ps.setString(3, inventory)
                ps.executeUpdate()
            }
        }
    }

    @Throws(SQLException::class)
    fun removeInventory(storageId: String) {
        connector.connection.use { connection ->
            connection.prepareStatement(
                "DELETE FROM $tablename WHERE storage_id = ?"
            ).use { ps ->
                ps.setString(1, storageId)
                ps.executeUpdate()
            }
        }
    }
}
