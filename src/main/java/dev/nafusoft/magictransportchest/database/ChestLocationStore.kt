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
import dev.nafusoft.magictransportchest.entities.MagicChestLocation
import java.sql.SQLException

class ChestLocationStore(connector: DatabaseConnector) : DatabaseTable("chest_location", connector) {

    init {
        try {
            if (super.createTable(
                    "storage_id VARCHAR(36) NOT NULL, location_world VARCHAR(128) NOT NULL, location_x DOUBLE NOT NULL, location_y DOUBLE NOT NULL, location_z DOUBLE NOT NULL, PRIMARY KEY (storage_id)"
                )
            ) {
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
    fun getLocation(storageId: String): MagicChestLocation? {
        connector.connection.use { connection ->
            connection.prepareStatement(
                "SELECT * FROM $tablename WHERE storage_id = ?"
            ).use { ps ->
                ps.setString(1, storageId)
                ps.executeQuery().use { rs ->
                    return if (rs.next()) {
                        MagicChestLocation(
                            rs.getString("location_world"),
                            rs.getDouble("location_x"),
                            rs.getDouble("location_y"),
                            rs.getDouble("location_z")
                        )
                    } else {
                        null
                    }
                }
            }
        }
    }

    @Throws(SQLException::class)
    fun getStorageByLocation(location: MagicChestLocation): String? {
        connector.connection.use { connection ->
            connection.prepareStatement(
                "SELECT * FROM $tablename WHERE location_world = ? AND location_x = ? AND location_y = ? AND location_z = ?"
            ).use { ps ->
                ps.setString(1, location.world)
                ps.setDouble(2, location.locationX)
                ps.setDouble(3, location.locationY)
                ps.setDouble(4, location.locationZ)
                ps.executeQuery().use { rs ->
                    return if (rs.next()) {
                        rs.getString("storage_id")
                    } else {
                        null
                    }
                }
            }
        }
    }

    @Throws(SQLException::class)
    fun registerLocation(storageId: String, location: MagicChestLocation) {
        connector.connection.use { connection ->
            connection.prepareStatement(
                "INSERT INTO $tablename (storage_id, location_world, location_x, location_y, location_z) VALUES (?, ?, ?, ?, ?)"
            ).use { ps ->
                ps.setString(1, storageId)
                ps.setString(2, location.world)
                ps.setDouble(3, location.locationX)
                ps.setDouble(4, location.locationY)
                ps.setDouble(5, location.locationZ)
                ps.executeUpdate()
            }
        }
    }

    @Throws(SQLException::class)
    fun updateLocation(storageId: String, location: MagicChestLocation) {
        connector.connection.use { connection ->
            connection.prepareStatement(
                "UPDATE $tablename SET location_world = ?, location_x = ?, location_y = ?, location_z = ? WHERE storage_id = ?"
            ).use { ps ->
                ps.setString(1, location.world)
                ps.setDouble(2, location.locationX)
                ps.setDouble(3, location.locationY)
                ps.setDouble(4, location.locationZ)
                ps.setString(5, storageId)
                ps.executeUpdate()
            }
        }
    }

    @Throws(SQLException::class)
    fun deleteLocation(storageId: String) {
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
