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
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryOpenEvent
import redis.clients.jedis.Jedis

class InventoryOpenEventListener(private val jedis: Jedis) : Listener {

    @EventHandler
    fun onInventoryOpen(event: InventoryOpenEvent) {
        val inventory = event.inventory
        val holder = inventory.holder

        if (holder is MagicInventoryHolder) {
            // Check if the chest is already opened by another server
            if (jedis.exists("open:${holder.storage.id}")) {
                event.player.sendMessage("${ChatColor.RED}${ChatColor.BOLD}This storage is already opened by another server. Please wait a moment.")
                event.isCancelled = true
            }
            jedis["open:${holder.storage.id}"] = System.currentTimeMillis().toString()
            MagicTransportChest.openedInventories.add(holder)
        }
    }
}
