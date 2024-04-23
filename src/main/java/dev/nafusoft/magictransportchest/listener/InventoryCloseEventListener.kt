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

import dev.nafusoft.magictransportchest.command.ItemFilterSettingCommand
import dev.nafusoft.magictransportchest.entities.MagicInventoryHolder
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryCloseEvent
import redis.clients.jedis.Jedis

class InventoryCloseEventListener(private val jedis: Jedis) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClose(event: InventoryCloseEvent) {
        val inventory = event.inventory
        val holder = inventory.holder

        if (holder is ItemFilterSettingCommand.MagicItemFilterSettingGuiHolder) {
            holder.saveInventory(event.inventory)
        } else if (holder is MagicInventoryHolder) {
            holder.saveInventory(event.inventory)
            jedis.del("open:${holder.storageId}")
        }
    }
}
