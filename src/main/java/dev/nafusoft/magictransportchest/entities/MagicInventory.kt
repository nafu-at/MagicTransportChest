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

package dev.nafusoft.magictransportchest.entities

import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

data class MagicInventory(val storageId: String, val size: Int, val items: Map<Int, MagicItemStack> = linkedMapOf()) :
    ConfigurationSerializable {

    constructor(serializedMap: Map<String, Any>) : this(
        serializedMap["storageId"] as String,
        serializedMap["size"] as Int,
        serializedMap["items"] as Map<Int, MagicItemStack>
    )


    fun toMinecraftInventory(holder: InventoryHolder): Inventory {
        val inventory = Bukkit.getServer().createInventory(holder, size, storageId)
        items.forEach { (index, item) ->
            inventory.setItem(index, item.toMinecraftItemStack())
        }
        return inventory
    }

    override fun toString(): String {
        val yamlConfiguration = YamlConfiguration()
        yamlConfiguration["inventory"] = this
        return yamlConfiguration.saveToString()
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf(
            "storageId" to storageId,
            "size" to size,
            "items" to items
        )
    }


    companion object {
        fun fromMinecraftInventory(storageId: String, inventory: Inventory): MagicInventory {
            val items = mutableMapOf<Int, MagicItemStack>()
            for (i in 0 until inventory.size) {
                val item = inventory.getItem(i)
                if (item != null) {
                    items[i] = MagicItemStack.fromMinecraftItemStack(item)
                }
            }
            return MagicInventory(storageId, inventory.size, items)
        }

        fun fromString(string: String): MagicInventory {
            val yamlConfiguration = YamlConfiguration()
            yamlConfiguration.loadFromString(string)
            return yamlConfiguration["inventory"] as MagicInventory
        }
    }
}
