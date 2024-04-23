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
import dev.nafusoft.magictransportchest.entities.MagicStorage
import java.sql.SQLException

class StorageStore(connector: DatabaseConnector) : DatabaseTable("storage", connector) {
    init {
        try {
            super.createTable(
                "storage_id VARCHAR(36) NOT NULL, storage_name VARCHAR(128) NOT NULL, storage_owner VARCHAR(36) NOT NULL, storage_type ENUM('block', 'virtual') NOT NULL DEFAULT 'virtual', storage_size INT NOT NULL DEFAULT 27, PRIMARY KEY (storage_id)"
            )
        } catch (e: Exception) {
            MagicTransportChest.instance!!.logger.severe("Failed to create table $tablename")
        }
    }

    @Throws(SQLException::class)
    fun getStorage(storageId: String): MagicStorage? {
        connector.connection.use { connection ->
            connection.prepareStatement(
                "SELECT * FROM $tablename WHERE storage_id = ?"
            ).use { ps ->
                ps.setString(1, storageId)
                ps.executeQuery().use { rs ->
                    return if (rs.next()) {
                        MagicStorage(
                            rs.getString("storage_id"),
                            rs.getString("storage_name"),
                            rs.getString("storage_owner"),
                            MagicStorage.StorageType.valueOf(rs.getString("storage_type").uppercase()),
                            rs.getInt("storage_size")
                        )
                    } else {
                        null
                    }
                }
            }
        }
    }

    @Throws(SQLException::class)
    fun getStorages(owner: String): List<MagicStorage> {
        val storages = mutableListOf<MagicStorage>()
        connector.connection.use { connection ->
            connection.prepareStatement(
                "SELECT * FROM $tablename WHERE storage_owner = ?"
            ).use { ps ->
                ps.setString(1, owner)
                ps.executeQuery().use { rs ->
                    while (rs.next()) {
                        storages.add(
                            MagicStorage(
                                rs.getString("storage_id"),
                                rs.getString("storage_name"),
                                rs.getString("storage_owner"),
                                MagicStorage.StorageType.valueOf(rs.getString("storage_type").uppercase()),
                                rs.getInt("storage_size")
                            )
                        )
                    }
                }
            }
        }
        return storages
    }

    fun getStorageByOwner(owner: String): List<MagicStorage> {
        val storages = mutableListOf<MagicStorage>()
        connector.connection.use { connection ->
            connection.prepareStatement(
                "SELECT * FROM $tablename WHERE storage_owner = ?"
            ).use { ps ->
                ps.setString(1, owner)
                ps.executeQuery().use { rs ->
                    while (rs.next()) {
                        storages.add(
                            MagicStorage(
                                rs.getString("storage_id"),
                                rs.getString("storage_name"),
                                rs.getString("storage_owner"),
                                MagicStorage.StorageType.valueOf(rs.getString("storage_type").uppercase()),
                                rs.getInt("storage_size")
                            )
                        )
                    }
                }
            }
        }
        return storages
    }

    @Throws(SQLException::class)
    fun registerStorage(storage: MagicStorage) {
        connector.connection.use { connection ->
            connection.prepareStatement(
                "INSERT INTO $tablename (storage_id, storage_name, storage_owner, storage_type, storage_size) SELECT ?, ?, ?, ?, ? WHERE NOT EXISTS (SELECT * FROM $tablename WHERE storage_id = ?)"
            ).use { ps ->
                ps.setString(1, storage.id)
                ps.setString(2, storage.name)
                ps.setString(3, storage.owner)
                ps.setString(4, storage.type.name)
                ps.setInt(5, storage.size)
                ps.setString(6, storage.id)
                ps.execute()
            }
        }
    }

    @Throws(SQLException::class)
    fun updateStorage(storage: MagicStorage) {
        connector.connection.use { connection ->
            connection.prepareStatement(
                "UPDATE $tablename SET storage_name = ?, storage_owner = ?, storage_type = ?, storage_size = ? WHERE storage_id = ?"
            ).use { ps ->
                ps.setString(1, storage.name)
                ps.setString(2, storage.owner)
                ps.setString(3, storage.type.name)
                ps.setInt(4, storage.size)
                ps.setString(5, storage.id)
                ps.execute()
            }
        }
    }

    @Throws(SQLException::class)
    fun deleteStorage(storageId: String) {
        connector.connection.use { connection ->
            connection.prepareStatement(
                "DELETE FROM $tablename WHERE storage_id = ?"
            ).use { ps ->
                ps.setString(1, storageId)
                ps.execute()
            }
        }
    }
}
