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
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent

class InventoryClickEventListener : Listener {

    @EventHandler
    fun onInventoryClickEvent(event: InventoryClickEvent) {
        val holder = event.inventory.holder
        if (holder is MagicInventoryHolder) {
            when (event.action) {
                InventoryAction.PLACE_ALL,
                InventoryAction.PLACE_SOME,
                InventoryAction.PLACE_ONE,
                InventoryAction.SWAP_WITH_CURSOR -> {
                    if (event.rawSlot < holder.storage.size) {
                        if (!holder.storage.writeLimit.isNullOrBlank() && holder.storage.writeLimit != MagicTransportChest.instance!!.pluginConfig.serverUniqueId) {
                            event.isCancelled = true
                            return
                        }

                        // Check if the item is allowed to be stored
                        if (event.cursor?.let { ItemFilterChecker.check(it) } == true) {
                            event.isCancelled = true
                            return
                        }
                    }
                }

                InventoryAction.MOVE_TO_OTHER_INVENTORY -> {
                    if (event.rawSlot >= holder.storage.size) {
                        if (!holder.storage.writeLimit.isNullOrBlank() && holder.storage.writeLimit != MagicTransportChest.instance!!.pluginConfig.serverUniqueId) {
                            event.isCancelled = true
                            return
                        }

                        // Check if the item is allowed to be stored
                        if (event.currentItem?.let { ItemFilterChecker.check(it) } == true) {
                            event.isCancelled = true
                            return
                        }
                    }
                }

                // この動作は変更されているアイテムが正常に全て取得できないため、キャンセルする
                InventoryAction.HOTBAR_MOVE_AND_READD -> {
                    event.isCancelled = true
                }

                else -> {}
            }
            // Check if the storage is limited to write

        }
    }
}
