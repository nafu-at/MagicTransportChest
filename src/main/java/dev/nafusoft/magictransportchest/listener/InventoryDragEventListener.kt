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

package dev.nafusoft.magictransportchest.listener

import dev.nafusoft.magictransportchest.MagicTransportChest
import dev.nafusoft.magictransportchest.entities.MagicInventoryHolder
import dev.nafusoft.magictransportchest.utils.ItemFilterChecker
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryDragEvent

class InventoryDragEventListener : Listener {

    @EventHandler
    fun onInventoryDrag(event: InventoryDragEvent) {
        val holder = event.inventory.holder
        if (holder is MagicInventoryHolder) {
            // Check if the storage is limited to write
            if (!holder.storage.writeLimit.isNullOrBlank() && holder.storage.writeLimit != MagicTransportChest.instance!!.pluginConfig.serverUniqueId) {
                event.newItems.keys.find { it < holder.inventory.size }?.let {
                    event.isCancelled = true
                    return
                }
            }

            // Check if the item is allowed to be stored
            event.newItems.values.find { !ItemFilterChecker.check(it) }?.let {
                event.isCancelled = true
                return
            }
        }
    }
}
