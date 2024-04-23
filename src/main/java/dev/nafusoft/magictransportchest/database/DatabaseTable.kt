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

abstract class DatabaseTable(tablename: String, protected val connector: DatabaseConnector) {
    val tablename: String

    init {
        this.tablename = PREFIX + tablename
    }

    /**
     * Creates a table with the specified structure.
     * If a table with the same name already exists, it exits without executing the process.
     *
     * @param construction Structure of the table to be created
     * @return Whether the table was created
     * @throws SQLException Thrown when creating a table fails.
     */
    @Throws(SQLException::class)
    fun createTable(construction: String): Boolean {
        connector.connection.use { connection ->
            connection.prepareStatement(
                "SHOW TABLES LIKE '$tablename'"
            ).use { ps ->
                ps.executeQuery().use { rs ->
                    if (rs.next()) {
                        return false
                    }
                }
            }

            connection.prepareStatement(
                "CREATE TABLE $tablename ($construction)"
            ).use { ps ->
                ps.execute()
            }

            return true
        }
    }

    companion object {
        private val PREFIX = MagicTransportChest.instance!!.pluginConfig.database.prefix
    }
}
