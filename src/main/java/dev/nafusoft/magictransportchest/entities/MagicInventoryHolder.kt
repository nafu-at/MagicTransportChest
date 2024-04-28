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

import dev.nafusoft.magictransportchest.service.StorageService
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class MagicInventoryHolder(val storageService: StorageService, val storage: MagicStorage, val player: Player) :
    InventoryHolder {

    override fun getInventory(): Inventory {
        storageService.getInventory(storage.id)?.let {
            return it.toMinecraftInventory(this)
        }
        return storageService.newInventory(storage.id).toMinecraftInventory(this)
    }

    fun saveInventory(inventory: Inventory) {
        storageService.setInventory(MagicInventory.fromMinecraftInventory(storage.id, inventory))
    }
}
