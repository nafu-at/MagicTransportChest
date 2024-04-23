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

package dev.nafusoft.magictransportchest.service

import dev.nafusoft.magictransportchest.database.ChestLocationStore
import dev.nafusoft.magictransportchest.database.InventoryStore
import dev.nafusoft.magictransportchest.database.SettingsStore
import dev.nafusoft.magictransportchest.database.StorageStore
import dev.nafusoft.magictransportchest.entities.MagicChestLocation
import dev.nafusoft.magictransportchest.entities.MagicInventory
import dev.nafusoft.magictransportchest.entities.MagicStorage
import org.bukkit.Location

class StorageService(
    private val storageStore: StorageStore,
    private val chestLocationStore: ChestLocationStore,
    private val inventoryStore: InventoryStore,
    private val settingsStore: SettingsStore,
) {
    fun getStorage(id: String) = storageStore.getStorage(id)

    fun getStorageByOwner(owner: String) = storageStore.getStorageByOwner(owner)

    fun getStorageByLocation(world: String, x: Double, y: Double, z: Double) =
        chestLocationStore.getStorageByLocation(MagicChestLocation(world, x, y, z))

    fun getStorageByLocation(location: Location) =
        chestLocationStore.getStorageByLocation(MagicChestLocation.fromMinecraftLocation(location))

    fun getInventory(storageId: String) = inventoryStore.getInventory(storageId)?.let { MagicInventory.fromString(it) }

    fun getInventoryByLocation(world: String, x: Double, y: Double, z: Double) =
        chestLocationStore.getStorageByLocation(MagicChestLocation(world, x, y, z))
            ?.let { inventoryStore.getInventory(it) }?.let { MagicInventory.fromString(it) }

    fun getInventoryByLocation(location: Location) =
        chestLocationStore.getStorageByLocation(MagicChestLocation.fromMinecraftLocation(location))
            ?.let { inventoryStore.getInventory(it) }?.let { MagicInventory.fromString(it) }

    fun setStorage(id: String, name: String?, owner: String, type: MagicStorage.StorageType, size: Int) =
        storageStore.registerStorage(MagicStorage(id, name, owner, type, size))

    fun setChestLocation(storageId: String, world: String, x: Double, y: Double, z: Double) =
        chestLocationStore.registerLocation(storageId, MagicChestLocation(world, x, y, z))

    fun setChestLocation(storageId: String, location: MagicChestLocation) =
        chestLocationStore.registerLocation(storageId, location)

    fun setChestLocation(storageId: String, location: Location) =
        chestLocationStore.registerLocation(storageId, MagicChestLocation.fromMinecraftLocation(location))

    fun setInventory(storageId: String, inventory: String) = inventoryStore.setInventory(storageId, inventory)

    fun setInventory(inventory: MagicInventory) =
        inventoryStore.setInventory(inventory.storageId, inventory.toString())

    fun removeChestLocation(storageId: String) = chestLocationStore.deleteLocation(storageId)

    fun removeChestLocationByLocation(world: String, x: Double, y: Double, z: Double) =
        chestLocationStore.getStorageByLocation(MagicChestLocation(world, x, y, z))
            ?.let { chestLocationStore.deleteLocation(it) }

    fun removeStorage(id: String) =
        storageStore.deleteStorage(id) // Deleting storage will delete all elements with foreign key constraints.

    fun newInventory(storageId: String) =
        MagicInventory(storageId, settingsStore.getSetting("mtc.storage.size")?.toInt() ?: 27)
}
